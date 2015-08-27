package org.aqua.voxel.craft;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Box;

public class ModelUnit extends AbstractUnit {
    private static PolygonAttributes EdgePolygonAttributes;
    private static float             Edge = 0.01f;
    private Box                      voxelBox;
    private Box                      edgedBox;

    static {
        EdgePolygonAttributes = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.1f);
    }

    public ModelUnit(float size) {
        
        float sizeVoxel = size - Edge * 2;
        float sizeDecor = size - Edge;
        voxelBox = new Box(sizeVoxel, sizeVoxel, sizeVoxel, null);
        edgedBox = new Box(sizeDecor, sizeDecor, sizeDecor, null);
        edgedBox.setPickable(false);
        edgedBox.getAppearance().setPolygonAttributes(EdgePolygonAttributes);

        Appearance app = new Appearance();
        Color3f ambient = new Color3f(Color.gray);// 环境光
        Color3f emissive = new Color3f(Color.black);// 自发光
        Color3f diffuse = new Color3f(Color.gray);// 扩散的
        Color3f specular = new Color3f(Color.black);// 反射的

        app.setMaterial(new Material(ambient, emissive, diffuse, specular, 0.8f));
        voxelBox.setAppearance(app);

        contentBranch.addChild(edgedBox);
        contentBranch.addChild(voxelBox);
//        unitBranch.addChild(voxelBox);
//        unitBranch.addChild(edgedBox);
    }

    public int[] getFaceNormal(Shape3D pickFace) {
        if (voxelBox.getShape(Box.TOP) == pickFace) {
            return new int[]{0, 1, 0};
        } else if (voxelBox.getShape(Box.BOTTOM) == pickFace) {
            return new int[]{0, -1, 0};
        } else if (voxelBox.getShape(Box.LEFT) == pickFace) {
            return new int[]{-1, 0, 0};
        } else if (voxelBox.getShape(Box.RIGHT) == pickFace) {
            return new int[]{1, 0, 0};
        } else if (voxelBox.getShape(Box.FRONT) == pickFace) {
            return new int[]{0, 0, 1};
        } else if (voxelBox.getShape(Box.BACK) == pickFace) {
            return new int[]{0, 0, -1};
        } else {
            return new int[3];
        }
    }

    public void focus(Shape3D pickFace) {
        // TODO Auto-generated method stub
        
    }

}
