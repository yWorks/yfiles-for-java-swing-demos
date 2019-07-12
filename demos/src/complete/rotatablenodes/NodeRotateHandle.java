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
package complete.rotatablenodes;

import complete.rotatablenodes.RotatedNodeResizeHandle.DelegatingContext;
import complete.rotatablenodes.RotatedNodeResizeHandle.DummyPortLocationModelParameterHandle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.AbstractUndoUnit;
import com.yworks.yfiles.graph.ICompoundEdit;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.UndoEngine;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IModelItemCollector;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.SnapContext;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A custom {@link IHandle} implementation needed for rotating label.
 */
public class NodeRotateHandle implements IHandle, IPoint {
  private static final double QUARTER_CIRCLE = Math.PI * 0.5;

  private static Cursor cursor;

  private final INode node;
  private final IReshapeHandler reshapeHandler;
  private final IInputModeContext inputModeContext;
  private final List<IHandle> portHandles;

  private PointD rotationCenter;
  private double initialAngle;

  private ICompoundEdit compoundEdit;

  /**
   * A cache of angles and nodes with those angles.
   * Used for same angle snapping.
   */
  private Map<Double, List<INode>> nodeAngles;

  /**
   * The currently highlighted nodes for same angle snapping.
   */
  private IEnumerable<INode> sameAngleHighlightedNodes;

  /**
   * The angular step size to which rotation should snap (in radians). Default is on eighth
   * of a circle Setting to zero will disable snapping to predefined steps.
   */
  private double snapStep;

  /**
   * The snapping distance when rotation should snap (in radians).
   * The rotation will snap if the angle is less than this distance from a {@link #snapStep}
   * snapping angle. Setting this to a non-positive value will disable snapping to predefined steps.
   */
  private double snapDelta;

  /**
   * The snapping distance (in radians) for snapping to the same angle as other visible nodes. Rotation will snap
   * to another node's rotation angle if the current angle differs from the other one by less than this.
   * Setting this to a non-positive will disable same angle snapping.
   */
  private double snapToSameAngleDelta;


  NodeRotateHandle(INode node, IReshapeHandler reshapeHandler, IInputModeContext inputModeContext) {
    this.node = node;
    this.reshapeHandler = reshapeHandler;
    this.inputModeContext = inputModeContext;
    this.portHandles = new ArrayList<>();
  }

  /**
   * Returns the custom rotate cursor.
   */
  private static Cursor loadCustomCursor( Class resolver ){
    return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
  }

  /**
   * Returns the current oriented rectangle for the given node.
   */
  private static CachingOrientedRectangle getOrientedRectangle(INode node) {
    if (node.getStyle() instanceof  RotatableNodeStyleDecorator) {
      return ((RotatableNodeStyleDecorator) node.getStyle()).getRotatedLayout(node);
    } else {
      return new CachingOrientedRectangle();
    }
  }

