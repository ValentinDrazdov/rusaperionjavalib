/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.reports;

import com.saperion.connector.SaClassicConnector;
import com.saperion.exception.*;
import com.saperion.intf.SaDocumentInfo;
import com.saperion.ngc.iform.*;
import com.saperion.rmi.*;
import java.nio.charset.Charset;
import java.util.*;
import org.zkoss.zul.*;

import org.apache.log4j.Logger;
import ru.saperion.com.Application;

/**
 *
 * @author VDrazdov
 */
public class Csv {
    private static final Logger LOG = Logger.getLogger(ru.saperion.reports.Csv.class);
    
    
    public static String generate(SaClassicConnector connector, Class<? extends IReportLine> reportType, IntelligentFormView form) throws InstantiationException, Exception
    {
        return generate(new Application(connector), reportType, form);
    }
    public static String generate(ru.saperion.com.Application oApp, Class<? extends IReportLine> reportType, IntelligentFormView form) throws InstantiationException, Exception
    {
        LOG.info("generate запущен");
        try
        {                             
            LOG.trace ("Формируется запрос на основе формы");
            SaQueryInfo info = reportType.newInstance().getSaperionQuery(new ru.saperion.webclient.forms.Mask(form));
            LOG.debug ("Сформирован запрос: " + info.getQueryString());
            
            LOG.debug ("Осуществляется запрос к БД");
               ru.saperion.com.Cursor oCur = oApp.SelectQuery(info);
                        
            
            if (oCur == null)
            {
                LOG.info ("БД не вернула записей для обработки");
                Messagebox.show("Не удалось получить данные", "Отчет", Messagebox.OK, Messagebox.EXCLAMATION);
                return "";
            }
            
            LOG.info(String.format("Получено записей из БД: %d%n", oCur.Count()));
            
            LOG.debug("Инициализируется объект обработки аудита");
            ReportGenerator generator = new ReportGenerator(reportType, oCur);
            
            LOG.info ("Формируется консолидированная информация");
            ArrayList<IReportLine> reportList = generator.generateList();
                        
            LOG.info (String.format("Получены сведения об участках в количестве: %d", reportList.size()));
            
            LOG.debug ("Формируется текст для сохранения");
            //String sOutputText = "Наименование судебного участка и отдела делопроизводства;Выдача копий решений для граждан;Поиск прецедентов аппаратом судебных участков;Поиск прецедентов мировым судьей;Примечание";
            String sOutputText = String.format("%s", reportType.newInstance().getFirstLine());
            for (IReportLine inlistElem :reportList)
            {
                String sOutline = inlistElem.toString();// String.format("%s;%d;%d;%d;%d;", inlistarea.sAreaNum, inlistarea.iKiosk, inlistarea.iOperators, inlistarea.iHelpers, inlistarea.iJudges);
                System.out.println(sOutline);
                sOutputText = String.format("%s%n%s", sOutputText, sOutline);
            }
            
            
            LOG.info ("GenerateCSV успешно выполнен");
            return sOutputText;
            
         
        }
        catch (Exception e)
        {
        //    LOG.error(String.format("Возникла ошибка при выполнении генерации CSV:%n%s", e.toString()));
            throw new Exception(String.format("Возникла ошибка при выполнении генерации CSV:%n%s", e.toString()));
        }
    }

}
