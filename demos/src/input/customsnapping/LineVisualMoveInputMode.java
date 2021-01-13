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
package input.customsnapping;

import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.InputModeEventArgs;
import com.yworks.yfiles.view.input.MoveInputMode;

import java.util.List;

/**
 * This input mode allows moving {@link LineVisual}s using a drag gesture.
 */
class LineVisualMoveInputMode extends MoveInputMode {
  private final List<LineVisual> lineVisuals;
  private IPositionHandler handler;

  /**
   * Creates a new instance.
   *
   * @param snapLines A list of the {@link LineVisual}s that shall be moved by this input mode.
   */
  public LineVisualMoveInputMode(List<LineVisual> snapLines) {
    this.lineVisuals = snapLines;
  }

  /**
   * Returns the list of {@link LineVisual}s this input mode works with.
   */
  public List<LineVisual> getLineVisuals() {
    return lineVisuals;
  }

  /**
   * Clears the {@link com.yworks.yfiles.view.input.MoveInputMode#getPositionHandler()} property and sets the {@link
   * com.yworks.yfiles.view.input.MoveInputMode#getHitTestable()} property to check for hit {@link LineVisual}s.
   */
  @Override
  public void install( IInputModeContext context, ConcurrencyController concurrencyController ) {
    super.install(context, concurrencyController);
    setPositionHandler(null);
    setHitTestable(this::isValidHit);
  }

  /**
   * Returns true if an LineVisual can be found in a close surrounding of the given location.
   */
  private boolean isValidHit(ICanvasContext context, PointD location) {
    LineVisual line = tryGetAdditionalSnapLineAt(location);
    if (line != null) {
      handler = new LineVisualPositionHandler(line, location);
      return true;
    }
    handler = null;
    return false;
  }

  /**
   * Returns the first LineVisual found in a close surrounding of the given location or null if none can be
   * found.
   */
  private LineVisual tryGetAdditionalSnapLineAt(PointD location) {
    RectD surrounding = new RectD(location.getX() - 3, location.getY() - 3, 6, 6);

    return getLineVisuals().stream()
        // filter all snap lines that intersect with surrounding
        .filter(line -> surrounding.intersectsLine(line.getFrom(), line.getTo()))
        // return the first one or null if no snap line is in the surrounding
        .findFirst().orElse(null);
  }

  /**
   * Sets the {@link com.yworks.yfiles.view.input.MoveInputMode#getPositionHandler()} property.
   */
  @Override
  protected void onDragStarting(InputModeEventArgs inputModeEventArgs) {
    setPositionHandler(handler);
    super.onDragStarting(inputModeEventArgs);
  }

  /**
   * Clears the {@link com.yworks.yfiles.view.input.MoveInputMode#getPositionHandler()} property.
   */
  @Override
  protected void onDragCanceled(InputModeEventArgs inputModeEventArgs) {
    super.onDragCanceled(inputModeEventArgs);
    setPositionHandler(null);
  }

  /**
   * Clears the {@link com.yworks.yfiles.view.input.MoveInputMode#getPositionHandler()} property.
   */
  @Override
  protected void onDragFinished(InputModeEventArgs inputModeEventArgs) {
    super.onDragFinished(inputModeEventArgs);
    setPositionHandler(null);
  }

  /**
   * An {@link IPositionHandler} used to move {@link LineVisual} instances.
   */
  private static class LineVisualPositionHandler implements IPositionHandler {
    private final LineVisual line;
    private final PointD mouseDeltaFromStart;
    private PointD startFrom;

    /**
     * Creates a new handler for the given <code>line</code>.
     *
     * @param line          the {@link LineVisual} to move
     * @param mouseLocation the mouse location at the beginning of a move gesture
     */
    public LineVisualPositionHandler(LineVisual line, PointD mouseLocation) {
      this.line = line;
      mouseDeltaFromStart = PointD.subtract(mouseLocation, line.getFrom());
    }

    /**
     * Called by clients to set the position to the given coordinates. The given position are interpreted to be the new
     * position of the {@link LineVisual#getFrom()} property of the moved {@link
     * LineVisual}.
     *
     * @param location the new location for the {@link LineVisual#getFrom()} property
     */
    public void setPosition(PointD location) {
      PointD delta = PointD.subtract(location, line.getFrom());
      line.setFrom(location);
      line.setTo(PointD.add(line.getTo(), delta));
    }

