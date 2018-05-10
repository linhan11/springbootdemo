package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDataRepository  extends JpaRepository<GameData, String> {
}
