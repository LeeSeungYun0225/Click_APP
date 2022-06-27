package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class EditEmail extends Activity {

    public EditText E_Email, E_Pass;
    public ProgressDialog progressDialog;
    public String email,pass,presentemail;
    public String userkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        Intent it = getIntent();

        userkey = it.getStringExtra("userKey");
        E_Email = (EditText) findViewById(R.id.emailToChange);
        E_Pass = (EditText) findViewById(R.id.E_PassWord);
        presentemail = it.getStringExtra("email");
        E_Email.setText(presentemail);
    }

    public void ChangeEmail(View v)
    {
        email = E_Email.getText().toString();
        pass = E_Pass.getText().toString();
        if(email.length() == 0)
        {
            Toast.makeText(getApplicationContext(),"이메일 주소를 입력 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(presentemail.equals(email))
        {
            Toast.makeText(getApplicationContext(),"현재 이메일 주소와 동일합니다.",Toast.LENGTH_LONG).show();
        }
        else if(pass.length()==0)
        {
            Toast.makeText(getApplicationContext(),"비밀번호를 입력 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else
        {
            EditEmailDB edb = new EditEmailDB();
            edb.execute();
        }

    }

    public void CancelEmail(View v)
    {
        finish();
    }


    public class EditEmailDB extends AsyncTask<String, Void, String> {

        private StringBuffer buff = new StringBuffer();
        private String data = "";

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditEmail.this,
                    "Please Wait", null, true, true);

        }

        protected void onPostExecute(String result) {

            String[] str = result.split("/");
            responseStatus(str);
            progressDialog.dismiss();
        }

        protected String doInBackground(String... unused) {
            /* 인풋 파라메터값 생성 */
            String param = "userkey=" + userkey + "&email=" + email + "&pwd=" + pass+ "";

            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://1.237.145.244/temp/Android_EditEmail.php");
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


    public void responseStatus(String[] response) {

        switch(response[0])
        {

            case "success":
            {
                Intent it = new Intent();
                it.putExtra("email",email);
                setResult(RESULT_OK,it);
                finish();
                break;
            }
            case "fail":
            {
                Toast.makeText(getApplicationContext(),"이메일 변경이 실패했습니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "passWrong":
            {
                Toast.makeText(getApplicationContext(),"비밀번호를 재 확인 해 주세요.",Toast.LENGTH_LONG).show();
                E_Pass.setText("");
                break;
            }
            case "Invalid email":
            {
                Toast.makeText(getApplicationContext(),"이메일 주소는 이메일의 형식을 갖추어야 합니다.",Toast.LENGTH_LONG).show();
                E_Email.setText("");
                break;
            }
        }


    }
}
