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
 * Custom {@link IHandle} implementation for rotating labels.
 */
public class LabelRotateHandle implements IHandle {
  private final ILabel label;
  private IInputModeContext inputModeContext;

  private IPoint location;
  private boolean emulate;
  private PointD rotationCenter;
  private PointD dummyLocation;
  private PointD up;
  private ICanvasObject sizeIndicator;

  /**
   * Initializes a new {@code LabelRotateHandle} instance for the given label.
   */
  public LabelRotateHandle( ILabel label ) {
    this.label = label;
  }

  /**
   * Returns the handle's type.
   */
  @Override
  public HandleTypes getType() {
    return HandleTypes.ROTATE;
  }

  /**
   * Returns the cursor that should be displayed while this handle
   * implementation is active.
   */
  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  /**
   * Returns the handle's location.
   */
  @Override
  public IPoint getLocation() {
    if (location == null) {
      location = new LabelRotateHandleLivePoint();
    }
    return location;
  }

  /**
   * Initializes the handle's state right before a rotate operation.
   * Invoked when dragging is about to start.
   */
  @Override
  public void initializeDrag( IInputModeContext context ) {
    inputModeContext = context;

    IOrientedRectangle layout = label.getLayout();
    rotationCenter = layout.getCenter();
    dummyLocation = layout.getAnchorLocation();
    up = layout.getUp();

    // start using the calculated dummy bounds
    emulate = true;

    CanvasComponent canvasComponent = context.getCanvasComponent();
    if (canvasComponent != null) {
      // add a ghost visualization for the rotated label bounds
      LabelRotateIndicatorInstaller installer = new LabelRotateIndicatorInstaller();
      sizeIndicator = installer.addCanvasObject(
              canvasComponent.getCanvasContext(),
              canvasComponent.getSelectionGroup(),
              this);
    }
  }

  /**
   * Calculates the new label angle for the current handle position.
   * Invoked when the handle is dragged.
   */
  @Override
  public void handleMove( IInputModeContext context, PointD originalLocation, PointD newLocation ) {
    // calculate the up vector
    up = PointD.subtract(newLocation, rotationCenter).getNormalized();

    // and the anchor point
    SizeD preferredSize = label.getPreferredSize();

    double w =  preferredSize.getWidth() * 0.5;
    double h = preferredSize.getHeight() * 0.5;

    double anchorX = rotationCenter.getX() + up.getY() * w - up.getX() * h;
    double anchorY = rotationCenter.getY() - up.getX() * w - up.getY() * h;

    // calculate the new location
    dummyLocation = new PointD(anchorX, anchorY);
  }

  /**
   * Rotates the handle's associated label.
   * Invoked when the drag gesture has finished.
   */
  @Override
  public void dragFinished( IInputModeContext context, PointD originalLocation, PointD newLocation ) {
    IGraph graph = context.getGraph();
    if (graph != null) {
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
   * Cancels the rotate operation and resets the handle's state.
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
  private OrientedRectangle getCurrentLabelLayout() {
    SizeD preferredSize = label.getPreferredSize();
    IOrientedRectangle layout = label.getLayout();
    return new OrientedRectangle(
            emulate ? dummyLocation.getX() : layout.getAnchorX(),
            emulate ? dummyLocation.getY() : layout.getAnchorY(),
            preferredSize.getWidth(),
            preferredSize.getHeight(),
            up.getX(),
            up.getY());
  }


  /**
   * Represents the location of the rotate handle.
   */
  private class LabelRotateHandleLivePoint implements IPoint {
    /**
     * Returns the handle's x-coordinate.
     */
    @Override
    public double getX() {
      IOrientedRectangle layout = label.getLayout();

      SizeD preferredSize = label.getPreferredSize();
      PointD anchor = emulate
              ? dummyLocation
              : layout.getAnchorLocation();
      PointD up = emulate
              ? LabelRotateHandle.this.up
              : layout.getUp();

      double offset = inputModeContext != null
              ? 20 / inputModeContext.getCanvasComponent().getZoom()
              : 20;
      // the rotate handle is placed above the center of the label's top border, i.e. 
      // the rotate handle location in the coordinate system of the label's oriented bounds is (0.5, 1 + d)
      //   where offset = d * preferredSize.getHeight()
      // transform the label-local coordinates to regular graph coordinates
      return anchor.getX()
             - up.getY() * preferredSize.getWidth() * 0.5
             + up.getX() * (preferredSize.getHeight() + offset);
    }

    /**
     * Returns the handle's y-coordinate.
     */
    @Override
    public double getY() {
      IOrientedRectangle layout = label.getLayout();

      SizeD preferredSize = label.getPreferredSize();
      PointD anchor = emulate
              ? dummyLocation
              : layout.getAnchorLocation();
      PointD up = emulate
              ? LabelRotateHandle.this.up
              : layout.getUp();

      double offset = inputModeContext != null
              ? 20 / inputModeContext.getCanvasComponent().getZoom()
              : 20;
      // the rotate handle is placed above the center of the label's top border, i.e. 
      // the rotate handle location in the coordinate system of the label's oriented bounds is (0.5, 1 + d)
      //   where offset = d * preferredSize.getHeight()
      // transform the label-local coordinates to regular graph coordinates
      return anchor.getY()
             + up.getX() * preferredSize.getWidth() * 0.5
             + up.getY() * (preferredSize.getHeight() + offset);
    }
  }

  /**
   * Visualizes the oriented bounds of the label that is rotated.
   */
  private class LabelRotateIndicatorInstaller extends OrientedRectangleIndicatorInstaller {
    /**
     * Returns the oriented bounds of the label that is rotated.
     */
    @Override
    protected IOrientedRectangle getRectangle( Object item ) {
      return getCurrentLabelLayout();
    }
  }
}
