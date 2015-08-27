package org.aqua.voxel.craft;

import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;

import com.sun.j3d.utils.geometry.Box;

public class WorkbenchUnit extends AbstractUnit {
    public static float       WorkbenchHeight = 0.002f;
    private static float      Edge            = 0.01f;
    private static Appearance HighlightAppearance;
    private static Appearance NormalAppearance;
    private Box               workbenchBox;

    {
        int tMode = TransparencyAttributes.BLENDED;
        float tVal = 0.5f;
        int srcBlendFunction = TransparencyAttributes.BLEND_SRC_ALPHA;
        int dstBlendFunction = TransparencyAttributes.BLEND_ONE;
        TransparencyAttributes attributes = new TransparencyAttributes(tMode, tVal, srcBlendFunction, dstBlendFunction);
        NormalAppearance = new Appearance();
        NormalAppearance.setTransparencyAttributes(attributes);
        HighlightAppearance = new Appearance();
    }

    public WorkbenchUnit(float size) {
        size = size - Edge * 2;
        workbenchBox = new Box(size, WorkbenchHeight, size, NormalAppearance);
        workbenchBox.getShape(Box.TOP).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        
        contentBranch.addChild(workbenchBox);
//        unitBranch.addChild(workbenchBox);
    }

    @Override
    public void focus() {
        workbenchBox.getShape(Box.TOP).setAppearance(HighlightAppearance);
    }

    @Override
    public void blur() {
        workbenchBox.getShape(Box.TOP).setAppearance(NormalAppearance);
    }

}