package jp.co.saison.tvc.springbootdemo.app;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {
  @Autowired
  LoginUserService service;

  @RequestMapping
  public String home(Model model, Principal principal) {
    // ここでDBにアクセスし、情報を表示するようにするとよい
    Authentication auth = (Authentication) principal;
    String login_user_name = auth.getName();
    Date login_on = new Date();
    
    service.save(new LoginUser(login_user_name, login_on));
    List<LoginUser> login_users = service.findAll();

    System.out.println(login_users);

    return "home";
  }

}
