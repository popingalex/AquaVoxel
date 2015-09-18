package org.aqua.voxel.container;

import org.aqua.graph.voxel.VoxelMatrixUtil;
import org.aqua.resource.KeyedPool;
import org.aqua.structure.space.SpaceNode;
import org.aqua.structure.space.SpaceNode.Content;
import org.aqua.structure.space.SpaceRoot;
import org.aqua.voxel.craft.AbstractUnit;
import org.aqua.voxel.craft.AbstractUnit.UnitType;
import org.aqua.voxel.craft.BeaconUnit;
import org.aqua.voxel.craft.CursorUnit;
import org.aqua.voxel.craft.ModelUnit;
import org.aqua.voxel.craft.WorkbenchUnit;

public abstract class VoxelSandbox {
    public static final String                BRANCH_MODEL     = "MODEL";
    public static final String                BRANCH_CURSOR    = "CURSOR";
    public static final String                BRANCH_WORKBENCH = "WORKBENCH";

    private SpaceRoot                         workbenchRoot;
    private SpaceRoot                         modelRoot;
    private AbstractUnit                      cursorUnit;
    private AbstractUnit                      focusUnit;
    private KeyedPool<UnitType, AbstractUnit> unitPool;

    public int                                floor            = 0;
    public float                              unit             = 0.4f;
    public float                              floorOffset      = (unit + WorkbenchUnit.WorkbenchHeight) / 2;
    public VoxelSandbox() {
        AbstractUnit.unit = unit;

        unitPool = new KeyedPool<AbstractUnit.UnitType, AbstractUnit>() {
            @Override
            protected UnitType keyof(AbstractUnit object) {
                return object.getType();
            }
            @Override
            protected AbstractUnit create(UnitType key) {
                switch (key) {
                case Beacon:
                    return new BeaconUnit();
                case Cursor:
                    return new CursorUnit();
                case Model:
                    return new ModelUnit();
                case Workbench:
                    return new WorkbenchUnit();
                }
                return null;
            }
        };
        workbenchRoot = new SpaceRoot(2) {
            @Override
            protected Content attachContent(SpaceNode node) {
                AbstractUnit content = unitPool.borrow(UnitType.Workbench);
                content.setCoord3(new int[] { node.coords[0], 0, node.coords[1] });
                display(BRANCH_WORKBENCH, content);
                content.rollover();
                return content;
            }
        };
        modelRoot = new SpaceRoot(3) {
            // @Override
            // public Content attachContent(SpaceNode node) {
            // AbstractUnit content = unitPool.borrow(UnitType.Model);
            // return content;
            // }

            // @Override
            // public void attachContent(VoxelMatrixNode node) {
            // ModelUnit content = new ModelUnit();
            // content.setCoord3(node.coord3);
            // node.content = content;
            // content.setParent(getParent(BRANCH_MODEL));
            // content.rollover();
            // }
        };
        buildSandbox();
    }

    public void buildSandbox() {
        int[] lower = new int[] { -15, -15, -15 };
        int[] upper = new int[] { 15, 15, 15 };
        int[] size = new int[] { 6, 4, 2 };

        {
            lower = new int[] { -(size[0] - 1) / 2, 0 };
            upper = new int[] { size[0] / 2, size[2] - 1 };
            workbenchRoot.realloc(lower, upper);
        }
        {
            lower = new int[] { -(size[0] - 1) / 2, 0, 0 };
            upper = new int[] { size[0] / 2, size[1], size[2] - 1 };
            modelRoot.realloc(lower, upper);
        }
        {
            cursorUnit = new CursorUnit();
        }
        {
            BeaconUnit arrow = new BeaconUnit();
            display(VoxelUniverse.BRANCH_BEACON, arrow);
            arrow.rollover();
        }
        // ((AbstractUnit) modelRoot.findNode(new int[3]).content).rollover();
    }

