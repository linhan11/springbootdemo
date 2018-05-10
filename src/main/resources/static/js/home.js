var game = {}


function send_message() {
	console.log(game);
}

/*
 * 配列へ変換
 */
function convert_map() {
	var tmp;
	var i;
	var x = 0;
	var y = 0;
	for (i = 1; i <= 9; i++) {
		tmp = $("#grid_" + i).text();
		game.map[y][x] = tmp;
		x++;
		if (i % 3 == 0) {
			y++;
			x = 0;
		}
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
		console.log("status   : " + game.status);
		return;
	}
	if (game.piece != game.turn) {
		$("#turnstatus").text(game.turn)
	} else {

	}

	var gridid = game.gridid;
	var tmp = $("#" + gridid).text();
	if (tmp != "") {
		console.log("div val : [" + tmp + "]");
		return;
	}

	/*
	 * 駒を置けた
	 * Serverへmessageを送る
	 */
	$("#" + gridid).text(game.turn);

	convert_map(game);

	send_message();

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
								+ '" class="game-play btn btn-danger">対戦</button></td></tr>')
		var button = document.getElementById(data.login_list[i].id);
		button.addEventListener("click", myfunc);
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

/*
 * 対戦の申し込み結果 {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK or NG"}
 */

function recv_play_matchWithReq(data) {

	console.log("recv_play_matchWithReq()");

	var data = {};

	data.targetID = game.id;
	data.proto = "matchWithRep";
	if (game.status == "login") {
		$("#show_dialog").dialog({
			modal : true,
			title : data.targetID + "と対戦",
			buttons : {
				"OK" : function() {
					$(this).dialog("close");
					data.status = "OK";
					game.status = "start";
				},
				"キャンセル" : function() {
					$(this).dialog("close");
					data.status = "NG";
				}
			}
		});

	} else {
		data.status = "NG";
	}
	data.user = game.userid;

	console.log(data);

	ws.send(JSON.stringify(data));
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


