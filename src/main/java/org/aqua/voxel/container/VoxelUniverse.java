package org.aqua.voxel.container;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.aqua.voxel.craft.AbstractUnit;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

public abstract class VoxelUniverse {
    public static final String         BRANCH_ARROW = "ARROW";
    protected Map<String, BranchGroup> groupMap;
    private Canvas3D                   canvas;
    private BranchGroup                baseGroup;
    private BranchGroup                rootGroup;
    /** 仰角变换 */
    private TransformGroup             elevationGroup;
    /** 偏角变换 */
    private TransformGroup             deflectionGroup;
    /** 缩放变换 */
    private TransformGroup             scaleGroup;

    // TODO add decorGroup
    public VoxelUniverse() {
        BoundingSphere behaviorBounding = new BoundingSphere(new Point3d(), 200);
        rootGroup = new BranchGroup();
        baseGroup = new BranchGroup();
        groupMap = new HashMap<String, BranchGroup>();
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());

        {
            scaleGroup = new TransformGroup();
            scaleGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            scaleGroup.addChild(baseGroup);
        }
        {
            deflectionGroup = new TransformGroup();
            deflectionGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            deflectionGroup.addChild(scaleGroup);
            {
                BranchGroup cursorGroup = new BranchGroup();
                cursorGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
                TransformGroup transform = new TransformGroup();
                transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                transform.addChild(cursorGroup);
                deflectionGroup.addChild(transform);
                groupMap.put(BRANCH_ARROW, cursorGroup);
            }
        }
        {
            elevationGroup = new TransformGroup();
            elevationGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            elevationGroup.addChild(deflectionGroup);
        }
        {   // TODO to review
            AmbientLight ambientLight = new AmbientLight(new Color3f(Color.white));
            ambientLight.setInfluencingBounds(behaviorBounding);

            DirectionalLight directionalLight = new DirectionalLight(new Color3f(Color.white), new Vector3f(40f, 40f,
                    40f));
            directionalLight.setInfluencingBounds(behaviorBounding);

            rootGroup.addChild(directionalLight);
            rootGroup.addChild(ambientLight);
            rootGroup.addChild(elevationGroup);

        }
        {
            PickMouseBehavior pickBehavior = new PickMouseBehavior(canvas, rootGroup, behaviorBounding) {

                @SuppressWarnings("rawtypes")
                @Override
                public void processStimulus(Enumeration criteria) {
                    super.processStimulus(criteria);
                    if (mevent.getID() == MouseEvent.MOUSE_MOVED) {
                        updateScene(mevent.getX(), mevent.getY());
                    }
                }

                @Override
                public void updateScene(int xpos, int ypos) {
                    pickCanvas.setShapeLocation(xpos, ypos);
                    PickResult result = pickCanvas.pickClosest();
                    if (result != null) {
                        Node pickNode = result.getNode(PickResult.PRIMITIVE);
                        for (; pickNode != null && !(pickNode instanceof AbstractUnit); pickNode = pickNode.getParent()) {
                        }
                        if (pickNode instanceof AbstractUnit) {
                            AbstractUnit pickUnit = (AbstractUnit) pickNode;
                            int pickFace = pickUnit.getFaceID((Shape3D) result.getNode(PickResult.SHAPE3D));
                            picking(pickUnit, pickFace, mevent);
                        }
                    } else {
                        picking(null, 0, mevent);
                    }
                }
            };
            MouseRotate rotateBehavior = new MouseRotate(deflectionGroup) {

                @Override
                public void processMouseEvent(MouseEvent evt) {
                    y_last = evt.getY();
                    super.processMouseEvent(evt);
                }

            };

            pickBehavior.setMode(PickTool.GEOMETRY);
            pickBehavior.setSchedulingBounds(behaviorBounding);
            rotateBehavior.setSchedulingBounds(behaviorBounding);
            baseGroup.addChild(pickBehavior);
            baseGroup.addChild(rotateBehavior);
        }

        scale(10);
        deflect(Math.PI / 4);
        eleveate(Math.PI / 6);
    }

    public void compile() {
        {
            Transform3D stadiaTrans = new Transform3D();
            stadiaTrans.setTranslation(new Vector3d(0f, 0f, 40f)); // distance to Camera
            SimpleUniverse universe = new SimpleUniverse(canvas);
            universe.getViewingPlatform().getViewPlatformTransform().setTransform(stadiaTrans);
            universe.addBranchGraph(rootGroup);
        }
    }

    public Canvas3D getCanvas() {
        return canvas;
    }

    public void addChild(Object node, String branch) {
        BranchGroup group;
        if (groupMap.containsKey(branch)) {
            group = groupMap.get(branch);
        } else {
            groupMap.put(branch, group = new BranchGroup());
            group.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            group.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            TransformGroup transform = new TransformGroup();
            transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            transform.addChild(group);
            baseGroup.addChild(transform);
        }
        group.addChild((Node) node);
    }
    
    public void removeChild(Object node, String branch) {
        
    }

    public void translate(float[] coord3, String branch) {
        Transform3D transform = new Transform3D();
        TransformGroup group = (TransformGroup) groupMap.get(branch).getParent();
        group.getTransform(transform);
        Vector3f vector = new Vector3f();
        transform.get(vector);
        vector.add(new Vector3f(coord3));
        transform.setTranslation(vector);
        group.setTransform(transform);
    }

    public void scale(int calibration) {
        Transform3D scaleTransform = new Transform3D();
        scaleGroup.getTransform(scaleTransform);
        scaleTransform.setScale(Math.max(scaleTransform.getScale() + calibration * 0.1f, 1));
        scaleGroup.setTransform(scaleTransform);
    }

    public void deflect(double angle) {
        Transform3D deflectionTrans = new Transform3D();
        deflectionGroup.getTransform(deflectionTrans);
        Quat4d quat4d = new Quat4d();
        deflectionTrans.get(quat4d);
        AxisAngle4d angle4d = new AxisAngle4d();
        angle4d.set(quat4d);
        angle4d.setAngle(angle4d.getAngle() + (angle4d.getY() < 0 ? -angle : angle));
        deflectionTrans.setRotation(angle4d);
        deflectionGroup.setTransform(deflectionTrans);
    }

    public void eleveate(double angle) {
        Transform3D elevationTrans = new Transform3D();
        elevationTrans.setRotation(new AxisAngle4d(1, 0, 0, angle));
        elevationGroup.setTransform(elevationTrans);
    }

    public void rotate(double x) {
        deflect(x);
    }

    public abstract void picking(AbstractUnit pickUnit, int id, MouseEvent mevent);

}
