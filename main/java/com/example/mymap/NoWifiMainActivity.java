package com.example.mymap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ldg.mybatis.model.Location;


public class NoWifiMainActivity extends AppCompatActivity {//두번째 실행인데 와이파이 없을시

    ImageView imageView;

    private static final String ADDRESS="주소";
    private static final String LOCATION="위치";
    private static final String LATITUDE="위도";
    private static final String LONGITUDE="경도";


    private List<Location> locations;

    ArrayList<HashMap<String, Object>> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_wifi_main);
        itemList = new ArrayList<HashMap<String, Object>>();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void search_button() {//검색버튼

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

            addListItem(res);//검색한 지역에 해당하는 데이터를 마커로 등록
        }else{//검색 잘못했을때
            Toast.makeText(NoWifiMainActivity.this,"잘못된 검색", Toast.LENGTH_SHORT).show();
        }

    }

    private void addListItem(List<Location> res){

        //리스트뷰(일단 모든 객체에 대해 리스트뷰로 보이기)
        ListView listview = (ListView) findViewById(R.id.listView);
        itemList.clear();//기존에 있던객체 삭제

        for(Location l:res){
            HashMap<String,Object> p=new HashMap<>();
            p.put(ADDRESS,l.getAddress());
            p.put(LOCATION,l.getLocation());
            p.put(LATITUDE,l.getLatitude());
            p.put(LONGITUDE,l.getLongitude());

            itemList.add(p);
        }

        //리스트 어뎁터
        ListAdapter adapter = new SimpleAdapter(
                NoWifiMainActivity.this, itemList, R.layout.location_list,
                new String[]{ADDRESS,LOCATION,LATITUDE,LONGITUDE},
                new int[]{R.id.address, R.id.location,R.id.latitude,R.id.longitude}
        );

        listview.setAdapter(adapter);
    }

    public void list_click(View view){//목록 버튼
        Intent intent=new Intent(NoWifiMainActivity.this, SettingActivity.class);
        startActivity(intent);
        //finish();
    }

}
