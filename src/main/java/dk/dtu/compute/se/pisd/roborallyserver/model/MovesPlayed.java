package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@IdClass(MovesPlayed.MovesPlayedKey.class)
public class MovesPlayed {

	@Id
	private Long rounds;

	private String move1;
	private String move2;
	private String move3;
	private String move4;
	private String move5;

	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIgnore
	private Lobby lobby;

	// Only return the lobby ID and not the lobby object itself
	@Transient
	public Long getLobbyId() {
		return lobby.getId();
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	@Id
	private ServerPlayer player;

	// Only return the lobby ID and not the lobby object itself
	@Transient
	public Long getPlayerId() {
		return player.getId();
	}

	@Embeddable
	public static class MovesPlayedKey implements Serializable {
		private Long rounds;
		@ManyToOne private ServerPlayer player;
	}
}
