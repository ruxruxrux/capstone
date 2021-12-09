package com.example.mymap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {//클러스터 등록하기 위해 필요한 Item
    private final LatLng position;//위치(위도,경도)
    private final String title;//타이틀(와이파이명)
    private String address;//주소

    public MyItem(double lat, double lng,String address,String title){
        position = new LatLng(lat, lng);
        this.title=title;
        this.address=address;
    }
    @Override
    public LatLng getPosition() {
        return position;
    }//마커의 위치

    @Override
    public String getTitle() {
        return title;
    }//마커의 타이틀

    @Override
    public String getSnippet() {
        return address;
    }//마커의 snippet



}
