/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.model;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author vkumar
 */
@XmlType(name="CheckSeleniumResponse")
public class CheckSeleniumResponse extends SeleniumResponse{
    
    private String sessionID;
    private String state;
    private String Status;
    private String environment;
    private Boolean sessionStatus;
    private String operatingSystem;
    
    public void setOS(String value) {
        this.operatingSystem = value;
    }

    public String getOS() {
        return this.operatingSystem;
    }
    
    public void setSessionId(String value) {
        this.sessionID = value;
    }

    public String getSessionId() {
        return this.sessionID;
    }
    
    public void setState(String value) {
        this.state = value;
    }

    public String getState() {
        return this.state;
    }
    
    public void setEnvironment(String value) {
        this.environment = value;
    }

    public String getEnvironment() {
        return this.environment;
    }
    
    public void setStatus(String value) {
        this.Status = value;
    }

    public String getStatus() {
        return this.Status;
    }
    
     public void setSessionStatus(Boolean value) {
        this.sessionStatus = value;
    }

    public Boolean getSessionStatus() {
        return this.sessionStatus;
    }
}
