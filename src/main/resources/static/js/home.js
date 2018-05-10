var game = {}

/*
 * grind を x, y へ変更
 */
function convert_grind_to_xy() {
	switch (game.gridid) {
	case "grid_1":
		game.x = 0;
		game.y = 0;
		break;
	case "grid_2":
		game.x = 1;
		game.y = 0;
		break;
	case "grid_3":
		game.x = 2;
		game.y = 0;
		break;
	case "grid_4":
		game.x = 0;
		game.y = 1;
		break;
	case "grid_5":
		game.x = 1;
		game.y = 1;
		break;
	case "grid_6":
		game.x = 2;
		game.y = 1;
		break;
	case "grid_7":
		game.x = 0;
		game.y = 2;
		break;
	case "grid_8":
		game.x = 1;
		game.y = 2;
		break;
	case "grid_9":
		game.x = 2;
		game.y = 2;
		break;
	}
}


/*
 * 判定 x x x
 *
 * y y y
 *
 * x x x
 */
function check_map() {
}

function set_piece() {
	if (game.status != "start") {
		utl_set_status("開始状態ではありません");
		return;
	}
	if (game.piece != game.turn) {
		utl_set_status("あいてが試行中です");
		return;
	}

	var tmp = $("#" + game.gridid).text();
	if (tmp != "") {
		console.log("div val : [" + tmp + "]");
		return;
	}

	/*
	 * 駒を置けた
	 * Serverへmessageを送る
	 */
	$("#" + game.gridid).text(game.turn);

	convert_grind_to_xy();

	send_play_matching();

	if (game.turn == "X") {
		game.turn = "O";
	} else {
		game.turn = "X";
	}

	utl_turn_message();
}

/*
 * 要素からユーザIDを取得
 */
function get_loginuser() {
	return $('#loginuser').text();
}

/* ------------------------------------------------------------------------
 * メイン
 * ---------------------------------------------------------------------- */
$(document).ready(function() {
	game.status = "";
	game.userid = "";
	game.id = "";
	game.targetid = "";
	game.turn = "O";
	game.gridid = "";
	game.piece = "";
	game.map = [ [ "", "", "" ], [ "", "", "" ], [ "", "", "" ] ];

	for (var i = 1; i <= 9; i++) {
		$("#grid_" + i).click(function() {

			console.log("name   : " + $("#name").val());
			console.log("div id : " + this.id);
			game.gridid = this.id;

			set_piece();
		})
	}


	// ゲーム用websocket
	var user = get_loginuser();
	var uri = "ws://" + location.host + "/websocket/game" + '?' + user;
	game.userid = user;

	ws = new WebSocket(uri);
	ws.onopen = function() {
		var loginData = new Object();
		loginData.proto = "login";
		loginData.user = user;
		game.status = "login"
		ws.send(JSON.stringify(loginData));
	};
	ws.onmessage = function(message) {
		try {
			wsRes = JSON.parse(message.data);

			console.log("onmessage()");
			console.log("message : " + message);
			console.log("  proto : " + wsRes.proto);

			if (wsRes.proto == "login_list") {
				//alert(wsRes);
				recv_login_list(wsRes);
				makeUserTable(wsRes);
			} else if (wsRes.proto == "matchWithReq") {
				// 対戦の申し込み
				console.log(wsRes);
				recv_play_matchWithReq(wsRes);
			} else if (wsRes.proto == "matchWithRep") {
				// 対戦の申し込み結果
				console.log(wsRes);
				recv_play_matchWithRep(wsRes);
			} else if (wsRes.proto == "matchStart") {
				recv_play_matchStart(wsRes);
			} else if (wsRes.proto == "matching") {
				recv_play_matching(wsRes);
			}
		} catch (e) {
			alert(e);
		}
	};
	ws.onclose = function() {
	}
	ws.onerror = function() {
		alert("hello world!");
	};

})

function clearUsertable(name) {
	$("#userlist").children().remove();
}

/*
 * 対戦ユーザ一覧
 *
 *  一覧を作成し、対戦ボタンにセッションIDを保持
 */
function makeUserTable(data) {
	console.log("makeUserTable()");

	clearUsertable("userlist");
	for (var i = 0; i < data.login_list.length; i++) {
		var button_caption;
		var click;
		if (game.id == data.login_list[i].id) {
			button_caption = '-----';
			click = false;
		} else {
			button_caption = data.login_list[i].status;
			click = true;
		}
		$("#UserTable")
				.append(
						'<tr><td>'
								+ data.login_list[i].user
								+ '</td><td>'
								+ data.login_list[i].login_on
								+ '</td><td>'
								+ data.login_list[i].status
								+ '</td><td><button id="'
									+ data.login_list[i].id
									+ '" class="game-play btn btn-danger">'
									+ button_caption
									+ '</button></td></tr>');
		if (click) {
			var button = document.getElementById(data.login_list[i].id);
			button.addEventListener("click", myfunc);
		}
	}
}

