package jp.co.saison.tvc.springbootdemo.app;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatHandler extends TextWebSocketHandler {
  private ConcurrentHashMap<String, Set<WebSocketSession>> roomSessionPool =
			new ConcurrentHashMap<>();

  String url = "http://localhost:8080/api/chatlog";

	@Autowired
	DemoDataService service;

  /***
   * おそらくconnectionが成立したときに呼び出される
   */
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    String roomName = session.getUri().getQuery();
    roomSessionPool.compute(roomName, (key, sessions) -> {
      if (sessions == null) {
        sessions = new CopyOnWriteArraySet<>();
      }
      sessions.add(session);
      return sessions;
    });
  }

  /***
   * 多分 webSocket.sendで呼ばれる
   */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String roomName = session.getUri().getQuery();

    /*
     * やり取りしたメッセージをDBへ保持
     *
     * メッセージ形式
     *   セッションID 識別子に利用 ユーザでわかるか？
     *   ログインユーザ
     *   メッセージ
     *
     * TODO:
     *  ログインユーザ名を取得
     */
     for (WebSocketSession roomSession : roomSessionPool.get(roomName)) {
      roomSession.sendMessage(message);
      //System.out.printf("%s:%s:%s(%s)\n", session.toString(), "unknown", message.getPayload().toString(), message.getPayload());

      DemoData demoData = new DemoData();
      demoData.setSession(session.toString());
      demoData.setName("unknown");
      demoData.setMessage(message.getPayload());

      RestTemplate restTemplate = new RestTemplate();
      restTemplate.postForEntity(url, demoData, DemoData.class);

    }
  }

  /**
   * 多分websocketクローズ後に呼ばれる
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String roomName = session.getUri().getQuery();

    roomSessionPool.compute(roomName, (key, sessions)->{
      sessions.remove(session);
      if (sessions.isEmpty()) {
      sessions = null;
      }
      return sessions;
    });
  }


}
