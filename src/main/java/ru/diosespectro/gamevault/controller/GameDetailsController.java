package ru.diosespectro.gamevault.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.diosespectro.gamevault.entity.*;
import ru.diosespectro.gamevault.repository.GameDetailsRepository;
import ru.diosespectro.gamevault.repository.GameRepository;
import ru.diosespectro.gamevault.repository.PlatformRepository;
import ru.diosespectro.gamevault.repository.StoreRepository;
import ru.diosespectro.gamevault.service.ActionLogService;
import ru.diosespectro.gamevault.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Controller
public class GameDetailsController {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameDetailsRepository gameDetailsRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private UserService userService;

    @GetMapping("/gameDetails")
    public ModelAndView getGameDetails(HttpServletRequest request, @RequestParam Long gameId) throws ModelAndViewDefiningException {
        ModelAndView mav = new ModelAndView("game-details");

        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game = new Game();
        if(optionalGame.isPresent()) {
            game = optionalGame.get();

            //if(game.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {

                mav.addObject("game", game);
                mav.addObject("gameDetails", game.getDetails());
                mav.addObject("platforms", platformRepository.findAll());
                mav.addObject("stores", storeRepository.findAll());
                mav.addObject("pageTitle", "Детали игры \"" + game.getGameName() + "\"");
            //} else {
            //    throw new ModelAndViewDefiningException(mav);
            //}
        } else {
            throw new ModelAndViewDefiningException(mav);
        }

        return mav;
    }

    @PostMapping("/gameDetails/addDetail")
    public String saveGameDetails(@ModelAttribute GameDetails gameDetails,
                                  @RequestParam Long gameId,
                                  @RequestParam("gamePlatformId") Long platformId,
                                  @RequestParam("gameStoreId") Long storeId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game;
        if(optionalGame.isPresent()) {
            game = optionalGame.get();

            if(game.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                Optional<Platform> optionalPlatform = platformRepository.findById(platformId);
                if (optionalPlatform.isPresent()) {
                    Platform platform = optionalPlatform.get();
                    gameDetails.setPlatform(platform);

                    Optional<Store> optionalStore = storeRepository.findById(storeId);
                    if (optionalStore.isPresent()) {
                        Store store = optionalStore.get();
                        gameDetails.setStore(store);
                        gameDetails.setGame(game);
                        gameDetailsRepository.save(gameDetails);
                        actionLogService.logging("Добавление копии игры \"" + game.getGameName() + "\" " +
                                "(ID: " + game.getGameId() + ") " +
                                "с параметрами " + platform.getName() + "/" + store.getName() + "/" + gameDetails.getPrice());
                    }
                }
            }
        }

        return "redirect:/gameDetails?gameId="+gameId;
    }

    @GetMapping("/gameDetails/deleteDetail")
    public String deleteGameDetails(HttpServletRequest request,
                                          @RequestParam Long gameId,
                                          @RequestParam Long detailId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game = new Game();
        if(optionalGame.isPresent()) {
            game = optionalGame.get();

            if(game.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                Optional<GameDetails> optionalGameDetails = gameDetailsRepository.findById(detailId);
                if (optionalGameDetails.isPresent()) {
                    GameDetails gameDetails = optionalGameDetails.get();

                    actionLogService.logging("Удаление копии игры \"" + game.getGameName() + "\" " +
                            "(ID: " + game.getGameId() + ") " +
                            "с параметрами " + gameDetails.getPlatform().getName() + "/" + gameDetails.getStore().getName() + "/" + gameDetails.getPrice());
                    gameDetailsRepository.deleteById(detailId);
                }
            }
        }

        return "redirect:/gameDetails?gameId=" + gameId;
    }
}