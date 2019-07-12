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
package complete.isometric;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * A node style that adds a flat header ribbon with an open/close state button
 * to another style.
 * This style is intended for group nodes that are represented as flat rhomboids
 * in an isometric fashion.
 */
public class GroupNodeStyle extends AbstractNodeStyle {
  /**
   * Returns the gap around a collapse button.
   */
  static final int ICON_GAP = 2;

  /**
   * Returns height of a collapse button.
   */
  static final int ICON_HEIGHT = 18;

  /**
   * Returns width of a collapse button.
   */
  static final int ICON_WIDTH = 18;

  /** Stores the node style for the actual node rendering. */
  private INodeStyle wrapped;
  /** Stores the color for rendering the header ribbon. */
  private Color fill;

  /**
   * Initializes a new group node style.
   */
  public GroupNodeStyle() {
    NodeStyle style = new NodeStyle();
    style.setBorder(null);
    style.setFill(new Color(202, 236, 255, 128));
    wrapped = style;
    fill = new Color(153, 204, 255);
  }

  /**
   * Returns the node style used for rendering the node.
   */
  public INodeStyle getWrapped() {
    return wrapped;
  }

  /**
   * Sets the node style used for rendering the node.
   */
  public void setWrapped( INodeStyle wrapped ) {
    this.wrapped = wrapped;
  }

  /**
   * Returns the color for rendering the header ribbon.
   */
  public Color getFill() {
    return fill;
  }

  /**
   * Sets the color for rendering the header ribbon.
   */
  public void setFill( Color fill ) {
    this.fill = fill;
  }


  @Override
  protected GeneralPath getOutline( INode node ) {
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getOutline();
  }

  @Override
  protected RectD getBounds( ICanvasContext context, INode node ) {
    return wrapped.getRenderer().getBoundsProvider(node, wrapped).getBounds(context);
  }

