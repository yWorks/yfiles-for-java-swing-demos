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
package tutorial02_CustomStyles.step25_StyleDecorator;

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/////////////// This class is new in this sample ///////////////

/**
 * A simple node style wrapper that takes a given node style and adds label edge rendering as a visual decorator on top
 * of the wrapped visualization. This node style wrapper implementation adds the label edge rendering that was formerly
 * part of {@link MySimpleNodeStyle} to the wrapped style. For the purpose of this tutorial step, label edge rendering
 * was removed from {@link MySimpleNodeStyle}. Similar to this implementation, wrapping styles for other graph items can
 * be created by implementing {@link com.yworks.yfiles.graph.styles.AbstractEdgeStyle}, {@link
 * com.yworks.yfiles.graph.styles.AbstractLabelStyle} and {@link com.yworks.yfiles.graph.styles.AbstractPortStyle}.
 */
public class MyNodeStyleDecorator extends AbstractNodeStyle {
  // the style that is decorated by this instance
  private INodeStyle wrapped;

  /**
   * Initializes a new <code>MyNodeStyleDecorator</code>instance using the given wrapped style.
   * @param wrappedStyle the style that is decorated by this instance
   */
  public MyNodeStyleDecorator(INodeStyle wrappedStyle) {
    wrapped = wrappedStyle;
  }

  /**
   * Creates the visual containing the wrapped visual as well as the visuals for the label connectors.
   */
  @Override
  protected VisualGroup createVisual(IRenderContext context, INode node) {
    // create a container that holds all visuals needed to paint the style
    VisualGroup container = new VisualGroup();

    // create the wrapped style's visual
    IVisual wrappedVisual = wrapped.getRenderer().getVisualCreator(node, wrapped).createVisual(context);
    container.add(wrappedVisual);

    // create edge-like connector's visuals
    LabelEdgesGroup labelEdges = new LabelEdgesGroup();
    List<PointD> labelLocations = getLabelLocations(node);
    labelEdges.update(context, node, labelLocations);
    labelEdges.setTransform(AffineTransform.getTranslateInstance(node.getLayout().getX(), node.getLayout().getY()));
    container.add(labelEdges);

    return container;
  }

  /**
   * Re-renders the style using the old visual instead of creating a new one for each call. It is strongly recommended
   * to do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, INode)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (!(oldVisual instanceof VisualGroup)) {
      return createVisual(context, node);
    }

    VisualGroup group = (VisualGroup) oldVisual;

    // update the wrapped style's visual
    IVisual oldWrappedVisual = group.getChildren().get(0);
    IVisual newWrappedVisual = wrapped.getRenderer().getVisualCreator(node, wrapped).updateVisual(context,
        oldWrappedVisual);
    if (oldWrappedVisual != newWrappedVisual) {
      group.getChildren().set(0, newWrappedVisual);
    }

    // update edge-like connector's visuals
    LabelEdgesGroup labelEdges = (LabelEdgesGroup) group.getChildren().get(1);
    List<PointD> labelLocations = getLabelLocations(node);
    labelEdges.update(context, node, labelLocations);
    labelEdges.getTransform().setToTranslation(node.getLayout().getX(), node.getLayout().getY());

    return group;
  }

  /**
   * Returns the center points of labels to draw edge-like connectors for, relative the node's top left corner.
   */
  private List<PointD> getLabelLocations(INode node) {
    List<PointD> labelLocations = new ArrayList<>();
    for (ILabel label : node.getLabels()) {
      PointD labelCenter = label.getLayout().getCenter();
      PointD nodeTopLeft = node.getLayout().getTopLeft();
      labelLocations.add(PointD.subtract(labelCenter, nodeTopLeft));
    }
    return labelLocations;
  }

  /**
   * A {@link com.yworks.yfiles.view.VisualGroup} that holds all {@link LabelEdgeVisual} instances for one node.
   */
  private static class LabelEdgesGroup extends VisualGroup {
    // edge style used for all edge-like connectors
    private static final MySimpleEdgeStyle CONNECTOR_STYLE;

    // the locations of the node's labels
    private List<PointD> labelLocations;

    static {
      CONNECTOR_STYLE = new MySimpleEdgeStyle(new MySimpleArrow(), IArrow.NONE, 2);
    }

    public LabelEdgesGroup() {
      this.labelLocations = new ArrayList<>();
    }

