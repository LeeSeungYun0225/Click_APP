package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.content.Intent;

public class System_Service extends Activity {

    public void onCreate()
    {
        Intent i = new Intent(this,Click_Background_Service.class);
        startService(i);
        finish();
    }

}
