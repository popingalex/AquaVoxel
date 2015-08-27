package org.aqua.voxel.container;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3d;

import org.aqua.graph.voxel.VoxelMatrixNode;
import org.aqua.graph.voxel.VoxelMatrixRoot;
import org.aqua.graph.voxel.VoxelMatrixConstant;
import org.aqua.graph.voxel.VoxelMatrixUtil;
import org.aqua.voxel.craft.AbstractUnit;
import org.aqua.voxel.craft.CursorUnit;
import org.aqua.voxel.craft.ModelUnit;
import org.aqua.voxel.craft.WorkbenchUnit;

public class VoxelSandbox extends BranchGroup {
    private VoxelMatrixRoot workbenchRoot;
    private VoxelMatrixRoot modelRoot;
    private TransformGroup  scaleGroup;
    private TransformGroup  liftGroup;
    private CursorUnit      cursor;
    private AbstractUnit    focus;

    private int             floor = 0;
    private float           unit  = 0.2f;
    public VoxelSandbox() {
        AbstractUnit.unit = unit * 2;
        Transform3D liftTransform = new Transform3D();
        liftTransform.setTranslation(new Vector3d(new Vector3d(0, -unit - WorkbenchUnit.WorkbenchHeight / 2, 0)));
        liftGroup = new TransformGroup();
        liftGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        liftGroup.setTransform(liftTransform);

        scaleGroup = new TransformGroup();
        scaleGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        scaleGroup.addChild(liftGroup);
        addChild(scaleGroup);
        scale(10);
        cursor = new CursorUnit(unit);
        scaleGroup.addChild(cursor);
        workbenchRoot = new VoxelMatrixRoot() {

            @Override
            public void attachContent(VoxelMatrixNode node) {
                WorkbenchUnit content = new WorkbenchUnit(unit);
                content.setCoord3(node.coord3);
                liftGroup.addChild(content);
                content.rollover();
                node.content = content;
            }

            @Override
            public void removeContent(VoxelMatrixNode node) {
                // TODO Auto-generated method stub
                super.removeContent(node);
            }

        };
        modelRoot = new VoxelMatrixRoot() {
            @Override
            public void attachContent(VoxelMatrixNode node) {
                ModelUnit content = new ModelUnit(unit);
                content.setCoord3(node.coord3);
                scaleGroup.addChild(content);
                // content.rollover();
                node.content = content;
            }

            @Override
            public void removeContent(VoxelMatrixNode node) {
                // TODO Auto-generated method stub
                super.removeContent(node);
            }

        };
        // ((AbstractUnit)modelRoot.findNode(0, 0, 0).content).rollover();
        buildWorkbench(10);
    }

    private void buildWorkbench(int radius) {
        workbenchRoot.realloc(new int[] { -radius, 0, -radius }, new int[] { radius, 0, radius });
    }

    private void buildModel() {

    }

    public void buildCursor(int x, int y, int z, int direction) {

    }

    public void lift(int unit) {
        Transform3D liftTransform = new Transform3D();
        liftGroup.getTransform(liftTransform);
        Vector3d liftVector = new Vector3d();
        liftTransform.get(liftVector);
        liftVector.y += unit;
        floor += unit;
        liftTransform.setTranslation(liftVector);
        liftGroup.setTransform(liftTransform);
    }

    public void scale(int scale) {
        Transform3D scaleTransform = new Transform3D();
        scaleGroup.getTransform(scaleTransform);
        scaleTransform.setScale(Math.max(scaleTransform.getScale() + scale * 0.1f, 1));
        scaleGroup.setTransform(scaleTransform);
    }

    public void hover(Node pickNode, Node pickFace) {
        AbstractUnit pickUnit = null;
        if (pickNode != null) {         // 有pick对象
            pickUnit = (AbstractUnit) pickNode.getParent().getParent().getParent();
            if (focus == pickUnit) {
                // return;
            } else if (focus != null) { // blur旧焦点
                focus.blur();
            } else {                    // 点亮cursor
                cursor.rollover();
            }
            focus = pickUnit;
            if (pickUnit instanceof WorkbenchUnit) {
                pickUnit.focus();
                int[] coord3 = pickUnit.getCoord3();
                cursor.setCoord3(new int[] { coord3[0], coord3[1] + floor, coord3[2] });
            } else if (pickUnit instanceof ModelUnit) {
                ModelUnit model = (ModelUnit) pickUnit;
                int[] coord3 = model.getCoord3();
                int[] offset = model.getFaceNormal((Shape3D) pickFace);
                model.focus((Shape3D) pickFace);
                cursor.setCoord3(new int[] { coord3[0] + offset[0], coord3[1] + offset[1], coord3[2] + offset[2] });
            }
        } else if (focus != null) {     // 无pick有焦点
            focus.blur();
            focus = null;
            cursor.rollover();
        }
    }

    public void leftClick(Node pickNode, Node pickFace) {
        AbstractUnit pickUnit = null;
        pickUnit = (AbstractUnit) pickNode.getParent().getParent().getParent();
        if (pickUnit instanceof WorkbenchUnit) {
            int[] coord3 = pickUnit.getCoord3();
            coord3 = new int[] { coord3[0], coord3[1] + floor, coord3[2] };
            VoxelMatrixNode node = modelRoot.findNode(coord3);
            if (node == null) {
                int[] lower = modelRoot.lowerPoint.clone();
                int[] upper = modelRoot.upperPoint.clone();
                VoxelMatrixUtil.packagePoints(lower, upper, coord3);
                modelRoot.realloc(lower, upper);
                node = modelRoot.findNode(coord3);
            }
            ModelUnit unit = (ModelUnit)node.content;
            unit.rollover();
        } else if (pickUnit instanceof ModelUnit) {
            System.out.println("model");
        }
    }

    public void rightClick(Node pickNode, Node pickFace) {
        System.out.println("right");
    }

}