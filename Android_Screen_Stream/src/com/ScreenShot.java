package com;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;

public class ScreenShot {
	 
    public IDevice device ;
   
    /**
     * 构造函数，默认获取第一个设备
     */
    public ScreenShot(){
    	AndroidDebugBridge.init(false); 
        device = this.getDevice(0);
    }
   
    /**
     * 构造函数，指定设备序号
     * @param deviceIndex 设备序号
     */
    public ScreenShot(int deviceIndex){
    	AndroidDebugBridge.init(false); //
    	device = this.getDevice(deviceIndex);
    }
   
    /**
     * 直接抓取屏幕数据
     * @return 屏幕数据
     */
    public RawImage getScreenShot(){
           RawImage rawScreen = null;
           if(device!=null){
                  try {
                         rawScreen = device.getScreenshot();
                  } catch (TimeoutException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                  } catch (AdbCommandRejectedException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                  } catch (IOException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                  }
           }else{
                  System.err.print("没有找到设备");
           }
           return rawScreen;
    }
   
   
    /**
     * 获取图片byte[]数据
     * @return 图片byte[]数据
     */
    public byte[] getScreenShotByteData(){
    	RawImage rawScreen = getScreenShot();
        if(rawScreen != null){
        	return rawScreen.data;
        }
        return null;
    }
   
   
    /**
     * 抓取图片并保存到指定路径
     * @param path 文件路径
     * @param fileName 文件名
     */
    public void getScreenShot(String path,String fileName){
    	RawImage rawScreen = getScreenShot();
    	if(rawScreen!=null){
    		Boolean landscape = false;
    		int width2 = landscape ? rawScreen.height : rawScreen.width;
    		int height2 = landscape ? rawScreen.width : rawScreen.height;
    		BufferedImage image = new BufferedImage(width2, height2,BufferedImage.TYPE_INT_RGB);
    		if (image.getHeight() != height2 || image.getWidth() != width2) {
    			image = new BufferedImage(width2, height2,BufferedImage.TYPE_INT_RGB);
            }
    		int index = 0;
    		int indexInc = rawScreen.bpp >> 3;
    		for (int y = 0; y < rawScreen.height; y++) {
    			for (int x = 0; x < rawScreen.width; x++, index += indexInc) {
    				int value = rawScreen.getARGB(index);
    				if (landscape) image.setRGB(y, rawScreen.width - x - 1, value);
    				else image.setRGB(x, y, value);
    			}
    		}
    		try {
    			ImageIO.write((RenderedImage) image, "jpeg", new File(path + "/" + fileName + ".jpeg"));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
   
    /**
     * 获取得到device对象
     * @param index 设备序号
     * @return 指定设备device对象
     */
    private IDevice getDevice(int index) {
    	IDevice device = null;
        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge();// 如果代码有问题请查看API，修改此处的参数值试一下
        waitDevicesList(bridge);
        IDevice devices[] = bridge.getDevices();
            
        for (int i = 0; i < devices.length; i++) {
        	System.out.println(devices[i].toString());
        }
            
        if(devices.length < index){
        	//没有检测到第index个设备
            System.err.print("没有检测到第" + index + "个设备");
        }else{
        	if (devices.length-1>=index) {
        		device = devices[index];
        	}else{
        		device = devices[0];
        	}
        }
        return device;
    }
   
    /**
     * 等待查找device
     * @param bridge
     */
    private void waitDevicesList(AndroidDebugBridge bridge) {
    	int count = 0;
         while (bridge.hasInitialDeviceList() == false) {
        	try {
        		Thread.sleep(500);
        		count++;
        	} catch (InterruptedException e) {
            
        	}
        	if (count > 60) {
        		System.err.print("等待获取设备超时");
                break;
            }
        }
    }
}