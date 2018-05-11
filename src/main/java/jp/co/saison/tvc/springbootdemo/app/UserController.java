package jp.co.saison.tvc.springbootdemo.app;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	DemoUserService service;

	@Value("${marupeke.rest.hosturl}")
	private String hosturl;

	private String address = "api/users";

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
	 * ユーザ一覧を表示
	 */
	@SuppressWarnings("unchecked")
	@GetMapping
	public String index(Model model, Principal principal) {
		Authentication auth = (Authentication)principal;
		if (auth == null) {
	        return "redirect:/";
		}

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<? extends ArrayList<DemoUser>> responseEntity = restTemplate.getForEntity(hosturl + address, (Class<? extends ArrayList<DemoUser>>)ArrayList.class);
		ArrayList<DemoUser> users = responseEntity.getBody();

		model.addAttribute("users", users);
		return "users/index";
	}

	/*
	 * GET /users/show
	 * ユーザプロフィールを表示
	 */
	@GetMapping("show")
	public String show(Model model, Principal principal) {
		Authentication auth = (Authentication)principal;
		if (auth == null) {
	        return "redirect:/";
		}

		RestTemplate restTemplate = new RestTemplate();

		String name = auth.getName();

		DemoUser user = restTemplate.getForObject(hosturl + address + "/" + name, DemoUser.class);

		model.addAttribute("user", user);
		return "users/show";
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

		RestTemplate restTemplate = new RestTemplate();
		String name = auth.getName();
		DemoUser user = restTemplate.getForObject(hosturl + address + "/" + name, DemoUser.class);
		user.setPassword("password");
        model.addAttribute("user", user);
 		return "users/edit";
	}

	/*
	 * PUT /users
	 * ユーザ情報の登録・更新
	 */
	@PutMapping
    public String update(@ModelAttribute DemoUser user, Model model, Principal principal) {
		RestTemplate restTemplate = new RestTemplate();

		restTemplate.postForEntity(hosturl + address, user, DemoUser.class);

        return "redirect:/users/show";
    }

	/*
	 * POST /users
	 * ユーザの新規登録
	 */
    @PostMapping
    public String create(@ModelAttribute DemoUser user, Model model) {
		RestTemplate restTemplate = new RestTemplate();

		restTemplate.postForObject(hosturl + address, user, DemoUser.class);

        return "redirect:/";
    }

    @DeleteMapping
    public String delete(@ModelAttribute DemoUser user, Model model) {
		RestTemplate restTemplate = new RestTemplate();

		restTemplate.delete(hosturl + address, user);

		return "redirect:/";
    }

}