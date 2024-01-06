package ru.diosespectro.gamevault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.diosespectro.gamevault.entity.*;
import ru.diosespectro.gamevault.repository.GameRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GamesStatisticImpl implements GamesStatistic{
    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserService userService;

    public String getStatistic(String statOption){
        String ret = "";
        String retHeader = "";

        Long curUserId = userService.getCurUserId();
        List<Game> gameList = gameRepository.findByCreatedBy(curUserId);

        Integer copies = 0;
        Float  price = 0.0f;

        switch (statOption) {
            case "general":
                    retHeader = "Общая статистика";

                    ret += getStatLine("Всего игр добавлено", String.valueOf(gameList.size()));
                    for (Game g : gameList) {
                        List<GameDetails> gD = g.getDetails();
                        for(GameDetails gDD: gD){
                            copies++;
                            price += gDD.getPrice();
                        }
                    }
                    ret += getStatLine("Всего копий игр", String.valueOf(copies));
                    ret += getStatLine("Общая стоимость, ₽", String.valueOf(price));

                    ret = getStatBlock(ret);
                break;

            case "platforms":
                    retHeader = "Статистика по платформам";

                    Map<Platform, Integer> platformMapCopies = new HashMap<>();
                    Map<Platform, Float> platformMapPrice = new HashMap<>();

                    for (Game g : gameList) {
                        List<GameDetails> gD = g.getDetails();
                        for(GameDetails gDD: gD){
                            Platform p = gDD.getPlatform();

                            if(platformMapCopies.containsKey(p))
                                platformMapCopies.put(p, platformMapCopies.get(p) + 1);
                            else
                                platformMapCopies.put(p, 1);

                            if(platformMapPrice.containsKey(p))
                                platformMapPrice.put(p, platformMapPrice.get(p) + gDD.getPrice());
                            else
                                platformMapPrice.put(p, gDD.getPrice());
                        }
                    }

                    ret = getStatBlockAll(platformMapCopies, platformMapPrice);
                break;

            case "stores":
                    retHeader = "Статистика по магазинам";

                    Map<Store, Integer> storeMapCopies = new HashMap<>();
                    Map<Store, Float> storeMapPrice = new HashMap<>();

                    for (Game g : gameList) {
                        List<GameDetails> gD = g.getDetails();
                        for(GameDetails gDD: gD){
                            Store s = gDD.getStore();

                            if(storeMapCopies.containsKey(s))
                                storeMapCopies.put(s, storeMapCopies.get(s) + 1);
                            else
                                storeMapCopies.put(s, 1);

                            if(storeMapPrice.containsKey(s))
                                storeMapPrice.put(s, storeMapPrice.get(s) + gDD.getPrice());
                            else
                                storeMapPrice.put(s, gDD.getPrice());
                        }
                    }

                    ret = getStatBlockAll(storeMapCopies, storeMapPrice);
                break;

            case "genres":
                    retHeader = "Статистика по жанрам";

                    Map<Genre, Integer> genreMapCopies = new HashMap<>();
                    Map<Genre, Float> genreMapPrice = new HashMap<>();

                    for (Game g : gameList) {
                        List<GameDetails> gD = g.getDetails();
                        Genre genre = g.getGenres().get(0);
                        for (GameDetails gDD : gD) {
                            if(genreMapCopies.containsKey(genre))
                                genreMapCopies.put(genre, genreMapCopies.get(genre) + 1);
                            else
                                genreMapCopies.put(genre, 1);

                            if(genreMapPrice.containsKey(genre))
                                genreMapPrice.put(genre, genreMapPrice.get(genre) + gDD.getPrice());
                            else
                                genreMapPrice.put(genre, gDD.getPrice());
                        }
                    }

                    ret = getStatBlockAll(genreMapCopies, genreMapPrice);
                break;
        }

        ret = "<h3>" + retHeader + "</h3>" + ret;

        return ret;
    }

    private String getStatLine(String header, String value) {
        return header + ": <strong>" + value + "</strong><br>";
    }

    private String getStatBlock(String text) {
        return "<div class=\"alert alert-success\" role=\"alert\">" +
                text +
                "</div>";
    }

    private String getStatBlockAll(Map<?, Integer> copiesMap, Map<?, Float> priceMap){
        String ret = "";
        Integer copies;
        Float price;

        for(Map.Entry<?, Integer> entry: copiesMap.entrySet()) {
            String local = "";

            Object key = entry.getKey();
            copies = entry.getValue();
            price = priceMap.get(key);

            local += getStatLine("<strong>" + key.toString() + "</strong>", "");
            local += getStatLine("Количество копий", String.valueOf(copies));
            local += getStatLine("Стоимость, ₽", String.valueOf(price));

            ret += getStatBlock(local);
        }

        if(ret.isEmpty()) ret = "Нечего отображать";

        return ret;
    }
}
