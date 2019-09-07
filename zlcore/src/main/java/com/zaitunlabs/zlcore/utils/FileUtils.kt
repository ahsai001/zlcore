package com.zaitunlabs.zlcore.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.nio.channels.FileChannel
import java.util.Calendar

import android.content.Context
import android.net.Uri

import com.google.android.gms.common.util.IOUtils

/**
 * this class used for create/put/insert/delete file
 *
 * @author ahmad
 * @version 1.0.0
 */
class FileUtils {
    companion object {

        fun getStreamFromRawFile(context: Context, rawFile: Int): InputStream {
            return context.resources.openRawResource(rawFile)
        }

        @Throws(IOException::class)
        fun getStringFromRawFile(context: Context, rawFile: Int): String {
            val r = BufferedReader(InputStreamReader(getStreamFromRawFile(context, rawFile)))
            val total = StringBuilder()
            var line: String
            while ((line = r.readLine()) != null) {
                total.append(line)
            }
            return total.toString().trim { it <= ' ' }
        }

        fun getBufferedReaderFromRawFile(context: Context, rawFile: Int): BufferedReader {
            return BufferedReader(InputStreamReader(getStreamFromRawFile(context, rawFile)))
        }

        fun getReaderFromRawFile(context: Context, rawFile: Int): Reader {
            return InputStreamReader(getStreamFromRawFile(context, rawFile))
        }

        @Throws(IOException::class)
        fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
            var fromChannel: FileChannel? = null
            var toChannel: FileChannel? = null
            try {
                fromChannel = fromFile.channel
                toChannel = toFile.channel
                fromChannel!!.transferTo(0, fromChannel.size(), toChannel)
            } finally {
                try {
                    fromChannel?.close()
                } finally {
                    toChannel?.close()
                }
            }
        }

        fun convertUriToFile(context: Context, fileUri: Uri, isOngoingInputStream: Boolean): File? {
            var targetFile: File? = null
            try {
                val inputStream = context.contentResolver.openInputStream(fileUri)

                targetFile = File.createTempFile(Calendar.getInstance().time.toString(), null, context.cacheDir)
                val outputStream = FileOutputStream(targetFile)

                val buffer: ByteArray
                if (isOngoingInputStream) {
                    //ongoing stream of data – for example, an HTTP response coming from an ongoing connection
                    buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while ((bytesRead = inputStream!!.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                } else {
                    //known and pre-determined data – such as a file on disk or an in-memory stream
                    buffer = ByteArray(inputStream!!.available())
                    inputStream.read(buffer)
                    outputStream.write(buffer)
                }

                IOUtils.closeQuietly(inputStream)
                IOUtils.closeQuietly(outputStream)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return targetFile
        }
    }
}// TODO Auto-generated constructor stub
