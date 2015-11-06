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
 * Отправная точка для работы с Саперион. Поддерживает работу напрямую с Saperion Classic Connector и с Saperion Web Client
 * @author Драздов Валентин
 */
public class Application {
    
    /*
     * Объект, хранящий подключение к Саперион 
     */
    private SaClassicConnector connector;
    
   
    /*
    * Данный тип используется для определения типа подключения к Саперион
    */    
    public enum ConnectorType
    {
        WEB("WebClient", 1),
        CLASSIC("Classic", 0);
        
        private final String sName;
        private final int iType;
        
        /**
         * Описание типа подключение
         * @param name Название подключения
         * @param type Номер подключения
         */
        ConnectorType(String name, int type)
        {
            sName = name;
            iType = type;
        }
        
        /**
         * Получить название типа подключения
         * @return Название типа подключения
         */
        public String getName()
        {
            return sName;
        }
        
        /**
         * Получить номер типа подключения
         * @return Номер типа подключения
         */
        public int getType()
        {
            return iType;
        }
    }
    
    /**
     * При создании экземпляра объекта типа Application происходит автоматическое подключение к службам Saperion
     * @param type определяет метод подключения к Saperion 
     * @param settingsFile путь к файлу настроек для подключения к Classic Connector. Пример файла можно найти по адресу %SAPERION%\scr\scr-classicconnector\config\saperion.properties. В параметре brokerhost= необходимо прописать адрес и порт сервера Saperion, к которому выполняется подключение. 
     * <p>В случае использования подключения к WEB – в данный параметр передать пустую строку.
     * @throws Exception 
     */
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
    
    /**
     * Данный вариант конструктора следует использовать только тогда ,когда есть необходимость использовать Connector вне библиотеки
     * @param connector Объект соединения с Саперион
     */
    public Application(SaClassicConnector connector)
    {
        this.connector = connector;
    }

    /**
     * Данный метод производит авторизацию в Saperion
     * @param login Имя пользователя
     * @param password Пароль пользователя
     * @param licenseType Лицензия
     * @throws Exception 
     */
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
    
    /**
     * Данный метод предназначен для выхода пользователем из системы
     * @throws Exception 
     */
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
    
    /**
     * Данная функция  является аналогом функции Application.SelectSQL из COM-версии
     * @param definition таблица, из которой необходимо получить список результатов
     * @param query запрос для получения фильтрованного списка
     * Важным отличием от COM-версии является то, что здесь используется не SQL, а HQL, который хоть и похож на первый, но, имеет свои особенности. Рекомендуется почитать про этот язык отдельно в случае, если ваш запрос не сработает. 
     * <br>Как и в COM-версии Сапериона имеется три варианта выполнения функции в зависимости от содержания параметра query:
     * <p>Query = пусто: будет выполнен поиск без фильтрации
     * <br>Query = условия: условия будут вставлены в запрос после where.
     * <br>Query = @полный HQL запрос [начинается именно с символа @]: параметр definition будет проигнорирован и выполнится тот запрос, который стоит после символа @. 
     * @return {@link Cursor Курсор} с данными, полученными при запросе
     * @throws Exception 
     */
    public Cursor SelectHQL(String definition, String query) throws Exception
    {
        try
        {
            SaQueryInfo saQuery = null;
        
            if (query != null)
            {
                if (query.substring(0,0) == "@")
                {
                    query = query.substring(1, query.length());
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
    
    /**
     * Выполняет запрос из текущего объекта Connector по уже составленному объекту запроса из ClassicConnector. Используется в крайне редких случаях.
     * @param query Запрос из ClassicConnector
     * @return Курсор с данными, полученными при запросе
     * @throws Exception 
     */
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
    
    /**
     * Данная функция служит для получения потока с контентом документа
     * @param xhdoc HexUID документа
     * @param currentRevision загрузка именно той ревизии, к которой относится HexUID
     * @param iElement номер элемента (файла) документа
     * @return Содержимое элемента документа в виде потока. Примечание: Для получения контента в виде массива байт необходимо в первую очередь инициализировать массив байт по размеру контента и выполнить функцию потока read, в которую необходимо передать инициализированный массив.
     * @throws Exception 
     */
    public InputStream LoadDocumentStream(String xhdoc, boolean currentRevision, int iElement) throws Exception
    {
        InputStream readStream = connector.readDocument(xhdoc, currentRevision, iElement);
        return readStream;
    }
    
    /**
     * Данная функция возвращает объект с информацией о документе в формате Saperion Classic Connector
     * @param xhdoc HexUID документа
     * @return Информация о документе в формате Saperion Classic Connector
     * @throws Exception 
     */
    public SaDocInfo LoadDocInfo(String xhdoc) throws Exception
    {
        return connector.getDocumentInfo(xhdoc, true, true);
    }
    
    /**
     * Получение оригинального объекта-коннектора Сапериона
     * @return Объект-коннектор к Сапериону
     */
    public SaClassicConnector getConnector()
    {
        return connector;
    }
}
