/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.com;

import com.saperion.connector.SaClassicConnector;

import com.saperion.rmi.*;
import com.saperion.intf.*;

import java.util.*;
import java.io.*;
/**
 *
 * @author VDrazdov
 */
public class Application {
    
    private SaClassicConnector connector;
   
    /*
    *
    *
    */    
    public enum ConnectorType
    {
        WEB("WebClient", 1),
        CLASSIC("Classic", 0);
        
        private final String sName;
        private final int iType;
        
        ConnectorType(String name, int type)
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
    
    public Application(ConnectorType type, String settingsFile) throws Exception
    {
        try
        {
            switch(type)
            {
                case WEB:
                    connector = ru.saperion.SaClassicConnectorCreator.GetWebClientConnector();
                    break;
                case CLASSIC:
                    connector = ru.saperion.SaClassicConnectorCreator.GetConnectorInstance(settingsFile);
                    break;
            }          
        }
        catch(Exception e)
        {
            throw new Exception(String.format("Произошла ошибка во время создания объекта Application:%n%s", e.getMessage()));
        }

    }
    
    public Application(SaClassicConnector connector)
    {
        this.connector = connector;
    }

    public void Login(String login, String password, int licenseType) throws Exception
    {
        try
        {
            int loginResult = this.connector.logon(login, password, licenseType, "");
            if (loginResult == 0)
            {
                throw new Exception ("Не удалось выполнить авторизацию");
            }
        }
        catch (Exception e)
        {
            throw new Exception(String.format("Произошла ошибка во время логина:%n%s", e.getMessage()));
        }
    }
    
    public void Logoff() throws Exception
    {
        try
        {
            if (connector != null) connector.logoff();
        }
        catch (Exception e)
        {
            throw new Exception (String.format("Произошла ошибка во время разлогина:%n%s", e.getMessage()));
        }
    }
    
    public Cursor SelectHQL(String definition, String query) throws Exception
    {
        try
        {
            SaQueryInfo saQuery = null;
        
            if (query != null)
            {
                if (query.substring(0,0) == "@")
                {
                    saQuery = new SaQueryInfo(query);
                }
            }
            
            if (saQuery == null)
            {
                String sHQL = "from " + definition;
                if (query != null && query.length() > 0)
                {
                    sHQL += "where " + query;
                }

                saQuery = new SaQueryInfo(sHQL);
            }
            return SelectQuery(saQuery);
        }
        catch(Exception e)
        {
            throw new Exception(String.format("Произошла ошибка во время создания запроса:%n%s", e.getMessage()));
        }
    }
    
    public Cursor SelectQuery (SaQueryInfo query) throws Exception
    {
        try
        {
            List<SaDocumentInfo> docs = connector.searchHQL(query);
            return new Cursor(this, docs);            
        }
        catch(Exception e)
        {
            throw new Exception(String.format("Произошла ошибка во время выполнения запроса:%n%s", e.getMessage()));
        }
    }
    
    public InputStream LoadDocumentStream(String xhdoc, boolean bln, int iElement) throws Exception
    {
        InputStream readStream = connector.readDocument(xhdoc, bln, iElement);
        return readStream;
    }
    
    public SaDocInfo LoadDocInfo(String xhdoc) throws Exception
    {
        return connector.getDocumentInfo(xhdoc, true, true);
    }
    
    public SaClassicConnector getConnector()
    {
        return connector;
    }
}
