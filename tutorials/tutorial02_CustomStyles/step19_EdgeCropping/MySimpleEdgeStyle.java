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
package tutorial02_CustomStyles.step19_EdgeCropping;

import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.EdgeSelectionIndicatorInstaller;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
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
 * com.yworks.yfiles.graph.styles.AbstractEdgeStyle} as its base class.
 */
public class MySimpleEdgeStyle extends AbstractEdgeStyle {
  private double pathThickness;

  /**
   * Initializes a new <code>MySimpleEdgeStyle</code>instance with an edge thickness of 3.
   */
  public MySimpleEdgeStyle() {
    setPathThickness(3);
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
  protected IVisual createVisual(IRenderContext context, IEdge edge) {
    // create the visual that paints the edge path
    EdgeVisual edgeVisual = new EdgeVisual();
    edgeVisual.update(getPath(edge), getPathThickness());
    return edgeVisual;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, IEdge)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual group, IEdge edge) {
    // update the visual that paints the edge path
    EdgeVisual edgeVisual = (EdgeVisual) group;
    edgeVisual.update(getPath(edge), getPathThickness());
    return group;
  }

  /**
   * Creates a {@link com.yworks.yfiles.geometry.GeneralPath} from the segments of the given edge.
   */
  @Override
  protected GeneralPath getPath(IEdge edge) {
    // create a general path from the source port over the bends to the target port of the edge
    GeneralPath path = new GeneralPath();
    path.moveTo(getLocation(edge.getSourcePort()));
    for (IBend bend : edge.getBends()) {
      path.lineTo(bend.getLocation());
    }
    path.lineTo(getLocation(edge.getTargetPort()));

    //////////////// New in this sample ////////////////
    // crop the edge's path at its nodes
    return cropPath(edge, IArrow.NONE, IArrow.NONE, path);
    ////////////////////////////////////////////////////
  }

  /**
   * Gets a snapshot of the current location of the port.
   * Unlike {@link com.yworks.yfiles.graph.IPort#getDynamicLocation()} this does not return a dynamic point that always refers to the current location.
   * It is recommended to use this method at performance critical places that require no live view of the port location.
   * @param port The port to retrieve the location from.
   * @return The current port location.
   */
  private static PointD getLocation(IPort port) {
    IPortLocationModelParameter param = port.getLocationParameter();
    return param.getModel().getLocation(port, param);
  }

  /**
   * Determines if the visual representation of the edge has been hit at the given location.
   * This implementation includes {@link #getPathThickness()} and the given
   * context's <code>HitTestRadius</code> in the calculation.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, IEdge edge) {
    // use the convenience method in GeneralPath
    return getPath(edge).pathContains(location, context.getHitTestRadius() + getPathThickness() * 0.5);
  }

  /**
   * Provides a custom implementation of the {@link com.yworks.yfiles.view.ISelectionIndicatorInstaller} interface
   * that is better suited to this style.
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
    protected Pen getPen(CanvasComponent canvas, IEdge edge) {
      return Pen.getTransparent();
    }
  }


  /**
   * A {@link IVisual} that paints the line of an edge. We store the path and the thickness of
   * the edge as instance variables.
   * The {@link #update(GeneralPath, double)} method checks whether these values have been changed.
   * If so, the instance variables are updated.
   */
  private static class EdgeVisual implements IVisual {
    // gradient to paint the edge with
    private static final Paint PATH_PAINT;

    // the shape to paint the line of the edge
    private Path2D.Double path;
    // stores the segments of the edge; used to create and update the path
    private GeneralPath generalPath;
    // the thickness of the edge
    private double pathThickness;

    static {
      PATH_PAINT =  new LinearGradientPaint(0, 0, 20, 20,
          new float[]{0, 0.5f, 1},
          new Color[]{ new Color(150, 255, 255, 150), new Color(0, 130, 180, 200), new Color(150, 255, 255, 150)},
          MultipleGradientPaint.CycleMethod.REPEAT);
    }

    EdgeVisual() {
      this.path = new Path2D.Double();
    }

    /**
     * Checks if the path or the thickness of the edge has been changed. If so, updates all items needed to paint the
     * edge.
     * @param generalPath   the path of the edge
     * @param pathThickness the thickness of the edge
     */
    void update(GeneralPath generalPath, double pathThickness) {
      // update the path
      if (!generalPath.equals(this.generalPath)) {
        this.generalPath = generalPath;
        this.generalPath.updatePath(path, null);
      }

      this.pathThickness = pathThickness;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, remember the old state and reset it afterwards.
      // The following two properties are eventually changed by the Pen (see Pen#commit(Graphics2D) method)
      Paint oldPaint = gfx.getPaint();
      Stroke oldStroke = gfx.getStroke();
      try {
        Pen pen = new Pen(PATH_PAINT, pathThickness);
        pen.commit(gfx);
        gfx.draw(path);
      } finally {
        // after all is done, reset the state
        gfx.setPaint(oldPaint);
        gfx.setStroke(oldStroke);
      }
    }
  }
}
