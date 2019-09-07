package com.zaitunlabs.zlcore.utils

import com.zaitunlabs.zlcore.models.InformationModel
import com.zaitunlabs.zlcore.events.InfoCounterEvent
import com.zaitunlabs.zlcore.events.InfoPositionEvent
import com.zaitunlabs.zlcore.events.UpdateInfoListEvent

import org.greenrobot.eventbus.EventBus

/**
 * Created by ahsai on 6/16/2017.
 */

object InfoUtils {
    fun insertNewInfo(title: String, body: String, photoUrl: String, infoUrl: String, type: Int): Long {
        val newInfo = InformationModel(title, body, photoUrl, infoUrl, type)

        //save to DB
        newInfo.saveWithTimeStamp()

        //broadcast to any listener
        notifyAddingInfo(newInfo)

        notifyInfoCounter()
        scrollInfoList(newInfo.id!!)

        return newInfo.id!!
    }

    fun notifyAddingInfo(newInfo: InformationModel) {
        EventBus.getDefault().post(newInfo)
    }

    fun notifyInfoCounter() {
        EventBus.getDefault().post(InfoCounterEvent())
    }

    fun notifyUpdateInfoList(position: Int, read: Boolean) {
        EventBus.getDefault().post(UpdateInfoListEvent(position, read))
    }

    fun scrollInfoList(infoId: Long) {
        EventBus.getDefault().post(InfoPositionEvent(infoId))
    }
}
