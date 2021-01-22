package kadai;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class ProgE1Graph extends JFrame {

	Graphics graph;
	Graphics2D g2d; // グラフィックに必要なクラス変数
	JMenuItem[] menu2Item;
	JRadioButton[] rbtnW; // ペンの太さ選択用ラジオボタン
	JRadioButton[] rbtnC; // ペンの色選択用ラジオボタン
	String[] menu2ItemName = new String[] { "自由曲線", "直線", "四角形" }; // メニューバー「描画の種類」の中身
	String[] penWidth = new String[] { "1pt", "2pt", "3pt", "4pt" }; // ラジオボタンの表示用文字
	String[] penColor = new String[] { "Black", "Pink", "Yellow", "Cyan" };// ラジオボタンの表示用文字
	Point pt = null;
	JTextField txt1; // 現在の状態を表示するためのテキスト領域
	JButton imageBtn; //
	boolean isFirstClick = true; // 最初のクリックかどうかを保持（直線の描画で必要）
	Point mousePosition; // マウスが押された場所を保持する
	int mode = 1; // 0: FreeHand, 1: line, 2: rectangle

	public ProgE1Graph(String title) {
		super(title);
		// # フレームの設定
		setLayout(null);
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// #メニューバーとメニューアイテムの設定
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu("ファイル");
		JMenu menu2 = new JMenu("描画の種類");
		menubar.add(menu1);
		menubar.add(menu2);

		JMenuItem menu1Item1 = new JMenuItem("読み込み"); // 機能は未実装
		JMenuItem menu1Item2 = new JMenuItem("保存"); // 機能は未実装
		menu1.add(menu1Item1);
		menu1.add(menu1Item2);

		menu2Item = new JMenuItem[menu2ItemName.length]; // メニューアイテムを配列変数で利用する
		for (int i = 0; i < menu2ItemName.length; i++) {
			menu2Item[i] = new JMenuItem(menu2ItemName[i]);
			menu2.add(menu2Item[i]);
			DrawMethodChangedLstn choose = new DrawMethodChangedLstn();
			menu2Item[i].addActionListener(choose);
		}
		setJMenuBar(menubar);

		// #ラジオボタンでペン太さコントロール
		int btnX = 20, btnY = 10, btnW = 50, btnH = 20; // ボタン配置用パラメータ
		PenWRBtnActionLstn pwLstn = new PenWRBtnActionLstn(); // ペン幅変更用ボタンアクションリスナー

		rbtnW = new JRadioButton[penWidth.length]; // 配列変数を確保
		for (int i = 0; i < rbtnW.length; i++) {
			rbtnW[i] = new JRadioButton(penWidth[i]); // 配列の要素毎にインスタンスを生成
			rbtnW[i].addActionListener(pwLstn); // ボタンアクションリスナーを登録
			rbtnW[i].setBounds(btnX, btnY, btnW, btnH); // フレーム内での配置
			btnX += 70; // 次の部品を右側にずらす
			add(rbtnW[i]); // ラジオボタンの登録
		}

		ButtonGroup bgrpPW = new ButtonGroup(); // ラジオボタンをグループ化
		for (int i = 0; i < rbtnW.length; i++)
			bgrpPW.add(rbtnW[i]);

		// #ラジオボタンでColorコントロール
		btnX = 20;
		btnY = 40;
		btnW = 70;
		btnH = 20; // ボタン配置用パラメータ
		PenColorRBtnActionLstn bcal = new PenColorRBtnActionLstn();

		rbtnC = new JRadioButton[penColor.length]; // 配列変数を確保
		for (int i = 0; i < rbtnC.length; i++) {
			rbtnC[i] = new JRadioButton(penColor[i]);
			rbtnC[i].addActionListener(bcal);
			rbtnC[i].setBounds(btnX, btnY, btnW, btnH);
			btnX += 70;
			add(rbtnC[i]);
		}

		ButtonGroup bgrpC = new ButtonGroup(); // ラジオボタンをグループ化
		for (int i = 0; i < rbtnC.length; i++)
			bgrpC.add(rbtnC[i]);

		MouseLstn mouLstn = new MouseLstn(); // マウスのボタン押しに反応するマウスリスナー
		this.addMouseListener(mouLstn); // マウスリスナーの登録。thisはこのフレームを指し示す
		MouseMotnLstn mmLstn = new MouseMotnLstn(); // マウスの動きに反応するマウスモーションリスナー
		this.addMouseMotionListener(mmLstn); // マウスモーションリスナーの登録

		txt1 = new JTextField("情報表示領域");
		txt1.setBounds(400, 10, 300, 20);
		add(txt1);

		// #ボタン（クリックで画像を表示用）
		// ボタンの設定
		// 画像読み込みアクションリスナー

		// アクションリスナーの登録

		setVisible(true); // frameの表示を行う

		this.graph = this.getGraphics(); // setVisible(true)の前に行うと，grapghにnullが入る
		this.g2d = (Graphics2D) graph;
		mousePosition = new Point(); // マウスの位置を格納するPointのインスタンス生成
	}

	public static void main(String[] args) {
		ProgE1Graph frame = new ProgE1Graph("Draw Tool by 4717113　工藤航世");
	}

	public class MouseLstn implements MouseListener {
		Point p0, p;
		int x,xx,y,yy;

		@Override
		public void mouseClicked(MouseEvent e) { // 同一位置でのPressed & Released
			String mc = ""; // クリックの順番表示用
			p = e.getPoint(); // マウスの位置を取得

			if (mode > 0) { // フリーハンドではない
				if (isFirstClick) {
					p0 = new Point(p); // 現在のマウス位置を保存
					mc = "1st ";
					isFirstClick = false;
				} else {
					mc = "2nd ";
					if (mode == 1) {
						graph.drawLine(p0.x, p0.y, p.x, p.y); // 直線を引く
						// System.out.println("Line drawn");
					}
					if (mode == 2) { // 四角形は左上を始点として幅と高さ
						Rectangle rect = new Rectangle();
						x  = Math.max(p0.x,p.x);
						xx = Math.min(p0.x,p.x);
						y  = Math.max(p0.y,p.y);
						yy = Math.min(p0.y,p.y);
						rect.setRect(xx, yy,(x -xx), (y - yy));
						((Graphics2D) graph).draw(rect);
						// 四角形を描画するための処理
					}
					isFirstClick = true;
				}
			}
			// 確認用
			txt1.setText(mc + "mouseClicked at (" + p.x + ", " + p.y + ")");
			System.out.println(mc + "mouseClicked!"); // コンソールへ出力（確認用）
		}

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.println("mouse Pressed!" + e.getX() + " " + e.getY());
			mousePosition.setLocation(e.getPoint());
			pt = e.getPoint();
			if (mode == 0) {
				graph.drawLine(pt.x, pt.y, pt.x, pt.y);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// System.out.println("mouse Released!"); // コンソールへ出力（確認用）
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

	}

	public class MouseMotnLstn implements MouseMotionListener {
		public void mouseMoved(MouseEvent e) {
			// System.out.println("mouse Moved"); // コンソールへ出力（確認用）
		}

		public void mouseDragged(MouseEvent e) {

			Point aa = pt;
			pt = e.getPoint();

			//System.out.println("mouse Dragged"); // コンソールへ出力（確認用）
			if (mode == 0) {
				graph.drawLine(aa.x, aa.y, pt.x, pt.y);

				// System.out.println(" "+ pt.x + " " + pt.y);
			}
		}
	}

	public class PenWRBtnActionLstn implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// 選択されているラジオボタンのテキストから数字を抽出し，ラインの幅にセットする
			JRadioButton btn = (JRadioButton) e.getSource();
			BasicStroke stroke;
			float width = Float.parseFloat(btn.getText().substring(0, 1)); // 最初の1文字をfloatにする
			stroke = new BasicStroke(width); // Lineの幅をwidthにする
			g2d.setStroke(stroke);
		}
	}

	public class PenColorRBtnActionLstn implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// 選択されているラジオボタンからラインの色をセットする
			JRadioButton btn = (JRadioButton) e.getSource();
			if (btn == rbtnC[0]) {
				g2d.setColor(Color.BLACK);
				return;
			}
			if (btn == rbtnC[1]) {
				g2d.setColor(Color.PINK);
				return;
			}
			if (btn == rbtnC[2]) {
				g2d.setColor(Color.YELLOW);
				return;
			}
			if (btn == rbtnC[3]) {
				g2d.setColor(Color.CYAN);
				return;
			}
		}
	}

	public class DrawMethodChangedLstn implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JMenuItem me = (JMenuItem) e.getSource();
			if (me == menu2Item[0]) {
				mode = 0;
				txt1.setText("自由曲線モード");
				return;
			}
			if (me == menu2Item[1]) {
				mode = 1;
				txt1.setText("直線モード");
				return;
			}
			if (me == menu2Item[2]) {
				mode = 2;
				txt1.setText("四角形モード");
				return;
			}

		}

	}

}


