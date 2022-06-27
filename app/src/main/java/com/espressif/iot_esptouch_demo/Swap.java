package com.espressif.iot_esptouch_demo;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.demo_activity.EsptouchDemoActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.String.format;


public class Swap extends AppCompatActivity implements LocationListener {


    private static Context main;
    private static Toolbar m_toolbar;
    private static DrawerLayout m_drawerLayout;
    private static NavigationView m_NavigationView;
    private static ActionBar m_ActionBar;
    private static Intent m_Espintent,m_main_Intent,m_InformationIntent;

    private static View m_Navigation_headerView;
    private static Button m_LoginBtn;
    private TextView m_LoginText;
    private String select = "";

    private MqttAndroidClient m_Mqttclient;
    private String url = "tcp://1.237.145.244:1883";
    private String clientID = UUID.randomUUID().toString();
    private MemoryPersistence mPer = new MemoryPersistence();
    private MqttConnectOptions options = new MqttConnectOptions();


    private boolean m_logincheck;
    private boolean m_isSerial;
    private  userInformation user;
    private Device[] dev = new Device[6];
    private Button[] addbtn = new Button[6];
    private TextView[] TdevName = new TextView[6];
    private RadioGroup[] onoffBtn = new RadioGroup[6];
    private AlertDialog.Builder alert;
    private int NumofDev,m_NumofRecipe;
    private LinearLayout[] linear = new LinearLayout[6];
    private AppliancesDB adb;
    private Spinner[] spinn = new Spinner[6];
    private ArrayAdapter[] array = new ArrayAdapter[2];

     // Location Variables //
    private static LocationManager m_LocationManager;
    private static Location m_Location;
    private final int REQUEST_LOCATION = 1005;
    private int m_Location_Integer;
     ///////////////////////


    //Weather Layout //
    private TextView m_Temp,m_Wind_Speed,m_Humidity,m_Weather_Here,m_TextView_NULL;
    private ImageView m_Weather_Image;
    private boolean m_Weater_check = true;


    // Recipt //
    private LinearLayout[] m_Recipe_layout = new LinearLayout[10];
    private Recipe[] m_Recipe = new Recipe[10];
    private TextView[] m_Recipe_Name = new TextView[10];
    private Button[] m_Recipe_Delete_Btn = new Button[10];
    private Switch[] m_Recipe_Switch = new Switch[10];
    private TextView[] m_Recipe_IF = new TextView[10];
    private TextView[] m_Recipe_Then_What = new TextView[10];
    private AppliancesDB m_rec_db;
    private String idByANDROID_ID;
    ////////////////////////

    // Data Refresh //
    private static Thread m_Data_Refresh_Thread = new Thread();
    private final int DATA_REFRESH_INTERVAL = 10;
    private AppliancesDB m_data_refresh_db;

    @Override
    public void onDestroy()
    {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swap);


        m_logincheck = false;
        m_isSerial = false;
        m_Weater_check = true;

        // Database Linker Init//
        adb = new AppliancesDB();
        m_data_refresh_db = new AppliancesDB();

