/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.uml;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Synchronizes the labels of the UML class node with the {@link UmlClassModel}.
 * It supports
 * <ul>
 *   <li>creating labels from the model</li>
 *   <li>remove attribute or operation from model by removing its appropriate label</li>
 *   <li>add attribute or operation to model by adding an appropriate label</li>
 * </ul>
 * The mapping between labels and model item:
 * <ul>
 *   <li>label 0: name</li>
 *   <li>label 1: attribute heading</li>
 *   <li>label 2: operation heading</li>
 *   <li>label 3 to n: attributes</li>
 *   <li>label n+1 to m: operations</li>
 * </ul>
 */
public class UmlClassLabelSupport {
  /** Gap between the last item of one section and the cation of the next section. */
  public static final double SECTION_GAP = 5;

  private UmlClassLabelSupport() {
  }

  /**
   * Removes and creates all labels of the given UML node.
   */
  public static void updateAllLabels( IGraph graph, INode context ) {
    removeAllLabels(graph, context);
    createAllLabels(graph, context);
  }

  /**
   * Removes all labels of the given UML node.
   */
  private static void removeAllLabels( IGraph graph, INode context ) {
    IListEnumerable<ILabel> labels = context.getLabels();
    for (int i = labels.size() - 1; i >= 0; --i) {
      graph.remove(labels.getItem(i));
    }
  }

  /**
   * Creates all labels of the given node realizer.
   */
  private static void createAllLabels( IGraph graph, INode context ) {
    UmlClassModel model = getModel(context);
    double yOffset = 0d;

    LabelDefaults defaults = LabelDefaults.INSTANCE;

    // Create and add label 0: name label.
    ILabelModelParameter nameParam = createLabelParameter(false, 20d, yOffset);
    ILabel nameLabel = graph.addLabel(context, model.getClassName(), nameParam, defaults.nameStyle);
    yOffset += nameLabel.getLayout().getHeight() + 5;

    if (model.areSectionsVisible()) {
      // Create and add label 1: attribute heading label.
      ILabelModelParameter headingParam = createLabelParameter(false, 20d, yOffset);
      ILabel attributeHeadingLabel = graph.addLabel(context, "Attributes", headingParam, defaults.captionStyle);
      yOffset += attributeHeadingLabel.getLayout().getHeight();

      // Create and add label 2: operation heading label. It is below all attribute labels!
      ILabel operationHeadingLabel = graph.addLabel(context, "Operations", headingParam, defaults.captionStyle);

      // Create and add label 3 to n: attribute labels.
      if (model.areAttributesVisible()) {
        for (String text : model.getAttributes()) {
          ILabelModelParameter attrParam = createLabelParameter(false, 30d, yOffset);
          ILabel attributeLabel = graph.addLabel(context, text, attrParam, defaults.defaultStyle);
          yOffset += attributeLabel.getLayout().getHeight();
        }
      }

      // Configure label 2; now we know its position.
      yOffset += SECTION_GAP;
      headingParam = createLabelParameter(false, 20d, yOffset);
      graph.setLabelLayoutParameter(operationHeadingLabel, headingParam);
      yOffset += operationHeadingLabel.getLayout().getHeight();

      // Create and add label n+1 to m: operation labels.
      if (model.areOperationsVisible()) {
        for (String text : model.getOperations()) {
          ILabelModelParameter opParam = createLabelParameter(false, 30d, yOffset);
          ILabel operationLabel = graph.addLabel(context, text, opParam, defaults.defaultStyle);
          yOffset += operationLabel.getLayout().getHeight();
        }
      }
    }
  }

  /**
   * Returns the {@link UmlClassModel model} stored and visualized by the
   * given UML node.
   */
  static UmlClassModel getModel( INode context ) {
    return (UmlClassModel) context.getTag();
  }

