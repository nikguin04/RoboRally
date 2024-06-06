package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import dk.dtu.compute.se.pisd.roborally.controller.PrioAntenna;
import dk.dtu.compute.se.pisd.roborally.controller.SpaceElement;
import dk.dtu.compute.se.pisd.roborally.controller.StartTile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deserializer {
	/**
	 * <p>JSON deserializer for type {@link Board}</p>
	 * <p>DESERIALIZING VARIABLES:</p>
	 * <p>Deserializes {@link Player} array with {@link PlayerDeserializer}</p>
	 * <p>Deserializes {@link Space} 2d array with {@link SpaceDeserializer} and sets board size accordingly</p>
	 *
	 * <p>TODO: Deserialize "move_count"</p>
	 * <p>TODO: Deserialize "current_playerindex"</p>
	 * <p>TODO: Deserialize "phase" (Currently always starts in PROGRAMMING)</p>
	 * <p>TODO: Deserialize "gameId"</p>
	 */
	public static class BoardDeserializer implements JsonDeserializer<Board>	{
        @Override
        public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            //String className = json.getAsJsonObject().get("class").getAsString();
            JsonObject obj = json.getAsJsonObject();
			JsonArray spaces = obj.get("spaces").getAsJsonArray();

			int x = spaces.size(), y = spaces.get(0).getAsJsonArray().size();
            Board b = new Board(x, y);

			for (int ix = 0; ix < spaces.size(); ix++) {
				JsonArray xarr = spaces.get(ix).getAsJsonArray();
				for (int iy = 0; iy < spaces.size(); iy++) {
					Space new_s = context.deserialize(xarr.get(iy), Space.class);
					b.getSpace(ix, iy).copyAttributesFrom(new_s);

					// Special case for prio antenna
					if (new_s.getElement() != null && new_s.getElement().getClass().equals(PrioAntenna.class)) {
						b.setPrioAntenna((PrioAntenna) new_s.getElement());
					} else if (new_s.getElement() != null && new_s.getElement().getClass().equals(StartTile.class)) {
						b.setStartTile((StartTile) new_s.getElement());
					}
				}
			}

			b.setMoveCount(obj.get("move_count").getAsInt());
			b.setPhase(Phase.valueOf(obj.get("phase").getAsString()));
			if (obj.has("gameId"))
				b.setGameId(obj.get("gameId").getAsInt());

			PlayerDeserializer.board = b;

			JsonArray players = obj.get("players").getAsJsonArray();
			for (int i = 0; i < players.size(); i++) {
				PlayerDeserializer.player_index = i;
				Player p = context.deserialize(players.get(i), Player.class);
				b.addPlayer(p);
				b.addPrioPlayer(p);
			}
			b.setCurrentPlayer(b.getPlayer(obj.get("current_playerindex").getAsInt()));

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

	/**
	 * <p>JSON deserializer for type {@link Player}</p>
	 * <p>DESERIALIZING VARIABLES:</p>
	 * <p>Deserializes {@link Player#name} as {@link String}</p>
	 * <p>Deserializes {@link Player#color} as {@link String}, needs to comply with css colors, <a href="https://www.w3schools.com/cssref/css_colors.php">css colors (w3schools)</a></p>
	 * <p>Deserializes {@link Player#space} as 2d coordinate in string "x,y"</p>
	 * <p>Deserializes {@link Player#heading} as {@link Heading} by enum string name</p>
	 *
	 * <p>Deserializes {@link Player#cards} array with {@link CommandDeserializer} which is converted to {@link CommandCardField} array by player</p>
	 *
	 */
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

			Player p = new Player(board, color, name, commands);
			p.setSpace(board.getSpace(Integer.parseInt(space[0]), Integer.parseInt(space[1])));
			p.setHeading(Heading.valueOf(obj.get("heading").getAsString()));



			return p;
		}
	}
	/**
	 * <p>JSON deserializer for type {@link Command}</p>
	 * <p>DESERIALIZING VARIABLES:</p>
	 * <p>Deserializes {@link Command} by enum string name</p>
	 */
	public static class CommandDeserializer implements JsonDeserializer<Command>	{
		@Override
		public Command deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			return Command.valueOf(obj.get("command").getAsString());
		}
	}

	/**
	 * <p>JSON deserializer for type {@link Space}</p>
	 * <p>DESERIALIZING VARIABLES:</p>
	 * <p>Deserializes {@link Space#walls} by enum string name from {@link Heading}</p>
	 * <p>Deserializes {@link Space#element} by class name string (hardcoded relative to "dk.dtu.compute.se.pisd.roborally.controller.").
	 * Basic exceptions are implemented for wrong class or invalid classes</p>
	 */
	public static class SpaceDeserializer implements JsonDeserializer<Space>	{
		@Override
		public Space deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			Space s = new Space(null, 0, 0);
			JsonArray walls = obj.get("walls").getAsJsonArray();
			for (int i = 0; i < walls.size(); i++) {
				s.getWalls().add(Heading.valueOf(walls.get(i).getAsString()));
			}
			String cl = obj.get("element").getAsString();
			if (!cl.isEmpty()) {
				String[] split = cl.split("-");
				try {
					Class elemClass = Class.forName("dk.dtu.compute.se.pisd.roborally.controller." + split[0]);
					boolean properclass = SpaceElement.class.isAssignableFrom(elemClass);
					if (properclass) {
						//String[] args = (split.size() == 0) ? null : (String[]) split.toArray();
						SpaceElement se = (SpaceElement)
							((split.length == 1)
							? elemClass.getDeclaredConstructor().newInstance()
							: elemClass.getDeclaredConstructor(String.class).newInstance(split[1]));
						s.setElement(se);
					}
				} catch (Exception e) {
					System.out.println("space element class not found");
					return null;
				}
			}

			return s;
		}
	}


}
