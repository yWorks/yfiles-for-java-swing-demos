/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.utils.FlagsEnum;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import toolkit.optionhandler.ComponentType;
import toolkit.optionhandler.ComponentTypes;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.MinMax;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration options for the layout algorithm of the same name.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("ComponentLayout")
public class ComponentLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public ComponentLayoutConfig() {
    ComponentLayout layout = new ComponentLayout((ILayoutAlgorithm)null);

    setStyleItem(ComponentArrangementStyles.ROWS);
    setRemovingOverlapsItem(!ComponentArrangementStyles.NONE.equals((layout.getStyle().and(ComponentArrangementStyles.MODIFIER_NO_OVERLAP))));
    setFromSketchModeEnabledItem(!ComponentArrangementStyles.NONE.equals((layout.getStyle().and(ComponentArrangementStyles.MODIFIER_AS_IS))));
    YDimension size = layout.getPreferredSize();
    setUsingScreenRatioItem(true);
    setAspectRatioItem(size.getWidth() / size.getHeight());

    setComponentSpacingItem(layout.getComponentSpacing());
    setGridEnabledItem(layout.getGridSpacing() > 0);
    setGridSpacingItem(layout.getGridSpacing() > 0 ? layout.getGridSpacing() : 20.0d);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    ComponentLayout layout = new ComponentLayout((ILayoutAlgorithm)null);
    layout.setComponentArrangementEnabled(true);
    ComponentArrangementStyles style = getStyleItem();
    if (isRemovingOverlapsItem()) {
      style = FlagsEnum.or(style, ComponentArrangementStyles.MODIFIER_NO_OVERLAP);
    }
    if (isFromSketchModeEnabledItem()) {
      style = FlagsEnum.or(style, ComponentArrangementStyles.MODIFIER_AS_IS);
    }
    layout.setStyle(style);

    double w, h;
    if (graphComponent != null && isUsingScreenRatioItem()) {
      w = graphComponent.getWidth();
      h = graphComponent.getHeight();
    } else {
      w = getAspectRatioItem();
      h = 1.0d / w;
      w *= 400.0d;
      h *= 400.0d;
    }
    layout.setPreferredSize(new YDimension(w, h));
    layout.setComponentSpacing(getComponentSpacingItem());
    if (isGridEnabledItem()) {
      layout.setGridSpacing(getGridSpacingItem());
    } else {
      layout.setGridSpacing(0);
    }

