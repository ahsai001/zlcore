package com.zaitunlabs.zlcore.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64

import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by ahmad s on 2019-06-30.
 */
object Lookup {
    private val KEY_SIZE = 256
    // requires Spongycastle crypto libraries
    // private static final String AES_KEY_ALG = "AES/GCM/NoPadding";
    // private static final String AES_KEY_ALG = "AES/CBC/PKCS5Padding";
    private val AES_KEY_ALG = "AES"
    private val PRIMARY_PBE_KEY_ALG = "PBKDF2WithHmacSHA1"
    private val BACKUP_PBE_KEY_ALG = "PBEWithMD5AndDES"
    private val ITERATIONS = 2000
    // change to SC if using Spongycastle crypto libraries
    private val PROVIDER = "BC"
    private var sKey: ByteArray? = null

    private var isSecureEnabled = false
    private var sqLiteWrapper: SQLiteWrapper? = null

    @JvmOverloads
    fun init(context: Context, isSecureEnabled: Boolean = false) {
        if (sqLiteWrapper == null) {
            sqLiteWrapper = SQLiteWrapper.getLookupDatabase(context)
        }

        // Initialize encryption/decryption key
        Lookup.isSecureEnabled = false
        if (isSecureEnabled) {
            try {
                val key = generateAesKeyName(context)
                var value = get(key, null)
                if (value == null) {
                    value = generateAesKeyValue()
                    Lookup[key] = value
                }
                sKey = decode(value)
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }

        }

        Lookup.isSecureEnabled = isSecureEnabled
    }


