package org.aqua.voxel.craft;

import javax.media.j3d.Appearance;
import javax.media.j3d.TransparencyAttributes;

import com.sun.j3d.utils.geometry.Box;

public class CursorUnit extends AbstractUnit {
    private static float Edge = 0.01f;
    private Box          box;
    public CursorUnit() {
        float size = unit - Edge * 2;

        int tMode = TransparencyAttributes.BLENDED;
        float tVal = 0.5f;
        int srcBlendFunction = TransparencyAttributes.BLEND_SRC_ALPHA;
        int dstBlendFunction = TransparencyAttributes.BLEND_ONE;
        TransparencyAttributes attributes = new TransparencyAttributes(tMode, tVal, srcBlendFunction, dstBlendFunction);
        Appearance app = new Appearance();
        app.setTransparencyAttributes(attributes);
        box = new Box(size / 2, size / 2, size / 2, app);
        box.setPickable(false);

        contentBranch.addChild(box);
    }

}
