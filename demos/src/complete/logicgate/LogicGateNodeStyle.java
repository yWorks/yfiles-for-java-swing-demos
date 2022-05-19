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
package complete.logicgate;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * An implementation of an {@link INodeStyle} that displays logic gates.
 */
public class LogicGateNodeStyle extends AbstractNodeStyle {
  // node fill
  private static final Color FILL_COLOR = Colors.WHITE_SMOKE;
  private static final Color OUTLINE_COLOR = Colors.BLACK;

  private static final GeneralPath AND_OUTLINE_PATH, OR_OUTLINE_PATH, NAND_OUTLINE_PATH, NOR_OUTLINE_PATH, NOT_OUTLINE_PATH;

  private LogicGateType type;

  static {
    // path for AND nodes
    AND_OUTLINE_PATH = new GeneralPath();
    AND_OUTLINE_PATH.moveTo(0.6, 0);
    AND_OUTLINE_PATH.lineTo(0.1, 0);
    AND_OUTLINE_PATH.lineTo(0.1, 1);
    AND_OUTLINE_PATH.lineTo(0.6, 1);
    AND_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    AND_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);

    // path for OR nodes
    OR_OUTLINE_PATH = new GeneralPath();
    OR_OUTLINE_PATH.moveTo(0.6, 0);
    OR_OUTLINE_PATH.lineTo(0.1, 0);
    OR_OUTLINE_PATH.quadTo(0.3, 0.5, 0.1, 1);
    OR_OUTLINE_PATH.lineTo(0.6, 1);
    OR_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    OR_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);

    // path for NAND nodes
    NAND_OUTLINE_PATH = new GeneralPath();
    NAND_OUTLINE_PATH.moveTo(0.6, 0);
    NAND_OUTLINE_PATH.lineTo(0.1, 0);
    NAND_OUTLINE_PATH.lineTo(0.1, 1);
    NAND_OUTLINE_PATH.lineTo(0.6, 1);
    NAND_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    NAND_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);
    NAND_OUTLINE_PATH.appendEllipse(new RectD(0.8, 0.4, 0.1, 0.2), false);

    // path for NOR nodes
    NOR_OUTLINE_PATH = new GeneralPath();
    NOR_OUTLINE_PATH.moveTo(0.6, 0);
    NOR_OUTLINE_PATH.lineTo(0.1, 0);
    NOR_OUTLINE_PATH.quadTo(0.3, 0.5, 0.1, 1);
    NOR_OUTLINE_PATH.lineTo(0.6, 1);
    NOR_OUTLINE_PATH.quadTo(0.8, 1.0, 0.8, 0.5);
    NOR_OUTLINE_PATH.quadTo(0.8, 0.0, 0.6, 0);
    NOR_OUTLINE_PATH.appendEllipse(new RectD(0.8, 0.4, 0.1, 0.2), false);

    // path for NOT nodes
    NOT_OUTLINE_PATH = new GeneralPath();
    NOT_OUTLINE_PATH.moveTo(0.8, 0.5);
    NOT_OUTLINE_PATH.lineTo(0.1, 0);
    NOT_OUTLINE_PATH.lineTo(0.1, 1);
    NOT_OUTLINE_PATH.lineTo(0.8, 0.5);
    NOT_OUTLINE_PATH.appendEllipse(new RectD(0.8, 0.4, 0.1, 0.2), false);
  }

  /**
   * Default constructor for serialization.
   */
  public LogicGateNodeStyle() {
  }

  public LogicGateNodeStyle(LogicGateType type) {
    this.type = type;
  }

  /**
   * Gets the type of the logic gate.
   */
  public LogicGateType getGateType() {
    return type;
  }

  /**
   * Sets the type of the logic gate.
   */
  public void setGateType(LogicGateType type) {
    this.type = type;
  }

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    LogicGateVisual visual = new LogicGateVisual(getGateType());
    visual.update(node);
    return visual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (oldVisual instanceof LogicGateVisual) {
      LogicGateVisual visual = (LogicGateVisual) oldVisual;

      if (visual.type == getGateType()) {
        visual.update(node);
        return visual;
      }
    }
    return createVisual(context, node);
  }

  @Override
  protected GeneralPath getOutline(INode node) {
    IRectangle layout = node.getLayout();
    Matrix2D transform = new Matrix2D(layout.getWidth(), 0, 0, layout.getHeight(), layout.getX(), layout.getY());
    return getNodeOutlinePath(getGateType()).createGeneralPath(transform);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    return node.getLayout().toRectD().getEnlarged(context.getHitTestRadius()).contains(location);
  }

  private static GeneralPath getNodeOutlinePath(LogicGateType type) {
    switch (type) {
      default:
      case AND:
        return AND_OUTLINE_PATH;
      case NAND:
        return NAND_OUTLINE_PATH;
      case NOR:
        return NOR_OUTLINE_PATH;
      case NOT:
        return NOT_OUTLINE_PATH;
      case OR:
        return OR_OUTLINE_PATH;
    }
  }

  /**
   * An implementation of an {@link IVisual} that renders a logic gate of the given {@link LogicGateType type}.
   */
  static class LogicGateVisual extends VisualGroup {
    LogicGateType type;
    Matrix2D transform;
    Line2D inPortLine1, inPortLine2;
    Path2D outline;
    Line2D outPortLine;

    double x, y, w, h;

    public LogicGateVisual(LogicGateType type) {
      this.type = type;
      this.transform = new Matrix2D();

      // add logic-gate outline
      add(new ShapeVisual(outline = new Path2D.Double(), new Pen(OUTLINE_COLOR, 2), FILL_COLOR));

      // add in-port line(s)
      add(new ShapeVisual(inPortLine1 = new Line2D.Double(), new Pen(Colors.BLACK, 3), null));
      if (type != LogicGateType.NOT) {
        add(new ShapeVisual(inPortLine2 = new Line2D.Double(), new Pen(Colors.BLACK, 3), null));
      }

      // add out-port line
      add(new ShapeVisual(outPortLine = new Line2D.Double(), new Pen(Colors.BLACK, 3), null));
    }

    void update(INode node) {
      if (x != node.getLayout().getX() || y != node.getLayout().getY()) {
        x = node.getLayout().getX();
        y = node.getLayout().getY();

        setTransform(AffineTransform.getTranslateInstance(x, y));
      }

      if (w != node.getLayout().getWidth() || h != node.getLayout().getHeight()) {
        w = node.getLayout().getWidth();
        h = node.getLayout().getHeight();

        // update in-port line(s)
        if (type == LogicGateType.NOT) {
          inPortLine1.setLine(0, 0.5 * h, 0.1 * w, 0.5 * h);
        } else {
          inPortLine1.setLine(0, 5, 0.3 * w, 5);
          inPortLine2.setLine(0, 25, 0.3 * w, 25);
        }

        // update logic-gate outline
        GeneralPath gp = getNodeOutlinePath(type);
        transform.set(w, 0, 0, h, 0, 0);
        gp.updatePath(outline, transform);

        // update out-port line
        if (type == LogicGateType.AND || type == LogicGateType.OR) {
          outPortLine.setLine(0.8 * w, 0.5 * h, w, 0.5 * h);
        } else {
          outPortLine.setLine(0.9 * w, 0.5 * h, w, 0.5 * h);
        }
      }
    }
  }
}
