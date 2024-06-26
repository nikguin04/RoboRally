package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MapRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping
	public ResponseEntity<List<Map>> findAllMaps() {
		List<Map> lobbyList = mapRepository.findAll();
		return ResponseEntity.ok(lobbyList);
	}

	@GetMapping("/{name}")
	public ResponseEntity<Map> getMap(@PathVariable("name") String mapName) {
		Optional<Map> map = mapRepository.findById(mapName);
		if (map.isEmpty())
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(map.get());
	}

	@PostMapping
	public ResponseEntity<Map> newMap(@RequestBody Map map) {
		mapRepository.saveAndFlush(map);
		return ResponseEntity.ok(map);
	}

}
