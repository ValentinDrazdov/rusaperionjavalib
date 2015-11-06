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
 * Работа с документом
 * @author Драздов Валентин
 */
public class Document {
    /**
     * HexUID документа
     */
    private String sXHDOC;
    
    /**
     * Свойства документа
     */
    private Map<String, String> props;
    
    /**
     * Объект {@link Application приложения}, от которого был получен документ 
     */
    private Application app;
    
    /**
     * Структура с информацией о документе, полученная из БД через Saperion Classic Connector
     */
    private SaDocumentInfo documentInfo;
    
    /**
     * Структура с подробной информацией о содержимом документа, получаемая из мадии через Saperion Classic Connector
     */
    private SaDocInfo docInfo;
    
    /**
     * Массив с {@link File файлами} документа, полученными из медии
     */
    private File[] files;
    
    /**
     * Данный тип используется при сохранении файлов из документа
     */
    public enum SaveType
    {
        WEB("Web", 1),
        CLASSIC("Classic", 0);
        
        private final String sName;
        private final int iType;
        
        /**
         * Объявление типа сохранения файлов документа
         * @param name {@link SaveType#sName Название типа сохранения}
         * @param type {@link SaveType#iType Номер типа сохранения}
         */
        SaveType(String name, int type)
        {
            sName = name;
            iType = type;
        }
        
        /**
         * Получить {@link SaveType#sName название типа сохранения}
         * @return {@link SaveType#sName Название типа сохранения}
         */
        public String getName()
        {
            return sName;
        }
        
        /**
         * Получить {@link SaveType#iType номер типа сохранения}
         * @return {@link SaveType#iType Номер типа сохранения}
         */
        public int getType()
        {
            return iType;
        }
    }
    
    /**
     * Данный конструктор создает пустой объект документа и инициализирует базовый объект {@link Application приложения}
     * @param app экземпляр класса {@link Application} для реализации обратной связи и возможности выполнять загрузку документа из медии (метод {@link Document#Load})
     */
    public Document(Application app)
    {
        this();
        this.app = app;
    }

    /**
     * Как правило, корректное использование библиотеки не подразумевает самостоятельное создание экземпляров данного класса. Экземпляр данного класса должен возвращаться функцией {@link Cursor#Document}
     * @param app экземпляр класса {@link Application} для реализации обратной связи и возможности выполнять загрузку документа из медии (метод {@link Document#Load})
     * @param docInfo  структура, полученная из Saperion Classic Connector.
     */
    public Document(Application app, SaDocumentInfo docInfo)
    {
        this(docInfo);
        this.app = app;
    }

    /**
     * Данный конструктор создает пустой объект документа
     */
    public Document()
    {
        props = new HashMap<String, String>();
    }

    /**
     * Как правило, корректное использование библиотеки не подразумевает самостоятельное создание экземпляров данного класса. Не использует экземпляр класса {@link Application}. Предполагается использовать в тех случаях, когда не нужно загружать документ из медии.
     * @param documentInfo структура, полученная из Saperion Classic Connector.
     */
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

    /**
     * Данная функция загружает документ из медии. После загрузки становится доступна работа с файлами документа: известны названия файлов, размер, содержимое. Для удобства работы с файлами был пересмотрен классический концепт COM-объектов сапериона и был введен новый класс {@link File}.
     * @return TRUE в случае успешной загрузки документа из медии<br>
     * Функция FALSE, если в Саперионе отсутствует информация о документе по XHDOC и если количество файлов в документе = 0. В случае ошибки будет выдана ошибка типа Exception с описанием.
     * @throws Exception в случае отказа в загрузке документа
     */
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

    /**
     * Данная функция загружает документ из медии. После загрузки становится доступна работа с файлами документа: известны названия файлов, размер, содержимое. Для удобства работы с файлами был пересмотрен классический концепт COM-объектов сапериона и был введен новый класс {@link File}.
     * @param sXHDOC {@link Document#sXHDOC HexUID} документа
     * @return TRUE в случае успешной загрузки документа из медии<br>
     * Функция FALSE, если в Саперионе отсутствует информация о документе по XHDOC и если количество файлов в документе = 0. В случае ошибки будет выдана ошибка типа Exception с описанием.
     * @throws Exception в случае отказа в загрузке документа.
     */
    public Boolean Load(String sXHDOC) throws Exception
    {
        this.sXHDOC = sXHDOC;
        return Load();
    }