    /**
     * Returns a view of the location of the item. The point describes the current world coordinate of the {@link
     * LineVisual#getFrom()} property of the moved {@link LineVisual}.
     */
    @Override
    public IPoint getLocation() {
      return line.getFrom();
    }

    /**
     * Called by clients to indicate that the element is going to be dragged. This call will be followed by one or more
     * calls to {@link com.yworks.yfiles.view.input.IDragHandler#handleMove(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)},
     * and a final {@link com.yworks.yfiles.view.input.IDragHandler#dragFinished(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)}
     * or {@link com.yworks.yfiles.view.input.IDragHandler#cancelDrag(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD)}.
     *
     * @param inputModeContext the context to retrieve information about the drag from
     */
    @Override
    public void initializeDrag(IInputModeContext inputModeContext) {
      startFrom = line.getFrom();
    }

    /**
     * Called by clients to indicate that the element has been dragged and its position should be updated. This method
     * may be called more than once after an initial {@link com.yworks.yfiles.view.input.IDragHandler#initializeDrag(com.yworks.yfiles.view.input.IInputModeContext)}
     * and the final call will be followed by either one {@link com.yworks.yfiles.view.input.IDragHandler#dragFinished(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)}
     * or one {@link com.yworks.yfiles.view.input.IDragHandler#cancelDrag(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD)}
     * call.
     *
     * @param inputModeContext The context to retrieve information about the drag from.
     * @param originalLocation The value of the {@link com.yworks.yfiles.view.input.IDragHandler#getLocation() Location}
     *                         property at the time of {@link com.yworks.yfiles.view.input.IDragHandler#initializeDrag(com.yworks.yfiles.view.input.IInputModeContext)}.
     * @param newLocation      The coordinates in the world coordinate system that the client wants the handle to be at.
     *                         Depending on the implementation the {@link com.yworks.yfiles.view.input.IDragHandler#getLocation()
     *                         Location} may or may not be modified to reflect the new value.
     */
    @Override
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation,
                              PointD newLocation) {
      setPosition(PointD.subtract(newLocation, mouseDeltaFromStart));
    }

    /**
     * Called by clients to indicate that the dragging has been canceled by the user. This method may be called after
     * the initial {@link #initializeDrag(com.yworks.yfiles.view.input.IInputModeContext)} and zero or more invocations of
     * {@link #handleMove(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)}.
     * Alternatively to this method the {@link #dragFinished(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)}
     * method might be called.
     *
     * @param inputModeContext The context to retrieve information about the drag from.
     * @param originalLocation The value of the coordinate of the {@link com.yworks.yfiles.view.input.IDragHandler#getLocation()
     *                         Location} property at the time of {@link com.yworks.yfiles.view.input.IDragHandler#initializeDrag(com.yworks.yfiles.view.input.IInputModeContext)}.
     */
    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
      setPosition(startFrom);
    }

    /**
     * Called by clients to indicate that the repositioning has just been finished. This method may be called after the
     * initial {@link #initializeDrag(com.yworks.yfiles.view.input.IInputModeContext)} and zero or more invocations of {@link
     * com.yworks.yfiles.view.input.IDragHandler#handleMove(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)}.
     * Alternatively to this method the {@link com.yworks.yfiles.view.input.IDragHandler#cancelDrag(com.yworks.yfiles.view.input.IInputModeContext, com.yworks.yfiles.geometry.PointD)}
     * method might be called.
     *
     * @param inputModeContext The context to retrieve information about the drag from.
     * @param originalLocation The value of the {@link com.yworks.yfiles.view.input.IDragHandler#getLocation() Location}
     *                         property at the time of {@link com.yworks.yfiles.view.input.IDragHandler#initializeDrag(com.yworks.yfiles.view.input.IInputModeContext)}.
     * @param newLocation      The coordinates in the world coordinate system that the client wants the handle to be at.
     *                         Depending on the implementation the {@link com.yworks.yfiles.view.input.IDragHandler#getLocation()
     *                         Location} may or may not be modified to reflect the new value. This is the same value as
     *                         delivered in the last invocation of {@link com.yworks.yfiles.view.input.IDragHandler#handleMove(com.yworks.yfiles.view.input.IInputModeContext,
     *                         com.yworks.yfiles.geometry.PointD, com.yworks.yfiles.geometry.PointD)}
     */
    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      setPosition(PointD.subtract(newLocation, mouseDeltaFromStart));
    }
  }
}
