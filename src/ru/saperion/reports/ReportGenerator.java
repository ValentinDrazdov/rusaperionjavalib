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
 *
 * @author VDrazdov
 */
public class ReportGenerator {
    private static final Logger LOG = Logger.getLogger(ru.saperion.reports.ReportGenerator.class);
    private ArrayList<IReportLine> outList = null;
    //rivate List<SaDocumentInfo> docs = null;
    private ru.saperion.com.Cursor oCur = null;
    
    private Class<? extends IReportLine> reportType;
    
    public ReportGenerator(Class<? extends IReportLine> reportType)
    {
        outList = null;
        oCur = null;
        this.reportType = reportType;
    }
    
    public ReportGenerator(Class<? extends IReportLine> reportType, ru.saperion.com.Cursor oCur)
    {
        outList = null;
        this.oCur = oCur;
        this.reportType = reportType;
    }
    
    public void initDocs(ru.saperion.com.Cursor oCur)
    {
        this.oCur = oCur;
    }
    
    public ArrayList<IReportLine> getList()
    {
        return outList;
    }
    
    public ArrayList<IReportLine> generateList(ru.saperion.com.Cursor oCur) throws Exception
    {
        initDocs(oCur);
        return ReportGenerator.this.generateList();
    }
    
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
