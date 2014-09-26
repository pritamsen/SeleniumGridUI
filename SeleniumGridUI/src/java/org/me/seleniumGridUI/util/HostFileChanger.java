/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.seleniumGridUI.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author vkumar
 */
public class HostFileChanger {    
    
    private final String _remoteMachine;
    private final String _envi;
    private final ReadUrlContent _readUrlContent;
    
    public HostFileChanger(String remoteMachineName, String environment)
    {
        _remoteMachine = remoteMachineName;
        _envi = environment.toLowerCase();
        _readUrlContent  = new ReadUrlContent(String.format(Constants.HOST_FILE_CONTENT, _envi));               
    }
    
    private List<String> GetAllDriveLettersOfHostMachine() {
        List<String> existingDrives = new ArrayList<String>();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] roots = fsv.getRoots();       
        File[] f = File.listRoots();
        for (int i = 0; i < f.length; i++){
            String dummy = f[i].toString().replaceAll("\\W", "").toLowerCase();
            existingDrives.add(dummy);
        }
        return existingDrives;
        
    }
    
    private String GetFreeDriveLetterOfHostmachine()    {
        List<String> alphabetList = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"));  
        List<String>  existingDrives = GetAllDriveLettersOfHostMachine();   
        for (int i = 0; i < existingDrives.size(); i++)
        {
            String temp = existingDrives.get(i);
            alphabetList.remove(temp);
        }
        return alphabetList.get(alphabetList.size() - 1);
    }
    
    private String MountClientHostFileLocationToServer() throws Throwable
    {
        String freeDriverLetterToMount = GetFreeDriveLetterOfHostmachine().toUpperCase();
        String command = String.format("%s use %s: \\\\%s%s /user:%s\\%s %s", Constants.WINDOWS_HOST_SYSTEM_NET_LOCATION,
                                                                                                freeDriverLetterToMount,
                                                                                                            _remoteMachine, 
                                                                                                                Constants.WINDOWS_HOST_FILE_LOCATION, 
                                                                                                                    Constants.NETWORK_DOMAIN, 
                                                                                                                        Constants.NETWORK_USER_NAME, 
                                                                                                                            Constants.NETWORK_PASSWORD);
        for(int i = 0; i < 5; i++)
        {         
            Runtime.getRuntime().exec(command);         
            List<String>  existingDrives = GetAllDriveLettersOfHostMachine(); 
            if(existingDrives.contains(freeDriverLetterToMount.toLowerCase()))
            {
                File file = new File(freeDriverLetterToMount+":\\hosts");  
                if(file.exists())
                    break;
                else
                    Thread.sleep(2000);
            }
            else
                    Thread.sleep(2000);
        }
        return freeDriverLetterToMount;
    }
    
    private void UnMountClientHostFileLocationToServer(String driveLetter)throws Throwable
    {
         String command = String.format("%s use %s: /delete", Constants.WINDOWS_HOST_SYSTEM_NET_LOCATION, driveLetter.toUpperCase());         
         for(int i = 0; i < 5; i++)
         {         
            Runtime.getRuntime().exec(command);         
            List<String>  existingDrives = GetAllDriveLettersOfHostMachine(); 
            if(!existingDrives.contains(driveLetter.toLowerCase()))
                break;
            else
                Thread.sleep(2000);
         }
    }
    
    public void ReplaceHostFile() throws Throwable
    {
        String environmentContent =  _readUrlContent.getCompleteContent();
        if(environmentContent == null || environmentContent.isEmpty())
            throw new Exception(String.format("Invalid Environemnt %s", _envi));
        String mountedDrive = MountClientHostFileLocationToServer();        
        OverwriteFileContent(String.format("%s:\\hosts", mountedDrive), environmentContent);
        UnMountClientHostFileLocationToServer(mountedDrive);        
    }   
       
    
    private void OverwriteFileContent(String fileLocation, String fileConTent) throws Throwable
    {
         File fnew = new File(fileLocation);
         if(fnew.exists())
         {
            FileWriter f2 = new FileWriter(fnew, false);
            f2.write(fileConTent);
            f2.close();
         }
         else
             throw new Exception(String.format("The %s file does not exist", fileLocation));
    }
}
