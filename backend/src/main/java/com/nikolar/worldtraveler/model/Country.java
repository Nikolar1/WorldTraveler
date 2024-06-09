package com.nikolar.worldtraveler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private Geometry geom;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "country_neighbors",
            joinColumns = @JoinColumn(name = "country_id"),
            inverseJoinColumns = @JoinColumn(name = "neighbor_id")
    )
    private List<Country> neighbors;

    public Country(Long id){
        this();
        this.id = id;
    }


}
