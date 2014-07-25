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

/**
 *
 * @author vkumar
 */
public class SeleniumMacOperations {

    private final int _portNumber;
    private final String _driver;
    private final String _windowExecutorPath;
    private final String _pathPuttyExe;
    private final String _hostName;
    private String _TempFile;
    
    public String getTempFile()
    {
        return _TempFile;
    }

    public SeleniumMacOperations(String windowExecutorPath, String pathPuttyExe, int portNumber, String hostName, String driver) {
        this._portNumber = portNumber;
        this._driver = driver;
        this._windowExecutorPath = windowExecutorPath;
        this._pathPuttyExe = pathPuttyExe;
        this._hostName = hostName;
    }

    private String CreateTextFileWithCommandToExecuteInMacOs() {
        return CreateATextFile(CommandToExecuteOnMac());
    }

    private String CommandToExecuteOnMac() {
        String syntax = String.format("java -jar %s -port %s", Constants.SELENIUM_JAVA_CLIENT_LOCATION_MAC, _portNumber);
        if (_driver.toLowerCase().startsWith("i")) {
            syntax = String.format("osascript -e 'tell app \"Terminal\" to do script \"java -jar %s -port %s -newSessionTimeoutSec 120 -beta\"'", Constants.IOS_Driver_JAVA_CLIENT_LOCATION_MAC, _portNumber);
        } else if (_driver.toLowerCase().startsWith("a")) {
            syntax = String.format("java -jar %s -port %s -newSessionTimeoutSec 120", Constants.SELENROID_JAVA_CLIENT_LOCATION_MAC, _portNumber);
        }
        return syntax;
    }

    private String CreateATextFile(String content) {
        String randomFileName = UUID.randomUUID().toString();
        try {
            _TempFile = String.format("%s\\%s.txt", Constants.TEMP_FILE_LOCATION_MAC, randomFileName);
            String txtFileLocationContainingMacSyntax = _TempFile;
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

    public String CompeleteSyntaxToExecuteForMac() {
        String argumentsArray[] = {String.format("machine=localhost process=cmd.exe processargs=\"/c %s -ssh %s@%s -pw %s -m %s\"",
            _pathPuttyExe, _hostName, Constants.NETWORK_USER_NAME, Constants.NETWORK_PASSWORD, CreateTextFileWithCommandToExecuteInMacOs())};
        return String.format("%s %s", _windowExecutorPath, argumentsArray[0]);
    }

    public static void DeleteTextFile(String textFileLocation) {
        File file = new File(textFileLocation);
        file.delete();
    }
}