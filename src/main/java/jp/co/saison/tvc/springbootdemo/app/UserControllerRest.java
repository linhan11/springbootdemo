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
@RequestMapping("/api/users")
public class UserControllerRest {
	@Autowired
	DemoUserService service;

	@RequestMapping(method = RequestMethod.GET)
	public List<DemoUser> getDemoUsers() {
		return service.findAll();
	}

	@RequestMapping(value="{name}", method = RequestMethod.GET)
	public DemoUser getDemoUser(@PathVariable String name) {
		return service.findOne(name);
	}

	@RequestMapping(method = RequestMethod.POST)
	public DemoUser createDemoUser(@Validated @RequestBody DemoUser user) {
		service.save(user);
		return user;
	}


	@RequestMapping(method = RequestMethod.DELETE)
	public DemoUser deleteDemoUser(@Validated @RequestBody DemoUser user) {
		service.delete(user);
		return user;
	}

	/*
	@RequestMapping(value="{id}", method = RequestMethod.POST)
	public void updateDemoUser(@PathVariable("name") String name, @RequestBody DemoUser user) {
		user.setName(name);
		service.save(user);
	}
	*/

}

