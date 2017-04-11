package cn.partytime.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Created by administrator on 2017/4/11.
 */
public interface User32 extends Library {

    User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
    int FindWindow(String className, String windowName);

    /**
     * @param hwnd 目标句柄
     * @param x 目标窗体新位置X轴坐标
     * @param y 目标窗体新位置Y轴坐标
     * @param nWidth 目标窗体新宽度
     * @param nHeight 目标窗体新高度
     * @param BRePaint 是否刷新窗体
     * @return
     */
    int MoveWindow(int hwnd, int x, int y, int nWidth, int nHeight, boolean BRePaint);

}
