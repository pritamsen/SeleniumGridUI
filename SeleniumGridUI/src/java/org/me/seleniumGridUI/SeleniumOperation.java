/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import com.thoughtworks.selenium.DefaultSelenium;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.servlet.ServletContext;
import org.me.seleniumGridUI.model.HostDetails;
import org.me.seleniumGridUI.model.StartSeleniumResponse;
import org.me.seleniumGridUI.util.Constants;
import org.me.seleniumGridUI.util.ExtendedRemoteWebDriver;
import org.me.seleniumGridUI.util.SeleniumGridHelper;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumOperation {

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
    public String startBrowser(StartSeleniumResponse response) throws Throwable {
        final String hostname = response.getHostName();
        final int port = response.getFreePort();
        IsJavaClientStartedFully(hostname, port);
        String remoteClientUrl = String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_FORMAT, hostname, port);
        WebDriver driver = new RemoteWebDriver(new URL(remoteClientUrl), CreateBrowserCapbility(response.getBrowser()));

        if (driver == null) {
            return "NO_WEB_DRIVER";
        }
        return ((RemoteWebDriver) driver).getSessionId().toString();
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
    public int startExecutor(HostDetails hostDetails, ServletContext context) throws Throwable {
        String windowsExecutorPath = context.getRealPath("/WEB-INF/resources/executor/WindowsTaskExecutor/WindowsTaskExecutor.exe");
        //String windowsExecutorPath = "S:\\QATestTools\\AS24.SeleniumScheduler\\AS24.SeleniumScheduler\\bin\\Debug\\WindowsTaskExecutor.exe";
        String puttyExePath = context.getRealPath("/WEB-INF/resources/Putty/PUTTY.EXE");

        if (hostDetails.getHostOperatingSystem().equals("mac")) {
            String[] path = SeleniumClientStartArgumentsMacBased(puttyExePath, hostDetails.getHostName(), CreateTextFileWithseleniumClientStartCommandsInMac(hostDetails.getPort()));
            windowsExecutorPath = String.format("%s %s", windowsExecutorPath, path[0]);

        } else {
            windowsExecutorPath = StartArgumentsForSeleniumJavaClientWindows(windowsExecutorPath, hostDetails.getHostName(), hostDetails.getPort());
        }

        Process ps = Runtime.getRuntime().exec(windowsExecutorPath);
        return ps.waitFor();
    }

    private String StartArgumentsForSeleniumJavaClientWindows(String pathWindowsExecutor, String hostName, int port) {
        String[] startArgument = seleniumClientStartArgumentsWindowsBased(hostName, port);
        return String.format("%s %s", pathWindowsExecutor, startArgument[0]);
    }

    /**
     * Stops the remote java client based on provided host information
     *
     * @param hostname
     * @param portNumber
     * @param sessionId
     * @throws Throwable
     */
    public void stopJavaClient(String hostname, int portNumber, String sessionId) throws Throwable {
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

    private void StopSeleniumJavaClient(String hostname, int portNumber) {
        DefaultSelenium runningSeleniumClient = InitializeJavaBackWardSelenium(hostname, portNumber);
        runningSeleniumClient.shutDownSeleniumServer();
    }

    private DefaultSelenium InitializeJavaBackWardSelenium(String hostname, int portNumber) {
        return new DefaultSelenium(hostname, portNumber, "*webdriver", "localhost");
    }

    private String[] seleniumClientStartArgumentsWindowsBased(String machineName, int portNumber) {
        String argumentsArray[] = {String.format("machine=%s process=java processargs=\" -jar \\\"%s\\\" -port %s -Dwebdriver.ie.driver=\\\"%s\\\" -Dwebdriver.chrome.driver=\\\"%s\\\" -Dphantomjs.binary.path=\\\"%s\\\"\"",
            machineName, Constants.SELENIUM_JAVA_CLIENT_LOCATION,
            portNumber, Constants.SELENIUM_IEDRIVER_LOCATION,
            Constants.SELENIUM_CHROMEDRIVER_LOCATION,
            Constants.SELENIUM_PHANTOMJS_LOCATION)};
        return argumentsArray;
    }

    private String[] SeleniumClientStartArgumentsMacBased(String pathPuttyExe, String machineName, String textFileLocationWithCommandToExecuteOnMac) {
        String argumentsArray[] = {String.format("machine=localhost process=cmd.exe processargs=\"/c %s -ssh zzqaadmin@%s -pw as24test -m %s\"",
            pathPuttyExe, machineName, textFileLocationWithCommandToExecuteOnMac)};
        return argumentsArray;
    }

    private String CreateTextFileWithseleniumClientStartCommandsInMac(int portNumber) {
        String syntax = String.format("java -jar %s -port %s -beta", Constants.SELENIUM_JAVA_CLIENT_LOCATION_MAC, portNumber);
        String randomFileName = UUID.randomUUID().toString();
        try {
            //What ever the file path is.
            String txtFileLocationContainingMacSyntax = String.format("C:\\Temp\\%s.txt", randomFileName);
            File statText = new File(txtFileLocationContainingMacSyntax);
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(syntax);
            w.close();
            return txtFileLocationContainingMacSyntax;
        } catch (IOException e) {
            System.err.println(String.format("Problem writing to the file %s.txt", randomFileName));
        }
        return null;
    }

    public static DesiredCapabilities CreateBrowserCapbility(String browser) {
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
        } else {
            caps = DesiredCapabilities.htmlUnit();
        }
        return caps;
    }

    private void IsJavaClientStartedFully(String hostname, int portNumber) throws Throwable {
        int retyTimes = 5;
        int delayTime = 2000;
        URL url = new URL(String.format("http://%s:%s/wd/hub", hostname, portNumber));
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        int i = 0;
        for (i = 0; i < retyTimes; i++) {
            try {
                final int status = http.getResponseCode();
                if (status != 200) {
                    throw new Exception(String.format("The status is now %s", status));
                }
            } catch (Exception e) {
                if (i == retyTimes) {
                    throw new Exception(String.format("%s attempts to retry failed at %s ms interval", retyTimes, delayTime));
                }
                Thread.sleep(delayTime);
            }
        }

    }
}
