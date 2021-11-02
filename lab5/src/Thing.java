public class Thing
{
    private Item name;
    public String rus(String s){
    switch (s){
        case "TOOTHBRUSH": {return "зубная щётка";}
        case "DENTIFRIECE": return "зубной порошок";
        case "SOAP": return "мыло";
        case "TOWEL": return "полотенце";
        case "CHIEF": return "носовой платок";
        case "SOCKS": return "носки";
        case "NAIL": return "гвоздь";
        case "COPPERWIRE": return "проволока";
    }
    return "";}

    public Item getName(){return name;}
    public void setName(Item item){this.name = item;}

    Thing(Item itemType){
    name=itemType;
}

}
