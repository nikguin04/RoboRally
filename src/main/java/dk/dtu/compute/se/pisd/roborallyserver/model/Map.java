package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Map implements Serializable {

	@Id
	private String mapName;

	@Lob
	private String json;

	private Long playerCount;

}
