package com.espressif.iot_esptouch_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public  class SystemStart_BroadCast extends BroadcastReceiver {


    private static Intent i;
    public void onReceive(Context context, Intent intent )
    {
        Log.d("LOG::@@","System Started");
        i = new Intent(context,System_Service.class);
        context.startActivity(i);
    }

}

