/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.saperion.com;

import com.saperion.intf.*;
import java.io.*;
/**
 * Работа с файлом, полученным из документа
 * @author Драздов Валентин
 */
public class File {
    /**
     * Объект {@link Application приложения}, от которого был получен документ 
     */
    private Application app;
    
    /**
     * Информация о файле, полученная из медии
     */
    private SaDocInfo.ElementInfo element;
    
    /**
     * Название файла
     */
    private String fileName;
    
    /**
     * Размер файла
     */
    private long fileSize;
    
    /**
     * Тип файла, полученный из медии
     */
    private SaDocInfo.ElementType elemType;
    /**
     * Содержимое файла
     */
    private byte[] content;

    /**
     * Общая информация о файле, полученнавя из медии
     */
    private SaDocInfo docInfo;
    
    /**
     * Данный конструктор создает объект файла, который позволяет получить всю необходимую информацию о файле из архива
     * @param doc Объект Х@link Document документа}
     * @param elementNo Номер элемента
     * @throws Exception в случае неудачной загрузки из медии
     */
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
    
    /**
     * Получить {@link File#fileName имя файла}
     * @return {@link File#fileName Имя файла}
     */
    public String getFileName()
    {
        return fileName;
    }
   
    /**
     * Получить {@link File#fileSize размер файла}
     * @return {@link File#fileSize Размер файла}
     */
    public Long getFileSize()
    {
        return fileSize;
    }
    
    /**
     * Получить {@link File#elemType тип файла}
     * @return {@link File#elemType Тип файла}
     */
    public SaDocInfo.ElementType getElemType()
    {
        return elemType;
    }
    
    /**
     * Получить {@link File#content содержимое файла в байтах}
     * @return {@link File#content Содержимое файла в байтах}
     */
    public byte[] getContent()
    {
        return content;
    }
    
    /**
     * Получить {@link File#docInfo общую информация о файле, полученнавя из медии}
     * @return {@link File#docInfo Общая информация о файле, полученнавя из медии}
     */
    public SaDocInfo getSaDocInfo()
    {
        return docInfo;
    }
}
