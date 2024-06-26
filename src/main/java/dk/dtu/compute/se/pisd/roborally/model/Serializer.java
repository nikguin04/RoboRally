package dk.dtu.compute.se.pisd.roborally.model;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dk.dtu.compute.se.pisd.roborally.controller.SpaceElement;

public class Serializer {
	/**
	 * <p>JSON serializer for type {@link Board}</p>
	 * <p>SERIALIZING VARIABLES:</p>
	 * <p>Serializes {@link Board#gameId}, {@link Board#phase}, {@link Board#move_count}</p>
	 * <p>Serializes {@link Board#players} array with {@link PlayerSerializer} for each player</p>
	 * <p>Serializes current player number (index) in {@link Board#players} with {@link Player#player_index} from {@link Board#current}</p>
	 * <p>Serializes {@link Board#spaces} 2darray with {@link SpaceSerializer} for each space, stored as a 2d array in json</p>
	 */
	public static class BoardSerializer implements JsonSerializer<Board> {
        @Override
        public JsonElement serialize(Board board, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("gameId", board.getGameId());

			JsonArray players = new JsonArray();
			for (int i = 0; i < board.getPlayersNumber(); i++) {
				players.add(context.serialize(board.getPlayer(i)));
			}
			jsonObject.add("players", players);
			//jsonObject.addProperty("players", context.serialize(board);
			// maybe dont save current player, we should only let player save at start/end of a phase
			jsonObject.addProperty("current_playerindex", board.getPlayerNumber(board.getCurrentPlayer()));
			jsonObject.addProperty("phase", board.getPhase().name());
			//jsonObject.addProperty("step", "null"); // Don't save step, we should only let player save at start/end of a phase
			jsonObject.addProperty("move_count", board.getMoveCount());
			jsonObject.addProperty("gameId", board.getGameId());

            //jsonObject.add("instance", context.serialize(src));
			JsonArray spacesX = new JsonArray();
			for (int a = 0; a < board.width; a++) {
				JsonArray spacesY = new JsonArray();
				for (int b = 0; b < board.height; b++) {
					spacesY.add(context.serialize(board.getSpace(a, b)));
				}
				spacesX.add(spacesY);
			}

			jsonObject.add("spaces", spacesX);

            return jsonObject;
        }
    }

	/**
	 * <p>JSON serializer for type {@link Player}</p>
	 * <p>SERIALIZING VARIABLES:</p>
	 * <p>Serializes {@link Player#name}, {@link Player#color}, {@link Player#heading}</p>
	 * <p>Serialized {@link Player#space} as a {@link String} coordinate with two numbers comma separated, ex: "3,5"</p>
	 * <p>Serializes {@link Player#cards} array with {@link CommandSerializer}. The {@link CommandCardField} types, gets converted to {@link Command} types, to avoid storing unnecessary variables from {@link CommandCardField}</p>
	 */
	public static class PlayerSerializer implements JsonSerializer<Player> {
        @Override
        public JsonElement serialize(Player p, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject player_object = new JsonObject();
			player_object.addProperty("name", p.getName());
			player_object.addProperty("color", p.getColor());
			player_object.addProperty("space", p.getSpace().x + "," + p.getSpace().y);
			player_object.addProperty("heading", p.getHeading().name());

			JsonArray comcardarr = new JsonArray();
			for (int i = 0; i < Player.NO_CARDS; i++) {
				comcardarr.add(context.serialize(p.getCardField(i).getCard().command));
			}
			player_object.add("command_cards", comcardarr);
			// Don't include stuff like program since saving during a move should not be allowed
            return player_object;
        }
    }

	/**
	 *
	 * <p>JSON serializer for type {@link Command}</p>
	 * <p>SERIALIZING VARIABLES:</p>
	 * <p>Serializes {@link Command} enum name as {@link String}</p>
	 */
	public static class CommandSerializer implements JsonSerializer<Command> {
        @Override
        public JsonElement serialize(Command com, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject com_object = new JsonObject();
			com_object.addProperty("command", com.name());
			// Don't include stuff like program since saving during a move should not be allowed
            return com_object;
        }
	}

	/**
	 *
	 * <p>JSON serializer for type {@link Space}</p>
	 * <p>SERIALIZING VARIABLES:</p>
	 * <p>Serializes {@link Space#walls} as array with enum name derived from {@link Heading}</p>
	 * <p>Serializes {@link Space#element}'s abstract class {@link SpaceElement}'s simple class name as {@link String}</p>
	 */
	public static class SpaceSerializer implements JsonSerializer<Space> {
        @Override
        public JsonElement serialize(Space space, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject space_object = new JsonObject();
			JsonArray wallArr = new JsonArray();
			List<Heading> walls = space.getWalls();
			for (int i = 0; i < walls.size(); i++) {
				wallArr.add(walls.get(i).name());
			}
			space_object.add("walls", wallArr);

			// TODO: Unused, broken code
			//SpaceElement elem = space.getElement();
			//String element = elem == null ? "" : elem.getClass().getSimpleName(); // Add class name
			//element += (elem == null || elem.getArgument() == "") ? "" : "-"+elem.getArgument(); // Add argument
			//space_object.addProperty("element", element);

			return space_object;
        }
	}

	/*public static class SpaceSerializer implements JsonSerializer<Space> {
        @Override
        public JsonElement serialize(Space s, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject space_object = new JsonObject();
			space_object.addProperty("x", s.);
			// Dont include stuff like program since saving during a move should not be allowed
            return space_object;
        }
    }*/
}
