package org.aqua.voxel.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public abstract class VoxelEditor implements ActionListener {
    public static final String KEY_INPUT       = "key_input";
    public static final String FLOOR_RISE      = "cmd_floor_rise";
    public static final String FLOOR_FALL      = "cmd_floor_fall";
    public static final String WORLD_CENTER    = "cmd_world_center";
    public static final String WORLD_TURNLEFT  = "cmd_world_turnleft";
    public static final String WORLD_TURNRIGHT = "cmd_world_turnright";

    public static final String MODEL_IMPORT    = "model_import";
    public static final String MODEL_EXPORT    = "model_export";
    public static final String TEXTURE_LOAD    = "texture_load";

    private JFrame             frame;
    private KeyAdapter         adapter;

    public VoxelEditor() {
        adapter = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                actionPerformed(new ActionEvent(e, e.getID(), KEY_INPUT));
            }

        };

        // TODO JLIST CellRender for texture
        JPanel operatePanel = new JPanel();
        JPanel texturePanel = new JPanel();
        {
            JButton buttonRise = new JButton("上升");
            JButton buttonFall = new JButton("下降");
            JButton buttonLeft = new JButton("左转");
            JButton buttonRight = new JButton("右转");
            JButton buttonCenter = new JButton("原点");

            buttonCenter.setEnabled(false);

            buttonRise.setFocusable(false);
            buttonFall.setFocusable(false);
            buttonLeft.setFocusable(false);
            buttonRight.setFocusable(false);
            buttonCenter.setFocusable(false);

            buttonRise.setActionCommand(FLOOR_RISE);
            buttonFall.setActionCommand(FLOOR_FALL);
            buttonLeft.setActionCommand(WORLD_TURNLEFT);
            buttonRight.setActionCommand(WORLD_TURNRIGHT);
            buttonCenter.setActionCommand(WORLD_CENTER);

            buttonRise.addActionListener(this);
            buttonFall.addActionListener(this);
            buttonLeft.addActionListener(this);
            buttonRight.addActionListener(this);
            buttonCenter.addActionListener(this);

            buttonRise.setMargin(new Insets(0, 0, 0, 0));
            buttonFall.setMargin(new Insets(0, 0, 0, 0));
            buttonLeft.setMargin(new Insets(0, 0, 0, 0));
            buttonRight.setMargin(new Insets(0, 0, 0, 0));
            buttonCenter.setMargin(new Insets(0, 0, 0, 0));

            buttonRise.setMargin(new Insets(0, 0, 0, 0));
            buttonFall.setMargin(new Insets(0, 0, 0, 0));
            buttonLeft.setMargin(new Insets(0, 0, 0, 0));
            buttonRight.setMargin(new Insets(0, 0, 0, 0));
            buttonCenter.setMargin(new Insets(0, 0, 0, 0));

            JPanel navigateBoxPanel = new JPanel(new GridLayout(3, 3, 3, 3));
            navigateBoxPanel.setPreferredSize(new Dimension(120, 120));
            navigateBoxPanel.add(Box.createGlue());
            navigateBoxPanel.add(buttonRise);
            navigateBoxPanel.add(Box.createGlue());
            navigateBoxPanel.add(buttonLeft);
            navigateBoxPanel.add(buttonCenter);
            navigateBoxPanel.add(buttonRight);
            navigateBoxPanel.add(Box.createGlue());
            navigateBoxPanel.add(buttonFall);
            navigateBoxPanel.add(Box.createGlue());

            JPanel navigatePanel = new JPanel();
            navigatePanel.setBorder(BorderFactory.createTitledBorder("Navigator"));
            navigatePanel.add(navigateBoxPanel);
            operatePanel.add(navigatePanel);
        }
        {
            JButton importButton = new JButton("导入");
            JButton exportButton = new JButton("导出");
            JButton textureButton = new JButton("加载纹理");

            textureButton.setEnabled(false);

            importButton.setActionCommand(MODEL_IMPORT);
            exportButton.setActionCommand(MODEL_EXPORT);
            textureButton.setActionCommand(TEXTURE_LOAD);

            importButton.addActionListener(this);
            exportButton.addActionListener(this);
            textureButton.addActionListener(this);

            importButton.setMargin(new Insets(0, 0, 0, 0));
            exportButton.setMargin(new Insets(0, 0, 0, 0));
            textureButton.setMargin(new Insets(0, 0, 0, 0));

            importButton.setMargin(new Insets(0, 0, 0, 0));
            exportButton.setMargin(new Insets(0, 0, 0, 0));
            textureButton.setMargin(new Insets(0, 0, 0, 0));

            JPanel functionPanel = new JPanel();
            functionPanel.setBorder(BorderFactory.createTitledBorder("Function"));
            functionPanel.add(importButton);
            functionPanel.add(exportButton);
            functionPanel.add(textureButton);
            operatePanel.add(functionPanel);
        }
        {
            JList textureList = new JList();
            textureList.setPreferredSize(new Dimension(40, 40));
            textureList.setListData(new String[] { "现在就石头" });
            textureList.setFocusable(false);

            texturePanel.setBorder(new TitledBorder("Texture"));
            texturePanel.add(textureList);
            texturePanel.setFocusable(false);
        }
        operatePanel.setPreferredSize(new Dimension(140, 360));
        operatePanel.setFocusable(false);
        texturePanel.setFocusable(false);

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(operatePanel, BorderLayout.EAST);
        frame.add(texturePanel, BorderLayout.SOUTH);
        frame.setBounds(20, 20, 800, 600);

        frame.getContentPane().addKeyListener(adapter);
    }

    public void setCanvas(Canvas canvas) {
        frame.add(canvas, BorderLayout.CENTER);
        canvas.addKeyListener(adapter);
    }

    public void display() {
        frame.setVisible(true);
        frame.getContentPane().requestFocus();
    }

}
