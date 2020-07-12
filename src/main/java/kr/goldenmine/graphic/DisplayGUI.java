package kr.goldenmine.graphic;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javafx.util.Pair;

import javax.swing.*;

import kr.goldenmine.CollideResult;
import kr.goldenmine.PointStorage;
import kr.goldenmine.graphic.util.ViewerPanel;
import kr.goldenmine.graphic.dialogs.LineFigureAddDialog;
import kr.goldenmine.graphic.dialogs.RectangleFigureAddDialog;
import kr.goldenmine.graphic.util.Face;
import kr.goldenmine.models.*;
import kr.goldenmine.points.Point;
import kr.goldenmine.graphic.util.GoldenList;
import kr.theterroronline.util.physics.Vector3d;

import static kr.theterroronline.util.physics.Vector3dKt.*;

public class DisplayGUI extends JFrame {
    final static double EPSILON = 0.00001D;
    // 보는 시점의 거리
    private double eye;

    // 1칸을 실제 컴퓨터의 몇 픽셀로 할것인가
    private final int onePixelSize;

    // 버퍼
    private final BufferedImage buffer;

    // 점/면/육면체 등
    private final PointStorage storage = new PointStorage();

    // 배율
    private double mul = 1D;

    // 각도
    private double xAngle = 0;
    private double yAngle = 0;

    // 마지막으로 드래그를 시도한 좌표
    private double lastX = -1;
    private double lastY = -1;

    // 현재 수정중인 벡터의 loopIndex
    private int currentEditingLoopIndex = -1;

    // 위에 생기는 바
    private final JMenuBar menuBar = new JMenuBar();

    // 벡터 추가할 때 Dialog
    private final LineFigureAddDialog vectorAddDialog = new LineFigureAddDialog((points, color) -> {
        addLine(new Vector3d(0, 0, 0, false), points.get(0), color, true);
        //repaint();
    });

    // 벡터 수정할 때 Dialog
    private final LineFigureAddDialog vectorEditDialog = new LineFigureAddDialog((points, color) -> {
        Figure figure = storage.getFigures().get(currentEditingLoopIndex).getKey();
        List<Vector3d> editingPoints = figure.getCoordinates();
        editingPoints.set(1, points.get(0));
        figure.setColor(color);

        updateVectorViewerPanels();
        //repaint();
    });

    // 직육면체 추가할 때 Dialog
    private RectangleFigureAddDialog rectangleAddDialog = new RectangleFigureAddDialog((points, color) -> {
        addRectangle(points.get(0), points.get(1), color);
        //repaint();
    });

    // 선택한 포인트 리스트
    private final List<Vector3d> points = new ArrayList<>();

    // 각종 버튼들
    private final JButton innerProduct = new JButton("dot product");
    private final JButton outerProduct = new JButton("cross product");
    private final JButton throwProjectile = new JButton("throw a projectile");

    // 3D를 그려내기 위해 추가로 만든 패널
    // 안만들고 직접 JFrame에 그려버릴 수도 있는데 그러면 위에 메뉴창이 안만들어진다.
    // 왜 메뉴창이 안만들어지냐면 다음과 같이 paint 메소드를 오버라이딩 했는데 (처음에는 JFrame의 paint(Graphics g)를 오버라이딩 했다)
    // 오버라이딩 하기 전 조상 메소드에 메뉴바를 그려주는 기능이 포함되어 있기 때문이다
    // 하지만 조상 메소드를 호출하게 되면 화면 깜빡임이 발생해서 호출을 할 수가 없다
    // 어쩔수 없이 하위 패널을 만드는 것이다
    // JFrame안에는 패널을 넣을 수 있고 패널 안에 또 패널을 또 넣을수도 있고 패널안에 패널 여러개를 집어넣을수도 있다
    private final JPanel drawPanel = new JPanel() {
        @Override
        public void paint(Graphics g) {
            // 버퍼에다가 쓴 뒤 draw 하는 이유는 업데이트 할때마다 생기는 깜빡이를 방지하기 위함
            // 캔버스(화면)를 초기화하고, 캔버스에 그림을 그려내는 그 순간에 깜빡거림이 생김
            try {
                drawInBuffer();
                g.drawImage(buffer, 0, 0, null);
            } catch(ConcurrentModificationException ex) {
                System.out.println("CME occured but keeps running 2");
            }
        }
    };

