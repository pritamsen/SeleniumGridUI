/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.me.seleniumGridUI.model.ChangeHostFileRequest;
import org.me.seleniumGridUI.model.CheckSeleniumRequest;
import org.me.seleniumGridUI.model.CheckSeleniumResponse;
import org.me.seleniumGridUI.model.CleanClientRequest;
import org.me.seleniumGridUI.model.HostDetails;
import org.me.seleniumGridUI.model.OperationStatus;
import org.me.seleniumGridUI.model.SeleniumResponse;
import org.me.seleniumGridUI.model.StartSeleniumRequest;
import org.me.seleniumGridUI.model.StartSeleniumResponse;
import org.me.seleniumGridUI.model.StopSeleniumRequest;
import org.me.seleniumGridUI.util.SeleniumGridHelper;

/**
 *
 * @author vkumar
 */
@WebService(serviceName = "SeleniumGridUIWS")
public class SeleniumGridUIWS {

    @Resource
    private WebServiceContext context;

    @WebMethod(operationName = "StartSeleniumClient")
    public StartSeleniumResponse StartSelenium(@WebParam(name = "clientdetails") StartSeleniumRequest client) {
        HostDetails hostDetails = SeleniumGridHelper.getHostDetails(client.getHostname());
        StartSeleniumResponse myResponse = new StartSeleniumResponse(hostDetails, client);
        if (hostDetails == null) {
            myResponse.setResponse(OperationStatus.FAIL);
            myResponse.setInfo(String.format("The Machine '%s' doesn't exist in network", client.getHostname()));
        } else {
            MessageContext msgContext = context.getMessageContext();
            ServletContext serveletContext = (ServletContext) msgContext.get(msgContext.SERVLET_CONTEXT);
            try {
                SeleniumGridOperation seleniumOperation = SeleniumGridOperation.getInstance();
                seleniumOperation.StartJavaClientAndOpenRequestedBrowserSession(hostDetails, myResponse, serveletContext);
                myResponse.setResponse(OperationStatus.SUCCESS);
            } catch (Throwable e) {
                myResponse.setResponse(OperationStatus.FAIL);
                myResponse.setInfo(e.getLocalizedMessage());
            }
        }
        return myResponse;
    }

    @WebMethod(operationName = "StopSeleniumClient")
    public SeleniumResponse StopSelenium(@WebParam(name = "clientdetails") StopSeleniumRequest client) {
        SeleniumResponse seleniumResponse = new SeleniumResponse();
        try {
            SeleniumGridOperation.getInstance().StopJavaClientAndCloseRequestedBrowserSession(client.getHostname(), client.getPort(), client.getSessionid());
            seleniumResponse.setResponse(OperationStatus.SUCCESS);
        } catch (Throwable e) {
            seleniumResponse.setResponse(OperationStatus.FAIL);
            seleniumResponse.setInfo(e.getLocalizedMessage());
        }
        return seleniumResponse;
    }

    @WebMethod(operationName = "CheckSeleniumClient")
    public CheckSeleniumResponse CheckSelenium(@WebParam(name = "clientdetails") CheckSeleniumRequest client) {
        CheckSeleniumResponse checkSeleniumResponse = new CheckSeleniumResponse();
        try {
            SeleniumStatus seleniumStatus = new SeleniumStatus(client.getHostname(), client.getPort(), client.getSessionid());
            //checkSeleniumResponse.setSessionId(seleniumStatus.getSessionId());                    
            //checkSeleniumResponse.setState(seleniumStatus.getState());
            checkSeleniumResponse.setOS(seleniumStatus.getOS());
            checkSeleniumResponse.setStatus(seleniumStatus.getStatus());

            checkSeleniumResponse.setEnvironment(seleniumStatus.getEnvironment());
            checkSeleniumResponse.setSessionStatus(seleniumStatus.getCurrentSessionStatus());
            checkSeleniumResponse.setResponse(OperationStatus.SUCCESS);
        } catch (Throwable e) {
            checkSeleniumResponse.setResponse(OperationStatus.FAIL);
            checkSeleniumResponse.setInfo(e.getLocalizedMessage());
        }
        return checkSeleniumResponse;
    }

    @WebMethod(operationName = "CleanClientMachine")
    public SeleniumResponse CleanHostMachine(@WebParam(name = "clientdetails") CleanClientRequest client) {
        SeleniumResponse myResponse = new SeleniumResponse();
        HostDetails hostDetails = SeleniumGridHelper.getHostDetails(client.getHostname());
        if (hostDetails == null) {
            myResponse.setResponse(OperationStatus.FAIL);
            myResponse.setInfo(String.format("The Machine %s doesn't exist in network", client.getHostname()));
        } else {
            MessageContext msgContext = context.getMessageContext();
            ServletContext serveletContext = (ServletContext) msgContext.get(msgContext.SERVLET_CONTEXT);
            try {
                SeleniumGridOperation seleniumOperation = SeleniumGridOperation.getInstance();
                seleniumOperation.PerformCleanUp(hostDetails, serveletContext);
                myResponse.setResponse(OperationStatus.SUCCESS);
            } catch (Throwable e) {
                myResponse.setResponse(OperationStatus.FAIL);
                myResponse.setInfo(e.getLocalizedMessage());
            }
        }
        return myResponse;
    }

    @WebMethod(operationName = "ChangeHostFile")
    public SeleniumResponse ChangeHostFileOfHostMachine(@WebParam(name = "clientdetails") ChangeHostFileRequest client) {
        SeleniumResponse myResponse = new SeleniumResponse();
        HostDetails hostDetails = SeleniumGridHelper.getHostDetails(client.getHostname());
        if (hostDetails == null) {
            myResponse.setResponse(OperationStatus.FAIL);
            myResponse.setInfo(String.format("The Machine %s doesn't exist in network", client.getHostname()));
        } else {
            hostDetails.setEnvironemnt(client.getEnvironment());
            MessageContext msgContext = context.getMessageContext();
            ServletContext serveletContext = (ServletContext) msgContext.get(msgContext.SERVLET_CONTEXT);
            try {
                SeleniumGridOperation seleniumOperation = SeleniumGridOperation.getInstance();
                seleniumOperation.ReplaceHostFileInMac(hostDetails, serveletContext);
                myResponse.setResponse(OperationStatus.SUCCESS);
            } catch (Throwable e) {
                myResponse.setResponse(OperationStatus.FAIL);
                myResponse.setInfo(e.getLocalizedMessage());
            }
        }
        return myResponse;
    }
}
