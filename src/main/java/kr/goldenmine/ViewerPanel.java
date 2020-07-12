package kr.goldenmine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;

public class ViewerPanel extends JPanel {
    private JLabel title = new JLabel();
    private JButton editButton = new JButton("Edit");
    private JCheckBox checkBox = new JCheckBox("");

    private int loopIndex = 0;

    private Lambda editButtonEvent;
    private Lambda checkBoxEvent;

    public ViewerPanel() {
        setPreferredSize(new Dimension(200, 60));
        setLayout(new BorderLayout());

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //editButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        editButton.setPreferredSize(new Dimension(60, 30));

        JPanel east = new JPanel();
        east.add(editButton, "West");
        east.add(checkBox, "East");
        add(title, "Center");
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

    public void setTitle(int index, String text) {
        this.loopIndex = index;
        title.setText(text);
        //        vector.setText("(" + point.getX() + ", " + point.getY() + ", " + point.getZ() + ")");
    }

    public int getLoopIndex() {
        return loopIndex;
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
