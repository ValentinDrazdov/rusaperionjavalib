/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.webclient.forms;
import com.saperion.ngc.iform.*;
import com.saperion.ngc.iform.field.*;
import com.saperion.rmi.SaQueryInfo;
import java.util.*;
import org.apache.log4j.Logger;
import ru.saperion.tools.DateWorker;
/**
 * Работа с формой веб-клиента
 * @author Драздов Валентин
 */
public class Mask {

    private static final Logger LOG = Logger.getLogger(ru.saperion.webclient.forms.Mask.class);

    /**
     * Объект формы, полученный из WEB-клиента
     */
    private IntelligentFormView form;

    /**
     * Данный конструктор инициализирует объект для дальнейшей работой с формой по привычному формату, как через COM
     * @param form {@link #form Объект формы, полученный из WEB-клиента}
     */
    public Mask(IntelligentFormView form)
    {
        this.form = form;
    }

    /**
     * Данная функция возвращает значение указанного в параметре поля. 
     * @param sFieldName {@link Field#name Наименование поля}
     * @return значение поля<br>В случае, если поле не найдено - будет возвращена пустая строка
     */
    public String Field(String sFieldName)
    {
        return Field(sFieldName, Field.Limit.ANY);
    }

    /**
     * Данная функция возвращает значение указанного в параметре поля. 
     * @param sFieldName {@link Field#name Наименование поля}
     * @param limit {@link Field#type Ограничение\граница поля  }, используемый при построении запросов.
     * @return значение поля<br>В случае, если поле не найдено - будет возвращена пустая строка
     */
    public String Field(String sFieldName, Field.Limit limit)
    {
        return FieldFromForm(form, sFieldName, limit).getValue();
    }

    /**
     * Данная функция устанавливает значение в поле
     * @param sFieldName {@link Field#name Наименование поля}
     * @param sFieldValue {@link Field#value Значение поля}
     */
    public void SetField(String sFieldName, String sFieldValue)
    {
        SetField(sFieldName, sFieldValue, Field.Limit.ANY);
    }
    
    /**
     * Данная функция устанавливает значение в поле
     * @param sFieldName {@link Field#name Наименование поля}
     * @param sFieldValue {@link Field#value Значение поля}
     * @param limit {@link Field#type Ограничение\граница поля}, используемый при построении запросов.
     */    
    public void SetField(String sFieldName, String sFieldValue, Field.Limit limit)
    {
        SetFieldToForm(form, sFieldName, sFieldValue, limit);
    }

    /**
     * Данная функция предназначена для формирования корректного параметрического фильтра в запрос из Saperion Classic Connector
     * @param query запрос, который необходимо дополнить параметрическим фильтром
     * @param sFieldName {@link Field#name Наименование поля}
     * @return запрос, дополненный параметрическим фильтром
     * @throws Exception в случае возникновении ошибки при составлении запроса
     */
    public SaQueryInfo FieldToQuery(SaQueryInfo query, String sFieldName) throws Exception
    {
        return FieldToQuery(query, sFieldName, Field.Limit.ANY);
    }

    /**
     * Данная функция предназначена для формирования корректного параметрического фильтра в запрос из Saperion Classic Connector
     * @param query запрос, который необходимо дополнить параметрическим фильтром
     * @param sFieldName {@link Field#name Наименование поля}
     * @param limit {@link Field#type Ограничение\граница поля}, используемый при построении запросов.
     * @return запрос, дополненный параметрическим фильтром
     * @throws Exception в случае возникновении ошибки при составлении запроса
     */
    public SaQueryInfo FieldToQuery(SaQueryInfo query, String sFieldName, Field.Limit limit) throws Exception
    {
        return AddParamFromForm(query, form, sFieldName, limit);
    }


