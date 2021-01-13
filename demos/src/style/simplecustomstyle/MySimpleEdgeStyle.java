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
package style.simplecustomstyle;

import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.EdgeSelectionIndicatorInstaller;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.ISelectionIndicatorInstaller;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Path2D;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.IEdgeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractEdgeStyle} as the base class.
 */
public class MySimpleEdgeStyle extends AbstractEdgeStyle {
  private double pathThickness;
  private IArrow sourceArrow;
  private IArrow targetArrow;

  /**
   * Returns the arrow at the beginning of the edge.
   */
  public IArrow getSourceArrow() {
    return sourceArrow;
  }

  /**
   * Specifies the arrow at the beginning of the edge.
   */
  public void setSourceArrow(IArrow arrow) {
    this.sourceArrow = arrow;
  }

  /**
   * Returns the arrow at the end of the edge.
   */
  public IArrow getTargetArrow() {
    return targetArrow;
  }

  /**
   * Specifies the arrow at the end of the edge.
   */
  public void setTargetArrow(IArrow arrow) {
    this.targetArrow = arrow;
  }

  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance using {@link MySimpleArrow} at both ends of the edge and
   * an edge thickness of 3.
   */
  public MySimpleEdgeStyle() {
    this(new MySimpleArrow(), 3);
  }

  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance using the given arrow at both ends of the edge and the
   * given edge thickness.
   */
  public MySimpleEdgeStyle(IArrow arrow, double pathThickness) {
    this(arrow, arrow, pathThickness);
  }
  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance using the given arrows and the given edge thickness.
   */
  public MySimpleEdgeStyle(IArrow sourceArrow, IArrow targetArrow, double pathThickness) {
    setSourceArrow(sourceArrow);
    setTargetArrow(targetArrow);
    setPathThickness(pathThickness);
  }

  /**
   * Gets the thickness of the edge. The default is 3.0.
   */
  public double getPathThickness() {
    return pathThickness;
  }

  /**
   * Sets the thickness of the edge. The default is 3.0.
   */
  public void setPathThickness(double pathThickness) {
    this.pathThickness = pathThickness;
  }

