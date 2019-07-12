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
package analysis.graphanalysis.styles;

import analysis.graphanalysis.ModelItemInfo;
import analysis.graphanalysis.NodeInfo;
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualizes nodes as circles with possible multiple colors to indicate
 * the component or path a given node belongs to.
 */
public class MultiColorNodeStyle extends AbstractNodeStyle {
  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    Object tag = node.getTag();
    if (tag instanceof NodeInfo) {
      Visual visual = new Visual();
      visual.update(node, (NodeInfo) tag);
      return visual;
    } else {
      return null;
    }
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    Object tag = node.getTag();
    if (tag instanceof NodeInfo) {
      Visual visual = oldVisual instanceof Visual ? (Visual) oldVisual : new Visual();
      visual.update(node, (NodeInfo) tag);
      return visual;
    } else {
      return null;
    }
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

  /**
   * Returns the color for the given component.
   * @param componentId The id of the component.
   * @return The color for the component.
   */
  private static Color getColorForComponent(int componentId) {
    return ModelItemInfo.getComponentColor(componentId);
  }



  private static class Visual implements IVisual {
    static final Pen PEN = new Pen(Colors.WHITE, 2);

    Color[] colors;
    Shape[] shapes;
    boolean drawBorder;

    Visual() {
      colors = new Color[0];
      shapes = new Shape[0];
    }

    void update( INode node, NodeInfo info) {
      IRectangle nl = node.getLayout();
      double x = nl.getX();
      double y = nl.getY();
      double width = nl.getWidth();
      double height = nl.getHeight();

      List<Integer> components = filterSelfloops(info.getNodeComponents());
      if (components.size() < 2) {
        drawBorder = false;

        boolean reset = colors.length != 1;
        if (reset) {
          colors = new Color[1];
          shapes = new Shape[1];
        }

        Color color = info.getColor();
        if (color == null) {
          colors[0] = components.isEmpty()
                  ? Color.GRAY : getColorForComponent(components.get(0));
        } else {
          colors[0] = color;
        }

        if (reset) {
          shapes[0] = new Ellipse2D.Double(x, y, width, height);
        } else {
          ((Ellipse2D) shapes[0]).setFrame(x, y, width, height);
        }
      } else {
        drawBorder = true;

        int count = components.size();
        boolean reset = colors.length != count + 1;
        if (reset) {
          colors = new Color[count + 1];
          shapes = new Shape[count + 1];
        }

        double angle = 360d / count;
        for (int i = 0; i < count; ++i) {
          if (reset) {
            shapes[i] = new Arc2D.Double(x, y, width, height, i * angle, angle, Arc2D.PIE);
          } else {
            ((Arc2D) shapes[i]).setFrame(x, y, width, height);
          }
          colors[i] = getColorForComponent(components.get(i));
        }

        colors[count] = colors[count - 1];
        if (reset) {
          shapes[count] = new Ellipse2D.Double(x + 5, y + 5, width - 10, height - 10);
        } else {
          ((Ellipse2D) shapes[count]).setFrame(x + 5, y + 5, width - 10, height - 10);
        }
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D graphics) {
      for (int i = 0; i < shapes.length; i++) {
        graphics.setPaint(colors[i]);
        graphics.fill(shapes[i]);
      }

      if (drawBorder) {
        PEN.commit(graphics);
        graphics.draw(shapes[shapes.length - 1]);
      }
    }


    static List<Integer> filterSelfloops(List<Integer> components) {
      if (components == null) {
        return new ArrayList<>();
      }

      List<Integer> result = new ArrayList<>();
      for (Integer component : components) {
        if (component > -1) {
          result.add(component);
        }
      }
      return result;
    }
  }
}
