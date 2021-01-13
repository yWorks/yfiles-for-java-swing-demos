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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IClipboardHelper;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IDisposeVisualCallback;
import com.yworks.yfiles.view.IFocusIndicatorInstaller;
import com.yworks.yfiles.view.IHighlightIndicatorInstaller;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ISelectionIndicatorInstaller;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.OrientedRectangleIndicatorInstaller;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;

import java.awt.geom.AffineTransform;

/**
 * A node style that displays another wrapped style rotated by a specified rotation angle.
 * <p>
 * The angle is stored in this decorator to keep the tag free for user data. Hence, this
 * decorator should not be shared between nodes if they can have different angles.
 * </p>
 */
public class RotatableNodeStyleDecorator extends AbstractNodeStyle implements IMarkupExtensionConverter  {

  private final CachingOrientedRectangle rotatedLayout = new CachingOrientedRectangle();

  private final Matrix2D matrix = new Matrix2D();
  private final Matrix2D inverseMatrix = new Matrix2D();

  private PointD matrixCenter = PointD.ORIGIN;
  private double matrixAngle;

  private PointD inverseMatrixCenter = PointD.ORIGIN;
  private double inverseMatrixAngle;

  /**
   * The wrapped style.
   */
  private INodeStyle wrapped;

  /**
   * Initializes new instance with a wrapped node style and an angle.
   * @param wrapped default value is null.
   * @param angle in radians, default value is zero.
   */
  public RotatableNodeStyleDecorator(INodeStyle wrapped, double angle) {
    this.wrapped = (wrapped != null) ? wrapped : new ShapeNodeStyle();
    this.setAngle(angle);
  }

