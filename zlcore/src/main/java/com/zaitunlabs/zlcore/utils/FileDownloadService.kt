package com.zaitunlabs.zlcore.utils

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.os.ResultReceiver
import android.util.Log

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.net.URLConnection
import java.util.ArrayList
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by Vaibhav.Jani on 6/4/15.
 */
class FileDownloadService : IntentService("") {

    override fun onHandleIntent(intent: Intent?) {
        val bundle = intent!!.extras

        if (bundle == null
                || !bundle.containsKey(DOWNLOADER_RECEIVER)
                || !bundle.containsKey(DOWNLOAD_DETAILS)) {

            return
        }

        val resultReceiver = bundle.getParcelable<ResultReceiver>(DOWNLOADER_RECEIVER)
        val downloadDetails = bundle.getParcelable<DownloadRequest>(DOWNLOAD_DETAILS)

        try {
            assert(downloadDetails != null)
            val url = URL(downloadDetails!!.serverFilePath)

            val urlConnection = url.openConnection()
            urlConnection.connect()

            val lengthOfFile = urlConnection.contentLength

            Log.d("FileDownloaderService", "Length of file: $lengthOfFile")
            downloadStarted(resultReceiver)

            val input = BufferedInputStream(url.openStream())
            val localPath = downloadDetails.localFilePath
            val output = FileOutputStream(localPath)

            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int

            while ((count = input.read(data)) != -1) {
                total += count.toLong()
                val progress = (total * 100 / lengthOfFile).toInt()
                sendProgress(progress, resultReceiver)
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            var filenames: ArrayList<String>? = null

            if (downloadDetails.isRequiresUnzip) {
                var unzipDestination = downloadDetails.unzipAtFilePath

                if (unzipDestination == null) {
                    val file = File(localPath)
                    unzipDestination = file.parentFile.absolutePath
                }

                filenames = unzip(localPath, unzipDestination)
            }

            downloadCompleted(resultReceiver, filenames)

            if (downloadDetails.isDeleteZipAfterExtract) {
                val file = File(localPath)
                file.delete()
            }
        } catch (e: Exception) {

            e.printStackTrace()

            downloadFailed(resultReceiver)
        }

    }

    fun sendProgress(progress: Int, receiver: ResultReceiver?) {
        val progressBundle = Bundle()
        progressBundle.putInt(FileDownloadService.DOWNLOAD_PROGRESS, progress)
        receiver!!.send(STATUS_OK, progressBundle)
    }

    fun downloadStarted(resultReceiver: ResultReceiver?) {
        val progressBundle = Bundle()
        progressBundle.putBoolean(FileDownloadService.DOWNLOAD_STARTED, true)
        resultReceiver!!.send(STATUS_OK, progressBundle)
    }

    fun downloadCompleted(resultReceiver: ResultReceiver?, filenames: ArrayList<String>?) {
        val progressBundle = Bundle()
        progressBundle.putBoolean(FileDownloadService.DOWNLOAD_COMPLETED, true)
        progressBundle.putStringArrayList(FileDownloadService.ZIP_FILENAMES, filenames)
        resultReceiver!!.send(STATUS_OK, progressBundle)
    }

    fun downloadFailed(resultReceiver: ResultReceiver?) {
        val progressBundle = Bundle()
        progressBundle.putBoolean(FileDownloadService.DOWNLOAD_FAILED, true)
        resultReceiver!!.send(STATUS_FAILED, progressBundle)
    }

    @Throws(Exception::class)
    private fun unzip(zipFilePath: String?, unzipAtLocation: String): ArrayList<String> {
        val archive = File(zipFilePath)
        val filenames = ArrayList<String>()

        try {
            val zipfile = ZipFile(archive)

            val e = zipfile.entries()
            while (e.hasMoreElements()) {
                val entry = e.nextElement() as ZipEntry
                unzipEntry(zipfile, entry, unzipAtLocation)
                filenames.add(entry.name)
            }

        } catch (e: Exception) {

            Log.e("Unzip zip", "Unzip exception", e)
        }

        return filenames
    }

    @Throws(IOException::class)
    private fun unzipEntry(zipfile: ZipFile, entry: ZipEntry, outputDir: String) {

        if (entry.isDirectory) {
            createDir(File(outputDir, entry.name))
            return
        }

        val outputFile = File(outputDir, entry.name)
        if (!outputFile.parentFile.exists()) {
            createDir(outputFile.parentFile)
        }

        Log.v("ZIP E", "Extracting: $entry")

        val zin = zipfile.getInputStream(entry)
        val inputStream = BufferedInputStream(zin)
        val outputStream = BufferedOutputStream(FileOutputStream(outputFile))

        try {

            //IOUtils.copy(inputStream, outputStream);

            try {

                var c = inputStream.read()
                while (c != -1) {
                    outputStream.write(c)
                    c = inputStream.read()
                }

            } finally {

                outputStream.close()
            }

        } finally {
            outputStream.close()
            inputStream.close()
        }
    }

    private fun createDir(dir: File) {

        if (dir.exists()) {
            return
        }

        Log.v("ZIP E", "Creating dir " + dir.name)

        if (!dir.mkdirs()) {

            throw RuntimeException("Can not create dir $dir")
        }
    }

    class FileDownloader private constructor(handler: Handler) : ResultReceiver(handler) {

        var downloadDetails: DownloadRequest? = null

        var onDownloadStatusListener: OnDownloadStatusListener? = null

        fun download(context: Context) {

            if (isOnline(context)) {

                val intent = Intent(context, FileDownloadService::class.java)
                intent.putExtra(FileDownloadService.DOWNLOADER_RECEIVER, this)
                intent.putExtra(FileDownloadService.DOWNLOAD_DETAILS, downloadDetails)
                context.startService(intent)
            }
        }

        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            super.onReceiveResult(resultCode, resultData)

            if (onDownloadStatusListener == null) {

                return
            }

            if (resultCode == FileDownloadService.STATUS_OK) {

                if (resultData.containsKey(FileDownloadService.DOWNLOAD_STARTED) && resultData.getBoolean(FileDownloadService.DOWNLOAD_STARTED)) {

                    onDownloadStatusListener!!.onDownloadStarted()

                } else if (resultData.containsKey(FileDownloadService.DOWNLOAD_COMPLETED) && resultData.getBoolean(FileDownloadService.DOWNLOAD_COMPLETED)) {
                    val filenames = resultData.getStringArrayList(FileDownloadService.ZIP_FILENAMES)
                    onDownloadStatusListener!!.onDownloadCompleted(filenames)

                } else if (resultData.containsKey(FileDownloadService.DOWNLOAD_PROGRESS)) {

                    val progress = resultData.getInt(FileDownloadService.DOWNLOAD_PROGRESS)
                    onDownloadStatusListener!!.onDownloadProgress(progress)

                }

            } else if (resultCode == FileDownloadService.STATUS_FAILED) {

                onDownloadStatusListener!!.onDownloadFailed()
            }
        }

        companion object {

            fun getInstance(downloadDetails: DownloadRequest, downloadStatusListener: OnDownloadStatusListener): FileDownloader {

                val handler = Handler()

                val fileDownloader = FileDownloader(handler)

                fileDownloader.downloadDetails = downloadDetails

                fileDownloader.onDownloadStatusListener = downloadStatusListener

                return fileDownloader
            }
        }

    }

