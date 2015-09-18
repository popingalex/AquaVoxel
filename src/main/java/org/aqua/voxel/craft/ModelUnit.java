package org.aqua.voxel.craft;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;

public class ModelUnit extends AbstractUnit {
    public static final int[][]      FACE_OFFSET = new int[6][];
    private static PolygonAttributes EdgePolygonAttributes;
    private static float             Edge        = 0.05f;
    private Box                      voxelBox;
    private Box                      edgedBox;
    public int                       content     = 1;

    static {
        FACE_OFFSET[Box.FRONT] = new int[] { 0, 0, 1 };
        FACE_OFFSET[Box.BACK] = new int[] { 0, 0, -1 };
        FACE_OFFSET[Box.RIGHT] = new int[] { 1, 0, 0 };
        FACE_OFFSET[Box.LEFT] = new int[] { -1, 0, 0 };
        FACE_OFFSET[Box.TOP] = new int[] { 0, 1, 0 };
        FACE_OFFSET[Box.BOTTOM] = new int[] { 0, -1, 0 };
        EdgePolygonAttributes = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.1f);
    }

    public ModelUnit() {
        super(UnitType.Model);
        float sizeVoxel = unit - Edge * 2;
        float sizeDecor = unit - Edge;
        voxelBox = new Box(sizeVoxel / 2, sizeVoxel / 2, sizeVoxel / 2, Box.GENERATE_TEXTURE_COORDS, null);
        edgedBox = new Box(sizeDecor / 2, sizeDecor / 2, sizeDecor / 2, null);
        edgedBox.setPickable(false);
        edgedBox.getAppearance().setPolygonAttributes(EdgePolygonAttributes);

        Appearance app = new Appearance();
        Color3f ambient = new Color3f(Color.gray);  // 环境光
        Color3f emissive = new Color3f(Color.black);// 自发光
        Color3f diffuse = new Color3f(Color.gray);  // 扩散光
        Color3f specular = new Color3f(Color.black);// 反射光

        // app.setMaterial(new Material(ambient, emissive, diffuse, specular, 0.8f));
        app.setMaterial(new Material());
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("src/main/resources/base.jpg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextureLoader loader = new TextureLoader(image);
        Texture texture = loader.getTexture();
        TextureAttributes attr = new TextureAttributes();
        attr.setTextureMode(TextureAttributes.MODULATE);
        // app.setTextureAttributes(attr);
        app.setTexture(texture);

        voxelBox.setAppearance(app);

        transformGroup.addChild(edgedBox);
        transformGroup.addChild(voxelBox);

    }

    @Override
    public int getFaceID(Shape3D pickFace) {
        if (voxelBox.getShape(Box.FRONT) == pickFace) {
            return Box.FRONT;
        } else if (voxelBox.getShape(Box.BACK) == pickFace) {
            return Box.BACK;
        } else if (voxelBox.getShape(Box.RIGHT) == pickFace) {
            return Box.RIGHT;
        } else if (voxelBox.getShape(Box.LEFT) == pickFace) {
            return Box.LEFT;
        } else if (voxelBox.getShape(Box.TOP) == pickFace) {
            return Box.TOP;
        } else if (voxelBox.getShape(Box.BOTTOM) == pickFace) {
            return Box.BOTTOM;
        } else {
            return -1;
        }
    }

    public void focus(int id) {
        // TODO Auto-generated method stub
    }

    // @Override
    // public int serialize() {
    // return isVisible() ? content : 0;
    // }
    //
    // @Override
    // public void deserialize(int value) {
    // content = value;
    // if (0 == value && isVisible() || 0 < value && !isVisible()) {
    // rollover();
    // }
    // }

}
