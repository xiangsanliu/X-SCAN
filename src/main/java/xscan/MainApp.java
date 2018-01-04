package xscan;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 项三六
 */
public class MainApp extends Frame implements ActionListener {


    private List<Point> points = new ArrayList<Point>();

    /**
     * 初始化顶点
     */
    private void initPoints() {

        points.add(new Point(200, 200));
        points.add(new Point(500, 100));
        points.add(new Point(1100, 300));
        points.add(new Point(1100, 800));
        points.add(new Point(500, 500));
        points.add(new Point(200, 700));

    }

    private void myPloygonScan(Graphics graphics) {
        initPoints();

        //获得扫描线的范围
        int maxY = 0;
        for (Point point : points) {
            maxY = point.getY() > maxY ? (int) point.getY() : maxY;
        }

        //建立新边表
        int windowHeight = 900;
        Edge[]  pET = new Edge[windowHeight];
        for (int i=0; i<maxY; i++) {
            pET[i] = new Edge();
        }
        Edge aet = new Edge();
        for (int i=0; i<points.size(); i++) {
//            int x0 = points.get((i - 1 + points.size()) % points.size()).x;
            int x1 = points.get(i).x;
            int x2 = points.get((i + 1) % points.size()).x;
//            int x3 = points.get((i + 2) % points.size()).x;
            int y0 = points.get((i - 1 + points.size()) % points.size()).y;
            int y1 = points.get(i).y;
            int y2 = points.get((i + 1) % points.size()).y;
            int y3 = points.get((i + 2) % points.size()).y;

            graphics.drawLine(x1, y1, x2, y2);
            if (y1 == y2) {
                continue;
            }


            //计算下端点y坐标、上端点y坐标、下端点x坐标和斜率倒数dx
            int yMin = y1 > y2 ? y2:y1;
            int yMax = y1 > y2 ? y1:y2;
            float x = y1 > y2 ? x2 : x1;
            float dx = (x1 - x2)*1.0f / (y1-y2);

            //共享顶点的两条边是否是分布在扫面线的两边
            boolean isSpread = (y1 < y2) && (y1 > y0) || (y2<y1) && (y2>y3);
            if (isSpread) {
                yMin++;
                x += dx;
            }

            Edge p = new Edge();
            p.yMax = yMax;
            p.x = x;
            p.dx = dx;
            p.next = pET[yMin].next;
            pET[yMin].next = p;

        }
        //新边表建立完毕

        //开始扫描
        for (int i=0 ;i<maxY; i++) {
            while (pET[i].next != null) {
                Edge insert = pET[i].next;
                Edge p = aet;
                while (p.next != null) {
                    if (insert.x > p.next.x) {
                        p = p.next;
                        continue;
                    }
                    if (insert.x == p.next.x && insert.dx > p.next.dx) {
                        p = p.next;
                        continue;
                    }
                    break;
                }

                //从新边表删除,并插入活性边表
                pET[i].next = insert.next;
                insert.next = p.next;
                p.next = insert;
            }

            Edge p = aet;
            while (p.next != null && p.next.next != null) {
                for (int x = (int) p.next.x; x <p.next.next.x; x++) {
                    drawDot(x, i);
                }
                p = p.next.next;
            }

            p = aet;
            while (p.next != null) {
                if (p.next.yMax == i) {
                    Edge delete = p.next;
                    p.next = delete.next;
                    delete.next = null;
//                    delete = null;
                } else {
                    p = p.next;
                }
            }

            p = aet;
            while (p.next != null) {
                p.next.x += p.next.dx;
                p = p.next;
            }

        }

    }

    public static void main(String[] args) {
        Frame frame = new MainApp();
        frame.setSize(1200, 900);
        frame.setVisible(true);
    }

    private MainApp() {
        setTitle("X扫描线算法");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }

        });
    }

    @Override
    public void paint(Graphics g) {
        myPloygonScan(g);
    }

    public void actionPerformed(ActionEvent e) {
    }

    //

    /**
     * 因为Graphics类中没有画点的方法，所以使用画直线代替
     * @param x 点的x坐标
     * @param y 点的y坐标
     */
    private void drawDot(int x, int y) {
        getGraphics().drawLine(x, y, x, y);
    }
}