        /////////////////////////
        user = new userInformation(); // User Information Init //
        main = this;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION); // 위치 권한
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE}, 2);

        idByANDROID_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID); // 안드로이드 기기 고유값 세팅

        //MQTT CONNECTION //
        options.setCleanSession(true);
        options.isAutomaticReconnect();
        m_Mqttclient = new MqttAndroidClient(getApplicationContext(),url,clientID,mPer);
        connect();
        /////////////////////////////

       isFile(); // 로그인 세션 파일 유무 체크

        if(isServiceRunning()) // 서비스 연결 되어있는지 체크
        {
            Log.d("LOG::@@","서비스 이미 실행중");
        }
        else
        {
            m_main_Intent = new Intent(getApplicationContext(),Click_Background_Service.class);
            startService(m_main_Intent);
        }

        // 날씨 정보 xml 연결 //
        m_Temp = findViewById(R.id.Text_Temp);
        m_Wind_Speed = findViewById(R.id.Text_Wind_Speed);
        m_Humidity = findViewById(R.id.Text_Humid);
        m_Weather_Here = findViewById(R.id.Text_Weather_Here);
        m_TextView_NULL = findViewById(R.id.Text_NULL);
        m_Weather_Image = findViewById(R.id.weather_image);




         // 인텐트 초기화 //
        m_main_Intent= new Intent(this,Splash.class); // 스플래시 - 인텐트 커넥트
        m_InformationIntent = new Intent(this,Information.class); // 인포 액티비티 커넥트
        m_Espintent = new Intent(this, EsptouchDemoActivity.class); // esp 액티비티 커넥트
        startActivityForResult(m_main_Intent,8100); // 스플래시 스타트
        /////////////////////


         // Main Screen Connection //
        m_toolbar = (Toolbar) findViewById(R.id.toolbar);
        m_drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        m_NavigationView = (NavigationView) findViewById(R.id.navigation_View);
        setSupportActionBar(m_toolbar);
        m_ActionBar = getSupportActionBar();
        m_ActionBar.setDisplayHomeAsUpEnabled(true);
        m_ActionBar.setDisplayShowCustomEnabled(true);
        m_ActionBar.setHomeAsUpIndicator(R.drawable.menu);
         //////////////////////////


        NumofDev = 0;
        m_NumofRecipe = 0;
        m_Location_Integer = 0;

        m_Navigation_headerView = m_NavigationView.getHeaderView(0);

        m_LoginBtn = (Button) m_Navigation_headerView.findViewById(R.id.loginbtn);
        m_LoginBtn.setOnClickListener(onClicked);
        m_LoginText = (TextView) m_Navigation_headerView.findViewById(R.id.logtxt);


         // 기기 삭제시 경고 메시지 설정 //
        alert = new AlertDialog.Builder(this);
        alert.setTitle("기기 삭제");
        alert.setCancelable(true);
         /////////////////////////////////

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


         // 기기 목록에 대한 XML 연결 //
        addbtn[0] = (Button) findViewById(R.id.addToggle1); // 기기 추가 버튼 0~5 index
        addbtn[1] = (Button) findViewById(R.id.addToggle2);
        addbtn[2] = (Button) findViewById(R.id.addToggle3);
        addbtn[3] = (Button) findViewById(R.id.addToggle4);
        addbtn[4] = (Button) findViewById(R.id.addToggle5);
        addbtn[5] = (Button) findViewById(R.id.addToggle6);

        TdevName[0] = (TextView) findViewById(R.id.appliance1); // 각 기기의 모델명 표기 텍스트뷰 0~5 index
        TdevName[1] = (TextView) findViewById(R.id.appliance2);
        TdevName[2] = (TextView) findViewById(R.id.appliance3);
        TdevName[3] = (TextView) findViewById(R.id.appliance4);
        TdevName[4] = (TextView) findViewById(R.id.appliance5);
        TdevName[5] = (TextView) findViewById(R.id.appliance6);

        onoffBtn[0] = (RadioGroup) findViewById(R.id.Radio1); // 각 기기의 전원 ON/OFF 라디오버튼 0~5 Index
        onoffBtn[1] = (RadioGroup) findViewById(R.id.Radio2);
        onoffBtn[2] = (RadioGroup) findViewById(R.id.Radio3);
        onoffBtn[3] = (RadioGroup) findViewById(R.id.Radio4);
        onoffBtn[4] = (RadioGroup) findViewById(R.id.Radio5);
        onoffBtn[5] = (RadioGroup) findViewById(R.id.Radio6);

        linear[0] = (LinearLayout) findViewById(R.id.devL1); // 각 기기의 정보들을 담아낼 LinearLayout 0~5 Index
        linear[1] = (LinearLayout) findViewById(R.id.devL2);
        linear[2] = (LinearLayout) findViewById(R.id.devL3);
        linear[3] = (LinearLayout) findViewById(R.id.devL4);
        linear[4] = (LinearLayout) findViewById(R.id.devL5);
        linear[5] = (LinearLayout) findViewById(R.id.devL6);

        linear[1].setVisibility(GONE); // 첫번째 기기의 목록만 초기에 비춰주기 위해 1~5 Index  GONE 설정
        linear[2].setVisibility(GONE);
        linear[3].setVisibility(GONE);
        linear[4].setVisibility(GONE);
        linear[5].setVisibility(GONE);


        // 기기의 추가 버튼들 배치를 위한 스피너 & ArrayAdapter  //
        spinn[0] = (Spinner) findViewById(R.id.spinner1);
        spinn[1] = (Spinner) findViewById(R.id.spinner2);
        spinn[2] = (Spinner) findViewById(R.id.spinner3);
        spinn[3] = (Spinner) findViewById(R.id.spinner4);
        spinn[4] = (Spinner) findViewById(R.id.spinner5);
        spinn[5] = (Spinner) findViewById(R.id.spinner6);
        array[0] = ArrayAdapter.createFromResource(this,R.array.TV,android.R.layout.simple_spinner_item);
        array[0].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        array[1] = ArrayAdapter.createFromResource(this,R.array.Aircon,android.R.layout.simple_spinner_item);
        array[1].setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         ////////////////////////////


        // 레시피 레이아웃 매핑 //
        m_Recipe_layout[0] = (LinearLayout) findViewById(R.id.Recp_layout_1);
        m_Recipe_layout[1] = (LinearLayout) findViewById(R.id.Recp_layout_2);
        m_Recipe_layout[2] = (LinearLayout) findViewById(R.id.Recp_layout_3);
        m_Recipe_layout[3] = (LinearLayout) findViewById(R.id.Recp_layout_4);
        m_Recipe_layout[4] = (LinearLayout) findViewById(R.id.Recp_layout_5);
        m_Recipe_layout[5] = (LinearLayout) findViewById(R.id.Recp_layout_6);
        m_Recipe_layout[6] = (LinearLayout) findViewById(R.id.Recp_layout_7);
        m_Recipe_layout[7] = (LinearLayout) findViewById(R.id.Recp_layout_8);
        m_Recipe_layout[8] = (LinearLayout) findViewById(R.id.Recp_layout_9);
        m_Recipe_layout[9] = (LinearLayout) findViewById(R.id.Recp_layout_10);

        // 레시피 대상 기기명 매핑
        m_Recipe_Name[0] = (TextView) findViewById(R.id.Recp_Dev_Name_1);
        m_Recipe_Name[1] = (TextView) findViewById(R.id.Recp_Dev_Name_2);
        m_Recipe_Name[2] = (TextView) findViewById(R.id.Recp_Dev_Name_3);
        m_Recipe_Name[3] = (TextView) findViewById(R.id.Recp_Dev_Name_4);
        m_Recipe_Name[4] = (TextView) findViewById(R.id.Recp_Dev_Name_5);
        m_Recipe_Name[5] = (TextView) findViewById(R.id.Recp_Dev_Name_6);
        m_Recipe_Name[6] = (TextView) findViewById(R.id.Recp_Dev_Name_7);
        m_Recipe_Name[7] = (TextView) findViewById(R.id.Recp_Dev_Name_8);
        m_Recipe_Name[8] = (TextView) findViewById(R.id.Recp_Dev_Name_9);
        m_Recipe_Name[9] = (TextView) findViewById(R.id.Recp_Dev_Name_10);

        //레시피 제거 버튼 매핑 //
        m_Recipe_Delete_Btn[0] = (Button) findViewById(R.id.Recp_DeleteBtn_1);
        m_Recipe_Delete_Btn[1] = (Button) findViewById(R.id.Recp_DeleteBtn_2);
        m_Recipe_Delete_Btn[2] = (Button) findViewById(R.id.Recp_DeleteBtn_3);
        m_Recipe_Delete_Btn[3] = (Button) findViewById(R.id.Recp_DeleteBtn_4);
        m_Recipe_Delete_Btn[4] = (Button) findViewById(R.id.Recp_DeleteBtn_5);
        m_Recipe_Delete_Btn[5] = (Button) findViewById(R.id.Recp_DeleteBtn_6);
        m_Recipe_Delete_Btn[6] = (Button) findViewById(R.id.Recp_DeleteBtn_7);
        m_Recipe_Delete_Btn[7] = (Button) findViewById(R.id.Recp_DeleteBtn_8);
        m_Recipe_Delete_Btn[8] = (Button) findViewById(R.id.Recp_DeleteBtn_9);
        m_Recipe_Delete_Btn[9] = (Button) findViewById(R.id.Recp_DeleteBtn_10);

        // 레시피 실행 조건 텍스트뷰 매핑 //
        m_Recipe_IF[0] = (TextView) findViewById(R.id.Recp_if_text_1);
        m_Recipe_IF[1] = (TextView) findViewById(R.id.Recp_if_text_2);
        m_Recipe_IF[2] = (TextView) findViewById(R.id.Recp_if_text_3);
        m_Recipe_IF[3] = (TextView) findViewById(R.id.Recp_if_text_4);
        m_Recipe_IF[4] = (TextView) findViewById(R.id.Recp_if_text_5);
        m_Recipe_IF[5] = (TextView) findViewById(R.id.Recp_if_text_6);
        m_Recipe_IF[6] = (TextView) findViewById(R.id.Recp_if_text_7);
        m_Recipe_IF[7] = (TextView) findViewById(R.id.Recp_if_text_8);
        m_Recipe_IF[8] = (TextView) findViewById(R.id.Recp_if_text_9);
        m_Recipe_IF[9] = (TextView) findViewById(R.id.Recp_if_text_10);

        // 레시피 실행 동작 텍스트뷰 매핑
        m_Recipe_Then_What[0] = (TextView) findViewById(R.id.Recp_then_text_1);
        m_Recipe_Then_What[1] = (TextView) findViewById(R.id.Recp_then_text_2);
        m_Recipe_Then_What[2] = (TextView) findViewById(R.id.Recp_then_text_3);
        m_Recipe_Then_What[3] = (TextView) findViewById(R.id.Recp_then_text_4);
        m_Recipe_Then_What[4] = (TextView) findViewById(R.id.Recp_then_text_5);
        m_Recipe_Then_What[5] = (TextView) findViewById(R.id.Recp_then_text_6);
        m_Recipe_Then_What[6] = (TextView) findViewById(R.id.Recp_then_text_7);
        m_Recipe_Then_What[7] = (TextView) findViewById(R.id.Recp_then_text_8);
        m_Recipe_Then_What[8] = (TextView) findViewById(R.id.Recp_then_text_9);
        m_Recipe_Then_What[9] = (TextView) findViewById(R.id.Recp_then_text_10);

        // 레시피 스위치 매핑 //
        m_Recipe_Switch[0] = (Switch) findViewById(R.id.Recp_Switch_1);
        m_Recipe_Switch[1] = (Switch) findViewById(R.id.Recp_Switch_2);
        m_Recipe_Switch[2] = (Switch) findViewById(R.id.Recp_Switch_3);
        m_Recipe_Switch[3] = (Switch) findViewById(R.id.Recp_Switch_4);
        m_Recipe_Switch[4] = (Switch) findViewById(R.id.Recp_Switch_5);
        m_Recipe_Switch[5] = (Switch) findViewById(R.id.Recp_Switch_6);
        m_Recipe_Switch[6] = (Switch) findViewById(R.id.Recp_Switch_7);
        m_Recipe_Switch[7] = (Switch) findViewById(R.id.Recp_Switch_8);
        m_Recipe_Switch[8] = (Switch) findViewById(R.id.Recp_Switch_9);
        m_Recipe_Switch[9] = (Switch) findViewById(R.id.Recp_Switch_10);


        // 레시피 초기화 //
        for(int k=0;k<10;k++)
        {
            m_Recipe[k] = new Recipe();
        }

        for(int i = 0; i<6;i++)
        {
            dev[i] = new Device();
            dev[i].initDv();
            onoffBtn[i].setOnCheckedChangeListener(onoffBtnListener);
        }

        m_NavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                m_drawerLayout.closeDrawers();

                int id = item.getItemId();

                switch (id) {
                    case R.id.espConnection:
                        startActivity(m_Espintent);
                        break;
                    case R.id.info:
                        startActivity(m_InformationIntent);
                        break;
                }

                return true;
            }
        });



        spinn[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                select = spinn[0].getItemAtPosition(i).toString();
                MqttMessage m_message = new MqttMessage();
                if(m_isSerial && !select.equals("제어")) {
                    if (select.equals("볼륨▲")) {
                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }

                    } else if (select.equals("볼륨▼")) {
                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▲")) {
                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▼")) {
                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("음소거")) {
                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("리모컨")) {
                        Intent it = new Intent(getApplicationContext(), RemoteControl_Activity.class);
                        it.putExtra("Model",dev[0].getdevModel());
                        it.putExtra("serial",user.getSerial());
                        startActivity(it);
                    } else if (select.equals("레시피")) {
                        if (m_NumofRecipe < 10) {
                            m_main_Intent = new Intent(getApplicationContext(), AddRecipe.class);
                            m_main_Intent.putExtra("Model", dev[0].getdevModel());
                            startActivityForResult(m_main_Intent, 1203);
                        } else {
                            Toast.makeText(getApplicationContext(), "레시피 최대 갯수를 초과했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (select.equals("온도설정")) {
                            m_main_Intent = new Intent(getApplicationContext(), Settingtemp.class);
                            startActivityForResult(m_main_Intent, 1578);
                    }

                    spinn[0].setSelection(0);
                }
                else if(!select.equals("제어"))
                {
                    Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                    spinn[0].setSelection(0);

                }
                }
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
            spinn[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        select = spinn[1].getItemAtPosition(i).toString();
                    MqttMessage m_message = new MqttMessage();
                    if(m_isSerial && !select.equals("제어")) {
                        if (select.equals("볼륨▲")) {
                            if (dev[1].getdevModel().equals("삼성 TV")) {
                                m_message = new MqttMessage("VU".getBytes());
                            } else if (dev[1].getdevModel().equals("LG TV")) {
                                m_message = new MqttMessage("VU".getBytes());
                            }
                            try {
                                m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                            } catch (Exception e) {

                            }

                        } else if (select.equals("볼륨▼")) {
                            if (dev[1].getdevModel().equals("삼성 TV")) {
                                m_message = new MqttMessage("VD".getBytes());
                            } else if (dev[1].getdevModel().equals("LG TV")) {
                                m_message = new MqttMessage("VD".getBytes());
                            }
                            try {
                                m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                            } catch (Exception e) {

                            }
                        } else if (select.equals("채널▲")) {
                            if (dev[1].getdevModel().equals("삼성 TV")) {
                                m_message = new MqttMessage("CU".getBytes());
                            } else if (dev[1].getdevModel().equals("LG TV")) {
                                m_message = new MqttMessage("CU".getBytes());
                            }
                            try {
                                m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                            } catch (Exception e) {

                            }
                        } else if (select.equals("채널▼")) {
                            if (dev[1].getdevModel().equals("삼성 TV")) {
                                m_message = new MqttMessage("CD".getBytes());
                            } else if (dev[1].getdevModel().equals("LG TV")) {
                                m_message = new MqttMessage("CD".getBytes());
                            }
                            try {
                                m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                            } catch (Exception e) {

                            }
                        } else if (select.equals("음소거")) {
                            if (dev[1].getdevModel().equals("삼성 TV")) {
                                m_message = new MqttMessage("M".getBytes());
                            } else if (dev[1].getdevModel().equals("LG TV")) {
                                m_message = new MqttMessage("M".getBytes());
                            }
                            try {
                                m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                            } catch (Exception e) {

                            }
                        } else if (select.equals("리모컨")) {
                            Intent it = new Intent(getApplicationContext(), RemoteControl_Activity.class);
                            it.putExtra("Model",dev[1].getdevModel());
                            it.putExtra("serial",user.getSerial());
                            startActivity(it);
                        } else if (select.equals("레시피")) {
                            if (m_NumofRecipe < 10) {
                                m_main_Intent = new Intent(getApplicationContext(), AddRecipe.class);
                                m_main_Intent.putExtra("Model", dev[1].getdevModel());
                                startActivityForResult(m_main_Intent, 1203);
                            } else {
                                Toast.makeText(getApplicationContext(), "레시피 최대 갯수를 초과했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else if (select.equals("온도설정")) {
                            m_main_Intent = new Intent(getApplicationContext(), Settingtemp.class);
                            startActivityForResult(m_main_Intent, 1578);
                        }

                        spinn[1].setSelection(0);
                    }
                    else if(!select.equals("제어"))
                    {
                        spinn[1].setSelection(0);
                        Toast.makeText(getApplicationContext(),"시리얼을 등록한 사용자만 사용 가능합니다.",Toast.LENGTH_LONG).show();
                    }

            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        spinn[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    select = spinn[2].getItemAtPosition(i).toString();

                MqttMessage m_message = new MqttMessage();
                if(m_isSerial && !select.equals("제어")) {
                    if (select.equals("볼륨▲")) {
                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }

                    } else if (select.equals("볼륨▼")) {
                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▲")) {
                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▼")) {
                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("음소거")) {
                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("리모컨")) {
                        Intent it = new Intent(getApplicationContext(), RemoteControl_Activity.class);
                        it.putExtra("Model",dev[2].getdevModel());
                        it.putExtra("serial",user.getSerial());
                        startActivity(it);
                    } else if (select.equals("레시피")) {
                        if (m_NumofRecipe < 10) {
                            m_main_Intent = new Intent(getApplicationContext(), AddRecipe.class);
                            m_main_Intent.putExtra("Model", dev[2].getdevModel());
                            startActivityForResult(m_main_Intent, 1203);
                        } else {
                            Toast.makeText(getApplicationContext(), "레시피 최대 갯수를 초과했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (select.equals("온도설정")) {
                        m_main_Intent = new Intent(getApplicationContext(), Settingtemp.class);
                        startActivityForResult(m_main_Intent, 1578);
                    }

                    spinn[2].setSelection(0);
                }
                else if(!select.equals("제어"))
                {
                    Toast.makeText(getApplicationContext(),"시리얼을 등록한 사용자만 사용 가능합니다.",Toast.LENGTH_LONG).show();
                    spinn[2].setSelection(0);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        spinn[3].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    select = spinn[3].getItemAtPosition(i).toString();
                MqttMessage m_message = new MqttMessage();
                if(m_isSerial && !select.equals("제어")) {
                    if (select.equals("볼륨▲")) {
                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }

                    } else if (select.equals("볼륨▼")) {
                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▲")) {
                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▼")) {
                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("음소거")) {
                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("리모컨")) {
                        Intent it = new Intent(getApplicationContext(), RemoteControl_Activity.class);
                        it.putExtra("Model",dev[3].getdevModel());
                        it.putExtra("serial",user.getSerial());
                        startActivity(it);
                    } else if (select.equals("레시피")) {
                        if (m_NumofRecipe < 10) {
                            m_main_Intent = new Intent(getApplicationContext(), AddRecipe.class);
                            m_main_Intent.putExtra("Model", dev[3].getdevModel());
                            startActivityForResult(m_main_Intent, 1203);
                        } else {
                            Toast.makeText(getApplicationContext(), "레시피 최대 갯수를 초과했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (select.equals("온도설정")) {
                        m_main_Intent = new Intent(getApplicationContext(), Settingtemp.class);
                        startActivityForResult(m_main_Intent, 1578);
                    }
                    spinn[3].setSelection(0);
                }
                else if(!select.equals("제어"))
                {
                    Toast.makeText(getApplicationContext(),"시리얼을 등록한 사용자만 사용 가능합니다.",Toast.LENGTH_LONG).show();
                    spinn[3].setSelection(0);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        spinn[4].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    select = spinn[4].getItemAtPosition(i).toString();
                MqttMessage m_message = new MqttMessage();
                if(m_isSerial && !select.equals("제어")) {
                    if (select.equals("볼륨▲")) {
                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }

                    } else if (select.equals("볼륨▼")) {
                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▲")) {
                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▼")) {
                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("음소거")) {
                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("리모컨")) {
                        Intent it = new Intent(getApplicationContext(), RemoteControl_Activity.class);
                        it.putExtra("Model",dev[4].getdevModel());
                        it.putExtra("serial",user.getSerial());
                        startActivity(it);
                    } else if (select.equals("레시피")) {
                        if (m_NumofRecipe < 10) {
                            m_main_Intent = new Intent(getApplicationContext(), AddRecipe.class);
                            m_main_Intent.putExtra("Model", dev[4].getdevModel());
                            startActivityForResult(m_main_Intent, 1203);
                        } else {
                            Toast.makeText(getApplicationContext(), "레시피 최대 갯수를 초과했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (select.equals("온도설정")) {
                        m_main_Intent = new Intent(getApplicationContext(), Settingtemp.class);
                        startActivityForResult(m_main_Intent, 1578);
                    }
                    spinn[4].setSelection(0);
                }
                else if(!select.equals("제어"))
                {
                    Toast.makeText(getApplicationContext(),"시리얼을 등록한 사용자만 사용 가능합니다.",Toast.LENGTH_LONG).show();
                    spinn[4].setSelection(0);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        spinn[5].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    select = spinn[5].getItemAtPosition(i).toString();
                MqttMessage m_message = new MqttMessage();
                if(m_isSerial && !select.equals("제어")) {
                    if (select.equals("볼륨▲")) {
                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }

                    } else if (select.equals("볼륨▼")) {
                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("VD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▲")) {
                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CU".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("채널▼")) {
                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("CD".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("음소거")) {
                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            m_message = new MqttMessage("M".getBytes());
                        }
                        try {
                            m_Mqttclient.publish("esp/sendIR/tvNEC/" + user.getSerial(), m_message);
                        } catch (Exception e) {

                        }
                    } else if (select.equals("리모컨")) {
                        Intent it = new Intent(getApplicationContext(), RemoteControl_Activity.class);
                        it.putExtra("Model",dev[5].getdevModel());
                        it.putExtra("serial",user.getSerial());
                        startActivity(it);
                    } else if (select.equals("레시피")) {
                        if (m_NumofRecipe < 10) {
                            m_main_Intent = new Intent(getApplicationContext(), AddRecipe.class);
                            m_main_Intent.putExtra("Model", dev[5].getdevModel());
                            startActivityForResult(m_main_Intent, 1203);
                        } else {
                            Toast.makeText(getApplicationContext(), "레시피 최대 갯수를 초과했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (select.equals("온도설정")) {
                        m_main_Intent = new Intent(getApplicationContext(), Settingtemp.class);
                        startActivityForResult(m_main_Intent, 1578);
                    }
                    spinn[5].setSelection(0);
                }
                else if(!select.equals("제어"))
                {
                    spinn[5].setSelection(0);
                    Toast.makeText(getApplicationContext(),"시리얼을 등록한 사용자만 사용 가능합니다.",Toast.LENGTH_LONG).show();
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });



        m_Recipe_Switch[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[0].m_get_recipeID()+"");
                adb.setIndex(0);
                adb.execute();
            }
        });
        m_Recipe_Switch[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[1].m_get_recipeID()+"");
                adb.setIndex(1);
                adb.execute();
            }
        });

        m_Recipe_Switch[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[2].m_get_recipeID()+"");
                adb.setIndex(2);
                adb.execute();
            }
        });

        m_Recipe_Switch[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[3].m_get_recipeID()+"");
                adb.setIndex(3);
                adb.execute();
            }
        });

        m_Recipe_Switch[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[4].m_get_recipeID()+"");
                adb.setIndex(4);
                adb.execute();
            }
        });

        m_Recipe_Switch[5].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[5].m_get_recipeID()+"");
                adb.setIndex(5);
                adb.execute();
            }
        });

        m_Recipe_Switch[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[6].m_get_recipeID()+"");
                adb.setIndex(6);
                adb.execute();
            }
        });

        m_Recipe_Switch[7].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[7].m_get_recipeID()+"");
                adb.setIndex(7);
                adb.execute();
            }
        });
        m_Recipe_Switch[8].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[8].m_get_recipeID()+"");
                adb.setIndex(8);
                adb.execute();
            }
        });

        m_Recipe_Switch[9].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                adb = new AppliancesDB();
                if(checked)
                {
                    adb.setFunctionType("Recep_ON");
                }
                else
                {
                    adb.setFunctionType("Recep_OFF");
                }
                adb.setRecipe_ID(m_Recipe[9].m_get_recipeID()+"");
                adb.setIndex(9);
                adb.execute();
            }
        });
        m_Data_Refresh_Thread = new Thread(new Runnable()
        {
            public void run()
            {
                while(true) {
                    if (m_logincheck) {
                        try {
                            Thread.sleep(DATA_REFRESH_INTERVAL * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("LOG::@@", "Data_REFRESH");
                        m_data_refresh_db = new AppliancesDB();
                        m_data_refresh_db.setSID(user.getID() + "");
                        m_data_refresh_db.setUID(user.getStrID());
                        m_data_refresh_db.setFunctionType("AUTO_LOGIN");
                        m_data_refresh_db.execute();
                    }
                    try{
                        Thread.sleep(3000);
                        m_Location_Integer++;
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if(m_Location_Integer == 10)
                    {
                        m_Weater_check = true;
                        m_Location_Integer=0;
                    }
                }
            }
        });
        m_Data_Refresh_Thread.start();
    }//end of oncreate

        public void onAddBtnClick1(View v)
        {
            if(m_logincheck) {
                if (!dev[0].getisEmpty()) {
                    DeleteAlertMessage(0);
                }
                else if (NumofDev <6) {
                    m_main_Intent = new Intent(this, AddAppliance.class);
                    startActivityForResult(m_main_Intent, 9101);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
            }
        }
    public void onAddBtnClick2(View v)
    {
        if(m_logincheck) {
            if (!dev[1].getisEmpty()) {
                DeleteAlertMessage(1);
            }
            else if (NumofDev <6) {
                m_main_Intent = new Intent(this, AddAppliance.class);
                startActivityForResult(m_main_Intent, 9101);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
        }

    }
    public void onAddBtnClick3(View v)
    {
        if(m_logincheck) {
            if (!dev[2].getisEmpty()) {
                DeleteAlertMessage(2);
            }
            else if (NumofDev <6) {
                m_main_Intent = new Intent(this, AddAppliance.class);
                startActivityForResult(m_main_Intent, 9101);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
        }
    }
    public void onAddBtnClick4(View v)
    {
        if(m_logincheck) {
            if (!dev[3].getisEmpty()) {
                DeleteAlertMessage(3);
            }
            else if (NumofDev <6) {
                m_main_Intent = new Intent(this, AddAppliance.class);
                startActivityForResult(m_main_Intent, 9101);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
        }
    }
    public void onAddBtnClick5(View v)
    {
        if(m_logincheck) {
            if (!dev[4].getisEmpty()) {
                DeleteAlertMessage(4);
            }
            else if (NumofDev <6) {
                m_main_Intent = new Intent(this, AddAppliance.class);
                startActivityForResult(m_main_Intent, 9101);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
        }
    }
    public void onAddBtnClick6(View v)
    {
        if(m_logincheck) {
            if (!dev[5].getisEmpty()) {
                DeleteAlertMessage(5);
            }
            else if (NumofDev <6) {
                m_main_Intent = new Intent(this, AddAppliance.class);
                startActivityForResult(m_main_Intent, 9101);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
        }
    }



    RadioGroup.OnCheckedChangeListener onoffBtnListener = new RadioGroup.OnCheckedChangeListener() { // 라디오 버튼 리스너
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) { // 전원버튼
            MqttMessage m_Message = new MqttMessage();
            String topic="";
                if (i == R.id.OnSwitch1) {
                    if(m_isSerial) {
                        MqttMessage mm = new MqttMessage("testMessage".getBytes());
                        try
                        {
                            m_Mqttclient.publish("abc",mm);
                        }catch(MqttException e)
                        {
                            e.printStackTrace();
                        }

                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[0].getdevModel().equals("삼성 에어컨")) {
                            m_Message = new MqttMessage("ON".getBytes());
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                            m_Message = new MqttMessage("P".getBytes());
                        } else if (dev[0].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("ON".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[0].clearCheck();
                    }

                } else if (i == R.id.OffSwitch1) {
                    if(m_isSerial) {


                        if (dev[0].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[0].getdevModel().equals("삼성 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        } else if (dev[0].getdevModel().equals("LG TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[0].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[0].clearCheck();
                    }
                }
                if (i == R.id.OnSwitch2) {
                    if(m_isSerial) {

                        if (dev[1].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[1].getdevModel().equals("삼성 에어컨")) {
                            m_Message = new MqttMessage("ON".getBytes());
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                        } else if (dev[1].getdevModel().equals("LG TV")) {
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                            m_Message = new MqttMessage("P".getBytes());
                        } else if (dev[1].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("ON".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[1].clearCheck();
                    }
                } else if (i == R.id.OffSwitch2) {
                    if(m_isSerial) {


                        if (dev[1].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[1].getdevModel().equals("삼성 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        } else if (dev[1].getdevModel().equals("LG TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[1].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[1].clearCheck();
                    }
                }
                if (i == R.id.OnSwitch3) {
                    if(m_isSerial) {

                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[2].getdevModel().equals("삼성 에어컨")) {
                            m_Message = new MqttMessage("ON".getBytes());
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                            m_Message = new MqttMessage("P".getBytes());
                        } else if (dev[2].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("ON".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[2].clearCheck();
                    }
                } else if (i == R.id.OffSwitch3) {
                    if(m_isSerial) {


                        if (dev[2].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[2].getdevModel().equals("삼성 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        } else if (dev[2].getdevModel().equals("LG TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[2].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[2].clearCheck();
                    }
                }
                if (i == R.id.OnSwitch4) {
                    if(m_isSerial) {

                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[3].getdevModel().equals("삼성 에어컨")) {
                            m_Message = new MqttMessage("ON".getBytes());
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                            m_Message = new MqttMessage("P".getBytes());
                        } else if (dev[3].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("ON".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[3].clearCheck();
                    }
                } else if (i == R.id.OffSwitch4) {
                    if(m_isSerial) {


                        if (dev[3].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[3].getdevModel().equals("삼성 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        } else if (dev[3].getdevModel().equals("LG TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[3].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[3].clearCheck();
                    }
                }
                if (i == R.id.OnSwitch5) {
                    if(m_isSerial) {

                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[4].getdevModel().equals("삼성 에어컨")) {
                            m_Message = new MqttMessage("ON".getBytes());
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                            m_Message = new MqttMessage("P".getBytes());
                        } else if (dev[4].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("ON".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[4].clearCheck();
                    }
                } else if (i == R.id.OffSwitch5) {
                    if(m_isSerial) {


                        if (dev[4].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[4].getdevModel().equals("삼성 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        } else if (dev[4].getdevModel().equals("LG TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[4].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[4].clearCheck();
                    }
                }
                if (i == R.id.OnSwitch6) {
                    if(m_isSerial) {

                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[5].getdevModel().equals("삼성 에어컨")) {
                            m_Message = new MqttMessage("ON".getBytes());
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                            m_Message = new MqttMessage("P".getBytes());
                        } else if (dev[5].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("ON".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[5].clearCheck();
                    }
                } else if (i == R.id.OffSwitch6) {
                    if(m_isSerial) {


                        if (dev[5].getdevModel().equals("삼성 TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[5].getdevModel().equals("삼성 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        } else if (dev[5].getdevModel().equals("LG TV")) {
                            m_Message = new MqttMessage("P".getBytes());
                            topic = "esp/sendIR/tvNEC/" + user.getSerial();
                        } else if (dev[5].getdevModel().equals("LG 에어컨")) {
                            topic = "esp/sendIR/LGAC/" + user.getSerial();
                            m_Message = new MqttMessage("OFF".getBytes());
                        }
                        try {
                            m_Mqttclient.publish(topic, m_Message);
                        } catch (Exception e) {
                            Log.d("LOGPUB", "PUBFAIL...");
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "시리얼을 등록한 사용자만 사용 가능합니다.", Toast.LENGTH_LONG).show();
                        onoffBtn[5].clearCheck();
                    }
                }
            }
    };


    public boolean onOptionsItemSelected(MenuItem it) // 툴바 메뉴 오픈 리스너
    {
        int id = it.getItemId();
        Intent itent;

        switch (id) {
            case android.R.id.home: // 네비게이션 드로어 오픈
                {

                    m_drawerLayout.openDrawer(GravityCompat.START);
                     return true;
            }
            case R.id.editpass: // 패스워드 수정
            {
                if(m_logincheck) {

                    itent = new Intent(this, EditPass.class);
                    String uk = Integer.toString(user.getID());
                    itent.putExtra("userKey",uk);
                    startActivity(itent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.editemail: // 이메일 수정
            {
                if(m_logincheck) {
                    itent = new Intent(this, EditEmail.class);
                    String uk = Integer.toString(user.getID());
                    itent.putExtra("userKey",uk);
                    itent.putExtra("email", user.getEmail());
                    startActivityForResult(itent, 9999);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
                }
                    break;
            }
            case R.id.ConnectESP:// 시리얼 검증
            {
                if(m_logincheck) {
                    itent = new Intent(this, ConnectEsp.class);
                    itent.putExtra("serial", user.getSerial());
                    itent.putExtra("userID", user.getStrID());
                    startActivityForResult(itent, 9595);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"로그인 한 사용자만 사용할 수 있습니다.",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(it);
    }

    Button.OnClickListener onClicked = new View.OnClickListener() // 네비게이션 메뉴의 로그인버튼
    {
        public void onClick(View v) {
            if(!m_logincheck)// 로그인 진입
            {
                m_main_Intent = new Intent(getApplicationContext(),Login.class);
                startActivityForResult(m_main_Intent,9997);
            }
            else// 로그아웃버튼
            {

                OnLogout_SendMQTT();
                File file = new File(getFilesDir()+"click_login.txt");
                file.delete();
                m_LoginBtn.setText("로그인");
                m_LoginText.setText("");
                Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
                if(user.getSerial().length()!=0)
                {
                    try{
                        m_Mqttclient.unsubscribe("esp/temperature/"+user.getSerial());
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }

                }

                user = new userInformation();
                m_logincheck = false;

                for(int k=0;k<6;k++)
                {
                    dev[k].initDv();
                    onoffBtn[k].setVisibility(GONE);
                    onoffBtn[k].clearCheck();
                    addbtn[k].setText("+");
                    TdevName[k].setText("디바이스를 추가 해 주세요.");
                    spinn[k].setVisibility(GONE);
                    linear[k].setVisibility(GONE);
                }
                for(int k=0;k<10;k++)
                {
                    m_Recipe_layout[k].setVisibility(GONE);
                    m_NumofRecipe=0;
                }
                linear[0].setVisibility(VISIBLE);
            }
        }
    };

    protected void onActivityResult(int request,int result, Intent data)
    {
        if(request == 9997 && result == RESULT_OK) // 로그인 성공시
        {
            user.userInformation(data.getStringExtra("email"),Integer.parseInt(data.getStringExtra("userid")),data.getStringExtra("strID"));
            user.setSerial(data.getStringExtra("serial"));

            OnLogin_SendMQTT();
            if(user.getSerial().equals("null"))
            {
                user.setSerial("");
                m_isSerial = false;
            }
            else
            {
                m_isSerial = true;
                try{
                    m_Mqttclient.subscribe("esp/temperature/"+user.getSerial(),0);
                }catch(MqttException e)
                {
                    e.printStackTrace();
                }

            }





            m_WriteLoginFile(user.getID()+"",user.getStrID(),user.getSerial());

            m_LoginText.setText(user.getEmail());
            m_LoginBtn.setText("로그아웃");
            m_logincheck = true;

                adb = new AppliancesDB();
                adb.setdata(null);
                adb.setUID(user.getStrID());
                adb.setFunctionType("load"); // 가전기기 로드
                adb.execute();


            adb = new AppliancesDB();
                adb.setUID(user.getStrID());
                adb.setFunctionType("Recipe_load"); // 레시피 로드
                adb.execute();


        }
        else if(request == 9999 && result == RESULT_OK) // 이메일 변경시
        {

            user.setEmail(data.getStringExtra("email"));
            m_LoginText.setText(data.getStringExtra("email"));
            Toast.makeText(getApplicationContext(),"이메일 변경이 완료되었습니다.",Toast.LENGTH_LONG).show();
        }
        else if(request == 9595 && result == RESULT_OK) // 시리얼 검증시
        {
            if((!user.getSerial().equals("") && data.getStringExtra("serial").equals("null")))//연동 해제시
            {
                m_isSerial = false;
                FileDelete();
                m_WriteLoginFile(user.getID()+"",user.getStrID(),"");
                OnSerial_DeleteMqtt();
                user.setSerial("");
            }
            else // 시리얼 검증되면
            {
                m_isSerial = true;
                FileDelete();
                m_WriteLoginFile(user.getID()+"",user.getStrID(),user.getSerial());
                user.setSerial(data.getStringExtra("serial"));
                    try{
                        m_Mqttclient.subscribe("esp/temperature/"+user.getSerial(),0);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }


                OnSerial_VerifyMqtt();
            }


            // 시리얼 정보를 유저정보에 저장
        }
        else if(request == 9101 && result == RESULT_OK) // 기기 1추가
        {
            add_App_Data_Base(NumofDev,data);
        }
        else if(request == 8100 && result == RESULT_OK) // 스플래시 아웃
        {
            if(!m_logincheck) {
                m_main_Intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(m_main_Intent, 9997);
            }
        }
        else if(request == 1203 && result == RESULT_OK) // 레시피 추가
        {
            add_Rec_Data_Base(m_NumofRecipe,data);
        }
        else if (request == 1578 && result == RESULT_OK) { // 온도 설정
           if(m_Mqttclient.isConnected())
           {
               MqttMessage mqtt_message = new MqttMessage(data.getStringExtra("temp").getBytes());
               Log.d("LOG::@@",mqtt_message.toString());
               try{
                   m_Mqttclient.publish("esp/sendIR/LGAC/setTemp/"+user.getSerial(),mqtt_message);
               }catch(MqttException e)
               {
                   e.printStackTrace();
               }
           }
        }
    }


    public void connect() {

        try {
            if (!m_Mqttclient.isConnected()) { // 연결되지 않았을 때
                IMqttToken token = m_Mqttclient.connect(options); // 연결 시도
                token.setActionCallback(new IMqttActionListener() {
                    public void onSuccess(IMqttToken asyncActionToken) { // 연결 성공
                    }

                    @Override

                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        //  연결 실패
                        Log.d("LOG::@@","Swap_Connect_FAIL");
                    }
                });
                m_Mqttclient.setCallback(new MqttCallback() {

                        public void connectComplete(boolean reconnect, String serverURI) {
                        }
                        @Override
                        public void connectionLost(Throwable cause) {
                            Log.d("LOG::@@","Swap_Connect_Lost");
                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            if(topic.equals("esp/temperature/"+user.getSerial()))
                            {
                                Log.d("LOG::@@",message.toString());
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                        }
                    });
            }
        } catch (MqttException e) {
        }
    }

    public class userInformation{


        private String email;
        private int userid;
        private String strID;
        private String serial;


        public void userInformation()
        {
            email = "";
            userid = -1;
            strID = "";
            serial = "";
        }

        public void userInformation(String Remail, int Ruid,String id)
        {
            email = Remail;
            userid = Ruid;
            strID = id;
        }



        public boolean setIntID(String id)
        {
            userid = Integer.parseInt(id);
            return true;
        }
        public boolean setStrID(String str)
        {
            strID = str;
            return true;
        }
        public void setserial(String sr)
        {
            serial = sr;
        }

        public String getEmail()
        {
            return email;
        }

        public String getStrID(){return strID;}

        public int getID()
        {
            return userid;
        }

        public void setEmail(String Remail)
        {
            email = Remail;
        }
        public void setSerial(String sr) { serial = sr;}
        public String getSerial()
        {
            return serial;
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) // 우측상단의 메뉴 인플레이트
    {
        getMenuInflater().inflate(R.menu.connectesp,menu);
        getMenuInflater().inflate(R.menu.info,menu);

        return true;
    }


    public void DeleteAlertMessage(final int devNum)
    {

            if(dev[devNum].getdevName().length()!= 0) {
                alert.setMessage(dev[devNum].getdevName() + " 기기(모델명 : " + dev[devNum].getdevModel() + ")를 삭제 하시겠습니까?");
            }
            else
            {
                alert.setMessage("모델명 : " + dev[devNum].getdevModel() + "를 삭제 하시겠습니까?");
            }
            alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adb = new AppliancesDB();
                    adb.setAppID(dev[devNum].getdevID());
                    adb.setFunctionType("delete");
                    adb.setIndex(devNum);
                    adb.execute();
                }
            })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            alert.show();

        }
    public void Rec_DeleteAlertMessage(final int Rec_NUM)
    {


        alert.setMessage("선택하신 레시피를 정말로 삭제 하시겠습니까?");

        alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                m_rec_db = new AppliancesDB();

                m_rec_db.setIndex(Rec_NUM);
                m_rec_db.setFunctionType("Recipe_delete");
                m_rec_db.setRecipe_ID(m_Recipe[Rec_NUM].m_get_recipeID()+"");
                m_rec_db.execute();

            }
        })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        alert.show();

    }

        public void add_App_Data_Base(int dvcount, Intent data)
        {
            adb = new AppliancesDB();
            adb.setModel(data.getStringExtra("model"),data.getStringExtra("nickname"),data.getStringExtra("comment"),data.getStringExtra("type"),data.getStringExtra("manu"));
            adb.setFunctionType("save");
            adb.setdata(data);
            adb.setIndex(dvcount);
            adb.execute();
        }
        public void add_Rec_Data_Base(int dvcount,Intent data)
        {
            m_rec_db = new AppliancesDB();
            m_rec_db.setdata(data);
            m_rec_db.setIndex(dvcount);

            String complex2;
            complex2 = "";
            if(data.getStringExtra("then").equals("채널변경"))
            {
                complex2 = data.getIntExtra("targetChannel",0)+"";
            } else if (data.getStringExtra("then").equals("온도설정")) {
                complex2 = data.getIntExtra("targetTemp",0)+"";
            }



            if(data.getStringExtra("functype").equals("time"))
            {
                m_rec_db.setRecipe_Info(data.getStringExtra("functype"),data.getStringExtra("then"),data.getStringExtra("Model"),user.getStrID(),data.getStringExtra("hour"),data.getStringExtra("min"),data.getStringExtra("time"),"",complex2);
            }
            else if(data.getStringExtra("functype").equals("callin") || data.getStringExtra("functype").equals("callout"))
            {
                m_rec_db.setRecipe_Info(data.getStringExtra("functype"),data.getStringExtra("then"),data.getStringExtra("Model"),user.getStrID(),"","","","",complex2);
            }
            else
            {
                m_rec_db.setRecipe_Info(data.getStringExtra("functype"),data.getStringExtra("then"),data.getStringExtra("Model"),user.getStrID(),"","","",data.getStringExtra("temp"),complex2);
            }
            m_rec_db.execute();
        }

    public void Delete_Recipe1(View v)
    {
        Rec_DeleteAlertMessage(0);
    }
    public void Delete_Recipe2(View v)
    {
        Rec_DeleteAlertMessage(1);
    }
    public void Delete_Recipe3(View v)
    {
        Rec_DeleteAlertMessage(2);
    }
    public void Delete_Recipe4(View v)
    {
        Rec_DeleteAlertMessage(3);
    }
    public void Delete_Recipe5(View v)
    {
        Rec_DeleteAlertMessage(4);
    }
    public void Delete_Recipe6(View v)
    {
        Rec_DeleteAlertMessage(5);
    }
    public void Delete_Recipe7(View v)
    {
        Rec_DeleteAlertMessage(6);
    }
    public void Delete_Recipe8(View v)
    {
        Rec_DeleteAlertMessage(7);
    }
    public void Delete_Recipe9(View v)
    {
        Rec_DeleteAlertMessage(8);
    }
    public void Delete_Recipe10(View v)
    {
        Rec_DeleteAlertMessage(9);
    }


              public class AppliancesDB extends AsyncTask<String, Void, String> {

                private StringBuffer buff = new StringBuffer();
                private String data = "";
                private String functionType = "";
                private int appid;
                private String amodel,anick,acomment,atype,amanu;
                private int index = 0;
                private Intent dataBox;
                private String UID = "";
                private String SID = "";
                private String if_this,then,app,h,m,d,complex1,complex2;
                private String Rec_ID;
                public void setSID(String id)
                {
                    SID = id;
                }
                public void setRecipe_ID(String id)
                {
                    Rec_ID = id;
                }
                public void setUID(String id)
                {
                    UID = id;
                }
                public void setdata(Intent d)
                {
                    dataBox = d;
                }
                public void setIndex(int idx)
                {
                    index = idx;
                }
                public void setFunctionType(String type)
                {
                    functionType = type;
                }

                public void setAppID(int id)
                {
                    appid = id;
                }

                public void setModel(String model,String nick,String comment,String type,String manu)
                {
                    amodel = model;
                    anick = nick;
                    acomment = comment;
                    atype = type;
                    amanu = manu;
                }

                public void setRecipe_Info(String IF,String THEN,String thisgadget, String Uid,String hour,String min,String day,String ifthis_complex1,String ifthis_complex2)
                {
                    if_this=IF;
                    functionType = "Recipe_save";
                    if(THEN.equals("전원"))
                    {
                        then = "off";
                    }else if(THEN.equals("온도설정"))
                    {
                        then ="setTemp";
                    }
                    else if(THEN.equals("음소거"))
                    {
                        then = "mute";
                    }
                    else if(THEN.equals("채널변경"))
                    {
                        then = "changeChannel";
                    }
                    else if(THEN.equals("전원 켜기"))
                    {
                        then = "on";
                    }
                    else if(THEN.equals("전원 끄기"))
                    {
                        then = "off";
                    }
                    app = thisgadget;
                    UID = Uid;
                    h = hour;
                    m = min;
                    if(day.equals("매일"))
                    {
                        d = "2";
                    }
                    else if(day.equals("주중"))
                    {
                        d = "1";
                    }
                    else if(day.equals("주말"))
                    {
                        d="3";
                    }
                    else
                    {
                        d = "0";
                        m="0";
                        h="0";
                    }
                    complex1 = ifthis_complex1;
                    complex2 = ifthis_complex2;
                    if(complex1.equals(""))
                    {
                        complex1 = "0";
                    }
                    if(complex2.equals(""))
                    {
                        complex2="0";
                    }
                }


                // 케이스 1 ) 가전 저장 할때 :: 한개의 가전만 저장함 functionType = "save"
                // 케이스 2 ) 가전을 삭제할때 :: 한개의 가전만 삭제함 functionType = "delete"
                // 케이스 3 ) 로그인시 가전을 가져올때, :: 모든 가전을 가져옴 functionType = "load"

                protected void onPreExecute() {
                    super.onPreExecute();
                }

                protected void onPostExecute(String result) {

                    String[] str = result.split("/");
                    responseStatus(str,index,dataBox);
                }

                protected String doInBackground(String... unused) {
                    /* 인풋 파라메터값 생성 */

                    String param = "";
                    URL url = null;
                    if(functionType.equals("save")) // 가전 저장
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Add_Appliance.php");
                        }catch(Exception e)
                        {}
                        if(amanu.equals("삼성"))
                        {
                            amanu = "Samsung";
                        }

                        param = "userID=" + user.getStrID() + "&model=" + amodel + "&nickname=" + anick + "&comment=" + acomment + "&type=" + atype + "&manu=" + amanu;
                    }
                    else if(functionType.equals("delete"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Delete_Appliance.php");
                        }catch(Exception e)
                        {}
                        param = "appID=" + appid;
                    }
                    else if(functionType.equals("load"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Load_Appliance.php");
                        }catch(Exception e)
                        {}
                        param = "userID="+UID;
                    }

                    else if (functionType.equals("Recipe_save"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Recipe.php");
                        }catch(Exception e)
                        {}
                        param = "userID="+UID + "&type=" + functionType + "&if_this=" + if_this + "&thenwhat=" + then + "&thisgad=" + app + "&hour=" +h + "&min=" +m + "&day=" +d +"&complex1="+complex1+"&complex2="+complex2;

                    }
                    else if(functionType.equals("Recipe_delete"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Recipe.php");
                        }catch(Exception e)
                        {}
                        param = "recipeID="+Rec_ID + "&type=" + functionType;
                        Log.d("LOG::@@","지울 아이디" + Rec_ID);
                    }
                    else if(functionType.equals("Recipe_load"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Recipe.php");
                        }catch(Exception e)
                        {}
                        param = "userID=" + UID + "&type=" + functionType;
                    }
                    else if(functionType.equals("Recipe_switch"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Recipe.php");
                        }catch(Exception e)
                        {}
                        param = "recipeID="+Rec_ID+ "&type=" + functionType;
                    }
                    else if(functionType.equals("Recep_ON"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Recipe.php");
                        }catch(Exception e)
                        {}
                        param = "recipeID="+Rec_ID+ "&type=" + functionType;
                    }
                    else if(functionType.equals("Recep_OFF"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Recipe.php");
                        }catch(Exception e)
                        {}
                        param = "recipeID="+Rec_ID+ "&type=" + functionType;
                    }
                    else if(functionType.equals("AUTO_LOGIN"))
                    {
                        try {
                            url = new URL(
                                    "http://1.237.145.244/temp/Android_Login_Sess.php");
                        }catch(Exception e)
                        {}
                        param = "numid="+SID+ "&userid=" + UID;
                    }



                    try {
                        /* 서버연결 */

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.connect();
                        /* 안드로이드 -> 서버 파라메터값 전달 */
                        OutputStream outs = conn.getOutputStream();
                        outs.write(param.getBytes("UTF-8"));
                        outs.flush();
                        outs.close();


                        /* 서버 -> 안드로이드 파라메터값 전달 */
                        InputStream is = null;
                        BufferedReader in = null;

                        is = conn.getInputStream();
                        in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                        String line = null;

                        while ( ( line = in.readLine() ) != null )
                        {
                            buff.append(line + "\n");
                        }
                        data = buff.toString().trim();
                        Log.e("LOG::@@",data);

                    } catch (MalformedURLException e) {

                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

            return data;
        }

        public String response()
        {
            return data;
        }


    }


    public void responseStatus(String[] response, int index, Intent data) {

        switch(response[0])
        {
            case "success_save":
            {
                dev[index].setisEmpty(false);
                dev[index].setdevType(data.getStringExtra("type"));
                dev[index].setdevManufact(data.getStringExtra("manu"));
                dev[index].setdevModel(data.getStringExtra("model"));
                dev[index].setdevName(data.getStringExtra("nickname"));
                dev[index].setComment(data.getStringExtra("comment"));
                if(!dev[index].getdevName().equals(""))
                {
                    TdevName[index].setText(dev[index].getdevName());
                }
                else
                {
                    TdevName[index].setText(dev[index].getdevModel());
                }
                if(dev[index].getdevType().equals("TV"))
                {
                    spinn[index].setVisibility(VISIBLE);
                    spinn[index].setAdapter(array[0]);
                }
                else if(dev[index].getdevType().equals("에어컨") || dev[index].getdevType().equals("Aircon"))
                {
                    spinn[index].setVisibility(VISIBLE);
                    spinn[index].setAdapter(array[1]);
                }
                onoffBtn[index].setVisibility(VISIBLE);
                addbtn[index].setText("-");

                NumofDev ++;
                if(NumofDev!=6) {
                    linear[NumofDev].setVisibility(VISIBLE);
                }
                dev[index].setdevID(Integer.parseInt(response[1])); // 테이블 내의 id값 가져오기

                break;
            }
            case "fail_save":
            {
                Toast.makeText(getApplicationContext(),"저장이 실패했습니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "success_delete":
            {
                if(index == 5) {
                    onoffBtn[index].clearCheck();
                    onoffBtn[index].setVisibility(GONE);
                    TdevName[index].setText("디바이스를 추가 해 주세요.");
                    addbtn[index].setText("+");
                    dev[index].initDv();
                    spinn[index].setVisibility(GONE);
                }
                else if(dev[index+1].getisEmpty())
                {
                    onoffBtn[index].clearCheck();
                    onoffBtn[index].setVisibility(GONE);
                    TdevName[index].setText("디바이스를 추가 해 주세요.");
                    addbtn[index].setText("+");
                    dev[index].initDv();
                    linear[index+1].setVisibility(GONE);
                    spinn[index].setVisibility(GONE);
                }
                else if(!dev[index+1].getisEmpty())
                {
                    for(int k=index;k<NumofDev-1;k++)
                    {

                        dev[k] =  dev[k+1];
                        onoffBtn[k].clearCheck();

                        if(dev[k].getdevName().length() !=0)
                        {
                            TdevName[k].setText(dev[k].getdevName());
                        }
                        else
                        {
                            TdevName[k].setText(dev[k].getdevModel());
                        }
                    }

                    if(NumofDev!=6)
                    {
                        linear[NumofDev].setVisibility(GONE);
                    }

                    onoffBtn[NumofDev-1].clearCheck();
                    onoffBtn[NumofDev-1].setVisibility(GONE);
                    TdevName[NumofDev-1].setText("기기를 추가해주세요.");
                    addbtn[NumofDev-1].setText("+");
                    dev[NumofDev-1] = new Device();
                    spinn[NumofDev-1].setVisibility(GONE);


                }

                Toast.makeText(getApplicationContext(),"기기를 삭제했습니다.",Toast.LENGTH_LONG).show();
                NumofDev--;
                break;
            }
            case "fail_delete":
            {
                Toast.makeText(getApplicationContext(),"삭제가 실패했습니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "success_load":
            {

                int row= 0;
                row = Integer.parseInt(response[1]);
                for(int j=0;j<6;j++)
                {
                    linear[j].setVisibility(GONE);

                    dev[j].initDv();
                    onoffBtn[j].setVisibility(GONE);

                    addbtn[j].setText("+");
                    TdevName[j].setText("디바이스를 추가 해 주세요.");
                    spinn[j].setVisibility(GONE);
                }

                Log.d("@@row: ", "" + row);
                for(int k=0;k<row;k++)
                {
                    Log.d("@@k: ", "" + k);
                    dev[k].setdevModel(response[k*6+2]);
                    dev[k].setdevName(response[k*6+3]);
                    dev[k].setComment(response[k*6+4]);
                    dev[k].setdevType(response[k*6+5]);
                    dev[k].setdevManufact(response[k*6+6]);
                    dev[k].setdevID(Integer.parseInt(response[k*6+7]));
                    dev[k].setisEmpty(false);
                    linear[k].setVisibility(VISIBLE);
                    if(dev[k].getdevType().equals("TV") || dev[k].getdevType().equals("TV_IPTV"))
                    {
                        spinn[k].setVisibility(VISIBLE);
                        spinn[k].setAdapter(array[0]);
                    }
                    else if(dev[k].getdevType().equals("에어컨") || dev[k].getdevType().equals("Aircon"))
                    {
                        spinn[k].setVisibility(VISIBLE);
                        spinn[k].setAdapter(array[1]);
                    }
                    onoffBtn[k].setVisibility(VISIBLE);
                    if(dev[k].getdevName().length() != 0) {
                        TdevName[k].setText(dev[k].getdevName());
                    }
                    else
                    {
                        TdevName[k].setText(dev[k].getdevModel());
                    }
                    addbtn[k].setText("-");
                    if(k!=5) {
                        linear[k + 1].setVisibility(VISIBLE);
                    }

                }
                linear[0].setVisibility(VISIBLE);
                Log.d("@@row: ", "" + row);
                NumofDev = row;

                break;
            }
            case "fail_load":
            {
                Log.d("@@@",response[0]);
                Toast.makeText(getApplicationContext(),"가전제품 로드가 실패했습니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "Recipe_save_Success":
            {
                Log.d("LOG::@@","Recipe_Saved");
                m_Recipe[m_NumofRecipe].m_set_recipe_id(Integer.parseInt(response[1]));
                m_Recipe[m_NumofRecipe].m_set_creator(user.getStrID());
                m_Recipe[m_NumofRecipe].m_set_status(true);
                m_Recipe[m_NumofRecipe].m_set_this_gadget(data.getStringExtra("Model"));
                if(data.getStringExtra("Model").equals("SamAC") || data.getStringExtra("Model").equals("삼성 에어컨"))
                {
                    m_Recipe[m_NumofRecipe].m_set_this_gadget("삼성 에어컨");
                }
                else if(data.getStringExtra("Model").equals("SamTV") || data.getStringExtra("Model").equals("삼성 TV"))
                {
                    m_Recipe[m_NumofRecipe].m_set_this_gadget("삼성 TV");
                }
                else if(data.getStringExtra("Model").equals("LGAC") || data.getStringExtra("Model").equals("LG 에어컨"))
                {
                    m_Recipe[m_NumofRecipe].m_set_this_gadget("LG 에어컨");
                }
                else if(data.getStringExtra("Model").equals("LGTV") || data.getStringExtra("Model").equals("LG TV"))
                {
                    m_Recipe[m_NumofRecipe].m_set_this_gadget("LG TV");
                }

                m_Recipe[m_NumofRecipe].m_set_if_this(data.getStringExtra("functype"));
                m_Recipe[m_NumofRecipe].m_set_then_what(data.getStringExtra("then"));
                if(data.getStringExtra("functype").equals("temp"))
                {
                    m_Recipe[m_NumofRecipe].m_set_ifthis_complex(data.getStringExtra("temp"));
                    m_Recipe_IF[m_NumofRecipe].setText("온도가 " + m_Recipe[m_NumofRecipe].m_get_ifthis_complex() + "ºC 일 때");
                    if (m_Recipe[m_NumofRecipe].m_get_then_what().equals("채널변경"))
                    {
                        m_Recipe[m_NumofRecipe].m_set_channel_temp(data.getIntExtra("targetChannel",0));
                        m_Recipe_Then_What[m_NumofRecipe].setText("채널 " + m_Recipe[m_NumofRecipe].m_get_channel_temp() + "으로 채널 변경" ) ;

                    }
                    else if(m_Recipe[m_NumofRecipe].m_get_then_what().equals("온도설정"))
                    {
                        m_Recipe[m_NumofRecipe].m_set_channel_temp(data.getIntExtra("targetTemp",0));
                        m_Recipe_Then_What[m_NumofRecipe].setText("온도 " + m_Recipe[m_NumofRecipe].m_get_channel_temp() + "ºC로 온도 변경");
                    }
                    else
                    {
                        m_Recipe_Then_What[m_NumofRecipe].setText(m_Recipe[m_NumofRecipe].m_get_then_what());
                    }
                    m_Recipe[m_NumofRecipe].m_set_hour(null);
                    m_Recipe[m_NumofRecipe].m_set_day(null);
                    m_Recipe[m_NumofRecipe].m_set_min(null);

                }
                else if(data.getStringExtra("functype").equals("time"))
                {
                    if (m_Recipe[m_NumofRecipe].m_get_then_what().equals("채널변경"))
                    {
                        m_Recipe[m_NumofRecipe].m_set_channel_temp(data.getIntExtra("targetChannel",0));
                        m_Recipe_Then_What[m_NumofRecipe].setText("채널 " + m_Recipe[m_NumofRecipe].m_get_channel_temp() + "으로 채널 변경");
                    }
                    else if(m_Recipe[m_NumofRecipe].m_get_then_what().equals("온도설정"))
                    {
                        m_Recipe[m_NumofRecipe].m_set_channel_temp(data.getIntExtra("targetTemp",0));
                        m_Recipe_Then_What[m_NumofRecipe].setText("온도 " + m_Recipe[m_NumofRecipe].m_get_channel_temp() + "ºC로 온도 변경");
                    }
                    else
                    {
                        m_Recipe_Then_What[m_NumofRecipe].setText(m_Recipe[m_NumofRecipe].m_get_then_what());
                    }
                    m_Recipe[m_NumofRecipe].m_set_ifthis_complex(null);
                    m_Recipe[m_NumofRecipe].m_set_day(data.getStringExtra("time"));
                    m_Recipe[m_NumofRecipe].m_set_hour(data.getStringExtra("hour"));
                    m_Recipe[m_NumofRecipe].m_set_min(data.getStringExtra("min"));
                    m_Recipe_IF[m_NumofRecipe].setText(m_Recipe[m_NumofRecipe].m_get_day()+" " + m_Recipe[m_NumofRecipe].m_get_hour()+ m_Recipe[m_NumofRecipe].m_get_min() + "에");
                }
                else if(data.getStringExtra("functype").equals("callin"))
                {
                    m_Recipe_IF[m_NumofRecipe].setText("내 폰으로 전화 올 때");
                    m_Recipe_Then_What[m_NumofRecipe].setText(m_Recipe[m_NumofRecipe].m_get_then_what());
                }
                else if(data.getStringExtra("functype").equals("callout"))
                {
                    m_Recipe_IF[m_NumofRecipe].setText("내 폰으로 전화 걸 때");
                    m_Recipe_Then_What[m_NumofRecipe].setText(m_Recipe[m_NumofRecipe].m_get_then_what());
                }
                m_Recipe_layout[m_NumofRecipe].setVisibility(VISIBLE);

                m_Recipe_Switch[m_NumofRecipe].setChecked(true);
                m_Recipe[m_NumofRecipe].m_set_status(true);
                m_Recipe_Name[m_NumofRecipe].setText("레시피 : " +  m_Recipe[m_NumofRecipe].m_get_this_gadget());
                m_NumofRecipe++;
                break;
            }
            case "Recipe_delete_Success":
            {
                if(index == 9)
                {
                    m_Recipe[index] = new Recipe();
                    m_Recipe_Switch[index].setChecked(false);
                    m_Recipe_Name[index].setText("");
                    m_Recipe_layout[index].setVisibility(GONE);
                }
                else if(m_NumofRecipe==1)
                {
                    m_Recipe[index] = new Recipe();
                    m_Recipe_Switch[index].setChecked(false);
                    m_Recipe_Name[index].setText("");
                    m_Recipe_layout[index].setVisibility(GONE);
                }
                else
                {
                    for(int w=index;w<m_NumofRecipe-1;w++)
                    {
                        m_Recipe[w].m_set_recipe_id(m_Recipe[w+1].m_get_recipeID());
                        m_Recipe[w].m_set_channel_temp(m_Recipe[w+1].m_get_channel_temp());
                        m_Recipe[w].m_set_then_what(m_Recipe[w+1].m_get_then_what());
                        m_Recipe[w].m_set_day(m_Recipe[w+1].m_get_day());
                        m_Recipe[w].m_set_ifthis_complex(m_Recipe[w+1].m_get_ifthis_complex());
                        m_Recipe[w].m_set_this_gadget(m_Recipe[w+1].m_get_this_gadget());
                        m_Recipe[w].m_set_if_this(m_Recipe[w+1].m_get_if_this());
                        m_Recipe[w].m_set_status(m_Recipe[w+1].m_get_status());
                        m_Recipe[w].m_set_creator(m_Recipe[w+1].m_get_creator());
                        m_Recipe[w].m_set_hour(m_Recipe[w+1].m_get_hour());
                        m_Recipe[w].m_set_min(m_Recipe[w+1].m_get_min());

                        if(m_Recipe[w].m_get_status() == true)
                        {
                            m_Recipe_Switch[w].setChecked(true);
                        }
                        else
                        {
                            m_Recipe_Switch[w].setChecked(false);
                        }
                        m_Recipe_IF[w].setText(m_Recipe_IF[w+1].getText());
                        m_Recipe_Then_What[w].setText(m_Recipe_Then_What[w+1].getText());
                        m_Recipe_Name[w].setText(m_Recipe[w].m_get_this_gadget());
                        m_Recipe_Switch[w].setChecked(m_Recipe_Switch[w+1].isChecked());
                    }
                    m_Recipe[m_NumofRecipe-1] = new Recipe();
                    m_Recipe_layout[m_NumofRecipe-1].setVisibility(GONE);

                }
                m_NumofRecipe--;
                break;
            }
            case "Recipe_load_Success":
            {

                m_NumofRecipe = Integer.parseInt(response[1]);

                for(int k=0;k<m_NumofRecipe;k++)
                {
                    m_Recipe[k].m_set_recipe_id(Integer.parseInt(response[k*10+2]));
                    if(response[k*10+3].equals("1"))
                    {
                        m_Recipe[k].m_set_status(true);
                        m_Recipe_Switch[k].setChecked(true);
                    }
                    else
                    {
                        m_Recipe[k].m_set_status(false);
                        m_Recipe_Switch[k].setChecked(false);
                    }
                    m_Recipe[k].m_set_if_this(response[k*10+4]);
                    m_Recipe[k].m_set_then_what(response[k*10+5]);



                    if(response[k*10+6].equals("SamAC") ||response[k*10+6].equals("삼성 에어컨"))
                    {
                        m_Recipe[k].m_set_this_gadget("삼성 에어컨");
                    }
                    else if(response[k*10+6].equals("SamTV") || response[k*10+6].equals("삼성 TV"))
                    {
                        m_Recipe[k].m_set_this_gadget("삼성 TV");
                    }
                    else if(response[k*10+6].equals("LGAC") || response[k*10+6].equals("LG 에어컨"))
                    {
                        m_Recipe[k].m_set_this_gadget("LG 에어컨");
                    }
                    else if(response[k*10+6].equals("LGTV") || response[k*10+6].equals("LG TV"))
                    {
                        m_Recipe[k].m_set_this_gadget("LG TV");
                    }


                    if(response[k*10+7].equals(null))
                    {
                        m_Recipe[k].m_set_day("0");
                        m_Recipe[k].m_set_hour("0");
                        m_Recipe[k].m_set_min("0");
                    }
                    else
                    {
                        m_Recipe[k].m_set_hour(response[k*10+7]);
                        m_Recipe[k].m_set_min(response[k*10+8]);
                        if(response[k*10+9].equals("1"))
                        {
                            m_Recipe[k].m_set_day("주중");
                        }
                        else if(response[k*10+9].equals("2"))
                        {
                            m_Recipe[k].m_set_day("매일");
                        }
                        else if(response[k*10+9].equals("3"))
                        {
                            m_Recipe[k].m_set_day("주말");
                        }
                    }
                    if(response[k*10+10].equals(null))
                    {
                        m_Recipe[k].m_set_ifthis_complex("0");
                    }
                    else
                    {
                        m_Recipe[k].m_set_ifthis_complex(response[k*10+10]);
                    }
                    if(response[k*10+11].equals(null))
                    {
                        m_Recipe[k].m_set_channel_temp(0);
                    }
                    else
                    {
                        m_Recipe[k].m_set_channel_temp(Integer.parseInt(response[k*10+11]));
                    }


                    m_Recipe_layout[k].setVisibility(VISIBLE);
                    m_Recipe_Name[k].setText("레시피 : " + m_Recipe[k].m_get_this_gadget());
                   if(m_Recipe[k].m_get_if_this().equals("temp"))
                    {
                        m_Recipe_IF[k].setText("온도가 " + m_Recipe[k].m_get_ifthis_complex() + "ºC 일 때");
                    }
                    else if(m_Recipe[k].m_get_if_this().equals("time"))
                    {
                        m_Recipe_IF[k].setText(m_Recipe[k].m_get_day()+" " +  m_Recipe[k].m_get_hour()+"시 "+ m_Recipe[k].m_get_min()+"분" + "에");
                    }
                    else if(m_Recipe[k].m_get_if_this().equals("callin"))
                   {
                       m_Recipe_IF[k].setText("내 폰으로 전화 올 때");
                   }
                   else if(m_Recipe[k].m_get_if_this().equals("callout"))
                   {
                       m_Recipe_IF[k].setText("내 폰으로 전화 걸 때");
                   }


                    if(m_Recipe[k].m_get_then_what().equals("off"))
                    {
                        m_Recipe_Then_What[k].setText("전원");
                    }
                    else if(m_Recipe[k].m_get_then_what().equals("on"))
                    {
                        m_Recipe_Then_What[k].setText("전원 켜기");
                    }
                    else if(m_Recipe[k].m_get_then_what().equals("Mute") || m_Recipe[k].m_get_then_what().equals("mute"))
                    {
                        m_Recipe_Then_What[k].setText("음소거");
                    }
                    if(m_Recipe[k].m_get_then_what().equals("temp") || m_Recipe[k].m_get_then_what().equals("setTemp"))
                    {
                        m_Recipe_Then_What[k].setText("온도 " + m_Recipe[k].m_get_channel_temp() + "ºC로 온도 변경");
                    }
                    else if(m_Recipe[k].m_get_then_what().equals("Channel") || m_Recipe[k].m_get_then_what().equals("changeChannel"))
                    {
                        m_Recipe_Then_What[k].setText("채널 " + m_Recipe[k].m_get_channel_temp() + "으로 채널 변경");
                    }

                }
                for(int k=m_NumofRecipe;k<10;k++)
                {
                    m_Recipe_layout[k].setVisibility(GONE);
                }

                break;
            }
            case "Recipe_on_Success":
            {
                Log.d("LOG::@@","Recipe_ON_SUCCESS");
                break;
            }
            case "Recipe_on_Fail":
            {
                Log.d("LOG::@@","Recipe_ON_FAILED");
                m_Recipe_Switch[index].setChecked(false);
                break;
            }
            case "Recipe_off_Success":
            {
                Log.d("LOG::@@","Recipe_OFF_SUCCESS");
                break;
            }
            case "Recipe_off_Fail":
            {
                Log.d("LOG::@@","Recipe_OFF_FAILED");
                m_Recipe_Switch[index].setChecked(true);
                break;
            }
            case "fail_save_Rec":
            {
                Log.d("LOG::@@","레시피 등록 실패");
                break;
            }
            case "Auto_Not_Found":
            {
                Log.d("LOG::@@","자동 로그인 실패");
                break;
            }
            case "Auto_Login_Success":
            {
                user.setEmail(response[1]);
                m_LoginText.setText(user.getEmail());
                m_LoginBtn.setText("로그아웃");
                m_logincheck = true;
                if(response[2].equals("null"))
                {
                    if(user.getSerial().length()!=0)
                    {
                        if(m_Mqttclient.isConnected())
                        {

                            try{
                                m_Mqttclient.publish("Click_Serial/"+user.getSerial(),new MqttMessage("Serial_Delete".getBytes()));
                            }catch(MqttException e)
                            {
                                e.printStackTrace();
                            }

                        }
                    }
                    m_isSerial = false;
                    user.setSerial("");
                }
                else
                {
                    m_isSerial=true;

                    user.setSerial(response[2]);
                    if(m_Mqttclient.isConnected()) {
                        try {
                            m_Mqttclient.subscribe("esp/temperature/" + user.getSerial(), 0);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                    OnSerial_VerifyMqtt();
                    FileDelete();
                    m_WriteLoginFile(user.getID()+"",user.getStrID(),response[2]);

                }


                adb = new AppliancesDB();
                adb.setUID(user.getStrID());
                adb.setFunctionType("load"); // 가전기기 로드


                adb.execute();


                adb = new AppliancesDB();
                adb.setUID(user.getStrID());
                adb.setFunctionType("Recipe_load"); // 레시피 로드
                adb.execute();




                OnLogin_SendMQTT();

                break;
            }
        }

    }
    public boolean OnLogin_SendMQTT()
    {
        if(m_Mqttclient.isConnected())
        {
            try{
                MqttMessage message;
                if(user.getSerial().equals(""))
                {
                    message = new MqttMessage((user.getID()+"/"+user.getStrID()+"/"+"null").getBytes());
                }
                else
                {
                    message= new MqttMessage((user.getID()+"/"+user.getStrID()+"/"+user.getSerial()).getBytes());
                }
                m_Mqttclient.publish(idByANDROID_ID,message);

                return true;
            }catch(MqttException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean OnLogout_SendMQTT() {
        if (m_Mqttclient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(("Logout").getBytes());
                m_Mqttclient.publish(user.getID() + user.getStrID(), message);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }
       }
       return false;
    }

    public boolean OnSerial_VerifyMqtt()
    {
        if(m_Mqttclient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(user.getSerial().getBytes());
                m_Mqttclient.publish(user.getID() + user.getStrID(), message);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean OnSerial_DeleteMqtt()
    {
        if(m_Mqttclient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage("Serial_Delete".getBytes());
                m_Mqttclient.publish("Click_Serial/" + user.getSerial(), message);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }



    // Location 함수 //
    @Override public void onLocationChanged(Location location) {
        if(m_Weater_check) {
            m_Location = location;
            m_Location.setLongitude(Double.parseDouble(format("%.4f", m_Location.getLongitude())));
            m_Location.setLatitude(Double.parseDouble(format("%.4f", m_Location.getLatitude())));
            Log.d("LOC::", "" + m_Location.getLatitude() + m_Location.getLongitude());

            getWeather();
        }
    }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onProviderDisabled(String provider) { }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("gps", "Location permission granted");
                    try {
                        m_LocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        m_LocationManager.requestLocationUpdates("gps", 0, 0, this);
                    } catch (SecurityException ex) {
                        Log.d("gps", "Location permission did not work!");
                    }
                }
                break;
        }
    }
    // Location 함수 //


    private boolean getWeather()
    {
        String in_weather_URL = "http://api.openweathermap.org/data/2.5/weather?lat=" + m_Location.getLatitude() + "&lon="+m_Location.getLongitude()+ "&units=metric&appid=3ebf6848a54542676899e033b9f1c989";

        ReceiveWeather receive_Weather = new ReceiveWeather();
        receive_Weather.setURL(in_weather_URL);
        receive_Weather.execute();

        return true;

    }


    public class ReceiveWeather extends AsyncTask<String,Void,JSONObject> {

        private String m_URL;

        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL(m_URL).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    InputStream input_stream = conn.getInputStream();
                    InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
                    BufferedReader buffer_reader = new BufferedReader(input_stream_reader);

                    String s_read;
                    while((s_read = buffer_reader.readLine()) != null)
                    {
                        JSONObject json_obj = new JSONObject(s_read);
                        return json_obj;
                    }
                }
                else
                {
                    return null;
                }

                Log.d("Log::@@","Weather Data Connection Successed");
            }catch(Exception e)
            {
                Log.d("Log::@@","Weather Data Connection Failed");
            }

            return null;
        }

        protected void onPostExecute(JSONObject result)
        {
            if(result != null)
            {
                try {

                    m_TextView_NULL.setVisibility(GONE);
                    m_Humidity.setText("습도 : " +Double.parseDouble(result.getJSONObject("main").getString("humidity")));
                    m_Wind_Speed.setText("풍속 : " + Double.parseDouble(result.getJSONObject("wind").getString("speed")) + "m/s");
                    m_Temp.setText("기온 : " + Double.parseDouble(result.getJSONObject("main").getString("temp")) + "℃");
                    m_Weather_Here.setText("현재 위치 날씨");

                    switch(result.getJSONArray("weather").getJSONObject(0).getString("main"))
                    {
                        case "Clear":
                        {
                            m_Weather_Image.setImageResource(R.drawable.day);
                            break;
                        }
                        case "Clouds":
                        {
                            m_Weather_Image.setImageResource(R.drawable.cloud);
                            break;
                        }
                        case "Atmosphere":
                        {
                            m_Weather_Image.setImageResource(R.drawable.cloud);
                            break;
                        }
                        case "Snow":
                        {
                            m_Weather_Image.setImageResource(R.drawable.snow);
                            break;
                        }
                        case "Rain":
                        {
                            m_Weather_Image.setImageResource(R.drawable.rain);
                            break;
                        }
                        case "Drizzle":
                        {
                            m_Weather_Image.setImageResource(R.drawable.rain);
                            break;
                        }
                        case "Thunderstorm":
                        {
                            m_Weather_Image.setImageResource(R.drawable.cloud);
                            break;
                        }
                    }

                }catch(Exception e)
                {

                    Log.d("Log::@@","Weather Parsing Failed");
                }

            }
        }
        public boolean setURL(String url)
        {
            try {
                m_URL = url;
                Log.d("Log::@@","URL Setting Successed");
                return true;
            }catch(Exception e)
            {
                Log.d("Log::@@","URL Setting Failed");
                return false;
            }
        }
    }
    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) main.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (Click_Background_Service.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }



    public boolean m_WriteLoginFile(String userNum,String id,String serial)
    {
        try{
            FileWriter fileWriter = new FileWriter(getFilesDir()+"click_login.txt");
            BufferedWriter buWriter = new BufferedWriter(fileWriter);

            buWriter.write(userNum);
            buWriter.write('/');
            buWriter.write(id);
            buWriter.write('/');
            if(serial.length()==0)
            {
                buWriter.write("null");
            }
            else
            {
                buWriter.write(serial);
            }
            Log.d("LOG::@@","File Writing Success");
            buWriter.close();
            fileWriter.close();
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public String m_ReadLoginFile()
    {
        String str = "";
        try{
            FileReader fileReader = new FileReader(getFilesDir()+"click_login.txt");
            BufferedReader buReader = new BufferedReader(fileReader);

            str = buReader.readLine();

            buReader.close();
            fileReader.close();
            return str;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isFile()
    {
        String filecheck = getFilesDir()+"click_login.txt";
        File file = new File(filecheck);

        if(file.exists())
        {
            user = new userInformation();
            String[] read = m_ReadLoginFile().split("/");
            user.setIntID(read[0]);
            user.setStrID(read[1]);
            if(read[2].equals("null"))
            {
                user.setSerial("");
            }
            else
            {
                user.setSerial(read[2]);
                m_isSerial= true;
            }

            adb.setSID(read[0]);
            adb.setUID(read[1]);
            adb.setFunctionType("AUTO_LOGIN");
            adb.execute();
            Log.d("LOG::@@","file Read_Success");

        }
        return file.exists();
    }

    public boolean FileDelete()
    {
        String filecheck = getFilesDir()+"click_login.txt";
        File file = new File(filecheck);
        file.delete();
        return true;
    }



}






