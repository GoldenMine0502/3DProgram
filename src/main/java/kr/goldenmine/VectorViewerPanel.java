package kr.goldenmine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import kr.goldenmine.points.Point3D;

public class VectorViewerPanel extends JPanel {
    private JLabel vector = new JLabel();
    private JButton editButton = new JButton("Edit");
    private JCheckBox checkBox = new JCheckBox("");

    private int index = 0;

    private Lambda editButtonEvent;
    private Lambda checkBoxEvent;

    public VectorViewerPanel() {
        setPreferredSize(new Dimension(200, 60));
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //editButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        editButton.setPreferredSize(new Dimension(60, 30));

        JPanel east = new JPanel();
        east.add(editButton, "West");
        east.add(checkBox, "East");
        add(vector, "Center");
        add(east, "East");

        editButton.addActionListener(e-> editButtonEvent.accept());
        checkBox.addActionListener(e->checkBoxEvent.accept());
    }



    public void setEvent(Lambda lambda) {
        this.editButtonEvent = lambda;
    }

    public void setEventCheckbox(Lambda lambda) {
        this.checkBoxEvent = lambda;
    }

    public void setVector(int index, Point3D point) {
        this.index = index;
        vector.setText("(" + point.x + ", " + point.y + ", " + point.z + ")");
    }

    public int getIndex() {
        return index;
    }

    public void clearCheckBox() {
        checkBox.setSelected(false);
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    interface Lambda {
        void accept();
    }
}
