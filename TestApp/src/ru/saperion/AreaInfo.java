/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion;

import com.saperion.rmi.SaQueryInfo;
import org.apache.log4j.Logger;

import ru.saperion.webclient.forms.*;
import ru.saperion.reports.*;
/**
 *
 * @author VDrazdov
 */
public class AreaInfo implements IReportLine {
    
    private static final Logger LOG = Logger.getLogger(AreaInfo.class);
    
    // Поля
    public int iKiosk;
    public int iOperators;
    public int iHelpers;
    public int iJudges;
    public String sAreaName;
    
    // Что должно произойти, когда встретится запись с таким же ключем, который уже был
    @Override
    public void combine(IReportLine other)
    {
        
        this.iKiosk += ((AreaInfo)other).iKiosk;
        this.iOperators += ((AreaInfo)other).iOperators;
        this.iHelpers += ((AreaInfo)other).iHelpers;
        this.iJudges += ((AreaInfo)other).iJudges;
    }
    
    // Преобразование в текстовую строку (для вывода в CSV)
    @Override
    public String toString()
    {
        return String.format("%s;%d;%d;%d;%d;", this.sAreaName, this.iKiosk, this.iOperators, this.iHelpers, this.iJudges);
    }
    
    // Формирование данных на основе объекта документа
    @Override
    public void loadLine(ru.saperion.com.Document oDoc)
    {
        
        this.sAreaName = oDoc.getProperty("AREANAME");
        this.iKiosk = (int)(Double.parseDouble(oDoc.getProperty("KIOSK")));
        this.iOperators = (int)(Double.parseDouble(oDoc.getProperty("OPERATORS")));
        this.iHelpers = (int)(Double.parseDouble(oDoc.getProperty("HELPERS")));
        this.iJudges = (int)(Double.parseDouble(oDoc.getProperty("JUDGES")));
    }
    

    // Получение ключевого поля (по которому определяется уникальность строки).
    @Override
    public String getKey()
    {
        return sAreaName;
    }
    
    // Получение заголовка для CSV-файла
    @Override    
    public String getFirstLine()
    {
        String sOut = "Наименование судебного участка и отдела делопроизводства;Выдача дел в электронном виде для граждан;Выдача копий решений для граждан;Поиск прецедентов аппаратом судебных участков;Поиск прецедентов мировым судьей;Примечание";
        return sOut;
    }
    
    // Формирование запроса. Может принимать маску. 
    @Override 
    public SaQueryInfo getSaperionQuery(ru.saperion.webclient.forms.Mask oMask) throws Exception
    {
        try
        {
            
            SaQueryInfo query = new SaQueryInfo("");
            String sHQL = "select audit.AREANAME, audit.KIOSK, audit.OPERATORS, audit.HELPERS, audit.JUDGES from case_docs_audit_view audit where 1 = 1 ";
            query.setQueryString(sHQL);
            LOG.trace("Создана строка запроса без условий");
            if (oMask != null)
            {                
                LOG.trace("Получен объект формы, будет произведена попытка получить данные с формы");
                query = oMask.FieldToQuery(query, "AREANUM", Field.Limit.ANY); 
                query = oMask.FieldToQuery(query, "PRTDATE", Field.Limit.LOWER);
                query = oMask.FieldToQuery(query, "PRTDATE", Field.Limit.UPPER);
            }
            sHQL = query.getQueryString();
            LOG.debug(String.format("Конечный вариант запроса: %s", sHQL));
            return query;     
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Во время формирования запроса к БД Саперион возникла ошибка:%n%s", e.getMessage()));
                    
        }

    }
}