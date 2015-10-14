/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.tools;

import java.util.*;
/**
 *
 * @author VDrazdov
 */
public class DateWorker {
    
    public enum DateFormat
    {
        SAPERION ("Saperion", "EEE MMM dd HH:mm:ss Z yyyy"),
        RUSSIAN ("Russian", "dd.MM.yyyy"),
        ENGLISH ("English", "yyyy-MM-dd");
        
        private final String sFormat;
        private final String sName;
        
        DateFormat(String name, String format)
        {
            sName = name;
            sFormat = format;            
        }
        
        public String getFormat()
        {
            return sFormat;
        }
        
        public String getName()
        {
            return sName;
        }
    }
    
    public static Date FromStringToDate(String sSource, DateFormat format) throws Exception
    {
        if (sSource.length() < 1)
        {
            throw new Exception ("Невозможно преобразовать строку в дату, так как строка содержит 0 символов");
        }
        try
        {
            java.text.DateFormat readFormat = new java.text.SimpleDateFormat( format.getFormat(), Locale.ENGLISH);
            Date retDate = readFormat.parse(sSource);
            return retDate;            
        }
        catch (Exception e)
        {
            throw new Exception (String.format("Не удалось преобразовать строку '%s' в дату по формату '%s', возникла ошибка:%n%s", sSource, format.getFormat(), e.getMessage()));
                    
        }
    }
    
    public static String FromDateToString(Date dateSource, DateFormat format) throws Exception
    {
        try
        {
            java.text.DateFormat writerFormat = new java.text.SimpleDateFormat( format.getFormat());
            String sDate = writerFormat.format(dateSource);
            return sDate;            
        }
        catch (Exception e)
        {
            throw new Exception (String.format("Не удалось преобразовать дату '%s' в дату по формату '%s', возникла ошибка:%n%s", dateSource.toString(), format.getFormat(), e.getMessage()));
                    
        }
    }
    
}
