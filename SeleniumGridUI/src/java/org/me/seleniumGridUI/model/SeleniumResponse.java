/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.model;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author dhayad
 */
@XmlType(name="SeleniumResponse")
public class SeleniumResponse {

    private OperationStatus response;
    private String info = "Selenium Response";

    public OperationStatus getResponse() {
        return response;
    }

    public void setResponse(OperationStatus response) {
        this.response = response;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
