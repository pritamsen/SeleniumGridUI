/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI.util;

/**
 *
 * @author vkumar
 */
public class MacOperationReturnValues {
    
    private String macSyntaxTextFileLocation;
    private String macSyntaxToExecute;
    
   public String getTextFileLocation() {
        return macSyntaxTextFileLocation;
    }

    public void setTextFileLocation(String value) {
        this.macSyntaxTextFileLocation = value;
    }
    
    public String getSyntaxToExecute() {
        return macSyntaxToExecute;
    }

    public void setSyntaxToExecute(String value) {
        this.macSyntaxToExecute = value;
    }
    
}