  /**
   * Creates a visual which rotates the visualization of the wrapped style.
   */
  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    INodeStyle wrapped = getWrapped();
    RotatedVisual container = new RotatedVisual(wrapped, getAngle(), node.getLayout().getCenter());
    container.add(wrapped.getRenderer().getVisualCreator(node, wrapped).createVisual(context));
    context.registerForChildrenIfNecessary(container, this::disposeChildren);
    return  container;
  }

  /**
   * A callback which will be called to free the resources which are occupied by the given {@code removedVisual} when
   * the visual will be removed.
   * @param dispose required to satisfy {@link IDisposeVisualCallback} contract.
   */
  private IVisual disposeChildren(IRenderContext ctx, IVisual removedVisual, boolean dispose) {
    VisualGroup container = (VisualGroup) removedVisual;
    if(container != null && container.getChildren().size() > 0) {
      ctx.childVisualRemoved(container.getChildren().get(0));
    }
    return null;
  }

  /**
   * Updates a visual which rotates the visualization of the wrapped style.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (oldVisual instanceof RotatedVisual) {
      RotatedVisual container = (RotatedVisual) oldVisual;

      INodeStyle oldWrappedStyle = container.wrapped;
      INodeStyle newWrappedStyle = getWrapped();
      IVisualCreator creator = newWrappedStyle.getRenderer().getVisualCreator(node, newWrappedStyle);

      IVisual oldWrappedVisual = container.getFirstChild();
      IVisual newWrappedVisual;
      if (newWrappedStyle != oldWrappedStyle) {
        newWrappedVisual = creator.createVisual(context);
      } else {
        newWrappedVisual = creator.updateVisual(context, oldWrappedVisual);
      }

      if (newWrappedVisual != oldWrappedVisual) {
        container.setFirstChild(newWrappedVisual);
        context.childVisualRemoved(oldWrappedVisual);
      }
      context.registerForChildrenIfNecessary(oldVisual, this::disposeChildren);

      double angle = getAngle();
      PointD center = node.getLayout().getCenter();
      if (newWrappedStyle != oldWrappedStyle || container.angle != angle || !container.center.equals(center)) {
        container.wrapped = newWrappedStyle;
        container.angle = angle;
        container.center = center;
        container.updateTransform();
      }

      return container;
    } else {
      return createVisual(context, node);
    }
  }

  /**
   * Returns bounds based on the size provided by the wrapped style and the location and rotation of the node.
   */
  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    CachingOrientedRectangle nodeOrientedRect = getRotatedLayout(node);

    //create an oriented rectangle with the size of the wrapped bounds and the location and rotation of the node
    RectD wrappedBounds = wrapped.getRenderer().getBoundsProvider(node, wrapped).getBounds(context);

    OrientedRectangle orientedRectangle = new OrientedRectangle(0, 0,
            wrappedBounds.getWidth(), wrappedBounds.getHeight(), nodeOrientedRect.getUpX(), nodeOrientedRect.getUpY());
    orientedRectangle.setCenter(node.getLayout().getCenter());

    return orientedRectangle.getBounds();
  }

  /**
   * Returns the intersection point of the node's rotated bounds and the segment between the inner and outer point or
   * {@literal null} if there is no intersection.
   */
  @Override
  protected PointD getIntersection(INode node, PointD inner, PointD outer) {
    PointD rotatedInner = getRotatedPoint(inner, node, false);
    PointD rotatedOuter = getRotatedPoint(outer, node, false);

    PointD rotatedIntersection = wrapped.getRenderer().getShapeGeometry(node, wrapped)
            .getIntersection(rotatedInner, rotatedOuter);

    return rotatedIntersection != null ? getRotatedPoint(rotatedIntersection, node, true) : null;
  }

  /**
   * Returns the outline of the node's rotated shape.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    GeneralPath outline = wrapped.getRenderer().getShapeGeometry(node, wrapped).getOutline();
    if (outline == null) {
      return null;
    } else {
      GeneralPath rotatedOutline = outline.clone();
      rotatedOutline.transform(getInverseRotationMatrix(node));
      return rotatedOutline;
    }
  }

  /**
   * Returns whether or not the given location is inside the rotated node.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    //rotated the point like the node, that is by the angle around the node center
    PointD transformedPoint = getRotatedPoint(location, node, false);
    return wrapped.getRenderer().getHitTestable(node, wrapped).isHit(context, transformedPoint);
  }

  /**
   * Returns whether or not the given node is inside the rectangle.
   */
  @Override
  protected boolean isInBox(IInputModeContext context, RectD rectangle, INode node) {
    CachingOrientedRectangle nodeOrientedRect = getRotatedLayout(node);

    //create an oriented rectangle with the size if the wrapped bounds, location and rotation of the node
    RectD wrappedBounds = wrapped.getRenderer().getBoundsProvider(node, wrapped).getBounds(context);
    OrientedRectangle orientedRectangle = new OrientedRectangle(0, 0, wrappedBounds.getWidth(),
            wrappedBounds.getHeight(), nodeOrientedRect.getUpX(), nodeOrientedRect.getUpY());
    orientedRectangle.setCenter(node.getLayout().getCenter());

    return rectangle.intersects(orientedRectangle, 0.01d);
  }

  /**
   * Returns whether or not the node is currently visible.
   */
  @Override
  protected boolean isVisible(ICanvasContext context, RectD rectangle, INode node) {
    return wrapped.getRenderer().getVisibilityTestable(node, wrapped).isVisible(context, rectangle) ||
            getBounds(context, node).intersects(rectangle);
  }

  /**
   * Returns customized helpers that consider the node rotation for resizing and rotating gestures,
   * highlighting indicators, and clipboard operations.
   * Other lookup calls will be delegated to the lookup of the wrapped node style.
   */
  @Override
  protected Object lookup(INode node, Class type) {
    //custom reshape handles that rotate with the node
    if(type == IReshapeHandleProvider.class) {
      return new RotatedReshapeHandleProvider(node);
    }
    //Custom handle to rotate the node
    if (type == IHandleProvider.class) {
      return new NodeRotateHandleProvider(node);
    }
    //selection decoration
    if (type == ISelectionIndicatorInstaller.class) {
      return new RotatableNodeIndicatorInstaller(OrientedRectangleIndicatorInstaller.SELECTION_TEMPLATE_KEY);
    }
    //focus decoration
    if (type == IFocusIndicatorInstaller.class) {
      return new RotatableNodeIndicatorInstaller(OrientedRectangleIndicatorInstaller.FOCUS_TEMPLATE_KEY);
    }
    //highlight decoration
    if (type == IHighlightIndicatorInstaller.class){
      return new RotatableNodeIndicatorInstaller(OrientedRectangleIndicatorInstaller.HIGHLIGHT_TEMPLATE_KEY);
    }
    //Clipboard helper that clones the style instance when pasting rotated nodes
    if (type == IClipboardHelper.class) {
      return new RotatableNodeClipboardHelper();
    }

    //else call super
    Object tmp = super.lookup(node, type);
    if(tmp != null){
      return tmp;
    }

    //if super call == null
    return wrapped.getRenderer().getContext(node, wrapped).lookup(type);
  }

  /**
   * Creates a copy of this node style decorator.
   */
  @Override
  public RotatableNodeStyleDecorator clone() {
    return new RotatableNodeStyleDecorator(wrapped, getAngle());
  }

  /**
   * Returns the rotated bounds of the node.
   */
  CachingOrientedRectangle getRotatedLayout(INode node) {
    rotatedLayout.updateCache(node.getLayout().toRectD());
    return rotatedLayout;
  }

  /**
   * Returns the rotated point.
   */
  PointD getRotatedPoint(PointD point, INode node, boolean inverse) {
    Matrix2D matrix = inverse ? getInverseRotationMatrix(node) : getRotationMatrix(node);
    return matrix.transform(point);
  }

  /**
   * Returns the rotation matrix for the given node and the current angle.
   */
  private Matrix2D getRotationMatrix(INode node) {
    PointD center = node.getLayout().getCenter();

    double angle = getAngle();

    if (!center.equals(matrixCenter) || angle != matrixAngle) {
      matrix.reset();
      matrix.rotate(angle, center);
      matrixCenter = center;
      matrixAngle = angle;
    }
    return matrix;
  }

  /**
   * Returns the inverse rotation matrix for the given node and the current angle.
   */
  private Matrix2D getInverseRotationMatrix(INode node) {
    PointD center = node.getLayout().getCenter();

    double angle = -getAngle();

    if (!(center.equals(inverseMatrixCenter)) || angle != inverseMatrixAngle) {
      inverseMatrix.reset();
      inverseMatrix.rotate(angle, center);
      inverseMatrixCenter = center;
      inverseMatrixAngle = angle;
    }
    return inverseMatrix;
  }

  /**
   * Returns {@code true}. This style can be converted always.
   */
  @Override
  public boolean canConvert(IWriteContext iWriteContext, Object o) {
    return true;
  }

  /**
   * Converts this style using {@link RotatableNodeStyleDecoratorExtension}.
   * @param context The current write context.
   * @param value The object to convert.
   */
  @Override
  public MarkupExtension convert(IWriteContext context, Object value) {
    RotatableNodeStyleDecorator decorator = (RotatableNodeStyleDecorator) value;

    RotatableNodeStyleDecoratorExtension extension = new RotatableNodeStyleDecoratorExtension();
    extension.setAngle(decorator.getAngle());
    extension.setWrapped(decorator.getWrapped());

    return extension;
  }

  /**
   * Returns the wrapped node style.
   */
  public INodeStyle getWrapped(){
    return wrapped;
  }

  /**
   * Sets the wrapped node style.
   */
  public void setWrapped(INodeStyle wrapped) {
    this.wrapped = wrapped;
  }

  /**
   * Returns rotation angle in radians.
   */
  public double getAngle() {
    return rotatedLayout.getAngle();
  }

  /**
   * Sets rotation angle in radians.
   */
  public void setAngle(double angle) {
    this.rotatedLayout.setAngle(angle);
  }

  /**
   * Caches the rotation angle and the rotation center to be able to determine
   * external changes to the style that require rebuilding the rotation matrix.
   */
  private static final class RotatedVisual extends VisualGroup {
    INodeStyle wrapped;
    double angle;
    PointD center;

    RotatedVisual(INodeStyle wrapped, double angle, PointD center) {
      this.wrapped = wrapped;
      this.angle = angle;
      this.center = center;

      setTransform(new AffineTransform());
      updateTransform();
    }

    IVisual getFirstChild() {
      return getChildren().get(0);
    }


    void setFirstChild(IVisual visual) {
      getChildren().set(0, visual);
    }

    void updateTransform() {
      AffineTransform transform = getTransform();
      transform.setToIdentity();
      transform.rotate(-angle, center.getX(), center.getY());
    }
  }
}
