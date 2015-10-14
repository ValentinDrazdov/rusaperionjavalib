/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.tools;

import java.io.*;
/**
 *
 * @author VDrazdov
 */
public class FileWorker {
    public static void SaveFile(String sFileName, byte[] content) throws Exception
    {
        try
        {            
            FileOutputStream out = new FileOutputStream(sFileName);
            out.write(content);
            out.flush();
            out.close();
            out = null;
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Возникла ошибка во время сохранения файла на диск '%s':%n%s", sFileName, e.getMessage()));
        }
    }
    
    public static void MkDir(String sDirName) throws Exception
    {
        try
        {
            File fileofDir = new File(sDirName);
                    
            if (!fileofDir.exists())
            {
                fileofDir.mkdir();
            }
            fileofDir = null;
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Возникла ошибка во время создания директории '%s':%n%s", sDirName, e.getMessage()));
        }
    }
}
