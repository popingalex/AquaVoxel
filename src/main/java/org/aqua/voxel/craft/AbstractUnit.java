package org.aqua.voxel.craft;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import org.aqua.graph.voxel.VoxelMatrixNode.VoxelMatrixContent;
import org.aqua.structure.space.SpaceNode;

public class AbstractUnit extends BranchGroup implements SpaceNode.Content {
    public static float unit = 1f;

    public enum UnitType {
        Cursor, Beacon, Model, Workbench
    }
    protected TransformGroup transformGroup = new TransformGroup();
    private boolean          visible        = false;
    private int[]            coord3         = new int[3];
    private BranchGroup      parent;
    private final UnitType   type;

    public AbstractUnit(UnitType type) {
        this.type = type;
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        transformGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        addChild(transformGroup);

        // transformGroup.addChild(shellBranch);
        // contentBranch.setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(Group.ALLOW_CHILDREN_WRITE);
        setCapability(Group.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_DETACH);
        // shellBranch.setCapability(Group.ALLOW_CHILDREN_WRITE);
        // shellBranch.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        // TODO
        RenderingAttributes attr = new RenderingAttributes();
        attr.setVisible(false);
        // setPickable(false);
    }

    public final UnitType getType() {
        return type;
    }

    public final int[] getCoord3() {
        return coord3.clone();
    }

    public final void setCoord3(int[] coord3) {
        this.coord3 = coord3;
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(coord3[0] * unit, coord3[1] * unit, coord3[2] * unit));
        transformGroup.setTransform(trans);
    }

    public final boolean isVisible() {
        return visible;
    }

    /**
     * 获得焦点
     */
    public void focus() {
    }

    /**
     * 失去焦点
     */
    public void blur() {
    }

    public void setParent(BranchGroup parent) {
        this.parent = parent;
    }
    /**
     * 翻转
     */
    public void rollover() {
        visible = !visible;
    }
    /**
     * 获得对应面的id
     * 
     * @param pickFace
     * @return
     */
    public int getFaceID(Shape3D pickFace) {
        return 0;
    }

    @Override
    public String serialize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deserialize(String value) {
        // TODO Auto-generated method stub

    }
}