  private static ILabelModelParameter createLabelParameter(
          boolean isCenter, double xOffset, double yOffset
  ) {
    FreeNodeLabelModel model = new FreeNodeLabelModel();
    if (isCenter) {
      PointD topCenter = new PointD(0.5, 0);
      return model.createParameter(topCenter, new PointD(xOffset, yOffset), topCenter, PointD.ORIGIN, 0);
    } else {
      return model.createParameter(PointD.ORIGIN, new PointD(xOffset, yOffset), PointD.ORIGIN, PointD.ORIGIN, 0);
    }
  }

  /**
   * Removes the label and its corresponding list item from the model.
   */
  public static void removeSelectedLabel( IGraph graph, INode context ) {
    ILabel label = getSelectedLabel(context);
    if (label != null) {
      UmlClassModel model = getModel(context);
      int labelIndex = indexOfLabel(context, label);

      int sl = model.getSelectedList();
      int slIdx = model.getSelectedListIndex();
      updateSelection(model, labelIndex);
      UndoFactory.addSelectedListUnit(graph, model, sl, slIdx);

      List<String> modelList = getModelList(model, labelIndex);
      int modelListIndex = modelListIndexOf(model, labelIndex);
      String oldValue = modelList.remove(modelListIndex);
      UndoFactory.addRemoveFeatureUnit(graph, model, modelList, modelListIndex, oldValue);

      updateAllLabels(graph, context);
      updateNodeSize(graph,context);
    }
  }

  /**
   * Updates the selection when removing a label with the given index.
   */
  private static void updateSelection( UmlClassModel model, int labelIndexToRemove ) {
    // Removing the last item -> select the second last item
    // Removing the sole item -> select none
    // Removing any other -> keep selected index
    int modelListIndex = modelListIndexOf(model, labelIndexToRemove);
    List modelList = getModelList(model, labelIndexToRemove);
    if (modelListIndex == modelList.size() - 1) {
      model.setSelectedListIndex(modelList.size() - 2);
    } else if (modelList.size() == 1) {
      model.setSelectedListIndex(UmlClassModel.LIST_INDEX_NONE);
    }
  }

  /**
   * Returns the list of attributes or operations depending to which one the given label index belongs to.
   */
  private static List<String> getModelList( UmlClassModel model, int labelIndex ) {
    if (model.areAttributesVisible()) {
      int attributeCount = model.getAttributes().size();
      if (labelIndex < attributeCount + 3) {
        // starts with index 3 -> labels for name, attribute and operation caption
        return model.getAttributes();
      }
    }
    return model.getOperations();
  }

  /**
   * Returns the list index of an attribute or operation depending to which one the given label index belongs to.
   */
  private static int modelListIndexOf( UmlClassModel model, int labelIndex ) {
    if (model.areAttributesVisible()) {
      int attributeCount = model.getAttributes().size();
      // starts with index 3 -> labels for name, attribute and operation caption
      if (labelIndex >= attributeCount + 3) {
        return labelIndex - 3 - attributeCount;
      }
    }
    return labelIndex - 3;
  }
  /**
   * Selects the list item of the label that contains the given point.
   */
  public static boolean selectListItemAt( IGraph graph, INode context, PointD p ) {
    UmlClassModel model = getModel(context);
    if (!model.areSectionsVisible()) {
      return false;
    }

    int labelIndex = indexOfLabelAt(context, p);
    if (labelIndex > 2) {
      int sl = model.getSelectedList();
      int slIdx = model.getSelectedListIndex();
      selectLabel(model, labelIndex);
      UndoFactory.addSelectedListUnit(graph, model, sl, slIdx);
      return true;
    }
    return false;
  }

  /**
   * Adds a label and a list item to the model for a new attribute.
   */
  public static ILabel addAttribute( IGraph graph, INode context ) {
    UmlClassModel model = getModel(context);
    String text = "attribute";
    addAttributeToModel(model, text);
    UndoFactory.addAddFeatureUnit(graph, model, UmlClassModel.LIST_ATTRIBUTES, text);
    updateAllLabels(graph, context);
    updateNodeSize(graph, context);
    int index = indexOfLastAttributeLabel(model);
    int sl = model.getSelectedList();
    int slIdx = model.getSelectedListIndex();
    selectLabel(model, index);
    UndoFactory.addSelectedListUnit(graph, model, sl, slIdx);
    return context.getLabels().getItem(index);
  }

