/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vkumar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "StartSeleniumClientRequest")
/**
 *
 * @author vkumar
 */
public class CleanClientRequest implements Serializable{
    
    @XmlElement(name = "hostname", required = true)
    protected String hostname;
    
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
}
