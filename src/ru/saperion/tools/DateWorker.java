/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.tools;

import java.util.*;
/**
 * Работа с датами
 * @author Драздов Валентин Сергеевич
 */
public class DateWorker {
    
    /**
     * Данный тип используется при преобразовании даты в строку и обратно
     */
    public enum DateFormat
    {
        SAPERION ("Saperion", "EEE MMM dd HH:mm:ss Z yyyy"),
        RUSSIAN ("Russian", "dd.MM.yyyy"),
        ENGLISH ("English", "yyyy-MM-dd");
        
        private final String sFormat;
        private final String sName;
        
        /**
         * Объявление формата даты
         * @param name Понятное название формата даты
         * @param format Формат даты
         */
        DateFormat(String name, String format)
        {
            sName = name;
            sFormat = format;            
        }
        
        /**
         * Получить форматы даты
         * @return Формат даты
         */
        public String getFormat()
        {
            return sFormat;
        }
        
        /**
         * Получить название формата даты
         * @return Название формата даты
         */
        public String getName()
        {
            return sName;
        }
    }
    
    /**
     * Данная функция предназначена для преобразования строки в формат даты JAVA. Как правило, данная функция необходима для дальнейшем использовании даты в запросе.
     * @param sSource строка с записанной датой
     * @param format тип формата даты, в котором она записана в строке
     * @return Дата, полученная из строки
     * @throws Exception 
     */
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
    
    /**
     * Данная функция предназначена для преобразования объекта даты в строку по нужному формату. Как правило, используется для отображения в графическом интерфейсе, или протоколах
     * @param dateSource дата, которую нужно преобразовать
     * @param format формат, в котором нужно записать дату в строке
     * @return Строка, преобразованная из даты, согласно формату
     * @see DateFormat
     * @throws Exception 
     */
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
