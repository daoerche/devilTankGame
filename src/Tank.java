import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tank {
	public int speedX = 5, speedY =5; // 静态全局变量速度---------可以作为扩张来设置级别，速度快的话比较难
	public static int count = 0;
	public static final int width = 35, length = 35; // 坦克的全局大小，具有不可改变性
	private Direction direction = Direction.STOP; // 初始化状态为静止
	private Direction Kdirection = Direction.U; // 初始化方向为向上
	// 设置子弹类型
	public int bullesType = 1;
	// 设置状态持续时间
	public int statusTime = 0;
	/**
	 * 0 - > 没有状态
	 * 1 - > 子弹变成普通墙
	 * 2 - > 四发子弹
	 * 3 - > 蜗牛减速
	 */
	public int statusImg = 0;
	TankClient tc;

	private boolean good;
	private int x, y;
	private int oldX, oldY;
	private boolean live = true; // 初始化为活着
	private int life = 200; // 初始生命值

	private static Random r = new Random();
	private int step = r.nextInt(10)+5 ; // 产生一个随机数,随机模拟坦克的移动路径

	private boolean bL = false, bU = false, bR = false, bD = false;
	

	private static Toolkit tk = Toolkit.getDefaultToolkit();// 控制面板
	private static Image[] tankImags = null; // 存储全局静态
	static {
		tankImags = new Image[] {
				tk.getImage(BombTank.class.getResource("Images/tankD.gif")),
				tk.getImage(BombTank.class.getResource("Images/tankU.gif")),
				tk.getImage(BombTank.class.getResource("Images/tankL.gif")),
				tk.getImage(BombTank.class.getResource("Images/tankR.gif")), };

	}

	public static Image[] tankStatusImags = null; // 存储全局静态
	static {
		tankStatusImags = new Image[]{
				tk.getImage(BombTank.class.getResource("Images/statusWall.png")),
				tk.getImage(BombTank.class.getResource("Images/statusFourFire.png")),
				tk.getImage(BombTank.class.getResource("Images/statusReduceSpeed.png")),
				tk.getImage(BombTank.class.getResource("Images/statusBigBullets.png"))
		};
	}

	public Tank(int x, int y, boolean good) {// Tank的构造函数1
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
		if(!this.good)
			Tank.count++;
	}

	public Tank(int x, int y, boolean good, Direction dir, TankClient tc) {// Tank的构造函数2
		this(x, y, good);
		this.direction = dir;
		this.tc = tc;
		if(!this.good)
			Tank.count++;
	}

	public void setSpeed(int addSpeedX, int addSpeedY) {
		this.speedX = 5 + addSpeedX;
		this.speedY = 5 + addSpeedY;
	}

	public void draw(Graphics g) {
		if (!live) {
			if (!good) {
				tc.tanks.remove(this); // 删除无效的
			}
			return;
		}

		if (good)
			new DrawBloodbBar().draw(g); // 创造一个血包

		switch (Kdirection) {
							//根据方向选择坦克的图片
		case D:
			g.drawImage(tankImags[0], x, y, null);
			break;

		case U:
			g.drawImage(tankImags[1], x, y, null);
			break;
		case L:
			g.drawImage(tankImags[2], x, y, null);
			break;

		case R:
			g.drawImage(tankImags[3], x, y, null);
			break;

		}

		move();   //调用move函数
	}

	void move() {

		this.oldX = x;
		this.oldY = y;

		switch (direction) {  //选择移动方向
		case L:
			x -= speedX;
			break;
		case U:
			y -= speedY;
			break;
		case R:
			x += speedX;
			break;
		case D:
			y += speedY;
			break;
		case STOP:
			break;
		}

		if (this.direction != Direction.STOP) {
			this.Kdirection = this.direction;
		}

		if (x < 0)
			x = 0;
		if (y < 40)      //防止走出规定区域
			y = 40;
		if (x + Tank.width > TankClient.Fram_width)  //超过区域则恢复到边界
			x = TankClient.Fram_width - Tank.width;
		if (y + Tank.length > TankClient.Fram_length)
			y = TankClient.Fram_length - Tank.length;

		if (!good) {
			Direction[] directons = Direction.values();
			if (step == 0) {                  
				step = r.nextInt(12) + 3;  //产生随机路径
				int rn = r.nextInt(directons.length);
				direction = directons[rn];      //产生随机方向
			}
			step--;

			if (r.nextInt(40) > 38)//产生随机数，控制敌人开火
				this.fire(this.bullesType);
		}
	}

	private void changToOldDir() {  
		x = oldX;
		y = oldY;
	}

	public void keyPressed(KeyEvent e) {  //接受键盘事件
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_RIGHT: //监听向右键
				bR = true;
				break;

			case KeyEvent.VK_LEFT://监听向左键
				bL = true;
				break;

			case KeyEvent.VK_UP:  //监听向上键
				bU = true;
				break;

			case KeyEvent.VK_DOWN://监听向下键
				bD = true;
				break;
		}
		decideDirection();//调用函数确定移动方向
	}

	void decideDirection() {
		if (!bL && !bU && bR && !bD)  //向右移动
			direction = Direction.R;

		else if (bL && !bU && !bR && !bD)   //向左移
			direction = Direction.L;

		else if (!bL && bU && !bR && !bD)  //向上移动
			direction = Direction.U;

		else if (!bL && !bU && !bR && bD) //向下移动
			direction = Direction.D;

		else if (!bL && !bU && !bR && !bD)
			direction = Direction.STOP;  //没有按键，就保持不动
	}

	public void keyReleased(KeyEvent e) {  //键盘释放监听
		int key = e.getKeyCode();
		switch (key) {
		
		case KeyEvent.VK_F:
			fire(this.bullesType);
			break;
			
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		
		case KeyEvent.VK_UP:
			bU = false;
			break;
		
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		}
		decideDirection();  //释放键盘后确定移动方向
	}

	public void fire(int bullesType) {  //开火方法
		// 子弹为正常子弹
		if(bullesType == 1) {
			if (!live)
				return;
			int x = this.x + Tank.width / 2;
			int y = this.y + Tank.length / 2;
			Bullets m = new Bullets(x, y + 2, good, Kdirection, this.tc);  //没有给定方向时，向原来的方向发火
			m.x = this.x + Tank.width / 2 - m.width / 2;
			m.y = this.y + Tank.length / 2 - m.length / 2 + 2;
			m.setSpeed(tc.level, tc.level);
			tc.bullets.add(m);

		// 子弹为普通墙壁
		}else if(bullesType == 2) {
			if (!live)
				return;
			int x;  //开火位置
			int y;
			switch (this.Kdirection) { // 选择不同方向的子弹
				case L:
					x = this.x+40;  //开火位置
					y = this.y;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;

				case U:
					x = this.x;  //开火位置
					y = this.y+40;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;

				case R:
					x = this.x-40;  //开火位置
					y = this.y;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;

				case D:
					x = this.x;  //开火位置
					y = this.y-40;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;
			}

		// 子弹为四射弹
		}else if(bullesType == 3) {
			if (!live)
				return;
			int x = this.x + Tank.width / 2;
			int y = this.y + Tank.length / 2;

			Bullets m1 = new Bullets(x, y + 2, good, Direction.U, this.tc);  //没有给定方向时，向原来的方向发火
			m1.x = this.x + Tank.width / 2 - m1.width / 2;
			m1.y = this.y + Tank.length / 2 - m1.length / 2 + 2;
			m1.setSpeed(tc.level, tc.level);
			tc.bullets.add(m1);

			Bullets m2 = new Bullets(x, y + 2, good, Direction.D, this.tc);  //没有给定方向时，向原来的方向发火
			m2.x = this.x + Tank.width / 2 - m2.width / 2;
			m2.y = this.y + Tank.length / 2 - m2.length / 2 + 2;
			m2.setSpeed(tc.level, tc.level);
			tc.bullets.add(m2);

			Bullets m3 = new Bullets(x, y + 2, good, Direction.L, this.tc);  //没有给定方向时，向原来的方向发火
			m3.x = this.x + Tank.width / 2 - m3.width / 2;
			m3.y = this.y + Tank.length / 2 - m3.length / 2 + 2;
			m3.setSpeed(tc.level, tc.level);
			tc.bullets.add(m3);

			Bullets m4 = new Bullets(x, y + 2, good, Direction.R, this.tc);  //没有给定方向时，向原来的方向发火
			m4.x = this.x + Tank.width / 2 - m4.width / 2;
			m4.y = this.y + Tank.length / 2 - m4.length / 2 + 2;
			m4.setSpeed(tc.level, tc.level);
			tc.bullets.add(m4);

		// 子弹为超大导弹
		}else if(bullesType == 4) {
			if (!live)
				return;
			int x = this.x + Tank.width / 2;
			int y = this.y + Tank.length / 2;
			Bullets m = new Bullets(x, y + 2, good, Kdirection, this.tc);  //没有给定方向时，向原来的方向发火
			switch (Kdirection) {
				case D:
				case U:
					m.width = 100;
					m.length = 75;
					break;
				case L:
				case R:
					m.width = 75;
					m.length = 100;
					break;
			}
			m.x = this.x + Tank.width / 2 - m.width / 2;
			m.y = this.y + Tank.length / 2 - m.length / 2 + 2;
			m.setSpeed(tc.level, tc.level);
			m.bulletsType = 1;
			tc.bullets.add(m);
		}
	}


	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isGood() {
		return good;
	}

	public boolean collideWithWall(CommonWall w) {  //碰撞到普通墙时
		if (this.live && this.getRect().intersects(w.getRect())) {
			 this.changToOldDir();    //转换到原来的方向上去
			return true;
		}
		return false;
	}

	public boolean collideWithWall(MetalWall w) {  //撞到金属墙
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.changToOldDir();     
			return true;
		}
		return false;
	}

	public boolean collideRiver(River r) {    //撞到河流的时候
		if (this.live && this.getRect().intersects(r.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideHome(Home h) {   //撞到家的时候
		if (this.live && this.getRect().intersects(h.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideWithTanks(java.util.List<Tank> tanks) {//撞到坦克时
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (this != t) {
				if (this.live && t.isLive()
						&& this.getRect().intersects(t.getRect())) {
					this.changToOldDir();
					t.changToOldDir();
					return true;
				}
			}
		}
		return false;
	}


	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	private class DrawBloodbBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(tc.Fram_width-250, 58, width, 10);
			int w = width * life / 200;
			g.fillRect(tc.Fram_width-250, 58, w, 10);
			g.setColor(c);
		}
	}

	/**
	 * 吃各种状态方法
	 * @param b
	 * @return
	 */
	// 吃到血
	public boolean eat(GetBlood b) {
		if (this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
			if(this.life<=100)
			this.life = this.life+100;      //每吃一个，增加100生命点
			else
				this.life = 200;
			b.setLive(false);
			return true;
		}
		return false;
	}

	// 吃到普通墙
	public boolean eatStatusWall(StatusWall s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			this.bullesType = 2;
			// 设置状态持续时间
			this.statusTime = 20 * 20; // 持续时间20s
			this.statusImg = 1;
			s.setLive(false);
			return true;
		}
		return false;
	}

	// 吃到四发子弹
	public boolean eatStatusFourFire(StatusFourFire s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			this.bullesType = 3;
			// 设置状态持续时间
			this.statusTime = 20 * 20; // 持续时间20s
			this.statusImg = 2;
			s.setLive(false);
			return true;
		}
		return false;
	}

	// 吃到蜗牛减速
	public boolean eatStatusReduceSpeed(StatusReduceSpeed s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			for(int i = 0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				t.speedX = 1;
				t.speedY = 1;
			}
			// 设置状态持续时间
			this.statusTime = 20 * 20; // 持续时间20s
			this.statusImg = 3;
			s.setLive(false);
			return true;
		}
		return false;
	}

	// 吃到超大导弹
	public boolean eatStatusBigBullets(StatusBigBullets s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			this.bullesType = 4;
			// 设置状态持续时间
			this.statusTime = 20 * 20; // 持续时间20s
			this.statusImg = 4;
			s.setLive(false);
			return true;
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}