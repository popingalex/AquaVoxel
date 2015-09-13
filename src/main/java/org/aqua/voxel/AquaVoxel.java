package org.aqua.voxel;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JOptionPane;

import org.aqua.voxel.container.VoxelSandbox;
import org.aqua.voxel.container.VoxelUniverse;
import org.aqua.voxel.craft.AbstractUnit;
import org.aqua.voxel.gui.VoxelEditor;

public class AquaVoxel {
    private VoxelEditor   editor;
    private VoxelSandbox  sandbox;
    private VoxelUniverse universe;

    public AquaVoxel() {
        universe = new VoxelUniverse() {

            @Override
            public void picking(AbstractUnit pickUnit, int id, MouseEvent mevent) {
                switch (mevent.getButton()) {
                case 0:
                    sandbox.hover(pickUnit, id);
                    break;
                case MouseEvent.BUTTON1:
                    sandbox.leftClick(pickUnit, id);
                    break;
                case MouseEvent.BUTTON3:
                    sandbox.rightClick(pickUnit, id);
                    break;
                }
            }

        };
        sandbox = new VoxelSandbox() {

            @Override
            public void addChild(Object node, String branch) {
                universe.addChild(node, branch);
            }

            @Override
            public void removeChild(Object node, String branch) {
                universe.removeChild(node, branch);
            }

        };

        Canvas canvas = universe.getCanvas();
        canvas.setFocusable(false);
        canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                universe.scale(-e.getWheelRotation());
            }
        });

        editor = new VoxelEditor() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if (command.equals(VoxelEditor.KEY_INPUT)) {
                    KeyEvent event = (KeyEvent) e.getSource();
                    if (event.getModifiers() == KeyEvent.SHIFT_MASK) {
                        switch (event.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            sandbox.floor++;
                            universe.translate(new float[] { 0, AbstractUnit.unit, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
                            break;
                        case KeyEvent.VK_DOWN:
                            sandbox.floor--;
                            universe.translate(new float[] { 0, -AbstractUnit.unit, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
                            break;
                        case KeyEvent.VK_LEFT:
                            universe.rotate(-Math.PI / 60);
                            break;
                        case KeyEvent.VK_RIGHT:
                            universe.rotate(Math.PI / 60);
                            break;
                        }
                    } else {
                        switch (event.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            universe.translate(new float[] { 0, 0, AbstractUnit.unit }, VoxelSandbox.BRANCH_WORKBENCH);
                            break;
                        case KeyEvent.VK_DOWN:
                            universe.translate(new float[] { 0, 0, -AbstractUnit.unit }, VoxelSandbox.BRANCH_WORKBENCH);
                            break;
                        case KeyEvent.VK_LEFT:
                            universe.translate(new float[] { -AbstractUnit.unit, 0, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
                            break;
                        case KeyEvent.VK_RIGHT:
                            universe.translate(new float[] { AbstractUnit.unit, 0, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
                            break;
                        }
                    }
                } else if (command.equals(VoxelEditor.FLOOR_RISE)) {
                    sandbox.floor++;
                    universe.translate(new float[] { 0, AbstractUnit.unit, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
                } else if (command.equals(VoxelEditor.FLOOR_FALL)) {
                    sandbox.floor--;
                    universe.translate(new float[] { 0, -AbstractUnit.unit, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
                } else if (command.equals(VoxelEditor.WORLD_TURNLEFT)) {
                    universe.rotate(-Math.PI / 60);
                } else if (command.equals(VoxelEditor.WORLD_TURNRIGHT)) {
                    universe.rotate(Math.PI / 60);
                } else if (command.equals(VoxelEditor.MODEL_IMPORT)) {
                    sandbox.importModel(JOptionPane.showInputDialog("把数据贴进来"));
                } else if (command.equals(VoxelEditor.MODEL_EXPORT)) {
                    JOptionPane.showInputDialog("把数据拷走", sandbox.exportModel());
                }
            }

        };
        editor.setCanvas(canvas);
        editor.display();
    }

    public void build() {
        universe.compile();
        universe.translate(new float[] { 0, -sandbox.floorOffset, 0 }, VoxelSandbox.BRANCH_WORKBENCH);
    }

    public static void main(String[] args) {
        new AquaVoxel().build();
    }

}
