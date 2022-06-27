package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Settingtemp extends Activity {

    private EditText m_edit_temp;
    private Intent m_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingtemp);
        m_edit_temp = (EditText) findViewById(R.id.Edit_temp);
    }

    public void close(View v)
    {
        finish();
    }


    public void Setting(View v)
    {
        m_intent = new Intent();
        m_intent.putExtra("temp",m_edit_temp.getText().toString());
        setResult(RESULT_OK,m_intent);
        finish();
    }
}
