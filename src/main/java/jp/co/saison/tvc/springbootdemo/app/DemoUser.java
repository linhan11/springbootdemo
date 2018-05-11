package jp.co.saison.tvc.springbootdemo.app;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "demo_user")
public class DemoUser {

  @Id
  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "win")
  private Integer win;
  @Column(name = "lose")
  private Integer lose;
  @Column(name = "draw")
  private Integer draw;
  @Column(name = "date")
  private Date date;
  @Column(name = "url")
  private String url;

  protected DemoUser() {
    this.win = 0;
    this.lose = 0;
    this.url = "";
    this.date = new Date();
  }

  public Integer getDraw() {
    return draw;
  }

  public void setDraw(Integer draw) {
    this.draw = draw;
  }

  public DemoUser(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getWin() {
    return win;
  }

  public void setWin(Integer win) {
    this.win = win;
  }

  public Integer getLose() {
    return lose;
  }

  public void setLose(Integer lose) {
    this.lose = lose;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void addWin() {
    if (win == null) {
      win = new Integer(0);
    }
    win += 1;
  }
  public void addLose() {
    if (lose == null) {
      lose = new Integer(0);
    }
    lose += 1;
  }
  public void addDraw() {
    if (draw == null) {
      draw = new Integer(0);
    }
    draw += 1;
  }

}
