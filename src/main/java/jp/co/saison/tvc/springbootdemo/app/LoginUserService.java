package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;;

@Service
@Transactional
public class LoginUserService {

	@Autowired
	LoginUserRepository repository;

	public List<LoginUser> findAll() {
		return repository.findAll();
	}

	public LoginUser findOne(String Name) {
		for (LoginUser user : repository.findAll()) {
			if (Name.equals(user.getName())) {
				return user;
			}
		}
		return null;
	}

    public LoginUser save(LoginUser user) {
        return repository.save(user);
    }

    public void delete(LoginUser user) {
      repository.delete(user);
    }

}
