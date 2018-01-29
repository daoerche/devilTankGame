import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tank {
	public int speedX = 5, speedY =5; // ��̬ȫ�ֱ����ٶ�---------������Ϊ���������ü����ٶȿ�Ļ��Ƚ���
	public static int count = 0;
	public static final int width = 35, length = 35; // ̹�˵�ȫ�ִ�С�����в��ɸı���
	private Direction direction = Direction.STOP; // ��ʼ��״̬Ϊ��ֹ
	private Direction Kdirection = Direction.U; // ��ʼ������Ϊ����
	// �����ӵ�����
	public int bullesType = 1;
	// ����״̬����ʱ��
	public int statusTime = 0;
	/**
	 * 0 - > û��״̬
	 * 1 - > �ӵ������ͨǽ
	 * 2 - > �ķ��ӵ�
	 * 3 - > ��ţ����
	 */
	public int statusImg = 0;
	TankClient tc;

	private boolean good;
	private int x, y;
	private int oldX, oldY;
	private boolean live = true; // ��ʼ��Ϊ����
	private int life = 200; // ��ʼ����ֵ

	private static Random r = new Random();
	private int step = r.nextInt(10)+5 ; // ����һ�������,���ģ��̹�˵��ƶ�·��

	private boolean bL = false, bU = false, bR = false, bD = false;
	

	private static Toolkit tk = Toolkit.getDefaultToolkit();// �������
	private static Image[] tankImags = null; // �洢ȫ�־�̬
	static {
		tankImags = new Image[] {
				tk.getImage(BombTank.class.getResource("Images/tankD.gif")),
				tk.getImage(BombTank.class.getResource("Images/tankU.gif")),
				tk.getImage(BombTank.class.getResource("Images/tankL.gif")),
				tk.getImage(BombTank.class.getResource("Images/tankR.gif")), };

	}

	public static Image[] tankStatusImags = null; // �洢ȫ�־�̬
	static {
		tankStatusImags = new Image[]{
				tk.getImage(BombTank.class.getResource("Images/statusWall.png")),
				tk.getImage(BombTank.class.getResource("Images/statusFourFire.png")),
				tk.getImage(BombTank.class.getResource("Images/statusReduceSpeed.png")),
				tk.getImage(BombTank.class.getResource("Images/statusBigBullets.png"))
		};
	}

	public Tank(int x, int y, boolean good) {// Tank�Ĺ��캯��1
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
		if(!this.good)
			Tank.count++;
	}

	public Tank(int x, int y, boolean good, Direction dir, TankClient tc) {// Tank�Ĺ��캯��2
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
				tc.tanks.remove(this); // ɾ����Ч��
			}
			return;
		}

		if (good)
			new DrawBloodbBar().draw(g); // ����һ��Ѫ��

		switch (Kdirection) {
							//���ݷ���ѡ��̹�˵�ͼƬ
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

		move();   //����move����
	}

	void move() {

		this.oldX = x;
		this.oldY = y;

		switch (direction) {  //ѡ���ƶ�����
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
		if (y < 40)      //��ֹ�߳��涨����
			y = 40;
		if (x + Tank.width > TankClient.Fram_width)  //����������ָ����߽�
			x = TankClient.Fram_width - Tank.width;
		if (y + Tank.length > TankClient.Fram_length)
			y = TankClient.Fram_length - Tank.length;

		if (!good) {
			Direction[] directons = Direction.values();
			if (step == 0) {                  
				step = r.nextInt(12) + 3;  //�������·��
				int rn = r.nextInt(directons.length);
				direction = directons[rn];      //�����������
			}
			step--;

			if (r.nextInt(40) > 38)//��������������Ƶ��˿���
				this.fire(this.bullesType);
		}
	}

	private void changToOldDir() {  
		x = oldX;
		y = oldY;
	}

	public void keyPressed(KeyEvent e) {  //���ܼ����¼�
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_RIGHT: //�������Ҽ�
				bR = true;
				break;

			case KeyEvent.VK_LEFT://���������
				bL = true;
				break;

			case KeyEvent.VK_UP:  //�������ϼ�
				bU = true;
				break;

			case KeyEvent.VK_DOWN://�������¼�
				bD = true;
				break;
		}
		decideDirection();//���ú���ȷ���ƶ�����
	}

	void decideDirection() {
		if (!bL && !bU && bR && !bD)  //�����ƶ�
			direction = Direction.R;

		else if (bL && !bU && !bR && !bD)   //������
			direction = Direction.L;

		else if (!bL && bU && !bR && !bD)  //�����ƶ�
			direction = Direction.U;

		else if (!bL && !bU && !bR && bD) //�����ƶ�
			direction = Direction.D;

		else if (!bL && !bU && !bR && !bD)
			direction = Direction.STOP;  //û�а������ͱ��ֲ���
	}

	public void keyReleased(KeyEvent e) {  //�����ͷż���
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
		decideDirection();  //�ͷż��̺�ȷ���ƶ�����
	}

	public void fire(int bullesType) {  //���𷽷�
		// �ӵ�Ϊ�����ӵ�
		if(bullesType == 1) {
			if (!live)
				return;
			int x = this.x + Tank.width / 2;
			int y = this.y + Tank.length / 2;
			Bullets m = new Bullets(x, y + 2, good, Kdirection, this.tc);  //û�и�������ʱ����ԭ���ķ��򷢻�
			m.x = this.x + Tank.width / 2 - m.width / 2;
			m.y = this.y + Tank.length / 2 - m.length / 2 + 2;
			m.setSpeed(tc.level, tc.level);
			tc.bullets.add(m);

		// �ӵ�Ϊ��ͨǽ��
		}else if(bullesType == 2) {
			if (!live)
				return;
			int x;  //����λ��
			int y;
			switch (this.Kdirection) { // ѡ��ͬ������ӵ�
				case L:
					x = this.x+40;  //����λ��
					y = this.y;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;

				case U:
					x = this.x;  //����λ��
					y = this.y+40;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;

				case R:
					x = this.x-40;  //����λ��
					y = this.y;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;

				case D:
					x = this.x;  //����λ��
					y = this.y-40;
					tc.otherWall.add(new CommonWall(x , y, this.tc));
					break;
			}

		// �ӵ�Ϊ���䵯
		}else if(bullesType == 3) {
			if (!live)
				return;
			int x = this.x + Tank.width / 2;
			int y = this.y + Tank.length / 2;

			Bullets m1 = new Bullets(x, y + 2, good, Direction.U, this.tc);  //û�и�������ʱ����ԭ���ķ��򷢻�
			m1.x = this.x + Tank.width / 2 - m1.width / 2;
			m1.y = this.y + Tank.length / 2 - m1.length / 2 + 2;
			m1.setSpeed(tc.level, tc.level);
			tc.bullets.add(m1);

			Bullets m2 = new Bullets(x, y + 2, good, Direction.D, this.tc);  //û�и�������ʱ����ԭ���ķ��򷢻�
			m2.x = this.x + Tank.width / 2 - m2.width / 2;
			m2.y = this.y + Tank.length / 2 - m2.length / 2 + 2;
			m2.setSpeed(tc.level, tc.level);
			tc.bullets.add(m2);

			Bullets m3 = new Bullets(x, y + 2, good, Direction.L, this.tc);  //û�и�������ʱ����ԭ���ķ��򷢻�
			m3.x = this.x + Tank.width / 2 - m3.width / 2;
			m3.y = this.y + Tank.length / 2 - m3.length / 2 + 2;
			m3.setSpeed(tc.level, tc.level);
			tc.bullets.add(m3);

			Bullets m4 = new Bullets(x, y + 2, good, Direction.R, this.tc);  //û�и�������ʱ����ԭ���ķ��򷢻�
			m4.x = this.x + Tank.width / 2 - m4.width / 2;
			m4.y = this.y + Tank.length / 2 - m4.length / 2 + 2;
			m4.setSpeed(tc.level, tc.level);
			tc.bullets.add(m4);

		// �ӵ�Ϊ���󵼵�
		}else if(bullesType == 4) {
			if (!live)
				return;
			int x = this.x + Tank.width / 2;
			int y = this.y + Tank.length / 2;
			Bullets m = new Bullets(x, y + 2, good, Kdirection, this.tc);  //û�и�������ʱ����ԭ���ķ��򷢻�
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

	public boolean collideWithWall(CommonWall w) {  //��ײ����ͨǽʱ
		if (this.live && this.getRect().intersects(w.getRect())) {
			 this.changToOldDir();    //ת����ԭ���ķ�����ȥ
			return true;
		}
		return false;
	}

	public boolean collideWithWall(MetalWall w) {  //ײ������ǽ
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.changToOldDir();     
			return true;
		}
		return false;
	}

	public boolean collideRiver(River r) {    //ײ��������ʱ��
		if (this.live && this.getRect().intersects(r.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideHome(Home h) {   //ײ���ҵ�ʱ��
		if (this.live && this.getRect().intersects(h.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideWithTanks(java.util.List<Tank> tanks) {//ײ��̹��ʱ
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
	 * �Ը���״̬����
	 * @param b
	 * @return
	 */
	// �Ե�Ѫ
	public boolean eat(GetBlood b) {
		if (this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
			if(this.life<=100)
			this.life = this.life+100;      //ÿ��һ��������100������
			else
				this.life = 200;
			b.setLive(false);
			return true;
		}
		return false;
	}

	// �Ե���ͨǽ
	public boolean eatStatusWall(StatusWall s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			this.bullesType = 2;
			// ����״̬����ʱ��
			this.statusTime = 20 * 20; // ����ʱ��20s
			this.statusImg = 1;
			s.setLive(false);
			return true;
		}
		return false;
	}

	// �Ե��ķ��ӵ�
	public boolean eatStatusFourFire(StatusFourFire s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			this.bullesType = 3;
			// ����״̬����ʱ��
			this.statusTime = 20 * 20; // ����ʱ��20s
			this.statusImg = 2;
			s.setLive(false);
			return true;
		}
		return false;
	}

	// �Ե���ţ����
	public boolean eatStatusReduceSpeed(StatusReduceSpeed s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			for(int i = 0; i<tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				t.speedX = 1;
				t.speedY = 1;
			}
			// ����״̬����ʱ��
			this.statusTime = 20 * 20; // ����ʱ��20s
			this.statusImg = 3;
			s.setLive(false);
			return true;
		}
		return false;
	}

	// �Ե����󵼵�
	public boolean eatStatusBigBullets(StatusBigBullets s) {
		if (this.live && s.isLive() && this.getRect().intersects(s.getRect())) {
			this.bullesType = 4;
			// ����״̬����ʱ��
			this.statusTime = 20 * 20; // ����ʱ��20s
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