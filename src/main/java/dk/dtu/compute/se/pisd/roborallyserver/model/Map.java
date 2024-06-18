package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "maps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Map implements Serializable {

	@Id
	private String mapName;

	@Lob
	private String mapjson;

	private Long numberofplayers;

}
