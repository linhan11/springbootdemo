package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;;

@Service
@Transactional
public class GameDataService {

	@Autowired
	GameDataRepository repository;

	public List<GameData> findAll() {
		return repository.findAll();
	}

	public List<GameData> findBySession(String id) {
		List<GameData> datas = repository.findAll();

		return datas.stream().filter(a -> id.equals(a.getId())).collect(Collectors.toList());
	}

	public GameData save(String gameid, String first, String second, String result, String data) {
		GameData gameData = new GameData(gameid, first, second, result, data);
        return save(gameData);
	}

    public GameData save(GameData data) {
        return repository.save(data);
    }

}
