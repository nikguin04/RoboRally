package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class Deserializer {
	public static class BoardDeserializer implements JsonDeserializer<Board>	{

        @Override
        public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            //String className = json.getAsJsonObject().get("class").getAsString();
            JsonObject obj = json.getAsJsonObject();
			JsonArray spaces = obj.get("spaces").getAsJsonArray();

            Board b = new Board(spaces.size(), spaces.get(0).getAsJsonArray().size());
			PlayerDeserializer.board = b;

			JsonArray players = obj.get("players").getAsJsonArray();
			for (int i = 0; i < players.size(); i++) {
				PlayerDeserializer.player_index = i;
				Player p = context.deserialize(players.get(i), Player.class);
				b.addPlayer(p);
			}

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

	public static class PlayerDeserializer implements JsonDeserializer<Player>	{
		public static Board board;
		public static int player_index;
		@Override
		public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			//String className = json.getAsJsonObject().get("class").getAsString();
			JsonObject obj = json.getAsJsonObject();
			String[] space = obj.get("space").getAsString().split(",");
			String name = obj.get("name").getAsString();
			String color = obj.get("color").getAsString();
			JsonArray cards = obj.get("command_cards").getAsJsonArray();
			Command[] commands = new Command[Player.NO_CARDS];
			int cmdindex = 0;
			for (JsonElement card : cards) {
				commands[cmdindex++] = context.deserialize(card, Command.class);
			}

			Player p = new Player(board, color, name, player_index, commands);
			p.setSpace(board.getSpace(Integer.parseInt(space[0]), Integer.parseInt(space[1])));
			p.setHeading(Heading.valueOf(obj.get("heading").getAsString()));



			return p;
		}
	}

	public static class CommandDeserializer implements JsonDeserializer<Command>	{
		@Override
		public Command deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			Command c = Command.valueOf(obj.get("command").getAsString());
			return c;
		}
	}


}
