package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    private static Intent m_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        m_intent = new Intent();

        startLoading();
    }

    private void startLoading()
    {
        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                    setResult(RESULT_OK, m_intent);
                    finish();
            }
        },3000);// 3000에 딜레이타임 설정
    }
}
