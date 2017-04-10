package cn.partytime.util;

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




}
