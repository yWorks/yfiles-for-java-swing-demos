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
package input.labelhandleprovider;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.OrientedRectangleIndicatorInstaller;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Cursor;

/**
 * Custom {@link IHandle} implementation for resizing labels.
 */
public class LabelResizeHandle implements IHandle {
  private final ILabel label;
  private final boolean symmetricResize;

  private IPoint location;
  private boolean emulate;
  private SizeD dummyPreferredSize;
  private PointD dummyLocation;
  private ICanvasObject sizeIndicator;

  /**
   * Initializes a new {@code LabelResizeHandle} instance for the given label.
   * @param symmetricResize if {@code true} resizing in east-west direction is
   * symmetric.
   */
  public LabelResizeHandle( ILabel label, boolean symmetricResize ) {
    this.label = label;
    this.symmetricResize = symmetricResize;
  }

  /**
   * Returns the handle's type.
   */
  @Override
  public HandleTypes getType() {
    return HandleTypes.RESIZE;
  }

  /**
   * Returns the cursor that should be displayed while this handle
   * implementation is active.
   */
  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
  }

  /**
   * Returns the handle's location.
   */
  @Override
  public IPoint getLocation() {
    if (location == null) {
      location = new LabelResizeHandleLivePoint();
    }
    return location;
  }

  /**
   * Initializes the handle's state right before a resize operation.
   * Invoked right before the handle is dragged.
   */
  @Override
  public void initializeDrag( IInputModeContext context ) {
    dummyPreferredSize = label.getPreferredSize();
    dummyLocation = label.getLayout().getAnchorLocation();

    // start using the calculated dummy bounds
    emulate = true;

    CanvasComponent canvasComponent = context.getCanvasComponent();
    if (canvasComponent != null) {
      // add a ghost visualization for the resized label bounds
      LabelResizeIndicatorInstaller installer = new LabelResizeIndicatorInstaller();
      sizeIndicator = installer.addCanvasObject(
              canvasComponent.getCanvasContext(),
              canvasComponent.getSelectionGroup(),
              this);
    }
  }

  /**
   * Calculates the new label size for the current handle position.
   * Invoked when the handle is dragged.
   */
  @Override
  public void handleMove( IInputModeContext context, PointD originalLocation, PointD newLocation ) {
    IOrientedRectangle layout = label.getLayout();
    // the normal (orthogonal) vector of the 'up' vector
    PointD upNormal = new PointD(-layout.getUpY(), layout.getUpX());

    // calculate the total distance the handle has been moved in this drag gesture
    double delta = upNormal.scalarProduct(PointD.subtract(newLocation, originalLocation));

    // prevent shrinking the label to a size less than 0
    delta = Math.max(delta, -layout.getWidth() * (symmetricResize ? 0.5 : 1));

    // add one or two times delta to the width to expand the label right and left
    double newWidth = Math.max(0, layout.getWidth() + delta * (symmetricResize ? 2 : 1));

    dummyPreferredSize = new SizeD(newWidth, dummyPreferredSize.getHeight());

    // calculate the new location
    dummyLocation =
            symmetricResize
            ? PointD.subtract(layout.getAnchorLocation(), new PointD(upNormal.getX() * delta, upNormal.getY() * delta))
            : layout.getAnchorLocation(); 
  }

  /**
   * Resizes the handle's associated label.
   * Invoked when the drag gesture has finished.
   */
  @Override
  public void dragFinished( IInputModeContext context, PointD originalLocation, PointD newLocation ) {
    IGraph graph = context.getGraph();
    if (graph != null) {
      // assign the new size
      graph.setLabelPreferredSize(label, dummyPreferredSize);

      // update the label's layout parameter for its new size
      // this ensures that the resize rectangle which acts as user feedback is
      // in sync with the actual bounds assigned to the label
      ILabelModel model = label.getLayoutParameter().getModel();
      ILabelModelParameterFinder finder = model.lookup(ILabelModelParameterFinder.class);
      if (finder != null) {
        ILabelModelParameter param = finder.findBestParameter(label, model, getCurrentLabelLayout());
        graph.setLabelLayoutParameter(label, param);
      }
    }

    reset();
  }

  /**
   * Cancels the resize operation and resets the handle's state.
   * Invoked when the drag gesture is canceled.
   */
  @Override
  public void cancelDrag( IInputModeContext context, PointD originalLocation ) {
    reset();
  }

  private void reset() {
    // use normal label bounds if drag gesture is over.
    emulate = false;

    // remove the visual size indicator
    if (sizeIndicator != null) {
      sizeIndicator.remove();
      sizeIndicator = null;
    }
  }

  /**
   * Returns the current label layout.
   */
  public OrientedRectangle getCurrentLabelLayout() {
    IOrientedRectangle labelLayout = label.getLayout();
    if (emulate) {
      return new OrientedRectangle(
              dummyLocation.getX(),
              dummyLocation.getY(),
              dummyPreferredSize.getWidth(),
              dummyPreferredSize.getHeight(),
              labelLayout.getUpX(),
              labelLayout.getUpY());
    } else {
      return new OrientedRectangle(
              labelLayout.getAnchorX(),
              labelLayout.getAnchorY(),
              label.getPreferredSize().getWidth(),
              label.getPreferredSize().getHeight(),
              labelLayout.getUpX(),
              labelLayout.getUpY());
    }
  }

  /**
   * Represents the location of the resize handle.
   */
  private class LabelResizeHandleLivePoint implements IPoint{
    /**
     * Returns the handle's x-coordinate.
     */
    @Override
    public double getX() {
      IOrientedRectangle layout = label.getLayout();

      SizeD preferredSize = emulate
              ? dummyPreferredSize
              : label.getPreferredSize();
      PointD anchor = emulate
              ? dummyLocation
              : layout.getAnchorLocation();
      PointD up = layout.getUp();

      // the resize handle is always placed in the center of the label's right border, i.e.
      // the resize handle location in the coordinate system of the label's oriented bounds is (1, 0.5)
      // transform the label-local coordinates to regular graph coordinates
      return anchor.getX()
             - up.getY() * preferredSize.getWidth()
             + up.getX() * preferredSize.getHeight() * 0.5;
    }

    /**
     * Returns the handle's y-coordinate.
     */
    @Override
    public double getY() {
      IOrientedRectangle layout = label.getLayout();

      SizeD preferredSize = emulate
              ? dummyPreferredSize
              : label.getPreferredSize();
      PointD anchor = emulate
              ? dummyLocation
              : layout.getAnchorLocation();
      PointD up = layout.getUp();

      // the resize handle is always placed in the center of the label's right border, i.e. 
      // the resize handle location in the coordinate system of the label's oriented bounds is (1, 0.5)
      // transform the label-local coordinates to regular graph coordinates
      return anchor.getY()
             + up.getX() * preferredSize.getWidth()
             + up.getY() * preferredSize.getHeight() * 0.5;
    }
  }

  /**
   * Visualizes the oriented bounds of the label that is resized.
   */
  private class LabelResizeIndicatorInstaller extends OrientedRectangleIndicatorInstaller {
    /**
     * Returns the oriented bounds of the label that is resized.
     */
    @Override
    protected IOrientedRectangle getRectangle( Object item ) {
      return getCurrentLabelLayout();
    }
  }
}
