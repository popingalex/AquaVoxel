package org.aqua.voxel.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.aqua.voxel.behavior.PickBehavior;
import org.aqua.voxel.container.VoxelSandbox;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class VoxelMatrixViewer {

    private VoxelSandbox sandbox;

    public VoxelMatrixViewer() {
        sandbox = new VoxelSandbox();

        JFrame frame = new JFrame();
        frame.add(buildCanvas());
        frame.setBounds(20, 20, 480, 360);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private Canvas3D buildCanvas() {
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                sandbox.scale(-e.getWheelRotation());
            }
        });
        BoundingSphere behaviorBounding = new BoundingSphere(new Point3d(), 200);
        SimpleUniverse universe = new SimpleUniverse(canvas);

        BranchGroup rootGroup = new BranchGroup();
        TransformGroup elevationGroup = new TransformGroup();
        TransformGroup deflectionGroup = new TransformGroup();

        Transform3D elevationTrans = new Transform3D();
        Transform3D deflectionTrans = new Transform3D();

        PickMouseBehavior pickBehavior = new PickBehavior(canvas, rootGroup, behaviorBounding) {
            @Override
            public void picking(Node pickNode, Node pickFace, MouseEvent event) {
                switch (event.getButton()) {
                case 0:                 // 鼠标移动
                    sandbox.hover(pickNode, pickFace);
                    break;
                case MouseEvent.BUTTON1:// 鼠标左击
                    sandbox.leftClick(pickNode, pickFace);
                    break;
                case MouseEvent.BUTTON3:// 鼠标右击
                    sandbox.rightClick(pickNode, pickFace);
                    break;
                }
            }
            
        };
        MouseRotate rotateBehavior = new MouseRotate(deflectionGroup) {

            @Override
            public void processMouseEvent(MouseEvent evt) {
                y_last = evt.getY();
                super.processMouseEvent(evt);
            }

        };

        AmbientLight ambientLight = new AmbientLight(new Color3f(Color.white));
        ambientLight.setInfluencingBounds(behaviorBounding);

        DirectionalLight directionalLight = new DirectionalLight(new Color3f(Color.white), new Vector3f(40f, 40f, 40f));
        directionalLight.setInfluencingBounds(behaviorBounding);

        pickBehavior.setSchedulingBounds(behaviorBounding);
        rotateBehavior.setSchedulingBounds(behaviorBounding);

        elevationGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        elevationTrans.setRotation(new AxisAngle4d(1, 0, 0, Math.PI / 6));
        elevationGroup.setTransform(elevationTrans);
        elevationGroup.addChild(deflectionGroup);

        deflectionGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        deflectionTrans.setRotation(new AxisAngle4d(0, 1, 0, Math.PI / 4));
        deflectionGroup.setTransform(deflectionTrans);
        deflectionGroup.addChild(sandbox);

        sandbox.addChild(rotateBehavior);
        sandbox.addChild(pickBehavior);

        rootGroup.addChild(elevationGroup);
        rootGroup.addChild(directionalLight);
        rootGroup.addChild(ambientLight);

        Transform3D viewTrans = new Transform3D();
        viewTrans.setTranslation(new Vector3d(0f, 0f, 40f));

        universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);
        universe.addBranchGraph(rootGroup);

        return canvas;
    }

}
