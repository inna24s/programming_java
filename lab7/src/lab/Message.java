package lab;

import java.io.Serializable;
import java.util.List;

/**
 * Класс, использующийся для передачи сообщений между клиентом и сервером,
 * используется для соответствия требованию о сериализации передаваемых объектов
 * и для решения проблемы с разделением команды и её объектов-аргументов
 */
public class Message<T extends Serializable> implements Serializable {
    private String message;
    private T argument;
    private String arg1;
    private List<Hat> list;
    private String userName;
    private String password;

    public Message(String message, String arg1, T argument) {
        this.message = message;
        this.argument = argument;
        this.arg1=arg1;
    }



    /**
     * Создаёт сообщение с указанным текстовым запросом и объектом-аргументом
     * @param message текстовый запрос
     * @param argument объект-аргумент, прикреплённый к сообщению
     */
    public Message(String message, T argument) {
        this.message = message;
        this.argument = argument;
    }

    /**
     * Создаёт сообщение с указанным текстовым запросом
     * @param message текстовый запрос
     */
    public Message(String message) {
        this.message=message;
    }

    /**
     * @return текстовый запрос сообщения
     */
    public String getMessage() {
        return message;
    }

    public String getArg1(){return arg1;}

    /**
     * @return объект-аргумент, прикреплённый к сообщению
     */
    public T getArgument() {
        return argument;
    }

    /**
     * @return true, если к сообщению прикреплён объект-аругмент
     */
    public boolean hasArgument() {
        return argument != null;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {return password;}

    void setPassword(String password){this.password=password;}
}
