package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

public class Login extends Activity {

    public Intent sign;
    public EditText Id,pass;
    private int key;
    private String email;
    public ProgressDialog progressDialog;
    public String username,pwd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sign = new Intent(this,SignUp.class);
        sign.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        key = 0;

        Id= (EditText) findViewById(R.id.ID);
        pass = (EditText) findViewById(R.id.PassWord);
    }


    public void startSign(View v)
    {
        startActivityForResult(sign,9992);
    }

    protected void onActivityResult(int request,int result, Intent data)
    {
        if(request == 9992 && result == RESULT_OK)
        {
            Id.setText(data.getStringExtra("id"));
            pass.setText(data.getStringExtra("pass"));
        }
    }

    public void Login(View v)
    {

        if(Id.getText().toString().length()==0)
        {
            Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else if(pass.getText().toString().length()==0)
        {
            Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else
        {
            username = Id.getText().toString();
            pwd = pass.getText().toString();

            LoginDB login = new LoginDB();
            login.execute();
        }
    }

    public void back(View v)
    {
        finish();
    }

    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }



    public class LoginDB extends AsyncTask<String, Void, String> {
        private StringBuffer buff = new StringBuffer();
        private String data = "";

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Login.this,
                    "Please Wait", null, true, true);

        }

        protected void onPostExecute(String result) {

            String[] str = result.split("/");
            responseStatus(str);
            progressDialog.dismiss();
        }

        protected String doInBackground(String... unused) {
            /* 인풋 파라메터값 생성 */
            String param = "username=" + username + "&pwd=" + pwd+ "";
            Log.d("LOG::@@","username + pwd" + username + pwd);
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://1.237.145.244/temp/AndroidLogin.php");
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
        switch(response[0])
        {
            case "success":
            {
                Intent it = new Intent();
                it.putExtra("userid",response[1]);
                it.putExtra("email",response[2]);
                it.putExtra("strID",Id.getText().toString());
                if(!response[3].equals("NULL"))
                {
                    it.putExtra("serial",response[3]);
                }
                else
                {
                    it.putExtra("serial","null");
                }


                setResult(RESULT_OK,it);
                Toast.makeText(getApplicationContext(),"로그인에 성공했습니다!.",Toast.LENGTH_LONG).show();
                finish();
                break;
            }
            case "NotFound":
            {
                Toast.makeText(getApplicationContext(),"찾을 수 없는 정보입니다.",Toast.LENGTH_LONG).show();
                Id.setText("");
                pass.setText("");
                break;
            }
            case "passWrong":
            {
                Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                pass.setText("");
                break;
            }
            case "fail":
            {
                Toast.makeText(getApplicationContext(),"로그인에 실패했습니다.",Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}
