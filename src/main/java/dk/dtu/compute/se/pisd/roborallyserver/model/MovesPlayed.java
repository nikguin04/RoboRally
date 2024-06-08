package dk.dtu.compute.se.pisd.roborallyserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MovesPlayed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovesPlayed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long stepid;

    private Long rounds;

	private String move1;

	private String move2;

	private String move3;

	private String move4;

	private String move5;

	@ManyToOne
	@JoinColumn(name = "lobby_id", nullable = false)
	@JsonIgnore
	private Lobby lobby;
	// Only return the lobby id and not the lobby object itself
	@Transient
	public Long getLobbyId() {
		return lobby.getId();
	}

	@ManyToOne
	@JoinColumn(name = "players_id", nullable = false)
	@JsonIgnore
	private ServerPlayer player;
	// Only return the lobby id and not the lobby object itself
	@Transient
	public Long getPlayerId() {
		return player.getId();
	}




}
