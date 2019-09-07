package com.zaitunlabs.zlcore.utils

import android.content.Context
import android.net.http.SslError
import android.os.Build

import androidx.annotation.IdRes
import android.text.TextUtils
import android.util.Base64
import android.webkit.SslErrorHandler

import com.jakewharton.picasso.OkHttp3Downloader
import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.api.APIConstant
import com.squareup.picasso.Picasso

import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.concurrent.TimeUnit

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by ahmad s on 10/7/2015.
 * Edited by ahmad s on 12/2/2016.
 */


object HttpClientUtils {
    private val DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000 // 15s
    private val DATA_DEFAULT_READ_TIMEOUT_MILLIS = 30 * 1000 // 30s
    private val DATA_DEFAULT_WRITE_TIMEOUT_MILLIS = 30 * 1000 // 30s


    private val IMAGE_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000 // 15s
    private val IMAGE_DEFAULT_READ_TIMEOUT_MILLIS = 30 * 1000 // 30s
    private val IMAGE_DEFAULT_WRITE_TIMEOUT_MILLIS = 30 * 1000 // 30s


    private val UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000 // 15s
    private val UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS = 60 * 1000 // 60s
    private val UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS = 60 * 1000 // 60s

    private val webview_user_agent: String? = null
    private var androidId: String? = null
    private var randomUUID: String? = null

    @Volatile
    private var singletonClient: OkHttpClient? = null
    @Volatile
    private var singletonUploadClient: OkHttpClient? = null
    @Volatile
    private var singletonUnsafeClient: OkHttpClient? = null
    @Volatile
    private var singletonUploadUnsafeClient: OkHttpClient? = null
    @Volatile
    private var singletonBuilderConfig: BuilderConfig? = null


    val modelNumberInUrlEncode: String
        get() {
            var model = ""
            try {
                model = CommonUtils.urlEncode(CommonUtils.modelNumber)
            } catch (e: UnsupportedEncodingException) {
                model = CommonUtils.modelNumber
            }

            return model
        }


    val authBasic: String
        get() {
            val credentials = PrefsData.userID + ":" + PrefsData.secret
            var result = ""
            result = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            return result
        }

    val authAPIKey: String
        get() {
            val credentials = "APIKEY=" + APIConstant.API_KEY
            var result = ""
            result = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            return result
        }


    fun getHeaderList(isMeid: Boolean, isAndroidID: Boolean, isRandomUUID: Boolean, isUserAgent: Boolean): ArrayList<String> {
        val headerList = ArrayList<String>()
        headerList.add("Authorization")
        headerList.add("x-screensize")
        headerList.add("x-model")
        if (isMeid) {
            headerList.add("x-meid")
        }
        headerList.add("x-packagename")
        headerList.add("x-versionname")
        headerList.add("x-versioncode")
        headerList.add("x-lang")
        headerList.add("x-platform")
        headerList.add("x-os")
        headerList.add("x-token")
        if (isAndroidID) {
            headerList.add("x-androidid")
        }
        if (isRandomUUID) {
            headerList.add("x-randomuuid")
        }
        if (isUserAgent) {
            headerList.add("x-useragent")
            headerList.add("User-Agent")
        }
        return headerList
    }

    fun getHeaderMap(context: Context, headerList: List<String>?): HashMap<String, String> {
        val headerMap = HashMap<String, String>()
        var userAgent = ""
        if (headerList != null) {
            for (header in headerList) {
                when (header) {
                    "Authorization" -> headerMap["Authorization"] = HttpClientUtils.authAPIKey
                    "x-screensize" -> headerMap["x-screensize"] = CommonUtils.getDisplayMetricsDensityDPIInString(context)
                    "x-model" -> headerMap["x-model"] = modelNumberInUrlEncode
                    "x-meid" -> headerMap["x-meid"] = CommonUtils.getMeid(context)
                    "x-packagename" -> headerMap["x-packagename"] = context.packageName
                    "x-versionname" -> headerMap["x-versionname"] = CommonUtils.getVersionName(context)
                    "x-versioncode" -> headerMap["x-versioncode"] = "" + CommonUtils.getVersionCode(context) + ""
                    "x-lang" -> headerMap["x-lang"] = CommonUtils.getCurrentDeviceLanguage(context)
                    "x-platform" -> headerMap["x-platform"] = "android"
                    "x-os" -> {
                        var osVersion = ""
                        try {
                            osVersion = CommonUtils.urlEncode(Build.VERSION.RELEASE)
                        } catch (e: UnsupportedEncodingException) {
                            ////e.printStackTrace();
                        }

                        headerMap["x-os"] = osVersion
                    }
                    "x-token" -> headerMap["x-token"] = PrefsData.token
                    "x-androidid" -> {
                        if (TextUtils.isEmpty(androidId)) {
                            androidId = CommonUtils.getAndroidID(context)
                        }
                        headerMap["x-androidid"] = androidId
                    }
                    "x-randomuuid" -> {
                        if (TextUtils.isEmpty(randomUUID)) {
                            randomUUID = CommonUtils.getRandomUUID(context)
                        }
                        headerMap["x-randomuuid"] = randomUUID
                    }
                    "x-useragent" -> {
                        try {
                            userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"))
                        } catch (e: UnsupportedEncodingException) {
                            ////e.printStackTrace();
                        }

                        headerMap["x-useragent"] = userAgent
                    }
                    "User-Agent" -> {
                        try {
                            userAgent = CommonUtils.urlEncode(System.getProperty("http.agent"))
                        } catch (e: UnsupportedEncodingException) {
                            ////e.printStackTrace();
                        }

                        headerMap["User-Agent"] = userAgent
                    }
                }
            }
        }
        return headerMap
    }


