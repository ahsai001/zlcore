package com.zaitunlabs.zlcore.utils

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by ahsai on 5/28/2018.
 */

object SecurityUtils {
    private fun convertedToHex(data: ByteArray): String {
        val buf = StringBuffer()
        for (i in data.indices) {
            var halfOfByte = data[i].ushr(4) and 0x0F
            var twoHalfBytes = 0
            do {
                if (0 <= halfOfByte && halfOfByte <= 9) {
                    buf.append(('0'.toInt() + halfOfByte).toChar())
                } else {
                    buf.append(('a'.toInt() + (halfOfByte - 10)).toChar())
                }

                halfOfByte = data[i] and 0x0F

            } while (twoHalfBytes++ < 1)
        }
        return buf.toString()
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun md5(text: String): String {
        val md: MessageDigest
        md = MessageDigest.getInstance("MD5")
        var md5 = ByteArray(64)
        md.update(text.toByteArray(charset("iso-8859-1")), 0, text.length)
        md5 = md.digest()
        return convertedToHex(md5)
    }
}
