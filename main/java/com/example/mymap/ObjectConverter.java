package com.example.mymap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import ldg.mybatis.model.Location;

public class ObjectConverter {//선우님

    public static byte[] getByte(Object object){
        try{
            ByteArrayOutputStream io=new ByteArrayOutputStream();
            ObjectOutputStream os=new ObjectOutputStream(io);
            os.writeObject(object);
            os.flush();

            return io.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Location> getObject(){

       try{
           FileInputStream fis =new FileInputStream("myFile");
           BufferedInputStream bis = new BufferedInputStream(fis);
           ObjectInputStream in = new ObjectInputStream(bis);

           List<Location> res= (List<Location>) in.readObject();
           return res;
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }

        return null;
    }



}
