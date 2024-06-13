package dk.dtu.compute.se.pisd.roborallyserver.repository;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {
	// https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
	@Query("SELECT p FROM ServerPlayer p WHERE p.lobby.id = ?1")
	public List<Map> getListOfMaps(Long id);
}
