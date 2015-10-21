package ru.saperion;
import ru.saperion.com.Application;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) {

        CursorDoc();
    }

    public static void CursorDoc()
    {
        try
        {
            String sPropertiesPath = "C:\\Users\\pfomchenkov\\Documents\\Java Projects\\RuSaperionJavaLib\\TestApp\\saperion.properties";
            ru.saperion.com.Application oApp;
            oApp = new ru.saperion.com.Application(Application.ConnectorType.CLASSIC, sPropertiesPath);
            System.out.println("Объект Сапериона получен");

            oApp.Login("Администратор", "1", 3);
            System.out.println("Авторизация прошла успешно");

            ru.saperion.com.Cursor cur = oApp.SelectHQL("SERVCARDS", "");
            if (cur.First())
            {
                System.out.println("Курсор получен");
                System.out.println(String.format("Количество записей в курсоре: %d", cur.Count()) );
                do
                {
                    ru.saperion.com.Document doc = cur.Document();
                    System.out.println("Документ получен из курсора");

                    String someProperty = doc.getProperty("F");
                    System.out.println(String.format("Получено свойство F: %s", someProperty));
                    if (doc.Load())
                    {
                        System.out.println("Документ загружен из медии");
                        for (ru.saperion.com.File file: doc.getFiles())
                        {
                            System.out.println(String.format("Имя файла: %s\tРазмер файла: %d", file.getFileName(), file.getFileSize()) );
                        }
                    }
                    else
                    {

                        System.out.println("Документ не был загружен из медии (скорее всего нету файлов)");
                    }
                } while(cur.Next());
            }

            oApp.Logoff();
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
        }
    }

    public static void GenerateReport()
    {
        try
        {
            ru.saperion.com.Application oApp;
            oApp = new ru.saperion.com.Application(Application.ConnectorType.CLASSIC, "C:\\Users\\vdrazdov\\Documents\\NetBeansProjects\\elar-judges-helper\\saperion.properties");
            oApp.Login("Администратор", "1", 3);
            String sReport =  ru.saperion.reports.Csv.generate(oApp, AreaInfo.class, null);
            System.out.print(sReport);
            byte[] unloadContent = sReport.getBytes(Charset.forName("Cp1251"));
            ru.saperion.tools.FileWorker.SaveFile("D:\\test.csv", unloadContent);
            oApp.Logoff();
        }
        catch (Exception e)
        {
            System.out.println(String.format("Ошибка: %s", e.getMessage() ));
        }
    }

}
