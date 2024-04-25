package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class Deserializer {
	public static class BoardDeserializer implements JsonDeserializer<Board> {

        @Override
        public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            //String className = json.getAsJsonObject().get("class").getAsString();
            //JsonObject obj = json.getAsJsonObject().get("instance").getAsJsonObject();
            Board b = new Board(0, 0);

            /*switch (className) {
                case "com.example.expressions.Const" -> {e = new Const(obj.get("value").getAsInt());}
                case "com.example.expressions.Prod" -> {
                    e = new Prod(
                        context.deserialize(obj.get("left"), typeOfT),
                        context.deserialize(obj.get("right"), typeOfT));
                }
                case "com.example.expressions.Sum" -> {
                    e = new Sum(
                        context.deserialize(obj.get("left"), typeOfT),
                        context.deserialize(obj.get("right"), typeOfT));
                }

                default -> throw new JsonParseException("Cant find class");
            }*/

            return b;
        }
    }

}
