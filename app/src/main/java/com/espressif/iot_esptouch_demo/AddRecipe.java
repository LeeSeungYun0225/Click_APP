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
import android.widget.TextView;
import android.widget.Toast;

public class AddRecipe extends Activity {

    private Spinner m_spin_IF,m_spin_IF_DETAIL,m_spin_hour,m_spin_min,m_spin_then_that;
    private TextView m_Text_Model,m_Text_View;
    private EditText m_Edit_Text;
    private String m_str_IF,m_str_IF_Detail,m_str_hour,m_str_min,m_str_then_that;
    private Context ts;
    private String m_Model_Name;
    private String m_functype;

    private ArrayAdapter m_array_IF,m_array_IF_Detail,m_array_hour,m_array_min,m_array_then_that;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        Intent it = getIntent();

        m_str_IF = "";
        m_str_IF_Detail= "";
        m_str_then_that = "";
        m_str_min = "";
        m_str_hour = "";
        m_Model_Name = it.getStringExtra("Model");


        ts = this;

        m_spin_IF = (Spinner) findViewById(R.id.Select_IF);
        m_spin_IF_DETAIL = (Spinner) findViewById(R.id.Select_IF_Detail_DayorTemp);
        m_spin_hour = (Spinner) findViewById(R.id.Select_hour);
        m_spin_min = (Spinner) findViewById(R.id.Select_min);
        m_spin_then_that = (Spinner) findViewById(R.id.Select_then_that);

        m_Text_Model = (TextView) findViewById(R.id.Text_Model);
        m_Text_Model.setText(m_Model_Name);
        m_Text_View = (TextView) findViewById(R.id.Text_View);
        m_Edit_Text = (EditText) findViewById(R.id.EditText_Value);


        m_array_IF = ArrayAdapter.createFromResource(this,R.array.Recep_type,android.R.layout.simple_spinner_item);
        m_array_IF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        m_spin_IF.setAdapter(m_array_IF);


        m_spin_IF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                m_str_IF = m_spin_IF.getItemAtPosition(i).toString();

