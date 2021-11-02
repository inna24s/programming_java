import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
public class Comand {
    private static String autosave = "autosave.txt";
    private ManyHats collection;
    private boolean needExit;
    boolean multiline = false;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Comand(ManyHats collection) {
        this.collection = collection != null ? collection : new ManyHats();
        this.needExit = false;
    }

    public static String getAutosave(){return autosave;}


    public boolean doCommand() throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите команду (чтобы увидеть список всех команд, введите man): ");
            String command = scanner.nextLine();
            String[] commands = command.split("\\s+");
                switch (commands[0]) {
                    case "insert": {
                        if (commands.length < 2) {
                            System.out.println("Отсутсвуют аргументы команды. Чтобы добавить элемент введите: insert <ключ>");
                        } else {
                            System.out.println("Здесь вы можете добавить новую шляпу в гардероб. Для этого введите текст в формате json так, как представленно на примере \n " +
                                    "\"{\"size\": <положительное целое число>, \"color\": <строка>} Если лень вводить, просто копируйте\" \n " +
                                    "{\"size\": 5, \"color\": \"red\"}\n" +
                                    "Если вы хотите создать шляпу, в которой сразу будут лежать какие-либо предметы, вам следует набрать следующий текст:  \n " +
                                    " \"{\"size\": <целое положительное число>, \"color\": <строка>, \"things\": [{\"item\": <строка>}, ... {\"item\": <строка>}]}\" \n " +
                                    "\"{\"size\": 5, \"color\": \"red\", \"things\": [{\"item\": \"SOAP\"}, {\"item\": \"TOWEL\"}]}\" \n " +
                                    "Будьте внимательны! Размер шляпы(size) показывает, какое количество предметов может в неё поместиться. \n " +
                                    "Если вы попытаетесь положить в неё больше предметов, чем позволяет размер, будет создана шляпа, в которой будут лежать первые несколько предметов по возможному количеству. \n " +
                                    "Оставшиеся будут проигнорированны. \n " +
                                    "ВАЖНО! Вы можете положить только предметы из этого списка: \n " +
                                    "зубная щётка(TOOTHBRUSH), зубной порошок (DENTIFRIECE), мыло (SOAP), полотенце (TOWEL), носовой платок (CHIEF),\n " +
                                    " носки (SOCKS), гвоздь (NAIL), проволока (COPPERWIRE).\n  " +
                                    "Обязательно используйте английский язык и заглавные буквы при вводе названий предметов, как указанно в скобках.\n " +
                                    "Каждый из предметов может лежать в шляпе только в одном экземпляре. \n " +
                                    "Если несколько раз какой-то предмет, он всё равно будет добавлен только один раз.\n " +
                                    "Если вы хотите вернуться к меню введите man.");
                            String text = multiline ? getMultilineCommand(reader) : reader.readLine();
                            if (!check(text)) {
                                if (text.equals("man")) {
                                    System.out.print("");
                                } else {
                                    if (!commands[1].equals("")) {
                                        try {
                                            ManyHats.addNewHat(commands[1], HatMaker.makeHatFromJSON(text));
                                            System.out.println("Элемент успешно добавлен в коллекцию.");
                                        } catch (Exception e) {
                                            System.out.println("Не получилось создать шляпу: " + e.getMessage());
                                        }
                                    } else System.out.println("Ошибка парсинга");
                                }
                            }
                        }
                        break;
                    }
                    case "removegreater": {
                        if(commands.length > 1) System.out.println("Не должно быть аргументов у команды!");
                        else {
                        System.out.println("Здесь вы можете удалить шляпу/шляпы из гардероба. \n " +
                                "Это можно сделать по ее цвету и размеру.\n " +
                                "Чтобы сделать это, задайте шляпу в формате json так, как представленно на примере  \n " +
                                "\"{\"size\": <положительное целое число>, \"color\": <строка>}\" \n " +
                                "Будут удалены только те шляпы, размер которых больше, чем размер указанной.\n " +
                                "Если вы хотите вернуться к меню, введите man.");
                        String text1 = multiline ? getMultilineCommand(reader) : reader.readLine();
                        if (!check(text1)) {
                            if (text1.equals("man")) System.out.print("");
                             else {
                                try {
                                    this.collection.remove_greater(HatMaker.makeHatFromJSON(text1));
                                } catch (Exception e) {
                                    System.out.println("Не получилось удалить шляпу: " + e.getMessage());
                                }
                            }
                        }}
                        break;
                    }
                    case "show": {
                        ManyHats.showHats();
                        break;
                    }
                    case "clear": {
                        ManyHats.clear();
                        break;
                    }
                    case "info": {
                        ManyHats.getInfo();
                        break;
                    }
                    case "man": {
                        this.man();
                        break;
                    }
                    case "save": {
                        ManyHats.writeFile(autosave);
                        break;
                    }
                    case "removeall": {
                        System.out.println("Здесь вы можете удалить шляпы, эквивалентные данной, из гардероба. \n " +
                                "Это можно сделать по ее цвету и размеру.\n " +
                                "Задайте шляпу в формате json так, как представленно на примере  \n " +
                                "\"{\"size\": <положительное целое число>, \"color\": <строка>}\" \n " +
                                "Если вы хотите вернуться к меню, введите man.");
                        String text2 = multiline ? getMultilineCommand(reader) : reader.readLine();
                        if (!check(text2)) {
                            if (text2.equals("man")) System.out.print("");
                             else {try{
                                    this.collection.remove_great(HatMaker.makeHatFromJSON(text2));}
                                    catch (NullPointerException e){
                                        System.out.println("Не получилось удалить шляпу");}
                                }
                        }
                        break;
                    }
                    case "addthing":{System.out.println("Здесь вы можете добавить прдемет в шляпу с заданными характеристиками. \n" +
                            "Внимание! Должны быть указаны обе характеристики(цвет и размер). \n" +
                            "Кроме того можно добавить только один предмет \n" +
                            "Для того, чтобы добавить предмет в шляпу, вам нужно сначала указать, в какую из шляп вы хотите добавить предмет \n" +
                            "Задайте шляпу в формате json: \"{\"size\": <положительное целое число>, \"color\": <строка>}\" " +
                            "После чего завершите ввод нажатием клавиши ENTER " +
                            "Далее введите предмет, который хотите добавить. Помните, что вы можете положить только предметы из этого списка: \n" +
                            "зубная щётка(TOOTHBRUSH), зубной порошок (DENTIFRIECE), мыло (SOAP), полотенце (TOWEL), носовой платок (CHIEF),\n" +
                            " носки (SOCKS), гвоздь (NAIL), проволока (COPPERWIRE). \n" +
                            "Обязательно используйте английский язык и заглавные буквы при вводе названий предметов как указанно в скобках.\n" +
                            "Каждый из предметов может лежать в шляпе только в одном экземпляре. \n" +
                            "Если такой предмет уже лежит в шляпе, он не будет добавлен.\n" +
                            "Также если в гардеробе есть несколько шляп с подходящими параметрами, то предмет будет по возможности добавлен во все шляпы" +
                            "Если вы хотите вернуться к меню введите man.");
                        System.out.print("> ");
                        String text = multiline ? getMultilineCommand(reader) : reader.readLine();
                        String items = multiline ? getMultilineCommand(reader) : reader.readLine();
                        if (!check(text)){
                            if (text.equals("man")){
                                System.out.print("");}
                            else {
                                try {
                                    ManyHats.addtohat(HatMaker.makeHatFromJSON(text), items);
                                    ManyHats.writeFile(autosave);
                                } catch (Exception e) {
                                    System.out.println("Не получилось добавить элемент в шляпу: " + e.getMessage());}
                            }
                        }
                        break;}
                    case "deletething":{System.out.println("Здесь вы можете удалить предемет из шляпы с заданными характеристиками. \\n\" +\n" +
                            "\"Внимание! Должны быть указаны обе характеристики(цвет и размер). \\n\" +\n" +
                            "\"Кроме того можно удалить только один предмет \\n\" +\n" +
                            " \"Для того чтобы удалить предмет, вам нужно сначала указать, из какой шляпы вы хотите удалить предмет \\n\" +\n" +
                            "\"Задайте шляпу в формате json: \\\"{\\\"size\\\": <положительное целое число>, \\\"color\\\": <строка>}\\\" \" +\n" +
                            "\"После чего завершите ввод нажатием клавиши ENTER \" +\n" +
                            "\"Далее введите предмет, который хотите удалить. Помните, что вы можете удалить только предметы из этого списка: \\n\" +\n" +
                            "\"зубная щётка(TOOTHBRUSH), зубной порошок (DENTIFRIECE), мыло (SOAP), полотенце (TOWEL), носовой платок (CHIEF),\\n\" +\n" +
                            "\" носки (SOCKS), гвоздь (NAIL), проволока (COPPERWIRE). \\n\" +\n" +
                            " \"Обязательно используйте английский язык и заглавные буквы при вводе названий предметов как указанно в скобках.\\n\" +\n" +
                            " \"Также если в гардеробе есть несколько шляп с подходящими параметрами, то предмет будет удален из всех\" +\n" +
                            "\"Если вы хотите вернуться к меню введите man.");
                        System.out.print("> ");
                        String text = multiline ? getMultilineCommand(reader) : reader.readLine();
                        String items = multiline ? getMultilineCommand(reader) : reader.readLine();
                        if (!check(text)){
                            if (text.equals("man")){
                                System.out.print("");}
                            else {
                                try {
                                    ManyHats.deletefromhat(HatMaker.makeHatFromJSON(text), items);
                                    ManyHats.writeFile(autosave);
                                } catch (Exception e) {
                                    System.out.println("Не получилось добавить элемент в шляпу: " + e.getMessage());}
                            }
                        }
                        break;}
                    case "remove": {
                        if (commands.length < 2) System.out.println("Отсутсвуют аргументы команды. Чтобы удалить элемент по ключу, введите: remove <ключ>\n" +
                                    "Если вы хотите вернуться к меню, введите man.");

                        this.collection.remove(commands[1]);
                        break;
                    }
                    case "break": {
                        System.out.println("Коллекция не сохранена.");
                        this.needExit = true;
                        break;
                    }
                    case "example": {
                        this.fileFormat();
                        break;
                    }
                    case "multiline": {
                        if (multiline) {
                            multiline = false;
                            System.out.println("многострочный ввод выключен");
                        } else {
                            multiline = true;
                            System.out.println("многострочный ввод включен. Для завершения ввода используйте \";\"");
                        }
                        break;
                    }
                    case "exit":{
                        scanner.close();
                        return true;}
                    default:
                        System.out.println("Команда не найдена");
                }
        }
        catch (
    NoSuchElementException e) {
        return true;
    }
                return false;
    }

    private void man() {
        System.out.println("insert <key> - добавляет элемент типа Hat в коллекцию. ВАЖНО! Ключи при добавлении шляп не должны повторяться. Если вы ввели ключ второй раз, то шляпа перезапишестя");
        System.out.println("removegreater - удаляет все шляпы, размер которых больше, чем размер указанной.");
        System.out.println("removeall - удаляет все шляпы, эквивалентные указанной по цвету и размеру.");
        System.out.println("remove <key> - удаляет элемент типа Hat из коллекции по его ключу.");
        System.out.println("save - сохраняет коллекцию в файл.");
        System.out.println("delete {String path}- удаляет файл.");
        System.out.println("example - показывает пример файла с коллекцией в xml формате.");
        System.out.println("show - показывает все элементы коллекции в строковом представлении.");
        System.out.println("info - выводит информацию о коллекции.");
        System.out.println("exit - выполняет выход из программы, коллекция сохраняется.");
        System.out.println("break- выполняет выход из программы, коллекция не сохраняется.");
        System.out.println("clear- очищает коллекцию");
        System.out.println("addthing - добавляет предмет в шляпу с указанными характеристиками");
        System.out.println("deletething - добавляет предмет в шляпу с указанными характеристиками");
    }

    private static boolean check(String text) {
        if (text==null) {
            return true;
        }else return false;
    }
    private static String getMultilineCommand(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char current;
        boolean inString = false;
        do {
            current = (char)reader.read();
            if (current != ';' || inString)
                builder.append(current);
            if (current == '"')
                inString = !inString;
        } while (current != ';' || inString);
        return builder.toString();
    }


    private void fileFormat() {
        System.out.println("<?xml version=\"1.0\"?>\n<collection>\n\t<Object>\n\t\t<key>1<\\key>\n\t\t<hat>\n\t\t\t<size>4<\\size>\n\t\t\t<collor>blue<\\collor>\n\t\t\t<things>\n\t\t\t\t<itemName>SOAP<\\itemName>\n\t\t\t\t<itemName>TOWEL<\\itemName>\n\t\t\t<\\things>\n\t\t<\\hat>\n\t<\\Object>\n<\\collection>");
        System.out.println("\u001b[31mОтступы в каждой строке можете не делать.\u001b[0m");
    }

}