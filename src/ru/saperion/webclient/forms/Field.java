/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.webclient.forms;

import com.saperion.ngc.iform.field.DateField;
import com.saperion.ngc.iform.field.IntelligentField;
import com.saperion.ngc.iform.field.LookupTextField;
import com.saperion.ngc.iform.field.TextField;
import java.util.Date;
import org.apache.log4j.Logger;
import ru.saperion.tools.DateWorker;

/**
 * Работа с элементом формы - полем
 * @author Драздов Валентин
 */
public class Field {
    private static final Logger LOG = Logger.getLogger(ru.saperion.webclient.forms.Field.class);
    
    /**
     * Название поля
     */
    private String name;
    
    /**
     * Значение поля
     */
    private String value;
    
    /** 
     * Тип поля
     */
    private Type type;
    
    /**
     * Данный тип используется при получении и занесении значений в поле
     */
    public static enum Type
    {
        TEXT (1, "Text"),
        COMBO (2, "Combo"),
        DATE (3, "Date");
        
        /**
         * Номер типа
         */
        private final int typeNum;
        
        /**
         * Наименование типа
         */
        private final String typeName;
        
        /**
         * Определение типа поля
         * @param type {@link #typeNum Номер типа}
         * @param name {@link #typeName Наименование типа}
         */
        Type(int type, String name)
        {
            typeNum = type;
            typeName = name;
        }
        
        /**
         * Получить {@link #typeNum номер типа}
         * @return {@link #typeNum Номер типа}
         */
        public int getTypeNum()
        {
            return typeNum;
        }
        
        /**
         * Получить {@link #typeNum наименование типа}
         * @return {@link #typeNum Наименование типа}
         */
        public String getTypeName()
        {
            return typeName;
        }                
    }
    
    /**
     * Данный тип используется при обработке поля в качестве условного параметра составляемого запроса
     */
    public static enum Limit
    {
        ANY (0, "", "like"),
        LOWER (1, "lower", ">="),
        UPPER (2, "upper", "<="),
        BOTH (3, "both", "=");
        
        /**
         * Числовое обозначение лимита
         */
        private final int limitNum;
        
        /**
         * Наименование лимита
         */
        private final String limitName;
        
        /**
         * Ключеове слово для запроса
         */
        private final String queryCompare;
        
        /** Определение нового типа лимита
         * 
         * @param type {@link #limitNum Числовое обозначение лимита)
         * @param name {@link #limitName Наименование лимита}
         * @param compare {@link queryCompare Ключеове слово для запроса}
         */
        Limit(int type, String name, String compare)
        {
            limitNum = type;
            limitName = name;
            queryCompare = compare;
        }
        
        /**
         * Получить {@link #limitNum числовое обозначение лимита)
         * @return {@link #limitNum Числовое обозначение лимита)
         */
        public int getLimitNum()
        {
            return limitNum;
        }
        
        /**
         * Получить {@link #limitName наименование лимита}
         * @return {@link #limitName Наименование лимита}
         */
        public String getLimitName()
        {
            return limitName;
        }   
        
        /**
         * Получить {@link #limitName ключеове слово для запроса}
         * @return {@link #limitName Ключеове слово для запроса}
         */
        public String getCompare()
        {
            return queryCompare;
        }
    }
    
    /**
     * Данный конструктор предназначен для создания объекта типа {@link Field} и хранения в нем оперативной информации по полю.
     * <p>Как правило, используется в функции {@link #ParseField} и не предназначен для использования извне
     * @param type Тип поля
     * @param name Наименование поля
     * @param value Значение поля
     */
    public Field(Type type, String name, String value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }
    
    /**
     * Получить {@link #type тип поля}
     * @return {@link #type Тип поля}
     */
    public Type getType()
    {
        return type;
    }
    
    /**
     * Получить {@link #value значение поля}
     * @return {@link #value Значение поля}
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Получить {@link #name наименование поля}
     * @return {@link #name Наименование поля}
     */
    public String getName()
    {
        return name;
    }
    
    /**
    * Получить {@link #value значение поля}
    * @return {@link #value Значение поля}
    */
    @Override
    public String toString()
    {
        return getValue();
    }
    
    /**
     * Данная функция предназначена для получения актуальной информации о поле по объекту из Saperion WEB Client. 
     * @param field объект поля из Saperion WEB Client
     * @return готовый экземпляр класса
     * @throws Exception в случае технических ошибок при получении значения поля.
     */
    public static Field ParseField(IntelligentField field) throws Exception
    {
        String fieldName="";
        String fieldValue="";
        try
        {
            try
            {
                fieldName = field.getFieldName().toUpperCase();
            }
            catch (Exception e)
            {
                
            }
                    
            switch (field.getElementType())
            {
                case TEXT:
                case TEXTEDIT:
                    try
                    {
                        TextField textField = (TextField)field;
                        fieldValue = textField.getValue();
                        LOG.debug(String.format("Из поля '%s' получено значение '%s'", fieldName, fieldValue));
                        textField = null;
                        return new Field(Type.TEXT, fieldName, fieldValue);
                    }
                    catch (Exception e)
                    {
                        LOG.error(String.format("Возникла ошибка во время получения данных с поля '%s':%n%s", fieldName, e.getMessage()));
                    }
                    break;

                case MULTI:
                    try
                    {
                        LookupTextField combo = (LookupTextField)field;
                        fieldValue = combo.getValue();
                        LOG.debug(String.format("Из комбо-поля '%s' получено значение '%s'", fieldName, fieldValue));
                        combo = null;
                        return new Field(Type.COMBO, fieldName, fieldValue);
                    }
                    catch (Exception e)
                    {
                        LOG.error(String.format("Возникла ошибка во время получения данных с комбо-поля '%s':%n%s", fieldName, e.getMessage()));
                    }
                    break;

                case DATE:
                    try
                    {

                        LOG.trace("Получаем поле с датой как обычное поле формата даты");
                        DateField fieldDate = (DateField)field;
                        String sInputDate = fieldDate.getValue().toString();
                        if (sInputDate.equals("")) return new Field(Type.TEXT, fieldName, "");
                        LOG.debug(String.format("Из поля с датой '%s' получено значение '%s'", fieldName, sInputDate));
                        Date objDate = ru.saperion.tools.DateWorker.FromStringToDate(sInputDate, DateWorker.DateFormat.SAPERION);
                        fieldValue = ru.saperion.tools.DateWorker.FromDateToString(objDate, DateWorker.DateFormat.RUSSIAN);
                        LOG.debug(String.format("Полученная ранее дата '%s' преобразована в другой формат: '%s'", sInputDate, fieldValue));
                        return new Field(Type.DATE, fieldName, fieldValue);
                    }                                        
                    catch (Exception e)
                    {
                        LOG.error(String.format("Возникла ошибка во время получения данных из поля с датой '%s':%n%s", fieldName, e.getMessage()));
                    }
                    break;
            }
        }
        catch(Exception e)
        {
            throw new Exception(String.format("Возникла ошибка во время получения значения поля:%n%s", e.getMessage()));
        }
                
        return new Field(Type.TEXT, fieldName, "");
    }
}
