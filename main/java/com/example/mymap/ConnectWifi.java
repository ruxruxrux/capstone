package com.example.mymap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectWifi {

    public static boolean getNetwork(Context context){ //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo!=null){//네트워크 연결됨
            int type = networkInfo.getType();
            if(type==ConnectivityManager.TYPE_WIFI){//와이파이 연결됨
                return true;
            }
        }
        return false;

    }
}