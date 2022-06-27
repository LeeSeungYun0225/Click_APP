package com.espressif.iot_esptouch_demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;

public class Click_Background_Service extends Service {

    private static final String TAG = "MyService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }
    
    public int onStartCommand(Intent intent,int flag,int startid)
    {
        return START_STICKY;
    }


    private TelephonyManager m_Telephony_Manager;
    private CallStateListener m_callStateListener;

    private String url = "tcp://1.237.145.244:1883";
    private String clientID = UUID.randomUUID().toString();
    private MemoryPersistence mPer = new MemoryPersistence();
    private MqttConnectOptions options = new MqttConnectOptions();
    private MqttAndroidClient m_Mqttclient;
    private MqttMessage m_message = new MqttMessage();
    private String numID,strID,serial;
    private String idByANDROID_ID;
    private NotificationManager m_Notification_Manager;
    private NotificationCompat.Builder m_Notification;
    private NotificationChannel m_Noti_Channel;
    private Thread m_Loading_Thread;

    public void NotifyRecipe(String title,String text)
    {
        m_Notification_Manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        m_Notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.app_icon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(new long[]{0,500});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            m_Noti_Channel = new NotificationChannel("chn",
                    "Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            m_Notification_Manager.createNotificationChannel(m_Noti_Channel);
            m_Notification.setChannelId("chn");
        }


        m_Notification_Manager.notify(0,m_Notification.build());
        Log.d("LOG::@@","NOTIFY");
    }

