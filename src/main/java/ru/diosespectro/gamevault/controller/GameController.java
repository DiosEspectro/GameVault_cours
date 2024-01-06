package ru.diosespectro.gamevault.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import ru.diosespectro.gamevault.entity.Game;
import ru.diosespectro.gamevault.entity.GameDetails;
import ru.diosespectro.gamevault.entity.User;
import ru.diosespectro.gamevault.repository.GameDetailsRepository;
import ru.diosespectro.gamevault.repository.GameRepository;
import ru.diosespectro.gamevault.repository.GenreRepository;
import ru.diosespectro.gamevault.repository.UserRepository;
import ru.diosespectro.gamevault.service.ActionLogService;
import ru.diosespectro.gamevault.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class GameController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameDetailsRepository gameDetailsRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private UserService userService;

    @GetMapping("/games")
    public ModelAndView getAllGames(HttpServletRequest request){
        ModelAndView mav = new ModelAndView("list-games");

        if(userService.getCurUserRole().equals("USER")) {
            Long curUserId = userService.getCurUserId();
            mav.addObject("games", gameRepository.findByCreatedBy(curUserId));
        } else {
            mav.addObject("games", gameRepository.findAll());
            mav.addObject("users", userRepository);
        }

        mav.addObject("pageTitle", "Список игр");
        return mav;
    }

    @GetMapping("/addGameForm")
    public ModelAndView addGameForm(HttpServletRequest request) {
        int year = Year.now().getValue();
        ModelAndView mav = new ModelAndView("add-game-form");
        Game game = new Game();
        mav.addObject("game", game);
        mav.addObject("genres", genreRepository.findAll());
        mav.addObject("curyear", year);
        mav.addObject("pageTitle", "Добавить игру");
        return mav;
    }

    @PostMapping("/saveGame")
    public String saveGame(@ModelAttribute Game game,
                           @RequestParam("gameGenreId") Long genreId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User curUser = userRepository.findByUsername(auth.getName());

        if(game.getCreatedBy() == null)
            game.setCreatedBy(curUser.getId());

        game.setGenre(gameRepository, genreRepository, genreId);

        gameRepository.save(game);
        actionLogService.logging("Сохранение игры \""+ game.getGameName() + "\" (ID: " + game.getGameId() + ")");
        return "redirect:/games";
    }

    @GetMapping("/updateGame")
    public ModelAndView updateGame(@RequestParam Long gameId) throws ModelAndViewDefiningException {
        int year = Year.now().getValue();
        ModelAndView mav = new ModelAndView("add-game-form");
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game = new Game();
        if(optionalGame.isPresent()){
            game = optionalGame.get();

            if(game.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                Long genreId = game.getGenres().get(0).getId();

                String details = "";
                List<GameDetails> gd = game.getDetails();
                for (GameDetails d : gd) {
                    if (!details.equals("")) details += ",";
                    details += d.getDetailId();
                }

                mav.addObject("game", game);
                mav.addObject("gameGenre", genreId);
                mav.addObject("details", details);
                mav.addObject("genres", genreRepository.findAll());
                mav.addObject("curyear", year);
                mav.addObject("pageTitle", "Изменить игру");
            } else {
                throw new ModelAndViewDefiningException(mav);
            }
        } else {
            throw new ModelAndViewDefiningException(mav);
        }
        return mav;
    }

    @GetMapping("/deleteGame")
    public String deleteGame(@RequestParam Long gameId){
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if(optionalGame.isPresent()) {
            Game game = optionalGame.get();

            if(game.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                game.getDetails().clear();
                actionLogService.logging("Удаление игры \"" + game.getGameName() + "\" (ID: " + game.getGameId() + ")");
                gameRepository.deleteById(gameId);
            }
        }
        return "redirect:/games";
    }
}