    return layout;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The component layout algorithm arranges the connected components of a graph. " +
           "It can use any other layout style to arrange each component separately, and then arranges the components as such.</p>" +
           "<p>In this demo, the arrangement of each component is just kept as it is.</p>";
  }

  private ComponentArrangementStyles styleItem = ComponentArrangementStyles.NONE;

  @Label("Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentArrangementStyles.class, stringValue = "ROWS")
  @EnumValueAnnotation(label = "No Arrangement", value = "NONE")
  @EnumValueAnnotation(label = "Multiple Rows", value = "ROWS")
  @EnumValueAnnotation(label = "Single Row", value = "SINGLE_ROW")
  @EnumValueAnnotation(label = "Single Column", value = "SINGLE_COLUMN")
  @EnumValueAnnotation(label = "Packed Rectangle", value = "PACKED_RECTANGLE")
  @EnumValueAnnotation(label = "Compact Rectangle", value = "PACKED_COMPACT_RECTANGLE")
  @EnumValueAnnotation(label = "Packed Circle", value = "PACKED_CIRCLE")
  @EnumValueAnnotation(label = "Compact Circle", value = "PACKED_COMPACT_CIRCLE")
  @EnumValueAnnotation(label = "Nested Rows", value = "MULTI_ROWS")
  @EnumValueAnnotation(label = "Compact Nested Rows", value = "MULTI_ROWS_COMPACT")
  @EnumValueAnnotation(label = "Width-constrained Nested Rows", value = "MULTI_ROWS_WIDTH_CONSTRAINT")
  @EnumValueAnnotation(label = "Height-constrained Nested Rows", value = "MULTI_ROWS_HEIGHT_CONSTRAINT")
  @EnumValueAnnotation(label = "Width-constrained Compact Nested Rows", value = "MULTI_ROWS_WIDTH_CONSTRAINT_COMPACT")
  @EnumValueAnnotation(label = "Height-constrained Compact Nested Rows", value = "MULTI_ROWS_HEIGHT_CONSTRAINT_COMPACT")
  public final ComponentArrangementStyles getStyleItem() {
    return this.styleItem;
  }

  @Label("Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentArrangementStyles.class, stringValue = "ROWS")
  @EnumValueAnnotation(label = "No Arrangement", value = "NONE")
  @EnumValueAnnotation(label = "Multiple Rows", value = "ROWS")
  @EnumValueAnnotation(label = "Single Row", value = "SINGLE_ROW")
  @EnumValueAnnotation(label = "Single Column", value = "SINGLE_COLUMN")
  @EnumValueAnnotation(label = "Packed Rectangle", value = "PACKED_RECTANGLE")
  @EnumValueAnnotation(label = "Compact Rectangle", value = "PACKED_COMPACT_RECTANGLE")
  @EnumValueAnnotation(label = "Packed Circle", value = "PACKED_CIRCLE")
  @EnumValueAnnotation(label = "Compact Circle", value = "PACKED_COMPACT_CIRCLE")
  @EnumValueAnnotation(label = "Nested Rows", value = "MULTI_ROWS")
  @EnumValueAnnotation(label = "Compact Nested Rows", value = "MULTI_ROWS_COMPACT")
  @EnumValueAnnotation(label = "Width-constrained Nested Rows", value = "MULTI_ROWS_WIDTH_CONSTRAINT")
  @EnumValueAnnotation(label = "Height-constrained Nested Rows", value = "MULTI_ROWS_HEIGHT_CONSTRAINT")
  @EnumValueAnnotation(label = "Width-constrained Compact Nested Rows", value = "MULTI_ROWS_WIDTH_CONSTRAINT_COMPACT")
  @EnumValueAnnotation(label = "Height-constrained Compact Nested Rows", value = "MULTI_ROWS_HEIGHT_CONSTRAINT_COMPACT")
  public final void setStyleItem( ComponentArrangementStyles value ) {
    this.styleItem = value;
  }

  private boolean removingOverlapsItem;

  @Label("Remove Overlaps")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRemovingOverlapsItem() {
    return this.removingOverlapsItem;
  }

  @Label("Remove Overlaps")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRemovingOverlapsItem( boolean value ) {
    this.removingOverlapsItem = value;
  }

  private boolean fromSketchModeEnabledItem;

  @Label("From Sketch")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isFromSketchModeEnabledItem() {
    return this.fromSketchModeEnabledItem;
  }

  @Label("From Sketch")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setFromSketchModeEnabledItem( boolean value ) {
    this.fromSketchModeEnabledItem = value;
  }

  private boolean usingScreenRatioItem;

  @Label("Use Screen Aspect Ratio")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingScreenRatioItem() {
    return this.usingScreenRatioItem;
  }

  @Label("Use Screen Aspect Ratio")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingScreenRatioItem( boolean value ) {
    this.usingScreenRatioItem = value;
  }

  private double aspectRatioItem;

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getAspectRatioItem() {
    return this.aspectRatioItem;
  }

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setAspectRatioItem( double value ) {
    this.aspectRatioItem = value;
  }

  public final boolean isAspectRatioItemDisabled() {
    return isUsingScreenRatioItem();
  }

  private double componentSpacingItem;

  @Label("Minimum Component Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(doubleValue = 45.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 400.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getComponentSpacingItem() {
    return this.componentSpacingItem;
  }

  @Label("Minimum Component Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(doubleValue = 45.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 400.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setComponentSpacingItem( double value ) {
    this.componentSpacingItem = value;
  }

  private boolean gridEnabledItem;

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGridEnabledItem() {
    return this.gridEnabledItem;
  }

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGridEnabledItem( boolean value ) {
    this.gridEnabledItem = value;
  }

  private double gridSpacingItem;

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 80)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 80)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( double value ) {
    this.gridSpacingItem = value;
  }

  public final boolean isGridSpacingItemDisabled() {
    return !isGridEnabledItem();
  }

}
