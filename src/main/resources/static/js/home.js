var game = {}

game.id = "";
game.turn = "O";
game.gridid = "";
game.map = [ [ "", "", "" ], [ "", "", "" ], [ "", "", "" ] ];

function send_message() {
	console.log(game);
}

/*
 * 配列へ変換
 */
function convert_map(game) {
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
 * 判定
 * x x x
 *
 * y
 * y
 * y
 *
 * x
 *   x
 *     x
 */
function check_map(game) {
}

function set_piece(game) {
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

	$("#turnstatus").text(game.turn)
}

/*
 * 要素からユーザIDを取得
 */
function get_loginuser() {
	return $('#loginuser').text();
}

$(document).ready(function() {
	for (var i = 1; i <= 9; i++) {
		$("#grid_" + i).click(function() {
			console.log("name   : " + $("#name").val());
			console.log("div id : " + this.id);
			game.id = $("#name").val();
			game.gridid = this.id;

			set_piece(game);
		})
	}


	// ゲーム用websocket
	var user = get_loginuser();
	var uri = "ws://" + location.host + "/websocket/game" + '?' + user;

	ws = new WebSocket(uri);
	ws.onopen = function() {
		var loginData = new Object();
		loginData.proto = "login";
		loginData.user = user;
		ws.send(JSON.stringify(loginData));
	};
	ws.onmessage = function(message) {
		try {
			wsRes = JSON.parse(message.data);
			console.log("onmessage()");
			console.log("proto : " + wsRes.proto);

			if (wsRes.proto == "login_list") {
				//alert(wsRes);
				makeUserTable(wsRes);
			} else if (wsRes.proto == "matchWithReq") {
				// 対戦の申し込み
				console.log(wsRes);
				recv_play_approach(wsRes);
			} else if (wsRes.proto == "matchWithRep") {
				// 対戦の申し込み結果
				console.log(wsRes);
			} else {
				alert(wsRes.proto);
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

/*
 * // 対戦の申し込み {"proto":"matchWithReq","targetID":"targetSeesionID"}
 */
function send_play_approach(sessionid) {
	console.log("send_play_approach()");
	var data = {};
	data.proto = "matchWithReq";
	data.targetID = sessionid;
	console.log(data);
	ws.send(JSON.stringify(data));

}

/*
 * 対戦の申し込み結果 {"proto":"matchWithRep","targetID":"targetSeesionID","status":"OK or NG"}
 */
function recv_play_approach(data) {
	$('#turnstatus').text(data.targetID);
}


function clearUsertable(name) {
	$("#userlist").empty();
}

/*
 * 対戦ユーザ一覧
 *
 *  一覧を作成し、対戦ボタンにセッションIDを保持
 */
function makeUserTable(data) {
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
	}
	$("#UserTable").on("click", ".game-play", function(event) {
		console.log("session id : " + this.id);
		var target = $(event.target);
		target.text("依頼");
		send_play_approach(this.id);
	});
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


