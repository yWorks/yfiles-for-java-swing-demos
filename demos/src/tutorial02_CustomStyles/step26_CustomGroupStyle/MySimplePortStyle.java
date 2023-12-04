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

import com.yworks.yfiles.graph.styles.AbstractPortStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;


/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.IPortStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractPortStyle} as its base class. The port is rendered as a circle for normal
 * nodes and as a rounded rectangle for group nodes.
 */
public class MySimplePortStyle extends AbstractPortStyle {
  // the size of the port shape - immutable
  private static final double WIDTH = 4.0;
  private static final double HEIGHT = 4.0;

  /**
   * Creates the visual for a port.
   */
  @Override
  protected IVisual createVisual(IRenderContext context, IPort port) {

    //////////////// New in this sample ////////////////
    boolean isGroupNode = isGroupNode((INode) port.getOwner(), (GraphComponent) context.getCanvasComponent());
    PortVisual visual = new PortVisual(isGroupNode);
    ////////////////////////////////////////////////////

    visual.update(getLocation(port));
    return visual;
  }

  /**
   * Re-renders the port using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, IPort)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual group, IPort port) {
    PortVisual visual = (PortVisual) group;
    visual.update(getLocation(port));
    return visual;
  }

  //////////////// New in this sample ////////////////
  /**
   * Checks whether or not a given node is a group node.
   */
  private static boolean isGroupNode(INode node, GraphComponent graphComponent) {
    IFoldingView foldingView = graphComponent.getGraph().getFoldingView();
    return foldingView != null && foldingView.getManager().getMasterGraph().isGroupNode(foldingView.getMasterItem(node));
  }
  ////////////////////////////////////////////////////

  /**
   * Calculates the bounds of this port.
   * These are also used for arranging the visual, hit testing, visibility testing, and marquee box tests.
   */
  @Override
  protected RectD getBounds(ICanvasContext context, IPort port) {
    return RectD.fromCenter(getLocation(port).toPointD(), new SizeD(WIDTH, HEIGHT));
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
   * A {@link IVisual} that paints a port as a circle for normal nodes and as a rounded
   * rectangle for group nodes.
   */
  private static class PortVisual implements IVisual {
    // pen to paint the border of the port that belongs to a normal node
    private static final Pen NODE_BORDER_PEN = new Pen(new Color(255, 255, 255, 80));
    // pen to paint the border of the port that belongs to a group node
    private static final Pen GROUP_BORDER_PEN = Pen.getGray();

    //////////////// New in this sample ////////////////
    // the pen to paint the border of the port with
    private Pen pen;
    // the shape of the port
    private RectangularShape shape;

    public PortVisual(boolean forGroupNode) {
      pen = forGroupNode ? GROUP_BORDER_PEN : NODE_BORDER_PEN;
      shape = forGroupNode ? new RoundRectangle2D.Double(0, 0, 0, 0, WIDTH / 2, HEIGHT / 2) : new Ellipse2D.Double();
    }
    ////////////////////////////////////////////////////

    /**
     * Updates the location of the shape used to paint the port.
     * @param location the location of the port
     */
    public void update(IPoint location) {
      shape.setFrame(location.getX() - WIDTH * 0.5, location.getY() - HEIGHT * 0.5, WIDTH, HEIGHT);
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, remember the old state and reset it afterwards.
      // The following two properties are eventually changed by the Pen (see Pen#commit(Graphics2D) method)
      Paint oldPaint = g.getPaint();
      Stroke oldStroke = g.getStroke();
      try {
        pen.commit(g);
        g.draw(shape);
      } finally {
        // after all is done, reset the state
        g.setPaint(oldPaint);
        g.setStroke(oldStroke);
      }
    }
  }
}
