package kr.goldenmine.graphic.util;

import java.awt.*;
import javax.swing.*;

public class GoldenList extends JPanel {
    //private TableBorder border;

    private JPanel panel = new JPanel();
    private JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private JLabel bigLabel = new JLabel();

    public GoldenList() {
        setLayout(new BorderLayout());
        panel.setLayout(new GridBagLayout());

        add(pane, "Center");
    }

    public JScrollPane getScrollPane() {
        return pane;
    }

    public void addElement(Component component) {
        // 아래에 추가했던 공간채우기용 JLabel 지우기
        if(panel.getComponentCount() > 0) {
            panel.remove(panel.getComponentCount() - 1);
        }

        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;

            // (0, y)로 설정
            constraints.gridx = 0;
            constraints.gridy = panel.getComponentCount();

            // 패널 추가하기
            panel.add(component, constraints);
        }

        // 위에서부터 컴포넌트들이 나오도록
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.gridx = 0;
            constraints.gridy = panel.getComponentCount() + 1;
            constraints.weightx = 1;
            constraints.weighty = 1;
            panel.add(bigLabel, constraints);
        }
    }

    public int listSize() {
        return panel.getComponentCount() - 1;
    }

    public Component getElement(int index) {
        return panel.getComponent(index);
    }
}
