package org.aqua.voxel.container;

import org.aqua.graph.voxel.VoxelMatrixNode;
import org.aqua.graph.voxel.VoxelMatrixRoot;
import org.aqua.graph.voxel.VoxelMatrixUtil;
import org.aqua.voxel.craft.AbstractUnit;
import org.aqua.voxel.craft.ArrowUnit;
import org.aqua.voxel.craft.CursorUnit;
import org.aqua.voxel.craft.ModelUnit;
import org.aqua.voxel.craft.WorkbenchUnit;

public abstract class VoxelSandbox {
    public static final String BRANCH_MODEL     = "MODEL";
    public static final String BRANCH_CURSOR    = "CURSOR";
    public static final String BRANCH_WORKBENCH = "WORKBENCH";

    private VoxelMatrixRoot    workbenchRoot;
    private VoxelMatrixRoot    modelRoot;
    private AbstractUnit       focusUnit;
    private CursorUnit         cursor;

    public int                 floor            = 0;
    public float               unit             = 0.4f;
    public float               floorOffset      = (unit + WorkbenchUnit.WorkbenchHeight) / 2;
    public VoxelSandbox() {
        AbstractUnit.unit = unit;

        workbenchRoot = new VoxelMatrixRoot() {

            @Override
            public void attachContent(VoxelMatrixNode node) {
                WorkbenchUnit content = new WorkbenchUnit();
                content.setCoord3(node.coord3);
                content.rollover();
                node.content = content;
                addChild(content, BRANCH_WORKBENCH);
            }

        };

        modelRoot = new VoxelMatrixRoot() {

            @Override
            public void attachContent(VoxelMatrixNode node) {
                ModelUnit content = new ModelUnit();
                content.setCoord3(node.coord3);
                // content.rollover();
                node.content = content;
                addChild(content, BRANCH_MODEL);
            }

        };
        
        int[] lower = new int[] { -15, -15, -15 };
        int[] upper = new int[] { 15, 15, 15 };
        
        int[] size = new int[] { 10, 10, 10 };
        lower = new int[] { -(size[0] - 1) / 2, 0, 0 };
        upper = new int[] { size[0] / 2, 0, size[2] - 1 };
        
        workbenchRoot.realloc(lower, upper);
        
        lower = new int[] { -(size[0] - 1) / 2, 0, 0 };
        upper = new int[] { size[0] / 2, size[1], size[2] - 1 };
        
        modelRoot.realloc(lower, upper);
        
        // ((AbstractUnit) modelRoot.findNode(new int[3]).content).rollover();
        buildCursor();
        // buildArrow();
    }

    public void buildCursor() {
        addChild(cursor = new CursorUnit(), BRANCH_CURSOR);
    }

    public void buildArrow() {
        ArrowUnit arrow = new ArrowUnit();
        arrow.rollover();
        addChild(arrow, VoxelUniverse.BRANCH_ARROW);
    }

    public abstract void addChild(Object node, String branch);
    
    public abstract void removeChild(Object node, String branch);

    public void hover(AbstractUnit pickUnit, int id) {
        if (pickUnit != null) {             // 有pick对象
            if (focusUnit == pickUnit) {
                // return;
            } else if (focusUnit != null) { // blur旧焦点
                focusUnit.blur();
            } else {                        // 点亮cursor
                cursor.rollover();
            }
            focusUnit = pickUnit;
            if (pickUnit instanceof WorkbenchUnit) {
                pickUnit.focus();
                int[] coord3 = pickUnit.getCoord3();
                cursor.setCoord3(new int[] { coord3[0], coord3[1] + floor, coord3[2] });
            } else if (pickUnit instanceof ModelUnit) {
                ModelUnit model = (ModelUnit) pickUnit;
                int[] coord3 = model.getCoord3();
                if (coord3[1] >= floor) {
                    int[] offset = ModelUnit.FACE_OFFSET[id];
                    model.focus(id);
                    cursor.setCoord3(new int[] { coord3[0] + offset[0], coord3[1] + offset[1], coord3[2] + offset[2] });
                }
            }
        } else if (focusUnit != null) {     // 无pick有焦点
            focusUnit.blur();
            focusUnit = null;
            cursor.rollover();
        }
    }

    public void leftClick(AbstractUnit pickUnit, int normal) {
        if (pickUnit instanceof WorkbenchUnit) {
            int[] coord3 = pickUnit.getCoord3();
            coord3 = new int[] { coord3[0], floor, coord3[2] };
            VoxelMatrixNode node = modelRoot.findNode(coord3);
            if (node == null) {
                int[] lower = modelRoot.lowerPoint.clone();
                int[] upper = modelRoot.upperPoint.clone();
                VoxelMatrixUtil.packagePoint(lower, upper, coord3);
                modelRoot.realloc(lower, upper);
                modelRoot.lowerPoint = lower;
                modelRoot.upperPoint = upper;
                node = modelRoot.findNode(coord3);
            }
            ModelUnit unit = (ModelUnit) node.content;
            if (!unit.isVisible()) {
                unit.rollover();
            }
        } else if (pickUnit instanceof ModelUnit) {
            int[] coord3 = pickUnit.getCoord3();
            int[] offset = ModelUnit.FACE_OFFSET[normal];
            for (int i = 0; i < 3; i++) {
                coord3[i] += offset[i];
            }
            VoxelMatrixNode node = modelRoot.findNode(coord3);
            if (node == null) {
                int[] lower = modelRoot.lowerPoint.clone();
                int[] upper = modelRoot.upperPoint.clone();
                VoxelMatrixUtil.packagePoint(lower, upper, coord3);
                modelRoot.realloc(lower, upper);
                node = modelRoot.findNode(coord3);
            }
            ModelUnit unit = (ModelUnit) node.content;
            if (!unit.isVisible()) {
                unit.rollover();
            }
        }
    }

    public void rightClick(AbstractUnit pickUnit, int normal) {
        if (pickUnit instanceof WorkbenchUnit) {
            int[] coord3 = pickUnit.getCoord3();
            coord3 = new int[] { coord3[0], coord3[1] + floor, coord3[2] };
            VoxelMatrixNode node = modelRoot.findNode(coord3);
            if (node == null) {
                int[] lower = modelRoot.lowerPoint.clone();
                int[] upper = modelRoot.upperPoint.clone();
                VoxelMatrixUtil.packagePoint(lower, upper, coord3);
                modelRoot.realloc(lower, upper);
                node = modelRoot.findNode(coord3);
            }
            ModelUnit unit = (ModelUnit) node.content;
            if (unit.isVisible()) {
                unit.rollover();
            }
        } else if (pickUnit instanceof ModelUnit) {
            pickUnit.rollover();
        }
    }

    public String exportModel() {
        return modelRoot.exportModel();
    }

    public void importModel(String data) {
        modelRoot.importModel(data);
    }

}