  /**
   * Adds a list item to the model for a new attribute.
   */
  private static void addAttributeToModel( UmlClassModel model, String text ) {
    model.getAttributes().add(text);
  }

  /**
   * Returns the index of the labels of the last attribute.
   */
  private static int indexOfLastAttributeLabel( UmlClassModel model ) {
    return model.getAttributes().size() + 2;
  }

  /**
   * Adds a label and a list item to the model for a new operation.
   */
  public static ILabel addOperation( IGraph graph, INode context ) {
    UmlClassModel model = getModel(context);
    String text = "operation";
    addOperationToModel(model, text);
    UndoFactory.addAddFeatureUnit(graph, model, UmlClassModel.LIST_OPERATIONS, text);
    updateAllLabels(graph, context);
    updateNodeSize(graph, context);
    int index = indexOfLastOperationLabel(model);
    int sl = model.getSelectedList();
    int slIdx = model.getSelectedListIndex();
    selectLabel(model, index);
    UndoFactory.addSelectedListUnit(graph, model, sl, slIdx);
    return context.getLabels().getItem(index);
  }

  /**
   * Adds a list item to the model for a new operation.
   */
  private static void addOperationToModel( UmlClassModel model, String text ) {
    model.getOperations().add(text);
  }

  /**
   * Returns the index of the labels of the last operation.
   */
  private static int indexOfLastOperationLabel( UmlClassModel model ) {
    if (model.areAttributesVisible()) {
      return model.getAttributes().size() + model.getOperations().size() + 2;
    } else
      return model.getOperations().size() + 2;
  }

  /**
   * Recalculates the size of the given UML node.
   */
  public static void updateNodeSize( IGraph graph, INode context ) {
    IRectangle nl = context.getLayout();
    INodeSizeConstraintProvider scp = context.lookup(INodeSizeConstraintProvider.class);
    SizeD minimumSize = scp == null ? SizeD.EMPTY : scp.getMinimumSize(context);
    double width = Math.max(nl.getWidth(), minimumSize.getWidth());
    double height = minimumSize.getHeight();
    graph.setNodeLayout(context, new RectD(nl.getX(), nl.getY(), width, height));
  }

  /**
   * Returns the index of the label that contains the given point.
   */
  private static int indexOfLabelAt( INode context, PointD p ) {
    int idx = 0;
    for (ILabel label : context.getLabels()) {
      if (label.getLayout().contains(p, 0)) {
        return idx;
      }
      ++idx;
    }
    return -1;
  }

  /**
   * Selects the list item corresponding to the given label index.
   */
  private static void selectLabel( UmlClassModel model, int labelIndex ) {
    if (model.getAttributes() == getModelList(model, labelIndex)) {
      model.setSelectedList(UmlClassModel.LIST_ATTRIBUTES);
    } else {
      model.setSelectedList(UmlClassModel.LIST_OPERATIONS);
    }
    model.setSelectedListIndex(modelListIndexOf(model, labelIndex));
  }

  /**
   * Returns the area of the given label.
   */
  public static void getLabelArea( INode context, ILabel label, Rectangle2D rect ) {
    IRectangle nl = context.getLayout();
    IOrientedRectangle ll = label.getLayout();
    rect.setFrame(
            nl.getX(),
            ll.getAnchorY() - ll.getHeight(),
            nl.getWidth(),
            ll.getHeight());
  }

  /**
   * Returns the index of the given label that belongs to the given UML node.
   */
  private static int indexOfLabel( INode context, ILabel label ) {
    int labelIndex = 0;
    for (ILabel candidate : context.getLabels()) {
      if (label == candidate) {
        return labelIndex;
      }
      ++labelIndex;
    }
    return -1;
  }

