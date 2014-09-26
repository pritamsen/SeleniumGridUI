/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.me.seleniumGridUI.util.Constants;
import org.me.seleniumGridUI.util.ExtendedRemoteWebDriver;
import static org.me.seleniumGridUI.util.JsonReader.readJsonFromUrl;
import org.me.seleniumGridUI.util.SeleniumGridHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 *
 * @author vkumar
 */
public class SeleniumStatus {

    private String statusUrl;
    private String hubUrl;
    private String currentSession;

    public SeleniumStatus(String hostname, int portNumber) throws Throwable {
        statusUrl = String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_STATUS, hostname, portNumber);
        hubUrl = String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_FORMAT, hostname, portNumber);
    }

    public SeleniumStatus(String hostname, int portNumber, String sessionId) throws Throwable {
        this(hostname, portNumber);
        currentSession = sessionId;
    }

    public String getOS() throws Throwable {
        JSONObject seleniumJsonObject = readJsonFromUrl(statusUrl);
        JSONObject value = (JSONObject) seleniumJsonObject.get("value");
        JSONObject osObject = (JSONObject) value.get("os");
        String os = osObject.get("name").toString();
        if (os != null && !os.isEmpty()) {
            return os;
        }
        return null;
    }

    public String getSessionId() throws Throwable {
        JSONObject seleniumJsonObject = readJsonFromUrl(statusUrl);
        Object session = seleniumJsonObject.get("sessionId");
        String sessionId = session.toString();
        if (sessionId != null && !sessionId.isEmpty()) {
            return sessionId;
        }
        return null;
    }

    public String getState() throws Throwable {
        JSONObject seleniumJsonObject = readJsonFromUrl(statusUrl);
        Object state = seleniumJsonObject.get("state");
        String stateId = state.toString();
        if (stateId != null && !stateId.isEmpty()) {
            return stateId;
        }
        return null;
    }

    private String getSupportedApps() throws Throwable {
        try {
            JSONObject seleniumJsonObject = readJsonFromUrl(statusUrl);
            JSONObject value = (JSONObject) seleniumJsonObject.get("value");
            JSONArray  supportedAppsObject = (JSONArray) value.get("supportedApps");
            String suppoterdAppsAsString = supportedAppsObject.toString();
            if (suppoterdAppsAsString != null && !suppoterdAppsAsString.isEmpty()) {
                return suppoterdAppsAsString;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getStatus() throws Throwable {
        JSONObject seleniumJsonObject = readJsonFromUrl(statusUrl);
        Object status = seleniumJsonObject.get("status");
        String statusId = status.toString();
        if (statusId != null && !statusId.isEmpty()) {
            return statusId;
        }
        return null;
    }

    public boolean IsThisDeviceTesting() throws Throwable {
        String value = getSupportedApps();
        return (value != null && !value.isEmpty()) ? true : false;
    }

    public String getEnvironment() throws Throwable {
        if (IsThisDeviceTesting()) {
            return StringUtils.EMPTY;
        }
        Map<String, String> myMap = new HashMap<String, String>();
        SeleniumGridOperation seleniumOperation = SeleniumGridOperation.getInstance();
        WebDriver driver = new RemoteWebDriver(new URL(hubUrl), seleniumOperation.CreateBrowserCapbility("phantomjs"));
        driver.get("http://www.autoscout24.de/build.txt");
        String siteContent = driver.findElement(By.tagName("pre")).getText();
        if (driver != null) {
            driver.quit();
        }
        String[] elements = siteContent.split("\\n");
        for (String s1 : elements) {
            s1 = s1.replaceAll("\\s", "");
            String[] keyValue = s1.split(":");
            myMap.put(keyValue[0], keyValue[1]);
        }
        return myMap.get("Environment");
    }

    public Boolean getCurrentSessionStatus() throws Throwable {
        if (IsThisDeviceTesting()) {
            return true;
        }
        if (!SeleniumGridHelper.isValidSessionParam(currentSession)) {
            return true;
        }
        SeleniumGridOperation seleniumOperation = SeleniumGridOperation.getInstance();
        DesiredCapabilities dummyCap = seleniumOperation.CreateBrowserCapbility("");
        ExtendedRemoteWebDriver driver = new ExtendedRemoteWebDriver(new URL(hubUrl), dummyCap, currentSession);
        driver.startSession(dummyCap);
        JavascriptExecutor js = null;
        if (driver instanceof JavascriptExecutor) {
            js = (JavascriptExecutor) driver;
        }
        Object sessionStatus = js.executeScript("return document.readyState === 'complete';");

        if (sessionStatus instanceof Boolean) {
            return (Boolean) sessionStatus;
        }
        return false;
    }
}
