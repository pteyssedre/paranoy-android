/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015. Pierre Teyssedre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.crypto;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ca.teyssedre.crypto.store.db.KeyStoreDataSource;
import ca.teyssedre.crypto.store.models.KeySet;
import ca.teyssedre.crypto.utils.CryptoException;
import ca.teyssedre.crypto.views.UIHelper;

public class Crypto {

    private Context context;
    private UIHelper uiHelper;
    private KeyStoreDataSource keyStorage;
    private boolean dialogNotification = true;
    private ThreadPoolExecutor background;
    private Handler uiThread;

    //region Singleton
    /**
     * Singleton instance.
     */
    private static Crypto instance;

    /**
     * Default constructor for {@code instance} parameter.
     *
     * @param context {@link Context} android to interact with the current application.
     */
    private Crypto(Context context) {
        this.context = context;
        this.keyStorage = new KeyStoreDataSource(this.context);
        int i = Runtime.getRuntime().availableProcessors();
        LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
        this.background = new ThreadPoolExecutor(1, i, 1, TimeUnit.SECONDS, queue);
        this.uiThread = new Handler(Looper.getMainLooper());
    }

    public void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * Instance getter for the {@link Crypto} singleton class. The getter must be the first call make
     * to instantiate the singleton. Using the {@code context} parameter the instance will be able to
     * manage the creation and maintenance of the storage for the CryptoLibrary.
     * If the context provide is null a {@link CryptoException} will be raised.
     *
     * @param context {@link Context} to hold te default android context for all operation that require
     *                a context.
     * @return {@link Crypto} instance.
     */
    public static Crypto getInstance(Context context) {
        if (context == null) {
            throw new CryptoException("No Context has been pass to Crypto library");
        }
        if (instance == null) {
            instance = new Crypto(context);
        }
        if (instance.context != context) {
            instance.context = context;
        }
        return instance;
    }

    /**
     * Shorter getter for the instance of {@link Crypto} singleton.
     * If the instance or the context provide is null a {@link CryptoException} will be raised.
     *
     * @return {@link Crypto} instance.
     */
    public static Crypto getInstance() {
        if (instance != null && instance.context != null) {
            return instance;
        }
        throw new CryptoException("No Context has been pass to Crypto library");
    }
    //endregion

    //region Static Methods

    //region AES

    /**
     * Helper to generate a {@link SecretKey} instance base on the AES Algorithm.
     *
     * @param length {@link Integer} value of the lenght of AES crypto.
     * @return {@link SecretKey} instance.
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey GenerateAESSecretKey(int length) throws NoSuchAlgorithmException {
        if (length <= 128) {
            length = 128;
        } else if (length >= 256) {
            length = 256;
        } else {
            length = 192;
        }
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(length);
        return keyGen.generateKey();
    }

    public static void GenerateBlowfishKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
        SecretKey secretkey = keyGen.generateKey();


    }

    /**
     * Helper to encrypt data from the parameter {@code data} using the algorithm AES and base on
     * the {@code key} as a {@link SecretKey}.
     *
     * @param key  {@link SecretKey} instance use to encrypt the content.
     * @param data {@link byte} of data to encrypt.
     * @return byte array representing the encrypted version of the {@code data} parameter.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] EncryptWithAES(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(data);
    }

    /**
     * Helper to decrypt the {@code data} parameter using a {@code key} with the AES algorithm.
     *
     * @param key  {@link SecretKey} instance to use for decryption.
     * @param data array of bytes representing the encrypt content.
     * @return array of bytes representing the un-encrypted version of {@code data}.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] DecryptWithAES(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key);
        return c.doFinal(data);
    }
    //endregion

    //region RSA

    /**
     * Helper to generate a map of public and private key using RSA
     *
     * @param lenght {@link Integer} value representing the numbers of bytes to generate the keys.
     *               this value must be set between 1024 and 4096.
     * @return {@link KeyPair} object instance that contains "public" key and "private" key.
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair GenerateRSAPair(int lenght) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (lenght <= 1024) {
            lenght = 1024;
        } else if (lenght >= 4096) {
            lenght = 4096;
        } else {
            lenght = 2048;
        }
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(lenght);
        return kpg.genKeyPair();
    }

    /**
     * Helper to encrypt using a given {@link PrivateKey} instance.
     *
     * @param privateKey {@link PrivateKey} require to encrypt the data
     * @param data       {@link Byte} array to encrypt.
     * @return {@link Byte} array, encrypted data.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] EncryptWithRSA(PrivateKey privateKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, privateKey);
        return c.doFinal(data);
    }

    /**
     * @param publicKey {@link PublicKey} require to decrypt the data.
     * @param data      {@link Byte} array to decrypt.
     * @return {@link Byte} array, decrypted data.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] DecryptWithRSA(PublicKey publicKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, publicKey);
        return c.doFinal(data);
    }

    /**
     * Helper to sign a content {@code data} using a {@link PrivateKey} with the RSA algorithm.
     *
     * @param privateKey {@link PrivateKey} require to encrypt the data
     * @param data       {@link Byte} array to encrypt.
     * @return {@link Byte} array, encrypted data.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static byte[] SignWithRSA(PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException, NoSuchProviderException {
        byte[] signedBytes = null;
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(privateKey);
        s.update(data);
        signedBytes = s.sign();
        return signedBytes;
    }

    /**
     * Helper to validate a signed content {@code data} using a {@link PublicKey} with the RSA algorithm.
     *
     * @param publicKey {@link PublicKey} object to validate the signature.
     * @param data      {@link byte} array representing the signed data.
     * @return {@link boolean} value indicate if the signature is valid.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean ValidateSignatureWithRSA(PublicKey publicKey, byte[] data, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(publicKey);
        s.update(data);
        return s.verify(signature);
    }
    //endregion

    //region Generics

    /**
     * Function to parse {@link String} instance into a {@link PrivateKey}.
     *
     * @param key64 {@link String} representing the {@link Base64} string value of stored key.
     * @return {@link PrivateKey} instance restore through the {@code PKCS8EncodedKeySpec} class.
     * @throws GeneralSecurityException
     * @see PKCS8EncodedKeySpec
     */
    public static PrivateKey StringToPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = base64Decode(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return privateKey;
    }

