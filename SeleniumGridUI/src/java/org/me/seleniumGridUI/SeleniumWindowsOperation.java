/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import org.me.seleniumGridUI.util.Constants;

/**
 *
 * @author vkumar
 */
public class SeleniumWindowsOperation {
    private final int _portNumber;
    private final String _windowExecutorPath;
    private final String _hostName;
    private final String _driver;

    public SeleniumWindowsOperation(String windowExecutorPath, int portNumber, String hostName,String driver) {
        this._portNumber = portNumber;
        this._windowExecutorPath = windowExecutorPath;
        this._hostName = hostName;
        this._driver = driver;
    }

    public String CompeleteSyntaxToExecuteForWindows() {
        
        String[] startArgument = seleniumJavaBrowserClientStartArgumentsWindowsBased();
        if (_driver.toLowerCase().startsWith("a")) {
         startArgument =    seleniumJavaAnroidClientStartArgumentsWindowsBased();
        }        
        return String.format("%s %s", _windowExecutorPath, startArgument[0]);
    }

    private String[] seleniumJavaBrowserClientStartArgumentsWindowsBased() {
        String argumentsArray[] = {String.format("machine=%s username=%s\\%s password=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -Dwebdriver.ie.driver=\\\"%s\\\" -Dwebdriver.chrome.driver=\\\"%s\\\" -Dphantomjs.binary.path=\\\"%s\\\"\"",
            _hostName, Constants.NETWORK_DOMAIN, Constants.NETWORK_USER_NAME, Constants.NETWORK_PASSWORD, Constants.SELENIUM_JAVA_CLIENT_LOCATION,
            _portNumber, Constants.SELENIUM_IEDRIVER_LOCATION,
            Constants.SELENIUM_CHROMEDRIVER_LOCATION,
            Constants.SELENIUM_PHANTOMJS_LOCATION)};
        return argumentsArray;
    }
    
    private String[] seleniumJavaAnroidClientStartArgumentsWindowsBased() {
        String argumentsArray[] = {String.format("machine=%s username=%s\\%s password=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -selendroidServerPort %s -sessionTimeout  120 -deviceScreenshot\"",
            _hostName, Constants.NETWORK_DOMAIN, Constants.NETWORK_USER_NAME, Constants.NETWORK_PASSWORD, Constants.SELENROID_JAVA_CLIENT_LOCATION_WINDOWS,
            _portNumber, (_portNumber+1))};
        return argumentsArray;
    }   
}