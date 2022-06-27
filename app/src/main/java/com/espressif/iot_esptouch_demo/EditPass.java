package com.espressif.iot_esptouch_demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
import java.sql.PreparedStatement;

public class EditPass extends Activity {


    public EditText PresentPass, PostPass,PostPassConfirm;
    public ProgressDialog progressDialog;
    public String present,post;
    public String userkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pass);

        PresentPass = (EditText) findViewById(R.id.presentPass);
        PostPass = (EditText) findViewById(R.id.PassToReplace);
        PostPassConfirm = (EditText) findViewById(R.id.PassToReplaceConfirm);
        Intent itent = getIntent();

        userkey = itent.getStringExtra("userKey");


    }

    public void Cancel(View v)
    {
        finish();
    }

    public void Edit(View v)
    {
        present = PresentPass.getText().toString();
        post = PostPass.getText().toString();
        if(present.length()== 0)
        {
            Toast.makeText(getApplicationContext(),"현재 패스워드를 입력 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(post.length()==0)
        {
            Toast.makeText(getApplicationContext(),"새 비밀번호를 입력 해 주세요.",Toast.LENGTH_LONG).show();
        }
        else if(PostPassConfirm.getText().toString().length() == 0)
        {
            Toast.makeText(getApplicationContext(),"새 비밀번호를 한번 더 입력해주세요..",Toast.LENGTH_LONG).show();
        }
        else if(!post.equals(PostPassConfirm.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"새 비밀번호를 확인 해 주세요.",Toast.LENGTH_LONG).show();
            PostPass.setText("");
            PostPassConfirm.setText("");
        }
        else
        {
            EditPassWordToDB saveInDB = new EditPassWordToDB();
            saveInDB.execute();
        }
    }


    public class EditPassWordToDB extends AsyncTask<String, Void, String> {

        private StringBuffer buff = new StringBuffer();
        private String data = "";

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditPass.this,
                    "Please Wait", null, true, true);

        }

        protected void onPostExecute(String result) {

            String[] str = result.split("/");
            responseStatus(str);
            progressDialog.dismiss();
        }

        protected String doInBackground(String... unused) {
            /* 인풋 파라메터값 생성 */
            String param = "userkey=" + userkey + "&pwd=" + present + "&pwd2=" + post+ "";
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://1.237.145.244/temp/Android_EditPass.php");
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
                Toast.makeText(getApplicationContext(),"비밀번호 변경이 완료되었습니다.",Toast.LENGTH_LONG).show();
                finish();
                break;
            }
            case "fail":
            {
                Toast.makeText(getApplicationContext(),"비밀번호 변경이 실패했습니다.",Toast.LENGTH_LONG).show();
                break;
            }
            case "passWrong":
            {
                Toast.makeText(getApplicationContext(),"현재 비밀번호를 재 확인 해 주세요.",Toast.LENGTH_LONG).show();
                PresentPass.setText("");
                break;
            }
        }


    }

}
