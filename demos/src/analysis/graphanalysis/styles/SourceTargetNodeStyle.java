/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package analysis.graphanalysis.styles;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Arc2D;

/**
 * Visualizes source and/or target nodes of paths as segmented rings in one or
 * two colors.
 */
public class SourceTargetNodeStyle extends AbstractNodeStyle {
  private Type type;

  /**
   * Initializes a new {@code SourceTargetNodeStyle} instance for
   * {@link Type#TYPE_SOURCE source} nodes.
   */
  public SourceTargetNodeStyle() {
    this(Type.TYPE_SOURCE);
  }

  /**
   * Initializes a new {@code SourceTargetNodeStyle} instance with the given
   * type.
   * @param type the node type to be visualized.
   */
  public SourceTargetNodeStyle(Type type) {
    this.type = type;
  }

  /**
   * Returns the node type visualized by this style instance.
   * @return the node type visualized by this style instance.
   */
  public Type getType() {
    return type;
  }

  /**
   * Sets the node type visualized by this style instance.
   * @param type the node type to be visualized.
   */
  public void setType(Type type) {
    this.type = type;
  }

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    DashedCircleVisual visual = new DashedCircleVisual();
    visual.update(node, getType());
    return visual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    DashedCircleVisual visual = oldVisual instanceof DashedCircleVisual
            ? (DashedCircleVisual) oldVisual : new DashedCircleVisual();
    visual.update(node, getType());
    return visual;
  }

  /**
   * Determines whether or not the given point lies inside the given node's
   * visualization.
   * <p>
   * Necessary for calculating intersection points of nodes and incident edges.
   * </p>
   */
  @Override
  protected boolean isInside(INode node, PointD location) {
    return GeomUtilities.ellipseContains(node.getLayout().toRectD(), location, 0);
  }

  /**
   * Returns the outline of the given node's visualization.
   * <p>
   * Necessary for calculating intersection points of nodes and incident edges.
   * </p>
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    RectD bounds = node.getLayout().toRectD();
    GeneralPath outline = new GeneralPath();
    outline.appendEllipse(bounds, false);
    return outline;
  }


  private static class DashedCircleVisual implements IVisual {
    static final Pen SOURCE_PEN = new Pen(Colors.YELLOW_GREEN, 5);
    static final Pen TARGET_PEN = new Pen(Colors.INDIAN_RED, 5);


    final Arc2D arc;
    Pen pen1;
    Pen pen2;

    DashedCircleVisual() {
      arc = new Arc2D.Double(Arc2D.OPEN);
      arc.setAngleExtent(90);
      pen1 = TARGET_PEN;
      pen2 = SOURCE_PEN;
    }

    void update(INode node, Type type) {
      IRectangle nl = node.getLayout();
      double width = nl.getWidth();
      double height = nl.getHeight();
      double size = Math.max(width, height);
      arc.setFrame(
              nl.getX() + (width - size) * 0.5,
              nl.getY() + (height - size) * 0.5,
              size, size);

      pen1 = TARGET_PEN;
      pen2 = SOURCE_PEN;
      switch (type) {
        case TYPE_TARGET:
          pen2 = TARGET_PEN;
          break;
        case TYPE_SOURCE:
          pen1 = SOURCE_PEN;
          break;
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      Paint oldPaint = g.getPaint();
      Stroke oldStroke = g.getStroke();

      pen1.commit(g);
      arc.setAngleStart(0);
      g.draw(arc);

      pen2.commit(g);
      arc.setAngleStart(arc.getAngleStart() + arc.getAngleExtent());
      g.draw(arc);

      pen1.commit(g);
      arc.setAngleStart(arc.getAngleStart() + arc.getAngleExtent());
      g.draw(arc);

      pen2.commit(g);
      arc.setAngleStart(arc.getAngleStart() + arc.getAngleExtent());
      g.draw(arc);

      g.setStroke(oldStroke);
      g.setPaint(oldPaint);
    }
  }



  public enum Type {
    TYPE_SOURCE,
    TYPE_TARGET,
    TYPE_SOURCE_AND_TARGET,
  }
}