    // 벡터 관리용으로 쓸 전용패널
    private final GoldenList vectorViewerPanelList = new GoldenList();

    private final DisplayGUI instance = this;
    private Thread updateThread;

    public DisplayGUI() {
        this(0, 25);
    }

    // 선 추가
    public void addLine(Vector3d p1, Vector3d p2, Color color, boolean administrator) {
        storage.addFigure(new Line(p1, p2, color), administrator);
        if (administrator) {
            updateVectorViewerPanels();
        }
    }

    public void addRectangle(Vector3d start, Vector3d size, Color color) {
        storage.addFigure(new SkyRectangle(new Vector3d(0, 0, 0, true), 0.0001, start.getX(), start.getY(), start.getZ(), size.getX(), size.getY(), size.getZ(), color), false);
    }

    public void initializePoints() {
        points.clear();

        for (int i = 0; i < vectorViewerPanelList.listSize(); i++) {
            ((ViewerPanel) vectorViewerPanelList.getElement(i)).clearCheckBox();
        }
    }

    public CollideResult checkCollision(Dot dot, SkyRectangle rectangle) {

        final Vector3d rectangleSize = rectangle.getSize();

        // Rectangle의 왼쪽 아래 위치, Rectangle의 오른쪽 위 위치
        final Vector3d startPos = rectangle.getCurrentPos();
        final Vector3d finishPos = startPos.add(rectangleSize);

        final Vector3d lineDirection = dot.getVelocity(); // 직선의 기울기
        final Vector3d linePos = dot.getCurrentPos(); // 직선의 위치
        final Vector3d linePosLast = dot.getLastPos(); // 직선의 위치

//        final Vector3d startPosEPSILON = startPos.add(-EPSILON, -EPSILON, -EPSILON); // 부동소수점 연산시 오차를 살짝 보정해주기 위함
//        final Vector3d finishPosEPSILON = finishPos.add(EPSILON, EPSILON, EPSILON);

        // SkyRectangle에 해당하는 여섯 개의 면에 대한 벡터 생성
        final Vector3d faceXDirection = new Vector3d(1, 0, 0, false);
        final Vector3d faceYDirection = new Vector3d(0, 1, 0, false);
        final Vector3d faceZDirection = new Vector3d(0, 0, 1, false);
//        final Vector3d faceXDirectionN = new Vector3d(-1, 0, 0, false);
//        final Vector3d faceYDirectionN = new Vector3d(0, -1, 0, false);
//        final Vector3d faceZDirectionN = new Vector3d(0, 0, -1, false);

        //System.out.println("direction: " + lineDirection);
//        System.out.println("linePosLast: " + linePosLast);
//        System.out.println("linePosCurrent: " + linePos);

//        if(linePos.between(startPosEPSILON, finishPosEPSILON)) {
//            System.out.println("linePosCurrent: " + linePos);

        // 고등학교 때 배운 평면과 직선 사이의 교점을 구하는 코드 (기하와 벡터 교과서에 나오는 개념, 문서 참조)
        final Vector3d intersectX = collide(faceXDirection, startPos, lineDirection, linePos); // 시작 점 기준
        // 이제 교점을 구했으니 구해진 교점이 SkyRectangle의 범위 안에서 형성되었는 지 체크하면 됨.
        if(lineDirection.getX() >= 0 && intersectX.between(startPos, finishPos, EPSILON) && intersectX.between(linePosLast, linePos, EPSILON)) return new CollideResult(intersectX, Face.Z);

        final Vector3d intersectY = collide(faceYDirection, startPos, lineDirection, linePos);
        if(lineDirection.getY() >= 0 && intersectY.between(startPos, finishPos, EPSILON) && intersectY.between(linePosLast, linePos, EPSILON)) return new CollideResult(intersectY, Face.Y);

        final Vector3d intersectZ = collide(faceZDirection, startPos, lineDirection, linePos);
        if(lineDirection.getZ() >= 0 && intersectZ.between(startPos, finishPos, EPSILON) && intersectZ.between(linePosLast, linePos, EPSILON)) return new CollideResult(intersectZ, Face.Z);

        final Vector3d intersectXPlus = collide(faceXDirection, finishPos, lineDirection, linePos); // 끝 점 기준
        if(lineDirection.getX() <= 0 && intersectXPlus.between(startPos, finishPos, EPSILON) && intersectXPlus.between(linePosLast, linePos, EPSILON)) return new CollideResult(intersectXPlus, Face.XPlus);

        final Vector3d intersectYPlus = collide(faceYDirection, finishPos, lineDirection, linePos);
        if(lineDirection.getY() <= 0 && intersectYPlus.between(startPos, finishPos, EPSILON) && intersectYPlus.between(linePosLast, linePos, EPSILON)) return new CollideResult(intersectYPlus, Face.YPlus);

        final Vector3d intersectZPlus = collide(faceZDirection, finishPos, lineDirection, linePos);
        if(lineDirection.getZ() <= 0 && intersectZPlus.between(startPos, finishPos, EPSILON) && intersectZPlus.between(linePosLast, linePos, EPSILON)) return new CollideResult(intersectZPlus, Face.ZPlus);

//        addLine(new Vector3d(0, 0, 0, false), intersectZ, Color.BLACK, false);
//        System.out.println(intersectX);
//        System.out.println(intersectY);
//        System.out.println("direction: " + lineDirection);
//        System.out.println("xyz: " + rectangle.getX() + ", " + rectangle.getY() + ", " + rectangle.getZ());
//        System.out.println("finishPos: " + finishPos);
//        System.out.println("size: " + rectangleSize);
//        System.out.println("intersectX: " + intersectX);
//        System.out.println("intersectY: " + intersectY);
//        System.out.println("intersectZ: " + intersectZ);
//        System.out.println(intersectXPlus);
//        System.out.println(intersectYPlus);
//        System.out.println(intersectZPlus);
//        }
        return null;
    }

