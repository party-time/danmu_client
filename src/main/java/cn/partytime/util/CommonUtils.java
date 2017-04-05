package cn.partytime.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/4/4 0004.
 */
public class CommonUtils {

    public static String getIpAddress(){
        String ip="";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = "";
            e.printStackTrace();
        }
        return ip;
    }

    /*public static void main(String[] args) {
        System.out.print(getIpAddress());
    }*/
}
