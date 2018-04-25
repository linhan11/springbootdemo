package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	DemoUserService service;

	@GetMapping
	public String index(Model model) {
		List<DemoUser> users = service.findAll();
		model.addAttribute("users", users);
		return "users/index";
	}

	@GetMapping("new")
	public String newUser(Model model) {
		return "users/new";
	}


	@GetMapping("{name}/edit")
	public String edit(@PathVariable String name, Model model) {
		DemoUser user = service.findOne(name);
		user.setPassword("password");
        model.addAttribute("user", user);
 		return "users/edit";
	}

	@GetMapping("{name}")
	public String show(@PathVariable String name, Model model) {
		DemoUser user = service.findOne(name);
        model.addAttribute("user", user);
 		return "users/show";
	}

	@PutMapping("{name}")
    public String update(@PathVariable String name, @ModelAttribute DemoUser user) {
		user.setName(name);
		service.save(user);
        return "redirect:/users";
    }

}