function myfunc(event) {
	console.log("session id : " + this.id);
	var target = $(event.target);

	target.text("依頼");
	game.button = target;
	send_play_matchWithReq(this.id);
}

/*
 * ログイン時
 *  {"proto":"login_list","id":"1", "login_list":[]}
 */
function recv_login_list(data) {
	console.log("recv_login_list()");
	game.id = data.id;
	makeUserTable(data);
}

/*
 * // 対戦の申し込み {"proto":"matchWithReq","targetID":"targetSeesionID"}
 */
function send_play_matchWithReq(sessionid) {
	console.log("send_play_matchWithReq()");

	var data = {};

	data.proto = "matchWithReq";
	game.targetid =
		data.targetID = sessionid;
	data.user = game.userid;
	data.status = "";

	console.log(data);
	ws.send(JSON.stringify(data));

}

function send_play_matchWithRep(data, status) {
	data.proto = "matchWithRep";
	data.status = status;

	ws.send(JSON.stringify(data));
}

/*
 * 対戦の申し込み結果 {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK or NG"}
 */

function recv_play_matchWithReq(data) {

	console.log("recv_play_matchWithReq()");

	if (game.status == "login") {
		$("#show_dialog").dialog({
			modal : true,
			title : data.targetID + "と対戦",
			buttons : {
				"OK" : function() {
					$(this).dialog("close");
					send_play_matchWithRep(data, "OK");
				},
				"キャンセル" : function() {
					$(this).dialog("close");
					send_play_matchWithRep(data, "NG");
				}
			}
		});
	}
}

function recv_play_matchWithRep(data) {
	console.log("recv_play_matchWithRep");
	game.status = "login";
	console.log(" NG だった?");
	console.log(" status : " + data.status);

}

function utl_set_status(msg) {
	$("#turnstatus").text(msg);
}

function utl_turn_message() {
	if (game.piece == game.turn) {
		utl_set_status("あなたの番です");
	} else {
		utl_set_status("あいての番です");
	}
}

function recv_play_matchStart(data) {
	console.log("recv_play_matchStart");
	console.log("status : " + data.status);

	game.status = "start";
	game.turn = "O";
	//game.button.text("対戦中");
	if (data.status == "First") {
		game.piece = "O";
	} else {
		game.piece = "X";
	}
	game.number = 0;
	utl_turn_message();
}

/*
 * 対戦中
 * {"proto":"matching", "targetID":"targetID", "status":"盤データ"
 * 盤データは置かれた順番が分かるようにしてもらえると途中セーブが不要になる
 */
function send_play_matching() {
	console.log("recv_play_matchStart");
	var data = {};

	data.proto = "matching";
	data.targetID = game.targetid;
	data.user = game.userid;
	data.status = "";

	data.x = game.x;
	data.y = game.y;
	data.number = game.number;
	data.turn = game.turn;
	data.grid = game.grind;

	console.log(data);
	ws.send(JSON.stringify(data));
}

function recv_play_matching(data) {
	console.log("recv_play_matching");

	console.log("x       : " + data.x);
	console.log("y       : " + data.y);
	console.log("number  : " + data.number);
	console.log("turn    : " + data.turn);
	console.log("grind   : " + data.grind);


	// 版情報とメッセージを書き換える
	$("#" + data.grind).text(data.turn);

	game.turn = game.piece;

	utl_turn_message();
}

$(function() {
	var endpoint = 'ws://' + location.host + '/endpoint';
	var webSocket = null;

	$('#connectButton').click(
			function() {

				$("#messageList").empty();

				webSocket = new WebSocket(endpoint + '?'
						+ encodeURIComponent($('#roomName').val()));
				webSocket.onopen = function() {
					$('#roomName').prop('disabled', true);
					$('#connectButton').prop('disabled', true);
					$('#disconnectButton').prop('disabled', false);
				};
				webSocket.onclose = function() {
				};
				webSocket.onmessage = function(message) {
					$('#messageList').prepend($('<li>').text(message.data));
				};
				webSocket.onerror = function() {
					alert('エラーが発生しました。');
				};
			});

	$('#disconnectButton').click(function() {
		webSocket.close();
		webSocket = null;

		$('#roomName').prop('disabled', false);
		$('#connectButton').prop('disabled', false);
		$('#disconnectButton').prop('disabled', true);
	});

	$('#sendButton').click(
			function() {
				if (!webSocket) {
					alert('未接続です。');
					return;
				}
				webSocket.send(get_loginuser() + " : "
						+ $('#message').val());
			});

});


