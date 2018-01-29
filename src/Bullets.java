import java.awt.*;
import java.util.List;

public class Bullets {
	public int speedX = 9;
	public int speedY = 9; // �ӵ���ȫ�־�̬�ٶ�

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
		bulletImages = new Image[] { // ��ͬ������ӵ�
				// С�ӵ�
				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletL.gif")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletU.gif")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletR.gif")),

				tk.getImage(Bullets.class.getClassLoader().getResource(
						"images/bulletD.gif")),

				// �����ӵ�
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

	public Bullets(int x, int y, Direction dir) { // ���캯��1������λ�úͷ���
		this.x = x;
		this.y = y;
		this.diretion = dir;
	}

	// ���캯��2������������������
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
			x -= speedX; // �ӵ������������
			break;

		case U:
			y -= speedY;
			break;

		case R:
			x += speedX; // �ֶβ�������
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

		switch (diretion) { // ѡ��ͬ������ӵ�
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

		move(); // �����ӵ�move()����
	}

	public boolean isLive() { // �ж��Ƿ񻹻���
		return live;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

	public boolean hitTanks(List<Tank> tanks) {// ���ӵ���̹��ʱ
		for (int i = 0; i < tanks.size(); i++) {
			if (hitTank(tanks.get(i))) { // ��ÿһ��̹�ˣ�����hitTank
				return true;
			}
		}
		return false;
	}

	public boolean hitTank(Tank t) { // ���ӵ���̹����

		if (this.live && this.getRect().intersects(t.getRect()) && t.isLive()&& this.good != t.isGood()) {

			BombTank e = new BombTank(t.getX(), t.getY(), tc);
			tc.bombTanks.add(e);
			if (t.isGood()) {
				t.setLife(t.getLife() - 50); // ��һ���ӵ���������50������4ǹ����,������ֵ200
				if (t.getLife() <= 0){
					t.setLive(false); // ������Ϊ0ʱ����������Ϊ����״̬
					this.tc.home.setLive(false);
				}
			} else {
				t.setLive(false);
				Tank.count--;
				tc.count++;
			}

			this.live = false;

			return true; // ����ɹ�������true
		}
		return false; // ���򷵻�false
	}

	public boolean hitWall(CommonWall w) { // �ӵ���CommonWall��
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			this.tc.otherWall.remove(w); // �ӵ���CommonWallǽ��ʱ���Ƴ��˻���ǽ
			this.tc.homeWall.remove(w);
			return true;
		}
		return false;
	}

	public boolean hitWall(MetalWall w) { // �ӵ��򵽽���ǽ��
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			//this.tc.metalWall.remove(w); //�ӵ����ܴ�Խ����ǽ
			return true;
		}
		return false;
	}

	public boolean hitHome() { // ���ӵ��򵽼�ʱ
		if (this.live && this.getRect().intersects(tc.home.getRect())) {
			this.live = false;
			this.tc.home.setLive(false); // ���ҽ���һǹʱ������
			return true;
		}
		return false;
	}
}
