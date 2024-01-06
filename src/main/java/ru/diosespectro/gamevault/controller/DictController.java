package ru.diosespectro.gamevault.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import ru.diosespectro.gamevault.entity.*;
import ru.diosespectro.gamevault.repository.GameDetailsRepository;
import ru.diosespectro.gamevault.repository.GenreRepository;
import ru.diosespectro.gamevault.repository.PlatformRepository;
import ru.diosespectro.gamevault.repository.StoreRepository;
import ru.diosespectro.gamevault.service.ActionLogService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
public class DictController {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private GameDetailsRepository gameDetailsRepository;

    @GetMapping("/dictionaries")
    public ModelAndView getDictionaries(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("dictionaries");
        mav.addObject("pageTitle", "Справочники");
        return mav;
    }

    @GetMapping("/dictionaries/genres")
    public ModelAndView getAllGenres(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-genres");

        if (inputFlashMap != null) {
            String error = "";

            Genre genre = (Genre) inputFlashMap.get("genreDeleteError");
            if(genre != null) {
                error = "Невозможно удалить жанр \"" + genre.getName() + "\", так как есть связанные с ним игры!";
            } else {
                genre = (Genre) inputFlashMap.get("genreMatchError");
                if(genre != null) {
                    error = "Операция отклонена. Жанр с именем \"" + genre.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("genres", genreRepository.findAll());
        mav.addObject("pageTitle", "Справочник жанров");
        return mav;
    }

    @GetMapping("/dictionaries/genres/addGenreForm")
    public ModelAndView addGenreForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-genre-form");
        Genre genre = new Genre();
        mav.addObject("genre", genre);
        mav.addObject("pageTitle", "Добавить жанр");
        return mav;
    }

    @PostMapping("/dictionaries/genres/saveGenre")
    public String saveGenre(@ModelAttribute Genre genre, RedirectAttributes redirectAttributes) {
        Genre check = genreRepository.findByName(genre.getName());
        if(check == null) {
            genreRepository.save(genre);
            actionLogService.logging("Сохранение жанра \"" + genre.getName() + "\" (ID: " + genre.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("genreMatchError", check);
        }

        return "redirect:/dictionaries/genres";
    }

    @GetMapping("/dictionaries/genres/updateGenre")
    public ModelAndView updateGenre(@RequestParam Long genreId) {
        ModelAndView mav = new ModelAndView("add-genre-form");
        Optional<Genre> optionalGenre = genreRepository.findById(genreId);
        Genre genre = new Genre();
        if(optionalGenre.isPresent()){
            genre = optionalGenre.get();
        }
        mav.addObject("genre", genre);
        mav.addObject("pageTitle", "Изменить жанр");
        return mav;
    }

    @GetMapping("/dictionaries/genres/deleteGenre")
    public String deleteGenre(@RequestParam Long genreId, RedirectAttributes redirectAttributes){
        Optional<Genre> optionalGenre = genreRepository.findById(genreId);
        Genre genre = new Genre();
        if(optionalGenre.isPresent()){
            genre = optionalGenre.get();

            List<Game> games = genre.getGames();

            if(games.size() == 0) {
                actionLogService.logging("Удаление жанра \"" + genre.getName() + "\" (ID: " + genre.getId() + ")");
                genreRepository.deleteById(genreId);
            } else {
                redirectAttributes.addFlashAttribute("genreDeleteError", genre);
            }
        }
        return "redirect:/dictionaries/genres";
    }



    @GetMapping("/dictionaries/platforms")
    public ModelAndView getAllPlatforms(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-platforms");

        if (inputFlashMap != null) {
            String error = "";

            Platform platform = (Platform) inputFlashMap.get("platformDeleteError");
            if(platform != null) {
                error = "Невозможно удалить платформу \"" + platform.getName() + "\", так как есть связанные с ней игры!";
            } else {
                platform = (Platform) inputFlashMap.get("platformMatchError");
                if(platform != null) {
                    error = "Операция отклонена. Платформа с именем \"" + platform.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("platforms", platformRepository.findAll());
        mav.addObject("pageTitle", "Справочник платформ");
        return mav;
    }

    @GetMapping("/dictionaries/platforms/addPlatformForm")
    public ModelAndView addPlatformForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-platform-form");
        Platform platform = new Platform();
        mav.addObject("platform", platform);
        mav.addObject("pageTitle", "Добавить платформу");
        return mav;
    }

    @PostMapping("/dictionaries/platforms/savePlatform")
    public String savePlatform(@ModelAttribute Platform platform, RedirectAttributes redirectAttributes) {
        Platform check = platformRepository.findByName(platform.getName());
        if(check == null) {
            platformRepository.save(platform);
            actionLogService.logging("Сохранение платформы \"" + platform.getName() + "\" (ID: " + platform.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("platformMatchError", check);
        }

        return "redirect:/dictionaries/platforms";
    }

    @GetMapping("/dictionaries/platforms/updatePlatform")
    public ModelAndView updatePlatform(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-platform-form");
        Optional<Platform> optionalPlatform = platformRepository.findById(id);
        Platform platform = new Platform();
        if(optionalPlatform.isPresent()){
            platform = optionalPlatform.get();
        }
        mav.addObject("platform", platform);
        mav.addObject("pageTitle", "Изменить платформу");
        return mav;
    }

    @GetMapping("/dictionaries/platforms/deletePlatform")
    public String deletePlatform(@RequestParam Long id, RedirectAttributes redirectAttributes){
        Optional<Platform> optionalPlatform = platformRepository.findById(id);
        Platform platform;
        if(optionalPlatform.isPresent()){
            platform = optionalPlatform.get();

            List<GameDetails> gameDetails = gameDetailsRepository.findAllByPlatform(platform);

            if(gameDetails.size() == 0) {
                actionLogService.logging("Удаление платформы \"" + platform.getName() + "\" (ID: " + platform.getId() + ")");
                platformRepository.deleteById(id);
            } else {
                redirectAttributes.addFlashAttribute("platformDeleteError", platform);
            }
        }
        return "redirect:/dictionaries/platforms";
    }




    @GetMapping("/dictionaries/stores")
    public ModelAndView getAllStores(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-stores");

        if (inputFlashMap != null) {
            String error = "";

            Store store = (Store) inputFlashMap.get("storeDeleteError");
            if(store != null) {
                error = "Невозможно удалить магазин \"" + store.getName() + "\", так как есть связанные с ним игры!";
            } else {
                store = (Store) inputFlashMap.get("storeMatchError");
                if(store != null) {
                    error = "Операция отклонена. Магазин с именем \"" + store.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("stores", storeRepository.findAll());
        mav.addObject("pageTitle", "Справочник магазинов");
        return mav;
    }

    @GetMapping("/dictionaries/stores/addStoreForm")
    public ModelAndView addStoreForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-store-form");
        Store store = new Store();
        mav.addObject("store", store);
        mav.addObject("pageTitle", "Добавить магазин");
        return mav;
    }

    @PostMapping("/dictionaries/stores/saveStore")
    public String saveStore(@ModelAttribute Store store, RedirectAttributes redirectAttributes) {
        Store check = storeRepository.findByName(store.getName());
        if(check == null) {
            storeRepository.save(store);
            actionLogService.logging("Сохранение магазина \"" + store.getName() + "\" (ID: " + store.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("storeMatchError", check);
        }

        return "redirect:/dictionaries/stores";
    }

    @GetMapping("/dictionaries/stores/updateStore")
    public ModelAndView updateStore(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-store-form");
        Optional<Store> optionalStore = storeRepository.findById(id);
        Store store = new Store();
        if(optionalStore.isPresent()){
            store = optionalStore.get();
        }
        mav.addObject("store", store);
        mav.addObject("pageTitle", "Изменить магазин");
        return mav;
    }

    @GetMapping("/dictionaries/stores/deleteStore")
    public String deleteStore(@RequestParam Long id, RedirectAttributes redirectAttributes){
        Optional<Store> optionalStore = storeRepository.findById(id);
        Store store ;
        if(optionalStore.isPresent()){
            store = optionalStore.get();

            List<GameDetails> gameDetails = gameDetailsRepository.findAllByStore(store);

            if(gameDetails.size() == 0) {
                actionLogService.logging("Удаление магазина \"" + store.getName() + "\" (ID: " + store.getId() + ")");
                storeRepository.deleteById(id);
            } else {
                redirectAttributes.addFlashAttribute("storeDeleteError", store);
            }
        }
        return "redirect:/dictionaries/stores";
    }
}
