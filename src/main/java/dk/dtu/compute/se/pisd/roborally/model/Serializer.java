package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mysql.cj.conf.ConnectionUrl.Type;
import com.mysql.cj.xdevapi.Expression;

public class Serializer {
	public static class BoardSerializer implements JsonSerializer<Board> {
        @Override
        public JsonElement serialize(Board board, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("gameId", board.getGameId());
			jsonObject.addProperty("spaces", "TODO");
			JsonArray players = new JsonArray();
			for (int i = 0; i < board.getPlayersNumber(); i++) {
				players.add(context.serialize(board.getPlayer(i)));
			}
			jsonObject.add("players", players);
			//jsonObject.addProperty("players", context.serialize(board);
			// maybe dont save current player, we should only let player save at start/end of a phase
			jsonObject.addProperty("current_playerindex", board.getCurrentPlayer().getPlayerIndex());
			jsonObject.addProperty("phase", board.getPhase().name());
			//jsonObject.addProperty("step", "null"); // dont save step, we should only let player save at start/end of a phase
			jsonObject.addProperty("move_count", board.getMoveCount());

            //jsonObject.add("instance", context.serialize(src));

            return jsonObject;
        }
    }

	public static class PlayerSerializer implements JsonSerializer<Player> {
        @Override
        public JsonElement serialize(Player p, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject player_object = new JsonObject();
			player_object.addProperty("name", p.getName());
			player_object.addProperty("color", p.getColor());
			player_object.addProperty("space", p.getSpace().x + "," + p.getSpace().y);
			player_object.addProperty("heading", p.getHeading().name());

			JsonArray comcardarr = new JsonArray();
			for (int i = 0; i < p.NO_CARDS; i++) {
				comcardarr.add(context.serialize(p.getCardField(i).getCard().command));
			}
			player_object.add("command_cards", comcardarr);
			// Dont include stuff like program since saving during a move should not be allowed
            return player_object;
        }
    }

	public static class CommandSerializer implements JsonSerializer<Command> {
        @Override
        public JsonElement serialize(Command com, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject com_object = new JsonObject();
			com_object.addProperty("command", com.name());
			// Dont include stuff like program since saving during a move should not be allowed
            return com_object;
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
