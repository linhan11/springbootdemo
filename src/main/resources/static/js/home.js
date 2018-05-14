var game = {}

/*
 * 判定 x x x
 *
 * y y y
 *
 * x x x
 */
function judge_xy(piece) {
	var x = 0;
	var y = 0;
	var ok;

	ok = piece;
	for (x = y = 0; x < 3; x++, y++) {
		if (game.map[y][x] != piece) {
			ok = "";
			break;
		}
	}
	if (ok != "") {
		return true;
	}

	ok = piece;
	for (x = 0, y = 2; x < 3; x++, y--) {
		if (game.map[y][x] != piece) {
			ok = "";
			break;
		}
	}	if (ok != "") {
		return true;
	}
	return false;
}

function judge_x(x, piece) {
	var y;

	for (y = 0; y < 3; y++) {
		if (game.map[y][x] != piece) {
			return false;
		}
	}
	return true;
}

function judge_y(y, piece) {
	var x;

	for (x = 0; x < 3; x++) {
		if (game.map[y][x] != piece) {
			return false;
		}
	}
	return true;
}

function grid_to_map() {
	var x = 0;
	var y = 0;
	var piece;

	for (var i = 1; i <= 9; i++) {
		piece = $("#grid_" + i).text();
		game.map[y][x] = piece;
		x++;
		if ((i % 3) == 0) {
			x = 0;
			y++;
		}
	}
}

function judge_map() {
	var x = 0;
	var y = 0;
	var piece;

	// grid を 2次元配列に落とす
	grid_to_map();

	for (y = 0; y < 3; y++) {
		piece = game.map[y][0];
		if (piece != "") {
			if (judge_y(y, piece)) {
				return piece;
			}
		}
	}
	for (x = 0; x < 3; x++) {
		piece = game.map[0][x];
		if (piece != "") {
			if (judge_x(x, piece)) {
				return piece;
			}
		}
	}

	piece = game.map[0][0];
	if (piece != "") {
		if (judge_xy(piece)) {
			return piece;
		}
	}
	piece = game.map[2][0];
	if (piece != "") {
		if (judge_xy(piece)) {
			return piece;
		}
	}

	// 引き分けか？
	for (x = 0; x < 3; x++) {
		for (y = 0; y < 3; y++) {
			if (game.map[y][x] == "") {
				return "";
			}
		}
	}

	return "-";
}

function judge_win_lose() {
	var piece;
	if ((piece = judge_map()) != "") {
		game.winpiece = piece;
		return true;
	}
	return false;
}

function html_set_piece(piece) {
	$("#" + piece.gridid).text(piece.turn);
	if (piece.turn == "O") {
		$("#" + piece.gridid).addClass("marupeke-o");
	} else {
		$("#" + piece.gridid).addClass("marupeke-x");
	}
}

/*
 * 駒を置いたときの処理
 */