  /**
   * Creates the visual for an edge.
   */
  @Override
  protected VisualGroup createVisual(IRenderContext context, IEdge edge) {
    // create a group that holds all visuals needed to paint the edge
    VisualGroup group = new VisualGroup();
    GeneralPath path = getPath(edge);

    // create the visual that paints the edge path
    EdgeVisual edgeVisual = new EdgeVisual();
    edgeVisual.update(path, getPathThickness(), isSelected(edge, context), getColor(edge), getAnimationTime(edge));
    group.add(edgeVisual);

    // add visuals that paint the arrows
    addArrows(context, group, edge, path, getSourceArrow(), getTargetArrow());

    return group;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. You must not override this
   * method, but it is strongly recommended to do it for performance reasons. Otherwise, the {@link
   * #createVisual(IRenderContext, IEdge)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, IEdge edge) {
    if (!(oldVisual instanceof VisualGroup)) {
      return createVisual(context, edge);
    }

    VisualGroup group = (VisualGroup) oldVisual;
    GeneralPath path = getPath(edge);

    // update the visual that paints the edge path
    EdgeVisual edgeVisual = (EdgeVisual) group.getChildren().get(0);
    edgeVisual.update(path, getPathThickness(), isSelected(edge, context), getColor(edge), getAnimationTime(edge));

    // since the edge's ends might have changed their positions or orientations, we also have to update the visuals
    // painting the arrows
    updateArrows(context, group, edge, path, getSourceArrow(), getTargetArrow());

    return group;
  }

  /**
   * Returns the color to paint the edge with.
   */
  private Color getColor(IEdge edge) {
    INode owner = edge.getSourceNode();
    return ((MySimpleNodeStyle) owner.getStyle()).getNodeColor(owner);
  }

  /**
   * Returns whether the edge is selected ot not.
   */
  private boolean isSelected(IEdge edge, IRenderContext renderContext) {
    // we acquire the CanvasComponent instance from the render context and
    // fetch an IGraphSelection instance using its lookup. We can be sure
    // that those instances actually exist in this demo because we know that
    // our canvas is a GraphComponent.
    // This is equivalent to casting the canvas to GraphComponent and calling
    // the method GraphComponent#getSelection() from it.
    IGraphSelection selection = renderContext.getCanvasComponent().lookup(IGraphSelection.class);
    return selection.isSelected(edge);
  }

  /**
   * Returns the animation time of the given edge.
   */
  private static double getAnimationTime(IEdge edge) {
    return edge.getTag() instanceof Double ? (double) edge.getTag() : 0;
  }

  /**
   * Creates a {@link com.yworks.yfiles.geometry.GeneralPath} from the segments of the given edge.
   */
  @Override
  protected GeneralPath getPath(IEdge edge) {
    // create a general path from the locations of the ports and the bends of the edge.
    GeneralPath path = new GeneralPath();
    path.moveTo(getLocation(edge.getSourcePort()));
    edge.getBends().forEach(bend -> path.lineTo(bend.getLocation()));
    path.lineTo(getLocation(edge.getTargetPort()));

    // crop the edge's path at its nodes

    // take the arrows into account when cropping the path
    return cropPath(edge, getSourceArrow(), getTargetArrow(), path);
  }

  /**
   * Unlike {@link com.yworks.yfiles.graph.IPort#getLocation()} this does not return a dynamic point that always refers to the current location.
   * It is recommended to use this method at performance critical places that require no live view of the port location.
   * @param port The port to retrieve the location from.
   * @return The current port location.
   */
  private static PointD getLocation(IPort port) {
    IPortLocationModelParameter param = port.getLocationParameter();
    return param.getModel().getLocation(port, param);
  }

  /**
   * Determines whether the visual representation of the edge has been hit at the given location. Overridden method to
   * include the {@link #getPathThickness()} and the HitTestRadius specified in the context in the calculation.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD p, IEdge edge) {
    // use the convenience method in GeneralPath
    return getPath(edge).pathContains(p, context.getHitTestRadius() + getPathThickness() * 0.5);
  }

  /**
   * This implementation of the look up provides a custom implementation of the {@link
   * com.yworks.yfiles.view.ISelectionIndicatorInstaller} interface that better suits to this style.
   */
  @Override
  protected Object lookup(IEdge edge, Class type) {
    if (type == ISelectionIndicatorInstaller.class) {
      return new MySelectionInstaller();
    } else {
      return super.lookup(edge, type);
    }
  }

  /**
   * This customized {@link com.yworks.yfiles.view.ISelectionIndicatorInstaller} overrides the pen property to be
   * transparent, so that no edge path is rendered if the edge is selected.
   */
  private static class MySelectionInstaller extends EdgeSelectionIndicatorInstaller {
    @Override
    protected Pen getPen(final CanvasComponent canvas, final IEdge edge) {
      return Pen.getTransparent();
    }
  }


  /**
   * A {@link IVisual} that paints the line of the edge.
   */
  private static class EdgeVisual implements IVisual {
    // distance from which the gradient will repeat
    private static final float CYCLE_LENGTH = 20f;

    // the shape to paint the line of the edge
    private final Path2D.Double path;
    // stores the segments of the edge; used to create and update the path
    private GeneralPath generalPath;
    // the thickness of the edge
    private double pathThickness;
    // whether or not the edge is currently selected; used to determine the color of the edge
    private boolean selected;
    // the time of the animation
    private double animationTime;
    // animated gradient to paint the selected edge with
    private Paint animatedPaint;
    // the color used to paint the edge if it is not selected
    private Color color;

    private EdgeVisual() {
      this.path = new Path2D.Double();
    }

    /**
     * Checks if the path or the thickness of the edge has been changed. If so, updates all items needed to paint the
     * edge.
     * @param generalPath   the path of the edge
     * @param pathThickness the thickness of the edge
     * @param selected      whether or not the edge is selected
     * @param color         the color of the edge, if not selected
     * @param animationTime animation time of the gradient animation
     */
    public void update(GeneralPath generalPath, double pathThickness, boolean selected, Color color, double animationTime) {
      // update the path
      if (!generalPath.equals(this.generalPath)) {
        this.generalPath = generalPath;
        this.generalPath.updatePath(path, null);
      }

      this.pathThickness = pathThickness;
      this.selected = selected;
      this.color = color;

      if (selected && animationTime != this.animationTime) {
        this.animationTime = animationTime;

        // move the start position of the gradient along the animation time
        final float startPos = CYCLE_LENGTH * (float) this.animationTime;
        animatedPaint = new LinearGradientPaint(startPos, startPos, startPos + CYCLE_LENGTH, startPos + CYCLE_LENGTH,
            new float[]{0, 0.5f, 1},
            new Color[]{new Color(255, 215, 0), new Color(255, 245, 30), new Color(255, 215, 0)},
            MultipleGradientPaint.CycleMethod.REPEAT);
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, remember the old state and reset it afterwards.
      // The following two properties are eventually changed by the Pen (see Pen#commit(Graphics2D) method)
      Paint oldPaint = g.getPaint();
      Stroke oldStroke = g.getStroke();
      try {
        Pen pen = new Pen(getPaint(), pathThickness);
        pen.commit(g);
        g.draw(path);
      } finally {
        // after all is done, reset the state
        g.setPaint(oldPaint);
        g.setStroke(oldStroke);
      }
    }

    /**
     * Determines the paint of the edge. If the edge is selected the animated paint is used, otherwise {@link #color}.
     */
    private Paint getPaint() {
      if (!selected) {
        return color;
      } else {
        return animatedPaint;
      }
    }
  }
}
