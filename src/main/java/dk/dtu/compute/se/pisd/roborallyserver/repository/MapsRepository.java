package dk.dtu.compute.se.pisd.roborallyserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import dk.dtu.compute.se.pisd.roborallyserver.model.Maps;


public interface MapsRepository extends JpaRepository<Maps, Long> {
	// public Maps GetListOfMaps();
    // public Maps FindAllMaps(long id);

	//https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
	// @Query("select p from ServerPlayer p where p.lobby.id = ?1")
	// public List<Maps> GetListOfMaps(Long id);

}
