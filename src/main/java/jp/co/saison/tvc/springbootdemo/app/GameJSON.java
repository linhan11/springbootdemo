package jp.co.saison.tvc.springbootdemo.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameJSON {
  enum PROGRESS {
    WAIT, MATCHWIDH, MATCHING
  }

  private String proto;// プロトコル種別
  private String user;
  private String login_on; // ログイン日時
  private String sessionID;// セッションID
  private String targetID; // 通信相手のセッションID
  private String status; // 各プロトコルの追加情報
  private PROGRESS progress;

  public String getProgressString() {
    switch (progress) {
      case WAIT:
        return "待機中";
      case MATCHWIDH:
        return "対戦依頼中";
      case MATCHING:
        return "対戦中";
    }
    return "不明";
  }

  public PROGRESS getProgress() {
    return progress;
  }

  public void setProgress(PROGRESS progress) {
    this.progress = progress;
  }

  @JsonIgnore
  private WebSocketSession session;

  private static ObjectMapper objectMapper = new ObjectMapper();

  public GameJSON(String user, String sessionID, WebSocketSession session) {
    this.user = user;
    this.sessionID = sessionID;
    this.session = session;
    this.progress = PROGRESS.WAIT;
    this.login_on = new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'mm'分'ss'秒'").format(new Date());
  }

  public GameJSON() {

  }

  public WebSocketSession getSession() {
    return session;
  }

  public String getSessionID() {
    return sessionID;
  }

  public String getStartDate() {
    return login_on;
  }

  public String getUser() {
    return user;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public String getTargetID() {
    return targetID;
  }

  public String getStatus() {
    return status;
  }
}