  /**
   * Initializes the drag.
   */
  public void initializeDrag(IInputModeContext context) {
    IModelItemCollector imc = context.lookup(IModelItemCollector.class);
    if (imc != null) {
      imc.add(node);
    }

    rotationCenter = node.getLayout().getCenter();
    initialAngle = getAngle();

    IGraph graph = context.lookup(IGraph.class);
    if (graph != null) {
      compoundEdit = graph.beginEdit("Change Rotation Angle", "Change Rotation Angle");
    }

    portHandles.clear();
    DelegatingContext portContext = new DelegatingContext(context);

    for (IPort port : node.getPorts()) {
      DummyPortLocationModelParameterHandle portHandle = new DummyPortLocationModelParameterHandle(port);
      portHandle.initializeDrag(portContext);
      portHandles.add(portHandle);
    }

    if(reshapeHandler != null) {
      reshapeHandler.initializeReshape(context);
    }

    //collect other visible nodes and their angles
    if(snapToSameAngleDelta > 0) {
      CanvasComponent canvas = context.getCanvasComponent();
      IEnumerable<ICanvasObject> canvasObjects = canvas.getCanvasObjects();
      List<INode> rotatedNodes;

      if (nodeAngles == null) {
        nodeAngles = new HashMap<>();
      } else {
        nodeAngles.clear();
      }
      for (ICanvasObject canvasObject : canvasObjects) {
        Object userObj = canvasObject.getUserObject();

        //only collect nodes, that are in the viewport
        if (userObj  instanceof  INode) {
          INode currNode = (INode) userObj;

          //node in the viewport, rotated and not *this* node
          if(canvas.getViewport().intersects(currNode.getLayout().toRectD()) &&
                  currNode.getStyle() instanceof RotatableNodeStyleDecorator &&
                  currNode != node) {
            RotatableNodeStyleDecorator style = (RotatableNodeStyleDecorator) currNode.getStyle();
            Double key = style.getAngle();
            rotatedNodes = nodeAngles.computeIfAbsent(key, k -> new ArrayList<>());
            rotatedNodes.add(currNode);
          }
        }
      }
    }
  }

  /**
   * Updates the node according to the moving handle.
   */
  public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    //calculate the angle
    PointD vector = PointD.subtract(newLocation, rotationCenter).getNormalized();
    double angle = calculateAngle(vector);

    if(shouldSnap(context)) {
      angle = snapAngle(context, angle);
    }
    setAngle(context, angle);

