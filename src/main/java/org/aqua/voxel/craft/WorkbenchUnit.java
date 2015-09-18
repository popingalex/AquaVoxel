package org.aqua.voxel.craft;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Box;

public class WorkbenchUnit extends AbstractUnit {
    public static float       WorkbenchHeight = 0.002f;
    private static float      Edge            = 0.01f;
    private static Appearance HighlightAppearance;
    private static Appearance NormalAppearance;
    private Box               workbenchBox;

    {
        int tMode = TransparencyAttributes.BLENDED;
        float tVal = 0.8f;
        int srcBlendFunction = TransparencyAttributes.BLEND_SRC_ALPHA;
        int dstBlendFunction = TransparencyAttributes.BLEND_ONE;
        TransparencyAttributes attributes = new TransparencyAttributes(tMode, tVal, srcBlendFunction, dstBlendFunction);
        NormalAppearance = new Appearance();
        Color3f ambient = new Color3f(Color.cyan);  // 环境光
        Color3f emissive = new Color3f(Color.black);// 自发光
        Color3f diffuse = new Color3f(Color.cyan);  // 扩散光
        Color3f specular = new Color3f(Color.black);// 反射光

        NormalAppearance.setMaterial(new Material(ambient, emissive, diffuse, specular, 0.2f));
        NormalAppearance.setTransparencyAttributes(attributes);
        HighlightAppearance = new Appearance();
    }

    public WorkbenchUnit() {
        super(UnitType.Workbench);
        float size = unit - Edge * 2;
        workbenchBox = new Box(size / 2, WorkbenchHeight, size / 2, NormalAppearance);
        workbenchBox.getShape(Box.TOP).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        transformGroup.addChild(workbenchBox);
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