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
    public StartSeleniumResponse StartSelenium(@WebParam(name = "clientdetails") StartSeleniumRequest client){
        HostDetails hostDetails = SeleniumGridHelper.getHostDetails(client.getHostname());
        ServletContext servletContext =
                (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        //context.getResourceAsStream("/WEB-INF/file1.xml");  
        StartSeleniumResponse myResponse = new StartSeleniumResponse(hostDetails);
        try {
            SeleniumOperation seleniumOperation = SeleniumOperation.getInstance();
            seleniumOperation.startExecutor(hostDetails);
            if (SeleniumGridHelper.isValidBrowserParam(client.getBrowser())) {
                myResponse.setBrowser(client.getBrowser());
                String session = seleniumOperation.startBrowser(myResponse);
                myResponse.setSessionId(session);
            }
            myResponse.setResponse(Response.SUCCESS);
        } catch(Throwable e){
            myResponse.setResponse(Response.FAIL);
            myResponse.setInfo(e.getLocalizedMessage());
        }
        return myResponse;
    }
    
    @WebMethod(operationName = "StopSeleniumClient")    
    public SeleniumResponse StopSelenium(@WebParam(name = "clientdetails") StopSeleniumRequest client){
        SeleniumResponse seleniumResponse = new SeleniumResponse();
        try {
            SeleniumOperation.getInstance().stopJavaClient(client.getHostname(), client.getPort(), client.getSessionid());
            seleniumResponse.setResponse(Response.SUCCESS);
        }catch(Throwable e){
            seleniumResponse.setResponse(Response.FAIL);
            seleniumResponse.setInfo(e.getLocalizedMessage());
        }
        return seleniumResponse;
    }    
}
