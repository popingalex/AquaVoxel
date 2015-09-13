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

public class AbstractUnit extends BranchGroup implements VoxelMatrixContent {
    protected TransformGroup transformGroup = new TransformGroup();
    protected BranchGroup    contentBranch  = new BranchGroup();
    private BranchGroup      shellBranch    = new BranchGroup();
    private boolean          visible        = false;
    private int[]            coord3         = new int[3];
    public static float      unit           = 1f;

    public AbstractUnit() {
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        transformGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        
        transformGroup.addChild(shellBranch);
        addChild(transformGroup);

        contentBranch.setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(Group.ALLOW_CHILDREN_WRITE);
        setCapability(Group.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_DETACH);
        shellBranch.setCapability(Group.ALLOW_CHILDREN_WRITE);
        shellBranch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        
        // TODO
        RenderingAttributes attr = new RenderingAttributes();
        attr.setVisible(false);
//        setPickable(false);
    }

    public int[] getCoord3() {
        return coord3.clone();
    }

    public void setCoord3(int[] coord3) {
        this.coord3 = coord3;
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(coord3[0] * unit, coord3[1] * unit, coord3[2] * unit));
        transformGroup.setTransform(trans);
    }

    public boolean isVisible() {
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

    /**
     * 翻转
     */
    public void rollover() {
        // TODO detach和remove的区别是?
        if (visible) {
  
            shellBranch.removeChild(contentBranch);
        } else {
            shellBranch.addChild(contentBranch);
        }
        visible = !visible;
    }
    /**
     * 获得对应面的id
     * @param pickFace
     * @return
     */
    public int getFaceID(Shape3D pickFace) {
        return 0;
    }

    @Override
    public int serialize() {
        return 0;
    }

    @Override
    public void deserialize(int value) {
    }
    
}