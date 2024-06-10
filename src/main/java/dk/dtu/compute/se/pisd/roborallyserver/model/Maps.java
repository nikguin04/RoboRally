package dk.dtu.compute.se.pisd.roborallyserver.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
public class Maps implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mapstring;

    private Long numberofplayers;

    private String mapname;


}
