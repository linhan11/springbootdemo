package jp.co.saison.tvc.springbootdemo.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;

public class GameJSON {
  private String proto;// プロトコル種別
  private String from; // 依頼元ユーザ
  private String to; // 依頼先ユーザ
  private String startDate; // ログイン日時
  private String sessionID;// セッションID
  private boolean isMatch;//対戦中の場合true 
  
  @JsonIgnore
  private WebSocketSession session;

  private static ObjectMapper objectMapper = new ObjectMapper();

  public GameJSON(String from, String sessionID, WebSocketSession session) {
    this.from = from;
    this.sessionID = sessionID;
    this.session = session;
    this.startDate = new SimpleDateFormat("yyyy'年'MM'月'dd'日'k'時'mm'分'ss'秒'").format(new Date());
  }

  public WebSocketSession getSession() {
    return session;
  }

  public String getStartDate() {
    return startDate;
  }
  
  public String getUser() {
    return from;
  }
  
  public boolean isMtach() {
    return isMatch;
  }

}
