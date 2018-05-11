package jp.co.saison.tvc.springbootdemo.app;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/records")
public class RecordsController {


	@Value("${marupeke.rest.hosturl}")
	private String hosturl;

	private String address = "/api/gamedata";


	@GetMapping
	public String index(Model model, Principal principal) {
		Authentication auth = (Authentication)principal;
		if (auth == null) {
	        return "redirect:/";
		}

		RestTemplate restTemplate = new RestTemplate();

		@SuppressWarnings("unchecked")
		ResponseEntity<? extends ArrayList<GameData>> responseEntity = restTemplate.getForEntity(hosturl + address, (Class<? extends ArrayList<GameData>>)ArrayList.class);
		ArrayList<GameData> records = responseEntity.getBody();

		model.addAttribute("records", records);
		return "records";
	}

}
