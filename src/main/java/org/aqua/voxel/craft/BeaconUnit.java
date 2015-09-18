package org.aqua.voxel.craft;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import org.aqua.graph.voxel.VoxelMatrixNode;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

public class BeaconUnit extends AbstractUnit {
    private Cone[]           arrows       = new Cone[3];
    private Cylinder[]       handles      = new Cylinder[3];
    private TransformGroup[] arrowGroups  = new TransformGroup[3];
    private TransformGroup[] handleGroups = new TransformGroup[3];
    public BeaconUnit() {
        super(UnitType.Beacon);
        Color3f black = new Color3f(Color.black);
        Color3f red = new Color3f(Color.red);
        Color3f green = new Color3f(Color.green);
        Color3f blue = new Color3f(Color.blue);

        int tMode = TransparencyAttributes.BLENDED;
        float tVal = 0.2f;
        int srcBlendFunction = TransparencyAttributes.BLEND_SRC_ALPHA;
        int dstBlendFunction = TransparencyAttributes.BLEND_ONE;
        TransparencyAttributes attributes = new TransparencyAttributes(tMode, tVal, srcBlendFunction, dstBlendFunction);

        for (int i = 0; i < 3; i++) {
            Appearance app = new Appearance();
            Transform3D arrowTransform = new Transform3D();
            Transform3D handleTransform = new Transform3D();
            switch (i) {
            case VoxelMatrixNode.IndexX:
                app.setMaterial(new Material(red, black, red, black, 1f));
                app.setTransparencyAttributes(attributes);
                arrowTransform.rotZ(Math.PI * 3 / 2);
                handleTransform.rotZ(Math.PI / 2);
                arrowTransform.setTranslation(new Vector3d(8, 0, 0));
                break;
            case VoxelMatrixNode.IndexY:
                app.setMaterial(new Material(green, black, green, black, 1f));
                arrowTransform.rotX(Math.PI / 2);
                handleTransform.rotX(Math.PI / 2);
                arrowTransform.setTranslation(new Vector3d(0, 0, 8));
                break;
            case VoxelMatrixNode.IndexZ:
                app.setMaterial(new Material(blue, black, green, blue, 1f));
                arrowTransform.setTranslation(new Vector3d(0, 8, 0));
                break;
            }
            arrows[i] = new Cone(unit / 3, unit, app);
            arrows[i].setPickable(false);
            handles[i] = new Cylinder(unit / 6, 16, app);
            handles[i].setPickable(false);
            arrowGroups[i] = new TransformGroup(arrowTransform);
            arrowGroups[i].addChild(arrows[i]);
            handleGroups[i] = new TransformGroup(handleTransform);
            handleGroups[i].addChild(handles[i]);
            transformGroup.addChild(arrowGroups[i]);
            transformGroup.addChild(handleGroups[i]);
        }
    }
}
