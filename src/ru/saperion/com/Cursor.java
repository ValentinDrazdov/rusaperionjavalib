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
 * Работа со списком документов, полученных в результате запроса
 * @author Драздов Валентин
 */
public class Cursor {
    /**
     * Текущая позиция курсора
     */
    private int currentPosition;
    
    /**
     * Максимальная позиция курсора (количество записей)
     */
    private int maxPosition;
    
    /**
     * Объект для обратной связи с приложением
     */
    private Application app;
    
    /**
     * Список документов, полученных из запроса к БД. 
     */
    private List<SaDocumentInfo> docs;
    
    /**
     * Как правило, корректное использование библиотеки не подразумевает самостоятельное создание экземпляров данного класса. Экземпляр данного класса должен возвращаться функциями {@link Application#SelectHQL} и {@link Application#SelectQuery}
     * @param app экземпляр класса {@link Application} для реализации обратной связи и возможности выполнять загрузку документов из медии в дальнейшем
     * @param docs инициализирующий список, содержащий базовую информацию о документах, полученную из базы данных
     */
    public Cursor(Application app, List<SaDocumentInfo> docs)
    {
        this(docs);
        this.app = app;
    }
    
    /**
     * Аналогично вышеописанному конструктору. Не использует экземпляр класса Application. Предполагается использовать в тех случаях, когда не нужно загружать документы из медии
     * @param docs инициализирующий список, содержащий базовую информацию о документах, полученную из базы данных
    */
    public Cursor(List<SaDocumentInfo> docs)
    {
        this.docs = docs;
        currentPosition = 0;
        maxPosition = this.docs.size() - 1;
        
    }
    
    /**
     * Устанавливает в качестве текущего документа самый первый, полученный из базы данных
     * @return TRUE, если курсор установлен на первую позицию
     * <br>FALSE, если не удалось установить курсор на первую позицию, либо нет документов
     */
    public Boolean First()
    {
        if (docs == null) return false;
        currentPosition = 0;
        return (docs.get(currentPosition) != null);
    }
    
    /**
     * Устанавливает в качестве текущего документа самый последний, полученный из базы данных. 
     * @return TRUE, если курсор установлен на последнюю позицию
     * <br>FALSE, если не удалось установить курсор на последнюю позицию, либо нет документов
     */
    public Boolean Last()
    {
        if (docs == null) return false;
        currentPosition = maxPosition;
        return (docs.get(currentPosition) != null);
    }
    
    /**
     * Устанавливает в качестве текущего документа следующий после текущего
     * @return TRUE, если курсор установлен на cледующую позицию позицию
     * <br>FALSE, если не удалось установить курсор на следующую позицию (был достигнут конец курсора,либо нет документов)
     */
    public Boolean Next()
    {
        if (docs == null) return false;
        currentPosition++;
        if (currentPosition > maxPosition) return false;
        return (docs.get(currentPosition) != null);    
    }
    
    /**
     * Получить количество документов в курсоре
     * @return Количество документов в курсоре. 
     * <br>Если документы еще не были получены - будет возвращен 0 
     */
    public int Count()
    {
        if (docs == null) return 0;
        if (docs.size() == 0) return 0;
        return maxPosition + 1;
    }
    
    /**
     * Получить объект {@link Cursor#app приложения}, от которого был получен данный курсор
     * @return Объект {@link Cursor#app приложения}, от которого был получен данный курсор
     */
    public Application getApplication()
    {
        return app;
    }
    
    /**
     * Получить объект документа, на который указывает курсор
     * @return {@link Document Документ}
     */
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
