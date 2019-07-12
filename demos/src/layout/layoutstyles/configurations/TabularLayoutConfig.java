/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for Java (Swing) functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for Java (Swing) version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for Java (Swing) powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for Java (Swing)
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package layout.layoutstyles.configurations;

import com.yworks.yfiles.layout.ColumnDescriptor;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.RowDescriptor;
import com.yworks.yfiles.layout.tabular.LayoutPolicy;
import com.yworks.yfiles.layout.tabular.NodeLayoutDescriptor;
import com.yworks.yfiles.layout.tabular.TabularLayout;
import com.yworks.yfiles.layout.tabular.TabularLayoutData;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import toolkit.optionhandler.ComponentType;
import toolkit.optionhandler.ComponentTypes;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.MinMax;
import toolkit.optionhandler.OptionGroupAnnotation;

@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("TabularLayout")
public class TabularLayoutConfig extends LayoutConfiguration {
  public TabularLayoutConfig() {
    TabularLayout layout = new TabularLayout();

    setLayoutPolicyItem(EnumLayoutPolicies.AUTO_SIZE);
    setRowCountItem(8);
    setColumnCountItem(12);
    setHorizontalAlignmentItem(EnumHorizontalAlignments.CENTER);
    setVerticalAlignmentItem(EnumVerticalAlignments.CENTER);
    setConsideringNodeLabelsItem(layout.isNodeLabelConsiderationEnabled());
    setMinimumRowHeightItem(0);
    setMinimumColumnWidthItem(0);
    setCellInsetsItem(5);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    TabularLayout layout = new TabularLayout();

    switch (getLayoutPolicyItem()) {
      case AUTO_SIZE:
        layout.setLayoutPolicy(LayoutPolicy.AUTO_SIZE);
        break;
      case FIXED_TABLE_SIZE:
      case SINGLE_ROW:
      case SINGLE_COLUMN:
        layout.setLayoutPolicy(LayoutPolicy.FIXED_SIZE);
        break;
      case FROM_SKETCH:
        layout.setLayoutPolicy(LayoutPolicy.FROM_SKETCH);
        break;
    }

    layout.setNodeLabelConsiderationEnabled(isConsideringNodeLabelsItem());

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    TabularLayoutData layoutData = new TabularLayoutData();
    NodeLayoutDescriptor nodeLayoutDescriptor = new NodeLayoutDescriptor();
    switch (getHorizontalAlignmentItem()) {
      case CENTER:
        nodeLayoutDescriptor.setHorizontalAlignment(0.5);
        break;
      case LEFT:
        nodeLayoutDescriptor.setHorizontalAlignment(0);
        break;
      case RIGHT:
        nodeLayoutDescriptor.setHorizontalAlignment(1);
        break;
    }
    switch (getVerticalAlignmentItem()) {
      case CENTER:
        nodeLayoutDescriptor.setHorizontalAlignment(0.5);
        break;
      case TOP:
        nodeLayoutDescriptor.setHorizontalAlignment(0);
        break;
      case BOTTOM:
        nodeLayoutDescriptor.setHorizontalAlignment(1);
        break;
    }

    int nodeCount = graphComponent.getGraph().getNodes().size();
    PartitionGrid partitionGrid;
    switch (getLayoutPolicyItem()) {
      case FIXED_TABLE_SIZE:
        int rowCount = getRowCountItem();
        int columnCount = getColumnCountItem();
        if (rowCount * columnCount >= nodeCount) {
          partitionGrid = new PartitionGrid(rowCount, columnCount);
        } else {
          // make sure partitionGrid has enough cells for all nodes
          partitionGrid = new PartitionGrid(nodeCount / columnCount, columnCount);
        }
        break;
      case SINGLE_ROW:
        partitionGrid = new PartitionGrid(1, nodeCount);
        break;
      case SINGLE_COLUMN:
        partitionGrid = new PartitionGrid(nodeCount, 1);
        break;
      default:
        partitionGrid = new PartitionGrid(1, 1);
        break;
    }

    double minimumRowHeight = getMinimumRowHeightItem();
    double minimumColumnWidth = getMinimumColumnWidthItem();
    double cellInsets = getCellInsetsItem();
    for (RowDescriptor row : partitionGrid.getRows()) {
      row.setMinimumHeight(minimumRowHeight);
      row.setTopInset(cellInsets);
      row.setBottomInset(cellInsets);
    }
    for (ColumnDescriptor column : partitionGrid.getColumns()) {
      column.setMinimumWidth(minimumColumnWidth);
      column.setLeftInset(cellInsets);
      column.setRightInset(cellInsets);
    }

    layoutData.setNodeLayoutDescriptors(nodeLayoutDescriptor);
    layoutData.getPartitionGridData().setGrid(partitionGrid);

    return layoutData;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The tabular layout style arranges the nodes in rows and columns. This is a" +
           " very simple layout which is useful when nodes should be placed under/next to each other.</p>" +
           "<p>Edges are ignored in this layout style. Their bends are removed.</p>";
  }

  private EnumLayoutPolicies layoutPolicyItem = EnumLayoutPolicies.AUTO_SIZE;

