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
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Provides visualization and interaction for UML nodes.
 */
public class UmlNodeStyle extends AbstractNodeStyle {
  static final int SECTION_ICON_GAP = 5;
  static final int SECTION_ICON_SIZE = 12;
  static final int DETAILS_ICON_GAP = 5;
  static final int DETAILS_ICON_SIZE = 16;

  private boolean visible;

  public UmlNodeStyle() {
    this(true);
  }

  public UmlNodeStyle(boolean visible) {
    this.visible = visible;
  }

  /**
   * Provides access to {@link UmlClickHandler} and {@link UmlNodeSizeProvider}.
   */
  @Override
  protected Object lookup( INode node, Class type ) {
    if (type == IClickListener.class) {
      return new UmlClickHandler(node);
    } else if (type == INodeSizeConstraintProvider.class) {
      return new UmlNodeSizeProvider();
    } else {
      return super.lookup(node, type);
    }
  }

  /**
   * Creates the basic visualization of UML nodes.
   * The actual data associated to UML nodes is visualized through node labels.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    RectD nl = new RectD(node.getLayout());
    UmlClassModel model = UmlClassLabelSupport.getModel(node);

    if (model == null) {
      UmlNodeVisual visual = new UmlNodeVisual();
      visual.setVisible(isVisible());
      visual.setNodeLayout(nl);
      visual.setNameLayout(new RectD(20, 0, 50, 44));
      return visual;
    } else {
      UmlNodeVisual visual = new UmlNodeVisual();
      visual.setVisible(isVisible());

      boolean sectionsVisible = model.areSectionsVisible();
      visual.setNodeLayout(nl);
      visual.setSectionsVisible(sectionsVisible);
      visual.setAttributesVisible(model.areAttributesVisible());
      visual.setOperationsVisible(model.areOperationsVisible());
      visual.setControlOpacity(OpacityProvider.INSTANCE.getOpacity(node));

      ILabel nameLabel = UmlClassLabelSupport.getNameLabel(node);
      visual.setNameLayout(getRelativeBounds(nl, nameLabel));

      if (sectionsVisible) {
        ILabel attributeCaptionLabel = UmlClassLabelSupport.getAttributeCaptionLabel(node);
        visual.setAttributeCaptionLayout(getRelativeBounds(nl, attributeCaptionLabel));

        ILabel operationCaptionLabel = UmlClassLabelSupport.getOperationCaptionLabel(node);
        visual.setOperationCaptionLayout(getRelativeBounds(nl, operationCaptionLabel));

        ILabel selectedLabel = UmlClassLabelSupport.getSelectedLabel(node);
        if (selectedLabel == null) {
          visual.setSelectedLayout(RectD.EMPTY);
        } else {
          visual.setSelectedLayout(getRelativeBounds(nl, selectedLabel));
        }
      }

      return visual;
    }
  }

  /**
   * Determines the bounds of the given label in relation to the upper left
   * corner of the given reference rectangle.
   */
  static RectD getRelativeBounds( RectD reference, ILabel label ) {
    IOrientedRectangle ll = label.getLayout();
    double h = ll.getHeight();
    return new RectD(
            ll.getAnchorX() - reference.getX(),
            ll.getAnchorY() - h - reference.getY(),
            ll.getWidth(), h);
  }

  /**
   * Calculates the area for the details button.
   * The details button may be used to show or hide the attributes and
   * operations sections of an UML node.
   */
  static void calcDetailsArea(
          RectD nodeLayout, RectangularShape rect
  ) {
    rect.setFrame(
            nodeLayout.getX() + nodeLayout.getWidth()- DETAILS_ICON_SIZE - DETAILS_ICON_GAP,
            nodeLayout.getY() + DETAILS_ICON_GAP,
            DETAILS_ICON_SIZE,
            DETAILS_ICON_SIZE);
  }

  /**
   * Calculates the area for the attributes or operations section button.
   * The section button may be used to show or hide the corresponding section
   * of an UML node (i.e. either the attributes section or the operations
   * sections).
   */
  static void calcSectionArea(
          RectD nodeLayout, RectD labelLayout, RectangularShape rect
  ) {
    rect.setFrame(
            nodeLayout.getX() + SECTION_ICON_GAP,
            nodeLayout.getY() + labelLayout.getY() + (labelLayout.getHeight() - SECTION_ICON_SIZE) * 0.5,
            SECTION_ICON_SIZE, SECTION_ICON_SIZE);
  }

