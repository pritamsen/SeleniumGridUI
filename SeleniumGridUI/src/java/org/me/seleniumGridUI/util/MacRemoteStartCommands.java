/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.util;

/**
 *
 * @author vkumar
 */
public class MacRemoteStartCommands {    
    
    public String GetSyntaxforJavaSeleniumWebClient(int portNumber)
    {
        return String.format("osascript -e 'tell app \"Terminal\" to do script \"java -jar %s -port %s -timeout=9999 -Dphantomjs.binary.path=%s\"'", Constants.MAC_SELENIUM_JAVA_CLIENT_LOCATION, portNumber, Constants.MAC_PHANTOMJS_LOCATION);
    }
    
    public String GetSyntaxforJavaIosDriver(int portNumber)
    {
        return String.format("osascript -e 'tell app \"Terminal\" to do script \"java -jar %s -port %s -newSessionTimeoutSec 120 -beta\"'", Constants.MAC_IOS_DRIVER_LOCATION, portNumber);
    }
    
    public String GetSyntaxToQuitRunningProcessByName(String processName)
    {        
        return String.format("osascript -e 'tell application \"%s\" to quit'", processName);        
    }
        
    public String GetSyntaxToForceKillRunningProcessByName(String processName)
    {
        return String.format("killall %s", processName);
    }
    
    public String GetSyntaxToOpenApplication(String appName)
    {
        return String.format("open -a %s.app", appName);
    }
    
    public String GetSyntaxToReplaceHostFile(String contentToCopy)
    {
        return String.format("chmod u+w %1$s;echo \"%2$s\" > %1$s", Constants.MAC_HOST_FILE_LOCATION,  contentToCopy);
    }
}