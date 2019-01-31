package com.zaitunlabs.zlcore.utils;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.SslErrorHandler;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.zaitunlabs.zlcore.R;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ahmad s on 10/7/2015.
 * Edited by ahmad s on 12/2/2016.
 */


public class HttpClientUtils {
    public static final int DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int DATA_DEFAULT_READ_TIMEOUT_MILLIS = 30 * 1000; // 30s
    public static final int DATA_DEFAULT_WRITE_TIMEOUT_MILLIS = 30 * 1000; // 30s


    public static final int IMAGE_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int IMAGE_DEFAULT_READ_TIMEOUT_MILLIS = 30 * 1000; // 30s
    public static final int IMAGE_DEFAULT_WRITE_TIMEOUT_MILLIS = 30 * 1000; // 30s


    public static final int UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS = 60 * 1000; // 60s
    public static final int UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS = 60 * 1000; // 60s

    public static String webview_user_agent = null;
    public static String androidId = null;



    public static OkHttpClient getHTTPClient(final Context context, String apiVersion){
        return getHTTPClient(context,apiVersion, false);
    }

    public static OkHttpClient getHTTPClient(final Context context, String apiVersion, boolean isUpload){
        OkHttpClient client = null;
        if(context!= null) {
            Interceptor interceptor = getInterceptor(context, apiVersion);
            // Add the interceptor to OkHttpClient
            client = new OkHttpClient.Builder()
                    .connectTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)
                    .build();
        }
        return client;
    }


    public static OkHttpClient getUnsafeHTTPClient(final Context context, String apiVersion){
        return getUnsafeHTTPClient(context,apiVersion, false);
    }

    public static OkHttpClient getUnsafeHTTPClient(final Context context, String apiVersion, boolean isUpload) {
        try {
            OkHttpClient client = null;
            if(context!= null) {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


                Interceptor interceptor = getInterceptor(context, apiVersion);
                // Add the interceptor to OkHttpClient
                client = new OkHttpClient.Builder()
                        .connectTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                        .readTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                        .writeTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                        .addInterceptor(interceptor)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0])
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        })
                        .build();
            }

            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OkHttpClient getCertificatePinnerHTTPClient(final Context context, String apiVersion, @IdRes int certRawResId){
        return getCertificatePinnerHTTPClient(context,apiVersion, false, certRawResId);
    }

    public static OkHttpClient getCertificatePinnerHTTPClient(final Context context, String apiVersion, boolean isUpload,  @IdRes int certRawResId){
        OkHttpClient client = null;
        if(context!= null) {
            CustomTrust customTrust = new CustomTrust(context,apiVersion,isUpload,certRawResId);
            client = customTrust.getClient();
        }
        return client;
    }



    public final static class CustomTrust {
        private final OkHttpClient client;
        private final Context context;
        private final int certRawResId;

        public CustomTrust(final Context context, String apiVersion, boolean isUpload, @IdRes int certRawResId) {
            this.context = context;
            this.certRawResId = certRawResId;
            X509TrustManager trustManager;
            SSLSocketFactory sslSocketFactory;
            try {
                trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }

            Interceptor interceptor = getInterceptor(context, apiVersion);
            // Add the interceptor to OkHttpClient
            client = new OkHttpClient.Builder()
                    .connectTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .readTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .writeTimeout(isUpload ? HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS : HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    //.protocols(Arrays.asList(Protocol.HTTP_1_1))
                    .build();
        }

        public OkHttpClient getClient() {
            return client;
        }

        /**
         * Returns an input stream containing one or more certificate PEM files. This implementation just
         * embeds the PEM files in Java strings; most applications will instead read this from a resource
         * file that gets bundled with the application.
         */
        private InputStream trustedCertificatesInputStream() {
            return context.getResources().openRawResource(certRawResId);
        }

        /**
         * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
         * certificates have not been signed by these certificates will fail with a {@code
         * SSLHandshakeException}.
         *
         * <p>This can be used to replace the host platform's built-in trusted certificates with a custom
         * set. This is useful in development where certificate authority-trusted certificates aren't
         * available. Or in production, to avoid reliance on third-party certificate authorities.
         *
         * <p>See also {@link //CertificatePinner}, which can limit trusted certificates while still using
         * the host platform's built-in trust store.
         *
         * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
         *
         * <p>Relying on your own trusted certificates limits your server team's ability to update their
         * TLS certificates. By installing a specific set of trusted certificates, you take on additional
         * operational complexity and limit your ability to migrate between certificate authorities. Do
         * not use custom trusted certificates in production without the blessing of your server's TLS
         * administrator.
         */
        private X509TrustManager trustManagerForCertificates(InputStream in)
                throws GeneralSecurityException {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
            if (certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }

            // Put the certificates a key store.
            char[] password = "password".toCharArray(); // Any password will work.
            KeyStore keyStore = newEmptyKeyStore(password);
            int index = 0;
            for (Certificate certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }

            // Use it to build an X509 trust manager.
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        }

        private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                InputStream in = null; // By convention, 'null' creates an empty key store.
                keyStore.load(in, password);
                return keyStore;
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }

    }




    public static String getModelNumberInUrlEncode(){
        String model = "";
        try {
            model = CommonUtils.urlEncode(CommonUtils.getModelNumber());
        } catch (UnsupportedEncodingException e) {
            model = CommonUtils.getModelNumber();
        }
        return model;
    }

    private static Interceptor getInterceptor(final Context context, final String apiVersion){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if(TextUtils.isEmpty(webview_user_agent)){
                    //webview_user_agent = new WebView(context).getSettings().getUserAgentString();
                }
                if(TextUtils.isEmpty(androidId)){
                    androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }

                String osVersion = "";
                try {
                    osVersion = CommonUtils.urlEncode(Build.VERSION.RELEASE);
                } catch (UnsupportedEncodingException e) {
                    ////e.printStackTrace();
                }

                String userAgent = "";
                try {
                    userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"));
                } catch (UnsupportedEncodingException e) {
                    ////e.printStackTrace();
                }

                Request.Builder builder = chain.request().newBuilder()
                        .addHeader("Authorization", HttpClientUtils.getAuthAPIKey())
                        .addHeader("x-screensize", CommonUtils.getDisplayMetricsDensityDPIInString(context))
                        .addHeader("x-model", getModelNumberInUrlEncode())
                        .addHeader("x-meid", CommonUtils.getMeid(context))
                        .addHeader("x-packagename", context.getPackageName())
                        .addHeader("x-versionname", CommonUtils.getVersionName(context))
                        .addHeader("x-versioncode", ""+ CommonUtils.getVersionCode(context)+"")
                        .addHeader("x-lang", CommonUtils.getCurrentDeviceLanguage(context))
                        .addHeader("x-platform", "android")//TODO : x-platform
                        .addHeader("x-os", osVersion)//TODO : x-os
                        .addHeader("x-token", PrefsData.getToken())
                        .addHeader("x-deviceid", androidId)//TODO : x-deviceid
                        .addHeader("x-useragent", userAgent)//TODO : x-useragent
                        .addHeader("User-Agent", userAgent);//TODO : User-Agent


                builder.addHeader("x-api", apiVersion);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };
        return interceptor;
    }


    public static void setCustomPicassoSingletoneInstance(Context context, String apiVersion){
        Picasso.setSingletonInstance(getPicassoInstance(context,apiVersion));
    }


    private static Picasso getPicassoInstance(Context context, String apiVersion){
        Interceptor interceptor = getInterceptor(context, apiVersion);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HttpClientUtils.IMAGE_DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(HttpClientUtils.IMAGE_DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(HttpClientUtils.IMAGE_DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .build();
        Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
        return picasso;
    }



    public static String getAuthBasic(){
        String credentials = PrefsData.getUserID()+":"+ PrefsData.getSecret();
        String result="";
        result = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return result;
    }

    public static String getAuthAPIKey(){
        String credentials = "APIKEY="+APIConstant.API_KEY;
        String result="";
        result = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return result;
    }

    public static void handleWebviewSSLError(Context context, final SslErrorHandler handler, SslError error){
        String sslMessage = "";
        switch(error.getPrimaryError()) {
            case SslError.SSL_DATE_INVALID:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_date_invalid).toString();
                break;
            case SslError.SSL_EXPIRED:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_expired).toString();
                break;
            case SslError.SSL_IDMISMATCH:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_idmismatch).toString();
                break;
            case SslError.SSL_INVALID:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_invalid).toString();
                break;
            case SslError.SSL_NOTYETVALID:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_not_yet_valid).toString();
                break;
            case SslError.SSL_UNTRUSTED:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_untrusted).toString();
                break;
            default:
                sslMessage = context.getText(R.string.zlcore_notification_error_ssl_cert_invalid).toString();
        }

        CommonUtils.showDialog2Option(context, context.getText(R.string.zlcore_notification_error_ssl_title).toString(), sslMessage,
                context.getText(R.string.zlcore_notification_error_ssl_continue_text).toString(), new Runnable() {
                    @Override
                    public void run() {
                        handler.proceed();
                    }
                }, context.getText(R.string.zlcore_notification_error_ssl_cancel_text).toString(), new Runnable() {
                    @Override
                    public void run() {
                        handler.cancel();
                    }
                });
    }
}
