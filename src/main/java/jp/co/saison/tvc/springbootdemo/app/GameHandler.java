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
    String myID = session.getId();
    GameJSON myGameData = new GameJSON(userID, myID, session);

    // 今回のセッションをセッションプールに追加
    gameSessionData.put(myID, myGameData);

    // 全ての接続に対し、ログインリストの更新を通知する
    sendUserList();

    System.out.printf("login user:%s id:%s\n", userID, myID);
  }

  private void sendUserList() {
    // 全セッションの情報をユーザリストに作成
    List<String> userList = new ArrayList<>();
    gameSessionData.forEach((key, value) -> {
      userList.add(
          String.format("{\"user\":\"%s\",\"status\":\"%s\", \"login_on\":\"%s\",\"id\":\"%s\"}",
              value.getUser(), value.getProgressString(), value.getStartDate(), value.getSessionID()));
    });
    String fmt = "{\"proto\":\"login_list\",\"id\":\"%s\", \"login_list\":["
        + String.join(",", userList) + "]}";

    // 全ての接続に対し、ログインリストの更新を通知する
    gameSessionData.forEach((key, value) -> {
      String msg = String.format(fmt, key);
      sendMSG(key, msg);
      System.out.printf("msg=%s\n", msg);
    });

  }

  /***
   * 多分 webSocket.sendで呼ばれる
   */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String myID = session.getId();
    GameJSON myGameData = gameSessionData.get(myID);
    GameJSON targetGameData = null;
    GameJSON msgToGameJson = GameJSON.getInstanceFromJSON(message.getPayload());
    String targetID = msgToGameJson.getTargetID();

    if (targetID != null && targetID.isEmpty() == false) {
      targetGameData = gameSessionData.get(targetID);
    }

    switch (msgToGameJson.getProto()) {
      case "matchWithReq": // 対戦の申し込み {"proto":"matchWithReq","targetID":"targetSeesionID"}

        // 対戦待ちもしくは対戦中の場合は申し込みを却下
        if (targetGameData.getProgress() == GameJSON.PROGRESS.MATCHWIDH
            || targetGameData.getProgress() == GameJSON.PROGRESS.MATCHING) {
          sendMSG(myID, "{\"proto\":\"matchWithRep\",\"targetID\":\"" + msgToGameJson.getTargetID()
              + "\", \"status\":\"NG\"}");
          return;
        }
        // 自分の状態遷移を対戦申込み中に変更し対戦相手に申し込みを行う
        myGameData.setProgress(GameJSON.PROGRESS.MATCHWIDH);
        sendMSG(targetID, "{\"proto\":\"matchWithReq\",\"targetID\":\"" + myID + "\"}");
        break;
      case "matchWithRep": // 対戦の申し込み結果
                           // {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK/NG"}
        // OKの場合は両セッションに対戦開始のメッセージを送る
        if (msgToGameJson.getStatus().equals("OK")) {
          int s = new Random().nextInt(2);
          String fmt = "{\"proto\":\"matchStart\",\"targetID\":\"%s\", \"status\":\"%s\"}";
          sendMSG(targetID, String.format(fmt, myID, s == 0 ? "First" : "Second"));
          sendMSG(myID, String.format(fmt, targetID, s == 0 ? "Second" : "First"));
          myGameData.setProgress(GameJSON.PROGRESS.MATCHING);
          targetGameData.setProgress(GameJSON.PROGRESS.MATCHING);
        } else {
          // NGの場合は自分は対戦待ちに戻し、申し込んだほうのセッションに対戦NGを伝える
          sendMSG(targetID,
              "{\"proto\":\"matchWithRep\",\"targetID\":\"" + myID + "\", \"status\":\"NG\"}");
          myGameData.setProgress(GameJSON.PROGRESS.WAIT);
        }
        break;
      default:
        break;
    }

    System.out.printf("sessionID:%s message:%s %s %s\n", myID, message.getPayload(),
        myGameData.getStartDate(), msgToGameJson);

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
