/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.util;

import java.net.InetAddress;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.me.seleniumGridUI.model.HostDetails;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SeleniumGridHelper {    
    final static Logger logger = Logger.getLogger(SeleniumGridHelper.class.getName());
     
    public static HostDetails getHostDetails(String hostName){
        HostDetails hostDetails = null;
        try {
            hostDetails = hostName.toLowerCase().startsWith("local") ? 
                                    getLocalHostDetails() : getInetHostDetails(hostName);
            hostDetails.setPort(SeleniumGridHelper.generateRandomPortNumber());
        } catch(Throwable e){
            logger.warning("Failed to extract hostDetails for " + hostName);
            logger.log(Level.SEVERE, "Failed to extract hostDetails for " + hostName, e);
        }
        return hostDetails;
    }
    
    private static HostDetails getLocalHostDetails() throws Throwable{
        HostDetails hostDetails = new HostDetails();
        hostDetails.setHostAddress(InetAddress.getLocalHost().getHostAddress());
        hostDetails.setHostName("localhost");
        return hostDetails;
    }
    
    private static HostDetails getInetHostDetails(String hostName) throws Throwable{
        InetAddress inetAddress = InetAddress.getByName(hostName);
        HostDetails hostDetails = new HostDetails();
        hostDetails.setHostAddress(inetAddress.getHostAddress());
        hostDetails.setHostName(inetAddress.getHostName());
        return hostDetails;
    }
      
    public static int generateRandomPortNumber() {
        int min = 1111;
        int max = 9999;
        Random num = new Random();
        return num.nextInt(max - min + 1) + min;
    }
    
    public static boolean isValidBrowserParam(String browser){
        return (browser != null && !browser.isEmpty()) ? true : false;
    }
    
    public static DesiredCapabilities createBrowserCapbility(String browser)
    {
        DesiredCapabilities caps = null;
        
        if(browser.equalsIgnoreCase("firefox"))        
        {
            caps = DesiredCapabilities.firefox();
        }
        else if (browser.equalsIgnoreCase("chrome"))
        {
            caps = DesiredCapabilities.chrome();
        }
        else if (browser.equalsIgnoreCase("ie"))
        {
            caps = DesiredCapabilities.internetExplorer();
        }
        else if (browser.equalsIgnoreCase("phantomjs"))
        {
            caps = DesiredCapabilities.phantomjs();
        }
        else
        {
            caps = DesiredCapabilities.htmlUnitWithJs();  
        }        
        return caps;
    }
}
