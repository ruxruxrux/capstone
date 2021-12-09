package com.example.mymap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ldg.mybatis.model.Location;


public class SettingActivity extends AppCompatActivity {
    public static Date date;//날짜
    String getTime;
    TextView currentDate;//날짜 띄울것

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //내부저장소에서 날짜객체 가져오기
        try {
            FileInputStream fis =openFileInput("downloadDate");
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(bis);
            Date res= (Date) in.readObject();//내부저장소에 저장된 날짜객체 읽어오기
            date=res;

        } catch (Exception e){
            e.printStackTrace();
        }


        //날짜객체 띄우기
        currentDate= findViewById(R.id.current_date);

        if(date!=null){//날짜객체가 있으면
            getTime= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);//날짜 포맷
            currentDate.setText(getTime);//textview에 날짜 표시
        }else{//else는 필요없을 듯
            currentDate.setText("널");
        }



    }

    public void update_click(View view){
        //최신버전이면 알림문구창 띄우고 시간변동없음
        //Toast.makeText(SettingActivity.this,"이미 최신버전 입니다.", Toast.LENGTH_SHORT).show();

        //최신 버전 아닐경우 data 업데이트 및 시간 업데이트
        new asyncUpdate().execute();//비동기 업데이트


    }

    public void send_click(View view){//메일 전송버튼
        Intent intent=new Intent(SettingActivity.this,MailActivity.class);//메일 전송화면으로
        startActivity(intent);

    }

    public void back_click(View view){//뒤로가기 버튼
        Intent intent=new Intent(SettingActivity.this,MainActivity.class);//메인화면으로
        startActivity(intent);
        finish();
    }


    private class asyncUpdate extends AsyncTask<Void, Void, Void> {//최신버전이 아닐경우

        @Override
        protected Void doInBackground(Void... voids) {//비동기 업데이트 실행(서버에서 객체가져오기)


            //업데이트하는 코드 추가해야함
            return null;

        }

        protected void onPostExecute(Void result) {//객체를 내부저장소에 저장하기&날짜 업데이트

            //서버에서 가져온 데이터를 바이트 배열로 바꾸고 다시 내부저장소에 저장하는 코드필요

            //서버에서 객체가져온 후 시간(업데이트 완료후 시간)
            date= new Date();
            getTime=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);

            currentDate= findViewById(R.id.current_date);
            currentDate.setText(getTime);//날짜 표시


            byte[] downloadDate=ObjectConverter.getByte(date);
            try {
                FileOutputStream fos = openFileOutput("downloadDate",MODE_PRIVATE);//내부저장소에 날짜객체 저장 날짜도 기억하기 위해서 저장함
                fos.write(downloadDate);
                fos.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(SettingActivity.this,"업데이트 완료", Toast.LENGTH_SHORT).show();



        }




    }
}
