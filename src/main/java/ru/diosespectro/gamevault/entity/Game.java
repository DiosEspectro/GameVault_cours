package ru.diosespectro.gamevault.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.diosespectro.gamevault.repository.GameRepository;
import ru.diosespectro.gamevault.repository.GenreRepository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="gameId")
    private long gameId;

    @Column(name="gameName")
    private String gameName;

    @Column(name="gameYear")
    private int gameYear;

    @Column(name="gameDeveloper")
    private String gameDeveloper;

    @Column(name="createdBy")
    private Long createdBy;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "games_genres",
            joinColumns = { @JoinColumn(name = "game_id", referencedColumnName = "gameId") },
            inverseJoinColumns = { @JoinColumn(name = "genre_id", referencedColumnName = "id") }
    )
    private List<Genre> genres = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="game_game_id")
    private List<GameDetails> details = new ArrayList<>();

    public void setGenre(GameRepository gameRepository, GenreRepository genreRepository, Long genreId){
        Optional<Genre> genreO = genreRepository.findById(genreId);
        if (genreO.isPresent()) {
            Genre genre = genreO.get();

            this.getGenres().clear();
            this.getGenres().add(genre);
            gameRepository.save(this);
        }
    }

    public String getGenre(){
        String ret = "";
        List<Genre> genres = this.getGenres();

        if(genres.size() == 0) ret = "";
            else ret = this.getGenres().get(0).getName();

        return ret;
    }


}