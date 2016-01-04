#!/usr/bin/env python
# -*- coding:utf-8 -*-

import sys,os
sys.path.append(r'C:\\ddmlib.jar')
sys.path.append(r'C:\\guava17.jar')
sys.path.append(r'C:\\rt.jar')

from java.awt.image import *
from java.io import *
from javax.imageio import *
from com.android.ddmlib import *
from time import sleep

class AndroidScreenStream(object):

    def __init__(self):
        self.devices=[]
        self.currentdevice=None
        self.rawimgdata=None
        self.rawscreen=None

    def wait_device_lists(self,bridge):
        """获取设备列表信息"""
        count = 0
        while(bridge.hasInitialDeviceList() == False):
            try:
                sleep(0.5)
                count += 1
            except:
                pass
            if(count > 60):
                print "获取设备列表信息超时！"
                break

    def get_devices(self):
        """获取所有已连接设备"""
        AndroidDebugBridge.init(False)
        mybridge = AndroidDebugBridge.createBridge()
        self.wait_device_lists(mybridge) #get
        self.devices.append(mybridge.getDevices())
        return self

    def get_device_by_index(self,index):
        """根据index获取单个当前设备"""
        AndroidDebugBridge.init(False)
        mybridge = AndroidDebugBridge.createBridge()
        self.wait_device_lists(mybridge)
        try:
            self.currentdevice = mybridge.getDevices()[index]
            return self
        except IndexError:
            print "不存在序号为" + str(index) + "的设备,请检查USB连接!"
            sys.exit()

    def get_current_screen_imgbyes(self):
        """获取当前屏幕图像的字节数据"""
        self.rawimgdata = self.currentdevice.getScreenshot().data  # 保存图像字节数据
        return self

    def get_current_rawscreen(self):
        """保存当前屏幕screen对象"""
        self.rawscreen = self.currentdevice.getScreenshot()
        return self

    def save_screen_img(self,path = os.path.join(os.getcwd(),'current_frame.jpeg'),islandscape = False):
        """将当前屏幕图像数据保存为图片"""
        if self.rawscreen is not None:
            landscape = islandscape
            # 横屏尺寸处理
            imgwidth = self.rawscreen.height if landscape else self.rawscreen.width
            imgheight = self.rawscreen.width if landscape else self.rawscreen.height
            image = BufferedImage(imgwidth,imgheight,BufferedImage.TYPE_INT_RGB)
            if image.getHeight() != imgheight or image.getWidth() != imgwidth:
                image = BufferedImage(imgwidth,imgheight,BufferedImage.TYPE_INT_RGB)
            index = 0
            indexInc = self.rawscreen.bpp >> 3
            for y in range(self.rawscreen.height):
                for x in range(self.rawscreen.width):
                    value = self.rawscreen.getARGB(index)
                    if landscape:
                        image.setRGB(y,self.rawscreen.width - x - 1,value)
                    else:
                        image.setRGB(x,y,value)
                    index += indexInc
            try:
                ImageIO.write(image,"JPEG",File(path))
            except IOError:
                print "发生异常!"
                sys.exit()
        else:
            print "rawscreen为空!"
            sys.exit()

if __name__ == "__main__":
    ASS = AndroidScreenStream()
    ShotDevice = ASS.get_device_by_index(0)
    while True:
        ShotDevice.get_current_rawscreen().save_screen_img()
        