    public void onCreate()
    {
        super.onCreate();
        idByANDROID_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        m_Loading_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (m_Mqttclient.isConnected()) {
                        isFile();
                        Log.d("LOG::@@", "service file load Success");
                        try{
                            m_Mqttclient.subscribe("abc",0);
                        }catch(MqttException e)
                        {
                            e.printStackTrace();
                        }

                        return;
                    }
                }
            }
        });
        m_Loading_Thread.start();
        // MQTT CONNECTION
        options.setCleanSession(true);
        options.isAutomaticReconnect();
        m_Mqttclient = new MqttAndroidClient(getApplicationContext(),url,clientID,mPer);
        connect();

        Log.d("LOG::@@","Click Service_ Started!");
        m_Telephony_Manager =(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        m_callStateListener = new CallStateListener();
        m_Telephony_Manager.listen(m_callStateListener,PhoneStateListener.LISTEN_CALL_STATE);

        numID = "";
        strID = "";
        serial = "";
    }

    public boolean isFile()
    {
        String filecheck = getFilesDir()+"click_login.txt";
        File file = new File(filecheck);

        if(file.exists() && m_Mqttclient.isConnected())
        {
            String[] read = m_ReadLoginFile().split("/");
            numID = read[0];
            strID = read[1];
            if(read[2].equals("null"))
            {
                try{
                    m_Mqttclient.subscribe(numID+strID,0);
                    Log.d("LOG::@@","MQTT_TEST1");
                }catch(MqttException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try{
                    serial = read[2];
                    m_Mqttclient.subscribe(numID+strID,0);
                    m_Mqttclient.subscribe("Click_Serial/"+serial,0);
                    m_Mqttclient.subscribe("esp/sendIR/"+serial+"/recipe",0);
                    Log.d("LOG::@@","MQTT_TEST2");

                }catch(MqttException e)
                {
                    e.printStackTrace();
                }
            }


    }
        else
        {
            if(m_Mqttclient.isConnected()) {
                try {
                    m_Mqttclient.subscribe(idByANDROID_ID, 0);
                    Log.d("LOG::@@","MQTT_TEST3");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

        return file.exists();
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



    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  {//전화 울릴 때
                    // called when someone is ringing to this phone
                    Log.d("LOG::@@", "PhoneCALL");
                    if(m_Mqttclient.isConnected() && serial.length()!=0) {
                        m_message = new MqttMessage("in".getBytes());
                        try {
                            m_Mqttclient.publish("esp/sendIR/" + serial +"/call", m_message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK:  // 전화 걸 때
                {
                    if(m_Mqttclient.isConnected() && serial.length()!=0)  {
                        try{
                            m_message = new MqttMessage("out".getBytes());
                            m_Mqttclient.publish("esp/sendIR/" + serial +"/call", m_message);
                            Log.d("LOG::@@", "CALLING ");
                        }catch(MqttException e)
                        {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }
        }
    }

    public void connect() {
        Log.d("LOG::@@","Mqtt_Connection_try");

        try {
            if (!m_Mqttclient.isConnected()) { // 연결되지 않았을 때
                IMqttToken token = m_Mqttclient.connect(options); // 연결 시도
                token.setActionCallback(new IMqttActionListener() {

                    public void onSuccess(IMqttToken asyncActionToken) { // 연결 성공
                    }

                    @Override

                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("LOG::@@","Service_fail");
                    }
                });
                m_Mqttclient.setCallback(new MqttCallback() {

                    public void connectComplete(boolean reconnect, String serverURI) {
                        Log.d("MqttConnection :","SERVICE_SUCCESS");
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.d("LOG::@@","Service_Lost");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        if(topic.equals(numID+strID))
                        {
                            if(message.toString().equals("Logout"))//로그아웃시
                            {
                                m_Mqttclient.unsubscribe(numID+strID);
                                if(serial.length()!=0)
                                {
                                    m_Mqttclient.unsubscribe("Click_Serial/"+serial);
                                }
                                m_Mqttclient.subscribe(idByANDROID_ID,0);
                                numID = "";
                                strID = "";
                                serial = "";

                                Log.d("LOG::@@","Logout");
                            }
                            else // 시리얼 정보 받을시
                            {
                                if(!message.toString().equals(serial))
                                {
                                    serial = message.toString();
                                    Log.d("LOG::@@",serial);
                                    m_Mqttclient.subscribe("Click_Serial/"+serial,0); // 시리얼 구독
                                    Log.d("LOG::@@","Serial_Arrive");
                                    m_Mqttclient.subscribe("esp/sendIR/"+serial+"/recipe",0);
                                }

                            }

                        }
                        else if(topic.equals("Click_Serial/"+serial)) {
                            if (message.toString().equals("Serial_Delete"))//시리얼 연동 해제시
                            {
                                m_Mqttclient.unsubscribe("Click_Serial/" + serial); // 시리얼 구독 해제
                                Log.d("LOG::@@", "Serial_Del");
                                serial = "";

                            }
                        }
                        else if(topic.equals(idByANDROID_ID)) // 로그인시
                        {
                            String[] str = message.toString().split("/");
                            numID = str[0];
                            strID = str[1];
                            if(!str[2].equals("null"))
                            {
                                serial = str[2];
                                m_Mqttclient.subscribe("Click_Serial/"+serial,0);
                                m_Mqttclient.subscribe("esp/sendIR/"+serial+"/recipe",0);
                            }

                            m_Mqttclient.unsubscribe(idByANDROID_ID);
                            m_Mqttclient.subscribe(numID+strID,0);
                            Log.d("LOG::@@","Login");

                        }
                        else if(topic.equals("esp/sendIR/"+serial+"/recipe"))//레시피 수행시
                        {
                                String str = message.toString();
                                String[] receive = str.split("/");
                                String title;
                                String noti_message = "";
                                    title = receive[0] + " 기기의 레시피 실행됨";
                                    if(receive[1].equals("temp")) // 기기명 // if_this // then what //  hour //  mm //  day //  ifThis_complex //  ifThis_complex2
                                    {
                                        noti_message += "온도가 " + receive[6] + "ºC 일 때 ";
                                    }
                                    else if(receive[1].equals("time"))
                                    {
                                        if(receive[5].equals("1")) // 주중
                                        {
                                            noti_message += "주중에 "+ receive[3] +"시 "+receive[4]+"분에 " ;
                                        }
                                        else if(receive[5].equals("2")) // 매일
                                        {
                                            noti_message += "매일 " +  receive[3] +"시 "+receive[4]+"분에 " ;
                                        }
                                        else if(receive[5].equals("3")) // 주말
                                        {
                                            noti_message += "주말에 " +  receive[3] +"시 "+receive[4]+"분에 " ;
                                        }
                                    }
                                    else if(receive[1].equals("callin"))
                                    {
                                        noti_message += "전화가 왔을 때 ";
                                    }
                                    else if(receive[1].equals("callout"))
                                    {
                                        noti_message += "전화를 걸 때 ";
                                    }
                                    if(receive[2].equals("On"))//전원 킬때
                                    {
                                        noti_message += "전원 켜기";
                                    }
                                    else if(receive[2].equals("Off"))
                                    {
                                        noti_message += "전원 끄기";
                                    } else if(receive[2].equals("Mute"))
                                    {
                                        noti_message += "음소거";
                                    }
                                    else if(receive[2].equals("temp"))
                                    {
                                        noti_message += "온도를 " + receive[7] + "ºC로 변경";
                                    }
                                    else if(receive[2].equals("Channel"))
                                    {
                                        noti_message += "채널을 " + receive[7] + "로 변경";
                                    }
                                    NotifyRecipe(title,noti_message);
                        }
                        else
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


}
