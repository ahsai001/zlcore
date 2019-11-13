package com.zaitunlabs.zlcore.utils;

import com.zaitunlabs.zlcore.tables.InformationModel;
import com.zaitunlabs.zlcore.events.InfoCounterEvent;
import com.zaitunlabs.zlcore.events.InfoPositionEvent;
import com.zaitunlabs.zlcore.events.UpdateInfoListEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ahsai on 6/16/2017.
 */

public class InfoUtil {
    public static long insertNewInfo(String title, String body, String photoUrl, String infoUrl, int type){
        InformationModel newInfo = new InformationModel(title, body, photoUrl, infoUrl, type);

        //save to DB
        newInfo.save();

        //broadcast to any listener
        notifyAddingInfo(newInfo);

        notifyInfoCounter();
        scrollInfoList(newInfo._id);

        return newInfo._id;
    }

    public static void notifyAddingInfo(InformationModel newInfo){
        EventBus.getDefault().post(newInfo);
    }

    public static void notifyInfoCounter(){
        EventBus.getDefault().post(new InfoCounterEvent());
    }

    public static void notifyUpdateInfoList(int position, boolean read){
        EventBus.getDefault().post(new UpdateInfoListEvent(position, read));
    }

    public static void scrollInfoList(long infoId){
        EventBus.getDefault().post(new InfoPositionEvent(infoId));
    }
}
