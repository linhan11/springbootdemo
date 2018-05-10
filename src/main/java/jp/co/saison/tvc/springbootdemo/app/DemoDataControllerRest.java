package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatlog")
public class DemoDataControllerRest {
	@Autowired
	DemoDataService demoDataService;

	@RequestMapping(method = RequestMethod.GET)
	public List<DemoData> getChatLogs() {
		return demoDataService.findAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public DemoData addChatlog(@Validated @RequestBody DemoData demoData) {
		demoDataService.save(demoData);
		return demoData;
	}

}
