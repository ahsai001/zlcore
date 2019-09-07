package com.zaitunlabs.zlcore.utils

/*
 * Copyright (C) 2013, Daniel Abraham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

import com.zaitunlabs.zlcore.utils.Base64

import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.HashMap
import java.util.HashSet
import kotlin.collections.Map.Entry

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Wrapper class for Android's [SharedPreferences] interface, which adds a
 * layer of encryption to the persistent storage and retrieval of sensitive
 * key-value pairs of primitive data types.
 *
 *
 * This class provides important - but nevertheless imperfect - protection
 * against simple attacks by casual snoopers. It is crucial to remember that
 * even encrypted data may still be susceptible to attacks, especially on rooted
 * or stolen devices!
 *
 *
 *
 * @see [CodeProject
 * article](http://www.codeproject.com/Articles/549119/Encryption-Wrapper-for-Android-SharedPreferences)
 */
class SPrefs
/**
 * Constructor.
 *
 * @param context
 * the caller's context
 */
(context: Context) : SharedPreferences {
    init {
        // Proxy design pattern
        if (SPrefs.sFile == null) {
            SPrefs.sFile = PreferenceManager
                    .getDefaultSharedPreferences(context)
        }
        // Initialize encryption/decryption key
        try {
            val key = SPrefs.generateAesKeyName(context)
            var value = SPrefs.sFile!!.getString(key, null)
            if (value == null) {
                value = SPrefs.generateAesKeyValue()
                SPrefs.sFile!!.edit().putString(key, value).commit()
            }
            SPrefs.sKey = SPrefs.decode(value)
        } catch (e: Exception) {
            if (isLoggingEnabled) {
                Log.e(TAG, "Error init:" + e.message)
            }
            throw IllegalStateException(e)
        }

    }

    override fun getAll(): Map<String, String> {
        val encryptedMap = SPrefs.sFile!!.all
        val decryptedMap = HashMap<String, String>(
                encryptedMap.size)
        for ((key, value) in encryptedMap) {
            try {
                decryptedMap[SPrefs.decrypt(key)!!] = SPrefs.decrypt(value.toString())!!
            } catch (e: Exception) {
                // Ignore unencrypted key/value pairs
            }

        }
        return decryptedMap
    }

    override fun getString(key: String, defaultValue: String?): String? {
        val encryptedValue = SPrefs.sFile!!.getString(
                SPrefs.encrypt(key), null)
        return if (encryptedValue != null)
            SPrefs
                    .decrypt(encryptedValue)
        else
            defaultValue
    }

    /**
     *
     * Added to get a values as as it can be useful to store values that are
     * already encrypted and encoded
     *
     * @param key
     * @param defaultValue
     * @return Unencrypted value of the key or the defaultValue if
     */
    fun getStringUnencrypted(key: String, defaultValue: String): String {
        val nonEncryptedValue = SPrefs.sFile!!.getString(
                SPrefs.encrypt(key), null)
        return nonEncryptedValue ?: defaultValue
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun getStringSet(key: String, defaultValues: Set<String>?): Set<String>? {
        val encryptedSet = SPrefs.sFile!!.getStringSet(
                SPrefs.encrypt(key), null) ?: return defaultValues
        val decryptedSet = HashSet<String>(
                encryptedSet.size)
        for (encryptedValue in encryptedSet) {
            decryptedSet.add(SPrefs.decrypt(encryptedValue))
        }
        return decryptedSet
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val encryptedValue = SPrefs.sFile!!.getString(
                SPrefs.encrypt(key), null) ?: return defaultValue
        try {
            return Integer.parseInt(SPrefs.decrypt(encryptedValue)!!)
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val encryptedValue = SPrefs.sFile!!.getString(
                SPrefs.encrypt(key), null) ?: return defaultValue
        try {
            return java.lang.Long.parseLong(SPrefs.decrypt(encryptedValue)!!)
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val encryptedValue = SPrefs.sFile!!.getString(
                SPrefs.encrypt(key), null) ?: return defaultValue
        try {
            return java.lang.Float.parseFloat(SPrefs.decrypt(encryptedValue)!!)
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val encryptedValue = SPrefs.sFile!!.getString(
                SPrefs.encrypt(key), null) ?: return defaultValue
        try {
            return java.lang.Boolean.parseBoolean(SPrefs
                    .decrypt(encryptedValue))
        } catch (e: NumberFormatException) {
            throw ClassCastException(e.message)
        }

    }

    override fun contains(key: String): Boolean {
        return SPrefs.sFile!!.contains(SPrefs.encrypt(key))
    }

    override fun edit(): Editor {
        return Editor()
    }

    /**
     * Wrapper for Android's [SharedPreferences.Editor].
     *
     *
     * Used for modifying values in a [SPrefs] object. All
     * changes you make in an editor are batched, and not copied back to the
     * original [SPrefs] until you call [.commit] or
     * [.apply].
     */
    class Editor
    /**
     * Constructor.
     */
    private constructor() : SharedPreferences.Editor {
        private val mEditor: SharedPreferences.Editor

        init {
            mEditor = SPrefs.sFile!!.edit()
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            mEditor.putString(SPrefs.encrypt(key),
                    SPrefs.encrypt(value))
            return this
        }

        /**
         * This is useful for storing values that have be encrypted by something
         * else
         *
         * @param key
         * - encrypted as usual
         * @param value
         * will not be encrypted
         * @return
         */
        fun putStringNoEncrypted(key: String,
                                 value: String): SharedPreferences.Editor {
            mEditor.putString(SPrefs.encrypt(key), value)
            return this
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        override fun putStringSet(key: String,
                                  values: Set<String>?): SharedPreferences.Editor {
            val encryptedValues = HashSet<String>(
                    values!!.size)
            for (value in values) {
                encryptedValues.add(SPrefs.encrypt(value))
            }
            mEditor.putStringSet(SPrefs.encrypt(key),
                    encryptedValues)
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mEditor.putString(SPrefs.encrypt(key),
                    SPrefs.encrypt(Integer.toString(value)))
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mEditor.putString(SPrefs.encrypt(key),
                    SPrefs.encrypt(java.lang.Long.toString(value)))
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mEditor.putString(SPrefs.encrypt(key),
                    SPrefs.encrypt(java.lang.Float.toString(value)))
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mEditor.putString(SPrefs.encrypt(key),
                    SPrefs.encrypt(java.lang.Boolean.toString(value)))
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            mEditor.remove(SPrefs.encrypt(key))
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            mEditor.clear()
            return this
        }

        override fun commit(): Boolean {
            return mEditor.commit()
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        override fun apply() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                mEditor.apply()
            } else {
                commit()
            }
        }
    }

    override fun registerOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        SPrefs.sFile!!
                .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(
            listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        SPrefs.sFile!!
                .unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
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
        private var sFile: SharedPreferences? = null
        private var sKey: ByteArray? = null
        var isLoggingEnabled = false
        private val TAG = SPrefs::class.java!!.getName()
        private fun encode(input: ByteArray): String {
            return Base64.encodeToString(input, Base64.NO_PADDING or Base64.NO_WRAP)
        }

        private fun decode(input: String): ByteArray? {
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
                key = SPrefs.generatePBEKey(password, salt,
                        PRIMARY_PBE_KEY_ALG, ITERATIONS, KEY_SIZE)
            } catch (e: NoSuchAlgorithmException) {
                // older devices may not support the have the implementation try
                // with a weaker
                // algorthm
                key = SPrefs.generatePBEKey(password, salt,
                        BACKUP_PBE_KEY_ALG, ITERATIONS, KEY_SIZE)
            }

            return SPrefs.encode(key.encoded)
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

            return SPrefs.encode(generator.generateKey().encoded)
        }

        private fun encrypt(cleartext: String?): String? {
            if (cleartext == null || cleartext.length == 0) {
                return cleartext
            }
            try {
                val cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER)
                cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(
                        SPrefs.sKey, AES_KEY_ALG))
                return SPrefs.encode(cipher.doFinal(cleartext
                        .toByteArray(charset("UTF-8"))))
            } catch (e: Exception) {
                if (isLoggingEnabled) {
                    Log.w(TAG, "encrypt", e)
                }
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
                        SPrefs.sKey, AES_KEY_ALG))
                return String(cipher.doFinal(SPrefs
                        .decode(ciphertext)), "UTF-8")
            } catch (e: Exception) {
                if (isLoggingEnabled) {
                    Log.w(TAG, "decrypt", e)
                }
                return null
            }

        }
    }
}
