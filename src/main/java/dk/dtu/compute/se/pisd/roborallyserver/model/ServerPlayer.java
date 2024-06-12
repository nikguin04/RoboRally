package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Server;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ServerPlayer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    @JsonIgnore
    private Lobby lobby;

    // Only return the lobby id and not the lobby object itself
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
