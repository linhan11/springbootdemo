package jp.co.saison.tvc.springbootdemo.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class MyLogoutHandler implements LogoutHandler {
  @Autowired
  LoginUserService service;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    LoginUser login_user = service.findOne(authentication.getName());

    service.delete(login_user);
  }

}
