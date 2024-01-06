package ru.diosespectro.gamevault.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gamedetails")
public class GameDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long detailId;

    @Column(nullable = false)
    private float price;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Platform platform;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;
}
