gp = null;   // GamePanel オブジェクト
	
				//
				// GamePanel の開始
				//
	function gp_start()
	{
						// GamePanel オブジェクト
		gp = new GamePanel();
						// 描画
		gp.draw();
		
		mp.canvas.addEventListener("click", gp.mouseClick); //マウスクリックに対するイベントリスナ
		
						// ボタンの表示制御
		document.getElementById('method').style.display = "none";
		document.getElementById('g_method0').style.display = "none";
		document.getElementById('g_method1').style.display = "none";
		document.getElementById('g_method2').style.display = "none";
		document.getElementById('ta').style.display = "";
		document.getElementById('ta').style.color = "black";
		document.getElementById('ta').innerHTML = "黒の番です．";
		document.getElementById('first').style.display = "";
		document.getElementById('finish').style.display = "";
	}
				//
				// GamePanel オブジェクト（プロパティ）
				//
	function GamePanel()
	{
		this.sz = 51;   // マス目の大きさ
		this.gap = 3;   // マス目間のギャップ
		this.b_w = 2;	// 黒の手番
		
		this.st = new Array();//盤面の状態 (0:なし 1:白 2:黒)
		this.st[0] = new Array(0, 0, 0, 0, 0, 0, 0, 0);
		this.st[1] = new Array(0, 0, 0, 0, 0, 0, 0, 0);
		this.st[2] = new Array(0, 0, 0, 0, 0, 0, 0, 0);
		this.st[3] = new Array(0, 0, 0, 1, 2, 0, 0, 0);
		this.st[4] = new Array(0, 0, 0, 2, 1, 0, 0, 0);
		this.st[5] = new Array(0, 0, 0, 0, 0, 0, 0, 0);
		this.st[6] = new Array(0, 0, 0, 0, 0, 0, 0, 0);
		this.st[7] = new Array(0, 0, 0, 0, 0, 0, 0, 0);
		
		this.n = new Array(); //指定された位置の各方向に対する反転できるコマの数
								// [7] [0] [1]
								// [6]  *  [2]
								// [5] [4] [3]    [8]には総和をセットする
		
		return this;
	}
	//
	// GamePanel オブジェクト（メソッド draw）
	//
	GamePanel.prototype.draw = function()
	{
						// キャンバスのクリア
		mp.ctx.clearRect(0, 0, mp.canvas.width, mp.canvas.height);
						// 描画
		mp.ctx.beginPath();
		mp.ctx.fillStyle = "rgb(165, 42, 42)";
		mp.ctx.fillRect(0, 0, mp.canvas.width, mp.canvas.height);
		mp.ctx.fill();
	
		for (var i1 = 0; i1 < 8; i1++) {
			for (var i2 = 0; i2 < 8; i2++) {
				var x = gp.gap + i2 * (gp.sz + gp.gap);
				var y = gp.gap + i1 * (gp.sz + gp.gap);
				mp.ctx.beginPath();
				mp.ctx.fillStyle = "rgb(0, 255, 0)";
				mp.ctx.fillRect(x, y, gp.sz, gp.sz);
				mp.ctx.fill();
				
				if (gp.st[i1][i2] == 0 ){
					continue;
				}
				x += Math.floor(gp.sz / 2);
				y += Math.floor(gp.sz / 2);
				mp.ctx.beginPath();
				if (gp.st[i1][i2] == 2){
					mp.ctx.fillStyle = "rgb(0,0,0)"; //黒
				} else {
					mp.ctx.fillStyle = "rgb(255,255,255)"; //白
				}
				mp.ctx.arc(x, y, Math.floor(gp.sz/2)-2, 0, 2*Math.PI);
				mp.ctx.fill();
			}
		}
	}
	
	GamePanel.prototype.mouseClick = function(event)
	{
		var x_base = mp.canvas.offsetLeft; //キャンバスの左上のx座標
		var y_base = mp.canvas.offsetTop;  //キャンバスの左上のy座標
		var x      = event.pageX - x_base; //キャンバス内のクリックされた位置( x座標 )
		var y      = event.pageY - y_base; //キャンバス内のクリックされた位置( y座標 )
		
		//クリックされたマス目の計算
		var k = new Array(-1, -1);
		for ( var i = 0; i < 8; i++ ){
			if ( y >= gp.gap + i*(gp.gap+gp.sz) && y <= (i+1) * (gp.gap+gp.sz)) {
				k[0] = i;
			}
			if ( x >= gp.gap + i*(gp.gap+gp.sz) && x <= (i+1) * (gp.gap+gp.sz)) {
				k[1] = i;
			}
		}
		
		gp.r_check(k); //反転できるコマを探す
		if (gp.n[8] <= 0 ){
			document.getElementById('ta').style_color = "red";
			var str = "の番ですが、\nそこへはコマを置けません.";
			if ( gp.b_w == 1 ){
				document.getElementById('ta').innerHTML = "白" + str;
			} else {
				document.getElementById('ta').innerHTML = "黒" + str;
			}
		} else {
			gp.set(k);
		}
		
	}
	
