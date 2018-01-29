import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;


public class TankClient extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int Fram_width = 1200; // 静态全局窗口大小
	public static final int Fram_length = 1000;
	public static final String IP_ADDR = "localhost";//服务器地址
	public static final int PORT = 3000;//服务器端口号
	public static int level = 0;
	public static boolean printable = true;
	public int count = 0;
	// 保存玩家名字
	public String name = "";
	// 接受排行榜信息
	public String rank = "";
	// 标志是否已经接受过数据
	private boolean getScore = false;
	private static Random r = new Random();

	Image screenImage = null;

	Tank homeTank = new Tank(Fram_width/2 - 100, Fram_length-34, true, Direction.STOP, this);// 实例化坦克
	GetBlood blood = new GetBlood(); // 实例化生命
	Home home = new Home(Fram_width/2-20, Fram_length-43, this);// 实例化home

	// 声明各种道具包
	StatusWall statusWall = new StatusWall();
	StatusFourFire statusFourFire = new StatusFourFire();
	StatusReduceSpeed statusReduceSpeed = new StatusReduceSpeed();
	StatusBigBullets statusBigBullets = new StatusBigBullets();

	List<River> theRiver = new ArrayList<River>();
	List<Tank> tanks = new ArrayList<Tank>();
	List<BombTank> bombTanks = new ArrayList<BombTank>();
	List<Bullets> bullets = new ArrayList<Bullets>();
	List<Tree> trees = new ArrayList<Tree>();
	List<CommonWall> homeWall = new ArrayList<CommonWall>(); // 实例化对象容器
	List<CommonWall> otherWall = new ArrayList<CommonWall>();
	List<MetalWall> metalWall = new ArrayList<MetalWall>();

	public void update(Graphics g) {

		screenImage = this.createImage(Fram_width, Fram_length);

		Graphics gps = screenImage.getGraphics();
		Color c = gps.getColor();
		gps.setColor(Color.GRAY);
		gps.fillRect(0, 0, Fram_width, Fram_length);
		gps.setColor(c);
		framPaint(gps);
		g.drawImage(screenImage, 0, 0, null);
	}

	public void framPaint(Graphics g) {

		for (int i = 0; i < theRiver.size(); i++) { // 画出河流
			River r = theRiver.get(i);
			r.draw(g);
			homeTank.collideRiver(r);
		}

		/**
		 * 绘制子弹，并解决子弹造成的一切效果
		 */
		for (int i = 0; i < bullets.size(); i++) { // 对每一个子弹
			Bullets m = bullets.get(i);
			m.hitTanks(tanks); // 每一个子弹打到坦克上
			m.hitTank(homeTank); // 每一个子弹打到自己家的坦克上时
			m.hitHome(); // 每一个子弹打到家里是

			for (int j = 0; j < metalWall.size(); j++) { // 每一个子弹打到金属墙上
				MetalWall mw = metalWall.get(j);
				m.hitWall(mw);
			}

			for (int j = 0; j < otherWall.size(); j++) {// 每一个子弹打到其他墙上
				CommonWall w = otherWall.get(j);
				m.hitWall(w);
			}

			for (int j = 0; j < homeWall.size(); j++) {// 每一个子弹打到家的墙上
				CommonWall cw = homeWall.get(j);
				m.hitWall(cw);
			}
			m.draw(g); // 画出效果图
		}

		/**
		 * 绘制用户坦克，并解决可能出现的情况
		 */
		homeTank.collideWithTanks(tanks);
		homeTank.collideHome(home);

		for (int i = 0; i < metalWall.size(); i++) {// 撞到金属墙
			MetalWall w = metalWall.get(i);
			homeTank.collideWithWall(w);
		}

		for (int i = 0; i < otherWall.size(); i++) {
			CommonWall cw = otherWall.get(i);
			homeTank.collideWithWall(cw);
		}

		for (int i = 0; i < homeWall.size(); i++) { // 家里的坦克撞到自己家
			CommonWall w = homeWall.get(i);
			homeTank.collideWithWall(w);
		}
		homeTank.draw(g);// 画出自己家的坦克


		/**
		 * 绘制电脑坦克，并解决可能出现的一切情况
		 */
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i); // 获得键值对的键

			for (int j = 0; j < homeWall.size(); j++) {
				CommonWall cw = homeWall.get(j);
				t.collideWithWall(cw); // 每一个坦克撞到家里的墙时
			}
			for (int j = 0; j < otherWall.size(); j++) { // 每一个坦克撞到家以外的墙
				CommonWall cw = otherWall.get(j);
				t.collideWithWall(cw);
			}
			for (int j = 0; j < metalWall.size(); j++) { // 每一个坦克撞到金属墙
				MetalWall mw = metalWall.get(j);
				t.collideWithWall(mw);
			}
			for (int j = 0; j < theRiver.size(); j++) {
				River r = theRiver.get(j); // 每一个坦克撞到河流时
				t.collideRiver(r);
			}

			t.collideWithTanks(tanks); // 撞到自己的人
			t.collideHome(home);

			t.draw(g);
		}

		/**
		 * 检测是否需要新增坦克
		 */
		if(tanks.size()<=0 && home.isLive()) {
			TankClient.level++;
			int localTanksCount = r.nextInt(9)+12;
			for (int i = 0; i < localTanksCount; i++) { // 初始化20辆坦克
				if (i < 9) // 设置坦克出现的位置
					tanks.add(new Tank(150 + 70 * i, 40, false, Direction.D, this));
				else if (i < 15)
					tanks.add(new Tank(800, 140 + 50 * (i - 6), false, Direction.D,this));
				else
					tanks.add(new Tank(10, 50 * (i - 12), false, Direction.D,this));
			}
			for(int i = 0; i<tanks.size(); i++) {
				Tank t = tanks.get(i);
				t.setSpeed(level, level);
			}
			homeTank.setSpeed(level, level);
		}

		/**
		 * 绘制各种道具包
		 */
		if(blood != null)
			blood.draw(g);

		if(statusWall != null)
			statusWall.draw(g);

		if(statusFourFire != null)
			statusFourFire.draw(g);

		if(statusReduceSpeed != null)
			statusReduceSpeed.draw(g);

		if(statusBigBullets != null)
			statusBigBullets.draw(g);


		/**
		 * 最后绘制其他场景
		 */
		for (int i = 0; i < trees.size(); i++) { // 画出trees
			Tree tr = trees.get(i);
			tr.draw(g);
		}

		for (int i = 0; i < bombTanks.size(); i++) { // 画出爆炸效果
			BombTank bt = bombTanks.get(i);
			bt.draw(g);
		}

		for (int i = 0; i < otherWall.size(); i++) { // 画出otherWall
			CommonWall cw = otherWall.get(i);
			cw.draw(g);
		}

		for (int i = 0; i < metalWall.size(); i++) { // 画出metalWall
			MetalWall mw = metalWall.get(i);
			mw.draw(g);
		}

		/**
		 * 检测各种状态
		 */
		// 检测是否吃到血包
		homeTank.eat(blood);
		// 检测是否迟到星星
		homeTank.eatStatusWall(statusWall);
		// 检测是否吃到战机
		homeTank.eatStatusFourFire(statusFourFire);
		// 检测是否吃到蜗牛
		homeTank.eatStatusReduceSpeed(statusReduceSpeed);
		// 检测是否吃到超大导弹
		homeTank.eatStatusBigBullets(statusBigBullets);

		// 如果道具状态时限未到，减少时间
		if(homeTank.statusTime>0) {
			homeTank.statusTime--;
		}

		// 如果时间已到
		if(homeTank.statusTime==0) {
			// 恢复状态
			homeTank.statusImg = 0;
			// 恢复子弹
			homeTank.bullesType = 1;
			// 恢复坦克速度
			for(int i = 0; i<tanks.size(); i++) {
				Tank t = tanks.get(i);
				t.speedX = 5;
				t.speedY = 5;
				t.setSpeed(level, level);
			}

			// 下次不再执行
			homeTank.statusTime = -1;
		}

		// 绘制状态图标和剩余时间
		if(homeTank.statusImg != 0 && homeTank.statusTime > 0) {

			g.drawImage(homeTank.tankStatusImags[homeTank.statusImg-1], 1100, 950, null);

			Color c3 = g.getColor();
			Font f3 = g.getFont();
			g.setColor(Color.orange); // 设置字体显示属性

			g.setFont(new Font("Consoles", Font.BOLD, 25));
			g.drawString("" + homeTank.statusTime/20, 1150, 975);

			g.setFont(f3);
			g.setColor(c3);
		}

		home.draw(g); // 画出home

		if(!home.isLive() && !this.getScore) {
			// 进行soket通信，获取分数排行
			Socket socket = null;
			try {
				//创建一个流套接字并将其连接到指定主机上的指定端口号
				socket = new Socket(IP_ADDR, PORT);

				//向服务器端发送数据
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				String score = this.name + " " + this.count;
				out.writeUTF(score);

				// 接受返回的排行榜
				char[] data = new char[1024*1024];
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
				int len = br.read(data);
				String rexml = String.valueOf(data, 0, len);

				// rexml 即为排行榜信息，格式为json
				//System.out.println(rexml);
				this.rank = rexml;

				out.close();
			} catch (Exception e) {
				System.out.println("客户端异常:" + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						socket = null;
						System.out.println("客户端 finally 异常:" + e.getMessage());
					}
				}
			}

			// 下次绘制不再通信
			this.getScore = true;
		}

		// 如果排行榜有值，渲染排行版
		if(!(this.rank == null || this.rank.isEmpty())) {
			String [] rankArr= this.rank.split(" ");

			for(int i=0; i<rankArr.length/3-1; i++) {
				String itemName = rankArr[0+i*3];
				String itemScore = rankArr[1+i*3];
				String itemRank = rankArr[2+i*3];

				Color c4 = g.getColor();
				g.setColor(Color.DARK_GRAY); // 设置字体显示属性

				Font f4 = g.getFont();
				g.setFont(new Font("Consoles", Font.BOLD, 40));
				g.drawString(itemName, 600, 280+i*80);
				g.setFont(new Font("Consoles", Font.BOLD, 40));
				g.drawString(itemScore, 900, 280+i*80);
				g.setFont(new Font("Consoles", Font.BOLD, 40));
				g.drawString(itemRank, 1000, 280+i*80);

				g.setFont(f4);
				g.setColor(c4);
			}

			Color c3 = g.getColor();
			g.setColor(Color.orange); // 设置字体显示属性

			Font f3 = g.getFont();
			g.setFont(new Font("Consoles", Font.BOLD, 40));
			g.drawString("Your rank: ", 300, 900);
			g.setFont(new Font("Consoles", Font.BOLD, 40));
			g.drawString(rankArr[rankArr.length-1-2], 600, 900);
			g.setFont(new Font("Consoles", Font.BOLD, 40));
			g.drawString(rankArr[rankArr.length-1-1], 900, 900);
			g.setFont(new Font("Consoles", Font.BOLD, 40));
			g.drawString(rankArr[rankArr.length-1], 1000, 900);

			g.setFont(f3);
			g.setColor(c3);
		}

		Color c = g.getColor();
		g.setColor(Color.DARK_GRAY); // 设置字体显示属性

		Font f1 = g.getFont();
		g.setFont(new Font("Consoles", Font.BOLD, 20));
		g.drawString("Name: ", 30, 70);
		g.setFont(new Font("Consoles", Font.BOLD, 20));
		g.drawString("" + this.name, 110, 70);
		g.setFont(new Font("Consoles", Font.BOLD, 20));
		g.drawString("Level: ", Fram_width-460, 70);
		g.setFont(new Font("Consoles", Font.BOLD, 20));
		g.drawString("" + TankClient.level, Fram_width-380, 70);
		g.setFont(new Font("Consoles", Font.BOLD, 20));
		g.drawString("Blood: ", Fram_width-330, 70);
		g.setFont(new Font("Consoles", Font.BOLD, 20));
		g.drawString("Tanks: ", Fram_width-180, 70);
		g.setFont(new Font("Consoles", Font.ITALIC, 20));
		g.drawString("" + this.count, Fram_width-100, 70);
		g.setFont(f1);
		g.setColor(c);
	}

	public TankClient(String name) {

		this.name = name;

		for (int i = 0; i < 12; i++) { // 家的格局
			if (i < 4)
				homeWall.add(new CommonWall(Fram_width/2-21-22-10, Fram_length - 21 * i, this));
			else if (i < 9)
				homeWall.add(new CommonWall((Fram_width/2-21-22-10) + 22 * (i - 4), Fram_length - 21*4, this));
			else
				homeWall.add(new CommonWall((Fram_width/2-21-22-10) + 22 * (8 - 4), Fram_length - 21*4 + (i - 8) * 21, this));
		}

		// 普通墙布局
		int OtherWall_X1 = r.nextInt(31)+0;
		int OtherWall_Y1 = r.nextInt(301)+400;
		int OtherWall_Width1 = r.nextInt(3)+1;
		int OtherWall_Height1 = r.nextInt(5)+6;
		for(int i=0; i<OtherWall_Width1; i++) {
			for(int j=0; j<OtherWall_Height1; j++) {
				otherWall.add(new CommonWall(OtherWall_X1+i*22 , OtherWall_Y1+j*21, this));
			}
		}
		int OtherWall_X2 = r.nextInt(51)+156;
		int OtherWall_Y2 = r.nextInt(101)+612;
		int OtherWall_Width2 = r.nextInt(2)+2;
		int OtherWall_Height2 = r.nextInt(2)+6;
		for(int i=0; i<OtherWall_Width2; i++) {
			for(int j=0; j<OtherWall_Height2; j++) {
				otherWall.add(new CommonWall(OtherWall_X2+i*22 , OtherWall_Y2+j*21, this));
			}
		}
		int OtherWall_X3 = r.nextInt(51)+273;
		int OtherWall_Y3 = r.nextInt(101)+620;
		int OtherWall_Width3 = r.nextInt(2)+6;
		int OtherWall_Height3 = r.nextInt(2)+2;
		for(int i=0; i<OtherWall_Width3; i++) {
			for(int j=0; j<OtherWall_Height3; j++) {
				otherWall.add(new CommonWall(OtherWall_X3+i*22 , OtherWall_Y3+j*21, this));
			}
		}
		int OtherWall_X4 = r.nextInt(51)+400;
		int OtherWall_Y4 = r.nextInt(201)+500;
		int OtherWall_Width4 = r.nextInt(2)+1;
		int OtherWall_Height4 = r.nextInt(2)+6;
		for(int i=0; i<OtherWall_Width4; i++) {
			for(int j=0; j<OtherWall_Height4; j++) {
				otherWall.add(new CommonWall(OtherWall_X4+i*22 , OtherWall_Y4+j*21, this));
			}
		}
		int OtherWall_X5 = r.nextInt(51)+1001;
		int OtherWall_Y5 = r.nextInt(101)+100;
		int OtherWall_Width5 = r.nextInt(2)+6;
		int OtherWall_Height5 = r.nextInt(1)+2;
		for(int i=0; i<OtherWall_Width5; i++) {
			for(int j=0; j<OtherWall_Height5; j++) {
				otherWall.add(new CommonWall(OtherWall_X5+i*22 , OtherWall_Y5+j*21, this));
			}
		}
		int OtherWall_X6 = r.nextInt(101)+1001;
		int OtherWall_Y6 = r.nextInt(101)+500;
		int OtherWall_Width6 = r.nextInt(2)+2;
		int OtherWall_Height6 = r.nextInt(3)+8;
		for(int i=0; i<OtherWall_Width6; i++) {
			for(int j=0; j<OtherWall_Height6; j++) {
				otherWall.add(new CommonWall(OtherWall_X6+i*22 , OtherWall_Y6+j*21, this));
			}
		}


		// 金属墙布局
		// one area
		int MetalWall_Y1 = r.nextInt(301)+200;
		int MetalWall_width1 = r.nextInt(3)+4;
		int MetalWall_thickness1 = r.nextInt(3)+1;
		for(int i=0; i<MetalWall_width1; i++) {
			for(int j=0; j<MetalWall_thickness1; j++) {
				metalWall.add(new MetalWall(155+i*36 , MetalWall_Y1+j*37, this));
			}
		}
		// two area
		int MetalWall_X2 = r.nextInt(201)+500;
		int MetalWall_Y2 = r.nextInt(201)+450;
		int MetalWall_length2 = r.nextInt(2)+4;
		int MetalWall_thickness2 = r.nextInt(3)+1;
		for(int i=0; i<MetalWall_thickness2; i++) {
			for(int j=0; j<MetalWall_length2; j++) {
				metalWall.add(new MetalWall(MetalWall_X2+i*36 , MetalWall_Y2+j*37, this));
			}
		}

		// 树的布局
		// one area
		int Tree_Y1 = r.nextInt(201)+100;
		int Tree_Width1 = r.nextInt(2)+1;
		int Tree_Height1 = r.nextInt(2)+1;
		for(int i=0; i<Tree_Width1; i++) {
			for(int j=0; j<Tree_Height1; j++) {
				trees.add(new Tree(0+i*36 , Tree_Y1+j*36, this));
			}
		}
		//two area
		int Tree_Y2 = r.nextInt(201)+100;
		int Tree_Width2 = r.nextInt(2)+5;
		int Tree_Height2 = r.nextInt(2)+1;
		for(int i=0; i<Tree_Width2; i++) {
			for(int j=0; j<Tree_Height2; j++) {
				trees.add(new Tree(372+i*36 , Tree_Y2+j*36, this));
			}
		}
		//three area
		int Tree_X3 = r.nextInt(56)+1001;
		int Tree_Y3 = r.nextInt(101)+800;
		int Tree_Width3 = r.nextInt(2)+3;
		int Tree_Height3 = r.nextInt(2)+1;
		for(int i=0; i<Tree_Width3; i++) {
			for(int j=0; j<Tree_Height3; j++) {
				trees.add(new Tree(Tree_X3+i*36 , Tree_Y3+j*36, this));
			}
		}

		// add river
		for(int i=0; i<2; i++) {
			int y = r.nextInt(692);
			int c = r.nextInt(2);
			if(i==0) {
				for(int j=0; j<c+1; j++)
					theRiver.add(new River(100, y+j*154, this));
			} else {
				for(int j=0; j<c+1; j++)
					theRiver.add(new River(945, y+j*154, this));
			}
		}


		for (int i = 0; i < 20; i++) { // 初始化20辆坦克
			if (i < 9) // 设置坦克出现的位置
				tanks.add(new Tank(150 + 70 * i, 40, false, Direction.D, this));
			else if (i < 15)
				tanks.add(new Tank(800, 140 + 50 * (i - 6), false, Direction.D,this));
			else
				tanks.add(new Tank(10, 50 * (i - 12), false, Direction.D,this));
		}

		this.setSize(Fram_width, Fram_length); // 设置界面大小
		this.setLocation(300, 20); // 设置界面出现的位置
		this.setTitle("鬼畜版坦克大战 ———— ( 开火：F键 )");

		this.addWindowListener(new WindowAdapter() { // 窗口监听关闭
			 public void windowClosing(WindowEvent e) {
				 System.exit(0);
			 }
		});
		this.setResizable(false);
		this.setBackground(Color.GRAY);
		this.setVisible(true);

		this.addKeyListener(new KeyMonitor());// 键盘监听
		new Thread(new PaintThread()).start(); // 线程启动
	}

	public static void main(String[] args) {

		JFrame f=new JFrame();
		f.setTitle("鬼畜版坦克大战 ———— ( 开火：F键 )");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);

