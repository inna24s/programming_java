package lab.server;

import lab.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.*;

class RequestResolver implements Runnable {

    private static int maxRequestSize = 268435456;

    private ObjectOutputStream out;
    private ObjectInputStream ois;
    private ManyHats manyHats;
    private Socket socket;

    private String autosave="autosave.xml";

    RequestResolver(Socket socket, ManyHats manyHats) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(
                    new LimitedInputStream(socket.getInputStream(), maxRequestSize)
            );
            this.socket = socket;
            this.manyHats = manyHats;



        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            List<Message> messages = new LinkedList<>();

            while (true) {
                Object incoming = ois.readObject();

                if (incoming instanceof Message) {
                    messages.add((Message) incoming);
                    if (((Message) incoming).hasEndFlag())
                        break;
                } else {
                    sendEndMessage("Клиент отправил данные в неверном формате");
                    return;
                }
            }

            if (messages.size() == 1) {
                Message message = messages.get(0);
                processMessage(message);
            } else {
                for (int i = 0; i < messages.size(); i++)
                    processMessage(messages.get(i), i + 1 == messages.size());
            }

        } catch (LimitAchievedException e) {
            sendEndMessage("Ваш запрос слишком большой, он должен быть не больше " + Utils.optimalInfoUnit(maxRequestSize));
        } catch (EOFException e) {
            sendEndMessage("Не удалось обработать ваш запрос: в ходе чтения запроса сервер наткнулся на неожиданный конец данных");
        } catch (IOException e) {
            sendEndMessage("На сервере произошла ошибка: " + e.toString());
        } catch (ClassNotFoundException e) {
            sendEndMessage("Клиент отправил данные в неверном формате");
        }
    }

    /**
     * Отправляет сообщение, отмеченное как последнее
     * @param message текст сообщения
     */
    private void sendEndMessage(String message) {
        sendMessage(message, true);
    }

    /**
     * Отправляет сообщение с указанным флагом окончания
     * @param message текст сообщения
     * @param endFlag флаг окончания
     */
    private void sendMessage(String message, boolean endFlag) {
        try {
            out.writeObject(new Message(message, endFlag));
        } catch (IOException e) {
           System.out.println("Ошибка отправки данных клиенту: " + e.getLocalizedMessage());
        }
    }

    /**
     * Обрабатывает сообщение, отправляемый клиенту результат будет отмечен как последний
     * @param message сообщение
     */
    private void processMessage(Message message) {
        processMessage(message, true);
    }

    /**
     * Обрабатывает сообщение
     * @param message сообщение
     * @param endFlag если он true, результат обработки отправится клиенту как последний
     */
    private <T extends Serializable> void processMessage(Message message, boolean endFlag) {
        if (message == null) {
            sendMessage("Задан пустой запрос", endFlag);
            return;
        }

        switch (message.getMessage()) {
            case "info":{
                sendMessage(ManyHats.getInfo(), endFlag);
                return;}
            case "show": {
                try {
                    Hat[] hats = new Hat[0];
                    hats = ManyHats.collection.values().toArray(hats);
                    Arrays.sort(hats);
                    for (int i = 0, hatsLength = hats.length; i < hatsLength; i++)
                        out.writeObject(new Message<>("", hats[i], i + 1 == hatsLength));
                    if (ManyHats.collection.size() == 0)
                        out.writeObject(new Message<>("", null));
                } catch (IOException e) {
                    System.out.println("Ошибка исполнения запроса show: " + e.getLocalizedMessage());
                }
                return;
            }
            case "save": {
                if (!message.hasArgument()) {
                    sendMessage("Имя не указано.\n" +
                            "Введите \"help save\", чтобы узнать, как пользоваться командой", endFlag);
                    return;
                }
                try {
                    if (!(message.getArgument() instanceof String)) {
                        sendMessage("Клиент отправил запрос в неверном формате (аргумент сообщения должен быть строкой)", endFlag);
                        return;
                    }
                    WardrobeLoaderSaver.writeFile(
                             (String) message.getArgument());
                    if (endFlag)
                        sendMessage("Сохранение успешно", true);
                } catch (IOException e) {
                    sendEndMessage("На сервере произошла чтения/записи");
                }
                return;
            }
            case "load": {
                if (!message.hasArgument()) {
                    sendMessage("Имя не указано.\n" +
                            "Введите \"help load\", чтобы узнать, как пользоваться командой", endFlag);
                    return;
                }
                try {
                    if (!(message.getArgument() instanceof String)) {
                        sendMessage("Клиент отправил запрос в неверном формате (аргумент сообщения должен быть строкой)", endFlag);
                        return;
                    }
                    ManyHats.clear();
                    WardrobeLoaderSaver.load((String) message.getArgument());
                    sendMessage("Загрузка успешна", endFlag);
                } catch (WardrobeOverflowException e) {
                    sendMessage("В шляпе не осталось места, некоторые существа загрузились", endFlag);
                }
                return;
            }
            case "import":{
                if (!message.hasArgument()) {
                    sendMessage("Имя не указано.\n" +
                            "Введите \"help import\", чтобы узнать, как пользоваться командой", endFlag);
                    return;
                }
                try {
                    if (!(message.getArgument() instanceof String)) {
                        sendMessage("Клиент отправил запрос в неверном формате (аргумент сообщения должен быть строкой)", endFlag);
                        return;
                    }
                    WardrobeLoaderSaver.imload( (String)message.getArgument());
                    sendMessage("Загрузка успешна! В гардеробе " + ManyHats.collection.size() + " шляп", endFlag);
                    WardrobeLoaderSaver.writeFile(autosave);
                } catch (WardrobeOverflowException e) {
                    sendMessage("В гардеробе не остмалось места, некоторые шляпы не загрузились", endFlag);}
                catch (IOException e){}
                catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                return;}




            case "add": {
                try {
                    if (!message.hasArgument()) {
                        sendMessage(helpFor(message.getMessage()), endFlag);
                        return;
                    }
                    if (!(message.getArgument() instanceof Hat)) {
                        sendMessage("Клиент отправил данные в неверном формате (аргумент должен быть сериализованным объектом)", endFlag);
                        return;
                    }
                    String key = message.getArg1();
                    Hat hat = (Hat) message.getArgument();

                    ManyHats.addNewHat(key, hat);
                    sendMessage(hat.getHatColor() + " добавлена в гардероб", endFlag);
                    WardrobeLoaderSaver.writeFile(autosave);
                    return;
                } catch (WardrobeOverflowException e) {
                    sendMessage("Недостаточно места в гардеробе. " +
                            "В гардероб может поместиться не больше " + ManyHats.getMaxCollectionElements() + " шляп.\n" +
                            "Попробуйте удалить что-то, чтобы освободить место.", endFlag);
                } catch (Exception e) {
                    sendMessage("Не получилось добавить шляпу: " + e.getMessage(), endFlag);
                }
                return;
            }
            case "removegreater": {
                try {
                    if (!message.hasArgument()) {
                        sendMessage(helpFor(message.getMessage()), endFlag);
                        return;
                    }
                    if (!(message.getArgument() instanceof Hat)) {
                        sendMessage("Клиент отправил данные в неверном формате (аргумент должен быть сериализованным объектом)", endFlag);
                        return;
                    }
                    if (ManyHats.remove_greater((Hat) message.getArgument())) {
                        sendMessage("Шляпы удалены", endFlag);
                        WardrobeLoaderSaver.writeFile(autosave);
                    } else sendMessage("Ошибка при удалении", endFlag);
                    return;
                } catch (Exception e) {
                    sendMessage(e.getMessage(), endFlag);
                }
            }
            case "remove": {
                try {
                    if (!message.hasArgument()) {
                        sendMessage(helpFor(message.getMessage()), endFlag);
                        return;
                    }

                    boolean removed =  ManyHats.remove((Hat) message.getArgument());
                    if (removed) {
                        sendMessage("Шляпа удалена", endFlag);
                        WardrobeLoaderSaver.writeFile(autosave);
                    } else
                        sendMessage("Такой шляпы не нашлось", endFlag);
                } catch (Exception e) {
                    sendMessage("Не получилось удалить шляпу:" + e.getMessage(), endFlag);
                }
                return;
            }
            case "help": {
                if (!message.hasArgument())
                    sendMessage(helpFor("help"), endFlag);
                else {
                    if (message.getArgument() instanceof String)
                        sendMessage(helpFor((String) message.getArgument()), endFlag);
                    else
                        sendMessage("Клент отправил данные в неверном формате (аргумент должен быть строкой)", endFlag);
                }
                return;
            }
            default:{
                if (message.getMessage().length() < 64)
                    sendMessage("Неизвестная команда " + message.getMessage() + ", введите help, чтобы получить помощь", endFlag);
                else
                    sendMessage("Неизвестная большая команда, введите help, чтобы получить помощь", endFlag);}
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