  /**
   * Calculates the area for the add item or remove item button.
   * The add item buttom may be used to add either an attribute or an operation
   * to an UML node (and its underlying data model).
   * The remove item button may be used to remove either an attribute or an
   * operation from an UML node (and its underlying data model).
   */
  static void calcItemArea(
          RectD nodeLayout, RectD labelLayout, boolean add, RectangularShape rect
  ) {
    int factor = add ? 2 : 1;
    rect.setFrame(
            nodeLayout.getX() + nodeLayout.getWidth()- factor * (SECTION_ICON_SIZE + SECTION_ICON_GAP),
            nodeLayout.getY() + labelLayout.getY() + (labelLayout.getHeight() - SECTION_ICON_SIZE) * 0.5,
            SECTION_ICON_SIZE,
            SECTION_ICON_SIZE);
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }


  /**
   * Visualizes (the backround of) UML nodes.
   */
  private static final class UmlNodeVisual implements IVisual {
    private static final Color COLOR_BACKGROUND = UmlStyleFactory.COLOR_BACKGROUND;
    private static final Color COLOR_FOREGROUND = UmlStyleFactory.COLOR_FOREGROUND;
    private static final Color COLOR_SELECTION = UmlStyleFactory.COLOR_SELECTION;


    private final BasicStroke borderStroke;
    private final BasicStroke iconStroke;
    private RectD nodeLayout;
    private RectD nameLayout;
    private RectD attributeCaptionLayout;
    private RectD operationCaptionLayout;
    private RectD selectedLayout;
    private boolean visible;
    private boolean sectionsVisible;
    private boolean attributesVisible;
    private boolean operationsVisible;
    private float controlOpacity;

    UmlNodeVisual() {
      nodeLayout = RectD.EMPTY;
      nameLayout = RectD.EMPTY;
      attributeCaptionLayout = RectD.EMPTY;
      operationCaptionLayout = RectD.EMPTY;
      selectedLayout = RectD.EMPTY;
      borderStroke = new BasicStroke(2);
      iconStroke = new BasicStroke(1);
      visible = true;
      controlOpacity = 0.0f;
    }

    void setNodeLayout( RectD bounds ) {
      this.nodeLayout = bounds;
    }

    void setNameLayout( RectD bounds ) {
      this.nameLayout = bounds;
    }

    void setAttributeCaptionLayout( RectD bounds ) {
      this.attributeCaptionLayout = bounds;
    }

    void setOperationCaptionLayout( RectD bounds ) {
      this.operationCaptionLayout = bounds;
    }

    void setSelectedLayout( RectD bounds ) {
      this.selectedLayout = bounds;
    }

    void setVisible(boolean visible) {
      this.visible = visible;
    }

    void setSectionsVisible(boolean visible ) {
      this.sectionsVisible = visible;
    }

    void setAttributesVisible( boolean visible ) {
      this.attributesVisible = visible;
    }

    void setOperationsVisible( boolean visible ) {
      this.operationsVisible = visible;
    }

    void setControlOpacity( float opacity ) {
      this.controlOpacity = opacity;
    }

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      if (!visible) {
        return;
      }

      Rectangle2D.Double rect = new Rectangle2D.Double();
      Ellipse2D.Double ellipse = new Ellipse2D.Double();

      Graphics2D graphics = (Graphics2D) g.create();
      Color oldColor = graphics.getColor();

      // Paint background.
      graphics.setColor(COLOR_BACKGROUND);
      adopt(rect, nodeLayout);
      graphics.fill(rect);

      // Paint area of the name.
      graphics.setColor(COLOR_FOREGROUND);
      calcArea(nodeLayout, nameLayout, rect);
      graphics.fill(rect);