    private fun encode(input: ByteArray): String {
        return Base64.encodeToString(input, Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun decode(input: String?): ByteArray {
        return Base64.decode(input, Base64.NO_PADDING or Base64.NO_WRAP)
    }

    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class)
    private fun generateAesKeyName(context: Context): String {
        val password = context.packageName.toCharArray()
        val salt = getDeviceSerialNumber(context).toByteArray()
        var key: SecretKey
        try {
            // TODO: what if there's an OS upgrade and now supports the primary
            // PBE
            key = generatePBEKey(password, salt,
                    PRIMARY_PBE_KEY_ALG, ITERATIONS, KEY_SIZE)
        } catch (e: NoSuchAlgorithmException) {
            // older devices may not support the have the implementation try
            // with a weaker
            // algorthm
            key = generatePBEKey(password, salt,
                    BACKUP_PBE_KEY_ALG, ITERATIONS, KEY_SIZE)
        }

        return encode(key.encoded)
    }

    /**
     * Derive a secure key based on the passphraseOrPin
     *
     * @param passphraseOrPin
     * @param salt
     * @param algorthm
     * - which PBE algorthm to use. some <4.0 devices don;t support
     * the prefered PBKDF2WithHmacSHA1
     * @param iterations
     * - Number of PBKDF2 hardening rounds to use. Larger values
     * increase computation time (a good thing), defaults to 1000 if
     * not set.
     * @param keyLength
     * @return Derived Secretkey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchProviderException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, NoSuchProviderException::class)
    private fun generatePBEKey(passphraseOrPin: CharArray,
                               salt: ByteArray, algorthm: String, iterations: Int, keyLength: Int): SecretKey {
        var iterations = iterations
        if (iterations == 0) {
            iterations = 1000
        }
        val secretKeyFactory = SecretKeyFactory.getInstance(
                algorthm, PROVIDER)
        val keySpec = PBEKeySpec(passphraseOrPin, salt, iterations,
                keyLength)
        return secretKeyFactory.generateSecret(keySpec)
    }

    /**
     * Gets the hardware serial number of this device.
     *
     * @return serial number or Settings.Secure.ANDROID_ID if not available.
     */
    private fun getDeviceSerialNumber(context: Context): String {
        // We're using the Reflection API because Build.SERIAL is only available
        // since API Level 9 (Gingerbread, Android 2.3).
        try {
            var deviceSerial = Build::class.java!!.getField("SERIAL").get(null) as String
            if (TextUtils.isEmpty(deviceSerial)) {
                deviceSerial = Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ANDROID_ID)
            }
            return deviceSerial
        } catch (ignored: Exception) {
            // default to Android_ID
            return Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ANDROID_ID)
        }

    }

    @Throws(NoSuchAlgorithmException::class)
    private fun generateAesKeyValue(): String {
        // Do *not* seed secureRandom! Automatically seeded from system entropy
        val random = SecureRandom()
        // Use the largest AES key length which is supported by the OS
        val generator = KeyGenerator.getInstance("AES")
        try {
            generator.init(KEY_SIZE, random)
        } catch (e: Exception) {
            try {
                generator.init(192, random)
            } catch (e1: Exception) {
                generator.init(128, random)
            }

        }

        return encode(generator.generateKey().encoded)
    }

    private fun encrypt(cleartext: String?): String? {
        if (cleartext == null || cleartext.length == 0) {
            return cleartext
        }
        try {
            val cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(
                    sKey, AES_KEY_ALG))
            return encode(cipher.doFinal(cleartext
                    .toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            return null
        }

    }

    private fun decrypt(ciphertext: String?): String? {
        if (ciphertext == null || ciphertext.length == 0) {
            return ciphertext
        }
        try {
            val cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER)
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(
                    sKey, AES_KEY_ALG))
            return String(cipher.doFinal(decode(ciphertext)), "UTF-8")
        } catch (e: Exception) {
            return null
        }

    }

    private fun checkCondition() {
        if (sqLiteWrapper == null) {
            throw IllegalStateException("you need to run init method first, you can put it inside oncreate of Application")
        }
    }


    private fun checkSecureCondition() {
        if (!isSecureEnabled) {
            throw IllegalStateException("you need to run init method first with parameter isSecuredEnabled true, you can put it inside oncreate of Application")
        }
    }


    //string
    operator fun get(key: String, defaultValue: String?): String? {
        return get(key, defaultValue, false)
    }

    fun getS(key: String, defaultValue: String): String? {
        return get(key, defaultValue, true)
    }

    operator fun set(key: String, value: String?) {
        set(key, value, false)
    }

    fun setS(key: String, value: String) {
        set(key, value, true)
    }

    private operator fun get(key: String?, defaultValue: String?, isSecureEnabled: Boolean): String? {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        return if (lookup == null) defaultValue else if (isSecureEnabled) decrypt(lookup.string) else lookup.string
    }


    private operator fun set(key: String?, value: String?, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        var lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        if (lookup != null) {
            lookup.string = if (isSecureEnabled) encrypt(value) else value
            lookup.update()
        } else {
            lookup = SQLiteWrapper.TLookup()
            lookup.key = key
            lookup.string = if (isSecureEnabled) encrypt(value) else value
            lookup.save()
        }
    }


    //boolean
    operator fun get(key: String, defaultValue: Boolean): Boolean {
        return get(key, defaultValue, false)
    }

    fun getS(key: String, defaultValue: Boolean): Boolean {
        return get(key, defaultValue, true)
    }

    operator fun set(key: String, value: Boolean) {
        set(key, value, false)
    }

    fun setS(key: String, value: Boolean) {
        set(key, value, true)
    }

    private operator fun get(key: String?, defaultValue: Boolean, isSecureEnabled: Boolean): Boolean {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        return if (lookup == null) defaultValue else if (isSecureEnabled) java.lang.Boolean.parseBoolean(decrypt(lookup.string)) else lookup.boolean
    }

    private operator fun set(key: String?, value: Boolean, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        var lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        if (lookup != null) {
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Boolean.toString(value))
            } else {
                lookup.boolean = value
            }
            lookup.update()
        } else {
            lookup = SQLiteWrapper.TLookup()
            lookup.key = key
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Boolean.toString(value))
            } else {
                lookup.boolean = value
            }
            lookup.save()
        }
    }


    //int
    operator fun get(key: String, defaultValue: Int): Int {
        return get(key, defaultValue, false)
    }

    fun getS(key: String, defaultValue: Int): Int {
        return get(key, defaultValue, true)
    }

    operator fun set(key: String, value: Int) {
        set(key, value, false)
    }

    fun setS(key: String, value: Int) {
        set(key, value, true)
    }

    private operator fun get(key: String?, defaultValue: Int, isSecureEnabled: Boolean): Int {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        return if (lookup == null) defaultValue else if (isSecureEnabled) Integer.parseInt(decrypt(lookup.string)!!) else lookup.int
    }

    private operator fun set(key: String?, value: Int, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        var lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        if (lookup != null) {
            if (isSecureEnabled) {
                lookup.string = encrypt(Integer.toString(value))
            } else {
                lookup.int = value
            }
            lookup.update()
        } else {
            lookup = SQLiteWrapper.TLookup()
            lookup.key = key
            if (isSecureEnabled) {
                lookup.string = encrypt(Integer.toString(value))
            } else {
                lookup.int = value
            }
            lookup.save()
        }
    }


    //long
    operator fun get(key: String, defaultValue: Long): Long {
        return get(key, defaultValue, false)
    }

    fun getS(key: String, defaultValue: Long): Long {
        return get(key, defaultValue, true)
    }

    operator fun set(key: String, value: Long) {
        set(key, value, false)
    }

    fun setS(key: String, value: Long) {
        set(key, value, true)
    }

    private operator fun get(key: String?, defaultValue: Long, isSecureEnabled: Boolean): Long {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        return if (lookup == null) defaultValue else if (isSecureEnabled) java.lang.Long.parseLong(decrypt(lookup.string)!!) else lookup.long
    }

    private operator fun set(key: String?, value: Long, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        var lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        if (lookup != null) {
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Long.toString(value))
            } else {
                lookup.long = value
            }
            lookup.update()
        } else {
            lookup = SQLiteWrapper.TLookup()
            lookup.key = key
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Long.toString(value))
            } else {
                lookup.long = value
            }
            lookup.save()
        }
    }


    //float
    operator fun get(key: String, defaultValue: Float): Float {
        return get(key, defaultValue, false)
    }

    fun getS(key: String, defaultValue: Float): Float {
        return get(key, defaultValue, true)
    }

    operator fun set(key: String, value: Float) {
        set(key, value, false)
    }

    fun setS(key: String, value: Float) {
        set(key, value, true)
    }

    private operator fun get(key: String?, defaultValue: Float, isSecureEnabled: Boolean): Float {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        return if (lookup == null) defaultValue else if (isSecureEnabled) java.lang.Float.parseFloat(decrypt(lookup.string)!!) else lookup.float
    }

    private operator fun set(key: String?, value: Float, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        var lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        if (lookup != null) {
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Float.toString(value))
            } else {
                lookup.float = value
            }
            lookup.update()
        } else {
            lookup = SQLiteWrapper.TLookup()
            lookup.key = key
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Float.toString(value))
            } else {
                lookup.float = value
            }
            lookup.save()
        }
    }


    //double
    operator fun get(key: String, defaultValue: Double): Double {
        return get(key, defaultValue, false)
    }

    fun getS(key: String, defaultValue: Double): Double {
        return get(key, defaultValue, true)
    }

    operator fun set(key: String, value: Double) {
        set(key, value, false)
    }

    fun setS(key: String, value: Double) {
        set(key, value, true)
    }

    private operator fun get(key: String?, defaultValue: Double, isSecureEnabled: Boolean): Double {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        return if (lookup == null) defaultValue else if (isSecureEnabled) java.lang.Double.parseDouble(decrypt(lookup.string)!!) else lookup.double
    }

    private operator fun set(key: String?, value: Double, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }

        var lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        if (lookup != null) {
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Double.toString(value))
            } else {
                lookup.double = value
            }
            lookup.update()
        } else {
            lookup = SQLiteWrapper.TLookup()
            lookup.key = key
            if (isSecureEnabled) {
                lookup.string = encrypt(java.lang.Double.toString(value))
            } else {
                lookup.double = value
            }
            lookup.save()
        }
    }


    fun remove(key: String) {
        remove(key, false)
    }

    fun removeS(key: String) {
        remove(key, true)
    }

    private fun remove(key: String?, isSecureEnabled: Boolean) {
        var key = key
        checkCondition()
        if (isSecureEnabled) {
            checkSecureCondition()
            key = encrypt(key)
        }
        val lookup = sqLiteWrapper!!.findFirstWithCriteria<SQLiteWrapper.TLookup>(null, SQLiteWrapper.TLookup::class.java,
                "key=?", arrayOf<String>(key))
        lookup?.delete()
    }
}
