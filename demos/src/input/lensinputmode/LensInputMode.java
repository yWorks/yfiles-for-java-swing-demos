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
package input.lensinputmode;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.Mouse2DEventTypes;
import com.yworks.yfiles.view.input.AbstractInputMode;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * A specialized {@link IInputMode} that displays the currently hovered-over part of the graph in
 * some kind of magnifying glass.
 */
public class LensInputMode extends AbstractInputMode {
  /**
   * Stores the size of the magnifying glass.
   */
  private double size = 250;

  /**
   * Stores the zoom factor of the magnifying glass.
   */
  private double zoomFactor = 3;

  /**
   * The {@link ICanvasObjectGroup} containing the lens graph control.
   */
  private ICanvasObjectGroup lensGroup;

  /**
   * Indicates whether the mouse is inside the {@link com.yworks.yfiles.view.CanvasComponent}.
   * If not, the magnifying {@link GraphComponent} is not painted in the {@link LensVisual}.
   */
  private boolean mouseInside;

  private final IEventListener<Mouse2DEventArgs> updateLensLocation =
      (sender, args) -> repaintComponent();

  private final IEventListener<Mouse2DEventArgs> updateLensVisibility =
      (sender, args) -> {
        mouseInside = args.getEventType() == Mouse2DEventTypes.ENTERED;
        repaintComponent();
      };

  /**
   * Gets the size of the "magnifying glass".
   */
  public double getSize() {
    return size;
  }

  /**
   * Sets the size of the "magnifying glass".
   */
  public void setSize(double size) {
    this.size = size;
    repaintComponent();
  }

  /**
   * Gets the zoom factor used for magnifying the graph.
   */
  public double getZoomFactor() {
    return zoomFactor;
  }

  /**
   * Sets the zoom factor used for magnifying the graph.
   */
  public void setZoomFactor(double zoomFactor) {
    this.zoomFactor = zoomFactor;
    repaintComponent();
  }

  /**
   * Repaints the graph component to which this mode has been added.
   */
  private void repaintComponent() {
    IInputModeContext context = getInputModeContext();
    if (context != null && context.getCanvasComponent() != null) {
      context.getCanvasComponent().repaint();
    }
  }

  /**
   * Installs the {@link LensInputMode} by adding the {@link LensVisual}
   * to the {@link lensGroup} and registering the necessary mouse event listeners.
   */
  public void install(IInputModeContext context, ConcurrencyController controller) {
    super.install(context, controller);

    GraphComponent canvasComponent = (GraphComponent) context.getCanvasComponent();

    lensGroup = canvasComponent.getRootGroup().addGroup();
    lensGroup.above(canvasComponent.getInputModeGroup());
    lensGroup.addChild(new LensVisual(), ICanvasObjectDescriptor.VISUAL);

    canvasComponent.addMouse2DMovedListener(updateLensLocation);
    canvasComponent.addMouse2DDraggedListener(updateLensLocation);

    canvasComponent.addMouse2DEnteredListener(updateLensVisibility);
    canvasComponent.addMouse2DExitedListener(updateLensVisibility);
  }

  /**
   * Uninstalls the {@link LensInputMode} by removing the {@link lensGroup}
   * and unregistering the various mouse event listeners.
   */
  public void uninstall(IInputModeContext context) {
    CanvasComponent canvasComponent = context.getCanvasComponent();

    if (lensGroup != null) {
      lensGroup.remove();
      lensGroup = null;
    }

    canvasComponent.removeMouse2DMovedListener(updateLensLocation);
    canvasComponent.removeMouse2DDraggedListener(updateLensLocation);

    canvasComponent.removeMouse2DEnteredListener(updateLensVisibility);
    canvasComponent.removeMouse2DExitedListener(updateLensVisibility);

    super.uninstall(context);
  }

  /**
   * Paints the "magnifying glass".
   */
  private class LensVisual implements IVisual {
    private final GraphComponent lensGraphComponent;

    LensVisual() {
      GraphComponent graphComponent = getGraphComponent(getInputModeContext());
      lensGraphComponent = new GraphComponent();
      lensGraphComponent.setSize((int) size, (int) size);
      lensGraphComponent.setGraph(graphComponent.getGraph());
      lensGraphComponent.setSelection(graphComponent.getSelection());
      lensGraphComponent.setProjection(graphComponent.getProjection());
      lensGraphComponent.setZoom(getZoomFactor() * graphComponent.getZoom());
      lensGraphComponent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      lensGraphComponent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      setDoubleBuffered(lensGraphComponent, false);
      // This is only necessary to show handles in the zoomed graph. Remove if not needed
      lensGraphComponent.setInputMode(new GraphEditorInputMode());
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      if (!mouseInside || !isEnabled()) {
        return;
      }

      // Update the GraphComponent with all relevant information
      GraphComponent graphComponent = getGraphComponent(context);
      reshape(lensGraphComponent, size);
      lensGraphComponent.setCenter(graphComponent.getLastEventLocation());
      lensGraphComponent.setProjection(context.getProjection());
      lensGraphComponent.setZoom(getZoomFactor() * context.getZoom());

      Graphics2D gfx = (Graphics2D) g.create();
      try {
        PointD drawingLocation = context.toViewCoordinates(graphComponent.getLastEventLocation());
        gfx.transform(context.getToViewTransform());
        gfx.translate(drawingLocation.x, drawingLocation.y);

        // Grab the clip after transforming; otherwise it won't work properly when we set it again later.
        Shape oldClip = gfx.getClip();

        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, size, size);
        gfx.clip(ellipse);

        lensGraphComponent.paint(gfx);

        // Draw the outline of lens (remove clip to avoid artifacts)
        gfx.setClip(oldClip);
        gfx.setColor(Color.GRAY);
        gfx.setStroke(new BasicStroke(2));
        gfx.draw(ellipse);
      } finally {
        gfx.dispose();
      }
    }

    /**
     * Gets the graph component from the given context.
     */
    private GraphComponent getGraphComponent( ICanvasContext context ) {
      return (GraphComponent) context.getCanvasComponent();
    }

    /**
     * Sets the double-buffered property for the given container and all its
     * descendants.
     */
    private void setDoubleBuffered( JComponent parent, boolean enabled ) {
      ArrayList<JComponent> stack = new ArrayList<>();
      stack.add(parent);
      while (!stack.isEmpty()) {
        final JComponent jc = stack.remove(stack.size() - 1);
        jc.setDoubleBuffered(enabled);

        for (int i = 0, n = jc.getComponentCount(); i< n; ++i) {
          Component c = jc.getComponent(i);
          if (c instanceof JComponent) {
            stack.add((JComponent) c);
          }
        }
      }
    }

    /**
     * Sets the size of the given container and updates the bounds of all its
     * descendants.
     */
    private void reshape( JComponent jc, double size ) {
      int newWidth = (int) size;
      int newHeight = (int) size;
      if (newWidth != jc.getWidth() || newHeight != jc.getHeight()) {
        jc.setSize(newWidth, newHeight);
        // update descendant bounds for new size
        jc.getLayout().layoutContainer(jc);
      }
    }
  }
}
