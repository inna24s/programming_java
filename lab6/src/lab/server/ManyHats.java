package lab.server;

import lab.Hat;
import java.util.*;
import java.util.stream.Stream;

public class ManyHats {
    private static int maxCollectionElements = 256;
    private static boolean hasChanged = false;
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
    public static boolean remove_greater(Hat hat) {
        if(collection.entrySet().removeIf(e -> (hat.getName().compareTo(collection.get(e.getKey()).getName()))<0));
        return (!(collection.entrySet().removeIf(e -> (hat.getName().compareTo(collection.get(e.getKey()).getName()))<0)));
    }

    public boolean removeGreater(Hat hat) {
        collection.entrySet().stream().filter(e ->(hat.getName().compareTo(collection.get(e.getKey()).getName()))<0).forEach(e -> collection.remove(e));
        return (!(collection.entrySet().removeIf(e -> (hat.getName().compareTo(collection.get(e.getKey()).getName()))<0)));
    }

    /**
     * Удаляет шляпу из гардероба
     * @param a Шляпа, которую нужно удалить
     */
    public static boolean remove(Hat a) {

        collection.entrySet().removeIf(e ->((a.getSize() == (collection.get(e.getKey()).getSize())) & (a.getName().equals(collection.get(e.getKey()).getName())) & ((a.color).equals(collection.get(e.getKey()).color)))) ;
            return (!(collection.entrySet().removeIf(e ->((a.getSize() == (collection.get(e.getKey()).getSize())) & (a.getName().equals(collection.get(e.getKey()).getName())) & ((a.color).equals(collection.get(e.getKey()).color))))));
    }
    /**

     * Удаляет из коллекции все шляпы, эквивалентные указанной по цвету и размеру
     * @param hat  Любая Hat.
     */
    public static void remove_all(Hat hat) {
       collection.entrySet().removeIf(e ->(hat.getSize() == (collection.get(e.getKey()).getSize())));
    }

    /**
     * Удаляет шляпу по ключу
     * @param key - ключ
     * @return true, если шляпа удалена, иначе false
     */
   /* public static boolean remove(String key)  {
        if (collection.remove(key) != null) {
            System.out.println("Элемент успешно удалён");
            return true;
        } else {
            System.out.println("Не удалось удалить элемент");
            return false;
        }
    }*/

    /**
     * Возвращает коллекцию в текущем состоянии
     * @return колекцию
     */
    public static Hashtable<String, Hat> getCollection() {
        return collection;
    }

}