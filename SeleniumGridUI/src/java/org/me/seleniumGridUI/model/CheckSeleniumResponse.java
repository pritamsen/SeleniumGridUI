/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vkumar
 */
public class CheckSeleniumResponse extends SeleniumResponse{
    
    private String sessionID;
    private String state;
    
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
}
