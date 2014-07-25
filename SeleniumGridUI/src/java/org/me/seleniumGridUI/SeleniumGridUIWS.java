/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import org.me.seleniumGridUI.model.StopSeleniumRequest;
import org.me.seleniumGridUI.model.StartSeleniumResponse;
import org.me.seleniumGridUI.model.StartSeleniumRequest;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.me.seleniumGridUI.model.HostDetails;
import org.me.seleniumGridUI.model.Response;
import org.me.seleniumGridUI.model.SeleniumResponse;
import org.me.seleniumGridUI.util.SeleniumGridHelper;
import javax.annotation.Resource;

/**
 *
 * @author vkumar
 */
@WebService(serviceName = "SeleniumGridUIWS")
public class SeleniumGridUIWS {

    @Resource
    private WebServiceContext context;

    @WebMethod(operationName = "StartSeleniumClient")
    @WebResult(name = "hostname")
    public StartSeleniumResponse StartSelenium(@WebParam(name = "clientdetails") StartSeleniumRequest client) {
        HostDetails hostDetails = SeleniumGridHelper.getHostDetails(client.getHostname());
        MessageContext msgContext = context.getMessageContext();
        ServletContext serveletContext = (ServletContext) msgContext.get(msgContext.SERVLET_CONTEXT);
        StartSeleniumResponse myResponse = new StartSeleniumResponse(hostDetails, client);
        try {
            SeleniumOperation seleniumOperation = SeleniumOperation.getInstance();
            seleniumOperation.StartJavaClientAndOpenRequestedBrowserSession(hostDetails, myResponse, serveletContext);
            myResponse.setResponse(Response.SUCCESS);
        } catch (Throwable e) {
            myResponse.setResponse(Response.FAIL);
            myResponse.setInfo(e.getLocalizedMessage());
        }
        return myResponse;
    }

    @WebMethod(operationName = "StopSeleniumClient")
    public SeleniumResponse StopSelenium(@WebParam(name = "clientdetails") StopSeleniumRequest client) {
        SeleniumResponse seleniumResponse = new SeleniumResponse();
        try {
            SeleniumOperation.getInstance().StopJavaClientAndCloseRequestedBrowserSession(client.getHostname(), client.getPort(), client.getSessionid());
            seleniumResponse.setResponse(Response.SUCCESS);
        } catch (Throwable e) {
            seleniumResponse.setResponse(Response.FAIL);
            seleniumResponse.setInfo(e.getLocalizedMessage());
        }
        return seleniumResponse;
    }
}
