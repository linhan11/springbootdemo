package jp.co.saison.tvc.springbootdemo.app;

import java.text.SimpleDateFormat;
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
  @Column(name = "status", nullable = false)
  private String status;

  protected LoginUser() {}

  public LoginUser(String name, Date login_on) {
    this(name, login_on, "login");
  }

  public LoginUser(String name, Date login_on, String status) {
    this.name = name;
    this.login_on = login_on;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogin_on() {
    return new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'mm'分'ss'秒'").format(login_on);
  }

  public void setLogin_on(Date login_on) {
    this.login_on = login_on;
  }

  public String getStatus() {
    if (status == null) {
      return "不明";
    }
    switch (status) {
      case "login":
        return "ログイン中";
      default:
        return "不明";
    }
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
