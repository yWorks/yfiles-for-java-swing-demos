/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step26_CustomGroupStyle;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.utils.ImageSupport;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.ISize;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/////////////// This class is new in this sample ///////////////

/**
 * An implementation of an {@link com.yworks.yfiles.graph.styles.INodeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractNodeStyle} as its base class.
 * <p>
 * This style is designed explicitly for group nodes. It paints the node as a file card with a tab in the upper left
 * corner. Narrow nodes have a narrow tab that only
 * contains the collapse button of the {@link tutorial02_CustomStyles.step26_CustomGroupStyle.MyCollapsibleNodeStyleDecorator}.
 * If the node is wide enough, the tab also contains text beside the button.
 * </p>
 */
public class MyGroupNodeStyle extends AbstractNodeStyle {
  private static final Color DEFAULT_NODE_COLOR = new Color(0, 130, 180, 200);
  // bounds of the tab shape
  private static final int TAB_HEIGHT = 16;
  private static final int TAB_WIDTH = 64;
  private static final int SMALL_TAB_WIDTH = 18;
  // radius of the outer path of the node
  private static final int OUTER_RADIUS = 5;
  // radius of the inner path of the node
  private static final int INNER_RADIUS = 3;
  // inset between the outer and inner path
  private static final int INSET = 2;

  private Color nodeColor;

  /**
   * Initializes a new <code>MyGroupNodeStyle</code> instance with a default node color.
   */
  public MyGroupNodeStyle() {
    super();
    nodeColor = DEFAULT_NODE_COLOR;
  }

  /**
   * Returns the fill color of the node.
   */
  public Color getNodeColor() {
    return nodeColor;
  }

  /**
   * Sets the fill color of the node.
   */
  public void setNodeColor(Color nodeColor) {
    this.nodeColor = nodeColor;
  }

  /**
   * Determines the color to use for filling the node.
   * This implementation uses {@link #getNodeColor()} unless the {@link com.yworks.yfiles.graph.ITagOwner#getTag()} of
   * the {@link com.yworks.yfiles.graph.INode} is of type {@link java.awt.Color}, in which case that color overrides
   * this style's setting.
   * @param node The node to determine the color for.
   * @return The color for filling the node.
   */
  private Color getNodeColor(INode node) {
    return node.getTag() instanceof Color ? (Color)node.getTag() : getNodeColor();
  }

