package jp.co.saison.tvc.springbootdemo.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// このクラスのインスタンスの持続期間はおそらくサーバ稼働中

@Component
public class GameHandler extends TextWebSocketHandler {
  private ConcurrentHashMap<String, GameJSON> gameSessionData = new ConcurrentHashMap<>();

  /***
   * おそらくconnectionが成立したときに呼び出される
   */
  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    String userID = session.getUri().getQuery();
    String sessionID = session.getId();
    GameJSON g = new GameJSON(userID, sessionID, session);

    // 今回のセッションをセッションプールに追加
    gameSessionData.put(sessionID, g);

    // 全セッションの情報を各ユーザに通知
    List<String> userList = new ArrayList<>();
    gameSessionData.forEach((key, value) -> {
      userList.add(String.format("{\"user\":\"%s\",\"status\":\"%s\", \"login_on\":\"%s\",\"id\":\"%s\"}",
          value.getUser(), value.isMtach() == true ? "対戦中" : "待機中", value.getStartDate(), value.getSessionID()));
    });
    String msg = "{\"proto\":\"login_list\",\"login_list\":[" + String.join(",", userList) + "]}";

    TextMessage message = new TextMessage(msg.getBytes());
    gameSessionData.forEach((key, value) -> {
      try {
        value.getSession().sendMessage(message);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

    System.out.printf("GameHandler:%s %s msg=%s\n", userID, sessionID, msg);
  }

  /***
   * 多分 webSocket.sendで呼ばれる
   */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String sessionID = session.getId();
    GameJSON g = gameSessionData.get(sessionID);
    /*
     * やり取りしたメッセージをDBへ保持
     *
     * メッセージ形式 セッションID 識別子に利用 ユーザでわかるか？ ログインユーザ メッセージ
     *
     * TODO: ログインユーザ名を取得
     */

    GameJSON gj = GameJSON.getInstanceFromJSON(message.getPayload());

    System.out.printf("sessionID:%s message:%s %s %s\n", sessionID, message.getPayload(),
        g.getStartDate(), gj);
  }

  /**
   * 多分websocketクローズ後に呼ばれる
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String sessionID = session.getId();
    gameSessionData.remove(sessionID);
    System.out.printf("session close sessionID:%s\n", sessionID);
  }

}
