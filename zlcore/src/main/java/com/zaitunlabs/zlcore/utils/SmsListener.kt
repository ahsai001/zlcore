package com.zaitunlabs.zlcore.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage

/**
 * Created by ahsai on 12/29/2017.
 */

class SmsListener : BroadcastReceiver() {
    private var receivedSMSListener: ReceivedSMSListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED_INTENT) {
            val bundle = intent.extras
            var msgs: Array<SmsMessage>? = null
            var msg_from: String?
            if (bundle != null) {
                try {
                    val pdus = bundle.get("pdus") as Array<Any>
                    msgs = arrayOfNulls(pdus.size)
                    for (i in msgs.indices) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val format = bundle.getString("format")
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                        } else {
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        }

                        msg_from = msgs[i].originatingAddress
                        val msgBody = msgs[i].messageBody

                        if (receivedSMSListener != null)
                            receivedSMSListener!!.onReceived(msg_from, "", msgBody)
                    }
                } catch (e: Exception) {
                    if (receivedSMSListener != null) receivedSMSListener!!.onFailed(e.message)
                }

            }
        }
    }

    interface ReceivedSMSListener {
        fun onReceived(from: String?, to: String, message: String)
        fun onFailed(failedMessage: String)
    }

    fun stopListenForSMS(context: Context) {
        context.unregisterReceiver(this)
    }

    companion object {
        val SMS_RECEIVED_INTENT = "android.provider.Telephony.SMS_RECEIVED"

        fun listenForSMS(context: Context, receivedSMSListener: ReceivedSMSListener): SmsListener {
            val smsBroadcastListener = SmsListener()
            smsBroadcastListener.receivedSMSListener = receivedSMSListener
            context.registerReceiver(smsBroadcastListener, IntentFilter(SMS_RECEIVED_INTENT))
            return smsBroadcastListener
        }
    }
}