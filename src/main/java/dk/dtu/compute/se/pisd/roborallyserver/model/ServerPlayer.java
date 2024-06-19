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
public class ServerPlayer implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIgnore
	private Lobby lobby;

	// Only return the lobby ID and not the lobby object itself
	@Transient
	public Long getLobbyId() {
		return lobby.getId();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) { return true; }
		if (!(other instanceof ServerPlayer)) { return false; }
		ServerPlayer p = (ServerPlayer) other;
		return p.getId().equals(getId());
	}
}
