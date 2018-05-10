package jp.co.saison.tvc.springbootdemo.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    // 全ての接続に対し、ログインリストの更新を通知する
    sendUserList();

    System.out.printf("login user:%s id:%s\n", userID, sessionID);
  }

  private void sendUserList() {
    // 全セッションの情報をユーザリストに作成
    List<String> userList = new ArrayList<>();
    gameSessionData.forEach((key, value) -> {
      userList.add(String.format(
          "{\"user\":\"%s\",\"status\":\"%s\", \"login_on\":\"%s\",\"id\":\"%s\"}", value.getUser(),
          value.isMtach() == true ? "対戦中" : "待機中", value.getStartDate(), value.getSessionID()));
    });
    String msg = "{\"proto\":\"login_list\",\"login_list\":[" + String.join(",", userList) + "]}";

    // 全ての接続に対し、ログインリストの更新を通知する
    gameSessionData.forEach((key, value) -> {sendMSG(key, msg);});

    System.out.printf("msg=%s\n", msg);

  }

  /***
   * 多分 webSocket.sendで呼ばれる
   */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String sessionID = session.getId();
    GameJSON g = gameSessionData.get(sessionID);

    GameJSON gj = GameJSON.getInstanceFromJSON(message.getPayload());

    switch (gj.getProto()) {
      case "matchWithReq": // 対戦の申し込み {"proto":"matchWithReq","targetID":"targetSeesionID"}
        sendMSG(gj.getTargetID(),
            "{\"proto\":\"matchWithReq\",\"targetID\":\"" + sessionID + "\"}");
        break;
      case "matchWithRep": // 対戦の申し込み結果
                           // {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK or
                           // NG"}
        if (gj.getStatus().equals("OK")) {
          // OKの場合は両セッションにメッセージを送る
          int s = new Random().nextInt(2);
          String fmt = "{\"proto\":\"matchStart\",\"targetID\":\"%s\", \"status\":\"%s\"}";
          sendMSG(gj.getTargetID(), String.format(fmt, sessionID, s == 0 ? "First" : "Second"));
          sendMSG(sessionID, String.format(fmt, gj.getTargetID(), s == 0 ? "Second" : "First"));
        } else {
          // NGの場合は申し込んだほうのセッションにのみメッセージを送る
          sendMSG(gj.getTargetID(),
              "{\"proto\":\"matchWithRep\",\"targetID\":\"" + sessionID + "\", \"status\":\"NG\"}");
        }
        break;
      default:
        break;
    }

    System.out.printf("sessionID:%s message:%s %s %s\n", sessionID, message.getPayload(),
        g.getStartDate(), gj);

  }

  private void sendMSG(String targetID, String msg) {
    try {
      gameSessionData.get(targetID).getSession().sendMessage(new TextMessage(msg.getBytes()));
    } catch (IOException e) {
      // クライアント側は既に切れているのでサーバ側は今はスタックトーレスのみ出しておく
      e.printStackTrace();
    }
  }

  /**
   * 多分websocketクローズ後に呼ばれる
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String sessionID = session.getId();
    gameSessionData.remove(sessionID);

    // 全ての接続に対し、ログインリストの更新を通知する
    sendUserList();

    System.out.printf("session close sessionID:%s\n", sessionID);
  }

}
