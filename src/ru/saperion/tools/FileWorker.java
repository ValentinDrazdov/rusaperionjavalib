/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.tools;

import java.io.*;

/**
 * Работа с файлами
 * @author Драздов Валентин Сергеевич
 */
public class FileWorker {
    /** 
    * Данный метод предназначен для сохранения массива байт (содержимого файлов) в файловую систему.
    * @param sFileName адрес для сохранения файла
    * @param content массив с содержимым файла
    * @throws Exception
    **/
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
    
    /**
     * Данный метод предназначен для создания директории
     * @param sDirName адрес директории
     * @throws Exception 
     */
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
