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

import com.android.volley.Request;
import com.android.volley.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import static java.lang.Thread.sleep;

public class SignUp extends Activity {

    public EditText IdT,emailT,pass1T,pass2T;
    public String username,email,pwd;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        IdT= (EditText) findViewById(R.id.Id);
        emailT = (EditText) findViewById(R.id.email);
        pass1T = (EditText) findViewById(R.id.PassWord1);
        pass2T = (EditText) findViewById(R.id.PassWord2);
    }

    public void SignUp(View v)
    {

        username = IdT.getText().toString();
        email = emailT.getText().toString();
        pwd = pass1T.getText().toString();
        if(username.length()==0 || email.length()==0 || pwd.length()==0||pass2T.getText().toString().length()==0)
        {
            Toast.makeText(getApplicationContext(),"모든 정보를 입력 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(!pwd.equals(pass2T.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"비밀번호를 확인 해 주세요.",Toast.LENGTH_LONG).show();
            pass1T.setText("");
            pass2T.setText("");
        }
        else {
            registDB rdb = new registDB();
            rdb.execute();

        }
    }

    public void back(View v)
    {
        finish();
    }




    public class registDB extends AsyncTask<String, Void, String> {

        private StringBuffer buff = new StringBuffer();
        private String data = "";

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignUp.this,
                    "Please Wait", null, true, true);

        }

        protected void onPostExecute(String result) {

            responseStatus(result);
            progressDialog.dismiss();
        }

        protected String doInBackground(String... unused) {
            /* 인풋 파라메터값 생성 */
            String param = "email=" + email + "&username=" + username + "&pwd=" + pwd+ "";
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://1.237.145.244/temp/sign.php");
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

    public void responseStatus(String response)
    {

        switch(response)
        {

            case "Invalid ID":
            {
                Toast.makeText(getApplicationContext(),"아이디는 대/소문자 알파벳과 숫자만 사용 가능합니다",Toast.LENGTH_LONG).show();
                IdT.setText("");
                break;
            }
            case "MySQL 접속 에러":
            {
                Toast.makeText(getApplicationContext(),"데이터베이스에 접속할 수 없습니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "Invalid email":
            {
                Toast.makeText(getApplicationContext(),"이메일 주소는 이메일의 형식을 갖추어야 합니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "duplecate":
            {
                Toast.makeText(getApplicationContext(),"중복인 아이디입니다. 다른 아이디를 입력 해 주세요.",Toast.LENGTH_LONG).show();
                IdT.setText("");
                break;
            }
            case "success":
            {
                Intent it = new Intent();
                it.putExtra("id",username);
                it.putExtra("pass",pwd);
                setResult(RESULT_OK,it);

                Toast.makeText(getApplicationContext(),"회원가입에 성공했습니다!.",Toast.LENGTH_LONG).show();
                finish();
                break;
            }
            case "fail":
            {
                Toast.makeText(getApplicationContext(),"회원가입에 실패했습니다. 관리자에게 문의 해 주세요.",Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}