      if (sectionsVisible) {
        // Paint area of the attribute caption.
        graphics.setColor(COLOR_FOREGROUND);
        calcArea(nodeLayout, attributeCaptionLayout, rect);
        graphics.fill(rect);

        // Paint area of the operation caption.
        graphics.setColor(COLOR_FOREGROUND);
        calcArea(nodeLayout, operationCaptionLayout, rect);
        graphics.fill(rect);

        // Opacity for fade in/fade out effect for selected features and
        // add/remove feature buttons
        float opacity = this.controlOpacity;
        Composite composite = 0f <= opacity && opacity <= 1f
                ? AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
                : graphics.getComposite();

        // Paint area of the selected label.
        if (!selectedLayout.equals(RectD.EMPTY)) {
          Composite oldComposite = graphics.getComposite();
          graphics.setComposite(composite);
          graphics.setColor(COLOR_SELECTION);
          calcArea(nodeLayout, selectedLayout, rect);
          graphics.fill(rect);
          graphics.setComposite(oldComposite);
        }

        // Paint buttons.
        // open/close attribute section
        paintSectionButton(attributeCaptionLayout, attributesVisible, rect, graphics);
        // add/remove attributes
        if (attributesVisible) {
          Composite oldComposite = graphics.getComposite();
          graphics.setComposite(composite);
          paintItemButton(attributeCaptionLayout, true, ellipse, graphics);
          paintItemButton(attributeCaptionLayout, false, ellipse, graphics);
          graphics.setComposite(oldComposite);
        }
        // open/close operations section
        paintSectionButton(operationCaptionLayout, operationsVisible, rect, graphics);
        // add/remove operations
        if (operationsVisible) {
          Composite oldComposite = graphics.getComposite();
          graphics.setComposite(composite);
          paintItemButton(operationCaptionLayout, true, ellipse, graphics);
          paintItemButton(operationCaptionLayout, false, ellipse, graphics);
          graphics.setComposite(oldComposite);
        }
      }

      // Paint buttons.
      graphics.setRenderingHint(
              RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_OFF);
      // open/close sections
      calcDetailsArea(nodeLayout, rect);
      graphics.setColor(COLOR_BACKGROUND);
      graphics.fill(rect);
      Shape shape = sectionsVisible ? getCloseShape(rect) : getOpenShape(rect);
      graphics.setColor(COLOR_FOREGROUND);
      graphics.setStroke(iconStroke);
      graphics.draw(shape);

      // Paint outline.
      adopt(rect, nodeLayout);
      graphics.setColor(COLOR_FOREGROUND);
      graphics.setStroke(borderStroke);
      graphics.draw(rect);

      graphics.dispose();
    }

