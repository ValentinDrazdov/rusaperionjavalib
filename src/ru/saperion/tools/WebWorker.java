/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.tools;

import java.nio.charset.Charset;
import org.zkoss.zul.*;
/**
 * Работа с WEB
 * @author Драздов Валентин Сергеевич  
 */
public class WebWorker {
    
    /** 
     * Данный метод предназначен для сохранения массива байт (содержимого файлов) через браузер (при использовании веб-клиента).
     * @param fileName имя для сохранения файла
     * @param content массив с содержимым файла
     **/
    public static void SaveFile(String fileName, byte[] content)
    {
        Filedownload.save(content, "application/octet-stream", fileName);
    }
   
    /** 
     * Данный метод предназначен для сохранения строки (содержимого файлов) через браузер (при использовании веб-клиента) с использованием конкретной кодировки.
     * @param fileName адрес для сохранения файла
     * @param sContent строка с содержанием файла
     * @param sCodePage строка с названием кодировки [например, Cp1251]
     **/ 
    public static void SaveFile(String fileName, String sContent, String sCodePage)
    {
        byte[] unloadContent = sContent.getBytes(Charset.forName(sCodePage));
        Filedownload.save(unloadContent, "application/octet-stream", fileName);
    }
}
