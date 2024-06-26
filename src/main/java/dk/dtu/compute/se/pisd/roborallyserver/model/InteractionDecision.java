package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.io.Serializable;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InteractionDecision.InteractionDecisionKey.class)
public class InteractionDecision {

	@Id
	private Long round;

	@Id
	private int step;

	@ManyToOne
	@JoinColumn(nullable = false)
	@Id
	private Lobby lobby;

	@ManyToOne
	@JoinColumn(nullable = false)
	@Id
	private ServerPlayer player;

	private Command command;

	@Embeddable
	public static class InteractionDecisionKey implements Serializable {
		private Long round;
		private int step;
		@ManyToOne
		private Lobby lobby;
		@ManyToOne
		private ServerPlayer player;
	}

}
