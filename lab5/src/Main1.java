import java.io.*;
public class Main1 {
    public Main1() { }
    public static void main(String[] args) throws UnsupportedEncodingException {
        ManyHats manyHats =null;
        boolean exit = false;
        if (args.length != 0) {
            ManyHats.addfromXML(args[0]);
        }
        else{
            System.out.println("Если вы хотели добавить элементы из вашего xml документа, то начните сначала и укажите в аргументе командной строки название вашего файла с указанием расширения, например file.xml, если нет");
             manyHats = new ManyHats();}
        try {
            Comand comand = new Comand(manyHats);
        while (!exit)
            exit= comand.doCommand();
        ManyHats.writeFile(Comand.getAutosave());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
    }
}