//设置窗口的大小和位置
		f.setSize(1200,1000);
		f.setLocation(300,20);


		Container con=f.getContentPane();//生成一个容器
		con.setLayout(new GridLayout(6,1));

		JPanel pan5 = new JPanel();
		con.add(pan5);

		JPanel pan1=new JPanel();//生成一个新的版面
		JLabel title=new JLabel("坦克大战");
		title.setFont(new Font("黑体",Font.BOLD, 60));
		pan1.add(title);
		con.add(pan1);

		JPanel pan2=new JPanel();//生成一个新的版面
		JLabel name=new JLabel("You name: ");
		name.setFont(new Font("黑体",Font.BOLD, 40));
		pan2.add(name);
		TextField tf_name=new TextField(30);
		pan2.add(tf_name);
		con.add(pan2);
//用户名及其文本框放置在第二个版面上

		JPanel pan6 = new JPanel();
		JLabel info = new JLabel("");
		info.setFont(new Font("宋体",Font.BOLD, 20));
		pan6.add(info);
		con.add(pan6);

		JPanel pan4 = new JPanel();
		JButton b_log=new JButton("Start");
		b_log.setFont(new Font("黑体",Font.BOLD, 20));
		pan4.add(b_log);
		b_log.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 进行逻辑处理即可
				String name =  tf_name.getText();
				if(name == null || name.isEmpty()) {
					info.setText("Please input your name...");
					return;
				}else {
					f.dispose();
					new TankClient(name); // 实例化
				}
			}
		});
		con.add(pan4);
//登陆和退出这两个按钮放在第四个版面上

		JPanel pan7 = new JPanel();
		con.add(pan7);
	}

	private class PaintThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			while (printable) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) { // 监听键盘释放
			homeTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) { // 监听键盘按下
			homeTank.keyPressed(e);
		}

	}
}
