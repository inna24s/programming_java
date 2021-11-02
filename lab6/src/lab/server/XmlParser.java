package lab.server;
import lab.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Класс для работы с xml документом
 */
public class XmlParser {
    public static Hashtable<String, Hat> pars(String filepath, Hashtable<String,Hat> hashtable) throws ParserConfigurationException, SAXException, IOException {
        // Получение фабрики, чтобы после получить билдер документов.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Получили из фабрики билдер, который парсит XML, создает структуру Document в виде иерархическом виде
        DocumentBuilder builder;
        Thing thing = null;

        File xmlFile = new File(filepath);
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("Object");
        NodeList nodeList1 = document.getElementsByTagName("hat");
        NodeList thingList2 = document.getElementsByTagName("things");
        String key;
        for (int j = 0; j < nodeList1.getLength(); j++) {
            Hat hat = getHat(nodeList1.item(j));
            key = getKey(nodeList.item(j));
            if (thingList2.item(j) != null) {
                NodeList child = thingList2.item(j).getChildNodes();
                for (int k = 0; k < child.getLength(); k++) {
                    Item item = Item.valueOf(getThing(thingList2.item(j), k));
                    thing = new Thing(item);
                    hat.addthing(thing);
                }
            }
            hashtable.put(key, hat);
            System.out.println("Добавлено: \n" + "key: " + key + ", " + "name: " + hat.getName() + ", " + "size: " + hat.getSize() + ", " + "color: " + hat.getColor() + ", " + "вещи в шляпе: " + hat.contentlist());
        }
        return hashtable;
    }


    /**
     * Создает из узла документа объект Hat
     * @param node - нода
     * @return
     */
    private static Hat getHat(Node node) {
        Hat lang = new Hat();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            Thing[] array = new Thing[1000000];
            lang.setName(getTagValue("name", element));
            lang.setColor(getTagValue("color", element));
            lang.setSize(Integer.parseInt(getTagValue("size", element)));
            lang.content = array;
        }
        return lang;
    }

    /**
     * Создаем из узла документа строку
     * @param node - нода
     * @param i - для считывания нескольких строк
     * @return название вещи
     */
    private static String getThing(Node node, int i) {
        String thing = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            thing = getTagValue1(i,"item", element);
        }
        return thing;
    }

    /**
     * Создаем из узла документа строку - ключ
     * @param node - нода
     * @return ключ
     */
    private static String getKey(Node node) {
        String key = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            key=getTagValue("key", element);
        }
        return key;
    }

    /**
     * Метод для получения значения элемента по указанному тегу
     * @param tag - тег
     * @return значение элемента
     */
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
    /**
     * Метод для получения значений элементов по указанному тегу
     * @param tag - тег
     * @param i - для получения нескольких значений элементов
     * @return значение элемента
     */
    private static String getTagValue1(int i, String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(i).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}

