package jp.co.saison.tvc.springbootdemo.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// このクラスのインスタンスの持続期間はおそらくサーバ稼働中

@Component
public class GameHandler extends TextWebSocketHandler {
  private ConcurrentHashMap<String, GameJSON> gameSessionData = new ConcurrentHashMap<>();

  @Autowired
  GameDataService serviceGame;
  @Autowired
  DemoUserService serviceUser;

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
      userList.add(String.format(
          "{\"user\":\"%s\",\"status\":\"%s\", \"login_on\":\"%s\",\"id\":\"%s\"}", value.getUser(),
          value.getProgressString(), value.getStartDate(), value.getSessionID()));
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
      case "matchWithReq": // 対戦の申し込み {"proto":"matchWithReq","targetID":"targetID"}

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
                           // {"proto":"matchWithRep","targetID":"targetID","status":"OK/NG"}
        // OKの場合は両セッションに対戦開始のメッセージを送る
        if (msgToGameJson.getStatus().equals("OK")) {
          int s = new Random().nextInt(2);
          String fmt = "{\"proto\":\"matchStart\",\"targetID\":\"%s\", \"status\":\"%s\"}";
          sendMSG(targetID, String.format(fmt, myID, s == 0 ? "First" : "Second"));
          sendMSG(myID, String.format(fmt, targetID, s == 0 ? "Second" : "First"));
          String gameID = UUID.randomUUID().toString();
          myGameData.setProgress(GameJSON.PROGRESS.MATCHING);
          myGameData.setGameID(gameID);
          myGameData.setFirst(s == 0 ? true : false);
          targetGameData.setProgress(GameJSON.PROGRESS.MATCHING);
          targetGameData.setGameID(gameID);
          targetGameData.setFirst(s == 0 ? true : false);
        } else {
          // NGの場合は自分は対戦待ちに戻し、申し込んだほうのセッションに対戦NGを伝える
          sendMSG(targetID,
              "{\"proto\":\"matchWithRep\",\"targetID\":\"" + myID + "\", \"status\":\"NG\"}");
          myGameData.setProgress(GameJSON.PROGRESS.WAIT);
        }
        break;
      case "matching": // 対戦中
                       // {"proto":"matching", "targetID":"targetID", "status":"盤データ"
                       // 盤データは置かれた順番が分かるようにしてもらえると途中セーブが不要になる
        // 対戦中の場合は、相手にそのデータを通知だけすればよい
        sendMSG(targetID,
            String.format("{\"proto\":\"matching\", \"targetID\":\"%s\", \"status\":%s}", myID,
                msgToGameJson.getStatus()));
        break;
      case "matchEnd": // 対戦終了
                       // {"proto":"matchEnd", "targetID":"targetID",
                       // "status":"盤データ","result":"WIN/LOSE/SUSPEND/DRAW"}

        // 対戦結果をセーブ
        String first = myGameData.isFirst() ? myID : targetID;
        String second = myGameData.isFirst() ? targetID : myID;
        serviceGame.save(myGameData.getGameID(), first, second, myGameData.getResult(),
            myGameData.getStatus());

        // それぞれのユーザの対戦成績を反映
        DemoUser user1st = serviceUser.findOne(first);
        DemoUser user2nd = serviceUser.findOne(second);
        switch (myGameData.getResult()) {
          case "WIN":
            user1st.setWin(user1st.getWin() + 1);
            user2nd.setLose(user2nd.getLose() + 1);
            break;
          case "LOSE":
            user1st.setLose(user1st.getLose() + 1);
            user2nd.setWin(user2nd.getWin() + 1);
            break;
          case "DRAW":
            user1st.setDraw(user1st.getDraw() + 1);
            user2nd.setDraw(user2nd.getDraw() + 1);
            break;
        }
        serviceUser.save(user1st);
        serviceUser.save(user2nd);

        // ステータスをWAITに戻す
        myGameData.setProgress(GameJSON.PROGRESS.WAIT);
        targetGameData.setProgress(GameJSON.PROGRESS.WAIT);

        break;
      default:
        break;
    }

    System.out.printf("sessionID:%s message:%s %s %s\n", myID, message.getPayload(),
        myGameData.getStartDate(), msgToGameJson);
    sendUserList();
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

    // 対戦中の場合は、相手ユーザに通知する

    // 全ての接続に対し、ログインリストの更新を通知する
    sendUserList();

    System.out.printf("session close sessionID:%s\n", sessionID);
  }

}