    public boolean checkCollision(SkyRectangle rec1, SkyRectangle rec2) {
        Vector3d rec1StartPos = rec1.getCurrentPos();
        Vector3d rec1FinishPos = rec1StartPos.add(rec1.getSize());
        Vector3d rec2StartPos = rec2.getCurrentPos();
        Vector3d rec2FinishPos = rec2StartPos.add(rec2.getSize());

        return rec1.getCoordinates().stream().anyMatch(it -> it.between(rec2StartPos, rec2FinishPos, EPSILON)) ||
                rec2.getCoordinates().stream().anyMatch(it -> it.between(rec1StartPos, rec1FinishPos, EPSILON));
    }

    public void startPhysicalCalculationThread() {
        if (updateThread == null) { // 1초에 100번정도 도는 쓰레드 굴리기
            updateThread = new Thread() {
                public void run() {
                    while (updateThread != null) {
                        try {
                            // buffer에 figure들을 그리는 과정과 충돌되는 CME 에러 대비
                            Iterator<Pair<Figure, Boolean>> objs = storage.getFigures().iterator(); // 모든 Figure들 foreach
                            while (objs.hasNext()) {
                                Pair<Figure, Boolean> pair = objs.next();
                                Figure figure = pair.getKey();
                                if (figure instanceof PhysicalObject) { // Figure중 PhysicalObject에 한해서 다음 위치 계산 함수 호출
                                    PhysicalObject obj = (PhysicalObject) figure;
                                    obj.calculateNextPosition(); // 해당 함수를 호출하면 다음의 위치를 계산해 줌.
                                    if (obj instanceof Dot) {
                                        Dot dot = (Dot) obj;

                                        // 서로 충돌한 SkyRectangle이 있는 지 체크
                                        Optional<Pair<SkyRectangle, CollideResult>> result = storage.getFigures().stream()
                                                .filter(it -> it.getKey() instanceof SkyRectangle) // SkyRectangle인 객체만 필터링
                                                .map(it -> new Pair<>((SkyRectangle) it.getKey(), checkCollision(dot, (SkyRectangle) it.getKey()))) // 충돌 계산
                                                .filter(it -> it.getValue() != null)
                                                .findFirst();
//                                    Optional<Pair<Figure, Boolean>> result = storage.getFigures().stream().filter(it->it.getKey() instanceof SkyRectangle && checkCollision(dot, (SkyRectangle) it.getKey())).findFirst();
                                        if (result.isPresent()) { // 충돌한 SkyRectangle이 있는가?
//                                            System.out.println("collided");
                                            SkyRectangle rectangle = result.get().getKey();
                                            //rectangle.getVelocity().setZ(-0.1);
                                            applyNewVelocity(dot, rectangle); // 새로운 Velocity 계산


                                            Vector3d intersect = result.get().getValue().getIntersect();
                                            Face face = result.get().getValue().getFace();

                                            dot.getCurrentPos().copyFrom(intersect);

                                            switch (face) { // 우선 충돌한 Dot에 대해 튕겨내는 효과 설정
                                                case X:
                                                case XPlus:
//                                                dot.getAcceleration().setX(-dot.getAcceleration().getX());
                                                    dot.getVelocity().setX(-dot.getVelocity().getX());
                                                    break;
                                                case Y:
                                                case YPlus:
//                                                dot.getAcceleration().setY(-dot.getAcceleration().getY());
                                                    dot.getVelocity().setY(-dot.getVelocity().getY());
                                                    break;
                                                case Z:
                                                case ZPlus:
//                                                dot.getAcceleration().setZ(-dot.getAcceleration().getZ());
                                                    dot.getVelocity().setZ(-dot.getVelocity().getZ());
                                                    // 공이 지면 위에 잘 서 있도록 하기 위함
                                                    if(0 <= dot.getVelocity().getZ() && dot.getVelocity().getZ() < 0.005) {
                                                        dot.getVelocity().setZ(0.005);
                                                    }
                                                    //System.out.println(dot.getVelocity());
//                                                    if(-EPSILON < dot.getVelocity().getZ())
//                                                    if(dot.getVelocity().getZ() < 0 && dot.getVelocity().getZ() > -EPSILON) {
//                                                        dot.getVelocity().setZ(dot.getVelocity().getZ() + EPSILON);
//                                                    }
                                                    break;
                                            }
                                            dot.calculateNextPosition();
                                        }
//                                        applyDragForce(dot);
                                    }
                                    if(obj instanceof SkyRectangle) {
                                        SkyRectangle skyRectangle = (SkyRectangle) obj;
                                        Optional<SkyRectangle> result = storage.getFigures().stream()
                                                .filter(it->it.getKey() instanceof SkyRectangle)
                                                .map(it->(SkyRectangle)it.getKey())
                                                .filter(it->it != skyRectangle && checkCollision(skyRectangle, it))
                                                .findFirst();
                                        if(result.isPresent()) {
                                            applyNewVelocity(skyRectangle, result.get());
                                        }
                                    }

                                    if (obj.getCurrentPos().getZ() <= -15) { // 만약 PhysicalObject가 너무 아래로 갔을 땐 리스트에서 제거
//                                        synchronized (storage.getFigures()) {
                                            objs.remove();
//                                        }
                                    }
                                }
                            }
                            // 화면 갱신
                            repaint();
                        } catch(ConcurrentModificationException ex) {
                            System.out.println("CME occured but keeps running");
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            Thread.sleep(10L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };

            updateThread.start();
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

                vectorAddDialog.setValues(Collections.singletonList(p3d.out(p3d2)), Color.GREEN);
                vectorAddDialog.setVisible(true);

                initializePoints();
            }
        });

        throwProjectile.addActionListener(e -> {

//            double angleXRadian = Math.toRadians(xAngle);
//            double angleYRadian = Math.toRadians(yAngle);
            // 현재 카메라 위치 대략적으로 구하기
            Vector3d currentPos = getDirection(yAngle + 90, -xAngle, true, 12).swapYZ();
//            addLine(new Vector3d(0, 0, 0, false), currentPos, Color.BLACK, false);
//            repaint();
            Vector3d velocity = getDirection(yAngle - 90, xAngle, true, 0.5).swapYZ();
            storage.addFigure(new Dot(currentPos, velocity, 3, 0.005));
        });

        // drawPanel외 여러 컴포넌트를 가운데에 추가해준다
        // 여담으로 Border로 5군데로 쪼갰다곤 하지만
        // (North, South, East, West) 나머지 쪼갠 것에 아무것도 넣지 않으면 Center가 그 자리를 먹어버린다.
        add(drawPanel, "Center");
        add(east, "East");
        add(throwProjectile, "South");

        addDefaultLines();
        registerEvents();

        addMenus();
        startPhysicalCalculationThread();
        addRectangle(new Vector3d(-2, -2, -2), new Vector3d(4, 4, 4), Color.GREEN);
        addRectangle(new Vector3d(-1, -1, 4), new Vector3d(2, 2, 2), Color.BLACK);
    }

    public void updateVectorViewerPanels() {
        int index = 0; // administorator가 켜져 있는 Figure에 한해서만 움직이는 인덱스
        int loopIndex = 0; // administrator가 켜져 있던 꺼져 있던 상관없이 storage를 위한 인덱스

        // 모든 도형(또는 점, 선)을 루프 돌려준다.
        for (Pair<Figure, Boolean> figureInfo : storage.getFigures()) {
            Figure figure = figureInfo.getKey();

            Vector3d point = figure.getCoordinates().get(1);
            boolean administrator = figureInfo.getValue();

            if (administrator) { // 수정이 가능한 선인지 확인
                ViewerPanel panel;
                if (index >= vectorViewerPanelList.listSize()) {
                    panel = new ViewerPanel();
                    panel.setEvent(() -> {
                        currentEditingLoopIndex = panel.getLoopIndex();
                        vectorEditDialog.setValues(Collections.singletonList(figure.getCoordinates().get(1)), figure.getColor());
                        vectorEditDialog.setVisible(true);
                    });
                    panel.setEventCheckbox(() -> {
                        if (panel.isSelected()) {
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
        // Alt + R 키보드 누르면 똑같은 효과가 남
        menuItemAddRectangle.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, InputEvent.ALT_MASK));

        menuAdd.add(menuItemAdd);
        menuAdd.add(menuItemAddRectangle);

        menuBar.add(menuAdd);

        // 버튼을 클릭했을때 벡터 추가 화면을 열어준다
        menuItemAdd.addActionListener(e -> vectorAddDialog.setVisible(true));
        menuItemAddRectangle.addActionListener(e -> rectangleAddDialog.setVisible(true));
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
                    //repaint();
                }

                // 이전 좌표 업데이트
                lastX = e.getX();
                lastY = e.getY();
            }


        });

        addMouseWheelListener(e -> {

            if (e.getWheelRotation() == 1) { // 휠 위쪽 아래쪽 방향에 따라 값이 1이거나 -1이다.
                // 여담으로 노트북 제스처로도 되더라
                if (mul > 0) {
                    mul -= 0.05;
                }
            } else { /* if e.getWheelRotation() == -1 */
                if (mul < 20) {
                    mul += 0.05;
                }
            }
            //repaint();
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
                    if (figure instanceof Line) {
                        if (figureInfo.getValue()) {
                            for (Vector3d point3D : figure.getCoordinates()) {
                                //각 점의 실제 위치를 구한다
                                Point point = getMonitorPosition(point3D);

                                // 실제 점의 좌표와 클릭한 마우스간 차이가 10픽셀 이내라면 해당 벡터를 수정할 수 있게 해준다
                                if (Math.abs(Math.abs(point.x - e.getX()) - adaptX) <= 10 && Math.abs(Math.abs(point.y - e.getY()) - adaptY) <= 10) {
                                    currentEditingLoopIndex = index;
                                    vectorEditDialog.setValues(Collections.singletonList(figure.getCoordinates().get(1)), figure.getColor());
                                    vectorEditDialog.setVisible(true);
                                    break Loop;
                                }
                            }
                        }
                    }

                    // TODO 원래 rectangle일 때 전용 수정 창을 보여줘야 하는데
                    if (figure instanceof kr.goldenmine.models.Rectangle) {
                        kr.goldenmine.models.Rectangle rec = (kr.goldenmine.models.Rectangle) figure;
                        Vector3d start = new Vector3d(rec.getX(), rec.getY(), rec.getZ());
                        Vector3d finish = new Vector3d(start).add(rec.getXs(), rec.getYs(), rec.getZs());

                        Point startPoint = getMonitorPosition(start);
                        Point finishPoint = getMonitorPosition(finish);
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


    public Point getMonitorPosition(Vector3d v3d) {
        return v3d.getRotatePoint(xAngle, yAngle).get2DPoint(eye, mul).toPosition(buffer.getWidth() / 2.0, buffer.getHeight() / 2.0, onePixelSize);
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

        // 초기화
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        // 선들을 그려낸다
//        synchronized (storage.getFigures()) {
            for (Pair<Figure, Boolean> figureInfo : storage.getFigures()) {
                // 색깔을 설정하고
                Figure figure = figureInfo.getKey();
                if (figure instanceof Dot) { // Dot인 경우 특별하게 처리
                    g.setColor(Color.RED);
                    Dot obj = (Dot) figure;
                    Point monitorPos = getMonitorPosition(obj.getCurrentPos());

                    g.fillOval((int) monitorPos.x - obj.getRadius(), (int) monitorPos.y - obj.getRadius(), obj.getRadius() * 2, obj.getRadius() * 2);
                } else {
                    g.setColor(figure.getColor());

                    // 그려낼 점들의 위치를 받아온다.
                    List<Vector3d> figures = figure.getCoordinates();

                    for (int i = 0; i < figures.size(); i++) {
                        Vector3d position = figures.get(i);

                        // 현재 각도에 맞게 회전을 시켜준다. (3D 회전을 시킬때 x축을 회전하고 y축을 따로 회전시켜도 된다)
                        // get2DPoint: 3D좌표를 2D 모니터 화면에 투영
                        // toPosition: 쓴 좌표를 모니터의 픽셀 위치에 맞게 배치시켜준다.
                        // (실제 Point에는 (1,1) 이런 좌표가 들어있는데 이걸 모니터에 표현해야 하니까. 모니터의 (1,1) 좌표에 아무런 보정도 없이 그려버리면 눈뜨고 보기 쉽지 않을꺼다)
                        Point point = getMonitorPosition(position);

                        // 점에는 따로 사각형 모양을 그려준다. 없애면 그냥 선만 남는다.
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
                        Point p = getMonitorPosition(figures.get((int) point.x));
                        Point p2 = getMonitorPosition(figures.get((int) point.y));

                        // 선을 그려준다.
                        g.drawLine((int) p.x, (int) p.y, (int) p2.x, (int) p2.y);
                    }
                }
            }
//        }

        //Dot을 그려낸다.
//        g.setColor(Color.RED);
//        for(PhysicalObject obj : storage.getObjects()) {
//            Point monitorPos = getMonitorPosition(obj.getCurrentPos());
//
//            g.fillOval((int) monitorPos.x - 2, (int) monitorPos.y - 2, 5, 5);
//        }
    }
}