    fun setSingletonBuilderConfig(builderConfig: BuilderConfig?) {
        if (builderConfig == null) {
            throw IllegalArgumentException("CUstomOkHttpBuilder must not be null.")
        }
        synchronized(HttpClientUtils::class.java) {
            if (singletonBuilderConfig != null) {
                throw IllegalStateException("Singleton instance already exists.")
            }
            singletonBuilderConfig = builderConfig
        }
    }

    fun setSingletonClient(okHttpClient: OkHttpClient?) {
        if (okHttpClient == null) {
            throw IllegalArgumentException("OkHttpClient must not be null.")
        }
        synchronized(HttpClientUtils::class.java) {
            if (singletonClient != null) {
                throw IllegalStateException("Singleton instance already exists.")
            }
            singletonClient = okHttpClient
        }
    }

    fun setSingletonUploadClient(okHttpClient: OkHttpClient?) {
        if (okHttpClient == null) {
            throw IllegalArgumentException("OkHttpClient must not be null.")
        }
        synchronized(HttpClientUtils::class.java) {
            if (singletonUploadClient != null) {
                throw IllegalStateException("Singleton instance already exists.")
            }
            singletonUploadClient = okHttpClient
        }
    }

    fun setSingletonUnsafeClient(unsafeOkHttpClient: OkHttpClient?) {
        if (unsafeOkHttpClient == null) {
            throw IllegalArgumentException("OkHttpClient must not be null.")
        }
        synchronized(HttpClientUtils::class.java) {
            if (singletonUnsafeClient != null) {
                throw IllegalStateException("Singleton instance already exists.")
            }
            singletonUnsafeClient = unsafeOkHttpClient
        }
    }

    fun setSingletonUploadUnsafeClient(unsafeOkHttpClient: OkHttpClient?) {
        if (unsafeOkHttpClient == null) {
            throw IllegalArgumentException("OkHttpClient must not be null.")
        }
        synchronized(HttpClientUtils::class.java) {
            if (singletonUploadUnsafeClient != null) {
                throw IllegalStateException("Singleton instance already exists.")
            }
            singletonUploadUnsafeClient = unsafeOkHttpClient
        }
    }

    @JvmOverloads
    fun getHTTPClient(context: Context, apiVersion: String, isMeid: Boolean, builderConfig: BuilderConfig? = null): OkHttpClient? {
        return getHTTPClient(context, apiVersion, isMeid, false, builderConfig)
    }

    @JvmOverloads
    fun getHTTPClient(context: Context, apiVersion: String, isMeid: Boolean, isUpload: Boolean, builderConfig: BuilderConfig? = null): OkHttpClient? {
        val headerList = getHeaderList(isMeid, isMeid, isMeid, isMeid)
        val headerMap = getHeaderMap(context, headerList)
        return getHTTPClient(context, headerMap, apiVersion, isUpload, builderConfig)
    }


