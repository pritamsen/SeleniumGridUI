/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;
import org.me.seleniumGridUI.util.Constants;
import org.me.seleniumGridUI.util.MacOperationReturnValues;
import org.me.seleniumGridUI.util.MacRemoteStartCommands;
import org.me.seleniumGridUI.util.ReadUrlContent;

/**
 *
 * @author vkumar
 */
public class SeleniumMacOperations {
    
    private final int _portNumber;    
    private final String _windowExecutorPath;
    private final String _pathPuttyExe;
    private final String _hostName;
    private final MacOperationReturnValues _macOperationReturnValues;
    
        public SeleniumMacOperations(String windowExecutorPath, String pathPuttyExe, int portNumber, String hostName) {
        this._portNumber = portNumber;        
        this._windowExecutorPath = windowExecutorPath;
        this._pathPuttyExe = pathPuttyExe;
        this._hostName = hostName;
        this._macOperationReturnValues = new MacOperationReturnValues();
    }   
        
    private String CreateATextFile(String content) {
        String randomFileName = UUID.randomUUID().toString();
        try {
            String txtFileLocationContainingMacSyntax = String.format("%s\\%s.txt", Constants.MAC_TEMP_FILE_LOCATION, randomFileName);
            File statText = new File(txtFileLocationContainingMacSyntax);
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(content);
            w.close();
            return txtFileLocationContainingMacSyntax;
        } catch (IOException e) {
            System.err.println(String.format("Problem writing to the file %s.txt", randomFileName));
        }
        return null;
    }

    private String CompeleteSyntaxToExecuteForMac(String textFileLocation) {
        String argumentsArray[] = {String.format("machine=localhost process=cmd.exe processargs=\"/c %s -ssh %s@%s -pw %s -m %s\"",
            _pathPuttyExe, Constants.NETWORK_USER_NAME, _hostName, Constants.NETWORK_PASSWORD, textFileLocation)};
        return String.format("%s %s", _windowExecutorPath, argumentsArray[0]);
    }
    
    public MacOperationReturnValues CompleteSyntaxToStartJavaWebClientInMac ()
    {
        String contentTowriteInTextFile = new MacRemoteStartCommands().GetSyntaxforJavaSeleniumWebClient(_portNumber);
        String writeATextFileAndReturnLocation = CreateATextFile(contentTowriteInTextFile);
        _macOperationReturnValues.setTextFileLocation(writeATextFileAndReturnLocation);        
        _macOperationReturnValues.setSyntaxToExecute(CompeleteSyntaxToExecuteForMac(writeATextFileAndReturnLocation));
        return _macOperationReturnValues;
    }
    
    public MacOperationReturnValues CompleteSyntaxToStartIosDriverInMac ()
    {
        String contentTowriteInTextFile = new MacRemoteStartCommands().GetSyntaxforJavaIosDriver(_portNumber);
        String writeATextFileAndReturnLocation = CreateATextFile(contentTowriteInTextFile);
         _macOperationReturnValues.setTextFileLocation(writeATextFileAndReturnLocation);        
        _macOperationReturnValues.setSyntaxToExecute(CompeleteSyntaxToExecuteForMac(writeATextFileAndReturnLocation));
        return _macOperationReturnValues;
    }
    
    public MacOperationReturnValues CompleteSyntaxToQuitRunningProcess (String processName)
    {
        String contentTowriteInTextFile = new MacRemoteStartCommands().GetSyntaxToQuitRunningProcessByName(processName);
        String writeATextFileAndReturnLocation = CreateATextFile(contentTowriteInTextFile);
         _macOperationReturnValues.setTextFileLocation(writeATextFileAndReturnLocation);        
        _macOperationReturnValues.setSyntaxToExecute(CompeleteSyntaxToExecuteForMac(writeATextFileAndReturnLocation));
        return _macOperationReturnValues;
    }
    
    public MacOperationReturnValues CompleteSyntaxToOpenAppication (String applicationName)
    {
        String contentTowriteInTextFile = new MacRemoteStartCommands().GetSyntaxToOpenApplication(applicationName);
        String writeATextFileAndReturnLocation = CreateATextFile(contentTowriteInTextFile);
        _macOperationReturnValues.setTextFileLocation(writeATextFileAndReturnLocation);        
        _macOperationReturnValues.setSyntaxToExecute(CompeleteSyntaxToExecuteForMac(writeATextFileAndReturnLocation));
        return _macOperationReturnValues;
    }
    
    public MacOperationReturnValues CompleteSyntaxToForceQuitRunningProcess ( String processName)
    {
        String contentTowriteInTextFile = new MacRemoteStartCommands().GetSyntaxToForceKillRunningProcessByName(processName);
        String writeATextFileAndReturnLocation = CreateATextFile(contentTowriteInTextFile);
         _macOperationReturnValues.setTextFileLocation(writeATextFileAndReturnLocation);        
        _macOperationReturnValues.setSyntaxToExecute(CompeleteSyntaxToExecuteForMac(writeATextFileAndReturnLocation));
        return _macOperationReturnValues;
    }
    
    public MacOperationReturnValues CompleteSyntaxToChangeTheHostFile (String environemntName) throws Throwable
    {        
        ReadUrlContent readUrlContent  = new ReadUrlContent(String.format(Constants.HOST_FILE_CONTENT, environemntName.toLowerCase()));
        String contentTowriteInTextFile = new MacRemoteStartCommands().GetSyntaxToReplaceHostFile(readUrlContent.getCompleteContent());
        String writeATextFileAndReturnLocation = CreateATextFile(contentTowriteInTextFile);
         _macOperationReturnValues.setTextFileLocation(writeATextFileAndReturnLocation);        
        _macOperationReturnValues.setSyntaxToExecute(CompeleteSyntaxToExecuteForMac(writeATextFileAndReturnLocation));        
        return _macOperationReturnValues;
    } 
    
    public static void DeleteTextFile(String textFileLocation) {
        File file = new File(textFileLocation);
        file.delete();
    }
}