function set_piece() {
	if (game.status != "start") {
		utl_set_message("開始状態ではありません");
		return;
	}
	if (game.piece != game.turn) {
		utl_set_message("あいてが試行中です");
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
	var piece = {};
	piece.turn = game.turn;
	piece.gridid = game.gridid;
	html_set_piece(piece);

	send_play_matching();

	if (judge_win_lose()) {
		console.log("win piece : " + game.winpiece);
		console.log("targetid : " + game.targetid);
		utl_win_lose_message();
		show_game_end_dialog();
		return;
	}

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

function utl_set_message(msg) {
	$("#turnstatus").text(msg);
}

function utl_turn_message() {
	console.log("utl_turn_message()");
	if (game.piece == game.turn) {
		game.message = "あなたの番です";
	} else {
		game.message = "あいての番です";
	}
	utl_set_message(game.message);
}

function utl_win_lose_message() {
	console.log("utl_win_lose_message()");
	console.log(" winpiece : " + game.winpiece);
	console.log(" piece    : " + game.piece);
	if (game.winpiece == "-") {
		game.message = "引き分けでした";
	} else if (game.piece == game.winpiece) {
		game.message = "あなたの勝ちです";
	} else {
		game.message = "あなたの負けです";
	}
	utl_set_message(game.message);
}

function init_game() {
	game.status = "";
	game.user = "";
	game.id = "";
	game.targetid = "";
	game.turn = "O";
	game.gridid = "";
	game.piece = "";
	game.map = [ [ "", "", "" ], [ "", "", "" ], [ "", "", "" ] ];
	game.winpiece = "";
	game.message = "";
}

function reset_game() {
	init_game();
	game.status = "login";
	game.user = get_loginuser();

	for (var i = 1; i <= 9; i++) {
		$("#grid_" + i).text("");
		$("#grid_" + i).removeClass("marupeke-o");
		$("#grid_" + i).removeClass("marupeke-x");
	}

	utl_set_message("");
}

/* ------------------------------------------------------------------------
 * メイン
 * ---------------------------------------------------------------------- */
$(document).ready(function() {

	init_game();

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
	game.user = user;

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
			console.log("JSON.parse (1): " + message.data);
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
			alert("(1)" + e);
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

	game.login_list = data.login_list;

	for (var i = 0; i < data.login_list.length; i++) {
		var button_caption;
		var button_style;
		var click;
		if (game.id == data.login_list[i].id) {
			button_caption = '-----';
			click = false;
		} else {
			button_caption = data.login_list[i].status;
			click = true;
		}
		if (button_caption == "-----") {
			button_style = "btn-default";
		} else if (button_caption == "対戦中") {
			button_style = "btn-danger";
		} else {
			button_style = "btn-success";
		}
		$("#UserTable")
				.append(
						'<tr><td>'
								+ data.login_list[i].user
								+ '</td><td>'
								+ data.login_list[i].login_on
								+ '</td><td><button id="'
								+ data.login_list[i].id
								+ '" class="game-play btn ' + button_style + '">'
								+ button_caption
								+ '</button></td></tr>');
		if (click) {
			var button = document.getElementById(data.login_list[i].id);
			button.addEventListener("click", function (event) {
				console.log("session id : " + this.id);
				send_play_matchWithReq(this.id);
			});
		}
	}
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
 * 対戦の申し込み
 *  {"proto":"matchWithReq","targetID":"targetSeesionID"}
 */
function send_play_matchWithReq(sessionid) {
	console.log("send_play_matchWithReq()");

	if (game.status != "login") {
		return;
	}

	var data = {};

	data.proto = "matchWithReq";
	game.targetid =
		data.targetID = sessionid;
	game.target_user = get_target_user(game.targetid);
	data.user = game.user;
	data.status = "";

	console.log(data);
	ws.send(JSON.stringify(data));

}

/*
 * 対戦申込みの返却
 *  {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK or NG"}
 */
function send_play_matchWithRep(data, status) {
	data.proto = "matchWithRep";
	data.status = status;

	game.targetid = data.targetID;
	game.target_user = get_target_user(game.targetid);


	ws.send(JSON.stringify(data));
}

function get_target_user(targetid) {
	var i;
	var c;
	var user;
	c = game.login_list.length;
	console.log("targetid : " + targetid);
	for (i = 0; i < c; i++) {
		if (game.login_list[i].id == targetid) {
			console.log("id : " + game.login_list[i].id);
			user = game.login_list[i].user;
			return user;
		}
	}
	return "";
}

function show_request_dialog(data) {
	console.log("show_request_dialog()");

	game.target_user = get_target_user(data.targetID);

	$("#show_dialog").html(game.target_user + "から対戦の申し込みが来ています");

	$("#show_dialog").dialog({
		modal : true,
		title : game.target_user + "と対戦",
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

function show_game_end_dialog() {
	console.log("show_game_end_dialog()");

	$("#show_dialog").html(game.message);
	$("#show_dialog").dialog({
		modal : true,
		title : game.target_user + "と対戦結果",
		buttons : {
			"OK" : function() {
				$(this).dialog("close");
				reset_game();
			}
		}
	});
}

/*
 * 対戦の申し込み結果の受信
 * 　ダイアログを表示し、許可 or 拒否
 * 　大戦中であれば　拒否 (Serverで実装）
 *  {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK or NG"}
 */
function recv_play_matchWithReq(data) {
	console.log("recv_play_matchWithReq()");

	if (game.status == "login") {

		show_request_dialog(data);
	}
}

/*
 * 対戦申込みの結果
 * 依頼した側に、対戦申込みの結果が来る（NG only）
 */
function recv_play_matchWithRep(data) {
	console.log("recv_play_matchWithRep()");
	game.status = "login";
	console.log(" NG だった?");
	console.log(" status : " + data.status);

}

/*
 * 対戦の開始
 *  駒が first か secound で決まる
 */
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
	console.log("send_play_matching");
	var json = {};

	json.proto = "matching";
	json.targetID = game.targetid;
	json.user = game.user;
	game.number++;
	var status = {};
	status.gridid = game.gridid;
	status.number = game.number;
	status.turn = game.turn;
	json.status = JSON.stringify(JSON.stringify(status));

	console.log(json);
	ws.send(JSON.stringify(json));
}

/*
 * 対戦中　相手から受信
 *  ・相手から受信した、駒の位置を盤に反映し、
 *  ・勝敗判定を行う
 *  ・勝ち負けが決まったら、対戦終了を通知する
 */
function recv_play_matching(data) {
	console.log("recv_play_matching");

	var wsRes;
	try {
		console.log("JSON.parse : " + data.status);
		wsRes = JSON.parse(data.status);
	} catch (e) {
		alert("(2) " + e);
	}
	console.log("gridid  : " + wsRes.gridid);
	console.log("number  : " + wsRes.number);
	console.log("turn    : " + wsRes.turn);

	// 版情報とメッセージを書き換える
	var piece = {};
	piece.turn = wsRes.turn;
	piece.gridid = wsRes.gridid;
	html_set_piece(piece);

	if (judge_win_lose()) {
		console.log("win piece : " + game.winpiece);
		send_play_matchEnd(data);
		return;
	}

	game.number = Number(wsRes.number);
	game.number++;
	game.turn = game.piece;

	utl_turn_message();
}

/*
 * 対戦終了
 * {"proto":"matchEnd", "targetID":"targetID","status":"盤データ","result":"WIN/LOSE/SUSPEND/DRAW"}
 *
 */
function send_play_matchEnd(data) {
	console.log("send_play_matchEnd()");
	console.log("piece : " + game.winpiece);

	var json = {};

	json.proto = "matchEnd";
	json.targetID = game.targetid;
	json.user = game.user;
	switch (game.winpiece) {
	case "O":
		json.result = "WIN";
		break;
	case "X":
		json.result = "LOSE";
		break;
	case "-":
		json.result = "DRAW";
		break;
	default:
		alert('エラーが発生しました。[' + game.winpiece + ']');
		break;
	}
	console.log("json.result : " + json.result);
	game.number++;
	var status = {};
	status.gridid = game.gridid;
	status.number = game.number;
	status.turn = game.turn;
	json.status = JSON.stringify(JSON.stringify(status));

	console.log(json);
	ws.send(JSON.stringify(json));

	utl_win_lose_message();
	show_game_end_dialog();
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


