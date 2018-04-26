package jp.co.saison.tvc.springbootdemo.app;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatHandler extends TextWebSocketHandler {
  private ConcurrentHashMap<String, Set<WebSocketSession>> roomSessionPool =
      new ConcurrentHashMap<>();

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
     for (WebSocketSession roomSession : roomSessionPool.get(roomName)) {
      roomSession.sendMessage(message);
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
