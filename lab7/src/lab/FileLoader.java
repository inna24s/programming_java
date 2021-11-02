package lab;
import java.io.*;

public class FileLoader {
    /**
     * Читает файл
     * @param filename имя файла
     * @param showProgressCat если true, в System.out будет отправляться красивая полоса прогресса
     * @return содержимое в виде строки
     * @throws IOException если что-то пойдет не так
     */
    public static String getFileContent(String filename, boolean showProgressCat) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
            long fileSize = new File(filename).length();
            StringBuilder fileContent = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null)
                fileContent.append(line +"\n");

            return fileContent.toString();
        }
    }

    /**
     * Читает файл, регулярно записывает текущий прогресс в System.out
     * @param filename имя файла
     * @return содержимое в виде строки
     * @throws IOException если что-то пойдет не так
     */
    public static String getFileContent(String filename) throws IOException {
        return getFileContent(filename, true);
    }
}
