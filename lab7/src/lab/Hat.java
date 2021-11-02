package lab;

import lab.json.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Hat  implements Serializable, Comparable<Hat> {
    public String color;
    private int size;
    private  String name;


    public Thing[] content;
    private String user;
    private String pass;
    private ZonedDateTime createdDate = ZonedDateTime.now();
    public Hat(){}
    public String getName(){return name;}

    public String getHatColor(){
        StringBuilder result = new StringBuilder();
        result.append("шляпа с цветом "+this.color);
        return result.toString();
    }
    @Override
    public  int compareTo(Hat hat){return getName().compareTo(hat.name);}





    /**
     * Добавляет предмет в шляпу
     * @param obj предмет, который нужно добавить
     */
    public void addthing(Thing obj){
        if (checkspace()!=-1){
            if (checkitem(obj)==-1){
            System.out.println("Объект " + obj.rus(obj.name.toString()) + " был успешно добавлен в шляпу.");
            this.content[checkspace()]=obj;}
            else {System.out.println("Объект " + obj.rus(obj.name.toString()) +" уже есть в этой шляпе");}
        }
        else {
            System.out.println("В шляпе не осталось места. Пожалуйста удалите какой-нибудь предмет прежде чем добавлять новый.\n" +
                    "Объект" + obj.rus(obj.name.toString()) + "не был добавлен в шляпу.");
        }
    }

    /**
     * Проверяет есть ли в шляпе свободное место
     * @return индекс ближайшей свободной ячейки; -1, если свободного места не осталось
     */
    public int checkspace(){
        for (int i=0; i < this.size; i++){
            if (this.content[i]==null){return i;}
        }
        return -1;
    }

    /**
     * Метод для того чтобы узнать только содержимое шляпы
     * @return строку в которой перечисленно все содержимое шляпы
     */
    public String contentlist(){
        StringBuilder result = new StringBuilder();
        result.append(" ");
        for (int i=0; i < this.size; i++) {
            if (this.content[i]!=null)
                result.append(this.content[i].name.toString()+", ");
        }
        return result.toString();
    }

    /**
     * Проверяет есть ли заданный предмет в шляпе
     * @param item предмет, наличие которого нужно проверить
     * @return индекс найденного предмета; -1, если предмета в шляпе нет
     */
    private int checkitem(Thing item){
        for (int i=0; i < this.size; i++){
            if (this.content[i]!=null)
                if ((this.content[i].name).equals(item.name)){return i;}
        }
        return -1;
    }

    /**
     * Выводит информацию о шляпе: размер, цвет, местоположение, дату создания и содержимое
     */
    public String showHat(){
        StringBuilder result= new StringBuilder();
        result.append("Размер шляпы "+this.size+"; Цвет шляпы "+ this.color+"; Название шляпы"+this.name +";"+ " Дата создания: "+this.createdDate + " Владелец: " + this.user +"\n");
        for (int i=0; i < this.size; i++){
            if (this.content[i]!=null){

                result.append("В шляпе лежит " + this.content[i].rus(this.content[i].name.toString())+"\n");}
        }
    return result.toString();
    }

    public Hat(int size, String color, String name){
        this.size=size;
        this.name=name;
        this.color=color;
        this.content= new Thing[size];
    }

    public Hat(int a, String c, String x, Thing[] arr){
        this.size=a;
        this.name=x;
        this.color=c;
        this.content=arr;
    }

    public Hat(int size, JSONString name, JSONString color){
        this.name=name.toString();
        this.size=size;
        this.color=color.toString();
        this.content= new Thing[size];
    }
    public void setColor(String color) {
        this.color = color;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSize(int age) {
        this.size = age;
    }
    public int getSize() {
        return size;
    }
    public String getColor(){
        return color;
    }

    public Thing[] getContent() {
        return content;
    }

    public ZonedDateTime getCreatedDate(){return createdDate;}

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
    public void setUser(String name){ this.user=name;}

    public void setCreatedDate(ZonedDateTime date){this.createdDate=date;}
}

