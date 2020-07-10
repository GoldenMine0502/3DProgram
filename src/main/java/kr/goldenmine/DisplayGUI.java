package kr.goldenmine;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.util.Pair;
import javax.swing.*;
import kr.goldenmine.models.Figure;
import kr.goldenmine.models.Line;
import kr.goldenmine.models.SkyRectangle;
import kr.goldenmine.points.Point;
import kr.theterroronline.util.physics.Vector3d;

public class DisplayGUI extends JFrame {
    // 버퍼
    private BufferedImage buffer;

    // 각도
    private double xAngle = 0;
    private double yAngle = 0;

    // 점/면/육면체 등
    private PointStorage storage = new PointStorage();

    // 보는 시점의 거리
    private double eye;

    // 1칸을 실제 컴퓨터의 몇 픽셀로 할것인가
    private int onePixelSize;

    // 배율
    private double mul = 1D;

    // 마지막으로 드래그를 시도한 좌표
    private double lastX = -1;
    private double lastY = -1;

    // 위에 생기는 바
    private JMenuBar menuBar = new JMenuBar();

    private int currentEditing = -1;

    // 벡터 추가할 때 Dialog
    private VectorAddDialog vectorAddDialog = new VectorAddDialog((point, color) -> {
        addLine(new Vector3d(0, 0, 0, false), point, color, true);
        repaint();
    });

    // 벡터 수정할 때 Dialog
    private VectorAddDialog vectorEditDialog = new VectorAddDialog((point, color) -> {
        Figure figure = storage.getFigures().get(currentEditing).getKey();
        List<Vector3d> points = figure.getCoordinates();
        points.set(1, point);
        figure.setColor(color);

        updateVectorViewerPanels();
        repaint();
    });

    // 선택한 포인트 리스트
    private List<Vector3d> points = new ArrayList<>();
    private JButton innerProduct = new JButton("dot product");
    private JButton outerProduct = new JButton("cross product");
    private JButton throwProjectile = new JButton("throw a projectile");

    // 3D를 그려내기 위해 추가로 만든 패널
    // 안만들고 직접 JFrame에 그려버릴 수도 있는데 그러면 위에 메뉴창이 안만들어진다.
    // 왜 메뉴창이 안만들어지냐면 다음과 같이 paint 메소드를 오버라이딩 했는데 (처음에는 JFrame의 paint(Graphics g)를 오버라이딩 했다)
    // 오버라이딩 하기 전 조상 메소드에 메뉴바를 그려주는 기능이 포함되어 있기 때문이다
    // 하지만 조상 메소드를 호출하게 되면 화면 깜빡임이 발생해서 호출을 할 수가 없다
    // 어쩔수 없이 하위 패널을 만드는 것이다
    // JFrame안에는 패널을 넣을 수 있고 패널 안에 또 패널을 또 넣을수도 있고 패널안에 패널 여러개를 집어넣을수도 있다
    private JPanel drawPanel = new JPanel() {
        @Override
        public void paint(Graphics g) {
            // 버퍼에다가 쓴 뒤 draw 하는 이유는 업데이트 할때마다 생기는 깜빡이를 방지하기 위함
            // 캔버스(화면)를 초기화하고, 캔버스에 그림을 그려내는 그 순간에 깜빡거림이 생김
            drawInBuffer();
            g.drawImage(buffer, 0, 0, null);
        }
    };

    // 벡터 관리용으로 쓸 전용패널
    private GoldenList vectorViewerPanelList = new GoldenList();
    private GoldenList rectangleList = new GoldenList();

    private DisplayGUI instance = this;

    public DisplayGUI() {
        this(0, 25);
    }

    // 0, 1로 쓰게 되면 0번 인덱스와 1번 인덱스에 등록한 점끼리 선으로 연결해주게 된다.
    // 아래 코드에서 상술
    private List<Point> lineDefaultPairs = Collections.singletonList(new Point(0, 1));

