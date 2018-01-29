import java.awt.*;
import java.util.List;

public class Bullets {
	public int speedX = 9;
	public int speedY = 9; // 子弹的全局静态速度

	public int width = 10;
	public int length = 10;

	public int x, y;
	Direction diretion;

	public int bulletsType = 0;
	private boolean good;
	private boolean live = true;

	private TankClient tc;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] bulletImages = null;

	static {
		bulletImages = new Image[] { // 不同方向的子弹
				// 小子弹
				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletL.gif")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletU.gif")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletR.gif")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletD.gif")),

				// 超大子弹
				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bullets2L.png")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bullets2U.png")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bullets2R.png")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bullets2D.png")),
		};

	}

	public Bullets(int x, int y, Direction dir) { // 构造函数1，传递位置和方向
		this.x = x;
		this.y = y;
		this.diretion = dir;
	}

	// 构造函数2，接受另外两个参数
	public Bullets(int x, int y, boolean good, Direction dir, TankClient tc) {
		this(x, y, dir);
		this.good = good;
		this.tc = tc;
	}

	public void setSpeed(int addSpeedX, int addSpeedY) {
		this.speedX = 9 + addSpeedX;
		this.speedY = 9 + addSpeedY;
	}

	private void move() {

		switch (diretion) {
		case L:
			x -= speedX; // 子弹不断向左进攻
			break;

		case U:
			y -= speedY;
			break;

		case R:
			x += speedX; // 字段不断向右
			break;

		case D:
			y += speedY;
			break;

		case STOP:
			break;
		}

		if (x < 0 || y < 0 || x > TankClient.Fram_width || y > TankClient.Fram_length) {
			live = false;
		}
	}

	public void draw(Graphics g) {
		if (!live) {
			tc.bullets.remove(this);
			return;
		}

		switch (diretion) { // 选择不同方向的子弹
		case L:
			g.drawImage(bulletImages[0+4*this.bulletsType], x, y, null);
			break;

		case U:
			g.drawImage(bulletImages[1+4*this.bulletsType], x, y, null);
			break;

		case R:
			g.drawImage(bulletImages[2+4*this.bulletsType], x, y, null);
			break;

		case D:
			g.drawImage(bulletImages[3+4*this.bulletsType], x, y, null);
			break;

		}

		move(); // 调用子弹move()函数
	}

	public boolean isLive() { // 判读是否还活着
		return live;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

	public boolean hitTanks(List<Tank> tanks) {// 当子弹打到坦克时
		for (int i = 0; i < tanks.size(); i++) {
			if (hitTank(tanks.get(i))) { // 对每一个坦克，调用hitTank
				return true;
			}
		}
		return false;
	}

	public boolean hitTank(Tank t) { // 当子弹打到坦克上

		if (this.live && this.getRect().intersects(t.getRect()) && t.isLive()&& this.good != t.isGood()) {

			BombTank e = new BombTank(t.getX(), t.getY(), tc);
			tc.bombTanks.add(e);
			if (t.isGood()) {
				t.setLife(t.getLife() - 50); // 受一粒子弹寿命减少50，接受4枪就死,总生命值200
				if (t.getLife() <= 0){
					t.setLive(false); // 当寿命为0时，设置寿命为死亡状态
					this.tc.home.setLive(false);
				}
			} else {
				t.setLive(false);
				Tank.count--;
				tc.count++;
			}

			this.live = false;

			return true; // 射击成功，返回true
		}
		return false; // 否则返回false
	}

	public boolean hitWall(CommonWall w) { // 子弹打到CommonWall上
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			this.tc.otherWall.remove(w); // 子弹打到CommonWall墙上时则移除此击中墙
			this.tc.homeWall.remove(w);
			return true;
		}
		return false;
	}

	public boolean hitWall(MetalWall w) { // 子弹打到金属墙上
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			//this.tc.metalWall.remove(w); //子弹不能穿越金属墙
			return true;
		}
		return false;
	}

	public boolean hitHome() { // 当子弹打到家时
		if (this.live && this.getRect().intersects(tc.home.getRect())) {
			this.live = false;
			this.tc.home.setLive(false); // 当家接受一枪时就死亡
			return true;
		}
		return false;
	}
}
