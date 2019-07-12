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

import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.CopiedLayoutGraph;
import com.yworks.yfiles.layout.GraphTransformer;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.OperationType;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ISelectionModel;
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
@Label("GraphTransformer")
public class GraphTransformerConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public GraphTransformerConfig() {
    GraphTransformer transformer = new GraphTransformer();

    setOperationItem(OperationType.SCALE);
    setActingOnSelectionOnlyItem(false);
    setRotationAngleItem(transformer.getRotationAngle());
    setApplyingBestFitRotationItem(false);
    setScaleFactorItem(transformer.getScaleFactorX());
    setScalingNodeSizeItem(transformer.isNodeSizeScalingEnabled());
    setTranslateXItem(transformer.getTranslateX());
    setTranslateYItem(transformer.getTranslateY());
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    GraphTransformer transformer = new GraphTransformer();
    transformer.setOperation(getOperationItem());
    transformer.setSubgraphLayoutEnabled(isActingOnSelectionOnlyItem());
    transformer.setRotationAngle(getRotationAngleItem());
    if (isApplyingBestFitRotationItem() && getOperationItem() == OperationType.ROTATE) {
      setApplyingBestFitRotationItem(true);

      CopiedLayoutGraph layoutGraph = new LayoutGraphAdapter(graphComponent.getGraph(), (ISelectionModel<IModelItem>)null).createCopiedLayoutGraph();
      transformer.setRotationAngle(GraphTransformer.findBestFitRotationAngle(layoutGraph, graphComponent.getWidth(), graphComponent.getHeight()));
    } else {
      setApplyingBestFitRotationItem(false);
    }

    transformer.setScaleFactor(getScaleFactorItem());
    transformer.setNodeSizeScalingEnabled(isScalingNodeSizeItem());
    transformer.setTranslateX(getTranslateXItem());
    transformer.setTranslateY(getTranslateYItem());

    return transformer;
  }

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Rotate")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RotateGroup;

  @Label("Scale")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object ScaleGroup;

  @Label("Translate")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object TranslateGroup;

  private OperationType operationItem = OperationType.MIRROR_X_AXIS;

  @Label("Operation")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = OperationType.class, stringValue = "SCALE")
  @EnumValueAnnotation(label = "Mirror on X axis", value = "MIRROR_X_AXIS")
  @EnumValueAnnotation(label = "Mirror on Y axis", value = "MIRROR_Y_AXIS")
  @EnumValueAnnotation(label = "Rotate", value = "ROTATE")
  @EnumValueAnnotation(label = "Scale", value = "SCALE")
  @EnumValueAnnotation(label = "Translate", value = "TRANSLATE")
  public final OperationType getOperationItem() {
    return this.operationItem;
  }

  @Label("Operation")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = OperationType.class, stringValue = "SCALE")
  @EnumValueAnnotation(label = "Mirror on X axis", value = "MIRROR_X_AXIS")
  @EnumValueAnnotation(label = "Mirror on Y axis", value = "MIRROR_Y_AXIS")
  @EnumValueAnnotation(label = "Rotate", value = "ROTATE")
  @EnumValueAnnotation(label = "Scale", value = "SCALE")
  @EnumValueAnnotation(label = "Translate", value = "TRANSLATE")
  public final void setOperationItem( OperationType value ) {
    this.operationItem = value;
  }

  private boolean actingOnSelectionOnlyItem;

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActingOnSelectionOnlyItem() {
    return this.actingOnSelectionOnlyItem;
  }

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActingOnSelectionOnlyItem( boolean value ) {
    this.actingOnSelectionOnlyItem = value;
  }

  private double rotationAngleItem;

  @Label("Rotation Angle")
  @OptionGroupAnnotation(name = "RotateGroup", position = 10)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = -360, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getRotationAngleItem() {
    return this.rotationAngleItem;
  }

  @Label("Rotation Angle")
  @OptionGroupAnnotation(name = "RotateGroup", position = 10)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = -360, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setRotationAngleItem( double value ) {
    this.rotationAngleItem = value;
  }

  public final boolean isRotationAngleItemDisabled() {
    return getOperationItem() != OperationType.ROTATE || isApplyingBestFitRotationItem();
  }

  private boolean applyingBestFitRotationItem;

  @Label("Best Fit Rotation")
  @OptionGroupAnnotation(name = "RotateGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isApplyingBestFitRotationItem() {
    return this.applyingBestFitRotationItem;
  }

  @Label("Best Fit Rotation")
  @OptionGroupAnnotation(name = "RotateGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setApplyingBestFitRotationItem( boolean value ) {
    this.applyingBestFitRotationItem = value;
  }

  public final boolean isApplyingBestFitRotationItemDisabled() {
    return getOperationItem() != OperationType.ROTATE;
  }

  private double scaleFactorItem;

  @Label("Scale Factor")
  @OptionGroupAnnotation(name = "ScaleGroup", position = 10)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.1d, max = 10.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getScaleFactorItem() {
    return this.scaleFactorItem;
  }

  @Label("Scale Factor")
  @OptionGroupAnnotation(name = "ScaleGroup", position = 10)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.1d, max = 10.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setScaleFactorItem( double value ) {
    this.scaleFactorItem = value;
  }

  public final boolean isScaleFactorItemDisabled() {
    return getOperationItem() != OperationType.SCALE;
  }

  private boolean scalingNodeSizeItem;

  @Label("Scale Node Size")
  @OptionGroupAnnotation(name = "ScaleGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isScalingNodeSizeItem() {
    return this.scalingNodeSizeItem;
  }

  @Label("Scale Node Size")
  @OptionGroupAnnotation(name = "ScaleGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setScalingNodeSizeItem( boolean value ) {
    this.scalingNodeSizeItem = value;
  }

  public final boolean isScalingNodeSizeItemDisabled() {
    return getOperationItem() != OperationType.SCALE;
  }

  private double translateXItem;

  @Label("Horizontal Distance")
  @OptionGroupAnnotation(name = "TranslateGroup", position = 10)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getTranslateXItem() {
    return this.translateXItem;
  }

  @Label("Horizontal Distance")
  @OptionGroupAnnotation(name = "TranslateGroup", position = 10)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setTranslateXItem( double value ) {
    this.translateXItem = value;
  }

  public final boolean isTranslateXItemDisabled() {
    return getOperationItem() != OperationType.TRANSLATE;
  }

  private double translateYItem;

  @Label("Vertical Distance")
  @OptionGroupAnnotation(name = "TranslateGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getTranslateYItem() {
    return this.translateYItem;
  }

  @Label("Vertical Distance")
  @OptionGroupAnnotation(name = "TranslateGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setTranslateYItem( double value ) {
    this.translateYItem = value;
  }

  public final boolean isTranslateYItemDisabled() {
    return getOperationItem() != OperationType.TRANSLATE;
  }

}
