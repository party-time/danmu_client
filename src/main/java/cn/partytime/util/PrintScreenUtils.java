package cn.partytime.util;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by administrator on 2016/12/9.
 */
@Slf4j
public class PrintScreenUtils {

    public static void screenShotAsFile(String savePath,String saveFile) {
        try {
            Robot robot = new Robot();
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, 1366 , 768 ));
            File path = new File(savePath);
            if(!path.exists()){
                path.mkdir();
            }
            File file = new File(path, saveFile);
            ImageIO.write(bfImage, "jpg", file);
        } catch (AWTException e) {
            log.error("",e);
        } catch (IOException e) {
            log.error("",e);
        }
    }

    public static void open2Screen() {
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        try {
            log.info("press win+shift+->");
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_RIGHT);
            robot.keyRelease(KeyEvent.VK_RIGHT);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
            robot.keyRelease(KeyEvent.VK_SHIFT);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void moveWindow(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        int screenWidth = 0;
        int screenHeight = 0;
        if( null != gs ){
            for( int i=0;i<gs.length;i++){
                if(!ge.getDefaultScreenDevice().equals(gs[i])){
                    GraphicsConfiguration[] gc =gs[i].getConfigurations();
                    for(GraphicsConfiguration curGc : gc) {
                        Rectangle bounds = curGc.getBounds();
                        log.info( "width:"+new Double(bounds.getWidth()).intValue() + "height:" + new Double(bounds.getHeight()).intValue()  );
                        screenWidth = new Double(bounds.getWidth()).intValue();
                        screenHeight = new Double(bounds.getHeight()).intValue();
                    }
                }
            }
        }
        if( gs.length > 1){
            try {
                Thread.sleep(5*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("start move");
            User32 user32 = User32.INSTANCE;
            WinDef.HWND hwnd = user32.FindWindow(null,"聚时代弹幕影院");
            user32.MoveWindow(hwnd,screenWidth,0,screenWidth,screenHeight,false);
            log.info("move end");
        }
    }



}
