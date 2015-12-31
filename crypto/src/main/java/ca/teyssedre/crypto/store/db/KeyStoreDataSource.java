/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Pierre Teyssedre
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

package ca.teyssedre.crypto.store.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.crypto.store.models.KeySet;

public class KeyStoreDataSource extends BaseDataSource {

    //region Properties
    private static final String KEY_STORE_TABLE_NAME = "keystore";
    private static final String TITLE_KEY_COLUMN = "title";
    private static final String DESCRIPTION_KEY_COLUMN = "description";
    private static final String CREATE_DATE_KEY_COLUMN = "created";
    private static final String MODIFY_DATE_KEY_COLUMN = "modified";
    private static final String ENCRYPT_KEY_COLUMN = "encrypt";
    private static final String DECRYPT_KEY_COLUMN = "decrypt";
    private static final String SECRET_KEY_COLUMN = "secret";

    private static final String CREATE_TABLE = "CREATE TABLE "
            + KEY_STORE_TABLE_NAME + "(" + CryptoStorageHelper.COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE_KEY_COLUMN + " TEXT, "
            + DESCRIPTION_KEY_COLUMN + " TEXT, "
            + CREATE_DATE_KEY_COLUMN + " DATE, "
            + MODIFY_DATE_KEY_COLUMN + " DATE, "
            + ENCRYPT_KEY_COLUMN + " TEXT, "
            + DECRYPT_KEY_COLUMN + " TEXT, "
            + SECRET_KEY_COLUMN + " TEXT "
            + ");";

    private CryptoStorageHelper dbHelper;
    private SQLiteDatabase database;
    private String[] allColumns = {CryptoStorageHelper.COLUMN_ID, TITLE_KEY_COLUMN,
            DESCRIPTION_KEY_COLUMN, CREATE_DATE_KEY_COLUMN, MODIFY_DATE_KEY_COLUMN,
            ENCRYPT_KEY_COLUMN, DECRYPT_KEY_COLUMN, SECRET_KEY_COLUMN};
    private boolean validateInsert = false;
    //endregion

    public KeyStoreDataSource(Context context) {
        dbHelper = CryptoStorageHelper.getInstance(context);
        dbHelper.RegisterDataSource(this);
        try {
            open();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            database.close();
            database = null;
        }
        dbHelper.close();
    }

    public long AddRSAKeyPair(KeySet keyPair) {
        try {
            open();
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put(TITLE_KEY_COLUMN, keyPair.getTitle());
            values.put(DESCRIPTION_KEY_COLUMN, keyPair.getDescription());
            values.put(CREATE_DATE_KEY_COLUMN, iso8601Format.format(keyPair.getCreated()));
            values.put(MODIFY_DATE_KEY_COLUMN, iso8601Format.format(keyPair.getModified()));
            values.put(ENCRYPT_KEY_COLUMN, DatabaseUtils.sqlEscapeString(Crypto.PrivateKeyRSAToString(keyPair.getPrivateKey())));
            values.put(DECRYPT_KEY_COLUMN, DatabaseUtils.sqlEscapeString(Crypto.PublicKeyRSAToString(keyPair.getPublicKey())));
            long insertId = database.insert(KEY_STORE_TABLE_NAME, null, values);
            if (validateInsert) {
                Cursor cursor = database.query(KEY_STORE_TABLE_NAME, allColumns, CryptoStorageHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
                cursor.moveToFirst();
                KeySet ks = cursorToKeySet(cursor);
                //TODO: validate keys to ensure serialization/deserialization works fine ...
                cursor.close();
            }
            close();
            return insertId;
        } catch (SQLException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<KeySet> GetAllKeySet() {
        List<KeySet> list = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(KEY_STORE_TABLE_NAME, allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                KeySet ks = cursorToKeySet(cursor);
                list.add(ks);
            }
            cursor.close();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void DeleteKeyById(long itemId) throws SQLException {
        open();
        database.delete(KEY_STORE_TABLE_NAME, CryptoStorageHelper.COLUMN_ID + " = " + itemId, null);
        close();
    }

    private KeySet cursorToKeySet(Cursor cursor) {
        KeySet ks = new KeySet();
        ks.setId(cursor.getLong(0));
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            ks.setTitle(cursor.getString(1));
            ks.setDescription(cursor.getString(2));
            ks.setCreated(iso8601Format.parse(cursor.getString(3)));
            ks.setModified(iso8601Format.parse(cursor.getString(4)));
            if (cursor.getString(5) != null)
                ks.setPrivateKey(Crypto.StringToPrivateKey(cursor.getString(5).replaceAll("'", "")));
            if (cursor.getString(6) != null)
                ks.setPublicKey(Crypto.StringToPublicKey(cursor.getString(6).replaceAll("'", "")));
            if (cursor.getString(7) != null)
                ks.setSecretKey(Crypto.StringToAESKey(cursor.getString(7).replaceAll("'", "")));
            cursor.moveToNext();
        } catch (GeneralSecurityException | ParseException e) {
            e.printStackTrace();
        }
        return ks;
    }

    public boolean isValidateInsert() {
        return validateInsert;
    }

    public void setValidateInsert(boolean validateInsert) {
        this.validateInsert = validateInsert;
    }

    //region BaseDataSource
    @Override
    public String CreateTableQuery() {
        return CREATE_TABLE;
    }

    @Override
    public String UpgradeTableQuery(int oldVersion, int newVersion) {
        return null;
    }

    public KeySet GetKeySetForId(long keyId) {
        List<KeySet> list = new ArrayList<>();
        try {
            open();
            Cursor cursor = database.query(KEY_STORE_TABLE_NAME, allColumns, CryptoStorageHelper.COLUMN_ID + " = " + keyId, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                KeySet ks = cursorToKeySet(cursor);
                list.add(ks);
            }
            cursor.close();
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.size() > 0 ? list.get(0) : null;
    }

    //endregion
}