    interface OnDownloadStatusListener {

        fun onDownloadStarted()

        fun onDownloadCompleted(filenames: List<String>?)

        fun onDownloadFailed()

        fun onDownloadProgress(progress: Int)

    }

    class DownloadRequest : Parcelable {

        var tag: String? = null

        var isRequiresUnzip: Boolean = false

        var serverFilePath: String? = null

        var localFilePath: String? = null

        var unzipAtFilePath: String? = null

        var isDeleteZipAfterExtract = true

        constructor(serverFilePath: String, localPath: String) {
            this.serverFilePath = serverFilePath
            this.localFilePath = localPath
        }

        protected constructor(`in`: Parcel) {
            isRequiresUnzip = `in`.readByte().toInt() != 0x00
            serverFilePath = `in`.readString()
            localFilePath = `in`.readString()
            unzipAtFilePath = `in`.readString()
            isDeleteZipAfterExtract = `in`.readByte().toInt() != 0x00
        }

        override fun describeContents(): Int {

            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {

            dest.writeByte((if (isRequiresUnzip) 0x01 else 0x00).toByte())
            dest.writeString(serverFilePath)
            dest.writeString(localFilePath)
            dest.writeString(unzipAtFilePath)
            dest.writeByte((if (isDeleteZipAfterExtract) 0x01 else 0x00).toByte())
        }

        companion object {

            val creator: Parcelable.Creator<DownloadRequest> = object : Parcelable.Creator<DownloadRequest> {

                override fun createFromParcel(`in`: Parcel): DownloadRequest {

                    return DownloadRequest(`in`)
                }

                override fun newArray(size: Int): Array<DownloadRequest> {

                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private val STATUS_OK = 100
        private val STATUS_FAILED = 200
        private val DOWNLOADER_RECEIVER = "downloader_receiver"
        private val DOWNLOAD_DETAILS = "download_details"
        private val DOWNLOAD_STARTED = "download_started"
        private val DOWNLOAD_FAILED = "download_failed"
        private val DOWNLOAD_COMPLETED = "download_completed"
        private val DOWNLOAD_PROGRESS = "download_progress"
        private val ZIP_FILENAMES = "zip_filenames"

        private fun isOnline(context: Context): Boolean {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val netInfo = cm.activeNetworkInfo

            return if (netInfo != null
                    && netInfo.isConnectedOrConnecting
                    && cm.activeNetworkInfo.isAvailable
                    && cm.activeNetworkInfo.isConnected) {

                true
            } else false

        }
    }
}
