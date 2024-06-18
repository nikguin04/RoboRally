package dk.dtu.compute.se.pisd.roborallyserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lobby {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long rounds;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Map map;

	private boolean gameStarted;

	@OneToMany(mappedBy = "lobby")
	private List<ServerPlayer> players;

}
