package lab.server;

import lab.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;
import static lab.server.ManyHats.collection;

class RequestResolver implements Runnable {

    private static int maxRequestSize = 268435456;
    private static DataBaseHandler db;
    private ObjectOutputStream out;
    private ObjectInputStream ois;
    private ManyHats manyHats;
    private Socket socket;
    public String a = "";
    private String autosave="autosave.xml";

    RequestResolver(Socket socket, ManyHats manyHats, DataBaseHandler db) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(
                    new LimitedInputStream(socket.getInputStream(), maxRequestSize)
            );
            this.socket = socket;
            this.manyHats = manyHats;
            this.db=db;
        } catch (IOException e) {}
    }

    @Override
    public void run() {
        try {
            List<Message> messages = new LinkedList<>();
                Object incoming = ois.readObject();
                if (incoming instanceof Message) messages.add((Message) incoming);
                 else {
                    sendMessage("Клиент отправил данные в неверном формате");
                    return;
                }
            if (messages.size() == 1) {
                Message message = messages.get(0);
                processMessage(message);
            } else {
                for (int i = 0; i < messages.size(); i++)
                    processMessage(messages.get(i));
            }

        } catch (LimitException e) {
            sendMessage("Ваш запрос слишком большой, он должен быть не больше " + maxRequestSize);
        } catch (EOFException e) {
            sendMessage("Не удалось обработать ваш запрос: в ходе чтения запроса сервер наткнулся на неожиданный конец данных");
        } catch (IOException e) {
            sendMessage("На сервере произошла ошибка: " + e.toString());
        } catch (ClassNotFoundException e) {
            sendMessage("Клиент отправил данные в неверном формате");
        }
    }


    /**
     * Отправляет сообщение
     * @param message текст сообщения
     */
    private void sendMessage(String message) {
        try {
            out.writeObject(new Message(message));
        } catch (IOException e) {
           System.out.println("Ошибка отправки данных клиенту: " + e.getLocalizedMessage());
        }
    }

    /**
     * Обрабатывает сообщение
     * @param message сообщение
     */
    private <T extends Serializable> void processMessage(Message message) {
        if (message == null) {
            sendMessage("Задан пустой запрос");
            return;
        }

        switch (message.getMessage()) {
            case "info":{
                sendMessage(ManyHats.getInfo());
                return;}

            case "show": {

                try {
                    for (Map.Entry<String, Hat> entry : ManyHats.collection.entrySet()) {
                        a = a  +"key: " + entry.getKey() +  " " +  entry.getValue().showHat();
                    }
                    out.writeObject(new Message<>("", a));
                    if (ManyHats.collection.size() == 0)
                        out.writeObject(new Message<>(""));
                } catch (IOException e) {
                    System.out.println("Ошибка исполнения запроса show: " + e.getLocalizedMessage());
                }
                return;
            }

            case "save": {
                if (!message.hasArgument()) {
                    sendMessage("Имя не указано.\n" +
                            "Введите \"help save\", чтобы узнать, как пользоваться командой");
                    return;
                }
                if (!(message.getArgument() instanceof String)) {
                    sendMessage("Клиент отправил запрос в неверном формате (аргумент сообщения должен быть строкой)");
                    return;
                }
                if (manyHats != null) {
                    db.saveHats((String) message.getArgument());
                    sendMessage("Гардероб сохранён в файл");
                }
                sendMessage("Гардероб пуст, сохранять нечего");

                return;
            }

            case "load": {
                if (!message.hasArgument()) {
                    sendMessage("Имя не указано.\n" +
                            "Введите \"help load\", чтобы узнать, как пользоваться командой");
                    return;
                }
                try {
                    if (!(message.getArgument() instanceof String)) {
                        sendMessage("Клиент отправил запрос в неверном формате (аргумент сообщения должен быть строкой)");
                        return;
                    }
                    db.clear();
                   if( Loader.load((String) message.getArgument(), this.db, message.getUserName(), message.getPassword() ))

                     sendMessage("Загрузка успешна! В коллекции "+collection.size()+" шляп.");
                    else sendMessage("Ошибка загрузки!");
                } catch (OverflowException e) {
                    sendMessage("В шляпе не осталось места, некоторые существа загрузились");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }

            case "import":{
                if (!message.hasArgument()) {
                    sendMessage("Имя не указано.\n" +
                            "Введите \"help import\", чтобы узнать, как пользоваться командой");
                    return;
                }
                try {
                    if (!(message.getArgument() instanceof String)) {
                        sendMessage("Клиент отправил запрос в неверном формате (аргумент сообщения должен быть строкой)");
                        return;
                    }
                    db.clear();
                    Loader.imload( (String)message.getArgument(), this.db,  message.getUserName(), message.getPassword());
                    sendMessage("Загрузка успешна! В гардеробе " + ManyHats.collection.size() + " шляп");
                    db.saveHats(autosave);
                } catch (OverflowException e) {
                    sendMessage("В гардеробе не остмалось места, некоторые шляпы не загрузились");}
                catch (IOException e){}
                catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;}

            case "add": {
                try {
                    if (!message.hasArgument()) {
                        sendMessage(helpFor(message.getMessage()));
                        return;
                    }
                    if (!(message.getArgument() instanceof Hat)) {
                        sendMessage("Клиент отправил данные в неверном формате (аргумент должен быть сериализованным объектом)");
                        return;
                    }
                    String key = message.getArg1();
                    Hat hat = (Hat) message.getArgument();

                    if(ManyHats.addH(key, hat, message.getUserName())){
                    sendMessage(hat.getHatColor() + " добавлена в гардероб");
                    db.addToDB(key, hat, message.getUserName(), message.getPassword());}
                    db.saveHats(autosave);
                    return;
                } catch (OverflowException e) {
                    sendMessage("Недостаточно места в гардеробе. " +
                            "В гардероб может поместиться не больше " + ManyHats.getMaxCollectionElements() + " шляп.\n" +
                            "Попробуйте удалить что-то, чтобы освободить место.");
                } catch (Exception e) {
                    sendMessage("Не получилось добавить шляпу: " + e.getMessage());
                }
                return;
            }

            case "remove": {
                try {
                    if (!message.hasArgument()) {
                        sendMessage(helpFor(message.getMessage()));
                        return;
                    }
                    Hat hat1=(Hat) message.getArgument();

                        if (db.removeHat(hat1, message.getUserName(), message.getPassword())){
                            sendMessage("Шляпа успешно удалена");
                            ManyHats.remove(hat1, message.getUserName());

                        db.saveHats( autosave);
                    } else
                        sendMessage("У Вас доступе не нашлось такой шляпы. Удалять можно только свои шляпы.");
                } catch (Exception e) {
                    sendMessage(e.getMessage());
                }
                return;
            }

            case "removegreater": {
                try {
                    if (!message.hasArgument()) {
                        sendMessage(helpFor(message.getMessage()));
                        return;
                    }
                    if (!(message.getArgument() instanceof Hat)) {
                        sendMessage("Клиент отправил данные в неверном формате (аргумент должен быть сериализованным объектом)");
                        return;
                    }

                    Hat hat1=(Hat) message.getArgument();
                    int k=0;
                    for (Map.Entry<String, Hat> entry1 : collection.entrySet()) {
                        if (hat1.getName().compareTo(collection.get(entry1.getKey()).getName()) < 0)
                            if (db.removeHat(entry1.getValue(), message.getUserName(), message.getPassword())) {
                                k++;
                                ManyHats.remove(entry1.getValue(), message.getUserName());
                            }




                        /* for (Map.Entry<String, Hat> entry: collection.entrySet()){
                            db.addToDB(entry.getKey(), entry.getValue(), entry.getValue().getUser(), message.getPassword());
                        }
                       // db.removeHat((Hat) message.getArgument(), message.getUserName());*/
                    }
                     if(k==0) sendMessage("Шляп для удаления не нашлось.");
                     else sendMessage("Шляпы удалены.");

                    db.saveHats(autosave);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(e.getMessage());
                }
            }

            case "help": {
                if (!message.hasArgument())
                    sendMessage(helpFor("help"));
                else {
                    if (message.getArgument() instanceof String)
                        sendMessage(helpFor((String) message.getArgument()));
                    else
                        sendMessage("Клент отправил данные в неверном формате (аргумент должен быть строкой)");
                }
                return;
            }



            case "register":{
                if (message.hasArgument()){
                    try {String args[] = message.getArgument().toString().split(" ");
                        String usernameS = args[0];
                        String mailS = args[1];
                        if (args.length>2){
                            String passwordS=args[2];
                            int resultR = db.executeRegister(usernameS, mailS, passwordS);
                            if (resultR == 1) {
                                sendMessage("Регистрация успешна");
                            } else if (resultR == 0) {
                                sendMessage("Вы уже зарегестрированны");
                            } else {
                                sendMessage("Ошибка при регистрации");
                            }}
                        else{
                            int resultR = db.executeRegister(usernameS, mailS,(new Integer(Math.round((ZonedDateTime.now()).getNano()))).toString());
                            if (resultR == 1) {
                                sendMessage("Регистрация успешна");
                            } else if (resultR == 0) {
                                sendMessage("Вы уже зарегестрированны");
                            } else {
                                sendMessage("Ошибка при регистрации");
                            }
                        }}
                    catch (NullPointerException e){sendMessage("Невернный ввод. Вы должны ввести имя пользователя почту и (при желании) пароль разделенный одним пробелом"); }
                    catch (ArrayIndexOutOfBoundsException  e) {sendMessage("Невернный ввод. Вы должны ввести имя пользователя почту и (при желании) пароль разделенный одним пробелом");}
                }
                else {sendMessage("Укажите имя пользователя и почту при желании пароль");}
                break;}
            case "login":{
                try {
                    String args[] = message.getArgument().toString().split(" ");
                    String usernameS = args[0];
                    String passwordS = args[1];
                    int result = db.executeLogin(usernameS, passwordS);
                    if (result == 0) {
                        sendMessage("Вы вошли как "+usernameS);
                    } else if (result == 1) {
                        sendMessage("You need to register first!");
                    } else if (result == 2) {
                        sendMessage("Wrong Password!");
                    } else {
                        sendMessage("Can't log in");
                    }}
                catch(NullPointerException e){
                    sendMessage("Невернный ввод. Вы должны ввести имя пользователя и пароль");
                }
                catch(ArrayIndexOutOfBoundsException e){
                    sendMessage("Невернный ввод. Вы должны ввести имя пользователя, почту и (при желании) пароль разделенный одним пробелом");
                }
                break;}
            default:{
                if (message.getMessage().length() < 64)
                    sendMessage("Неизвестная команда " + message.getMessage() + ", введите help, чтобы получить помощь");
                else
                    sendMessage("Неизвестная большая команда, введите help, чтобы получить помощь");}
        }
    }

    /**
     * Возвращает инструкции к команде
     * @param command команда, для которой нужна инструкция
     * @return инструкция к указанной команде
     */
    private static String helpFor(String command) {
        switch (command) {
            case "help": {
                return "Вот команды, которыми можно пользоваться:\n\n" +
                        "exit - выход\n" +
                        "address [newAddress] - информация об адресе сервера. Если указан адрес, он будет заменён\n" +
                        "port [newPort] - информация о порте сервера. Если указан порт, он будет заменён\n" +
                        "repeat - выполнить предыдущую введённую команду\n" +
                        "info - информация о гардеробе\n" +
                        "show - показать гардероб\n" +
                        "save {файл} - сохранить в файл\n" +
                        "removegreater {elem} - удалиль только те шляпы, название которых начитается с буквы дальше в алфавите\n" +
                        "add <key> {elem} - добавить шляпу, обязательные поля: size, name, color; дополнительно contents\n" +
                        "multiline - включить/выключить ввод в несколько строк\n" +
                        "import {file} - добавить данные из файла клиента в коллекцию\n" +
                        "load {file} - загрузить состояние коллекции из файла сервера\n" +
                        "save {file} - сохранить состояние коллекции в файл сервера\n" +
                        "remove {elem} - удалить шляпу\n" +
                        "help {command} - инструкция к команде\n" +
                        "help - показать этот текст";
            }
            case "exit":{
                return "Введите \"exit\", чтобы выйти";}
            case "address":{
                return  "Если вызвать эту команду, то можно узнать адрес, к которому клиент будет\n" +
                        "подключаться и отправлять команды. Если после команды указать новый адрес,\n" +
                        "то текущий адрес заменится им.\n\n" +
                        "Например:\n" +
                        "> address 192.168.1.100\n" +
                        "> address 127.0.0.1\n" +
                        "> address localhost";
            }
            case "port": {
                return "Если вызвать эту команду, то можно узнать порт, по которому клиент будет\n" +
                        "подключаться и отправлять команды. Если после команды указать новый порт,\n" +
                        "то текущий порт заменится им. Порт должен быть в пределах от 1 до 65535.\n\n" +
                        "Например:\n" +
                        "> port 8080\n" +
                        "> port 80\n" +
                        "> port 21";
            }
            case "repeat": {
                return "Эта команда выполняет предыдущую введённую команду.\n" +
                        "Команда repeat никогда не бывает первой введённой командой и\n" +
                        "никогда не бывает \"предыдущей\" введённой командой.";
            }
            case "info": {
                return "Введите \"info\", чтобы узнать о количестве шляп в гардеробе, времени создания и типе используемой коллекции";
            }
            case "show": {
                return "Выводит список шляп в гардеробе";
            }
            case "save {файл}": {
                return "Введите \"save\", а затем имя файла, чтобы сохранить в него гардероб.\n" +
                        "Файл будет содержать список шляп в формате xml\n\n" +
                        "Например:\n" +
                        "> save saved_state.xml";
            }

            case "add": {
                return "Здесь вы можете добавить новую шляпу в гардероб.  \n" +
                        "Чтобы сделать это, пожалуйста, введите текст в формате json так, как представленно на примере \n " +
                        "\"{\"size\": <положительное целое число>, \"color\": <строка> , \"name\":<строка>}\" \n " +
                        "Если вы хотите создать шляпу, в которой сразу будут лежать каки-либо предметы, вам следует набрать следующий текст:  \n " +
                        " \"{\"size\": <целое положительное число>, \"color\": <строка>, \"name\":<строка>}\", \"contents\": [{\"Itemname\": <строка>}, ... {\"Itemname\": <строка>}]}\" \n " +
                        " Размер шляпы(size) показывает, какое количество предметов может в неё поместиться. \n " +
                        "Так, если вы попытаетесь при создании шляпы положить в неё больше предметов, чем позволяет размер,  \n" +
                        "будет создана шляпа, в которой будут лежать первые несколько предметов по возможному количеству. \n " +
                        "Оставшиеся будут проигнорированны. \n " +
                        "Кроме того вы можете положить только предметы из этого списка: \n " +
                        "зубная щётка(TOOTHBRUSH), зубной порошок (DENTIFRIECE), мыло (SOAP), полотенце (TOWEL), носовой платок (CHIEF),\n " +
                        " носки (SOCKS), гвоздь (NAIL), проволока (COPPERWIRE).\n  " +
                        "Обязательно используйте английский язык и заглавные буквы при вводе названий предметов как указанно в скобках.\n " +
                        "Каждый из предметов может лежать в шляпе только в одном экземпляре. \n " +
                        "Если какой-то предмет будет введен несколько раз он будет добавлен только один раз.\n ";
            }
            case "multiline": {
                return "Переключает режим многострочного ввода. Если многострочный ввод выключен, введите \"multiline\",\n" +
                        "чтобы включить его. После того, как вы включили многострочный режим, ваши команды будут\n" +
                        "отделяться друг от друга знаком ';'.\n" +
                        "Чтобы выключить многострочный ввод, введите \"multiline;\". Обратите внимание, что в режиме\n" +
                        "многострочного ввода также нужен знак ';' после команды отключения многострочного ввода.";
            }
            case "import": {
                return "Иногда бывает так, что нужно передать содержимое всего файла на сервер, где этого файла нет.\n" +
                        "Используйте команду \"import\", чтобы сделать это. После имени команды укажите файл,\n" +
                        "содержимое которого передастся на сервер.  Файл должен хранить данные в формате xml\n\n" +
                        "Например:\n" +
                        "> import client_file.xml";
            }
            case "load": {
                return "Эта команда идентична команде import (введите \"help import\", чтобы узнать о ней), но  \n" +
                        "load используется для загрузки файла сервера\n";
            }
            case "save": {
                return "Эта команда сохраняет состояние коллекции в файл сервера в формате xml.\n\n" +
                        "Например:\n" +
                        "> save server_file.xml";
            }
            case "remove": {
                return "Здесь вы можете удалить шляпу из гардероба по ключу. \n ";
            }
                default:{
                    return  "Неизвестная команда " + command + "\n" +
                            "Введите \"help\", чтобы узнать, какие есть команды";}
        }
    }
}
