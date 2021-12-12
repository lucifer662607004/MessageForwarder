package com.steven.messageforwarder2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class SmsReceiver extends BroadcastReceiver {
    static String RECEIVER_LOG_TAG = "sms_receiver";

    //The result data
    private String smsSender = "";
    private String smsBody = "";
    private String barkPushAPI = null;
    private String barkPushParam = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences sharedPref = context.getSharedPreferences(
                "bark_push_api", Context.MODE_PRIVATE);
        barkPushAPI = sharedPref.getString("bark_url", null);
        barkPushParam = sharedPref.getString("bark_param", null);
        if (barkPushAPI == null) {
            Log.w(RECEIVER_LOG_TAG, "No bark url found");
            return;
        }

        if (Objects.equals(intent.getAction(), Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender = smsMessage.getDisplayOriginatingAddress();
                smsBody += smsMessage.getMessageBody();
            }

            //Send
            if (smsBody.length() > 1 && smsSender.length() > 1) {
                performSendOnce(context);
            }
        }
        //TODO: Send battery alert
//        else if (Objects.equals(intent.getAction(), Intent.ACTION_BATTERY_LOW)) {
//            performBatteryLowAlert(context);
//        }
    }

    private RequestQueue queue;
    private void performSendOnce(final Context context) {
        //Prepare error handler
        Response.ErrorListener onError = prepareErrorHandler(context);

        String url;
        try {
//            barkPushAPI = "https://api.day.app/xxxxxxx/"
            url = barkPushAPI + URLEncoder.encode(smsSender, "UTF-8") + "/" +
                    URLEncoder.encode(smsBody, "UTF-8");
            if (barkPushParam != null && barkPushParam.length() > 0) {
                url = url + URLEncoder.encode(barkPushParam, "UTF-8");
            }
        } catch (UnsupportedEncodingException ue) {
            return;
        }

        queue = Volley.newRequestQueue(context);

        request(url, onError);

        // TODO: You can send different messages to different phone like:
        /*
        if (smsBody.contains("融e购") || smsBody.contains("平安健康互联网")) {
            //send to another person
        }
        */
    }

    private void request(final String url, Response.ErrorListener onError) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //check result here
                //{"code":200,"msg":"处理成功","data":null}
                if (response.contains("\"code\":200")) {
                    Log.i(RECEIVER_LOG_TAG, "steven sent successfully with url" + url);
                } else {
                    Log.e(RECEIVER_LOG_TAG, "steven failed with:" + response);
                }
            }
        }, onError);
        queue.add(stringRequest);
    }

    private Response.ErrorListener prepareErrorHandler(final Context context) {
        return error -> {
            Toast toast = Toast.makeText(context, "VolleyError: " + error.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        };
    }

}