    /**
     * Updates the edge-like connectors from a node to its labels.
     */
    public void update(IRenderContext context, INode node, List<PointD> labelLocations) {
      if (this.labelLocations.equals(labelLocations)) {
        // nothing to update since the labels has not been changed
        return;
      }

      this.labelLocations = labelLocations;
      if (node.getLabels().size() > 0) {
        // create a SimpleEdge which will be used as a dummy for rendering
        SimpleEdge simpleEdge = new SimpleEdge(null, null);
        // assign the style
        simpleEdge.setStyle(CONNECTOR_STYLE);

        // create a SimpleNode which provides the source port for the edge but won't be drawn itself
        SimpleNode sourceDummyNode = new SimpleNode();
        sourceDummyNode.setLayout(new RectD(0, 0, node.getLayout().getWidth(), node.getLayout().getHeight()));
        sourceDummyNode.setStyle(node.getStyle());

        // create a SimpleNode which provides the target port for the edge but won't be drawn itself
        SimpleNode targetDummyNode = new SimpleNode();

        // set source port to the port of the node using a dummy node that is located at the origin.
        simpleEdge.setSourcePort(new SimplePort(sourceDummyNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
        // create port on target dummy node for the label target
        simpleEdge.setTargetPort(new SimplePort(targetDummyNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));

        // render one edge for each label
        int index = 0;
        for (PointD labelLocation : this.labelLocations) {
          LabelEdgeVisual visual = getLabelEdgeVisual(index);
          // let the visual update itself with the new parameters
          visual.update(context, labelLocation, simpleEdge, targetDummyNode);
          index++;
        }

        // if the number of labels has been decreased we remove spare visuals
        if (index < getChildren().size()) {
          removeSpareVisuals(index);
        }
      } else {
        getChildren().clear();
      }
    }

    /**
     * Returns the visual to render the connection to the label at in the given
     * index or creates a new visual if there is none for the given index.
     */
    private LabelEdgeVisual getLabelEdgeVisual(int index) {
      if (getChildren().size() > index && getChildren().get(index) != null) {
        return (LabelEdgeVisual) getChildren().get(index);
      } else {
        LabelEdgeVisual labelEdgeVisual = new LabelEdgeVisual();
        getChildren().add(labelEdgeVisual);
        return labelEdgeVisual;
      }
    }

    /**
     * Removes visuals that are spare from the children list.
     * @param index the index of the first spare child.
     */
    private void removeSpareVisuals(int index) {
      getChildren().subList(index, getChildren().size() - 1).clear();
    }
  }

  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    // delegate this to the wrapped style
    return wrapped.getRenderer().getBoundsProvider(node, wrapped).getBounds(context);
  }

  @Override
  protected boolean isVisible(ICanvasContext context, RectD clip, INode node) {
    // first check if the wrapped style is visible
    if (wrapped.getRenderer().getVisibilityTestable(node, wrapped).isVisible(context, clip)) {
      return true;
    }
    // if not, check for labels connection lines
    RectD enlargedClip = clip.getEnlarged(10);
    PointD nodeCenter = node.getLayout().getCenter();
    return node.getLabels().stream().anyMatch(label -> enlargedClip.intersectsLine(nodeCenter, label.getLayout().getCenter()));
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    // delegate this to the wrapped style since we don't want the visual decorator to be hit testable
    return wrapped.getRenderer().getHitTestable(node, wrapped).isHit(context, location);
  }

  @Override
  protected boolean isInBox(IInputModeContext context, RectD box, INode node) {
    // delegate this to the wrapped style
    return wrapped.getRenderer().getMarqueeTestable(node, wrapped).isInBox(context, box);
  }

  @Override
  protected Object lookup(INode node, Class type) {
    // delegate this to the wrapped style
    return wrapped.getRenderer().getContext(node, wrapped).lookup(type);
  }

  @Override
  protected PointD getIntersection(INode node, PointD inner, PointD outer) {
    // delegate this to the wrapped style
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getIntersection(inner, outer);
  }

  @Override
  protected boolean isInside(INode node, PointD point) {
    // delegate this to the wrapped style
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).isInside(point);
  }

  @Override
  protected GeneralPath getOutline(INode node) {
    // delegate this to the wrapped style
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getOutline();
  }

  /**
   * A {@link IVisual} that paints edge-like connectors from a node to one of its labels.
   */
  private static class LabelEdgeVisual implements IVisual {
    // the current location of the label
    private PointD labelLocation;
    // the visual that paints the edge
    private IVisual edgeVisual;

    LabelEdgeVisual() {
      this.labelLocation = PointD.ORIGIN;
    }

    /**
     * Updates the visual that paints the edge-like connector from a node to one of its labels.
     */
    public void update(IRenderContext context, PointD labelLocation, SimpleEdge simpleEdge, SimpleNode targetDummyNode) {
      if (!labelLocation.equals(this.labelLocation)) {
        this.labelLocation = labelLocation;

        // move the dummy node to the location of the label
        targetDummyNode.setLayout(new RectD(labelLocation.getX(), labelLocation.getY(), 0, 0));

        // now create a new visual or update an existing one that paints an edge-like connector using
        // the style interface
        IEdgeStyleRenderer renderer = simpleEdge.getStyle().getRenderer();
        IVisualCreator creator = renderer.getVisualCreator(simpleEdge, simpleEdge.getStyle());
        if (edgeVisual == null) {
          edgeVisual = creator.createVisual(context);
        } else {
          creator.updateVisual(context, edgeVisual);
        }
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // call the paint method of the edge's visual
      edgeVisual.paint(context, gfx);
    }
  }
}