  @Override
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    return wrapped.getRenderer().getHitTestable(node, wrapped).isHit(context, location);
  }

  @Override
  protected boolean isInside( INode node, PointD location ) {
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).isInside(location);
  }

  @Override
  protected boolean isVisible( ICanvasContext context, RectD rectangle, INode node ) {
    return wrapped.getRenderer().getVisibilityTestable(node, wrapped).isVisible(context, rectangle);
  }

  @Override
  protected PointD getIntersection( INode node, PointD inner, PointD outer ) {
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getIntersection(inner, outer);
  }

  @Override
  protected Object lookup( INode node, Class type ) {
    if (IClickListener.class.equals(type)) {
      return new MyClickListener(node);
    }
    return super.lookup(node, type);
  }

  /**
   * Creates the visual representations for nodes using this style.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    HeaderVisual header = new HeaderVisual();
    // calculate the corners of the node in the view space.
    header.corners = NodeStyle.corners(node);
    header.collapsed = isCollapsed(node, context.getCanvasComponent());
    header.width = IsometricGeometry.get(node).getWidth();
    ILabel label = first(node.getLabels());
    header.labelHeight = label == null ? 0 : label.getLayout().getHeight();
    header.fill = getFill();

    VisualGroup group = new VisualGroup();
    group.add(wrapped.getRenderer().getVisualCreator(node, wrapped).createVisual(context));
    group.add(header);
    return group;
  }

  /**
   * Returns whether or not the given group node is collapsed.
   */
  private static boolean isCollapsed( INode node, CanvasComponent component ) {
    if (!(component instanceof GraphComponent)) {
      return false;
    }
    IFoldingView foldingView = ((GraphComponent) component).getGraph().getFoldingView();
    // check if given node is in graph
    if (foldingView == null || !foldingView.getGraph().contains(node)) {
      return false;
    }
    // check if the node really is a group in the master graph
    return !foldingView.isExpanded(node);
  }

  private static <T> T first( IEnumerable<T> enumerable ) {
    Iterator<T> it = enumerable.iterator();
    return it.hasNext() ? it.next() : null;
  }


  /**
   * Handles the actual header and open/close state button rendering in an
   * isometric fashion.
   */
  private static final class HeaderVisual implements IVisual {
    Color fill;
    double[] corners;
    double width;
    double labelHeight;
    boolean collapsed;

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      // save the original graphics context
      AffineTransform oldTransform = g.getTransform();
      Color oldColor = g.getColor();

      // the lower corner is the anchor of the label.
      double anchorX = corners[IsometricTransformationSupport.C3_X];
      double anchorY = corners[IsometricTransformationSupport.C3_Y];

      // set the transformation from the layout space into the view space on the graphics context.
      g.translate(anchorX, anchorY);
      g.transform(new AffineTransform(
              IsometricTransformationSupport.M_TO_VIEW_11,
              IsometricTransformationSupport.M_TO_VIEW_21,
              IsometricTransformationSupport.M_TO_VIEW_12,
              IsometricTransformationSupport.M_TO_VIEW_22,
              0, 0));
      g.translate(-anchorX, -anchorY);


      // paint label background
      // Calculate the box of the label in the layout space. It uses the whole width of the node.
      double headerHeight = GroupNodeStyle.ICON_HEIGHT + (2 * GroupNodeStyle.ICON_GAP);
      headerHeight = Math.max(headerHeight, labelHeight);
      Rectangle2D.Double header = new Rectangle2D.Double(anchorX, anchorY - headerHeight, width, headerHeight);
      g.setColor(fill);
      g.fill(header);

      // paint group state icon
      // determine position of the icon
      double x = anchorX + ICON_GAP;
      double y = anchorY - ICON_HEIGHT - ICON_GAP;
      Rectangle2D.Double button = new Rectangle2D.Double(x, y, ICON_WIDTH, ICON_HEIGHT);

      // paint icon border
      g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
      g.setColor(Color.WHITE);
      g.fill(button);
      g.setColor(Color.BLACK);
      g.draw(button);

      // paint "+" (folder) or "-" (group)
      Line2D line = new Line2D.Double(x + ICON_WIDTH * 0.25, y + ICON_HEIGHT * 0.5,
                                      x + ICON_WIDTH * 0.75, y + ICON_HEIGHT * 0.5);
      g.draw(line);
      if (collapsed) {
        line.setLine(x + ICON_WIDTH * 0.5, y + ICON_HEIGHT * 0.25,
                     x + ICON_WIDTH * 0.5, y + ICON_HEIGHT * 0.75);
        g.draw(line);
      }


      // Restore the original graphics context.
      g.setTransform(oldTransform);
      g.setColor(oldColor);
    }
  }



  /**
   * Closes and opens group nodes on open/close state button clicks.
   */
  private static class MyClickListener implements IClickListener, IHitTestable {
    private final INode node;

    MyClickListener( INode node ) {
      this.node = node;
    }

    /*
     * ###################################################################
     * IClickListener
     * ###################################################################
     */

    @Override
    public IHitTestable getHitTestable() {
      return this;
    }

    @Override
    public void onClicked( IInputModeContext context, PointD location ) {
      ICommand.TOGGLE_EXPANSION_STATE.execute(node, context.getCanvasComponent());
    }

    /*
     * ###################################################################
     * IHitTestable
     * ###################################################################
     */

    public boolean isHit( IInputModeContext context, PointD location ) {
      // calculate the corners of the node in the view space.
      IsometricGeometry geometry = IsometricGeometry.get(node);
      double[] corners = IsometricTransformationSupport.calculateCorners(
              node.getLayout(), geometry);

      // the lower corner is the anchor of the label.
      double anchorX = corners[IsometricTransformationSupport.C3_X];
      double anchorY = corners[IsometricTransformationSupport.C3_Y];

      // move the given mouse coordinates by anchor and transform them into layout space
      // that way, the hit test can use a non-transformed rectangle
      double x = location.getX();
      double y = location.getY();
      double mouseX = IsometricTransformationSupport.toLayoutX(x - anchorX, y - anchorY);
      double mouseY = IsometricTransformationSupport.toLayoutY(x - anchorX, y - anchorY);

      // return whether or not the mouse is located in the icons rectangle
      return mouseX >  ICON_GAP && mouseX <  ICON_WIDTH  + ICON_GAP &&
             mouseY < -ICON_GAP && mouseY > -ICON_HEIGHT - ICON_GAP;
    }
  }
}
