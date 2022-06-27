package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddAppliance extends Activity {

    private Spinner type,manu;
    private ArrayAdapter Amanu,Atype;
    private String str_manu,str_type,str_model;
    private Context thisct;
    private Intent it;
    private EditText nick,comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appliance);

        it = new Intent();
        thisct = this;

        manu = (Spinner) findViewById(R.id.manufact);
        type = (Spinner) findViewById(R.id.appliType);


        Amanu = ArrayAdapter.createFromResource(this,R.array.manufact,android.R.layout.simple_spinner_item);
        Amanu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        nick = (EditText) findViewById(R.id.nickname);
        comment= (EditText) findViewById(R.id.comment);


        manu.setAdapter(Amanu);



        manu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_manu = manu.getItemAtPosition(i).toString();

                if(str_manu.equals("삼성"))
                {
                    Atype = ArrayAdapter.createFromResource(thisct,R.array.type_Samsung,android.R.layout.simple_spinner_item);
                    Atype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    type.setAdapter(Atype);
                    manu.setEnabled(false);
                }
                else if(str_manu.equals("LG"))
                {
                    Atype = ArrayAdapter.createFromResource(thisct,R.array.type_LG,android.R.layout.simple_spinner_item);
                    Atype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    type.setAdapter(Atype);
                    manu.setEnabled(false);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_type = type.getItemAtPosition(i).toString();
                if(str_manu.equals("삼성")) {
                    if (str_type.equals("에어컨")) {
                        str_model = "삼성 에어컨";
                    }
                    else if (str_type.equals("TV")){
                        str_model = "삼성 TV";
                    }
                }
                else if(str_manu.equals("LG"))
                {
                    if(str_type.equals("에어컨"))
                    {
                        str_model = "LG 에어컨";
                    }
                    else  if(str_type.equals("TV"))
                    {
                        str_model = "LG TV";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void Complete(View v)
    {
        if(str_manu.equals("제조사를 선택 해 주세요.")) {
            Toast.makeText(getApplicationContext(),"제조사를 먼저 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else
        {
            if(str_type.equals("타입을 선택 해 주세요."))
            {
                Toast.makeText(getApplicationContext(),"타입을 먼저 선택 해 주세요.",Toast.LENGTH_LONG).show();
            }
            else
                {

                it.putExtra("manu", str_manu);
                it.putExtra("type", str_type);
                it.putExtra("model",str_model);
                it.putExtra("nickname", nick.getText().toString());
                it.putExtra("comment", comment.getText().toString());
                setResult(RESULT_OK, it);
                finish();
            }
        }
    }
    public void Cancelation(View v)
    {
        finish();
    }






}
