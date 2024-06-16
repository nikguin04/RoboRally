package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Lobbies")
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
    private Map mapName;

    private boolean game_started;

}
