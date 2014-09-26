/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.model;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="OperationStatus")
public enum OperationStatus {

    SUCCESS, FAIL;
}
