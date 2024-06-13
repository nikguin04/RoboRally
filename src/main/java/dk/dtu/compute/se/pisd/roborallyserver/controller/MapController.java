package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MapRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maps")
public class MapController {

	private MapRepository mapRepository;
	private LobbyRepository lobbyRepository;

	public MapController(MapRepository mapRepository, LobbyRepository lobbyRepository) {
		this.mapRepository = mapRepository;
		this.lobbyRepository = lobbyRepository;
	}

	@GetMapping(value = "")
	public ResponseEntity<List<Map>> findAllMaps() {
		List<Map> lobbyList = mapRepository.findAll();
		return ResponseEntity.ok(lobbyList);
	}

	@PostMapping("/newmap")
	public ResponseEntity<Map> newMap(@RequestBody Map map) {
		mapRepository.saveAndFlush(map);
		return ResponseEntity.ok(map);
	}

}
