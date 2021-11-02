
import json.*;


public class HatMaker {
     public static Hat makeHatFromJSON(String json) throws Exception {
        JSONEntity entity;

        try {
            entity = JSONParser.parse(json);
        } catch (JSONParseException e) {
            throw new JSONParseException("Не удалось обработать этот json:\n" + e);
        }

        if (entity == null)
            throw new IllegalArgumentException("Требуется json-объект, но получен null");

        JSONObject object = entity.toObject(new IllegalArgumentException("Нужен json-объект, но вместо него " + entity.getTypeName()));

        JSONEntity size = object.getItem("size");
        JSONEntity color = object.getItem("color");

        if (size == null)
            throw new IllegalArgumentException("Требуется параметр 'size', но он не указан");
        if (color == null)
            throw new IllegalArgumentException("Требуется параметр 'color', но он не указан");

        try {
            Hat hat = new Hat(
                    size.toInt(new IllegalArgumentException("Параметр 'size' должен быть числом, но это " + size.getTypeName())),
                    color.toString(new IllegalArgumentException("Параметр 'color' должен быть строкой, но это " + color.getTypeName())));
            JSONEntity contents = object.getItem("things");
            if (contents != null) {
                JSONArray contentsArray = contents.toArray();
                int k=0;
                while ((hat.checkspace()!=-1)&(k<contentsArray.size()) ){
                        JSONEntity itemindex = contentsArray.getItem(k);
                        JSONObject itemobj = itemindex.toObject();
                        JSONEntity item = itemobj.getItem("item");
                        try {
                           hat.addthing(new Thing(Item.valueOf((item.toString()).replaceAll("\"", ""))));
                        } catch (IllegalArgumentException e) {
                            System.out.println("Введен предмет не из списка");
                        }

                    ++k;
                }
            }
            return hat;
        } catch(NegativeArraySizeException e){System.out.println("размер шляпы не может быть отрицательным числом");
            return new Hat(0,""); }
    }
}