package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;;

@Service
@Transactional
public class DemoUserService {

	@Autowired
	DemoUserRepository repository;

	public List<DemoUser> findAll() {
		return repository.findAll();
	}

	public DemoUser findOne(String Name) {
		for (DemoUser user : repository.findAll()) {
			if (Name.equals(user.getName())) {
				return user;
			}
		}
		return null;
	}

    public DemoUser save(DemoUser user) {
        return repository.save(user);
    }

}
