package org.aqua.voxel.behavior;

import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Node;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

public class PickBehavior extends PickMouseBehavior {

    public PickBehavior(Canvas3D canvas, BranchGroup root, Bounds bounds) {
        super(canvas, root, bounds);
        setMode(PickTool.GEOMETRY);
    }

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
            Node node = result.getNode(PickResult.PRIMITIVE);
            Node face = result.getNode(PickResult.SHAPE3D);
            picking(node, face, mevent);
        } else {
            picking(null, null, mevent);
        }
    }

    public void picking(Node pickNode, Node pickFace, MouseEvent event) {
    }
    
}