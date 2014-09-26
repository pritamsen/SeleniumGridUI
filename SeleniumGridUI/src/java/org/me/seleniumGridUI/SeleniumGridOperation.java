/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import com.thoughtworks.selenium.DefaultSelenium;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import org.me.seleniumGridUI.model.HostDetails;
import org.me.seleniumGridUI.model.StartSeleniumResponse;
import org.me.seleniumGridUI.util.Constants;
import org.me.seleniumGridUI.util.ExtendedRemoteWebDriver;
import org.me.seleniumGridUI.util.HostFileChanger;
import org.me.seleniumGridUI.util.MacOperationReturnValues;
import org.me.seleniumGridUI.util.SeleniumGridHelper;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumGridOperation {

    private String MAC_TEMP_FILE;

    /**
     * Get an instance of selenium operation
     *
     * @return SeleniumGridOperation
     */
    public static SeleniumGridOperation getInstance() {
        return new SeleniumGridOperation();
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
            final SeleniumMacOperations seleniumMacOperations = new SeleniumMacOperations(windowsExecutorPath, puttyExePath, hostDetails.getPort(), hostDetails.getHostName());
            if (driver.toLowerCase().startsWith("i")) {
                MacOperationReturnValues macOperation = seleniumMacOperations.CompleteSyntaxToStartIosDriverInMac();
                windowsExecutorPath = macOperation.getSyntaxToExecute();
                MAC_TEMP_FILE= macOperation.getTextFileLocation();
                
            } else {
                MacOperationReturnValues macOperation = seleniumMacOperations.CompleteSyntaxToStartJavaWebClientInMac();
                 windowsExecutorPath = macOperation.getSyntaxToExecute();
                MAC_TEMP_FILE= macOperation.getTextFileLocation();
            }
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

    public static DesiredCapabilities CreateBrowserCapbility(String browser) {
        DesiredCapabilities caps = null;
        if (browser.equalsIgnoreCase("firefox")) {
            caps = DesiredCapabilities.firefox();
        } else if (browser.equalsIgnoreCase("chrome")) {
            caps = DesiredCapabilities.chrome();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("disable-popup-blocking");
            options.addArguments("disable-prompt-on-repost");
            options.addArguments("whitelist-ips");
            options.addArguments("no-first-run");
            options.addArguments("disk-cache-size=1");
            options.addArguments("media-cache-size=1");            
            options.addArguments("test-type");           
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
                if (ss.getOS() != null) {
                    return ss.getOS();
                } else {
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

    public void PerformCleanUp(HostDetails hostDetails, ServletContext context) throws Throwable {
        List<String> finalCommandToRun = new ArrayList<String>();
        List<String> tempFilesTodelete = new ArrayList<String>();        
        if (hostDetails.getHostOperatingSystem().equals("mac")) {
            String windowsExecutorPath = context.getRealPath("/WEB-INF/resources/executor/WindowsTaskExecutor/WindowsTaskExecutor.exe");
            String puttyExePath = context.getRealPath("/WEB-INF/resources/Putty/PUTTY.EXE");
            final SeleniumMacOperations seleniumMacOperations = new SeleniumMacOperations(windowsExecutorPath, puttyExePath, hostDetails.getPort(), hostDetails.getHostName());
            MacOperationReturnValues macOperation = seleniumMacOperations.CompleteSyntaxToQuitRunningProcess("java");
            finalCommandToRun.add(macOperation.getSyntaxToExecute());
            tempFilesTodelete.add(macOperation.getTextFileLocation());
            
            macOperation = seleniumMacOperations.CompleteSyntaxToQuitRunningProcess("Safari");
            finalCommandToRun.add(macOperation.getSyntaxToExecute());
            tempFilesTodelete.add(macOperation.getTextFileLocation());
            
            macOperation = seleniumMacOperations.CompleteSyntaxToForceQuitRunningProcess("Terminal");
            finalCommandToRun.add(macOperation.getSyntaxToExecute());
            tempFilesTodelete.add(macOperation.getTextFileLocation());
            
            macOperation = seleniumMacOperations.CompleteSyntaxToOpenAppication("Terminal");
            finalCommandToRun.add(macOperation.getSyntaxToExecute());
            tempFilesTodelete.add(macOperation.getTextFileLocation());
            
            macOperation = seleniumMacOperations.CompleteSyntaxToQuitRunningProcess("Terminal");
            finalCommandToRun.add(macOperation.getSyntaxToExecute());
            tempFilesTodelete.add(macOperation.getTextFileLocation());
        } else {
            String killRemoteProcessExePath = context.getRealPath("/WEB-INF/resources/killRemoteProcess/CleanVM.exe");
            finalCommandToRun.add(String.format("%1$s \"chrome; iexplore; firefox; java; chromedriver; IEDriverServer; cmd; WerFault; powershell\" %2$s %2$s", killRemoteProcessExePath, hostDetails.getHostName()));
        }

        for (String command : finalCommandToRun) {
            Process ps = Runtime.getRuntime().exec(command);
            ps.waitFor();
            Thread.sleep(2000);
        }

        if (hostDetails.getHostOperatingSystem().toLowerCase().startsWith("mac")) {
            for (String tempFile : tempFilesTodelete) {
                SeleniumMacOperations.DeleteTextFile(tempFile);
            }
        }
    }

    public void ReplaceHostFileInMac(HostDetails hostDetails, ServletContext context) throws Throwable {
        if (hostDetails.getHostOperatingSystem().equalsIgnoreCase("mac")) {            
            String windowsExecutorPath = context.getRealPath("/WEB-INF/resources/executor/WindowsTaskExecutor/WindowsTaskExecutor.exe");
            String puttyExePath = context.getRealPath("/WEB-INF/resources/Putty/PUTTY.EXE");
            final SeleniumMacOperations seleniumMacOperations = new SeleniumMacOperations(windowsExecutorPath, puttyExePath, hostDetails.getPort(), hostDetails.getHostName());
            MacOperationReturnValues macOperation = seleniumMacOperations.CompleteSyntaxToChangeTheHostFile(hostDetails.getEnvironemnt());
            Process ps = Runtime.getRuntime().exec(macOperation.getSyntaxToExecute());
            ps.waitFor();
            Thread.sleep(3000);
            SeleniumMacOperations.DeleteTextFile(macOperation.getTextFileLocation());
        } else {
            HostFileChanger md = new HostFileChanger(hostDetails.getHostName(), hostDetails.getEnvironemnt());
            md.ReplaceHostFile();
        }
    }
    
    
}
