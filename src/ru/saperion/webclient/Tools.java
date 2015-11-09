/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.webclient;
import com.saperion.ngc.iform.field.DateField;
import com.saperion.ngc.iform.field.IntelligentField;
import com.saperion.ngc.iform.field.LookupTextField;
import com.saperion.ngc.iform.field.TextField;
import java.util.Date;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zul.Messagebox;
import ru.saperion.tools.DateWorker;
/**
 * Работа с компонентами веб-клиента
 * @author Драздов Валентин
 */
public class Tools {
    
    /**
     * Данный тип используется для выбора иконок сообщения
     */
    public static enum ICONS
    {
        INFORMATION (org.zkoss.zul.Messagebox.INFORMATION),
        EXCLAMATION (org.zkoss.zul.Messagebox.EXCLAMATION),
        QUESTION (org.zkoss.zul.Messagebox.QUESTION),
        ERROR (org.zkoss.zul.Messagebox.ERROR);
        
        /**
         * Наименование иконки
         */
        private final String sName;
        
        /**
         * Определение иконки
         * @param Name Наименование иконки
         */
        ICONS(String Name)
        {
            sName = Name;
        }
        
        /**
         * Получение наименования иконки
         * @return название
         */
        public String getName()
        {
            return sName;
        }
    }
    
    /**
     * Данный тип используется для работы с кнопками
     */
    public static enum BUTTONS
    {
        NOTHING (-1),
        ABORT (org.zkoss.zul.Messagebox.ABORT),
        CANCEL (org.zkoss.zul.Messagebox.CANCEL),
        IGNORE (org.zkoss.zul.Messagebox.IGNORE),
        NO (org.zkoss.zul.Messagebox.NO),
        OK (org.zkoss.zul.Messagebox.OK),
        YES (org.zkoss.zul.Messagebox.YES),
        RETRY (org.zkoss.zul.Messagebox.RETRY),
        YESNO(org.zkoss.zul.Messagebox.YES + org.zkoss.zul.Messagebox.NO),
        OKCANCEL(org.zkoss.zul.Messagebox.OK + org.zkoss.zul.Messagebox.CANCEL),
        OKCANCELIGNORE(org.zkoss.zul.Messagebox.OK + org.zkoss.zul.Messagebox.CANCEL + org.zkoss.zul.Messagebox.IGNORE),
        YESNOIGNORE(org.zkoss.zul.Messagebox.YES + org.zkoss.zul.Messagebox.NO + org.zkoss.zul.Messagebox.IGNORE);
        
        /**
         * Идентификатор кнопки
         */
        private final int iID;
        
        /**
         * Определение кнопки
         * @param ID идентификатор кнопки
         */
        BUTTONS(int ID)
        {
            iID = ID;
        }
        
        /**
         * Получение идентификатора кнопки
         * @return идентификатор
         */
        public int getID()
        {
            return iID;
        }
    }
    
    /**
     * Вывод сообщения с текстом, заголовком, кнопками и иконкой.
     * @param sMessage Сообщение
     * @param sCaption Заголовок
     * @param button Кнопки
     * @param icon Иконки
     * @return Нажатая кнопка
     * @throws Exception Ошибка прерывания работы сообщения
     */
    public static BUTTONS MessageBox(String sMessage, String sCaption, BUTTONS button, ICONS icon) throws Exception
    {
        int ret = org.zkoss.zul.Messagebox.show(sMessage, sCaption, button.getID() , icon.getName());
        
        for(BUTTONS iButton : BUTTONS.values())
        {
            if (ret == iButton.getID()) return iButton;
        }
        return BUTTONS.NOTHING;
    }
}
