package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gamedata")
public class GameDataControllerRest {

	@Autowired
	GameDataService gameDataService;

	@RequestMapping(method = RequestMethod.GET)
	public List<GameData> getGameDatas() {
		return gameDataService.findAll();
	}

	@RequestMapping(value="{id}", method = RequestMethod.GET)
	public List<GameData> getGameDatas(@PathVariable String id) {
		return gameDataService.findBySession(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	public GameData saveGameDatar(@Validated @RequestBody GameData gameData) {
		gameDataService.save(gameData);
		return gameData;
	}

}
