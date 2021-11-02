import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class XmlParser {
    public static Hashtable<String, Hat> pars(String filepath, Hashtable<String,Hat> hashtable){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Thing thing = null;
        try {
            File xmlFile = new File(filepath);
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("Object");
            NodeList nodeList1 = document.getElementsByTagName("hat");
            NodeList thingList2 = document.getElementsByTagName("things");
            NodeList itemlist = document.getElementsByTagName("item");
            String key;
            for(int j =0; j<nodeList1.getLength(); j++) {
                Hat hat = getHat(nodeList1.item(j)) ;
                key = getKey(nodeList.item(j));
                if(thingList2.item(j)!= null){
                NodeList child = thingList2.item(j).getChildNodes();
                for(int k =0; k<child.getLength(); k++) {
                Item item = Item.valueOf(getThing(thingList2.item(j), k));
                thing = new Thing(item);
                hat.addthing(thing);}
                }
                hashtable.put(key,hat);
                System.out.println("Добавлено: \n" + "key: " + key +", " + "size: " + hat.getSize() +", " +"color: " + hat.getColor()+ ", " + "вещи в шляпе: " + hat.contentlist());
                   }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
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
            Thing[] array = new Thing[10];
            lang.setColor(getTagValue("color", element));
            lang.setSize(Integer.parseInt(getTagValue("size", element)));
            lang.content = array;
        }
        return lang;
    }

    /**
     * создаем из узла документа строку
     * @param node
     * @return
     */
    private static String getThing(Node node, int i) {
        String thing = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            thing = getTagValue1(i,"item", element);
        }
        return thing;
    }

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
    private static String getTagValue1(int i, String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(i).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}