  /**
   * Returns the label that is selected.
   */
  public static ILabel getSelectedLabel( INode context ) {
    int listIndex = getModel(context).getSelectedListIndex();
    if (listIndex < 0 || !isSelectedSectionVisible(context)) {
      return null;
    }

    int list = getModel(context).getSelectedList();
    int labelIndex = indexOfLabel(context, list, listIndex);
    return context.getLabels().getItem(labelIndex);
  }

  /**
   * Returns the index of the label corresponding to the item with the given index in the given list.
   */
  private static int indexOfLabel( INode context, int list, int index) {
    UmlClassModel model = getModel(context);
    if ((list == UmlClassModel.LIST_ATTRIBUTES) ||
        !model.areAttributesVisible()) {
      return index + 3;
    } else {
      return model.getAttributes().size() + index + 3;
    }
  }

  /**
   * Checks whether or not the section with the selection is visible.
   */
  private static boolean isSelectedSectionVisible( INode context ) {
    UmlClassModel model = getModel(context);
    int list = model.getSelectedList();
    return model.areSectionsVisible() &&
           (model.areAttributesVisible() && (list == UmlClassModel.LIST_ATTRIBUTES)) ||
           (model.areOperationsVisible() && (list == UmlClassModel.LIST_OPERATIONS));
  }

  /**
   * Returns the label of the class name.
   */
  static ILabel getNameLabel( INode context ) {
    IListEnumerable<ILabel> labels = context.getLabels();
    return labels.size() > 0 ? labels.first() : null;
  }

  /**
   * Returns the label of the caption of the attribute section.
   */
  static ILabel getAttributeCaptionLabel(INode context) {
    IListEnumerable<ILabel> labels = context.getLabels();
    return labels.size() > 1 ? labels.getItem(1) : null;
  }

  /**
   * Returns the label of the caption of the operation section.
   */
  static ILabel getOperationCaptionLabel(INode context) {
    IListEnumerable<ILabel> labels = context.getLabels();
    return labels.size() > 2 ? labels.getItem(2) : null;
  }

  /**
   * Checks whether or not the given label is a caption label e.g. shows "Attributes" or "Operations".
   */
  static boolean isCaptionLabel( ILabel label ) {
    int labelIndex = indexOfLabel((INode) label.getOwner(), label);
    return (labelIndex == 1) || (labelIndex == 2);
  }

  /**
   * Adopts the text from the given label for the corresponding feature of
   * the model associated to the given node.
   */
  static void adopt( INode context, ILabel label ) {
    UmlClassModel model = getModel(context);

    int idx = indexOfLabel(context, label);
    if (idx == 0) {
      model.setClassName(label.getText());
    } else if (idx > 2) {
      List<String> modelList = getModelList(model, idx);
      int modelListIndex = modelListIndexOf(model, idx);
      modelList.set(modelListIndex, label.getText());
    }
  }



  private static final class LabelDefaults {
    static final LabelDefaults INSTANCE = new LabelDefaults();


    final ILabelStyle defaultStyle;
    final ILabelStyle captionStyle;
    final ILabelStyle nameStyle;

    private LabelDefaults() {
      Font font = new Font("Dialog", Font.PLAIN, 14);

      DefaultLabelStyle dls = new DefaultLabelStyle();
      dls.setFont(font);
      dls.setTextClippingEnabled(false);
      dls.setTextPaint(Color.DARK_GRAY);
      dls.setUsingFractionalFontMetricsEnabled(true);
      defaultStyle = dls;

      DefaultLabelStyle cls = new DefaultLabelStyle();
      cls.setFont(font);
      cls.setTextClippingEnabled(false);
      cls.setTextPaint(Color.WHITE);
      cls.setUsingFractionalFontMetricsEnabled(true);
      captionStyle = cls;

      DefaultLabelStyle nls = new DefaultLabelStyle();
      nls.setFont(font.deriveFont(Font.BOLD, 16));
      nls.setInsets(new InsetsD(10, 0, 15, 0));
      nls.setTextClippingEnabled(false);
      nls.setTextPaint(Color.WHITE);
      nls.setUsingFractionalFontMetricsEnabled(true);
      nameStyle = nls;
    }
  }
}
