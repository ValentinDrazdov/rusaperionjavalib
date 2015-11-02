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
    
    public enum SaveType
    {
        WEB("Web", 1),
        CLASSIC("Classic", 0);
        
        private final String sName;
        private final int iType;
        
        SaveType(String name, int type)
        {
            sName = name;
            iType = type;
        }
        
        public String getName()
        {
            return sName;
        }
        
        public int getType()
        {
            return iType;
        }
    }
    

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

    public String GetProperty(String name)
    {
        if (props.containsKey(name)) return props.get(name);

        return "";
    }

    public File[] getFiles() throws Exception
    {
        if (files == null) throw new Exception ("Документ не содержит файлов или еще не был загружен");
        return files;
    }
    
    public int NumElems() 
    {
        if (files == null) return 0;
        return files.length;
    }
    
    public File SubDocument(int index) throws Exception
    {
        index--;
        if (files == null)  throw new Exception ("Документ не содержит файлов или еще не был загружен");
        if (index < 0 || index >= files.length) throw new Exception ("Был запрпошен документ с несуществующим индексом");
        return files[index];
    }
    
    public Boolean SaveAs(int index, SaveType type, String path) throws Exception
    {
        if (files == null) throw new Exception ("Документ не содержит файлов или еще не был загружен");
        if (index < 0 || index > files.length) throw new Exception ("Был запрпошен документ с несуществующим индексом");
        
        if (!path.substring(path.length() - 1, path.length()).equals("\\")) path = String.format("%s\\", path);
        
        if (index > 0)
        {
            index--;
            File currentFile = files[index];
            switch(type)
            {
                case CLASSIC:
                    ru.saperion.tools.FileWorker.SaveFile(String.format("%s%s", path, currentFile.getFileName()), currentFile.getContent());
                    break;
                case WEB:
                    ru.saperion.tools.WebWorker.SaveFile(currentFile.getFileName(), currentFile.getContent());
                    break;
            }
        }
        else
        {
            for(File currentFile: files)
            {
                switch(type)
                {
                    case CLASSIC:
                        ru.saperion.tools.FileWorker.SaveFile(String.format("%s%s", path, currentFile.getFileName()), currentFile.getContent());
                        break;
                    case WEB:
                        ru.saperion.tools.WebWorker.SaveFile(currentFile.getFileName(), currentFile.getContent());
                        break;
                }
                
            }
        }
        return true;
    }

}
