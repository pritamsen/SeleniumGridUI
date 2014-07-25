/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.model;

public class HostDetails {

    private int port;
    private String hostName;
    private String hostAddress;
    private String operatingSystem;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostOperatingSystem() {
        return operatingSystem;
    }

    public void setHostOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
}
