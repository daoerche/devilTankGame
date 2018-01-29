import java.awt.*;
import java.util.Random;

public class StatusWall {
    public static final int width = 32;
    public static final int length = 32;

    private int x, y;
    TankClient tc;
    private static Random r = new Random();

    private boolean live = false;

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    private static Image[] statusImags = null;
    static {
        statusImags = new Image[] { tk.getImage(CommonWall.class
                .getResource("Images/statusWall.png")), };
    }

    public void draw(Graphics g) {
        if (r.nextInt(1000) > 997) {
            this.live = true;
            move();
        }
        if (!live)
            return;
        g.drawImage(statusImags[0], x, y, null);

    }

    private void move() {
        x = r.nextInt(1150);
        y = r.nextInt(750)+100;
    }

    public Rectangle getRect() { //返回长方形实例
        return new Rectangle(x, y, width, length);
    }

    public boolean isLive() {//判断是否还活着
        return live;
    }

    public void setLive(boolean live) {  //设置生命
        this.live = live;
    }
}
