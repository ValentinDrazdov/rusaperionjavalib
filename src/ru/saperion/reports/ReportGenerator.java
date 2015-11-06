/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.reports;
import com.saperion.intf.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Класс для формирования объекта со строками отчета
 * @author Драздов Валентин
 */
public class ReportGenerator {
    private static final Logger LOG = Logger.getLogger(ru.saperion.reports.ReportGenerator.class);
    
    /**
     *  Список-массив со строками отчета
     */
    private ArrayList<IReportLine> outList = null;
    
    /**
     * Курсор для получения данных для отчета
     */
    private ru.saperion.com.Cursor oCur = null;
    
    /**
     * Тип класса, реализованный согласно интерфейсу {@link IReportLine}
     */
    private Class<? extends IReportLine> reportType;
    
    /**
     * Данный конструктор инициализирует генератор отчетов
     * @param reportType Класс, реализованный согласно интерфейсу {@link IReportLine}
     */
    public ReportGenerator(Class<? extends IReportLine> reportType)
    {
        outList = null;
        oCur = null;
        this.reportType = reportType;
    }
    
    /**
     * Данный конструктор инициализирует генератор отчетов
     * @param reportType Класс, реализованный согласно интерфейсу {@link IReportLine}
     * @param oCur {@link ru.saperion.com.Cursor Курсор}, содержащий результат запроса для построения отчета.
     */
    public ReportGenerator(Class<? extends IReportLine> reportType, ru.saperion.com.Cursor oCur)
    {
        outList = null;
        this.oCur = oCur;
        this.reportType = reportType;
    }
    
    /**
     * Данный метод предназначен для поздней инициализации данных для генерации отчета 
     * @param oCur {@link ru.saperion.com.Cursor Курсор}, содержащий результат запроса для построения отчета.
     */
    public void initDocs(ru.saperion.com.Cursor oCur)
    {
        this.oCur = oCur;
    }
    
    /**
     * Получение созданного {@link #outList списка-массива со строками отчета}
     * @return Созданный {@link #outList список-массив со строками отчета}
     */
    public ArrayList<IReportLine> getList()
    {
        return outList;
    }
    
    /**
     * Данная функция выполняет генерацию отчета по данным из переданного {@link ru.saperion.com.Cursor курсора}
     * @param oCur {@link ru.saperion.com.Cursor Курсор}, содержащий результат запроса для построения отчета.
     * @return Созданный {@link #outList список-массив со строками отчета}
     * @throws Exception в случае ошибок, произошедших при обработке документов
     */
    public ArrayList<IReportLine> generateList(ru.saperion.com.Cursor oCur) throws Exception
    {
        initDocs(oCur);
        return ReportGenerator.this.generateList();
    }
    
    /**
     * Данная функция выполняет генерацию отчета по данным из {@link #oCur курсора}, 
     * который был задан при {@link #ReportGenerator(java.lang.Class, ru.saperion.com.Cursor) инициализации}, 
     * либо в функции {@link #initDocs(ru.saperion.com.Cursor) initDocs}
     * @return Созданный {@link #outList список-массив со строками отчета}
     * @throws Exception в случае ошибок, произошедших при обработке документов
     */
    public ArrayList<IReportLine> generateList() throws Exception
    {
        try
        {
            if (oCur == null) return null;
            
            outList = new ArrayList<IReportLine>();
            if (oCur.Count() < 1) return outList;
            if (oCur.First())
            {
                do
                {
                    IReportLine newElem = reportType.newInstance();
                    ru.saperion.com.Document oDoc = oCur.Document();
                    if (oDoc == null) continue;
                    newElem.loadLine(oDoc);
                    for (int iRow = outList.size() -1; iRow >=0; iRow--)
                    {
                        IReportLine inlistElem = outList.get(iRow);

                        if (inlistElem.getKey().equals(newElem.getKey()))
                        {
                            inlistElem.combine(newElem);
                            newElem = null;
                            break;
                        }
                    }
                    if (newElem != null)
                    {
                        outList.add(newElem);
                    }
                } while(oCur.Next());
            }

            return outList;
        
        }
        catch (Exception e)
        {
            LOG.error(String.format("Возникла ошибка при обработке документов:%n%s", e.getMessage()));
            throw new Exception (String.format("Возникла ошибка при обработке документов:%n%s", e.getMessage()));
        }
    }
    
}
