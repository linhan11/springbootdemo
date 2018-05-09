package jp.co.saison.tvc.springbootdemo.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;

public class GameJSON {
  private String proto;// プロトコル種別
  private String user;
  private String login_on; // ログイン日時
  private String sessionID;// セッションID
  private boolean isMatch;// 対戦中の場合true

  @JsonIgnore
  private WebSocketSession session;

  private static ObjectMapper objectMapper = new ObjectMapper();

  public GameJSON(String user, String sessionID, WebSocketSession session) {
    this.user = user;
    this.sessionID = sessionID;
    this.session = session;
    this.login_on = new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'mm'分'ss'秒'").format(new Date());
  }

  public GameJSON() {

  }

  public WebSocketSession getSession() {
    return session;
  }

  public String getStartDate() {
    return login_on;
  }

  public String getUser() {
    return user;
  }

  public boolean isMtach() {
    return isMatch;
  }

  public String toString() {
    return "GameJSON [proto=" + proto + ", user=" + user + "]";
  }

  static public GameJSON getInstanceFromJSON(String json) {
    GameJSON gj = null;
    try {
      gj = objectMapper.readValue(json, GameJSON.class);
    } catch (IOException e) {
      System.out.print(e);
    }
    return gj;
  }

  public String getProto() {
    return proto;
  }

}