    // 선 추가
    public void addLine(Vector3d p1, Vector3d p2, Color color, boolean administrator) {
        storage.add(new Line(p1, p2, color), administrator);
        if (administrator) {
            updateVectorViewerPanels();
        }
    }

    public void addRectangle(Vector3d pos, Vector3d start, Vector3d finish, Color color) {
        Vector3d difference = finish.subtract(start);
        storage.add(new SkyRectangle(pos, new Vector3d(0, 0, 0, true), 0.1, start.getX(), start.getY(), start.getZ(), difference.getX(), difference.getY(), difference.getZ(), color), false);
    }

    public void initializePoints() {
        points.clear();

        for (int i = 0; i < vectorViewerPanelList.listSize(); i++) {
            ((ViewerPanel) vectorViewerPanelList.getElement(i)).clearCheckBox();
        }
    }

    public DisplayGUI(double eye, int onePixelSize) {
        // 타이틀 설정
        setTitle("3d program");

        vectorAddDialog.setTitle("add new vector..");
        vectorEditDialog.setTitle("edit vector..");

        // 레이아웃 설정
        // 화면을 동서남북 가운데 이렇게 5군데로 쪼개서 쓸 수 있다
        setLayout(new BorderLayout());

        this.eye = eye;
        this.onePixelSize = onePixelSize;

        // 사이즈 설정
        // 800,600으로 고정시켰다. + 200은 벡터 관리 전용
        setSize(1000, 620);
        buffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        setResizable(false);

        drawPanel.setPreferredSize(new Dimension(800, 600));
        vectorViewerPanelList.setPreferredSize(new Dimension(200, 600));
        vectorViewerPanelList.getScrollPane().setPreferredSize(new Dimension(200, 600));

        JPanel east_south = new JPanel();
        east_south.setLayout(new FlowLayout());
        east_south.add(innerProduct);
        east_south.add(outerProduct);

        JPanel east = new JPanel();
        east.setLayout(new BorderLayout());
        east.add(vectorViewerPanelList);
        east.add(east_south, "South");


        innerProduct.addActionListener((e) -> {
            if (points.size() >= 2) {
                Vector3d result = new Vector3d(1, 1, 1);
                for (int i = 0; i < points.size(); i++) {
                    Vector3d p3d = points.get(i);
                    result.multiply(p3d);
                }

                double sum = result.getX() + result.getY() + result.getZ();
                JOptionPane.showMessageDialog(instance, "counts: " + points.size() + ", result: " + sum);

                initializePoints();
            }
        });

        outerProduct.addActionListener(e -> {
            if (points.size() >= 2) {
                Vector3d p3d = points.get(0);
                Vector3d p3d2 = points.get(1);

                vectorAddDialog.setVector(p3d.out3D(p3d2), Color.GREEN);
                vectorAddDialog.setVisible(true);

                initializePoints();
            }
        });

        throwProjectile.addActionListener(e -> {

        });

        // drawPanel을 가운데에 추가해준다
        // 여담으로 Border로 5군데로 쪼갰다곤 하지만
        // (North, South, East, West) 나머지 쪼갠 것에 아무것도 넣지 않으면 Center가 그 자리를 먹어버린다.
        add(drawPanel, "Center");
        add(east, "East");
        add(throwProjectile, "South");

        addDefaultLines();
        registerEvents();

        addMenus();
    }

    public void updateVectorViewerPanels() {
        int index = 0;
        int loopIndex = 0;

        for (Pair<Figure, Boolean> figureInfo : storage.getFigures()) {
            Figure figure = figureInfo.getKey();
            Vector3d point = figure.getCoordinates().get(1);
            boolean administrator = figureInfo.getValue();

            if (administrator) {
                ViewerPanel panel;
                if (index >= vectorViewerPanelList.listSize()) {
                    panel = new ViewerPanel();
                    panel.setEvent(() -> {
                        currentEditing = panel.getIndex();
                        vectorEditDialog.setVector(figure.getCoordinates().get(1), figure.getColor());
                        vectorEditDialog.setVisible(true);
                    });
                    panel.setEventCheckbox(() -> {
                        if(panel.isSelected()) {
                            System.out.println("Added");
                            points.add(figure.getCoordinates().get(1));
                        } else {
                            points.remove(figure.getCoordinates().get(1));
                        }
                    });
                    vectorViewerPanelList.addElement(panel);
                } else {
                    panel = (ViewerPanel) vectorViewerPanelList.getElement(index);
                }


                panel.setTitle(loopIndex, "(" + point.getX() + ", " + point.getY() + ", " + point.getZ() + ")");
                panel.invalidate();

                index++;
            }

            loopIndex++;
        }
    }