    fun getHTTPClient(context: Context?, headerMap: Map<String, String>, apiVersion: String, isUpload: Boolean, builderConfig: BuilderConfig?): OkHttpClient? {
        if (isUpload) {
            if (singletonUploadClient != null) {
                return singletonUploadClient
            }
        } else {
            if (singletonClient != null) {
                return singletonClient
            }
        }

        var client: OkHttpClient? = null
        if (context != null) {
            val interceptor = getInterceptor(headerMap, apiVersion)
            // Add the interceptor to OkHttpClient
            val builder = OkHttpClient.Builder()
                    .connectTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                    .readTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                    .writeTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)

            if (singletonBuilderConfig != null) {
                singletonBuilderConfig!!.configure(builder)
            }

            builderConfig?.configure(builder)

            client = builder.build()
        }
        return client
    }


    fun getUnsafeHTTPClient(context: Context, headerMap: Map<String, String>, apiVersion: String, builderConfig: BuilderConfig): OkHttpClient? {
        return getUnsafeHTTPClient(context, headerMap, apiVersion, false, builderConfig)
    }

    fun getUnsafeHTTPClient(context: Context?, headerMap: Map<String, String>, apiVersion: String, isUpload: Boolean, builderConfig: BuilderConfig?): OkHttpClient? {
        if (isUpload) {
            if (singletonUploadUnsafeClient != null) {
                return singletonUploadUnsafeClient
            }
        } else {
            if (singletonUnsafeClient != null) {
                return singletonUnsafeClient
            }
        }

        try {
            var client: OkHttpClient? = null
            if (context != null) {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory


                val interceptor = getInterceptor(headerMap, apiVersion)
                // Add the interceptor to OkHttpClient
                val builder = OkHttpClient.Builder()
                        .connectTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                        .readTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                        .writeTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                        .addInterceptor(interceptor)
                        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                        .hostnameVerifier { hostname, session -> true }

                if (singletonBuilderConfig != null) {
                    singletonBuilderConfig!!.configure(builder)
                }

                builderConfig?.configure(builder)

                client = builder.build()
            }

            return client
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun getCertificatePinnerHTTPClient(context: Context, headerMap: Map<String, String>, apiVersion: String, @IdRes certRawResId: Int, builderConfig: BuilderConfig): OkHttpClient? {
        return getCertificatePinnerHTTPClient(context, headerMap, apiVersion, false, certRawResId, builderConfig)
    }

    fun getCertificatePinnerHTTPClient(context: Context?, headerMap: Map<String, String>, apiVersion: String, isUpload: Boolean, @IdRes certRawResId: Int, builderConfig: BuilderConfig): OkHttpClient? {
        var client: OkHttpClient? = null
        if (context != null) {
            val customTrust = CustomTrust(context, headerMap, apiVersion, isUpload, certRawResId, builderConfig)
            client = customTrust.client
        }
        return client
    }


    interface BuilderConfig {
        fun configure(builder: OkHttpClient.Builder)
    }


    class CustomTrust(private val context: Context, headerMap: Map<String, String>, apiVersion: String, isUpload: Boolean, @param:IdRes private val certRawResId: Int, builderConfig: BuilderConfig?) {
        val client: OkHttpClient

        init {
            val trustManager: X509TrustManager
            val sslSocketFactory: SSLSocketFactory
            try {
                trustManager = trustManagerForCertificates(trustedCertificatesInputStream())
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
                sslSocketFactory = sslContext.socketFactory
            } catch (e: GeneralSecurityException) {
                throw RuntimeException(e)
            }

            val interceptor = getInterceptor(headerMap, apiVersion)
            // Add the interceptor to OkHttpClient
            val builder = OkHttpClient.Builder()
                    .connectTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_CONNECT_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_CONNECT_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                    .readTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_READ_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_READ_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                    .writeTimeout((if (isUpload) HttpClientUtils.UPLOAD_DEFAULT_WRITE_TIMEOUT_MILLIS else HttpClientUtils.DATA_DEFAULT_WRITE_TIMEOUT_MILLIS).toLong(), TimeUnit.MILLISECONDS)
                    .addInterceptor(interceptor)
                    .sslSocketFactory(sslSocketFactory, trustManager)
            //.protocols(Arrays.asList(Protocol.HTTP_1_1))

            if (singletonBuilderConfig != null) {
                singletonBuilderConfig!!.configure(builder)
            }

            builderConfig?.configure(builder)

            client = builder.build()
        }

        /**
         * Returns an input stream containing one or more certificate PEM files. This implementation just
         * embeds the PEM files in Java strings; most applications will instead read this from a resource
         * file that gets bundled with the application.
         */
        private fun trustedCertificatesInputStream(): InputStream {
            return context.resources.openRawResource(certRawResId)
        }

        /**
         * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
         * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
         *
         *
         * This can be used to replace the host platform's built-in trusted certificates with a custom
         * set. This is useful in development where certificate authority-trusted certificates aren't
         * available. Or in production, to avoid reliance on third-party certificate authorities.
         *
         *
         * See also [//CertificatePinner], which can limit trusted certificates while still using
         * the host platform's built-in trust store.
         *
         * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
         *
         *
         * Relying on your own trusted certificates limits your server team's ability to update their
         * TLS certificates. By installing a specific set of trusted certificates, you take on additional
         * operational complexity and limit your ability to migrate between certificate authorities. Do
         * not use custom trusted certificates in production without the blessing of your server's TLS
         * administrator.
         */
        @Throws(GeneralSecurityException::class)
        private fun trustManagerForCertificates(`in`: InputStream): X509TrustManager {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificates = certificateFactory.generateCertificates(`in`)
            if (certificates.isEmpty()) {
                throw IllegalArgumentException("expected non-empty set of trusted certificates")
            }

            // Put the certificates a key store.
            val password = "password".toCharArray() // Any password will work.
            val keyStore = newEmptyKeyStore(password)
            var index = 0
            for (certificate in certificates) {
                val certificateAlias = Integer.toString(index++)
                keyStore.setCertificateEntry(certificateAlias, certificate)
            }

            // Use it to build an X509 trust manager.
            val keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, password)
            val trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            val trustManagers = trustManagerFactory.trustManagers
            if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
                throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
            }
            return trustManagers[0] as X509TrustManager
        }

        @Throws(GeneralSecurityException::class)
        private fun newEmptyKeyStore(password: CharArray): KeyStore {
            try {
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                val `in`: InputStream? = null // By convention, 'null' creates an empty key store.
                keyStore.load(`in`, password)
                return keyStore
            } catch (e: IOException) {
                throw AssertionError(e)
            }

        }

    }

    private fun getInterceptor(headerMap: Map<String, String>, apiVersion: String): Interceptor {
        return Interceptor { chain ->
            val builder = chain.request().newBuilder()

            for ((key, value) in headerMap) {
                builder.addHeader(key, value)
            }

            if (!TextUtils.isEmpty(apiVersion)) {
                builder.addHeader("x-api", apiVersion)
            }

            val newRequest = builder.build()
            chain.proceed(newRequest)
        }
    }


    fun setCustomPicassoSingletoneInstance(context: Context, headerMap: Map<String, String>, apiVersion: String, builderConfig: BuilderConfig) {
        Picasso.setSingletonInstance(getPicassoInstance(context, headerMap, apiVersion, builderConfig))
    }


    private fun getPicassoInstance(context: Context, headerMap: Map<String, String>, apiVersion: String, builderConfig: BuilderConfig?): Picasso {
        val interceptor = getInterceptor(headerMap, apiVersion)
        val builder = OkHttpClient.Builder()
                .connectTimeout(HttpClientUtils.IMAGE_DEFAULT_CONNECT_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(HttpClientUtils.IMAGE_DEFAULT_READ_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(HttpClientUtils.IMAGE_DEFAULT_WRITE_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)

        if (singletonBuilderConfig != null) {
            singletonBuilderConfig!!.configure(builder)
        }

        builderConfig?.configure(builder)

        val client = builder.build()
        return Picasso.Builder(context).downloader(OkHttp3Downloader(client)).build()
    }

    fun handleWebviewSSLError(context: Context, handler: SslErrorHandler, error: SslError) {
        var sslMessage = ""
        when (error.primaryError) {
            SslError.SSL_DATE_INVALID -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_date_invalid).toString()
            SslError.SSL_EXPIRED -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_expired).toString()
            SslError.SSL_IDMISMATCH -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_idmismatch).toString()
            SslError.SSL_INVALID -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_invalid).toString()
            SslError.SSL_NOTYETVALID -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_not_yet_valid).toString()
            SslError.SSL_UNTRUSTED -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_untrusted).toString()
            else -> sslMessage = context.getText(R.string.zlcore_notification_error_ssl_cert_invalid).toString()
        }

        CommonUtils.showDialog2Option(context, context.getText(R.string.zlcore_notification_error_ssl_title).toString(), sslMessage,
                context.getText(R.string.zlcore_notification_error_ssl_continue_text).toString(), { handler.proceed() }, context.getText(R.string.zlcore_notification_error_ssl_cancel_text).toString(), { handler.cancel() })
    }
}