  /**
   * Creates the visual for a node.
   */
  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    TabVisual visual = new TabVisual();
    visual.update(getNodeColor(node), node.getLayout(), isCollapsed(node, (GraphComponent) context.getCanvasComponent()));
    return visual;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, INode)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual group, INode node) {
    TabVisual visual = (TabVisual) group;
    visual.update(getNodeColor(node), node.getLayout(), isCollapsed(node, (GraphComponent) context.getCanvasComponent()));
    return visual;
  }

  /**
   * Checks whether or not a given group node is collapsed.
   */
  private static boolean isCollapsed(INode node, GraphComponent graphComponent) {
    if (graphComponent != null) {
      IGraph foldedGraph = graphComponent.getGraph();
      IFoldingView foldingView = foldedGraph.getFoldingView();
      if (foldingView != null) {
        // check if the node really is a group in the master graph
        boolean isGroupNode = foldingView.getManager().getMasterGraph().isGroupNode(foldingView.getMasterItem(node));
        // check if the node is collapsed in the view graph
        return isGroupNode && !foldedGraph.isGroupNode(node);
      }
    }
    return false;
  }

  /**
   * Determines if the given point lies in the file card shape that represents
   * the given node.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    double x = node.getLayout().getX();
    double y = node.getLayout().getY();
    double w = node.getLayout().getWidth();
    double h = node.getLayout().getHeight();
    RectD rect = new RectD(x, y + TAB_HEIGHT, w, h - TAB_HEIGHT);
    // check main node rect
    if (rect.contains(location, context.getHitTestRadius())) {
      return true;
    }
    // check tab
    int tabWidth = useLargeTab(w) ? TAB_WIDTH : SMALL_TAB_WIDTH;
    return new RectD(x, y, tabWidth, TAB_HEIGHT).contains(location, context.getHitTestRadius());
  }

  /**
   * Returns the outline path to make connecting edges consider the tab shape.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    GeneralPath path = createOuterPath(node.getLayout());
    path.transform(new Matrix2D(1, 0, 0, 1, node.getLayout().getX(), node.getLayout().getY()));
    return path;
  }

  /**
   * Provides lookup access to the following custom implementations:
   * <ul>
   *   <li> {@link com.yworks.yfiles.view.input.INodeInsetsProvider} for preventing group contents from overlapping with the
   *   file card tab.</li>
   *   <li> {@link com.yworks.yfiles.view.input.INodeSizeConstraintProvider} for adjusting the minimum size to ensure that the
   *   node is wide enough for the small tab and the button corner.
   *   </li>
   * </ul>
   */
  @Override
  protected Object lookup(INode node, Class type) {
    if (type == INodeInsetsProvider.class) {
      // use a custom insets provider
      return new MyGroupInsetsProvider();
    }
    if (type == INodeSizeConstraintProvider.class) {
      // use a custom size constraint provider to constrain minimum size
      return new MySizeProvider();
    }
    return super.lookup(node, type);
  }

  /**
   * Creates the inner node path for the given node size.
   */
  private static GeneralPath createInnerPath(ISize size) {
    double w = size.getWidth();
    double h = size.getHeight();
    GeneralPath path = new GeneralPath();
    path.moveTo(INSET + INNER_RADIUS, INSET + TAB_HEIGHT);
    path.lineTo(w - INSET - INNER_RADIUS, INSET + TAB_HEIGHT);
    path.quadTo(w - INSET, INSET + TAB_HEIGHT, w - INSET, INSET + TAB_HEIGHT + INNER_RADIUS);
    path.lineTo(w - INSET, h - INSET - INNER_RADIUS);
    path.quadTo(w - INSET, h - INSET, w - INSET - INNER_RADIUS, h - INSET);
    path.lineTo(INSET + INNER_RADIUS, h - INSET);
    path.quadTo(INSET, h - INSET, INSET, h - INSET - INNER_RADIUS);
    path.lineTo(INSET, INSET + TAB_HEIGHT + INNER_RADIUS);
    path.quadTo(INSET, INSET + TAB_HEIGHT, INSET + INNER_RADIUS, INSET + TAB_HEIGHT);
    path.close();
    return path;
  }

  /**
   * Creates the outer node path for the given node size.
   */
  private static GeneralPath createOuterPath(ISize size) {
    double w = size.getWidth();
    double h = size.getHeight();
    int tabWidth = useLargeTab(w) ? TAB_WIDTH : SMALL_TAB_WIDTH;
    GeneralPath path = new GeneralPath();
    path.moveTo(tabWidth + OUTER_RADIUS, TAB_HEIGHT);
    path.lineTo(w - OUTER_RADIUS, TAB_HEIGHT);
    path.quadTo(w, TAB_HEIGHT, w, TAB_HEIGHT + OUTER_RADIUS);
    path.lineTo(w, h - OUTER_RADIUS);
    path.quadTo(w, h, w - OUTER_RADIUS, h);
    path.lineTo(OUTER_RADIUS, h);
    path.quadTo(0, h, 0, h - OUTER_RADIUS);
    path.lineTo(0, OUTER_RADIUS);
    path.quadTo(0, 0, OUTER_RADIUS, 0);
    path.lineTo(-OUTER_RADIUS + tabWidth, 0);
    path.quadTo(tabWidth, 0, tabWidth, OUTER_RADIUS);
    path.lineTo(tabWidth, TAB_HEIGHT - OUTER_RADIUS);
    path.quadTo(tabWidth, TAB_HEIGHT, tabWidth + OUTER_RADIUS, TAB_HEIGHT);
    path.close();
    return path;
  }

  /**
   * Checks whether the node is wide enough to display the large tab.
   */
  private static boolean useLargeTab(double width) {
    return width >= TAB_WIDTH + 2 * OUTER_RADIUS;
  }

  /**
   * Customizes group insets to prevent group contents from overlapping with the
   * tab of the group's file card shape.
   */
  private static class MyGroupInsetsProvider implements INodeInsetsProvider {
    private static final int INSET = 6;

    public InsetsD getInsets(INode item) {
      // use insets and respect the tab height
      return new InsetsD(TAB_HEIGHT + INSET, INSET, INSET, INSET);
    }
  }

  /**
   * Customizes the minimum node size to assure that the node is wide enough for the small tab.
   */
  private static class MySizeProvider implements INodeSizeConstraintProvider {
    public SizeD getMinimumSize(INode item) {
      // constrain minimum size to reasonable width and height
      return new SizeD(SMALL_TAB_WIDTH + 2 * OUTER_RADIUS, TAB_HEIGHT + OUTER_RADIUS);
    }

    public SizeD getMaximumSize(INode item) {
      // don't constrain maximum size
      return SizeD.INFINITE;
    }

    public RectD getMinimumEnclosedArea(INode item) {
      return RectD.EMPTY;
    }
  }

  /**
   * A {@link IVisual} that paints a file card with a tab in the upper left corner.  Note that
   * we paint the file card at the origin and move the graphics context to the current location of the node.
   */
  private static class TabVisual implements IVisual {
    private static final Matrix2D IDENTITY = new Matrix2D();

    // position, color and font of the text shown in the wide tab
    private static final float TEXT_POS_X = 20f;
    private static final float TEXT_POS_Y = 14f;
    private static final Color TEXT_COLOR = Color.decode("#333333");
    private static final Font TEXT_FONT = new Font("Dialog", Font.PLAIN, 14);

    // color of the file card
    private Color color;
    // size of the file card
    private SizeD size;
    // location of the file card
    private PointD location;

    // inner and outer path of the file card
    private Path2D.Double innerPath;
    private Path2D.Double outerPath;

    // fills of the paths
    private Paint innerFill;
    private Paint outerFill;

    // whether to paint the group node as collapsed or expanded
    boolean collapsed;

    private TabVisual() {
      innerPath = new Path2D.Double();
      outerPath = new Path2D.Double();
    }

    /**
     * Updates the color, size and location of the shapes painting the file card.
     * @param color  the color of the file card
     * @param layout the location and size of the file card
     * @param collapsed whether or not the group node is collapsed
     */
    void update(Color color, IRectangle layout, boolean collapsed) {
      // update the location of the file card
      this.location = layout.getTopLeft();
      // update the collapsed state
      this.collapsed = collapsed;

      // update the shape and fill only if color or size of the file card has been changed
      SizeD size = layout.toSizeD();
      if (!color.equals(this.color) || !size.equals(this.size)) {
        this.color = color;
        this.size = size;

        // update paths
        createInnerPath(size).updatePath(innerPath, IDENTITY);
        createOuterPath(size).updatePath(outerPath, IDENTITY);

        // update fills
        innerFill = ImageSupport.mix(color, Colors.WHITE, 0.1);
        outerFill = new GradientPaint(0, 0, ImageSupport.mix(color, Color.WHITE, 0.5), 0, (float) size.getHeight(), color);
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, work on a copy of the graphic context.
      Graphics2D gfx = (Graphics2D) g.create();
      try {
        // move the graphics context to the current location of the node
        gfx.translate(location.getX(), location.getY());

        // fill the outer path
        gfx.setPaint(outerFill);
        gfx.fill(outerPath);

        // fill the inner path
        gfx.setPaint(innerFill);
        gfx.fill(innerPath);

        // draw the text if we have the large tab
        if (useLargeTab(size.getWidth())) {
          String text = collapsed ? "Folder" : "Group";
          gfx.setColor(TEXT_COLOR);
          gfx.setFont(TEXT_FONT);
          gfx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
          gfx.drawString(text, TEXT_POS_X, TEXT_POS_Y);
        }
      } finally {
        // after all is done, dispose the copy
        gfx.dispose();
      }
    }
  }
}
