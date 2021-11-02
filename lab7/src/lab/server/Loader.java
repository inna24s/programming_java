package lab.server;

import lab.Hat;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Arrays;
import java.util.Map;

import static lab.server.ManyHats.collection;

public class Loader {
    public static String autosave = "timesave.xml";
    public static void writeFile(String file) throws IOException {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<collection>\n");

            for (String s : ManyHats.getCollection().keySet()) {
                Hat hat = (Hat) ManyHats.getCollection().get(s);
                writer.write("  <Object>\n");

                writer.write("    <key>" + s + "</key>\n");
                writer.write("    <hat>\n");
                writer.write("      <name>" + hat.getName() + "</name>\n");
                writer.write("      <color>" + hat.getColor() + "</color>\n");
                writer.write("      <size>" + hat.getSize() + "</size>\n");
                writer.write("      <things>");
                for(int i = 0; i<hat.checkspace(); i++){
                    writer.write("<item>" + hat.content[i].getName().toString() + "</item>");
                }
                writer.write("</things>\n");
                writer.write("    </hat>\n");
                writer.write("  </Object>\n");
            }
            writer.write("</collection>\n");
            writer.close();
    }
   
    static boolean load( String file, DataBaseHandler db, String username, String password) {
        int s = ManyHats.collection.size();
        int i =0;
        Hat[] hats = new Hat[0];
        hats = ManyHats.collection.values().toArray(hats);
        Arrays.sort(hats);
        for (i = 0; i < s; i++)
            ManyHats.remove(hats[i],username);
        try{
           XmlParser.pars(file, collection);
            for(Map.Entry<String,Hat> entry: collection.entrySet()){
                ManyHats.addH(entry.getKey(), entry.getValue(),username);
                db.addToDB(entry.getKey(), entry.getValue(), username, password);
            }
        } catch (IOException e) {
            System.out.println("При загрузке гардероба произошла ошибка "+e.getLocalizedMessage());
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    static void imload ( String filecontent, DataBaseHandler db, String username, String password) throws IOException, ParserConfigurationException, SAXException {
        FileWriter fos = new FileWriter(autosave);
        String[] content;
        content = filecontent.split("\n");
        for (int j=0; j<content.length; j++){
            fos.write(content[j]);
    }
        fos.close();
        XmlParser.pars(autosave, collection);
        for(Map.Entry<String,Hat> entry: collection.entrySet()){
            ManyHats.addH(entry.getKey(), entry.getValue(),username);
            db.addToDB(entry.getKey(), entry.getValue(), username, password);
        }
    }
}
