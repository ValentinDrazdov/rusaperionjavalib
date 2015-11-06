/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.reports;
import com.saperion.intf.*;
import com.saperion.ngc.iform.IntelligentFormView;
import com.saperion.rmi.SaQueryInfo;

import ru.saperion.com.*;
import ru.saperion.webclient.forms.*;

/**
 * Интерфейс для описания состава строки отчета и ее обработки. Для работы с {@link ReportGenerator генератором отчетов} необходимо объявить класс, соответствующий данному интерфейсу
 * @author Драздов Валентин
 */
public interface IReportLine {
    
    /**
     * При выполнении данного метода необходимо получить или высчитать все требуемые поля для строки отчета из объекта документа
     * @param oDoc Объект {@link Document документа} из которого идет получение свойств
     */
    public void loadLine(Document oDoc);  
    
    /**
     * Метод используется в цикле в том случае, если найдена строка БД с {@link #getKey() ключем}, который соответствует уже ранее обработанному {@link #getKey() ключу}. Как правило, выполняется для сложения определенных значений (суммирования)
     * @param line Объект того же типа для проведения операции комбинации.
     */
    public void combine(IReportLine line);
    
    /**
     * Данная функция должна возвращать содержимое поля-ключа (либо сумму нескольких полей). Именно по этой функции вычисляется повторяемость строки.
     * @return Ключевые поля в виде строки
     */
    public String getKey();
    
    /**
     * Данная функция используется при генерации CSV-файла, должна возвращать полную строку шапки.
     * @return Полная строка шапки для CSV-таблицы.
     */
    public String getFirstLine();
    
    /**
     * Данная функция должна создавать запрос для формирования отчета.
     * <p>В случае, если объект {@link Mask маски} не передается – запрос должен выполнятся с заранее предопределенными условиями (либо без них).
     * <p>Если объект {@link Mask маски} передан – критерии запроса должны быть собраны с полей формы через метод {@link Mask#FieldToQuery}
     * @param oMask Объект {@link Mask маски}.
     * @return Объект запроса в формате Saperion Classic Connector
     * @throws Exception в случае возникновения ошибок, определенных классом, реализующим данный интерфейс
     */
    public SaQueryInfo getSaperionQuery(Mask oMask) throws Exception;
    
    /**
     * Данная функция используется при генерации CSV-файла, должна возвращать значение полей строки в качестве единой строчки с разделителями.
     * @return строка с данными полей для записи в CSV-таблицу
     */
    @Override 
    public String toString();
    
            
}
