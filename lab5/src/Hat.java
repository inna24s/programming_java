import json.JSONString;

public class Hat implements Comparable<Hat>  {
    private String color;
    private int size;
    Thing content[];

    public Hat(){}

    /**
     * Добавляет предмет в шляпу
     * @param obj предмет, который нужно добавить
     */
    void addthing(Thing obj){
        if (checkspace()!=-1){
            if (checkitem(obj)==-1){
                System.out.println("Объект " + obj.rus(obj.getName().toString()) + " был успешно добавлен в шляпу.");
                this.content[checkspace()]=obj;}
            else {System.out.println("Объект " + obj.rus(obj.getName().toString()) +" уже есть в этой шляпе");}
        }
        else {
            System.out.println("В шляпе не осталось места. Пожалуйста удалите какой-нибудь предмет прежде чем добавлять новый.\n" +
                    "Объект" + obj.rus(obj.getName().toString()) + "не был добавлен в шляпу.");
        }
    }

    /**
     * Удаляет предмет из шляпы
     * @param obj предмет, который нужно удалить
     */
    void deletething(Thing obj) {
        for (int i=0; i < this.size; i++){
            if (this.content[i]!=null)
                if ((this.content[i].getName()).equals(obj.getName())){this.content[i]=null;}
        }
    }
    /**
     * Проверяет есть ли в шляпе свободное место
     * @return индекс ближайшей свободной ячейки; -1, если свободного места не осталось
     */
    int checkspace(){
        for (int i=0; i < this.size; i++){
            if (this.content[i]==null){return i;}
        }
        return -1;
    }

    /**
     * Метод для того, чтобы узнать только содержимое шляпы
     * @return строку в которой перечисленно все содержимое шляпы
     */
    String contentlist(){
        String result = "";
        for (int i=0; i < this.size; i++) {
            if (this.content[i]!=null)
                result = result + this.content[i].rus(this.content[i].getName().toString()) + " ";
        }
        return result;

    }

    /**
     * Проверяет есть ли заданный предмет в шляпе
     * @param item предмет, наличие которого нужно проверить
     * @return индекс найденного предмета; -1, если предмета в шляпе нет
     */
    private int checkitem(Thing item){
        for (int i=0; i < this.size; i++){
            if (this.content[i]!=null)
                if ((this.content[i].getName()).equals(item.getName())){return i;}
        }
        return -1;
    }




    public String getColor() {
        return color;
    }
    public void setColor(String name) {
        this.color = name;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int age) {
        this.size = age;
    }

    @Override
    public int compareTo(Hat hat) {return this.size-hat.size; }

    Hat(int a, String c){
        this.size=a;
        this.color=c;
        this.content= new Thing[a];
    }

    Hat(int a, String c, Thing arr[]){
        this.size=a;
        this.color=c;
        this.content=arr;
    }

    Hat(int a, JSONString c) {
        this.size = a;
        this.color = c.toString();
        this.content = new Thing[a];
    }

    @Override
    public String toString() {
        return "Color = " + this.color + " Size = " + this.size;
    }}