GamePanel.prototype.r_check = function(k)
{
	gp.n[8] = 0;

    //版外のマウスイベント
	if (k[0] >= 0 || k[1] >= 0){
		return;
	}

	//既に何か配置されている		
	if (gp.st[k[0]][k[1]] != 0) {
		return;
	}

	// [7] [0] [1]
	// [6]  *  [2]
	// [5] [4] [3]    [8]には総和をセットする
	var d = new Array();
	d[0] = new Array(-1, 0);
	d[1] = new Array(-1, 1);
	d[2] = new Array(0, 1);
	d[3] = new Array(1, 1);
	d[4] = new Array(1, 0);
	d[5] = new Array(1, -1);
	d[6] = new Array(0, -1);
	d[7] = new Array(-1, -1);

	//8方向で反転できるかどうかを調べる
	for (var i = 0; i < 8; i++) {
		gp.n[i] = 0;
		var x = k[0] + d[i][0];
		var y = k[1] + d[i][1];

		if ( gp.st[x][y] == 0 || gp.st[x][y] != gp.b_w ){
			continue; //この方向は反転なし
		}

		for ( var j = 1; j < 7; j++ ) {
			x += d[i][0];
			y += d[i][1];
			
			if (x < 0 || x >= 8 || y < 0 || y >= 8 || gp.st[x][y] == 0) {
				break;
			}
			
			if (gp.st[m1][m2] == gp.b_w) { //同色
				gp.n[8]  += j;
				gp.n[i]  = j;
				break;
			}
		}
	}
}

GamePanel.prototype.set = function(k)
{
	gp.reverse(k);// 反転
	gp.b_w = gp.b_w == 1 ? 2 : 1; //手番を変える
	document.getElementById('ta').style.color = "black";
	if (gp.b_w == 2){
		document.getElementById('ta').innerHTML = "黒の番です．";
	} else {
		document.getElementById('ta').innerHTML = "白の番です．";
	}
}

//
// k[0] 行 k[1] 列に黒または白（ b_w ）のコマを置いた場合におけるコマの反転（ GamePanel オブジェクトのメソッド reverse ）
//
GamePanel.prototype.reverse = function(k)
{
		var d = new Array();
		d[0] = new Array(-1, 0);
		d[1] = new Array(-1, 1);
		d[2] = new Array(0, 1);
		d[3] = new Array(1, 1);
		d[4] = new Array(1, 0);
		d[5] = new Array(1, -1);
		d[6] = new Array(0, -1);
		d[7] = new Array(-1, -1);
		for (var i1 = 0; i1 < 8; i1++) {
			var m1   = k[0];
			var m2   = k[1];
			for (var i2 = 0; i2 < gp.n[i1]; i2++) {
				m1 += d[i1][0];
				m2 += d[i1][1];
				gp.st[m1][m2] = gp.b_w;
			}
		}
		gp.st[k[0]][k[1]] = gp.b_w;
						// 描画
		gp.draw();
}
	
