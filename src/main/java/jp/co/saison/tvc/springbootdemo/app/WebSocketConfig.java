package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  @Autowired
  private final ChatHandler chatHandler = new ChatHandler();
  @Autowired
  private final GameHandler gameHandler = new GameHandler();


  //ここにエンドポイントとハンドラクラスを指定すればWebSocketが使える
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(chatHandler, "/endpoint");
    registry.addHandler(gameHandler, "/websocket/game");//対戦相手マッチ処理および実対戦処理
  }
}
