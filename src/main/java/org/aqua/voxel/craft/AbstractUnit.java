package org.aqua.voxel.craft;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

public class AbstractUnit extends TransformGroup {
    protected BranchGroup contentBranch = new BranchGroup();
    private BranchGroup   shellBranch   = new BranchGroup();
    private boolean       visible       = false;
    private int[]         coord3        = new int[3];
    public static float   unit          = 1f;

    public AbstractUnit() {
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        setCapability(Group.ALLOW_CHILDREN_WRITE);
        setCapability(Group.ALLOW_CHILDREN_EXTEND);

        shellBranch.setCapability(BranchGroup.ALLOW_DETACH);
        shellBranch.setCapability(Group.ALLOW_CHILDREN_WRITE);
        shellBranch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    }

    public int[] getCoord3() {
        return coord3.clone();
    }

    public void setCoord3(int[] coord3) {
        this.coord3 = coord3;
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(coord3[0] * unit, coord3[1] * unit, coord3[2] * unit));
        setTransform(trans);
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
        if (visible) {
            // TODO detach和remove的区别是?
            // shellBranch.detach();
            removeChild(shellBranch);
            shellBranch.removeChild(contentBranch);
        } else {
            shellBranch.addChild(contentBranch);
            addChild(shellBranch);
        }
        visible = !visible;
    }

}