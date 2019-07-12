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
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * A node style that visualizes nodes as cuboids in an isometric fashion.
 */
public class NodeStyle extends AbstractNodeStyle {
  /** Stores the color for rendering the node border. */ 
  private Color border;
  /** Stores the base color for rendering the node sides. */ 
  private Color fill;

  /**
   * Initializes a new node style.
   */
  public NodeStyle() {
    this.border = null;
    this.fill = Color.WHITE;
  }

  /**
   * Returns the color for rendering the node border.
   */
  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public Color getBorder() {
    return border;
  }

  /**
   * Sets the color for rendering the node border.
   */
  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public void setBorder( Color border ) {
    this.border = border;
  }

  /**
   * Returns the color for rendering the node sides.
   */
  @DefaultValue(stringValue = "White", classValue = Color.class)
  public Color getFill() {
    return fill;
  }

  /**
   * Sets the color for rendering the node sides.
   */
  @DefaultValue(stringValue = "White", classValue = Color.class)
  public void setFill( Color fill ) {
    this.fill = fill;
  }


  @Override
  protected GeneralPath getOutline( INode node ) {
    double[] corners = corners(node);

    GeneralPath outline = new GeneralPath();
    outline.moveTo(corners[IsometricTransformationSupport.C0_X], corners[IsometricTransformationSupport.C0_Y]);
    outline.lineTo(corners[IsometricTransformationSupport.C3_X], corners[IsometricTransformationSupport.C3_Y]);
    outline.lineTo(corners[IsometricTransformationSupport.C2_X], corners[IsometricTransformationSupport.C2_Y]);
    outline.lineTo(corners[IsometricTransformationSupport.C6_X], corners[IsometricTransformationSupport.C6_Y]);
    outline.lineTo(corners[IsometricTransformationSupport.C5_X], corners[IsometricTransformationSupport.C5_Y]);
    outline.lineTo(corners[IsometricTransformationSupport.C4_X], corners[IsometricTransformationSupport.C4_Y]);
    outline.close();
    return outline;
  }

  @Override
  protected RectD getBounds( ICanvasContext context, INode node ) {
    return getOutline(node).getBounds();
  }

  @Override
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    return getOutline(node).areaContains(location);
  }

  @Override
  protected boolean isInside( INode node, PointD location ) {
    return getOutline(node).areaContains(location);
  }

  @Override
  protected boolean isVisible( ICanvasContext context, RectD rectangle, INode node ) {
    return getOutline(node).intersects(rectangle, 0.5);
  }

  @Override
  protected PointD getIntersection( INode node, PointD inner, PointD outer ) {
    GeneralPath outline = getOutline(node);
    double factor = outline.findLineIntersection(inner, outer);
    if (Double.isInfinite(factor)) {
      return null;
    } else {
      double x1 = inner.getX();
      double y1 = inner.getY();
      double x2 = outer.getX();
      double y2 = outer.getY();
      return new PointD(x1 + (factor * (x2 - x1)), y1 + (factor * (y2 - y1)));
    }
  }


  /**
   * Creates the visual representations for nodes using this style.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    NodeStyleVisual visual = new NodeStyleVisual();
    visual.corners = corners(node);
    visual.height = IsometricGeometry.get(node).getHeight();
    visual.fill = getFill();
    visual.border = getBorder();
    return visual;
  }

  /**
   * Calculates the corners of the isometrically projected node bounds.
   */
  static double[] corners( INode node ) {
    return IsometricTransformationSupport.calculateCorners(
            node.getLayout(), IsometricGeometry.get(node));
  }


  /**
   * Handles the actual node rendering in an isometric fashion.
   */
  private static final class NodeStyleVisual implements IVisual {
    Color fill;
    Color border;
    double[] corners;
    double height;

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      Color background = this.fill;
      Color border = this.border;

      Color oldColor = g.getColor();

      Path2D.Double path = new Path2D.Double();

      if (background != null) {
        path.moveTo(corners[IsometricTransformationSupport.C4_X], corners[IsometricTransformationSupport.C4_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C5_X], corners[IsometricTransformationSupport.C5_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C6_X], corners[IsometricTransformationSupport.C6_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C7_X], corners[IsometricTransformationSupport.C7_Y]);
        path.closePath();

        g.setColor(background);
        g.fill(path);

        if (height > 0) {
          path.reset();
          path.moveTo(corners[IsometricTransformationSupport.C0_X], corners[IsometricTransformationSupport.C0_Y]);
          path.lineTo(corners[IsometricTransformationSupport.C4_X], corners[IsometricTransformationSupport.C4_Y]);
          path.lineTo(corners[IsometricTransformationSupport.C7_X], corners[IsometricTransformationSupport.C7_Y]);
          path.lineTo(corners[IsometricTransformationSupport.C3_X], corners[IsometricTransformationSupport.C3_Y]);
          path.closePath();
  
          background = background.darker();
          g.setColor(background);
          g.fill(path);
  
          path.reset();
          path.moveTo(corners[IsometricTransformationSupport.C3_X], corners[IsometricTransformationSupport.C3_Y]);
          path.lineTo(corners[IsometricTransformationSupport.C7_X], corners[IsometricTransformationSupport.C7_Y]);
          path.lineTo(corners[IsometricTransformationSupport.C6_X], corners[IsometricTransformationSupport.C6_Y]);
          path.lineTo(corners[IsometricTransformationSupport.C2_X], corners[IsometricTransformationSupport.C2_Y]);
          path.closePath();
  
          background = background.darker();
          g.setColor(background);
          g.fill(path);
        }
      }

      if (border != null) {
        g.setColor(border);

        path.reset();
        path.moveTo(corners[IsometricTransformationSupport.C0_X], corners[IsometricTransformationSupport.C0_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C3_X], corners[IsometricTransformationSupport.C3_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C2_X], corners[IsometricTransformationSupport.C2_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C6_X], corners[IsometricTransformationSupport.C6_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C5_X], corners[IsometricTransformationSupport.C5_Y]);
        path.lineTo(corners[IsometricTransformationSupport.C4_X], corners[IsometricTransformationSupport.C4_Y]);
        path.closePath();

        g.draw(path);

        if (height > 0) {
          Line2D.Double line = new Line2D.Double();
          line.setLine(corners[IsometricTransformationSupport.C7_X], corners[IsometricTransformationSupport.C7_Y],
                       corners[IsometricTransformationSupport.C4_X], corners[IsometricTransformationSupport.C4_Y]);
          g.draw(line);
          line.setLine(corners[IsometricTransformationSupport.C7_X], corners[IsometricTransformationSupport.C7_Y],
                       corners[IsometricTransformationSupport.C3_X], corners[IsometricTransformationSupport.C3_Y]);
          g.draw(line);
          line.setLine(corners[IsometricTransformationSupport.C7_X], corners[IsometricTransformationSupport.C7_Y],
                       corners[IsometricTransformationSupport.C6_X], corners[IsometricTransformationSupport.C6_Y]);
          g.draw(line);
        }
      }

      g.setColor(oldColor);
    }
  }
}
