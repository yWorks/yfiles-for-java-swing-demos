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
package style.compositenodestyle;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.ILassoTestable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Style that allows to combines other {@link INodeStyle} instances to form a composite visualization.
 */
public class CompositeNodeStyle extends AbstractNodeStyle {

  // the style to which any hit detection will delegate to
  private final INodeStyle mainStyle;
  // any additional style the will be rendered above the main style
  private final ArrayList<INodeStyle> components = new ArrayList<>();
  // insets to apply to a style, if any
  private final HashMap<INodeStyle, InsetsD> insetsForStyle = new HashMap<>();

  private final SimpleNode dummyNode = new SimpleNode();

  /**
   * Initializes a new <code>CompositeNodeStyle</code> instance with the given styles as the main style.
   *
   * @param mainStyle the style which will be used for hit detection and visibility checks.
   */
  public CompositeNodeStyle(INodeStyle mainStyle) {
    this.mainStyle = mainStyle;
    components.add(mainStyle);
  }

  /**
   * Initializes a new <code>CompositeNodeStyle</code> instance with the given styles as the main style.
   *
   * @param mainStyle the style which will be used for hit detection and visibility checks.
   * @param insets    the insets to be used with this style.
   */
  public CompositeNodeStyle(INodeStyle mainStyle, InsetsD insets) {
    this(mainStyle);
    if (insets != null) {
      this.insetsForStyle.put(mainStyle, insets);
    }
  }

  /**
   * Initializes a new <code>CompositeNodeStyle</code> instance with the given styles. The first style is considered to
   * be this composite node style' main style.
   *
   * @param styles the style instances that will be combined in this composite node style.
   */
  public CompositeNodeStyle(INodeStyle... styles) {
    // set the first style as main style per convention
    if (styles.length > 0) {
      this.mainStyle = styles[0];
      this.components.addAll(Arrays.asList(styles));
    } else {
      throw new IllegalArgumentException("At least one style must be provided.");
    }
  }

  /**
   * Adds a style to be rendered
   *
   * @param style the {@link INodeStyle} to be added.
   */
  public void addNodeStyle(INodeStyle style) {
    components.add(style);
  }

  /**
   * Adds a style to be rendered, with the provided insets.
   *
   * @param style  the {@link INodeStyle} to be added.
   * @param insets the {@link InsetsD} to be used.
   */
  public void addNodeStyle(INodeStyle style, InsetsD insets) {
    components.add(style);
    if (insets != null) {
      insetsForStyle.put(style, insets);
    }
  }

  /**
   * Creates the visual for a node.
   */
  @Override
  protected VisualGroup createVisual(IRenderContext context, INode node) {
    setDummyLabelsPortsAndTag(node);

    VisualGroup group = new VisualGroup();

    for (INodeStyle component : components) {
      setDummyLayoutAndStyle(node, component);
      INodeStyle style = component;
      IVisualCreator creator = style.getRenderer().getVisualCreator(dummyNode, style);

      group.add(creator.createVisual(context));
    }
    return group;
  }

