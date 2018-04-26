package jp.co.saison.tvc.springbootdemo.app;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "login_user")
public class LoginUser {

  @Id
  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "login_on", nullable = false)
  private Date login_on;

  protected LoginUser() {}

  public LoginUser(String name, Date login_on) {
    this.name = name;
    this.login_on = login_on;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getLogin_on() {
    return login_on;
  }

  public void setLogin_on(Date login_on) {
    this.login_on = login_on;
  }

}