    /**
     * Function to parse a {@link PrivateKey} into a {@link String} value.
     *
     * @param privateKey {@link PrivateKey} instance to parse.
     * @return {@link String} value resulting of {@link PKCS8EncodedKeySpec} class.
     * @throws GeneralSecurityException
     * @see PKCS8EncodedKeySpec
     */
    public static String PrivateKeyRSAToString(PrivateKey privateKey) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(privateKey, PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        return base64Encode(packed).replaceAll("\\n", "");
    }

    /**
     * Function to parse {@link String} instance into a {@link PublicKey}.
     *
     * @param key64 {@link String} representing the {@link Base64} string value of stored key.
     * @return {@link PublicKey} instance restore through the {@code X509EncodedKeySpec} class.
     * @throws GeneralSecurityException
     * @see X509EncodedKeySpec
     */
    public static PublicKey StringToPublicKey(String key64) throws GeneralSecurityException {
        byte[] data = base64Decode(key64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    /**
     * Function to parse {@link PublicKey} instance into a {@link String} value.
     *
     * @param publicKey {@link PublicKey} instance to transform.
     * @return {@link String} value resulting of the {@link X509EncodedKeySpec} class.
     * @throws GeneralSecurityException
     * @see X509EncodedKeySpec
     */
    public static String PublicKeyRSAToString(PublicKey publicKey) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publicKey, X509EncodedKeySpec.class);
        return base64Encode(spec.getEncoded()).replaceAll("\\n", "");
    }

    /**
     * Helper to generate a {@link Key} from a {@link String} value.
     *
     * @param data {@link String} value of the key.
     * @return {@link SecretKey} instance generated based on the parameter {@code data} and {@code algorithm}.
     */
    public static SecretKey StringToAESKey(String data) {
        byte[] decoded = base64Decode(data);
        return new SecretKeySpec(decoded, 0, decoded.length, "AES");
    }

    /**
     * Helper to "stringify" a {@link SecretKey} instance.
     *
     * @param key instance of {@link SecretKey} to stringify.
     * @return Base64 {@link String} value.
     */
    public static String SecretKeyToString(SecretKey key) {
        return base64Encode(key.getEncoded());
    }

    /**
     * Helper to list all the supported Security provider names.
     *
     * @return {@link Set} of supported provider names.
     */
    public static Set<String> GetProvidersNames() {

        Set<String> results = new TreeSet<>();
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            results.add(provider.getName());
        }

