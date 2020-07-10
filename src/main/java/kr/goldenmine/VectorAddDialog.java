package kr.goldenmine;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;
import kr.goldenmine.points.Point3D;

public class VectorAddDialog extends JDialog {
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private JLabel xPosLabel = new JLabel("x: ");
    private JLabel yPosLabel = new JLabel("y: ");
    private JLabel zPosLabel = new JLabel("z: ");

    // 자바에서 기본 지원하는 컬러 선택 유틸이다.
    private JColorChooser colorChooser = new JColorChooser(Color.BLACK);

    private JTextField xPosField = new JTextField();
    private JTextField yPosField = new JTextField();
    private JTextField zPosField = new JTextField();

    public VectorAddDialog(BiConsumer<Point3D, Color> ok) {
        // 맨위를 Border로 만들어서 5조각으로 화면을 나누고
        setLayout(new BorderLayout());

        // 윗부분에 놓을 패널을 설정할 것이다
        JPanel panelTop = new JPanel();
        // 뭐 이정도는 아시겠지만 FlowLayout은 배치하면 그 옆에 컴포넌트를 차곡차곡 쌓는 레이아웃이다.
        // (Border는 5조각으로 나누는 반면)
        panelTop.setLayout(new FlowLayout());

        panelTop.add(xPosLabel);
        panelTop.add(xPosField);
        panelTop.add(yPosLabel);
        panelTop.add(yPosField);
        panelTop.add(zPosLabel);
        panelTop.add(zPosField);

        // 이걸 안하면 텍스트필드가 너무 작아서 글씨를 입력할 수 없으니 적당한 크기로 설정해준다
        xPosField.setPreferredSize(new Dimension(40, 20));
        yPosField.setPreferredSize(new Dimension(40, 20));
        zPosField.setPreferredSize(new Dimension(40, 20));

        // 아랫부분에 놓을 패널을 설정한다 확인과 취소버튼을 놓았다.
        JPanel panelBottom = new JPanel();
        panelBottom.add(okButton);
        panelBottom.add(cancelButton);

        // 이제 이 패널들을 적용시켜준다.
        add(panelTop, "North");
        add(colorChooser, "Center");
        add(panelBottom, "South");

        // ok 버튼을 눌렀을 때 다음과 같은 실행을 한다
        okButton.addActionListener(e-> {
            // 콜백.
            // colorChooser.getColor()를 하면 컬러 선택창에서 설정한 색깔을 얻어올 수 있다.
            // TextField에서 값을 입력하고 불러온 값들은 기본적으로 String이기 때문에 Double이나 Integer로 바꿔줘야 숫자로서의 기능을 할 수 있게 된다.
            ok.accept(new Point3D(Double.parseDouble(xPosField.getText()), Double.parseDouble(yPosField.getText()), Double.parseDouble(zPosField.getText())), colorChooser.getColor());

            //창을 숨긴다
            cancel();
        });

        // cancel 버튼을 눌렀을 때
        cancelButton.addActionListener(e -> cancel());

        // X버튼 또는 Alt + F4를 눌렀을때 창을 숨긴다
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                cancel();
            }
        });

        // 사이즈 설정
        setSize(500, 400);
        setPreferredSize(new Dimension(500, 400));
    }

    public void setVector(Point3D point, Color color) {
        xPosField.setText(String.valueOf(point.x));
        yPosField.setText(String.valueOf(point.y));
        zPosField.setText(String.valueOf(point.z));
        colorChooser.setColor(color);
    }

    // 이 창을 숨긴다
    private void cancel() {
        xPosField.setText("");
        yPosField.setText("");
        zPosField.setText("");
        setVisible(false);
    }
}