  @Label("Layout Mode")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @EnumValueAnnotation(label = "Automatic Table Size", value = "AUTO_SIZE")
  @EnumValueAnnotation(label = "Single Row", value = "SINGLE_ROW")
  @EnumValueAnnotation(label = "Single Column", value = "SINGLE_COLUMN")
  @EnumValueAnnotation(label = "Fixed Table Size", value = "FIXED_TABLE_SIZE")
  @EnumValueAnnotation(label = "From Sketch", value = "FROM_SKETCH")
  public final EnumLayoutPolicies getLayoutPolicyItem() {
    return this.layoutPolicyItem;
  }

  @Label("Layout Mode")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @EnumValueAnnotation(label = "Automatic Table Size", value = "AUTO_SIZE")
  @EnumValueAnnotation(label = "Single Row", value = "SINGLE_ROW")
  @EnumValueAnnotation(label = "Single Column", value = "SINGLE_COLUMN")
  @EnumValueAnnotation(label = "Fixed Table Size", value = "FIXED_TABLE_SIZE")
  @EnumValueAnnotation(label = "From Sketch", value = "FROM_SKETCH")
  public final void setLayoutPolicyItem( EnumLayoutPolicies value ) {
    this.layoutPolicyItem = value;
  }

  private int rowCountItem;

  @Label("Row Count")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @MinMax(min = 1, max = 200, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getRowCountItem() {
    return this.rowCountItem;
  }

  @Label("Row Count")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @MinMax(min = 1, max = 200, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setRowCountItem( int value ) {
    this.rowCountItem = value;
  }

  public final boolean isRowCountItemDisabled() {
    return getLayoutPolicyItem() != EnumLayoutPolicies.FIXED_TABLE_SIZE;
  }

  private int columnCountItem;

  @Label("Column Count")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @MinMax(min = 1, max = 200, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getColumnCountItem() {
    return this.columnCountItem;
  }

  @Label("Column Count")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @MinMax(min = 1, max = 200, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setColumnCountItem( int value ) {
    this.columnCountItem = value;
  }

  public final boolean isColumnCountItemDisabled() {
    return getLayoutPolicyItem() != EnumLayoutPolicies.FIXED_TABLE_SIZE;
  }

  private boolean consideringNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  public final boolean isConsideringNodeLabelsItem() {
    return this.consideringNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  public final void setConsideringNodeLabelsItem( boolean value ) {
    this.consideringNodeLabelsItem = value;
  }

  private EnumHorizontalAlignments horizontalAlignmentItem = EnumHorizontalAlignments.LEFT;

  @Label("Horizontal Alignment")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  public final EnumHorizontalAlignments getHorizontalAlignmentItem() {
    return this.horizontalAlignmentItem;
  }

  @Label("Horizontal Alignment")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  public final void setHorizontalAlignmentItem( EnumHorizontalAlignments value ) {
    this.horizontalAlignmentItem = value;
  }

  private EnumVerticalAlignments verticalAlignmentItem = EnumVerticalAlignments.TOP;

  @Label("Vertical Alignment")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @EnumValueAnnotation(label = "Top", value = "TOP")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Bottom", value = "BOTTOM")
  public final EnumVerticalAlignments getVerticalAlignmentItem() {
    return this.verticalAlignmentItem;
  }

  @Label("Vertical Alignment")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @EnumValueAnnotation(label = "Top", value = "TOP")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Bottom", value = "BOTTOM")
  public final void setVerticalAlignmentItem( EnumVerticalAlignments value ) {
    this.verticalAlignmentItem = value;
  }

  private double cellInsetsItem;

  @Label("Cell Insets (all sides)")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @MinMax(min = 0, max = 50, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCellInsetsItem() {
    return this.cellInsetsItem;
  }

  @Label("Cell Insets (all sides)")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @MinMax(min = 0, max = 50, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCellInsetsItem( double value ) {
    this.cellInsetsItem = value;
  }

  private double minimumRowHeightItem;

  @Label("Minimum Row Height")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 80)
  @MinMax(min = 0, max = 100, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumRowHeightItem() {
    return this.minimumRowHeightItem;
  }

  @Label("Minimum Row Height")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 80)
  @MinMax(min = 0, max = 100, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumRowHeightItem( double value ) {
    this.minimumRowHeightItem = value;
  }

  private double minimumColumnWidthItem;

  @Label("Minimum Column Width")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 80)
  @MinMax(min = 0, max = 100, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumColumnWidthItem() {
    return this.minimumColumnWidthItem;
  }

  @Label("Minimum Column Width")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 80)
  @MinMax(min = 0, max = 100, step = 1)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumColumnWidthItem( double value ) {
    this.minimumColumnWidthItem = value;
  }

  public enum EnumLayoutPolicies {
    AUTO_SIZE(0),

    SINGLE_ROW(1),

    SINGLE_COLUMN(2),

    FIXED_TABLE_SIZE(3),

    FROM_SKETCH(4);

    private final int value;

    private EnumLayoutPolicies( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLayoutPolicies fromOrdinal( int ordinal ) {
      for (EnumLayoutPolicies current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumHorizontalAlignments {
    LEFT(0),

    CENTER(1),

    RIGHT(2);

    private final int value;

    private EnumHorizontalAlignments( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumHorizontalAlignments fromOrdinal( int ordinal ) {
      for (EnumHorizontalAlignments current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumVerticalAlignments {
    TOP(0),

    CENTER(1),

    BOTTOM(2);

    private final int value;

    private EnumVerticalAlignments( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumVerticalAlignments fromOrdinal( int ordinal ) {
      for (EnumVerticalAlignments current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

}
