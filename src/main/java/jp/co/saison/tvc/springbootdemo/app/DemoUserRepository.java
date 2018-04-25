package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoUserRepository  extends JpaRepository<DemoUser, Long> {
	//public DemoUser findByName(String name);
	//public DemoUser findOne(String name);
}
