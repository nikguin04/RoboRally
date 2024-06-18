/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.SpaceElement;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

	/**
	 * Loads a board with a given name from the included application resources.
	 * If the board doesn't exist in the application resources, null is returned.
	 * Note that this doesn't load from external files, only from resources compiled into the game.
	 * @param boardName The board name
	 * @return A board loaded from resources, or null if the specified board doesn't exist
	 */
	public static Board loadBoard(String boardName, Lobby lobby) {
		if (boardName == null) {
			boardName = DEFAULTBOARD;
		}

		ClassLoader classLoader = LoadBoard.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardName + "." + JSON_EXT);

		if (inputStream == null) {
			return null;
		}

		return loadBoard(new InputStreamReader(inputStream), lobby);
	}

	/**
	 * Load a board from a {@link Reader} supplying a JSON stream.
	 * The {@link Reader} is automatically closed when done, even if an error occurs.
	 * @param reader The reader supplying JSON
	 * @return A board loaded from the supplied JSON
	 */
	public static Board loadBoard(@NotNull Reader reader, Lobby lobby) {
		GsonBuilder simpleBuilder = new GsonBuilder().
			registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>());
		Gson gson = simpleBuilder.create();

		Board result;
		try (JsonReader jsonReader = gson.newJsonReader(reader)) {
			BoardTemplate template = gson.fromJson(jsonReader, BoardTemplate.class);

			result = new Board(template.width, template.height, lobby);
			int numCheckpoints = 0;
			for (SpaceTemplate spaceTemplate : template.spaces) {
				Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
				if (space == null) continue;
				space.setElement(spaceTemplate.element);
				space.getWalls().addAll(spaceTemplate.walls);
				if (spaceTemplate.element instanceof CheckPoint) {
					numCheckpoints++;
				}
			}
			result.setNumCheckpoints(numCheckpoints);
			return result;
		} catch (IOException ignored) {}
		return null;
	}

    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || space.getElement() != null) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.element = space.getElement();
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(SpaceElement.class, new Adapter<SpaceElement>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

}
