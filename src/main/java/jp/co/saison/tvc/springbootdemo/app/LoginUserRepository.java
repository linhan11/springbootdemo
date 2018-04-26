package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginUserRepository  extends JpaRepository<LoginUser, Long> {
	//public DemoUser findByName(String name);
	//public DemoUser findOne(String name);
}
