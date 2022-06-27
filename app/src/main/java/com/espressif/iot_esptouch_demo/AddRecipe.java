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

                if(m_str_IF.equals("온도"))
                {
                    m_functype = "temp";
                    m_spin_IF.setEnabled(false);
                    m_array_IF_Detail = ArrayAdapter.createFromResource(ts,R.array.Recep_temp,android.R.layout.simple_spinner_item);
                    m_array_IF_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_IF_DETAIL.setAdapter(m_array_IF_Detail);

                    if(m_Model_Name.equals("삼성 에어컨") || m_Model_Name.equals("LG 에어컨"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_Aircon,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                    else if(m_Model_Name.equals("삼성 TV") || m_Model_Name.equals("LG TV"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_TV,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                }
                else if(m_str_IF.equals("시간"))
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

                    if(m_Model_Name.equals("삼성 에어컨") || m_Model_Name.equals("LG 에어컨"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_Aircon,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                    else if(m_Model_Name.equals("삼성 TV") || m_Model_Name.equals("LG TV"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_TV,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }

                }
                else if(m_str_IF.equals("전화"))
                {
                    m_functype = "call";
                    m_spin_IF.setEnabled(false);
                    m_array_IF_Detail = ArrayAdapter.createFromResource(ts,R.array.Recep_call,android.R.layout.simple_spinner_item);
                    m_array_IF_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    m_spin_IF_DETAIL.setAdapter(m_array_IF_Detail);
                    if(m_Model_Name.equals("삼성 에어컨") || m_Model_Name.equals("LG 에어컨"))
                    {
                        m_array_then_that = ArrayAdapter.createFromResource(ts,R.array.Recep_Aircon,android.R.layout.simple_spinner_item);
                        m_array_then_that.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        m_spin_then_that.setAdapter(m_array_then_that);
                    }
                    else if(m_Model_Name.equals("삼성 TV") || m_Model_Name.equals("LG TV"))
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

                if(m_str_IF_Detail.equals("전화 올 때"))
                {
                    m_functype = "callin";
                }
                else if(m_str_IF_Detail.equals("전화 걸 때"))
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

                if(m_str_then_that.equals("채널변경"))
                {
                    m_Text_View.setText("채널 : ");
                    m_Text_View.setVisibility(View.VISIBLE);
                    m_Edit_Text.setHint("채널");
                    m_Edit_Text.setVisibility(View.VISIBLE);
                }
                else if(m_str_then_that.equals("온도설정"))
                {
                    m_Text_View.setText("온도 : ");
                    m_Text_View.setVisibility(View.VISIBLE);
                    m_Edit_Text.setHint("온도");
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
        if(m_str_IF.equals("실행 조건을 선택 해 주세요."))
        {
            Toast.makeText(ts,"레시피 실행 조건을 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_IF_Detail.equals("실행 온도를 선택 해 주세요."))
        {
            Toast.makeText(ts,"레시피 실행 온도를 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }  else if(m_str_IF_Detail.equals("실행할 날을 선택 해 주세요."))
        {
            Toast.makeText(ts,"레시피 실행 요일을 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_IF_Detail.equals("실행할 전화 상태를 선택 해 주세요."))
        {
            Toast.makeText(ts,"레시피 실행할 전화 상태를 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_hour.equals("실행 시간을 선택 해 주세요."))
        {
            Toast.makeText(ts,"레시피 실행 시간을 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_min.equals("실행 분을 선택 해 주세요."))
        {
            Toast.makeText(ts,"레시피 실행할 분을 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(m_str_then_that.equals("실행할 동작을 선택 해 주세요."))
        {
            Toast.makeText(ts,"실행할 동작을 선택 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if((m_str_then_that.equals("채널변경") || m_str_then_that.equals("온도설정")) && m_Edit_Text.getText().toString().length() == 0)
        {
                Toast.makeText(ts,"변경할 채널 또는 온도를 숫자로 입력 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent sender = new Intent();
            setResult(RESULT_OK,sender);
            if(m_functype.equals("temp")) { // 조건을 온도로 설정시

                m_str_IF_Detail = m_str_IF_Detail.split("º")[0];
                sender.putExtra("temp", m_str_IF_Detail); // temp로 실행 온도 보내기
            }
            else if(m_functype.equals("time")) // 조건을 채널로 설정시
            {
                sender.putExtra("time", m_str_IF_Detail); // time에 실행 요일
                sender.putExtra("hour",m_str_hour); // hour에 실행 시간
                sender.putExtra("min",m_str_min); // min에 실행 분
            }
            else if(m_functype.equals("callin") || m_functype.equals("callout"))
            {
                sender.putExtra("call",m_str_IF_Detail);
            }
            if(m_str_then_that.equals("채널변경")) // 수행하고자 하는 동작이 채널 변경일 때
            {
                int it = Integer.parseInt(m_Edit_Text.getText().toString());
                sender.putExtra("targetChannel",it); // 변경할 채널 넘겨주기
            }
            else if(m_str_then_that.equals("온도설정"))
            {
                int it = Integer.parseInt(m_Edit_Text.getText().toString());
                sender.putExtra("targetTemp",it);
            }
            if(m_Model_Name.equals("삼성 에어컨"))
            {
                m_Model_Name = "SamAC";
            }
            else if(m_Model_Name.equals("삼성 TV"))
            {
                m_Model_Name = "SamTV";
            }
            else if(m_Model_Name.equals("LG 에어컨"))
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
