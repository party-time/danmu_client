package cn.partytime.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/4 0004.
 */
public class CommonUtil {

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

    public static String convertFirstWordUpperCase(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 判断字符串中是否含有数字
     * @param content
     * @return
     */
    public static boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }


}
