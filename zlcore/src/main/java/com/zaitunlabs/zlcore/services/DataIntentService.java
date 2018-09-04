package com.zaitunlabs.zlcore.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.zaitunlabs.zlcore.api.APIConstant;
import com.zaitunlabs.zlcore.events.UploadCallbackEvent;
import com.zaitunlabs.zlcore.utils.CommonUtils;
import com.zaitunlabs.zlcore.utils.HttpClientUtils;
import com.zaitunlabs.zlcore.utils.NotificationProgressUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by ahsai on 2/19/2018.
 */

public class DataIntentService extends IntentService {
    private final String TAG = DataIntentService.class.getSimpleName();
    private static final String ACTION_UPLOAD = "com.zaitunlabs.zlcore.services.action.UPLOAD";
    private static final String ACTION_POST = "com.zaitunlabs.zlcore.services.action.POST";
    private static final String ACTION_DOWNLOAD = "com.zaitunlabs.zlcore.services.action.DOWNLOAD";
    private static final String PARAM_URL = "param_url";
    private static final String PARAM_ICON = "param_icon";
    private static final String PARAM_TITLE = "param_title";
    private static final String PARAM_DESC = "param_desc";
    private static final String PARAM_NOTIFID = "param_notifid";
    private static final String PARAM_FILES = "param_files";
    private static final String PARAM_HEADERS = "param_headers";
    private static final String PARAM_BODYS = "param_bodys";
    private static final String PARAM_EXTRAS = "param_extras";
    private static final String PARAM_TAG = "param_tag";

