/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import com.thoughtworks.selenium.DefaultSelenium;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.me.seleniumGridUI.model.HostDetails;
import org.me.seleniumGridUI.model.StartSeleniumResponse;
import org.me.seleniumGridUI.util.Constants;
import org.me.seleniumGridUI.util.ExtendedRemoteWebDriver;
import static org.me.seleniumGridUI.util.JsonReader.readJsonFromUrl;
import org.me.seleniumGridUI.util.SeleniumGridHelper;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumOperation {

    private String MAC_TEMP_FILE;

    /**
     * Get an instance of selenium operation
     *
     * @return SeleniumOperation
     */
    public static SeleniumOperation getInstance() {
        return new SeleniumOperation();
    }

    /**
     *
     * @param response
     * @return sessionId
     * @throws Throwable
     */
    private String startBrowser(StartSeleniumResponse response) throws Throwable {
        final String hostname = response.getHostName();
        final int port = response.getFreePort();

        String remoteClientUrl = String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_FORMAT, hostname, port);

        WebDriver driver = new RemoteWebDriver(new URL(remoteClientUrl), CreateBrowserCapbility(response.getBrowser()));

        if (driver == null) {
            return "NO_WEB_DRIVER";
        }
        return ((RemoteWebDriver) driver).getSessionId().toString();
    }

    public void StartJavaClientAndOpenRequestedBrowserSession(HostDetails hostDetails, StartSeleniumResponse response, ServletContext serveletContext) throws Throwable {
        String os = "unknown os";
        if (SeleniumGridHelper.isValidBrowserParam(response.getBrowser())) {
            os = startExecutor(hostDetails, response.getBrowser(), serveletContext);
            hostDetails.setHostOperatingSystem(os);
            response.setOs(os);
            response.setBrowser(response.getBrowser());
            if (response.getBrowser().toLowerCase().startsWith("ip") || response.getBrowser().toLowerCase().startsWith("a")) {
                return;
            }
            String session = startBrowser(response);
            response.setSessionId(session);
        } else {
            os = startExecutor(hostDetails, "", serveletContext);
            hostDetails.setHostOperatingSystem(os);
            response.setOs(os);
        }

        if (hostDetails.getHostOperatingSystem().toLowerCase().startsWith("mac")) {
            SeleniumMacOperations.DeleteTextFile(MAC_TEMP_FILE);
        }
    }

    /**
     * Start executor using host details, the method wait till the process gets
     * terminated
     *
     * @param hostDetails
     * @param context
     * @return
     * @throws Throwable
     */
    private String startExecutor(HostDetails hostDetails, String driver, ServletContext context) throws Throwable {
        String windowsExecutorPath = context.getRealPath("/WEB-INF/resources/executor/WindowsTaskExecutor/WindowsTaskExecutor.exe");
        String puttyExePath = context.getRealPath("/WEB-INF/resources/Putty/PUTTY.EXE");

        if (hostDetails.getHostOperatingSystem().equals("mac")) {
            final SeleniumMacOperations seleniumMacOperations = new SeleniumMacOperations(windowsExecutorPath, puttyExePath, hostDetails.getPort(), hostDetails.getHostName(), driver);
            windowsExecutorPath = seleniumMacOperations.CompeleteSyntaxToExecuteForMac();
            MAC_TEMP_FILE = seleniumMacOperations.getTempFile();
        } else {
            windowsExecutorPath = new SeleniumWindowsOperation(windowsExecutorPath, hostDetails.getPort(), hostDetails.getHostName(), driver).CompeleteSyntaxToExecuteForWindows();
        }
        Process ps = Runtime.getRuntime().exec(windowsExecutorPath);
        ps.waitFor();
        return IsJavaClientStartedFully(hostDetails.getHostName(), hostDetails.getPort());
    }

    /**
     * Stops the remote java client based on provided host information
     *
     * @param hostname
     * @param portNumber
     * @param sessionId
     * @throws Throwable
     */
    public void StopJavaClientAndCloseRequestedBrowserSession(String hostname, int portNumber, String sessionId) throws Throwable {
        if (SeleniumGridHelper.isValidSessionParam(sessionId)) {
            try {
                String remoteClientUrl = String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_FORMAT, hostname, portNumber);
                DesiredCapabilities dummyCap = CreateBrowserCapbility("");
                ExtendedRemoteWebDriver driver = new ExtendedRemoteWebDriver(new URL(remoteClientUrl), dummyCap, sessionId);
                driver.startSession(dummyCap);
                driver.quit();
            } catch (Throwable e) {
                throw e;
            } finally {
                StopSeleniumJavaClient(hostname, portNumber);
            }
        } else {            
            StopSeleniumJavaClient(hostname, portNumber);

        }
    }   
    

    private void StopSeleniumJavaClient(String hostname, int portNumber) throws Throwable {        
        URL url = new URL(String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_SHUTDOWN, hostname, portNumber));
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.getResponseCode();
    }

    private DefaultSelenium InitializeJavaBackWardSelenium(String hostname, int portNumber) {
        return new DefaultSelenium(hostname, portNumber, "*webdriver", "localhost");
    }

    private static DesiredCapabilities CreateBrowserCapbility(String browser) {
        DesiredCapabilities caps = null;
        if (browser.equalsIgnoreCase("firefox")) {
            caps = DesiredCapabilities.firefox();
        } else if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("disable-popup-blocking");
            options.addArguments("disable-prompt-on-repost");
            options.addArguments("no-first-run");
            options.addArguments("proxy-auto-detect");
            options.addArguments("test-type");
            caps = DesiredCapabilities.chrome();
            caps.setCapability(ChromeOptions.CAPABILITY, options);
        } else if (browser.equalsIgnoreCase("ie")) {
            caps = DesiredCapabilities.internetExplorer();
            caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            caps.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
            caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            caps.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
            caps.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
            caps.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, UnexpectedAlertBehaviour.DISMISS);
        } else if (browser.equalsIgnoreCase("phantomjs")) {
            caps = DesiredCapabilities.phantomjs();
        } else if (browser.equalsIgnoreCase("safari")) {
            caps = DesiredCapabilities.safari();
        } else if (browser.equalsIgnoreCase("iphone")) {
            caps = DesiredCapabilities.iphone();
        } else if (browser.equalsIgnoreCase("ipad")) {
            caps = DesiredCapabilities.ipad();
        } else {
            caps = DesiredCapabilities.htmlUnit();
        }
        return caps;
    }

    private String IsJavaClientStartedFully(String hostname, int portNumber) throws Throwable {
        int retyTimes = 5;
        int delayTime = 2000;        
        int i = 0;
        for (i = 0; i < retyTimes; i++) {
            try {                
                SeleniumStatus ss = new SeleniumStatus(hostname, portNumber);
                if(ss.getOS() != null)
                {
                  return ss.getOS();
                }               
                 else {
                    throw new Exception("Empty os");
                }
            } catch (Exception e) {
                if (i == retyTimes) {
                    throw new Exception(String.format("%s attempts to retry failed at %s ms interval becoz %s", retyTimes, delayTime, e.getMessage()));
                }
                Thread.sleep(delayTime);
            }
        }
        return null;
    }
    
     public void PerformCleanUp(String hostName,  ServletContext context) throws Throwable{
         String killRemoteProcessExePath = context.getRealPath("/WEB-INF/resources/killRemoteProcess/CleanVM.exe");
         String finalPath = String.format("%s \"chrome; iexplore; firefox; java; chromedriver; IEDriverServer; cmd; WerFault; powershell\" %s %s", killRemoteProcessExePath, hostName, hostName);
         Process ps = Runtime.getRuntime().exec(finalPath);
         ps.waitFor();
    } 
}
