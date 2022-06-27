package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectEsp extends Activity {



    private EditText Eserial;
    private Button Bverify;
    private String userID;
    private String serial;
    private Intent itt;
    private boolean isVerified;
    private ProgressDialog progressDialog;
    private DeviceVerifyDB dvdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_esp);

        Eserial = (EditText) findViewById(R.id.Serial);
        Bverify = (Button) findViewById(R.id.verifyBTN);

        isVerified = false;

        itt = getIntent();
        userID = itt.getStringExtra("userID");
        Eserial.setText(itt.getStringExtra("serial"));
        if(Eserial.getText().toString().length()!= 0)
        {
            serial = Eserial.getText().toString();
            Bverify.setText("연동해제");
            Eserial.setEnabled(false);
            isVerified = true;
        }

    }


    public void Verify(View v) {

        serial = Eserial.getText().toString();
        dvdb = new DeviceVerifyDB();
         //1) MQTT 통신으로 ESP와 검증과정 수행
        if(!isVerified) {
            dvdb.setFunctype("1");

                Log.d("LOG::@@", "VERIF");

        }
        else
        {
            dvdb.setFunctype("2");
            Log.d("LOG::@@", "VERIF2");
        }

        dvdb.execute();


        //2) 검증 되었으면 DB와 연결해서 사용자 테이블에 ESP 시리얼 정보 저장 + 기기 테이블에 연동 되었는지 bindedinto에 사용자 계정 저장

        //3) Swap 액티비티로 시리얼 값 전달

        //4) 파일 입출력을 통해 어플을 켤 때마다 바로 해당 시리얼 읽어올 수 있도록 파일 저장
    }
    public void Close(View v)
    {
        if(!isVerified)
        {
            itt.putExtra("serial","null");
        }
        finish();
    }



    public class DeviceVerifyDB extends AsyncTask<String, Void, String> {

        private StringBuffer buff = new StringBuffer();
        private String data = "";
        private String functype = "1";

        public void setFunctype(String f)
        {
            functype = f;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ConnectEsp.this,
                    "Please Wait", null, true, true);

        }

        protected void onPostExecute(String result) {

            String[] str = result.split("/");
            responseStatus(str);
            progressDialog.dismiss();
        }

        protected String doInBackground(String... unused) {
            /* 인풋 파라메터값 생성 */
            String param = "userID=" + userID + "&serial=" + serial+ "&functype=" + functype +"";
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://1.237.145.244/temp/Android_Device_Verification.php");
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
                Log.e("RECV DATA",data);

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

    public void responseStatus(String[] response)
    {

        Log.d("LOG::@@",response[0]);

        switch(response[0])
        {
            case "Verify_Success":
            {
                Bverify.setText("연동해제");
                Eserial.setEnabled(false);
                itt.putExtra("serial",Eserial.getText().toString());
                setResult(RESULT_OK,itt);
                isVerified = true;
                Toast.makeText(getApplicationContext(),"연동에 성공했습니다!.",Toast.LENGTH_LONG).show();
                finish();
                break;
            }
            case "Serial_not_Exist":
            {
                Toast.makeText(getApplicationContext(),"존재하지 않는 시리얼 번호입니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "DeVerify_Success":
            {
                Bverify.setText("연동");
                Eserial.setText("");
                itt.putExtra("serial","null");
                setResult(RESULT_OK,itt);
                Eserial.setEnabled(true);
                isVerified = false;
                Toast.makeText(getApplicationContext(),"연동 해제에 성공했습니다!.",Toast.LENGTH_LONG).show();
                finish();
                break;
            }
            case "Already_Using_Device":
            {
                Toast.makeText(getApplicationContext(),"이미 사용중인 디바이스입니다.",Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}
