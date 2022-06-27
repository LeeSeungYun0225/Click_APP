package com.espressif.iot_esptouch_demo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.infotool);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                return true;
            }
        }

        return false;
    }

    public boolean tell(View v)
    {
        Intent it = new Intent();
        it.setAction(Intent.ACTION_DIAL);
        it.setData(Uri.parse("tel:010-3925-1978"));
        startActivity(it);
        return true;
    }
}
