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
  private final GameHandler gameHandler = new GameHandler();
  

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(chatHandler, "/endpoint");
    registry.addHandler(gameHandler, "/game");//対戦相手マッチ処理および実対戦処理
    
    /*
     *  ログイン時に /gameのwabsocketをonOpenする ?ユーザID
     *  対戦したくなったユーザは対戦したいID(to側)をメッセージで送る(JSONかなにかでプロトコルを作る必要あり)
     *  サーバはそれを受信し、対戦したいクライアントに対し、from側のIDを送る
     *  to側は対戦OKであればその旨を、NGであればその旨をサーバに送る
     *  サーバはto側の意向をfromに送信する。
     *  以下続く
     *  
     *  プロトコル
     *   protocol 処理種別 (対戦相手指定、対戦実処理)
     *   status 処理結果
     *   from   getQueryから取得できるものと同じfromのユーザID
     *   to     相手のユーザID
     *   data   汎用データ
     */
  }

}