  /**
   * Updates the Visuals
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (oldVisual == null) {
      return createVisual(context, node);
    }

    for (int i = 0; i < components.size(); i++) {
      setDummyLayoutAndStyle(node, components.get(i));

      VisualGroup group = (VisualGroup) oldVisual;
      IVisual oldStyleVisual = group.getChildren().get(i);
      INodeStyle style = dummyNode.getStyle();
      IVisual newStyleVisual =
              oldStyleVisual == null
                      ? (style.getRenderer().getVisualCreator(dummyNode, style).createVisual(context))
                      : (style.getRenderer()
                      .getVisualCreator(dummyNode, style)
                      .updateVisual(context, oldStyleVisual));

      if (newStyleVisual != oldStyleVisual) {
        ((VisualGroup) oldVisual).getChildren().set(i, newStyleVisual);
      }
    }
    return oldVisual;
  }


  /**
   * Calculates the visual bounds for the given node. This method delegates bounds calculation to the composite node
   * style's main style.
   */
  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getBoundsProvider(dummyNode, mainStyle).getBounds(context);
  }

  /**
   * Calculates the intersection point of the line segment defined by the two given points and the given node's visual
   * outline. This method delegates intersection calculation to the composite node style's main style.
   */
  @Override
  protected PointD getIntersection(INode node, PointD inner, PointD outer) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getShapeGeometry(dummyNode, mainStyle).getIntersection(inner, outer);
  }

  /**
   * Calculates the geometry of the visual outline for the given node. This method delegates outline calculation to the
   * composite node style's main style.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getShapeGeometry(dummyNode, mainStyle).getOutline();
  }

  /**
   * Determines if a click at the given location hits the visualization for the given node. This method delegates hit
   * testing to the composite node style's main style.
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getHitTestable(dummyNode, mainStyle).isHit(context, location);
  }

  /**
   * Determines if the visualization for the specified node is included in given marquee selection. This method
   * delegates marquee testing to the composite node style's main style.
   */
  @Override
  protected boolean isInBox(IInputModeContext context, RectD rectangle, INode node) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getMarqueeTestable(dummyNode, mainStyle).isInBox(context, rectangle);
  }

  /**
   * Determines if the visualization for the specified node is included in the given lasso selection. This method
   * delegates lasso testing to the composite node style's main style.
   */
  @Override
  protected boolean isInPath(IInputModeContext context, GeneralPath path, INode node) {
    INode dummyNode = configureMainStyle(node);

    ILassoTestable testable = mainStyle.getRenderer()
            .getContext(dummyNode, mainStyle)
            .lookup(ILassoTestable.class);
    if (testable != null) {
      return testable.isInPath(context, path);
    } else {
      return super.isInPath(context, path, node);
    }
  }

  /**
   * Determines if the provided point is geometrically inside the visual bounds of the node. This method delegates
   * contains testing to the composite node style's main style.
   */
  @Override
  protected boolean isInside(INode node, PointD location) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getShapeGeometry(dummyNode, mainStyle).isInside(location);
  }

  /**
   * Determines if the given node's visualization intersects the given viewport rectangle. This method delegates
   * visibility testing to the composite node style's main style.
   */
  @Override
  protected boolean isVisible(ICanvasContext context, RectD rectangle, INode node) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getVisibilityTestable(dummyNode, mainStyle).isVisible(context, rectangle);
  }

  /**
   * Handles queries for behavior implementations for the given node. This method delegates behavior lookup to the
   * composite node style's main style.
   */
  @Override
  protected Object lookup(INode node, Class type) {
    INode dummyNode = configureMainStyle(node);
    return mainStyle.getRenderer().getContext(dummyNode, mainStyle).lookup(type);
  }

  private INode configureMainStyle(INode node) {
    // in case we do not have insets for the main node style, we can use the original node because
    // the layout is the same
    if (!insetsForStyle.containsKey(mainStyle)) {
      return node;
    } else {
      setDummyLayoutAndStyle(node, mainStyle);
      setDummyLabelsPortsAndTag(node);
      return dummyNode;
    }
  }

  private void setDummyLayoutAndStyle(INode prototype, INodeStyle style) {
    InsetsD insets = insetsForStyle.get(style);
    reshape(prototype, insets);
    dummyNode.setStyle(style);
  }

  private void setDummyLabelsPortsAndTag(INode prototype) {
    dummyNode.setLabels(prototype.getLabels());
    dummyNode.setPorts(prototype.getPorts());
    dummyNode.setTag(prototype.getTag());
  }

  /**
   * Sets the given rectangle's geometry to the interior of the given node. In this context,
   * <em>interior</em> means the node's paraxial bounds minus the given insets.
   */
  private void reshape(INode prototype, InsetsD insets) {
    IRectangle nl = prototype.getLayout();
    if (insets != null) {
      dummyNode.setLayout(new RectD(
              nl.getX() + insets.left,
              nl.getY() + insets.top,
              nl.getWidth() - insets.getHorizontalInsets(),
              nl.getHeight() - insets.getVerticalInsets()));
    } else {
      dummyNode.setLayout(nl);
    }
  }
}
