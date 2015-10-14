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
import java.io.*;
/**
 *
 * @author VDrazdov
 */
public class File {
    private Application app;
    
    private SaDocInfo.ElementInfo element;
    private String fileName;
    private long fileSize;
    private SaDocInfo.ElementType elemType;
    private byte[] content;
    private SaDocInfo docInfo;
    
    public File(Document doc, int elementNo) throws Exception
    {
        try
        {
            docInfo = doc.getDocInfo();
            
            this.element = docInfo.getElementInfo(elementNo);
            fileName = this.element.getName();
            fileSize = this.element.getSize();
            elemType = this.element.getType();       

            InputStream in = doc.getApplication().LoadDocumentStream(doc.HexUID(), true, elementNo);
            content = new byte[(int)fileSize];
            in.read(content);        
        }
        catch(Exception e)
        {
            throw new Exception(String.format("Произошла ошибка во время загрузки файла:%n%s", e.getMessage()));
        }
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public Long getFileSize()
    {
        return fileSize;
    }
    public SaDocInfo.ElementType getElemType()
    {
        return elemType;
    }
    public byte[] getContent()
    {
        return content;
    }
    public SaDocInfo getSaDocInfo()
    {
        return docInfo;
    }
    
    
    
}