        return results;
    }

    /**
     * Helper to retrieved all the type supported for a specific Provider.
     *
     * @param name {@link String} representing the Provider name.
     * @return {@link Set} of type supported for the given Provider name.
     */
    public static Set<String> GetServiceTypes(String name) {

        Set<String> results = new TreeSet<>();
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            if (provider.getName().equals(name)) {
                Set<Object> ks = provider.keySet();
                for (Object k1 : ks) {
                    String k = k1.toString();
                    k = k.split(" ")[0];
                    if (k.startsWith("Alg.Alias."))
                        k = k.substring(10);

                    results.add(k.substring(0, k.indexOf('.')));
                }
            }
        }
        return results;
    }

    /**
     * Helper to retrieved all the algorithms supported
     *
     * @param name {@link String} representing the Provider name.
     * @param type {@link String} representing a specific type of the Provider.
     * @return {@link Set} of algorithms supported.
     */
    public static Set<String> GetAlgorithms(String name, String type) {

        Set<String> results = new TreeSet<>();
        Set<String> serviceTypes = new TreeSet<>();
        // get all the providers
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            if (provider.getName().equals(name)) {
                Set<Object> ks = provider.keySet();
                for (Object k1 : ks) {
                    String k = k1.toString();
                    k = k.split(" ")[0];
                    if (k.startsWith("Alg.Alias.")) {
                        k = k.substring(10);
                    }
                    Set<String> algorithms = new TreeSet<>();
                    String sType = k.substring(0, k.indexOf('.'));
                    if (sType.equals(type)) {
                        k = k1.toString();
                        k = k.split(" ")[0];
                        if (k.startsWith(sType + ".")) {
                            algorithms.add(k.substring(sType.length() + 1));
                        } else if (k.startsWith("Alg.Alias." + sType + ".")) {
                            algorithms.add(k.substring(sType.length() + 11));
                        }
                        for (String algorithm : algorithms) {
                            results.add(algorithm);
                        }
                    }
                }
            }
        }
        return results;
    }
    //endregion

    //region Helpers

    public static byte[] base64Decode(String stored) {
        return Base64.decode(stored.getBytes(), Base64.DEFAULT);
    }

    public static String base64Encode(byte[] packed) {
        return Base64.encodeToString(packed, 0, packed.length, Base64.DEFAULT);
    }

    public static String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public static byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }
    //endregion

    //endregion

    //region Async Method

    /**
     * Execute the generation of RSA key in a new thread and add {@link android.app.ProgressDialog}
     * to inform the user of the status.
     *
     * @param min      {@link Integer} value of the RSA key min length;
     * @param callback {@link ICryptoCallback} instance to callback on completion.
     */
    public void GenerateRSAPairAsync(final int min, final ICryptoCallback<KeyPair> callback) {
        ShowProgress("Generating RSA Pair " + min);
        background.execute(new Runnable() {
            @Override
            public void run() {
                KeyPair keyPair = null;
                try {
                    keyPair = GenerateRSAPair(min);
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    final KeyPair finalKeyPair = keyPair;
                    uiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.OnComplete(finalKeyPair);
                        }
                    });
                }
                HideProgress();
            }
        });
    }

    /**
     * @param privateKey {@link PrivateKey} instance to encrypt data.
     * @param data       {@code byte[]} value to encrypt.
     * @param callback   {@link ICryptoCallback} instance to callback on completion.
     */
    public void RSAEncryptAsync(final PrivateKey privateKey, final byte[] data, final ICryptoCallback<byte[]> callback) {
        ShowProgress("Encrypting with RSA " + privateKey.getAlgorithm() + " " + privateKey.getFormat());
        background.execute(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = new byte[0];
                try {
                    bytes = EncryptWithRSA(privateKey, data);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.OnComplete(bytes);
                }
                HideProgress();
            }
        });
    }

    /**
     * Public method to execute a decryption using a {@link PublicKey}. The operation will be executed in a
     * new {@link Thread} to prevent the current thread to be stopped. When the operation will be completed,
     * the {@code callback#OnComplete} will be executed with the result as parameter, this result could be null.
     *
     * @param publicKey {@link PublicKey} instance use to decrypt the content {@code data}.
     * @param data      content to decrypt.
     * @param callback  {@link ICryptoCallback} instance to callback on operation completed.
     */
    public void RSADecryptAsync(final PublicKey publicKey, final byte[] data, final ICryptoCallback<byte[]> callback) {
        ShowProgress("Decrypting with RSA " + publicKey.getAlgorithm() + " " + publicKey.getFormat());
        background.execute(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = new byte[0];
                try {
                    bytes = DecryptWithRSA(publicKey, data);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.OnComplete(bytes);
                }
                HideProgress();
            }
        });
    }

    /**
     * Helper to retrieve from the {@code keyStorage} all the {@link KeySet}.
     *
     * @param callback {@link ICryptoCallback} will return a {@link List} of {@link KeySet}.
     */
    public void GetAllStoredKeysAsync(final ICryptoCallback<List<KeySet>> callback) {
        background.execute(new Runnable() {
            @Override
            public void run() {
                List<KeySet> keySets = keyStorage.GetAllKeySet();
                if (callback != null) {
                    callback.OnComplete(keySets);
                }
            }
        });
    }

    public void GetStoredKeysAsync(final long keyId, final ICryptoCallback<KeySet> callback) {
        background.execute(new Runnable() {
            @Override
            public void run() {
                KeySet keyset = keyStorage.GetKeySetForId(keyId);
                if (callback != null) {
                    callback.OnComplete(keyset);
                }
            }
        });
    }

    public KeySet GetStoredKey(long keyId) {
        return keyStorage.GetKeySetForId(keyId);
    }

    /**
     * Generate a RSA Pair key, on complete the pair is added to the {@code keyStorage}.
     *
     * @param length   {@link Integer} value of the RSA key min length.
     * @param callback {@link ICryptoCallback} will return a {@link Boolean} value.
     */
    public void AddRSAKeyAsync(int length, final ICryptoCallback<Boolean> callback) {
        GenerateRSAPairAsync(length, new ICryptoCallback<KeyPair>() {
            @Override
            public void OnComplete(KeyPair data) {
                KeySet ks = new KeySet();
                ks.setPrivateKey(data.getPrivate());
                ks.setPublicKey(data.getPublic());
                final long l = keyStorage.AddRSAKeyPair(ks);

                if (callback != null) {
                    final boolean b = l > -1;
                    uiThread.post(new Runnable() {
                        @Override
                        public void run() {

                            callback.OnComplete(b);
                        }
                    });
                }
            }
        });
    }

    /**
     * Generate a RSA Pair key given the instance {@code key}, on complete the pair is added to the {@code keyStorage}.
     *
     * @param key      {@link KeySet} instance to save in the db.
     * @param callback {@link ICryptoCallback<Boolean>} callback methods.
     */
    public void AddRSAKeyAsync(final KeySet key, final ICryptoCallback<Boolean> callback) {
        GenerateRSAPairAsync(key.getLength(), new ICryptoCallback<KeyPair>() {
            @Override
            public void OnComplete(KeyPair data) {
                key.setPrivateKey(data.getPrivate());
                key.setPublicKey(data.getPublic());
                long l = keyStorage.AddRSAKeyPair(key);
                key.setId(l);
                if (callback != null) {
                    callback.OnComplete(l > -1);
                }
            }
        });
    }

    /**
     * @param key      {@link KeySet} instance to push inside the database.
     * @param callback {@link ICryptoCallback<KeySet>} callback methods.
     */
    public void PushRSAKeyAsync(final KeySet key, final ICryptoCallback<KeySet> callback) {
        GenerateRSAPairAsync(key.getLength(), new ICryptoCallback<KeyPair>() {
            @Override
            public void OnComplete(KeyPair data) {
                key.setPrivateKey(data.getPrivate());
                key.setPublicKey(data.getPublic());
                long l = keyStorage.AddRSAKeyPair(key);
                key.setId(l);
                if (callback != null) {
                    callback.OnComplete(key);
                }
            }
        });
    }
    //endregion

    //region Private Methods

    /**
     * Shorter to prompt the {@link android.app.ProgressDialog} with the {@code text} value.
     *
     * @param text {@link String} text to display in the {@link android.app.ProgressDialog} of the
     *             {@link UIHelper}.
     */
    private void ShowProgress(String text) {
        if (uiHelper != null && dialogNotification) {
            uiHelper.showProgressDialog(text);
        }
    }

    /**
     * Shorter to hide the {@link android.app.ProgressDialog}.
     */
    private void HideProgress() {
        if (uiHelper != null && instance.dialogNotification) {
            uiHelper.dismissProgressDialog();
        }
    }

    /**
     * @param itemId   {@link Long} unique id of the {@link KeySet} to delete.
     * @param callback {@link ICryptoCallback<Boolean>} callback methods.
     */
    public void DeleteKeyAsync(final long itemId, final ICryptoCallback<Boolean> callback) {
        background.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    keyStorage.DeleteKeyById(itemId);
                    if (callback != null) {
                        callback.OnComplete(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.OnComplete(false);
                    }
                }
            }
        });
    }

    public void UpdateActivity(Activity activity) {
        this.uiHelper = UIHelper.getInstance(activity);
    }

    //endregion


}