    // 상단에 나오는 메뉴를 설정해준다
    public void addMenus() {
        JMenu menuAdd = new JMenu("add");

        JMenuItem menuItemAdd = new JMenuItem("add new vector..");
        // Alt + V 키보드 누르면 똑같은 효과가 남
        menuItemAdd.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, InputEvent.ALT_MASK));
        JMenuItem menuItemAddRectangle = new JMenuItem("add new rectangle..");
        // Alt + V 키보드 누르면 똑같은 효과가 남
        menuItemAddRectangle.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, InputEvent.ALT_MASK));
        menuAdd.add(menuItemAdd);

        menuBar.add(menuAdd);

        // 버튼을 클릭했을때 벡터 추가 화면을 열어준다
        menuItemAdd.addActionListener(e -> vectorAddDialog.setVisible(true));
        menuItemAddRectangle.addActionListener(e -> {});
        // 메뉴바 설정
        setJMenuBar(menuBar);
    }

    // 기본적인 이벤트들을 등록시켜준다.
    public void registerEvents() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastX != -1 && lastY != -1) {
                    // 현재 좌표 - 이전 좌표 이렇게 해서 Angle에 더해주거나 빼주는 것
                    // 부호를 바꾸면 반대로 간다
                    yAngle += (e.getX() - lastX) / 2;
                    xAngle -= (e.getY() - lastY) / 2;

                    // 화면 업데이트
                    repaint();
                }

                // 이전 좌표 업데이트
                lastX = e.getX();
                lastY = e.getY();
            }


        });

        addMouseWheelListener(e -> {

            if (e.getWheelRotation() == 1) { // 휠 위쪽 아래쪽 방향에 따라 값이 1이거나 -1이다.
                // 여담으로 노트북 제스처로도 되더라
                if (mul >= 0.1) {
                    mul -= 0.1;
                }
            } else { /* if e.getWheelRotation() == -1 */
                if (mul < 20) {
                    mul += 0.1;
                }
            }
            repaint();
        });


        addMouseListener(new MouseAdapter() {
            // 마우스를 뗐을때 최근 좌표 초기화
            // 안하게 되면 회전이 순간이동한다
            @Override
            public void mouseReleased(MouseEvent e) {
                lastX = -1;
                lastY = -1;
            }

            // 패널이 약간 아래쪽으로 내려가 있어서 실제 마우스 좌표와 차이가 있어 약간의 보정을 해준다
            int adaptX = 0;
            int adaptY = 50;

            // 클릭했을때 점들의 실제 좌표랑 마우스 클릭 좌표랑 비교해본다
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = 0;

                // 현재 존재하는 모든 점들을 루프 돌려본다
                Loop:
                for (Pair<Figure, Boolean> figureInfo : storage.getFigures()) {
                    Figure figure = figureInfo.getKey();
                    if (figureInfo.getValue()) {
                        for (Vector3d point3D : figure.getCoordinates()) {
                            //각 점의 실제 위치를 구한다
                            Point point = point3D.getRotatePoint(xAngle, yAngle).get2DPoint(eye, mul).toPosition(buffer.getWidth() / 2.0, buffer.getHeight() / 2.0, onePixelSize);

                            // 실제 점의 좌표와 클릭한 마우스간 차이가 10픽셀 이내라면 해당 벡터를 수정할 수 있게 해준다
                            if (Math.abs(Math.abs(point.x - e.getX()) - adaptX) <= 10 && Math.abs(Math.abs(point.y - e.getY()) - adaptY) <= 10) {
                                currentEditing = index;
                                vectorEditDialog.setVector(figure.getCoordinates().get(1), figure.getColor());
                                vectorEditDialog.setVisible(true);
                                break Loop;
                            }
                        }
                    }
                    index++;
                }
            }
        });

        // X버튼 누르거나 Alt + F4 등으로 껐을때 강종시켜버린다
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // 강종하는 코드
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    // 기본이 될 베이스 선들을 추가해준다
    public void addDefaultLines() {
        for (int x = -10; x <= 10; x++) {
            addLine(new Vector3d(x, -10, 0), new Vector3d(x, 10, 0), Color.LIGHT_GRAY, false);
        }
        for (int y = -10; y <= 10; y++) {
            addLine(new Vector3d(-10, y, 0), new Vector3d(10, y, 0), Color.LIGHT_GRAY, false);
        }
        addLine(new Vector3d(0, 0, -10), new Vector3d(0, 0, 10), Color.LIGHT_GRAY, false);
    }

    public void drawInBuffer() {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 버퍼에다 싸질러녾은 잡잡한거 초기화
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        // 선들을 그려낸다
        for (Pair<Figure, Boolean> figureInfo : storage.getFigures()) {
            // 색깔을 설정하고
            Figure figure = figureInfo.getKey();
            g.setColor(figure.getColor());

            // 그려낼 점들의 위치를 받아온다.
            List<Vector3d> figures = figure.getCoordinates();

            for (int i = 0; i < figures.size(); i++) {
                Vector3d position = figures.get(i);

                // 현재 각도에 맞게 회전을 시켜준다. (3D 회전을 시킬때 x축을 회전하고 y축을 따로 회전시켜도 된다)
                // 왜 이렇게하면 회전이 되는지는 구글에 많이 나와있다.
                Vector3d point3D = position.getRotatePoint(xAngle, yAngle);

                // get2DPoint: 3D좌표를 2D 모니터 화면에 투영
                // toPosition: 쓴 좌표를 모니터의 픽셀 위치에 맞게 배치시켜준다.
                // (실제 Point에는 (1,1) 이런 좌표가 들어있는데 이걸 모니터에 표현해야 하니까. 모니터의 (1,1) 좌표에 아무런 보정도 없이 그려버리면 눈뜨고 보기 쉽지 않을꺼다)
                Point point = point3D.get2DPoint(eye, mul).toPosition(buffer.getWidth() / 2.0, buffer.getHeight() / 2.0, onePixelSize);

                // 점에는 따로 사각형 모양을 해준다. 없애면 그냥 선만 남는다.
                g.fillRect((int) point.x - 3, (int) point.y - 3, 6, 6);
            }

            List<Point> connect = figure.getConnects();

            for (int i = 0; i < connect.size(); i++) {
                Point point = connect.get(i);

                //연결할 두개의 점을 가져온다.
                Vector3d pp1 = figures.get((int) point.x).getRotatePoint(xAngle, yAngle);
                Vector3d pp2 = figures.get((int) point.y).getRotatePoint(xAngle, yAngle);

                if (Math.abs(pp1.getZ()) > eye) {
                    eye = Math.abs(pp1.getZ()) * 3;
                }

                if (Math.abs(pp2.getZ()) > eye) {
                    eye = Math.abs(pp2.getZ()) * 3;
                }

                // 위와 똑같이 모니터 픽셀에 맞게 좌표를 모니터 픽셀 위치로 변경
                Point p = pp1.get2DPoint(eye, mul).toPosition(buffer.getWidth() / 2.0, buffer.getHeight() / 2.0, onePixelSize);
                Point p2 = pp2.get2DPoint(eye, mul).toPosition(buffer.getWidth() / 2.0, buffer.getHeight() / 2.0, onePixelSize);

                // 선을 그려준다.
                g.drawLine((int) p.x, (int) p.y, (int) p2.x, (int) p2.y);
            }
        }
    }
}
