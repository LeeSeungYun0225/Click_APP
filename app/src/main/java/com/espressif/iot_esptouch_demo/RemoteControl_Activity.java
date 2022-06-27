package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class RemoteControl_Activity extends Activity  {

    private MqttAndroidClient mqtt_client;
    private MqttConnectOptions options;
    private String url = "tcp://1.237.145.244:1883";
    private String clientID = UUID.randomUUID().toString();
    private MemoryPersistence mPer = new MemoryPersistence();
    private String ModelName = "";
    private String serial = "";
    private TextView m_Model_Text;
    private ImageButton m_Image_onoff,m_Image_Mute;
    private ImageButton m_Image_zero, m_Image_one, m_Image_two, m_Image_three, m_Image_four, m_Image_five, m_Image_six, m_Image_seven, m_Image_eight, m_Image_nine;
    private ImageButton  m_Image_Volup, m_Image_Voldown, m_Image_ChanUp, m_Image_ChanDown;
    private Thread Thread_Mqtt_Connection_Check;
    private MqttMessage m_MqttMessage;
    private String topic = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_remote_control_);

        Thread_Mqtt_Connection_Check = new Thread(new Runnable()
        {
            public void run()
            {
                while(!mqtt_client.isConnected())
                {
                    try{
                        Thread.sleep(1000);
                    }catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                try{
                    mqtt_client.subscribe("Click_Serial/"+serial,0);
                }catch(MqttException e)
                {
                    e.printStackTrace();
                }
                return;
            }
        });

        options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.isAutomaticReconnect();
        mqtt_client = new MqttAndroidClient(getApplicationContext(),url,clientID,mPer);

        connect();
        Thread_Mqtt_Connection_Check.start();


        Intent it = getIntent();
        ModelName = it.getStringExtra("Model");
        serial = it.getStringExtra("serial");
        topic = "esp/sendIR/tvNEC/" + serial;

        m_Model_Text = (TextView) findViewById(R.id.Text_ModelName);
        m_Model_Text.setText(ModelName);

        m_Image_Mute = (ImageButton) findViewById(R.id.Image_mute);
        m_Image_onoff = (ImageButton) findViewById(R.id.Image_OnOff);
        m_Image_zero = (ImageButton)findViewById(R.id.Image_Zero);
        m_Image_one = (ImageButton)findViewById(R.id.Image_One);
        m_Image_two = (ImageButton)findViewById(R.id.Image_Two);
        m_Image_three = (ImageButton)findViewById(R.id.Image_Three);
        m_Image_four = (ImageButton)findViewById(R.id.Image_Four);
        m_Image_five = (ImageButton)findViewById(R.id.Image_Five);
        m_Image_six = (ImageButton)findViewById(R.id.Image_Six);
        m_Image_seven = (ImageButton)findViewById(R.id.Image_Seven);
        m_Image_eight = (ImageButton) findViewById(R.id.Image_Eight);
        m_Image_nine = (ImageButton) findViewById(R.id.Image_Nine);
        m_Image_Volup = (ImageButton)findViewById(R.id.Image_VolUp);
        m_Image_Voldown = (ImageButton)findViewById(R.id.Image_VolDown);
        m_Image_ChanUp = (ImageButton) findViewById(R.id.Image_ChanUp);
        m_Image_ChanDown = (ImageButton) findViewById(R.id.Image_ChanDown);



        m_Image_Mute.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("M".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }

                    Log.d("LOG::@@","음소거버튼");
                }
            }
        });

        m_Image_onoff.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("P".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","전원버튼");
                }
            }
        });
        m_Image_zero.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("0".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","0");
                }
            }
        });
        m_Image_one.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("1".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","1");

                }
            }
        });
        m_Image_two.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("2".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","2");
                }
            }
        });
        m_Image_three.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("3".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","3");
                }
            }
        });
        m_Image_four.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("4".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","4");
                }
            }
        });
        m_Image_five.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("5".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","5");
                }
            }
        });
        m_Image_six.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("6".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","6");
                }
            }
        });
        m_Image_seven.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("7".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","7");
                }
            }
        });
        m_Image_eight.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("8".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","8");
                }
            }
        });
        m_Image_nine.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("9".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","9");
                }
            }
        });
        m_Image_Volup.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("VU".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","Vol_Up");
                }
            }
        });
        m_Image_Voldown.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("VD".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","Vol_Down");
                }
            }
        });
        m_Image_ChanUp.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("CU".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","Chan_Up");
                }
            }
        });
        m_Image_ChanDown.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mqtt_client.isConnected())
                {
                    m_MqttMessage = new MqttMessage("CD".getBytes());
                    try{
                        mqtt_client.publish(topic,m_MqttMessage);
                    }catch(MqttException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("LOG::@@","Chan_Down");
                }
            }
        });

    }

    public void finish(View v)
    {
        try{
            mqtt_client.disconnect();
        }catch(MqttException e)
        {
            e.printStackTrace();
        }
        finish();
    }
    public void connect() {

        try {
            if (!mqtt_client.isConnected()) { // 연결되지 않았을 때
                IMqttToken token = mqtt_client.connect(options); // 연결 시도
                token.setActionCallback(new IMqttActionListener() {
                    public void onSuccess(IMqttToken asyncActionToken) { // 연결 성공
                        Log.d("MqttConnection:","SUCCESS");
                    }

                    @Override

                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("LOG::@@","REmote_Connect_Fail");
                        //  연결 실패
                    }
                });
                mqtt_client.setCallback(new MqttCallback() {

                    public void connectComplete(boolean reconnect, String serverURI) {
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.d("LOG::@@","Remote_Connect_Lost");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        if(message.toString().equals("Serial_Delete"))//시리얼 연동 해제시
                        {
                            Toast.makeText(getApplicationContext(),"시리얼이 해제되었습니다.", Toast.LENGTH_LONG).show();
                            Log.d("LOG::@@","시리얼 해제 감지");
                            mqtt_client.disconnect();
                            finish();
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
}
