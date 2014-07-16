/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI;

import com.thoughtworks.selenium.DefaultSelenium;
import java.net.URL;
import org.me.seleniumGridUI.model.HostDetails;
import org.me.seleniumGridUI.model.StartSeleniumResponse;
import org.me.seleniumGridUI.util.Constants;
import org.me.seleniumGridUI.util.SeleniumGridHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumOperation {
    
    /**
     * Get an instance of selenium operation
     * @return SeleniumOperation
     */
    public static SeleniumOperation getInstance(){
        return new SeleniumOperation();
    }
    
    
    /**
     * 
     * @param response
     * @return sessionId
     * @throws Throwable 
     */
    public String startBrowser(StartSeleniumResponse response) throws Throwable{
        String remoteClientUrl = String.format("http://%s:%s/wd/hub", response.getHostName(), response.getFreePort());        
        WebDriver driver = new RemoteWebDriver(new URL(remoteClientUrl), SeleniumGridHelper.createBrowserCapbility(response.getBrowser()));

        if (driver == null) {
            return "NO_WEB_DRIVER";
        }
        return ((RemoteWebDriver) driver).getSessionId().toString();
    }
    
    /**
     * Start executor using host details, the method wait till the process gets terminated
     * @param hostDetails
     * @return
     * @throws Throwable 
     */
    public int startExecutor(HostDetails hostDetails) throws Throwable {
        String path = "S:/QATestTools/AS24.SeleniumScheduler/AS24.SeleniumScheduler/bin/Debug/WindowsTaskExecutor.exe";
        String[] startArgument = seleniumClientStartArguments(hostDetails.getHostName(), hostDetails.getPort());
        path = String.format("%s %s", path, startArgument[0]);
        Process ps = Runtime.getRuntime().exec(path);
        return ps.waitFor();
    }
    
    /**
     * Stops the remote java client based on provided host information
     * @param hostname
     * @param portNumber
     * @param sessionId
     * @throws Throwable 
     */
    public void stopJavaClient(String hostname, int portNumber, String sessionId) throws Throwable{
        DefaultSelenium runningSeleniumClient = new DefaultSelenium(hostname, portNumber, "*webdriver", "localhost");
        if (sessionId != null && sessionId.length() != 0) {
            try {
                runningSeleniumClient.start("webdriver.remote.sessionid=" + sessionId);
            } catch (Throwable e) {
                throw e;
            }finally{
                runningSeleniumClient.shutDownSeleniumServer();
            }
        }
    }
    
    private String[] seleniumClientStartArguments(String machineName, int portNumber) {
        String argumentsArray[] = {String.format("machine=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -Dwebdriver.ie.driver=\\\"%s\\\" -Dwebdriver.chrome.driver=\\\"%s\\\" -Dphantomjs.binary.path=\\\"%s\\\"\"", 
                machineName, Constants.SELENIUM_JAVA_CLIENT_LOCATION, 
                portNumber, Constants.SELENIUM_IEDRIVER_LOCATION, 
                Constants.SELENIUM_CHROMEDRIVER_LOCATION, 
                Constants.SELENIUM_PHANTOMJS_LOCATION)};
        return argumentsArray;
    }
}
