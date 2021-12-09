package com.example.mymap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import ldg.mybatis.model.Location;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    public static boolean file_exist;//파일저장여부

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView load = (ImageView) findViewById(R.id.gif_image);
        Glide.with(this).load(R.raw.loading).into(load);//gif 이미지 띄우기


        File file=new File("/data/user/0/com.example.mymap/files/myFile");//파일존재여부
        file_exist=file.exists();
        Log.i("파일", String.valueOf(file.exists()));//파일존재 여부 확인용도


        if(!file_exist){//첫실행이면(다운로드된 파일이 없다면)

            boolean network=ConnectWifi.getNetwork(getApplicationContext());//와이파이 연결 체크

            if(network){//와이파이 연결될시
                new download().execute();//doInBackgroud 실행(다운로드 실행)


            }else{//와이파이 연결 안될 시
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("에러").setMessage("와이파이 연결상태 확인");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
        else{//첫실행이 아닐결우
            boolean network=ConnectWifi.getNetwork(getApplicationContext());//와이파이 연결 체크

            if(network){//첫실행 아니고 와이파이 연결되면 지도 보여줌
                //최신버전 확인하는 코드 필요

                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(main);
                finish();

            }else{//첫실행 아니고 와이파이 연결안될시 위치 데이터만 보여줌(지도x)
                //와이파이연결 안되어 있기 때문에 최신버전 여부 확인 안함
                Intent main = new Intent(SplashActivity.this, NoWifiMainActivity.class);
                startActivity(main);
                finish();
            }

        }

    }

    private class download extends AsyncTask<Void, Void, List<Location>> {//첫 실행일 경우 다운로드 할 것,두번째라면 최신버전 여부묻기

        Socket mSocket;
        ObjectInputStream ois;

        @Override
        protected List<Location> doInBackground(Void... voids) {//백그라운드 실행할것(다운로드)


            //서버연결
            try {
                mSocket=new Socket("58.65.115.41",5002);//연결
                System.out.println("연결됨");

                ois=new ObjectInputStream(mSocket.getInputStream());
                System.out.println("받아옴");

                List<Location> locations= (List<Location>)ois.readObject();
                System.out.println("객체받아옴");
                return locations;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            } finally{
                try {
                    ois.close();
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
           return null;



        }

        protected void onPostExecute(List<Location> result) {//백그라운드 완료되면 실행할것

            //첫실행이면 내부저장소에 객체 저장
            //두번째 실행(최신버전확인 용도)이라면 마지막 객체 확인후 마지막객체가 다르면 객체를 새로 내부저장소에 저장

            byte[] resultByte= ObjectConverter.getByte(result);//바이트배열로 바꿈

            try {
                FileOutputStream fos = openFileOutput("myFile",MODE_PRIVATE);//내부저장소에 파일 저장
                fos.write(resultByte);
                fos.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Date date= new Date();//다운로드 다 되었을때 시간

            byte[] downloadDate=ObjectConverter.getByte(date);//날짜객체 바이트배열로 바꿈
            try {
                FileOutputStream fos = openFileOutput("downloadDate",MODE_PRIVATE);//내부저장소에 날짜객체 저장
                fos.write(downloadDate);
                fos.flush();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Intent main = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        }


    }

}