    DelegatingContext portContext = new DelegatingContext(context);
    for (IHandle portHandle : portHandles) {
      portHandle.handleMove(portContext, originalLocation, newLocation);
    }
    if (reshapeHandler != null) {
      RectD bounds = node.getLayout().toRectD();
      reshapeHandler.handleReshape(context, bounds, bounds);
    }
  }

  /**
   * Returns the 'snapped' vector for the given up vector.
   *
   * If the vector is almost horizontal or vertical, this method returns the exact horizontal or vertical
   * up vector instead.
   */
  private static double calculateAngle(PointD upVector) {
    double angle = QUARTER_CIRCLE + Math.atan2(upVector.getY(), upVector.getX());
    return CachingOrientedRectangle.normalizeAngle(-angle);
  }

  /**
   * Snaps the angle to the rotation angles of other nodes and the coordinate axis.
   * Angles near such an angle are replaced with this angle.
   */
  private double snapAngle(IInputModeContext context, double angle) {
    //check if snapping is disabled
    SnapContext snapContext = context.lookup(SnapContext.class);
    if (snapContext != null && !snapContext.isEnabled()){
      return angle;
    }

    //same angle snapping
    if(snapToSameAngleDelta > 0 && nodeAngles != null) {
      //find the first angle that is sufficiently similar
      List<Map.Entry<Double, List<INode>>> candidate = new ArrayList<>();

      for (Map.Entry<Double, List<INode>> entry : nodeAngles.entrySet()) {
        if (CachingOrientedRectangle.normalizeAngle(Math.abs(entry.getKey() - angle)) < snapToSameAngleDelta) {
          candidate.add(entry);
        }
      }

      if (candidate.isEmpty()) {
        // use default value: null
      } else {
        candidate.sort(Comparator.comparingDouble(Map.Entry::getKey));
      }

      if(!candidate.isEmpty()) {
        //add highlight to every matching node
        GraphComponent canvas = ((GraphComponent) context.getCanvasComponent());

        if (sameAngleHighlightedNodes != candidate.get(0).getValue()) {
          clearSameAngleHighlights(context);
        }

        for (INode matchingNode : candidate.get(0).getValue()) {
          canvas.getHighlightIndicatorManager().addHighlight(matchingNode);
        }
        sameAngleHighlightedNodes = IEnumerable.create(candidate.get(0).getValue());
        return candidate.get(0).getKey();
      }
      clearSameAngleHighlights(context);
    }

    if (snapDelta <= 0.0 || snapStep == 0) {
      return angle;
    }

    double mod = Math.abs(angle % snapStep);
    return (mod < snapDelta || mod > snapStep - snapDelta) ?
            snapStep * Math.round(angle / snapStep) :
            angle;
  }

  /**
   * Cancels the drag and cleans up.
   */
  @Override
  public void cancelDrag(IInputModeContext context, PointD originalLocation) {
    setAngle(context, initialAngle);

    DelegatingContext portContext = new DelegatingContext(context);

    for (IHandle portHandle: portHandles) {
      portHandle.cancelDrag(portContext, originalLocation);
    }
    portHandles.clear();
    if (reshapeHandler != null) {
      reshapeHandler.cancelReshape(context, node.getLayout().toRectD());
    }
    if (compoundEdit != null) {
      compoundEdit.cancel();
    }
    nodeAngles = null;
    clearSameAngleHighlights(context);
  }

  /**
   * Finishes the drag an updates the angle of the rotated node.
   */
  @Override
  public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    PointD vector = PointD.subtract(newLocation, rotationCenter).getNormalized();

    double angle = calculateAngle(vector);
    if (shouldSnap(context)) {
      angle = snapAngle(context, angle);
    }
    setAngle(context, angle);

    //Switch width / height for 'vertical' rotations
    //note that other parts of the application need support for this feature as well
    IGraph graph = context.getGraph();
    if (graph == null) {
      return;
    }

    DelegatingContext portContext = new DelegatingContext(context);
    for (IHandle portHandle : portHandles) {
      portHandle.dragFinished(portContext, originalLocation, newLocation);
    }
    portHandles.clear();

    //Workaround: if the OrthogonalEdgeEditingContext is used to keep the edges orthogonal, it is not allowed to
    //change that edge manually. Therefore, we explicitly finish the OrthogonalEdgeEditingContext here and
    //then call the edge router
    OrthogonalEdgeEditingContext edgeEditingContext = context.lookup(OrthogonalEdgeEditingContext.class);
    if(edgeEditingContext != null && edgeEditingContext.isInitialized()) {
      edgeEditingContext.dragFinished();
    }

    if (reshapeHandler != null) {
      reshapeHandler.reshapeFinished(context, node.getLayout().toRectD(), node.getLayout().toRectD());
    }

    if (compoundEdit != null) {
      compoundEdit.commit();
    }

    nodeAngles = null;
    clearSameAngleHighlights(context);
  }

  /**
   * Removes highlights for the same angle snapping.
   */
  private void clearSameAngleHighlights(IInputModeContext context) {
    if (sameAngleHighlightedNodes != null) {
      for (INode highlightedNode : sameAngleHighlightedNodes) {
        ((GraphComponent) context.getCanvasComponent()).getHighlightIndicatorManager().removeHighlight(highlightedNode);
      }
      sameAngleHighlightedNodes = null;
    }
  }

  /**
   * Sets the angle to the node style if the style supports this.
   */
  private void setAngle(IInputModeContext context, double angle) {

    if (node.getStyle() instanceof  RotatableNodeStyleDecorator) {
      RotatableNodeStyleDecorator wrapper = (RotatableNodeStyleDecorator) node.getStyle();
      UndoEngine undoEngine = context.lookup(UndoEngine.class);

      if(undoEngine != null) {
        AngleChangeUndoUnite undoUnit = new AngleChangeUndoUnite(wrapper);
        undoEngine.addUnit(undoUnit);
      }
      wrapper.setAngle(angle);
    }
  }

  /**
   * Reads the angle from the node style if the style supports this.
   */
  private double getAngle() {
    if (node.getStyle() instanceof  RotatableNodeStyleDecorator) {
      return  ((RotatableNodeStyleDecorator) node.getStyle()).getAngle();
    } else {
      return 0;
    }
  }

  /**
   * Whether the current gesture does not disable snapping.
   *
   * @return true if snapping is not temporarily disabled.
   */
  private boolean shouldSnap(IInputModeContext context) {
    boolean shouldSnap = !Objects.equals(context.getCanvasComponent().getLastMouse2DEvent()
            .getModifiers().and(ModifierKeys.SHIFT), ModifierKeys.SHIFT);

    if (!shouldSnap && sameAngleHighlightedNodes != null) {
      clearSameAngleHighlights(context);
    }
    return shouldSnap;
  }

  /**
   * Returns the x-coordinate of the handle's location.
   */
  @Override
  public double getX() {
    return getLocation().getX();
  }

  /**
   * Returns the y-coordinate of the handle's location.
   */
  @Override
  public double getY() {
    return getLocation().getY();
  }

  /**
   * Returns the location of the handle.
   */
  public PointD getLocation() {
    CachingOrientedRectangle orientedRectangle = getOrientedRectangle(node);
    PointD anchor = orientedRectangle.getAnchorLocation();
    SizeD size = orientedRectangle.toSizeD();
    PointD up = orientedRectangle.getUp();

    //calculate the location of the handle from the anchor, the size and the orientation
    double offset = inputModeContext != null ? 20 / inputModeContext.getCanvasComponent().getZoom() : 20;

    PointD upVectorCalculation = new PointD(up.getX() * (size.getHeight()  + offset),
            up.getY() * (size.getHeight() + offset));

    PointD tmp = new PointD(-up.getY() * (size.getWidth() * 0.5), up.getX() * (size.getWidth() * 0.5));

    PointD location = PointD.add(anchor, upVectorCalculation);
    location = PointD.add(location, tmp);

    return location;
  }

  /**
   * Returns the cursor that is shown when using this handle.
   */
  public Cursor getCursor(){
    if (cursor == null) {
      cursor = loadCustomCursor(getClass());
    }
    return cursor;
  }

  /**
   * The type of handle which is use.
   * Always returns {@link HandleTypes#MOVE}.
   */
  public HandleTypes getType(){
    return HandleTypes.MOVE;
  }

  /**
   * Returns the angular step size to which rotation should snap (in radians).
   */
  public double getSnapStep() {
    return snapStep;
  }

  /**
   * Sets the angular step size to which rotation should snap (in radians).
   */
  void setSnapStep(double snapStep) {
    this.snapStep = snapStep;
  }

  /**
   * Returns the snapping distance when rotation should snap (in radians).
   */
  public double getSnapDelta() {
    return snapDelta;
  }

  /**
   * Sets the snapping distance when rotation should snap (in radians).
   */
  void setSnapDelta(double snapDelta) {
    this.snapDelta = snapDelta;
  }

  /**
   * Returns the snapping distance (in radians) for snapping to the same angle as other visible nodes.
   */
  public double getSnapToSameAngleDelta() {
    return snapToSameAngleDelta;
  }

  /**
   * Sets the snapping distance (in radians) for snapping to the same angle as other visible nodes.
   */
  void setSnapToSameAngleDelta(double snapToSameAngleDelta) {
    this.snapToSameAngleDelta = snapToSameAngleDelta;
  }

  /**
   * A undo unit to provide undo and redo functionality for angle changes.
   */
  private static class AngleChangeUndoUnite extends AbstractUndoUnit {

    private final RotatableNodeStyleDecorator nodeStyleDecorator;
    private final double oldAngle;
    private double newAngle;

    /**
     * Initializes a new instance
     */
    AngleChangeUndoUnite(RotatableNodeStyleDecorator nodeStyleDecorator) {
      super("Change Angle");
      this.nodeStyleDecorator = nodeStyleDecorator;
      oldAngle = nodeStyleDecorator.getAngle();
    }

    @Override
    public void undo() {
      newAngle = nodeStyleDecorator.getAngle();
      nodeStyleDecorator.setAngle(oldAngle);
    }

    @Override
    public void redo() {
      nodeStyleDecorator.setAngle(newAngle);
    }
  }
}
