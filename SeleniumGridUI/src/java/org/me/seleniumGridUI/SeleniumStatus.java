/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.me.seleniumGridUI;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.me.seleniumGridUI.util.Constants;
import static org.me.seleniumGridUI.util.JsonReader.readJsonFromUrl;

/**
 *
 * @author vkumar
 */
public class SeleniumStatus{

    private String url;
       
    public SeleniumStatus(String hostname, int portNumber) throws Throwable{        
        url = String.format(Constants.SELENIUM_REMOTE_WEBDRIVER_URL_STATUS, hostname, portNumber);        
    }
          
    public String getOS() throws Throwable {
        JSONObject seleniumJsonObject = readJsonFromUrl(url); 
        JSONObject value = (JSONObject) seleniumJsonObject.get("value");
        JSONObject osObject = (JSONObject) value.get("os");
        String os = osObject.get("name").toString(); 
        if (os != null && !os.isEmpty()) 
            return os;
        return null;
    }
    
    public String getSessionId() throws Throwable{
        JSONObject seleniumJsonObject = readJsonFromUrl(url); 
        Object session = seleniumJsonObject.get("sessionId");
        String sessionId = session.toString();
         if (sessionId != null && !sessionId.isEmpty()) 
             return sessionId;
         return null;            
    }
    
    public String getState() throws Throwable{
        JSONObject seleniumJsonObject = readJsonFromUrl(url); 
        Object state = seleniumJsonObject.get("state");
        String stateId = state.toString();
         if (stateId != null && !stateId.isEmpty()) 
             return stateId;
         return null;            
    }
    
    public String getStatus() throws Throwable{
        JSONObject seleniumJsonObject = readJsonFromUrl(url); 
        Object status = seleniumJsonObject.get("status");
        String statusId = status.toString();
         if (statusId != null && !statusId.isEmpty()) 
             return statusId;
         return null;            
    }
            
}
