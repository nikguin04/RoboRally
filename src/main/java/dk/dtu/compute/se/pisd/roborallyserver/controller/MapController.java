package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MapRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/maps")
public class MapController {

	private MapRepository mapRepository;
	private LobbyRepository lobbyRepository;

	public MapController(MapRepository mapRepository, LobbyRepository lobbyRepository) {
		this.mapRepository = mapRepository;
		this.lobbyRepository = lobbyRepository;
	}

	@GetMapping()
	public ResponseEntity<List<Map>> findAllMaps() {
		List<Map> lobbyList = mapRepository.findAll();
		return ResponseEntity.ok(lobbyList);
	}

	@GetMapping("/get")
	public ResponseEntity<Map> getMap(@RequestParam("id") Long id) {
		Optional<Map> map = mapRepository.findById(id);
		if (map.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(map.get());
	}

	@PostMapping("/newmap")
	public ResponseEntity<Map> newMap(@RequestBody Map map) {
		mapRepository.saveAndFlush(map);
		return ResponseEntity.ok(map);
	}

}