                if(m_str_IF.equals("??????"))
                {
                    m_functype = "temp";
                    m_spin_IF.setEnabled(false);
                    m_array_IF_Detail = ArrayAdapter.createFromResource(ts,R.array.Recep_temp,android.R.layout.simple_spinner_item);
                    m_array_IF_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_IF_DETAIL.setAdapter(m_array_IF_Detail);

                    if(m_Model_Name.equals("?????? ?????????") || m_Model_Name.equals("LG ?????????"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_Aircon,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                    else if(m_Model_Name.equals("?????? TV") || m_Model_Name.equals("LG TV"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_TV,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                }
                else if(m_str_IF.equals("??????"))
                {
                    m_functype = "time";
                    m_spin_IF.setEnabled(false);
                    m_array_IF_Detail = ArrayAdapter.createFromResource(ts,R.array.Recep_time_day,android.R.layout.simple_spinner_item);
                    m_array_IF_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_IF_DETAIL.setAdapter(m_array_IF_Detail);


                    m_array_hour = ArrayAdapter.createFromResource(ts,R.array.Recep_time_hour,android.R.layout.simple_spinner_item);
                    m_array_hour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_hour.setAdapter(m_array_hour);
                    m_spin_hour.setVisibility(View.VISIBLE);


                    m_array_min = ArrayAdapter.createFromResource(ts,R.array.Recep_time_min,android.R.layout.simple_spinner_item);
                    m_array_min.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_min.setAdapter(m_array_min);
                    m_spin_min.setVisibility(View.VISIBLE);

                    if(m_Model_Name.equals("?????? ?????????") || m_Model_Name.equals("LG ?????????"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_Aircon,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                    else if(m_Model_Name.equals("?????? TV") || m_Model_Name.equals("LG TV"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_TV,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }

                }
                else if(m_str_IF.equals("??????"))
                {
                    m_functype = "call";
                    m_spin_IF.setEnabled(false);
                    m_array_IF_Detail = ArrayAdapter.createFromResource(ts,R.array.Recep_call,android.R.layout.simple_spinner_item);
                    m_array_IF_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_IF_DETAIL.setAdapter(m_array_IF_Detail);
                    if(m_Model_Name.equals("?????? ?????????") || m_Model_Name.equals("LG ?????????"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_Aircon,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                    else if(m_Model_Name.equals("?????? TV") || m_Model_Name.equals("LG TV"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_TV,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }

                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        m_spin_IF_DETAIL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                m_str_IF_Detail = m_spin_IF_DETAIL.getItemAtPosition(i).toString();

                if(m_str_IF_Detail.equals("?????? ??? ???"))
                {
                    m_functype = "callin";
                }
                else if(m_str_IF_Detail.equals("?????? ??? ???"))
                {
                    m_functype = "callout";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        m_spin_hour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                m_str_hour = m_spin_hour.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        m_spin_min.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                m_str_min = m_spin_min.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        m_spin_then_that.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                m_str_then_that = m_spin_then_that.getItemAtPosition(i).toString();

                if(m_str_then_that.equals("????????????"))
                {
                    m_Text_View.setText("?????? : ");
                    m_Text_View.setVisibility(View.VISIBLE);
                    m_Edit_Text.setHint("??????");
                    m_Edit_Text.setVisibility(View.VISIBLE);
                }
                else if(m_str_then_that.equals("????????????"))
                {
                    m_Text_View.setText("?????? : ");
                    m_Text_View.setVisibility(View.VISIBLE);
                    m_Edit_Text.setHint("??????");
                    m_Edit_Text.setVisibility(View.VISIBLE);
                }
                else
                {
                    m_Text_View.setVisibility(View.GONE);
                    m_Edit_Text.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void RecCancel(View v)
    {

        finish();
    }

    public void RecComplete(View v)
    {
        if(m_str_IF.equals("?????? ????????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ?????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_IF_Detail.equals("?????? ????????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ?????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }  else if(m_str_IF_Detail.equals("????????? ?????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ?????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_IF_Detail.equals("????????? ?????? ????????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ????????? ?????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_hour.equals("?????? ????????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ?????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_min.equals("?????? ?????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ????????? ?????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_then_that.equals("????????? ????????? ?????? ??? ?????????."))
        {
            Toast.makeText(ts,"????????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else if((m_str_then_that.equals("????????????") || m_str_then_that.equals("????????????")) && m_Edit_Text.getText().toString().length() == 0)
        {
                Toast.makeText(ts,"????????? ?????? ?????? ????????? ????????? ?????? ??? ?????????.",Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent sender = new Intent();
            setResult(RESULT_OK,sender);
            if(m_functype.equals("temp")) { // ????????? ????????? ?????????

                m_str_IF_Detail = m_str_IF_Detail.split("??")[0];
                sender.putExtra("temp", m_str_IF_Detail); // temp??? ?????? ?????? ?????????
            }
            else if(m_functype.equals("time")) // ????????? ????????? ?????????
            {
                sender.putExtra("time", m_str_IF_Detail); // time??? ?????? ??????
                sender.putExtra("hour",m_str_hour); // hour??? ?????? ??????
                sender.putExtra("min",m_str_min); // min??? ?????? ???
            }
            else if(m_functype.equals("callin") || m_functype.equals("callout"))
            {
                sender.putExtra("call",m_str_IF_Detail);
            }
            if(m_str_then_that.equals("????????????")) // ??????????????? ?????? ????????? ?????? ????????? ???
            {
                int it = Integer.parseInt(m_Edit_Text.getText().toString());
                sender.putExtra("targetChannel",it); // ????????? ?????? ????????????
            }
            else if(m_str_then_that.equals("????????????"))
            {
                int it = Integer.parseInt(m_Edit_Text.getText().toString());
                sender.putExtra("targetTemp",it);
            }
            if(m_Model_Name.equals("?????? ?????????"))
            {
                m_Model_Name = "SamAC";
            }
            else if(m_Model_Name.equals("?????? TV"))
            {
                m_Model_Name = "SamTV";
            }
            else if(m_Model_Name.equals("LG ?????????"))
            {
                m_Model_Name = "LGAC";
            }
            else if(m_Model_Name.equals("LG TV"))
            {
                m_Model_Name = "LGTV";
            }
            sender.putExtra("Model",m_Model_Name);
            sender.putExtra("then",m_str_then_that);

            sender.putExtra("functype",m_functype);

            finish();
        }


    }
}
