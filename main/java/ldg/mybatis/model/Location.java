package ldg.mybatis.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Location implements Serializable {
    //private int num;
    private String address;//주소
    private String location;//와이파이명
    private double latitude;//위도
    private double longitude;//경도

    //public int getNumber(){return num;}
    //public void setNumber(int number){this.number=num;}

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