    /**
     * Данная функция аналогична функции FieldToQuery, отличается тем, что не требует создания объекта формы, но нуждается в передаче полного набора параметров
     * @param query запрос, который необходимо дополнить параметрическим фильтром
     * @param formView объект формы из Saperion WEB Client
     * @param fieldName {@link Field#name Наименование поля}
     * @param fieldLimit {@link Field#type Ограничение\граница поля}, используемый при построении запросов.
     * @return запрос, дополненный параметрическим фильтром
     * @throws Exception в случае возникновении ошибки при составлении запроса
     */
    public static SaQueryInfo AddParamFromForm(SaQueryInfo query, IntelligentFormView formView, String fieldName, Field.Limit fieldLimit) throws Exception
    {
        try
        {
            if (query == null) query = new SaQueryInfo("");
            Field field = FieldFromForm(formView, fieldName, fieldLimit);
            if (field.getValue().equals("")) return query;
            String sQuery = String.format("and %s %s :%s%s", fieldName.toUpperCase(), fieldLimit.getCompare(), fieldName, fieldLimit.getLimitName());
            String sHQL  = query.getQueryString();

            sHQL = String.format("%s %s", sHQL, sQuery);

            switch (field.getType())
            {
                case TEXT:
                case COMBO:
                    query.setText(String.format("%s%s", fieldName, fieldLimit.getLimitName()), field.getValue());
                    break;
                case DATE:
                    query.setDate(String.format("%s%s", fieldName, fieldLimit.getLimitName()), new java.sql.Date(ru.saperion.tools.DateWorker.FromStringToDate(field.getValue(), DateWorker.DateFormat.RUSSIAN).getTime()));
                    break;
            }
            query.setQueryString(sHQL);

            return query;
        }
        catch (Exception e)
        {
            LOG.error(String.format("Возникла ошибка при составлении параметра в запрос с поля '%s' на форме:%n%s", fieldName, e.getMessage()));
            return query;
        }

    }

    /**
     * Данная функция аналогична функции Field, отличается тем, что не требует создания объекта формы, но нуждается в передаче полного набора параметров
     * @param formView объект формы из Saperion WEB Client
     * @param fieldName {@link Field#name Наименование поля}
     * @param fieldLimit {@link Field#type Ограничение\граница поля}, используемый при построении запросов.
     * @return значение поля<br>В случае, если поле не найдено - будет возвращена пустая строка
     */
    public static Field FieldFromForm (IntelligentFormView formView, String fieldName, Field.Limit fieldLimit)
    {
        String fieldValue = "";
        try
        {
            List<IntelligentField> fields;
            TextField textField = null;

            LOG.trace("Выполняется получение значения с поля");
            fields = formView.getFields();

            LOG.trace(String.format("Выполняется поиск поля '%s'", fieldName));
            for (IntelligentField field : fields)
            {
                if (field.getFieldName().equalsIgnoreCase(fieldName) && (fieldLimit == Field.Limit.ANY || field.getLimit().equals(fieldLimit.getLimitName())) )
                {
                    LOG.trace(String.format("Поле '%s' найдено", fieldName));
                    return Field.ParseField(field);
                }
            }
        }
        catch (Exception e)
        {
            LOG.error(String.format("Возникла непредвиденная ошибка во время получения данных с поля '%s':%n%s", fieldName, e.getMessage()));
            LOG.error(String.format("Трассировка ошибки: %s", e.getStackTrace().toString()));
        }
        return new Field(Field.Type.TEXT, fieldName, fieldValue);
    }

    /**
     * Данная функция аналогична функции SetField, отличается тем, что не требует создания объекта формы, но нуждается в передаче полного набора параметров
     * @param formView объект формы из Saperion WEB Client
     * @param fieldName {@link Field#name Наименование поля}
     * @param value значение, которое нужно установить в поле
     * @param limit {@link Field#type Ограничение\граница поля}, используемый при построении запросов.
     */
    public static void SetFieldToForm (IntelligentFormView formView, String fieldName, String value, Field.Limit limit)
    {
        try
        {
            List<IntelligentField> fields = null;

            LOG.trace("Выполняется получение занесение значение в поле");
            fields = formView.getFields();

            LOG.trace(String.format("Выполняется поиск поля '%s'", fieldName));
            for (IntelligentField field : fields)
            {
                if (field.getFieldName().equalsIgnoreCase(fieldName) && (limit == Field.Limit.ANY || field.getLimit().equals(limit.getLimitName())) )
                {
                    LOG.trace(String.format("Поле '%s' найдено", fieldName));
                    try
                    {
                        field.setDefaultValue(value);
                        field.setChangedImplicitly();
                        LOG.trace(String.format("В поле '%s' успешно занесено значение '%s'", fieldName, value));
                    }
                    catch (Exception e)
                    {
                        LOG.error(String.format("Возникла ошибка во время занесения в поле '%s' значения '%s':%n%s", fieldName, value, e.getMessage()));
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.error(String.format("Возникла непредвиденная ошибка во время занесения в поле '%s' значения '%s':%n%s", fieldName, value, e.getMessage()));
            LOG.error(String.format("Трассировка ошибки: %s", e.getStackTrace().toString()));
        }
    }
}
