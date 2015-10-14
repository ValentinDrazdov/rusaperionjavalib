/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.com;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import com.saperion.rmi.*;
import com.saperion.intf.*;
/**
 *
 * @author VDrazdov
 */
public class Cursor {
    private int currentPosition;
    private int maxPosition;
    
    private Application app;
    
    private List<SaDocumentInfo> docs;
    public Cursor(Application app, List<SaDocumentInfo> docs)
    {
        this(docs);
        this.app = app;
    }
    public Cursor(List<SaDocumentInfo> docs)
    {
        this.docs = docs;
        currentPosition = 0;
        maxPosition = this.docs.size() - 1;
    }
    
    public Boolean First()
    {
        if (docs == null) return false;
        currentPosition = 0;
        return (docs.get(currentPosition) != null);
    }
    
    public Boolean Last()
    {
        if (docs == null) return false;
        currentPosition = maxPosition;
        return (docs.get(currentPosition) != null);
    }
    
    public Boolean Next()
    {
        if (docs == null) return false;
        currentPosition++;
        if (currentPosition > maxPosition) return false;
        return (docs.get(currentPosition) != null);    
    }
    
    public int Count()
    {
        if (docs == null) return 0;
        if (docs.size() == 0) return 0;
        return maxPosition + 1;
    }
    
    public Application getApplication()
    {
        return app;
    }
    
    public Document Document()
    {
        SaDocumentInfo doc;
        try
        {
            doc = docs.get(currentPosition);
        }
        catch (Exception e)
        {
            return null;
        }
        
        return new Document(app, doc);
    }
}
