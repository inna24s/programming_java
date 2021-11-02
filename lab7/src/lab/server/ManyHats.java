package lab.server;

import lab.Hat;
import java.util.*;


public class ManyHats {
    private static int maxCollectionElements = 256;

    static int getMaxCollectionElements() {
        return maxCollectionElements;
    }
    public static Hashtable<String, Hat> collection = new Hashtable<String, Hat>();
    private static Date createdDate = new Date();

    public ManyHats() {}

    /**
     * Добавляет в коллекцию новый элемент.
     * @param key  - Название(ключ) элемента коллекции.
     * @param a - Новыая Hat.
     * @return true, если шляпа добавлена, false - если нет
     */
    public static boolean addNewHat(String key, Hat a) {
        if (!(a.getColor().equals(""))) {
            collection.put(key, new Hat(a.getSize(), a.color, a.getName(), a.content));
            return true;
        } else {
            return false;
        }
    }

    public static boolean addNewHat2(Hashtable f, String key, Hat a) {
        if (!(a.getColor().equals(""))) {
            f.put(key, new Hat(a.getSize(), a.color, a.getName(), a.content));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Вывести в стандатный поток вывода информацию по каждой шляпе.
     */
    public static void showHats() {
        System.out.println("Список элементов коллекции:");
       for (Map.Entry<String, Hat> entry : collection.entrySet()) {
           System.out.println("Key = " + entry.getKey() + " " +  entry.getValue()+ " вещи в шляпе: " + entry.getValue().contentlist());
       }
    }

    /**
     * Добавляет шляпу в гардероб
     *
     * @param a Шляпа, которую нужно добавить
     * @return true, если шляпа успешно добавлена
     */
    synchronized public static boolean addH(String key, Hat a, String  username) {
        if (!(a.color.equals(""))) {
            a.setUser(username);
            collection.put(key, a);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Вывести в стандартый поток вывода информацию о коллекции...
     */
    public static String getInfo() {
        String a = null;
        a= a +"Дата инициализации: " + createdDate+ "\n";
         a = a+ "Тип: HashTable\n";
        a = a +"Количество элементов: " + collection.entrySet().stream().count();
        return a;
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
     * @param hat  шляпа
     */
    synchronized public static boolean remove_greater(Hat hat, String username, String pass) {
        if (username.equals(hat.getUser())&& pass.equals(hat.getPass())) {
            collection.entrySet().removeIf(e -> (hat.getName().compareTo(collection.get(e.getKey()).getName())) < 0);
            return (!(collection.entrySet().removeIf(e -> (hat.getName().compareTo(collection.get(e.getKey()).getName())) < 0)));
        }
        return false;
    }

/*    public static Hashtable<String, Hat> chooseHats(Hat hat, String username, String pass) {
        Hashtable<String, Hat> a = null;

            for (Map.Entry<String, Hat> entry : collection.entrySet()) {
                if (hat.getName().compareTo(collection.get(entry.getKey()).getName()) < 0) a.put(entry.getKey(), entry.getValue());
            }
            return a;

    }*/
    /**
     * Удаляет шляпу из гардероба
     * @param a Шляпа, которую нужно удалить
     */
   synchronized public static boolean remove(Hat a, String username) {
       if (username.equals(a.getUser())) {
           boolean result = false;
           collection.entrySet().removeIf(e -> ((a.getSize() == (collection.get(e.getKey()).getSize())) & (a.getName().equals(collection.get(e.getKey()).getName())) & ((a.color).equals(collection.get(e.getKey()).color))));
           result = !(collection.entrySet().removeIf(e -> ((a.getSize() == (collection.get(e.getKey()).getSize())) & (a.getName().equals(collection.get(e.getKey()).getName())) & ((a.color).equals(collection.get(e.getKey()).color)))));
           return result;
       }
       return false;
   }


    /**
     * Возвращает коллекцию в текущем состоянии
     * @return колекцию
     */
    public static Hashtable<String, Hat> getCollection() {
        return collection;
    }

}