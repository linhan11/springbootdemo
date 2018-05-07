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
    registry.addHandler(gameHandler, "/websocket/game");//対戦相手マッチ処理および実対戦処理
    
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
     *   　　　　　　　　　　　　　　
     *   　　　　　　　　　　　　　　ログイン data なし .. このメッセージを受信したらログインの通知を(自身を除く)マルチキャストで返却
     *   　　　　　　　　　　　　　　ログインの通知 .. data ログイン中のユーザ一覧および対戦中か否か ログイン中の全セッションに対し返却　useridとstatusのクラスのリスト
     *   　　　　　　　　　　　　　　対戦の申し込み  data なし
     *   　                                   対戦の申し込みの返却  data OK, NG
     *   　　　　　　　　　　　　　　対戦の開始 data 先攻・後攻
     *   　　　　　　　　　　　　　　○×の配置(手番側) data 9文字の盤データ　座標とコマの状態のクラスのリスト
     *   　                                  ○×の配置結果の送信(待ち側) 座標とコマの状態のクラスのリスト
     *   　　　　　　　　　　　　　　終了の通知 data 勝者
     *   
     *   　　　　　　　　　　　　　　以下予約
     *   　　　　　　　　　　　　　　中断
     *   　　　　　　　　　　　　　　投了
     *   status 通信やDBの異常有無
     *   from   getQueryから取得できるものと同じfromのユーザID
     *   to     相手のユーザID
     *   data   汎用データ
     *   
     *   jsonのparseはjacksonを使う
     */
  }

}
