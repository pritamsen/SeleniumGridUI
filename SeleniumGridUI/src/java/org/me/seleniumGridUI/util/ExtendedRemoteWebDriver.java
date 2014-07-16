/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.util;

import java.net.URL;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 *
 * @author vkumar
 */
public class ExtendedRemoteWebDriver extends RemoteWebDriver {

    String sid;

    public ExtendedRemoteWebDriver(URL remoteAddress, Capabilities desiredCapabilities, String sessionId) {
        super(remoteAddress, desiredCapabilities);
        sid = sessionId;
    }

    @Override
    public void startSession(Capabilities desiredCapabilities) {
        if (sid != null) {
            setSessionId(sid);
            try {
                getCurrentUrl();
            } catch (WebDriverException e) {
                sid = null;
            }
        }
        if (sid == null) {
            super.startSession(desiredCapabilities);
        }
    }
}