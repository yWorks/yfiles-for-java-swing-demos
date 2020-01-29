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
package layout.hierarchiclayout;

import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.view.input.ConstrainedHandle;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortSide;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.geom.Path2D;

/**
 * Helper class that provides a handle for the first and last bend of an edge that interactively determines the port
 * constraint.
 */
class PortConstraintBendHandle extends ConstrainedHandle implements IVisualCreator {
  // the minimum distance to require for a port constraint
  private static final int MIN_DISTANCE = 12;

  private boolean sourceEnd;
  private IBend bend;
  private Mapper<IEdge, PortConstraint> portConstraints;
  private ICanvasObject canvasObject;

  public PortConstraintBendHandle(boolean sourceEnd, IBend bend, IHandle originalImplementation, Mapper<IEdge, PortConstraint> portConstraints) {
    super(originalImplementation);
    this.sourceEnd = sourceEnd;
    this.bend = bend;
    this.portConstraints = portConstraints;
  }

  @Override
  protected void onInitialized(IInputModeContext inputModeContext, PointD originalLocation) {
    super.onInitialized(inputModeContext, originalLocation);
    // render the indicator
    canvasObject = inputModeContext.getCanvasComponent().getRootGroup().addChild(this, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  @Override
  protected void onCanceled(IInputModeContext inputModeContext, PointD originalLocation) {
    super.onCanceled(inputModeContext, originalLocation);
    // remove the indicator
    canvasObject.remove();
  }

  @Override
  protected void onFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    super.onFinished(inputModeContext, originalLocation, newLocation);
    // remove the indicator
    canvasObject.remove();

    // calculate the direction
    IPort port = sourceEnd ? bend.getOwner().getSourcePort() : bend.getOwner().getTargetPort();
    RectD nodeLayout = ((INode) port.getOwner()).getLayout().toRectD();
    PointD portLocation = nodeLayout.getCenter();
    PointD bendLocation = bend.getLocation().toPointD();
    PointD delta = PointD.subtract(bendLocation, portLocation);
    PortConstraint pc = null;

    // create a port constraint for the side to which the bend has been moved. The bend should have a minimum distance
    // to the node and should not lay inside the node.
    if (delta.getVectorLength() > MIN_DISTANCE && !nodeLayout.contains(bendLocation)) {
      PointD direction = delta.getNormalized();
      if (direction.isHorizontalVector()) {
        if (direction.getX() > 0) {
          pc = PortConstraint.create(PortSide.EAST);
        } else {
          pc = PortConstraint.create(PortSide.WEST);
        }
      } else {
        if (direction.getY() > 0) {
          pc = PortConstraint.create(PortSide.SOUTH);
        } else {
          pc = PortConstraint.create(PortSide.NORTH);
        }
      }
    }

    // and set the port constraint
    if (pc == null) {
      portConstraints.removeValue(bend.getOwner());
    } else {
      portConstraints.setValue(bend.getOwner(), pc);
    }
  }

  @Override
  protected PointD constrainNewLocation( IInputModeContext context, PointD originalLocation, PointD newLocation) {
    // do not constrain...
    return newLocation;
  }

  /**
   * Creates a visual representation of the constraint indicator.
   */
  public GeneralPath createConstraintIndicator() {
    IPort port = sourceEnd ? bend.getOwner().getSourcePort() : bend.getOwner().getTargetPort();
    RectD nodeLayout = ((INode) port.getOwner()).getLayout().toRectD();

    PointD portLocation = nodeLayout.getCenter();
    PointD bendLocation = bend.getLocation().toPointD();
    PointD delta = PointD.subtract(bendLocation, portLocation);

    // the indicator must have a minimum length and should not lay inside the node bounds
    if (delta.getVectorLength() > MIN_DISTANCE && !nodeLayout.contains(bendLocation)) {
      PointD direction = delta.getNormalized();

      // create a path visualizing the constraint indicator
      GeneralPath path = new GeneralPath(20);
      path.moveTo(-15, 0);
      path.lineTo(-5, 10);
      path.lineTo(-2, 7);
      path.lineTo(-5, 4);
      path.lineTo(8, 4);
      path.lineTo(8, -4);
      path.lineTo(-5, -4);
      path.lineTo(-2, -7);
      path.lineTo(-5, -10);
      path.close();

      // mirror at target end
      if (!sourceEnd) {
        path.transform(new Matrix2D(-1, 0, 0, 1, 0, 0));
      }

      // rotate and translate arrow for the side to which the bend has been moved
      int ArrowOffset = 11;
      if (direction.isHorizontalVector()) {
        if (direction.getX() > 0) {
          path.transform(new Matrix2D(-1, 0, 0, 1, nodeLayout.getMaxX() + ArrowOffset, nodeLayout.getCenterY()));
        } else {
          path.transform(new Matrix2D(1, 0, 0, 1, nodeLayout.getX() - ArrowOffset, nodeLayout.getCenterY()));
        }
      } else {
        if (direction.getY() < 0) {
          path.transform(new Matrix2D(0, 1, 1, 0, nodeLayout.getCenterX(), nodeLayout.getY() - ArrowOffset));
        } else {
          path.transform(new Matrix2D(0, 1, -1, 0, nodeLayout.getCenterX(), nodeLayout.getMaxY() + ArrowOffset));
        }
      }

      // and render
      return path;
    }
    return null;
  }

  @Override
  public IVisual createVisual(IRenderContext ctx) {
    GeneralPath indicator = createConstraintIndicator();
    if (indicator != null) {
      Path2D path = indicator.createPath(new Matrix2D());
      return new ConstraintIndicatorVisual(path, Pen.getBlack(), Color.GREEN);
    } else {
      return null;
    }
  }

  @Override
  public IVisual updateVisual(IRenderContext ctx, IVisual oldVisual) {
    GeneralPath indicator = createConstraintIndicator();

    // update an already created constraint indicator
    if (indicator != null && oldVisual instanceof ConstraintIndicatorVisual) {
      Path2D path = ((ConstraintIndicatorVisual) oldVisual).getPath();
      indicator.updatePath(path, new Matrix2D());
      return oldVisual;
    } else {
      return createVisual(ctx);
    }
  }
}
