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
 *
 * @author VDrazdov
 */
public class Field {
    private static final Logger LOG = Logger.getLogger(ru.saperion.webclient.forms.Field.class);
    
    private String name;
    private String value;
    
    private Type type;
    
    
    public static enum Type
    {
        TEXT (1, "Text"),
        COMBO (2, "Combo"),
        DATE (3, "Date");
        
        private final int typeNum;
        private final String typeName;
        Type(int type, String name)
        {
            typeNum = type;
            typeName = name;
        }
        
        public int getTypeNum()
        {
            return typeNum;
        }
        public String getTypeName()
        {
            return typeName;
        }                
    }
    
    
    public static enum Limit
    {
        ANY (0, "", "like"),
        LOWER (1, "lower", ">="),
        UPPER (2, "upper", "<="),
        BOTH (3, "both", "=");
        
        private final int limitNum;
        private final String limitName;
        private final String queryCompare;
        
        Limit(int type, String name, String compare)
        {
            limitNum = type;
            limitName = name;
            queryCompare = compare;
        }
        
        public int getLimitNum()
        {
            return limitNum;
        }
        public String getLimitName()
        {
            return limitName;
        }                
        public String getCompare()
        {
            return queryCompare;
        }
    }
    
    public Field(Type type, String name, String value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return getValue();
    }
    
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
