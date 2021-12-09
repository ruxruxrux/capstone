package com.example.mymap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ldg.mybatis.model.Location;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView imageView;//검색버튼

    private GoogleMap mMap;//구글맵
    private ClusterManager<MyItem> clusterManager;//클러스터

    public List<Location> locations;//와이파이 데이터


    double Lat1;//현재위도
    double Lon1;//현재경도


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        gpsTracker gpsTracker = new gpsTracker(MainActivity.this);//현재위치 받기
        Lat1 = gpsTracker.latitude; //현재 위도
        Lon1 = gpsTracker.longitude; //현재 경도


        // 내부저장소에서 데이터 가져오기
        try {
            FileInputStream fis =openFileInput("myFile");
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(bis);
            List<Location> res= (List<Location>) in.readObject();
            locations=res;

        } catch (Exception e){
            e.printStackTrace();
        }




        //검색버튼 누름 효과
        imageView=findViewById(R.id.search_button);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP://버튼 뗄때 이미지
                        view.setBackgroundResource(R.drawable.search);
                        search_button();//버튼을 눌렀다가 뗄때 검색함
                        return false;

                    case MotionEvent.ACTION_DOWN:
                        view.setBackgroundResource(R.drawable.search2);//버튼 눌렀을때 이미지
                        return true;
                }
                return false;
            }
        });

    }



    @Override
    public void onMapReady(final GoogleMap googleMap){

        mMap=googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat1, Lon1),15));//카메라가 현재위치를 줌하게

        clusterManager=new ClusterManager<>(this,mMap);//클러스터
        mMap.setOnCameraIdleListener(clusterManager);//클러스터 리스너

        MyItem currentItem=new MyItem(Lat1,Lon1,"주소","현재위치");//맵켰을때 현재위치를 마커에 표시-->나중에 삭제
        clusterManager.addItem(currentItem);//->나중에 삭제(현재위치를 마커에 띄울 필요는 없으므로)


        //초기화면에는 지역 상관 없이 현재위치에서 일정거리 이내에 있는 와이파이 데이터를 모두 표시
        // 나중에 검색하면 검색한 지역에 해당하는 와이파이 표시
        for(int i=0; i<locations.size();i++){//데이터 가져와서 현재위치와 거리비교
            //데이터가 100개정도라 for 문으로 함
            Location region=locations.get(i);


            double d=CalDistance.getDistance(Lat1,Lon1,region.getLatitude(),region.getLongitude());//현재위치와 와이파이 데이터의 거리계산


            if(d<2000){//일정거리 이내일때만 마커에 표시(임시로 2km)
                MyItem offsetItem=new MyItem(region.getLatitude(),region.getLatitude(),region.getAddress(),region.getLocation());//클러스터 등록
                clusterManager.addItem(offsetItem);

            }

        }


    }

    private void addItems(List<Location> res){//검색한 지역위치에 마커 등록하는 함수
        clusterManager.clearItems();//새로 검색하면 기존에 있던 마크 삭제

        for(Location r:res){
            //클러스터 등록(getAddress는 snippet으로, getLocation(와이파이명)은 title)
            MyItem offsetItem=new MyItem(r.getLatitude(),r.getLongitude(),r.getAddress(),r.getLocation());
            clusterManager.addItem(offsetItem);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void search_button(){//검색버튼
        EditText region=(EditText)findViewById(R.id.content_text);//검색한 지역
        String[] contents=(region.getText().toString()).split(" ");//공백기준으로 시군구 나눔
        //contents[0]은 시도, contents[1]은 시군구 contents[2]는 읍면동

        //위치정보객체를 스트림으로 필터링하여 검색한것과 같은것만 골라냄
        List<Location> res = null;
        if(contents.length==3){
            res=locations.stream()
                    .filter(p-> {
                        String[] add = p.getAddress().split(" ");//주소를 공백기준으로 나눔
                        if(add[0].equals(contents[0]) && add[1].equals(contents[1]) && add[2].equals(contents[2])){
                            return true;//주소가 검색한 지역과 일치하면 true
                        }
                        return false;
                    }).collect(Collectors.toList());//일치하는 데이터만 리스트로

            addItems(res);//검색한 지역에 해당하는 데이터를 마커로 등록
        }else{//검색 잘못했을때
            Toast.makeText(MainActivity.this,"잘못된 검색", Toast.LENGTH_SHORT).show();
        }

    }

    public void list_click(View view){//목록 버튼
        Intent intent=new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
        //finish();
    }
}