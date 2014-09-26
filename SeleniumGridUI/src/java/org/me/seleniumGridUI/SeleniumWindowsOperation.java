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

    public SeleniumWindowsOperation(String windowExecutorPath, int portNumber, String hostName, String driver) {
        this._portNumber = portNumber;
        this._windowExecutorPath = windowExecutorPath;
        this._hostName = hostName;
        this._driver = driver;
    }

    public String CompeleteSyntaxToExecuteForWindows() {
        String[] startArgument = null;        
        if(_driver.toLowerCase().startsWith("a") && !_driver.toLowerCase().contains("app"))
            startArgument = seleniumJavaAnroidClientStartArgumentsWindowsBased(false);
        else if(_driver.toLowerCase().startsWith("a") && _driver.toLowerCase().contains("app"))
            startArgument = seleniumJavaAnroidClientStartArgumentsWindowsBased(true);
        else
            startArgument = seleniumJavaBrowserClientStartArgumentsWindowsBased();        
        return String.format("%s %s", _windowExecutorPath, startArgument[0]);
    }

    private String[] seleniumJavaBrowserClientStartArgumentsWindowsBased() {
        String argumentsArray[] = {String.format("machine=%s username=%s\\%s password=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -timeout=9999 -Dwebdriver.ie.driver=\\\"%s\\\" -Dwebdriver.chrome.driver=\\\"%s\\\" -Dphantomjs.binary.path=\\\"%s\\\"\"",
            _hostName, Constants.NETWORK_DOMAIN, Constants.NETWORK_USER_NAME, Constants.NETWORK_PASSWORD, Constants.WINDOWS_SELENIUM_JAVA_CLIENT_LOCATION,
            _portNumber, Constants.WINDOWS_SELENIUM_IEDRIVER_LOCATION,
            Constants.WINDOWS_SELENIUM_CHROMEDRIVER_LOCATION,
            Constants.WINDOWS_SELENIUM_PHANTOMJS_LOCATION)};
        return argumentsArray;
    }

    private String[] seleniumJavaAnroidClientStartArgumentsWindowsBased(Boolean appTesting) {
        String argumentsArrayWebClient[] = {String.format("machine=%s username=%s\\%s password=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -selendroidServerPort %s -sessionTimeout  120 -deviceScreenshot\"",
            _hostName, Constants.NETWORK_DOMAIN, Constants.NETWORK_USER_NAME, Constants.NETWORK_PASSWORD, Constants.ANDROID_SELENROID_CLIENT_LOCATION,
            _portNumber, (_portNumber + 1))};

        String argumentsArrayAppClient[] = {String.format("machine=%s username=%s\\%s password=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -selendroidServerPort %s -sessionTimeout  120 -deviceScreenshot -app \\\"%s\\\"\"",
            _hostName, Constants.NETWORK_DOMAIN, Constants.NETWORK_USER_NAME, Constants.NETWORK_PASSWORD, Constants.ANDROID_SELENROID_CLIENT_LOCATION,
            _portNumber, (_portNumber + 1), Constants.ANDROID_SELENROID_AUT_LOCATION)};
      
        if (appTesting) 
            return  argumentsArrayAppClient;
        return argumentsArrayWebClient;
    }
}
