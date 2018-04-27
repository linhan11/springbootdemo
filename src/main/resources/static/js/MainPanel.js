mp = null;   // MainPanel オブジェクト

			//
			// MainPanel の開始
			//
function mp_start()
{
					// キャンバス情報
	var canvas = document.getElementById('canvas_e');   // キャンバス要素の取得
	var ctx    = canvas.getContext('2d');   // キャンバスからコンテキストを取得
					// MainPanel オブジェクト
	mp = new MainPanel(canvas, ctx);
					// StartPanel の表示
	st_start();
}
			//
			// MainPanel オブジェクト（プロパティ）
			//
function MainPanel(canvas, ctx)
{
	this.canvas = canvas;   // キャンバス要素
	this.ctx    = ctx;   // キャンバスのコンテキスト
	this.method = 0;   // ゲーム方法（0:対人間，1:対PC（先手），2:対PC（後手））
	return this;
}
			//
			// MainPanel オブジェクト（メソッド）
			//
MainPanel.prototype.finish = function()
{
					// キャンバスのクリア
	mp.ctx.clearRect(0, 0, mp.canvas.width, mp.canvas.height);
					// ボタンを非表示
	document.getElementById('method').style.display = "none";
	document.getElementById('g_method0').style.display = "none";
	document.getElementById('g_method1').style.display = "none";
	document.getElementById('g_method2').style.display = "none";
	document.getElementById('ta').style.display = "none";
	document.getElementById('first').style.display = "none";
	document.getElementById('finish').style.display = "none";
}
