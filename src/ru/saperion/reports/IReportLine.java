/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.reports;
import com.saperion.intf.*;
import com.saperion.ngc.iform.IntelligentFormView;
import com.saperion.rmi.SaQueryInfo;

import ru.saperion.com.*;
import ru.saperion.webclient.forms.*;

/**
 *
 * @author VDrazdov
 */
public interface IReportLine {
    
    
    public void loadLine(Document oDoc);  
    public void combine(IReportLine line);
    
    public String getKey();
    public String getFirstLine();
    public SaQueryInfo getSaperionQuery(Mask oMask) throws Exception;
    
    @Override 
    public String toString();
    
            
}
