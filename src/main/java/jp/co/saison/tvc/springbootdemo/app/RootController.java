package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {
  @RequestMapping
  public String home() {
    //ここでDBにアクセスし、情報を表示するようにするとよい
    return "home";
  }

}