    public DataIntentService() {
        super("DataIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD.equals(action)) {
                handleActionUpload(intent);
            } else if (ACTION_POST.equals(action)) {
                handleActionPost(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleActionUpload(Intent intent) {
        String url = CommonUtils.getStringIntent(intent, PARAM_URL,null);
        int icon = CommonUtils.getIntIntent(intent, PARAM_ICON,1);
        String title = CommonUtils.getStringIntent(intent, PARAM_TITLE,null);
        String desc = CommonUtils.getStringIntent(intent, PARAM_DESC,null);
        int notifID = CommonUtils.getIntIntent(intent, PARAM_NOTIFID,-1);
        FileParts files = (FileParts) CommonUtils.getSerializableIntent(intent, PARAM_FILES,null);
        HeaderParts headers = (HeaderParts) CommonUtils.getSerializableIntent(intent, PARAM_HEADERS,null);
        BodyParts bodys = (BodyParts) CommonUtils.getSerializableIntent(intent, PARAM_BODYS,null);
        final Extras extras = (Extras) CommonUtils.getSerializableIntent(intent, PARAM_EXTRAS,null);
        final String tag = CommonUtils.getStringIntent(intent, PARAM_TAG,null);

        final NotificationProgressUtils progressUtils = new NotificationProgressUtils(this,
                title, desc, icon, notifID);

        ANRequest.MultiPartBuilder builder = AndroidNetworking.upload(url)
                .setOkHttpClient(HttpClientUtils.getHTTPClientForUpload(this, APIConstant.API_VERSION));


        if(headers != null){
            builder.addHeaders(headers.getHeaderList());
        }

        builder.addMultipartFile(files.getFilePartList());

        if(bodys != null){
            builder.addMultipartParameter(bodys.getBodyList());
        }


        builder.setTag(ACTION_UPLOAD+this.toString())
                .setPriority(Priority.HIGH)
                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        progressUtils.setProgress(100, (int)Math.floor(bytesUploaded*100/totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressUtils.setComplete(response.optString("message"));
                        EventBus.getDefault().post(new UploadCallbackEvent(tag, response, null, extras));
                    }
                    @Override
                    public void onError(ANError error) {
                        progressUtils.setComplete(error.getErrorDetail());
                        EventBus.getDefault().post(new UploadCallbackEvent(tag, null, error, extras));
                    }
                });
    }


    private void handleActionPost(Intent intent) {
        String url = CommonUtils.getStringIntent(intent, PARAM_URL,null);
        int icon = CommonUtils.getIntIntent(intent, PARAM_ICON,1);
        String title = CommonUtils.getStringIntent(intent, PARAM_TITLE,null);
        String desc = CommonUtils.getStringIntent(intent, PARAM_DESC,null);
        int notifID = CommonUtils.getIntIntent(intent, PARAM_NOTIFID,-1);
        HeaderParts headers = (HeaderParts) CommonUtils.getSerializableIntent(intent, PARAM_HEADERS,null);
        BodyParts bodys = (BodyParts) CommonUtils.getSerializableIntent(intent, PARAM_BODYS,null);
        final Extras extras = (Extras) CommonUtils.getSerializableIntent(intent, PARAM_EXTRAS,null);
        final String tag = CommonUtils.getStringIntent(intent, PARAM_TAG,null);

        final NotificationProgressUtils progressUtils = new NotificationProgressUtils(this,
                title, desc, icon, notifID);

        ANRequest.PostRequestBuilder builder = AndroidNetworking.post(url)
                .setOkHttpClient(HttpClientUtils.getHTTPClient(this, APIConstant.API_VERSION));

        if(headers != null){
            builder.addHeaders(headers.getHeaderList());
        }

        if(bodys != null){
            builder.addBodyParameter(bodys.getBodyList());
        }


        builder.setTag(ACTION_POST+this.toString())
                .setPriority(Priority.HIGH)
                //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        progressUtils.setProgress(100, (int)Math.floor(bytesDownloaded*100/totalBytes));
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressUtils.setComplete(response.optString("message"));
                        EventBus.getDefault().post(new UploadCallbackEvent(tag, response, null, extras));
                    }
                    @Override
                    public void onError(ANError error) {
                        progressUtils.setComplete(error.getErrorDetail());
                        EventBus.getDefault().post(new UploadCallbackEvent(tag, null, error, extras));
                    }
                });
    }

    public static void startUpload(Context context, String url, int icon, String title, String desc,
                                   int notifID, FileParts files, HeaderParts headers, BodyParts bodys, Extras extras, String tag) {
        Intent intent = new Intent(context, DataIntentService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(PARAM_URL, url);
        intent.putExtra(PARAM_ICON, icon);
        intent.putExtra(PARAM_TITLE, title);
        intent.putExtra(PARAM_DESC, desc);
        intent.putExtra(PARAM_NOTIFID, notifID);
        intent.putExtra(PARAM_FILES, files);
        intent.putExtra(PARAM_HEADERS,headers);
        intent.putExtra(PARAM_BODYS, bodys);
        intent.putExtra(PARAM_EXTRAS, extras);
        intent.putExtra(PARAM_TAG, tag);
        context.startService(intent);
    }


    public static void startPost(Context context, String url, int icon, String title, String desc,
                                 int notifID, HeaderParts headers, BodyParts bodys, Extras extras, String tag) {
        Intent intent = new Intent(context, DataIntentService.class);
        intent.setAction(ACTION_POST);
        intent.putExtra(PARAM_URL, url);
        intent.putExtra(PARAM_ICON, icon);
        intent.putExtra(PARAM_TITLE, title);
        intent.putExtra(PARAM_DESC, desc);
        intent.putExtra(PARAM_NOTIFID, notifID);
        intent.putExtra(PARAM_HEADERS,headers);
        intent.putExtra(PARAM_BODYS, bodys);
        intent.putExtra(PARAM_EXTRAS, extras);
        intent.putExtra(PARAM_TAG, tag);
        context.startService(intent);
    }

    public static class HeaderParts implements Serializable{
        private Map<String, String> headerList;
        public HeaderParts(){
            headerList = new HashMap<>();
        }

        public HeaderParts addItem(String key, String value){
            headerList.put(key,value);
            return this;
        }

        public HeaderParts addItemCollection(Map<String, String> headerList){
            for (Map.Entry<String, String> entry : headerList.entrySet()){
                addItem(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Map<String, String> getHeaderList(){
            return headerList;
        }
    }

    public static class FileParts implements Serializable{
        private Map<String, File> filePartList;
        public FileParts(){
            filePartList = new HashMap<>();
        }

        public FileParts addItem(String key, File value){
            filePartList.put(key,value);
            return this;
        }

        public FileParts addItemCollection(Map<String, File> fileList){
            for (Map.Entry<String, File> entry : fileList.entrySet()){
                addItem(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Map<String, File> getFilePartList(){
            return filePartList;
        }
    }

    public static class BodyParts implements Serializable{
        private Map<String, String> bodyList;
        public BodyParts(){
            bodyList = new HashMap<>();
        }

        public BodyParts addItem(String key, String value){
            bodyList.put(key,value);
            return this;
        }

        public BodyParts addItemCollection(Map<String, String> bodyList){
            for (Map.Entry<String, String> entry : bodyList.entrySet()){
                addItem(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Map<String, String> getBodyList(){
            return bodyList;
        }
    }

    public static class Extras implements Serializable{
        private Map<String, Object> extraList;
        public Extras(){
            extraList = new HashMap<>();
        }

        public Extras addItem(String key, Object value){
            extraList.put(key,value);
            return this;
        }

        public Extras addItemCollection(Map<String, String> extraList){
            for (Map.Entry<String, String> entry : extraList.entrySet()){
                addItem(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Map<String, Object> getExtraList(){
            return extraList;
        }
    }
}
