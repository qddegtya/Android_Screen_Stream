package com;

public class PCScreenShot {
	public static void main(String[] args) {
		ScreenShot screenShot = new ScreenShot();     //支持多个手机端设备管理。
//        screenShot.getScreenShot("e://wts//", "screencapture_"+System.currentTimeMillis());
		while(true){
			screenShot.getScreenShot("D://python_workspace//Android_Screen_Stream//", "current_frame");
		}
    }
}