    private void paintItemButton(
            RectD labelLayout, boolean add,
            RectangularShape scratch,
            Graphics2D graphics
    ) {
      calcItemArea(nodeLayout, labelLayout, add, scratch);
      graphics.setRenderingHint(
              RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      graphics.setColor(COLOR_BACKGROUND);
      graphics.fill(scratch);
      graphics.setRenderingHint(
              RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_OFF);
      Shape shape = add ? getPlusShape(scratch) : getMinusShape(scratch);
      graphics.setColor(COLOR_FOREGROUND);
      graphics.setStroke(iconStroke);
      graphics.draw(shape);
    }

    private void paintSectionButton(
            RectD labelLayout, boolean labelVisible,
            RectangularShape scratch,
            Graphics2D graphics
    ) {
      graphics.setRenderingHint(
              RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_OFF);

      calcSectionArea(nodeLayout, labelLayout, scratch);
      graphics.setColor(COLOR_BACKGROUND);
      graphics.fill(scratch);

      Shape shape = labelVisible ? getMinusShape(scratch) : getPlusShape(scratch);
      graphics.setColor(COLOR_FOREGROUND);
      graphics.setStroke(iconStroke);
      graphics.draw(shape);
    }

    private static void adopt( Rectangle2D r, RectD bounds ) {
      r.setFrame(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    private static void calcArea( RectD nl, RectD ll, Rectangle2D rect ) {
      rect.setFrame(nl.getX(), nl.getY() + ll.getY(), nl.getWidth(), ll.getHeight());
    }

    /**
     * Returns the shape of the icon of the closed state.
     */
    private static Shape getMinusShape( RectangularShape area ) {
      double minX = area.getX() + area.getWidth() * 0.25;
      double maxX = area.getX() + area.getWidth() * 0.75;
      double midY = area.getY() + area.getHeight() * 0.5;

      Path2D.Double icon = new Path2D.Double();
      icon.moveTo(minX, midY);
      icon.lineTo(maxX, midY);
      return icon;
    }

    /**
     * Returns the shape of the icon of the opened state.
     */
    private static Shape getPlusShape( RectangularShape area ) {
      double minX = area.getX() + area.getWidth() * 0.25;
      double midX = area.getX() + area.getWidth() * 0.5;
      double maxX = area.getX() + area.getWidth() * 0.75;
      double minY = area.getY() + area.getHeight() * 0.25;
      double midY = area.getY() + area.getHeight() * 0.5;
      double maxY = area.getY() + area.getHeight() * 0.75;

      Path2D.Double icon = new Path2D.Double();
      icon.moveTo(minX, midY);
      icon.lineTo(maxX, midY);
      icon.moveTo(midX, minY);
      icon.lineTo(midX, maxY);
      return icon;
    }

    /**
     * Returns the shape of the icon of the closed state.
     */
    private Shape getCloseShape( RectangularShape area ) {
      double minX = area.getX() + area.getWidth() * 0.25;
      double midX = area.getX() + area.getWidth() * 0.5;
      double maxX = area.getX() + area.getWidth() * 0.75;
      double minY = area.getY() + area.getHeight() * 0.25;
      double midY = area.getY() + area.getHeight() * 0.5;
      double maxY = area.getY() + area.getHeight() * 0.75;

      Path2D.Double icon = new Path2D.Double();
      icon.moveTo(minX, midY);
      icon.lineTo(midX, minY);
      icon.lineTo(maxX, midY);
      icon.moveTo(minX, maxY);
      icon.lineTo(midX, midY);
      icon.lineTo(maxX, maxY);

      return icon;
    }

    /**
     * Returns the shape of the icon of the opened state.
     */
    private Shape getOpenShape( RectangularShape area ) {
      double minX = area.getX() + area.getWidth() * 0.25;
      double midX = area.getX() + area.getWidth() * 0.5;
      double maxX = area.getX() + area.getWidth() * 0.75;
      double minY = area.getY() + area.getHeight() * 0.25;
      double midY = area.getY() + area.getHeight() * 0.5;
      double maxY = area.getY() + area.getHeight() * 0.75;

      Path2D.Double icon = new Path2D.Double();
      icon.moveTo(minX, minY);
      icon.lineTo(midX, midY);
      icon.lineTo(maxX, minY);
      icon.moveTo(minX, midY);
      icon.lineTo(midX, maxY);
      icon.lineTo(maxX, midY);
      return icon;
    }
  }

  /**
   * Handles user interaction with the details, section, and add/remove item
   * buttons of an UML node.
   */
  private static class UmlClickHandler implements IClickListener, IHitTestable {
    private static final int ACTION_NONE = 0;
    private static final int ACTION_DETAILS_SHOW = 1;
    private static final int ACTION_ATTRS_SHOW = 2;
    private static final int ACTION_ATTRS_ADD = 3;
    private static final int ACTION_ATTRS_REMOVE = 4;
    private static final int ACTION_OPS_SHOW = 5;
    private static final int ACTION_OPS_ADD = 6;
    private static final int ACTION_OPS_REMOVE = 7;


    final INode node;

    UmlClickHandler( INode node ) {
      this.node = node;
    }

    @Override
    public IHitTestable getHitTestable() {
      return this;
    }

    /**
     * Handle clicks on one of the buttons displayed for an UML node.
     */
    @Override
    public void onClicked( IInputModeContext context, PointD location ) {
      switch (getHitAction(context, node, location)) {
        case ACTION_DETAILS_SHOW:
          showDetails(context, node);
          break;
        case ACTION_ATTRS_SHOW:
          showAttributes(context, node);
          break;
        case ACTION_ATTRS_ADD:
          UmlNodeAnimation.createAddAttributeAnimation((GraphComponent) context.getCanvasComponent(), node).play();
          break;
        case ACTION_ATTRS_REMOVE:
          removeItem(context, node, true);
          break;
        case ACTION_OPS_SHOW:
          showOperations(context, node);
          break;
        case ACTION_OPS_ADD:
          UmlNodeAnimation.createAddOperationAnimation((GraphComponent) context.getCanvasComponent(), node).play();
          break;
        case ACTION_OPS_REMOVE:
          removeItem(context, node, false);
          break;
        // default:
        //   do nothing
      }
    }

    private static void removeItem( IInputModeContext context, INode node, boolean attribute ) {
      UmlClassModel model = UmlClassLabelSupport.getModel(node);
      int selectedListId = attribute ? UmlClassModel.LIST_ATTRIBUTES : UmlClassModel.LIST_OPERATIONS;
      if (model.getSelectedList() == selectedListId && model.getSelectedListIndex() > -1) {
        UmlNodeAnimation.createRemoveItemAnimation((GraphComponent) context.getCanvasComponent(), node).play();
      }
    }

    private static void showDetails( IInputModeContext context, INode node ) {
      UmlClassModel model = UmlClassLabelSupport.getModel(node);
      GraphComponent graphComponent = (GraphComponent) context.getCanvasComponent();
      UmlNodeAnimation.createDetailSectionAnimation(graphComponent, node, model.areSectionsVisible()).play();
    }

    private static void showAttributes(IInputModeContext context, INode node ) {
      UmlClassModel model = UmlClassLabelSupport.getModel(node);
      GraphComponent graphComponent = (GraphComponent) context.getCanvasComponent();
      UmlNodeAnimation.createAttributeSectionAnimation(graphComponent, node, model.areAttributesVisible()).play();
    }

    private static void showOperations( IInputModeContext context, INode node ) {
      UmlClassModel model = UmlClassLabelSupport.getModel(node);
      GraphComponent graphComponent = (GraphComponent) context.getCanvasComponent();
      UmlNodeAnimation.createOperationSectionAnimation(graphComponent, node, model.areOperationsVisible()).play();
    }

    /**
     * Determine if a click occurred on one of the buttons displayed for an
     * UML node.
     */
    @Override
    public boolean isHit( IInputModeContext context, PointD location ) {
      return getHitAction(context, node, location) != ACTION_NONE;
    }

    /**
     * Determine the type of action appropriate for the given location.
     */
    private static int getHitAction(
            IInputModeContext context, INode node, PointD location
    ) {
      Rectangle2D.Double scratch = new Rectangle2D.Double();

      RectD nodeLayout = new RectD(node.getLayout());
      calcDetailsArea(nodeLayout, scratch);
      if (scratch.contains(location.getX(), location.getY())) {
        return ACTION_DETAILS_SHOW;
      } else {
        UmlClassModel model = UmlClassLabelSupport.getModel(node);
        if (model.areSectionsVisible()) {
          ILabel captionLabel = UmlClassLabelSupport.getAttributeCaptionLabel(node);
          RectD labelBnds = getRelativeBounds(nodeLayout, captionLabel);
          calcSectionArea(nodeLayout, labelBnds, scratch);
          if (scratch.contains(location.getX(), location.getY())) {
            return ACTION_ATTRS_SHOW;
          }
          if (model.areAttributesVisible()) {
            calcItemArea(nodeLayout, labelBnds, true, scratch);
            if (scratch.contains(location.getX(), location.getY())) {
              return ACTION_ATTRS_ADD;
            }

            calcItemArea(nodeLayout, labelBnds, false, scratch);
            if (scratch.contains(location.getX(), location.getY())) {
              return ACTION_ATTRS_REMOVE;
            }
          }

          captionLabel = UmlClassLabelSupport.getOperationCaptionLabel(node);
          labelBnds = getRelativeBounds(nodeLayout, captionLabel);
          calcSectionArea(nodeLayout, labelBnds, scratch);
          if (scratch.contains(location.getX(), location.getY())) {
            return ACTION_OPS_SHOW;
          }
          if (model.areOperationsVisible()) {
            calcItemArea(nodeLayout, labelBnds, true, scratch);
            if (scratch.contains(location.getX(), location.getY())) {
              return ACTION_OPS_ADD;
            }

            calcItemArea(nodeLayout, labelBnds, false, scratch);
            if (scratch.contains(location.getX(), location.getY())) {
              return ACTION_OPS_REMOVE;
            }
          }
        }
        return ACTION_NONE;
      }
    }
  }

}
