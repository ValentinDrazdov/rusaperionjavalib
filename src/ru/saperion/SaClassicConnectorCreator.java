/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion;
import com.saperion.connector.*;
import com.saperion.ngc.model.ClassicConnectorProvider;
import org.apache.log4j.Logger;
/**
 *
 * @author VDrazdov
 */
public class SaClassicConnectorCreator {
    private static final Logger LOG = Logger.getLogger(ru.saperion.SaClassicConnectorCreator.class);
    
    public static SaClassicConnector GetWebClientConnector() throws Exception
    {
        try
        {
            LOG.info("Подключаемся к провайдеру");
            
            ClassicConnectorProvider provider = new com.saperion.ngc.model.ClassicConnectorProvider();

            if (provider == null) {throw new Exception ("Не удалось получить провайдер");}
            
            LOG.debug ("Провайдер подключен. Подключаемся в Saperion Classic Connector");

            SaClassicConnector connector = provider.get();
            if (connector == null) {throw new Exception ("Не удалось получить Saperion Classic Connector из провайдера");}
            LOG.info ("Saperion Classic Connector подключен");

            provider = null;
            return connector;
        }
        catch (Exception e)
        {
            LOG.error(String.format("Возникла ошибка при подключении к Saperion Classic Connector через WEB:%n%s", e.toString()));
            throw new Exception(String.format("Возникла ошибка при подключении к Saperion Classic Connector через WEB:%n%s", e.toString()));
        }
        
    }
    
    public static SaClassicConnector GetConnectorInstance(String sSetiingsFile) throws Exception
    {
        try
        {
            SaClassicConnector connector = null;
            LOG.info (String.format("Создается Saperion Classic Connector на основе файла '%s'", sSetiingsFile));   
            java.io.File check = new java.io.File(sSetiingsFile);
            if (check == null){throw new Exception (String.format("Не удалось получить доступ к файлу настроек '%s'", sSetiingsFile));};
            if (check.exists() == false) {throw new Exception (String.format("Не удалось найти файл настроек '%s'", sSetiingsFile));};
            connector = new SaClassicConnectorImpl(sSetiingsFile);
            LOG.info ("Saperion Classic Connector создан");
            return connector;
        }
        catch (Exception e)
        {
            LOG.error(String.format("Возникла ошибка при создании объекта Saperion Classic Connector: %s", e.toString()));
            throw new Exception(String.format("Возникла ошибка при создании объекта Saperion Classic Connector: %s", e.toString()));
        }
    }
}
