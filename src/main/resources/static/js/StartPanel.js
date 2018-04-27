			//
			// StartPanel の開始
			//
function st_start()
{
	mp.level = 1;   // ゲームレベルの設定
					// キャンバスのクリア
	mp.ctx.clearRect(0, 0, mp.canvas.width, mp.canvas.height);
					// ゲームタイトルの表示
	mp.ctx.font = "40px 'ＭＳ ゴシック'";
	mp.ctx.textBaseline = "middle";
	mp.ctx.textAlign = "center";
	mp.ctx.fillStyle = "rgb(0, 0, 0)";
	mp.ctx.fillText("オセロ", mp.canvas.width/2, mp.canvas.height/2);
					// ボタンの表示制御
	document.getElementById('method').style.display = "";
	document.getElementById('g_method0').style.display = "";
	document.getElementById('g_method1').style.display = "";
	document.getElementById('g_method2').style.display = "";
	document.getElementById('ta').style.display = "none";
	document.getElementById('first').style.display = "none";
	document.getElementById('finish').style.display = "none";
}
