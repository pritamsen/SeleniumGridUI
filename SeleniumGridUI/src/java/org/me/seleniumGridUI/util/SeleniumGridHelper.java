/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.util;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.Callable;
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
        hostDetails.setHostOperatingSystem(ReturnOperatingSystem(InetAddress.getLocalHost().getHostName()));
        return hostDetails;
    }
    
    private static HostDetails getInetHostDetails(String hostName) throws Throwable{
        InetAddress inetAddress = InetAddress.getByName(hostName);
        HostDetails hostDetails = new HostDetails();
        hostDetails.setHostAddress(inetAddress.getHostAddress());
        final String myHostName = inetAddress.getHostName();
        hostDetails.setHostName(myHostName); 
        hostDetails.setHostOperatingSystem(ReturnOperatingSystem(myHostName));
        return hostDetails;
    }
    
    private static String ReturnOperatingSystem (String validHostname)
    {
        if(Constants.MAC_MACHINE.contains(validHostname))
        {
           return "mac";
        }
        return "windows";
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
    
    public static boolean isValidSessionParam(String sessionId) {
        return (sessionId != null && !sessionId.isEmpty()) ? true : false;
    }
}
