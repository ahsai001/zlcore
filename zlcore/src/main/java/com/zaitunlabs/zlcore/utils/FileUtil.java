package com.zaitunlabs.zlcore.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.common.util.IOUtils;

/**
 * this class used for create/put/insert/delete file
 * 
 * @author ahmad
 * @version 1.0.0
 */
public class FileUtil {
	public FileUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static InputStream getStreamFromRawFile(Context context, int rawFile){
		InputStream in = context.getResources().openRawResource(rawFile);
		return in;
	}
	
	public static String getStringFromRawFile(Context context, int rawFile) throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(getStreamFromRawFile(context, rawFile)));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		return total.toString().trim();
	}

	public static BufferedReader getBufferedReaderFromRawFile(Context context, int rawFile){
		return new BufferedReader(new InputStreamReader(getStreamFromRawFile(context, rawFile)));
	}

	public static Reader getReaderFromRawFile(Context context, int rawFile){
		return new InputStreamReader(getStreamFromRawFile(context, rawFile));
	}

	public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
		FileChannel fromChannel = null;
		FileChannel toChannel = null;
		try {
			fromChannel = fromFile.getChannel();
			toChannel = toFile.getChannel();
			fromChannel.transferTo(0, fromChannel.size(), toChannel);
		} finally {
			try {
				if (fromChannel != null) {
					fromChannel.close();
				}
			} finally {
				if (toChannel != null) {
					toChannel.close();
				}
			}
		}
	}

	public static File convertUriToFile(Context context, Uri fileUri, boolean isOngoingInputStream){
		File targetFile = null;
		try {
			InputStream inputStream = context.getContentResolver().openInputStream(fileUri);

			targetFile = File.createTempFile(Calendar.getInstance().getTime().toString(), null, context.getCacheDir());
			OutputStream outputStream = new FileOutputStream(targetFile);

			byte[] buffer;
			if(isOngoingInputStream){
				//ongoing stream of data – for example, an HTTP response coming from an ongoing connection
				buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} else {
				//known and pre-determined data – such as a file on disk or an in-memory stream
				buffer = new byte[inputStream.available()];
				inputStream.read(buffer);
				outputStream.write(buffer);
			}

			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(outputStream);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return targetFile;
	}
}
