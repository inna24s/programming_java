
import java.io.*;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class ManyHats {

    private static Hashtable<String, Hat> collection = new Hashtable<String, Hat>();
    private static Date createdDate = new Date();

    public ManyHats() {
    }

    /**
     * Добавляет в коллекцию новый элемент.
     *
     * @param key  - Название(ключ) элемента коллекции.
     * @param nHat - Новыая Hat.
     */
    public static boolean addNewHat(String key, Hat nHat) {
        if (!(nHat.getColor().equals(""))) {
            collection.put(key, new Hat(nHat.getSize(), nHat.getColor(), nHat.content));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Добавляет предмет в каждую шляпу, подходящую под введенные параметры
     *
     * @param hat Шляпа, в которую нужно добавить предмет
     * @param obj Предмет, который нужно добавить
     */
    public static void addtohat(Hat hat, String obj) {
        if (obj.indexOf(' ') != -1)
            obj = obj.substring(0, obj.indexOf(' '));
        obj = obj.replaceAll("\n", "");
        for(Map.Entry<String,Hat> entry : collection.entrySet())
            if ((entry.getValue().getSize() == hat.getSize()) & ((entry.getValue().getColor()).equals(hat.getColor()))) {
                try {
                    entry.getValue().addthing(new Thing(Item.valueOf(obj)));
                } catch (IllegalArgumentException e) {
                    System.out.println("Введен предмет не из списка/ Название предмета введено с ошибкой(содержит кавычки или другие символы)");
                    break;
                }
            }
        }

   public static void deletefromhat(Hat hat, String obj){
        if (obj.indexOf(' ') != -1)
            obj = obj.substring(0, obj.indexOf(' '));
        obj=obj.replaceAll("\n", "");
        //hat.getColor() = hat.getColor().replaceAll("\"", "");
        for(Map.Entry<String,Hat> entry : collection.entrySet())
            if ((entry.getValue().getSize() == hat.getSize()) & ((entry.getValue().getColor()).equals(hat.getColor()))) {
                try{
                    entry.getValue().deletething(new Thing(Item.valueOf(obj)));
                } catch (IllegalArgumentException e) {
                    System.out.println("Введен предмет не из списка/ Название предмета введено с ошибкой(содержит кавычки или другие символы)");
                    break;}}}

    /**
     * Вывести в стандатный поток вывода все ключи коллекции.
     */
    public static void showHats() {
        System.out.println("Список элементов коллекции:");
       for (Map.Entry<String, Hat> entry : collection.entrySet()) {
           System.out.println("Key = " + entry.getKey() + " " +  entry.getValue()+ " вещи в шляпе: " + entry.getValue().contentlist());
       }
    }


    public static void addfromXML(String file) {
        XmlParser.pars(file, collection);
    }

    /**
     * Вывести в стандартый поток вывода информацию о коллекции...
     */
    public static void getInfo() {
        System.out.println("Дата инициализации: " + createdDate);
        System.out.println("Тип: HashTable");
        System.out.println("Количество элементов: " + collection.size());
    }

    /**
     * Удалить из коллекции все объекты.
     */
    public static void clear() {
        collection.clear();
        System.out.println("Коллекция успешно очищена");
    }

    /**
     * Удаляет из коллекции все шляпы, размер которых меньше ,чем размер указанной
     * * @param hat - Любая Hat.
     */
    public static void remove_greater(Hat hat) {
        collection.entrySet().removeIf(e -> hat.getSize() < collection.get(e.getKey()).getSize());
        System.out.println("Элементы, размер которых больше \"" + hat.getSize() + "\", удалены из коллекции");
    }

    /**
     * Удаляет из коллекции все шляпы, размер которых меньше ,чем размер указанной
     * * @param hat - Любая Hat.
     */
    public static void remove_great(Hat hat) {
        collection.entrySet().removeIf(e ->(hat.getColor().equals(collection.get(e.getKey()).getColor()) & hat.getSize() == (collection.get(e.getKey()).getSize())));
    }

    public static boolean remove(String key) {
        if (collection.remove(key) != null) {
            System.out.println("Элемент успешно удалён");
            return true;
        } else {
            System.out.println("Не удалось удалить элемент");
            return false;
        }
    }
    /**
     * Возвращает коллекцию в текущем состоянии.
     */
    public static Hashtable<String, Hat> getCollection() {
        return collection;
    }

    public static void writeFile(String file) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        for (String s : ManyHats.getCollection().keySet()) {
            Hat hat = (Hat) ManyHats.getCollection().get(s);
            writer.write("KEY = " + s + ", " + "color: " + hat.getColor() + ", " + "size: " + hat.getSize() + ", " + hat.contentlist() + "\n");
        }
        System.out.println("Запись в файл успешна");
        writer.close();
}}