/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.com;

import java.util.*;
import com.saperion.constants.SaConstants;
import com.saperion.rmi.*;
import com.saperion.intf.*;
import java.util.*;
/**
 *
 * @author VDrazdov
 */
public class Document {
    private String sXHDOC;
    private Map<String, String> props;    
    private Application app;
    private SaDocumentInfo documentInfo;
    private SaDocInfo docInfo;
    private File[] files; 
    
    public Document(Application app)
    {
        this();
        this.app = app;
    }
    
    public Document(Application app, SaDocumentInfo docInfo)
    {
        this(docInfo);
        this.app = app;
    }
    
    public Document()
    {
        
        props = new HashMap<String, String>();        
    }
    
    public Document(SaDocumentInfo documentInfo)
    {
        
        SaPropertyValue prop;
        this.documentInfo = documentInfo;
        props = new HashMap<String, String>();
        
        try
        {
            prop = documentInfo.getValue("$HDOC");
            sXHDOC = prop.getStringValue();
        }
        catch (Exception e)
        {
            sXHDOC = "";
        }
        
        
        for(SaPropertyValue inprop : documentInfo.getValues())
        {
            try
            {
                int valueType = inprop.getValues()[0].getValueType();
                switch (valueType)
                {
                    case SaConstants.FT_STRING:
                        String getStringValue = inprop.getStringValue();
                        props.put(inprop.getName(), getStringValue);
                        break;
                    case SaConstants.FT_INTEGER:
                        int getIntValue = inprop.getValues()[0].getIntValue();                    
                        props.put(inprop.getName(), String.format("%d", getIntValue));
                        break;
                    case SaConstants.FT_DOUBLE:
                        double getDoubleValue = inprop.getValues()[0].getFloatValue();
                        props.put(inprop.getName(), String.format(Locale.US, "%f", getDoubleValue));
                        break;
                    case SaConstants.FT_DATE:
                        short dtar[] = inprop.getValues()[0].getDateValue();
                        props.put(inprop.getName(), String.format("%d.%d.%d", dtar[0], dtar[1], dtar[2]));
                        break;
                }
            }
            catch (Exception e)
            {
                
            }
            
        }
        
    }
    
    public Boolean Load() throws Exception
    {
        try
        {
            docInfo = app.LoadDocInfo(sXHDOC);
               
            if (docInfo == null) return false;
            if (docInfo.getElementCount() == 0) return false;
            files = new File[docInfo.getElementCount()];
            
            for (int i = 0; i < files.length; i++)
            {
                files[i] = new File(this, i + 1);
            }
            return true;
        }
        catch(Exception e)
        {
            throw new Exception(String.format("Не удалось загрузить документ с HexUID = '%s', ошибка:%n%s", sXHDOC, e.getMessage()));
        }
        
    }
    
    public Boolean Load(String sXHDOC) throws Exception
    {
        this.sXHDOC = sXHDOC;
        return Load();
    }
    
    
    public String HexUID()
    {
        return sXHDOC;
    }
    public SaDocumentInfo getDocumentInfo()
    {
        return documentInfo;
    }
    public SaDocInfo getDocInfo()
    {
        return docInfo;
    }
    public Application getApplication()
    {
        return app;
    }
    
    public String getProperty(String name)
    {
        if (props.containsKey(name)) return props.get(name);
        
        return "";
    }
    
    public File[] getFiles()
    {
        return files;
    }
    
}
