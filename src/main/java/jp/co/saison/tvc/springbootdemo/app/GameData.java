package jp.co.saison.tvc.springbootdemo.app;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "game_data")
public class GameData {

  @Id
  @Column(name = "id")
  private String id;
  @Column(name = "first")
  private String first;
  @Column(name = "second")
  private String second;
  @Column(name = "result")
  private String result;

  private String end_date;
  @Column(name = "data")
  private String data;// 盤データ

  protected GameData() {
    this.end_date = new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'mm'分'ss'秒'").format(new Date());
  }

  protected GameData(String id, String first, String second, String result, String data) {
    this.id = id;
    this.first = first;
    this.second = second;
    this.end_date = new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'mm'分'ss'秒'").format(new Date());
    this.result = result;
    this.data = data;
  }

  public String getResult() {
    return result;
  }

  public String getEnd_date() {
    return end_date;
  }

  public String getId() {
    return id;
  }

  public String getFirst() {
    return first;
  }

  public String getSecond() {
    return second;
  }

  public String getData() {
    return data;
  }
}
