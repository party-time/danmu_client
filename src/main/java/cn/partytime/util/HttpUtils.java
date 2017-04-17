package cn.partytime.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by administrator on 2016/12/8.
 */
@Slf4j
public class HttpUtils {



    public static String repeatRequest(String requestUrl, String requestMethod, String outputStr){
        int count = 0;
        while (count<3){
            String str = HttpUtils.httpRequestStr(requestUrl,"GET",null);;
            try {
                if(!StringUtils.isEmpty(str)){
                    return str;
                }
            }catch (Exception e){
                System.out.print("获取数据异常");
            }
            count++;
            System.out.print("请求失败，等待"+count+"秒，再次发起请求");
            try {
                Thread.sleep(count*2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String  httpRequestStrEncodeUrl(String requestUrl,String param, String requestMethod, String outputStr){
        try {
            param = URLEncoder.encode(param, "utf-8");
            requestUrl+="&param="+param;
            // 如有中文一定要加上，在接收方用相应字符转码即可
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return httpRequestStr(requestUrl, requestMethod, outputStr);
    }
    /**
     * http请求
     *
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @return
     */
    public static String httpRequestStr(String requestUrl, String requestMethod, String outputStr) {
        System.out.println("url:"+requestUrl);
        StringBuffer buffer = new StringBuffer();
        try {
            //requestUrl = requestUrl.replaceAll(requestUrl, URLEncoder.encode(requestUrl, "utf-8"));
            //requestUrl = URLEncoder.encode(requestUrl, "utf-8");
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);
            httpUrlConn.setRequestProperty("Content-type", "text/html");
            httpUrlConn.setRequestProperty("Accept-Charset", "utf-8");
            httpUrlConn.setRequestProperty("contentType", "utf-8");
            if ("GET".equalsIgnoreCase(requestMethod))

                httpUrlConn.connect();


            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
        } catch (ConnectException ce) {
            log.error("http server connection timed out.");
        } catch (Exception e) {
            log.error("https request error:{}", e);
        }
        log.info("getInfo  :" + buffer.toString());
        return buffer.toString();
    }


    /**
     * 下载网络文件
     *
     * @param urlStr
     */
    public static String downloadHttpFile(String urlStr, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String fileName = getFileName(urlStr);
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        log.info("info:" + url + " download success");
        return file.getCanonicalPath().replaceAll("\\\\", "/");
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 从url中获取文件名
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        if (!StringUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return "";
    }


    public static void postFile(String filePath,String postUrl){
        HttpURLConnection conn = null;
        String BOUNDARY = "----WebKitFormBoundaryhgNhGseXdl2PrpA6";
        String inputName = "Filedata";
        try {
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            File file = new File(filePath);
            String filename = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            contentType = "image/jpg";
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY).append(
                    "\r\n");
            strBuf.append("Content-Disposition: form-data; name=\""
                    + inputName + "\"; filename=\"" + filename
                    + "\"\r\n");
            strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(
                    new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 将返回的输入流转换成字符串
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            conn.disconnect();
        }catch (Exception e){
            log.error("",e);
        }

    }
    
}
