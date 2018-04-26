package jp.co.saison.tvc.springbootdemo.app;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	DemoUserService service;

	private DemoUser createUser(Principal principal) {
		Authentication auth = (Authentication)principal;
		String name = auth.getName();

		DemoUser user = service.findOne(name);
		if (user == null) {
			user = new DemoUser();
			user.setName(name);
			service.save(user);
		}

		return user;
	}

	/*
	 * GET /users
	 * ユーザプロフィールを表示
	 */
	@GetMapping
	public String index(Model model, Principal principal) {
		Authentication auth = (Authentication)principal;
		if (auth == null) {
	        return "redirect:/";
		}
		String name = auth.getName();
		DemoUser user = service.findOne(name);
		model.addAttribute("user", user);
		return "users/show";
	}

	@GetMapping("list")
	public String list(Model model) {
		List<DemoUser> users = service.findAll();
		model.addAttribute("users", users);
		return "users/list";
	}

	/*
	 * GET /users/new
	 * ユーザの新規登録ページの表示
	 */
	@GetMapping("new")
	public String newUser(Model model, Principal principal) {
		if (principal != null) {
	        return "redirect:/";
		}
		DemoUser user = new DemoUser();
		user.setName("unknown");
		user.setPassword("password");
		model.addAttribute("user", user);
		return "users/new";
	}

	/*
	 * GET /users/edit
	 * ユーザ情報の表示
	 */
	@GetMapping("edit")
	public String edit(Model model, Principal principal) {
		Authentication auth = (Authentication)principal;
		if (auth == null) {
	        return "redirect:/";
		}
		String name = auth.getName();
		DemoUser user = service.findOne(name);
		user.setPassword("password");
        model.addAttribute("user", user);
 		return "users/edit";
	}

	/*
	 * PUT /users
	 * ユーザ情報の更新
	 */
	@PutMapping
    public String update(@ModelAttribute DemoUser user, Model model, Principal principal) {
		service.save(user);
        return "redirect:/users";
    }

	/*
	 * POST /users
	 * ユーザの新規登録
	 */
    @PostMapping
    public String create(@ModelAttribute DemoUser user, Model model) {
    	service.save(user);
        return "redirect:/";
    }


}