    public void hover(AbstractUnit pickUnit, int id) {
        if (pickUnit != null) {             // 有pick对象
            if (focusUnit == pickUnit) {
                // return;
            } else if (focusUnit != null) { // blur旧焦点
                focusUnit.blur();
            } else {                        // 点亮cursor
                display(BRANCH_CURSOR, cursorUnit);
                cursorUnit.rollover();
            }
            focusUnit = pickUnit;
            if (pickUnit instanceof WorkbenchUnit) {
                pickUnit.focus();
                int[] coord3 = pickUnit.getCoord3();
                cursorUnit.setCoord3(new int[] { coord3[0], coord3[1] + floor, coord3[2] });
            } else if (pickUnit instanceof ModelUnit) {
                ModelUnit model = (ModelUnit) pickUnit;
                int[] coord3 = model.getCoord3();
                if (coord3[1] >= floor) {
                    int[] offset = ModelUnit.FACE_OFFSET[id];
                    model.focus(id);
                    cursorUnit.setCoord3(new int[] { coord3[0] + offset[0], coord3[1] + offset[1],
                            coord3[2] + offset[2] });
                }
            }
        } else if (focusUnit != null) {     // 无pick有焦点
            focusUnit.blur();
            focusUnit = null;
            restore(BRANCH_CURSOR, cursorUnit);
            cursorUnit.rollover();
        }
    }

    public void leftClick(AbstractUnit pickUnit, int normal) {
        if (pickUnit instanceof WorkbenchUnit) {
            int[] coord3 = pickUnit.getCoord3();
            coord3 = new int[] { coord3[0], floor, coord3[2] };
            SpaceNode node = modelRoot.findNode(coord3);
            if (node == null) {     // 结点不存在, 扩充并建立
                int[] lower = modelRoot.lower.clone();
                int[] upper = modelRoot.upper.clone();
                VoxelMatrixUtil.packagePoint(lower, upper, coord3);
                modelRoot.realloc(lower, upper);
                node = modelRoot.findNode(coord3);
            }
            ModelUnit unit = (ModelUnit) unitPool.borrow(UnitType.Model);
            node.setContent(unit);
            unit.setCoord3(coord3);
            display(BRANCH_MODEL, unit);
            unit.rollover();
        } else if (pickUnit instanceof ModelUnit) {
            int[] coord3 = pickUnit.getCoord3();
            int[] offset = ModelUnit.FACE_OFFSET[normal];
            for (int i = 0; i < 3; i++) {
                coord3[i] += offset[i];
            }
            SpaceNode node = modelRoot.findNode(coord3);
            if (node == null) {
                int[] lower = modelRoot.lower.clone();
                int[] upper = modelRoot.upper.clone();
                VoxelMatrixUtil.packagePoint(lower, upper, coord3);
                modelRoot.realloc(lower, upper);
                node = modelRoot.findNode(coord3);
            }
            ModelUnit unit = (ModelUnit) unitPool.borrow(UnitType.Model);
            node.setContent(unit);
            unit.setCoord3(coord3);
            display(BRANCH_MODEL, unit);
            unit.rollover();
        }
    }

    public void rightClick(AbstractUnit pickUnit, int normal) {
        if (pickUnit instanceof WorkbenchUnit) {
            int[] coord3 = pickUnit.getCoord3();
            coord3 = new int[] { coord3[0], coord3[1] + floor, coord3[2] };
            SpaceNode node = modelRoot.findNode(coord3);
            // if (node == null) {
            // int[] lower = modelRoot.lower.clone();
            // int[] upper = modelRoot.upper.clone();
            // VoxelMatrixUtil.packagePoint(lower, upper, coord3);
            // modelRoot.realloc(lower, upper);
            // node = modelRoot.findNode(coord3);
            // }
            // ModelUnit unit = (ModelUnit) node.getContent();
            // if (unit.isVisible()) {
            // unit.rollover();
            // }
        } else if (pickUnit instanceof ModelUnit) {
            restore(BRANCH_MODEL, pickUnit);
            pickUnit.rollover();
        }
    }

    public String exportModel() {
        // return modelRoot.exportModel();
        return null;
    }

    public void importModel(String data) {
        // modelRoot.importModel(data);
    }

    public abstract void display(String branch, AbstractUnit unit);
    public abstract void restore(String branch, AbstractUnit unit);
}