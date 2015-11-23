/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.wsservice.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NoSSLValidation extends SSLSocketFactory {
    public SSLContext sslContext;
    public TrustManager tm;
    public HostnameVerifier hv;

    public NoSSLValidation(String algorithm) throws NoSuchAlgorithmException, KeyManagementException {
        super();
        sslContext = SSLContext.getInstance(algorithm);
        hv = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        tm = new X509TrustManager() {
            /**
             * Checks whether the specified certificate chain (partial or complete) can
             * be validated and is trusted for client authentication for the specified
             * authentication type.
             *
             * @param chain    the certificate chain to validate.
             * @param authType the authentication type used.
             * @throws CertificateException     if the certificate chain can't be validated or isn't trusted.
             * @throws IllegalArgumentException if the specified certificate chain is empty or {@code null},
             *                                  or if the specified authentication type is {@code null} or an
             *                                  empty string.
             */
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            /**
             * Checks whether the specified certificate chain (partial or complete) can
             * be validated and is trusted for server authentication for the specified
             * key exchange algorithm.
             *
             * @param chain    the certificate chain to validate.
             * @param authType the key exchange algorithm name.
             * @throws CertificateException     if the certificate chain can't be validated or isn't trusted.
             * @throws IllegalArgumentException if the specified certificate chain is empty or {@code null},
             *                                  or if the specified authentication type is {@code null} or an
             *                                  empty string.
             */
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            /**
             * Returns the list of certificate issuer authorities which are trusted for
             * authentication of peers.
             *
             * @return the list of certificate issuer authorities which are trusted for
             * authentication of peers.
             */
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };
        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the parameters {@code host} and {@code port}. The socket is bound to any
     * available local address and port.
     *
     * @param host the remote host address the socket has to be connected to.
     * @param port the port number of the remote host at which the socket is
     *             connected.
     * @return the created connected socket.
     * @throws IOException          if an error occurs while creating a new socket.
     * @throws UnknownHostException if the specified host is unknown or the IP address could not
     *                              be resolved.
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the parameters {@code host} and {@code port}. The socket is bound to the
     * local network interface specified by the InetAddress {@code localHost} on
     * port {@code localPort}.
     *
     * @param host      the remote host address the socket has to be connected to.
     * @param port      the port number of the remote host at which the socket is
     *                  connected.
     * @param localHost the local host address the socket is bound to.
     * @param localPort the port number of the local host at which the socket is
     *                  bound.
     * @return the created connected socket.
     * @throws IOException          if an error occurs while creating a new socket.
     * @throws UnknownHostException if the specified host is unknown or the IP address could not
     *                              be resolved.
     */
    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the InetAddress {@code host}. The socket is bound to any available local
     * address and port.
     *
     * @param host the host address the socket has to be connected to.
     * @param port the port number of the remote host at which the socket is
     *             connected.
     * @return the created connected socket.
     * @throws IOException if an error occurs while creating a new socket.
     */
    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the InetAddress {@code address}. The socket is bound to the local network
     * interface specified by the InetAddress {@code localHost} on port {@code
     * localPort}.
     *
     * @param address      the remote host address the socket has to be connected to.
     * @param port         the port number of the remote host at which the socket is
     *                     connected.
     * @param localAddress the local host address the socket is bound to.
     * @param localPort    the port number of the local host at which the socket is
     *                     bound.
     * @return the created connected socket.
     * @throws IOException if an error occurs while creating a new socket.
     */
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }

    /**
     * Returns the names of the cipher suites that are enabled by default.
     *
     * @return the names of the cipher suites that are enabled by default.
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    /**
     * Returns the names of the cipher suites that are supported and could be
     * enabled for an SSL connection.
     *
     * @return the names of the cipher suites that are supported.
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    /**
     * Creates an {@code SSLSocket} over the specified socket that is connected
     * to the specified host at the specified port.
     *
     * @param s         the socket.
     * @param host      the host.
     * @param port      the port number.
     * @param autoClose {@code true} if socket {@code s} should be closed when the
     *                  created socket is closed, {@code false} if the socket
     *                  {@code s} should be left open.
     * @return the creates ssl socket.
     * @throws IOException          if creating the socket fails.
     * @throws UnknownHostException if the host is unknown.
     */
    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(s, host, port, autoClose);

    }
}
