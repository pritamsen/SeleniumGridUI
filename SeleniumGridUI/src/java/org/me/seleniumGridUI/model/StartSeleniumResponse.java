/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.model;

import org.me.seleniumGridUI.model.HostDetails;

/**
 *
 * @author vkumar
 */
public class StartSeleniumResponse extends SeleniumResponse{

    private String hostName;
    private int freePort;
    private String ipAddres;
    private String browser;
    private String sessionID;

    public StartSeleniumResponse(HostDetails hostDetails){
        this.hostName = hostDetails.getHostName();
        this.freePort = hostDetails.getPort();
        this.ipAddres = hostDetails.getHostAddress(); 
    }
    
    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String value) {
        this.hostName = value;
    }

    public String getIpAddres() {
        return this.ipAddres;
    }

    public void setIpAddres(String value) {
        this.ipAddres = value;
    }

    public int getFreePort() {
        return this.freePort;
    }

    public void setFreePort(int value) {
        this.freePort = value;
    }

    public void setBrowser(String value) {
        this.browser = value;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setSessionId(String value) {
        this.sessionID = value;
    }

    public String getSessionId() {
        return this.sessionID;
    }
}
