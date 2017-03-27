package cn.partytime.util;


import java.io.*;

/**
 * Created by lENOVO on 2016/11/28.
 */
public class FileUtils {


    public static String txt2String(String filPath){
        StringBuilder result = new StringBuilder();
        try{
            File file = new File(filPath);
            if(!file.exists()){
                return "";
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){
                result.append(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void outputFile(String filePath,String content)  {
            FileOutputStream fos=null;
            try {
                File f = new File(filePath);
                if (f.exists()==false) {
                    f.createNewFile();//create file if not exist
                }
                //create FileOutputStream with file
                fos = new FileOutputStream(f);
                //output file
                fos.write(content.getBytes());
                //flush this stream
                fos.flush();
                //close this stream
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void writeContentToFile(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, false);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
