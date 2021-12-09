package com.example.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
    }

    public void mail_send_click(View view){
        String[] address = {"gy8094@nate.com"};
        String content=((EditText)findViewById(R.id.content)).getText().toString();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, address);//보낼사람주소
        email.putExtra(Intent.EXTRA_SUBJECT, "제목을 입력하세요.");//제목
        email.putExtra(Intent.EXTRA_TEXT, content);//내용


        startActivity(email);
    }

    public void back_click2(View view){
        Intent intent=new Intent(MailActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}

