package kr.goldenmine;

import java.awt.*;
import javax.swing.*;

public class GoldenList extends JPanel {
    //private TableBorder border;

    private JPanel panel = new JPanel();
    private JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private JLabel bigLabel = new JLabel();

    public GoldenList() {
        setLayout(new BorderLayout());

        // gridbaglayout은 x,y좌표를 이용해 컴포넌트를 배치할 수 있게 해준다
        // 비율이나 마진 등 다양한 옵션도 있다
        // 옵션별 설명은 검색하면 나온다
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
            //constraints.fill = GridBagConstraints.NORTHWEST;
            // 이 옵션이 왼쪽 위를 기준으로 늘리는건가 줄이는건가 일겁니다
            constraints.anchor = GridBagConstraints.NORTHWEST;

            // (0, y)로 설정
            constraints.gridx = 0;
            constraints.gridy = panel.getComponentCount();

            // 패널 추가하기
            panel.add(component, constraints);
        }

        // weight는 비어있는 공간이 있으면 비어있는 공간을 얼마나 비율로 채울 것인가를 정하는 겁니다
        // weight = 1이면 100%
        // 0.5면 50%

        // 이걸 왜 추가했냐면 이게 없으면 가운데서부터 컴포넌트들이 나옵니다
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
