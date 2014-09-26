/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author vkumar
 */
public class Constants {

    public final static String WINDOWS_SELENIUM_JAVA_CLIENT_LOCATION = "\\\\filesrv\\public\\public files\\SeleniumSetUp\\selenium-server-standalone-2.43.1.jar";
    public final static String WINDOWS_SELENIUM_CHROMEDRIVER_LOCATION = "\\\\filesrv\\public\\public files\\SeleniumSetUp\\ChromeDriver2.10\\chromedriver.exe";
    public final static String WINDOWS_SELENIUM_IEDRIVER_LOCATION = "\\\\filesrv\\public\\public files\\SeleniumSetUp\\IEDriver2.43.1\\IEDriverServer.exe";
    public final static String WINDOWS_SELENIUM_PHANTOMJS_LOCATION = "\\\\filesrv\\public\\public files\\SeleniumSetUp\\phantomjs-1.9.7\\phantomjs.exe";
    public final static String WINDOWS_HOST_SYSTEM_NET_LOCATION = "C:\\Windows\\system32\\net.exe";
    public final static String WINDOWS_HOST_FILE_LOCATION = "\\c$\\windows\\system32\\drivers\\etc";
    
    public final static String ANDROID_SELENROID_CLIENT_LOCATION = "\\\\filesrv\\public\\public files\\SeleniumSetUp\\selendroid-standalone-0.9.0-with-dependencies.jar";
    public final static String ANDROID_SELENROID_AUT_LOCATION = "C:\\autoscout24-android-app.apk";
    
          
    public final static String MAC_SELENIUM_JAVA_CLIENT_LOCATION = "Downloads/selenium-server-standalone-2.43.1.jar";
    public final static String MAC_PHANTOMJS_LOCATION = "Downloads/phantomjs/bin/phantomjs";      
    public final static String MAC_IOS_DRIVER_LOCATION = "Downloads/ios-server-standalone-0.6.6-SNAPSHOT.jar";
    public final static String MAC_TEMP_FILE_LOCATION = "C:\\Temp";
    public final static String MAC_HOST_FILE_LOCATION = "/private/etc/hosts";
   
    public final static String SELENIUM_REMOTE_WEBDRIVER_URL_FORMAT = "http://%s:%s/wd/hub";
    public final static String SELENIUM_REMOTE_WEBDRIVER_URL_STATUS = "http://%s:%s/wd/hub/status";
    public final static String SELENIUM_REMOTE_WEBDRIVER_URL_SHUTDOWN = "http://%s:%s/selenium-server/driver/?cmd=shutDownSeleniumServer";    
    
    public final static String NETWORK_USER_NAME = "zzqaadmin";
    public final static String NETWORK_PASSWORD = "as24test";
    public final static String NETWORK_DOMAIN = "DEV";
    
    public final static List MAC_MACHINE = Arrays.asList("andreass-mbp.as24.local");
    
    public final static String HOST_FILE_CONTENT = "http://dev-as24config.as24.local/GetHostFile.aspx?environment=%s";
    
    public final static String AS24_GATEWAY_PROXY = "http://tmg-array.as24.local:8080/array.dll?Get.Routing.Script";
}