    /**
     * Получить {@link Document#sXHDOC HexUID} документа
     * @return {@link Document#sXHDOC HexUID} документа
     */
    public String HexUID()
    {
        return sXHDOC;
    }
    
    /**
     * Получить {@link Document#documentInfo структуру с информацией о документе из БД}
     * @return {@link Document#documentInfo Структура с информацией о документе из БД}
     */
    public SaDocumentInfo getDocumentInfo()
    {
        return documentInfo;
    }
    
    /**
     * Получить {@link Document#docInfo структуру с информацией о документе из Медии}
     * @return {@link Document#docInfo Структура с информацией о документе из Медии}
     */
    public SaDocInfo getDocInfo()
    {
        return docInfo;
    }
    
    /**
     * Получить объект {@link Cursor#app приложения}, от которого был получен данный документ
     * @return Объект {@link Cursor#app приложения}, от которого был получен данный документ
     */
    public Application getApplication()
    {
        return app;
    }

    /**
     * Получить свойство документа
     * @param name Название свойства
     * @return Значение свойства. В случае, если свойство отсутствует - будет возвращена пустая строка.
     */
    public String GetProperty(String name)
    {
        if (props.containsKey(name)) return props.get(name);

        return "";
    }

    /**
     * Получить массив объектов типа {@link File}, которые содержат информацию о файлах в документе.
     * @return Массив объектов типа {@link File}, которые содержат информацию о файлах в документе.
     * @throws Exception в случае отсутствия документов (вызов до {@link Document#Load() загрузки})
     */
    public File[] getFiles() throws Exception
    {
        if (files == null) throw new Exception ("Документ не содержит файлов или еще не был загружен");
        return files;
    }
    
    /**
     * Получить количество элементов в документе
     * @return Количество элементов в документе
     */
    public int NumElems() 
    {
        if (files == null) return 0;
        return files.length;
    }
    
    /**
     * Получить элемент документа
     * @param index Номер элемента в документе (от 1 до {@link Document#NumElems() количества элементов})
     * @return Объект типа {@link File}
     * @throws Exception <ul><li>в случае отсутствия документов (вызов до {@link Document#Load() загрузки})
     * <li>в случае попытки получить элемент вне диапозона (от 1 до {@link Document#NumElems() количества элементов})</ul> 
     */
    public File SubDocument(int index) throws Exception
    {
        index--;
        if (files == null)  throw new Exception ("Документ не содержит файлов или еще не был загружен");
        if (index < 0 || index >= files.length) throw new Exception ("Был запрпошен документ с несуществующим индексом");
        return files[index];
    }
    
    /**
     * Сохранить файл из документа
     * @param index Номер элемента в документе (от 1 до {@link Document#NumElems() количества элементов})
     * <p>В случае, если будет указан 0 - будут сохранены все документы.
     * @param type Так как изначально с Saperion Classic Connector работа может вестись напрямую (Classic) и через веб клиент (Web), то, соответственно, и методов сохранения два (для работы напрямую и через браузер). 
     * <br>*При сохранении через браузер пользователю будет предложено выбрать место для сохранения файла. 
     * <p>Категорически не рекомендуется использовать индекс 0 при сохранении через веб, так как при большом количестве файлов процесс сохранения может затянуться. 
     * @param path адрес директории для сохранения файлов при работе через режим Classic. При использовании режима Web – игнорируется
     * @return TRUE - в случае успешного сохранения<br>FALSE - в случае штатного отказа от сохранения (если не возник Exception)
     * @throws Exception <ul><li>в случае отсутствия документов (вызов до {@link Document#Load() загрузки})
     * <li>в случае попытки получить элемент вне диапозона (от 1 до {@link Document#NumElems() количества элементов})
     * <li>в случае возникновении ошибок при сохранении</ul> 
     */
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
