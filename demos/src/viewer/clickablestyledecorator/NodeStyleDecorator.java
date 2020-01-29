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
package viewer.clickablestyledecorator;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ImageNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;

import javax.swing.JOptionPane;
import java.awt.Image;
import java.net.URL;

/**
 * This node style decorator adds an image in the upper right corner of a given node style.
 * <p>
 * The {@link com.yworks.yfiles.graph.styles.ImageNodeStyle} class is used to render the decoration image.
 * </p><p>
 * This style overrides the {@link com.yworks.yfiles.view.IVisibilityTestable#isVisible} with a custom implementation
 * that also checks the visibility of the decoration image in addition to calling the implementation of the decorated style.
 * </p><p>
 * Other checks like {@link com.yworks.yfiles.view.input.IHitTestable#isHit} and
 * {@link com.yworks.yfiles.view.input.IMarqueeTestable#isInBox} are simply delegated to the base style in order to not
 * make the node selectable by clicking or marquee selecting the decoration image part of the visualization. If desired,
 * this feature can be implemented like demonstrated in {@link NodeStyleDecorator#isVisible}.
 * </p>
 */
public class NodeStyleDecorator extends AbstractNodeStyle {
  private INodeStyle wrapped;
  private ImageNodeStyle decoStyle;
  private SimpleNode decoNode;

  /**
   * Initializes a new instance of {@code NodeStyleDecorator} with a wrapped
   * style of type {@link ShapeNodeStyle}.
   */
  public NodeStyleDecorator() {
    this(new ShapeNodeStyle());
  }

  /**
   * Initializes a new instance of {@code NodeStyleDecorator} with the given base style.
   * @param wrapped The style to be decorated.
   */
  NodeStyleDecorator( INodeStyle wrapped ) {
    this.wrapped = wrapped;
    this.decoStyle = new ImageNodeStyle();

    // this dummy node is passed to the image node style to render the decoration image
    // its size is the size of the decoration
    // its location is adjusted during each createVisual and updateVisual call
    this.decoNode = new SimpleNode();
    this.decoNode.setLayout(new RectD(0, 0, 32, 32));
  }

  /**
   * Creates a new visual as a combination of the base node visualization and
   * the decoration.
   * @param context The render context.
   * @param node The node to which this style is assigned.
   * @return The created visual.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    if (getUrl() == null) {
      return getCreator(node, wrapped).createVisual(context);
    }

    // specify the size of the decoration to retain the image's aspect ratio
    Image image = decoStyle.getImage();
    double iw = image.getWidth(null);
    double ih = image.getHeight(null);
    if (iw > ih) {
      decoNode.setLayout(new RectD(0, 0, 32, (int) Math.ceil(32 * ih / iw)));
    } else if (iw < ih) {
      decoNode.setLayout(new RectD(0, 0, (int) Math.ceil(32 * iw / ih), 32));
    }
    // ... then set the location of the decoration
    decoNode.setLayout(getDecorationLayout(node));

    // create the base visualization
    IVisual baseVisual = getCreator(node, wrapped).createVisual(context);

    // create the decoration visualization
    IVisual decoration = getCreator(decoNode, decoStyle).createVisual(context);

    // create a composite visual for base visualization and decoration
    VisualGroup composite = new VisualGroup();
    composite.add(baseVisual);
    composite.add(decoration);

    return composite;
  }

  /**
   * Updates the provided visual to show a combination of the base node
   * visualization and the decoration.
   * @param context The render context.
   * @param oldVisual A visual that has been created in a call to
   * {@link #createVisual(IRenderContext, INode)}.
   * @param node The node to which this style instance is assigned.
   * @return The updated visual.
   */
  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, INode node ) {
    if (getUrl() == null) {
      return getCreator(node, wrapped).updateVisual(context, oldVisual);
    }

    // check if it is possible to update the old visualizations
    // ... first check if the old visual has the correct type
    if (oldVisual == null || !oldVisual.getClass().equals(VisualGroup.class)) {
      return createVisual(context, node);
    }

    // ... then check if it has the correct number of children
    VisualGroup composite = (VisualGroup) oldVisual;
    if (composite.getChildren().size() != 2) {
      return createVisual(context, node);
    }

    // update old visualization(s)
    // ... first update the location of the decoration
    decoNode.setLayout(getDecorationLayout(node));

    // ... then update the old base visual
    set(composite, 0,
        getCreator(node, wrapped).updateVisual(context, get(composite, 0)));
    // ... and finally update the old decoration visual
    set(composite, 1,
        getCreator(decoNode, decoStyle).updateVisual(context, get(composite, 1)));

    return oldVisual;
  }

  /**
   * Returns whether at least one of the base visualization and the decoration is visible.
   * @param context The canvas context.
   * @param rect The clipping rectangle.
   * @param node The node to which this style instance is assigned.
   * @return {@code true} if either the base visualization or the decoration is visible.
   */
  @Override
  protected boolean isVisible( ICanvasContext context, RectD rect, INode node ) {
    return wrapped.getRenderer().getVisibilityTestable(node, wrapped).isVisible(context, rect) ||
           rect.intersects(getDecorationLayout(node));
  }

  /**
   * Returns whether the base visualization is hit.
   * @param context The context.
   * @param location The point to test.
   * @param node The node to which this style instance is assigned.
   * @return {@code true} if the base visualization is hit.
   */
  @Override
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    return wrapped.getRenderer().getHitTestable(node, wrapped).isHit(context, location) ||
           // the decoration should be "clickable" and thus needs to be checked for a "hit"
           getDecorationLayout(node).contains(location);
  }

  /**
   * Returns whether the base visualization is in the box, we don't want the decoration to be marquee selectable.
   * @param context The input mode context.
   * @param rect The marquee selection box.
   * @param node The node to which this style instance is assigned.
   * @return {@code true} if the base visualization is hit.
   */
  @Override
  protected boolean isInBox( IInputModeContext context, RectD rect, INode node ) {
    // delegate check to wrapped style, because the decoration should not be
    // marquee selectable
    return wrapped.getRenderer().getMarqueeTestable(node, wrapped).isInBox(context, rect);
  }

  /**
   * Gets the intersection of a line with the visual representation of the node.
   * @param node The node to which this style instance is assigned.
   * @param inner The coordinates of a point lying {@link AbstractNodeStyle#isInside} inside the shape.
   * @param outer The coordinates of a point lying outside the shape.
   * @return The intersection point if one has been found or {@code null}, otherwise.
   */
  @Override
  protected PointD getIntersection( INode node, PointD inner, PointD outer ) {
    // delegate check to wrapped style
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getIntersection(inner, outer);
  }

  /**
   * Returns whether the provided point is inside of the base visualization.
   * @param node The node to which this style instance is assigned.
   * @param location The point to test.
   * @return {@code true} if the provided location is inside of the base visualization.
   */
  @Override
  protected boolean isInside( INode node, PointD location ) {
    // delegate check to wrapped style
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).isInside(location);
  }

  /**
   * Returns the {@link URL} that identifies the decoration image used by this
   * style.
   */
  public URL getUrl() {
    return decoStyle.getUrl();
  }

  /**
   * Specifies the {@link URL} that identifies the decoration image used by this
   * style.
   */
  public void setUrl( URL url ) {
    decoStyle.setUrl(url);
  }

  /**
   * Returns the style that is decorated by this style.
   */
  public INodeStyle getWrapped() {
    return wrapped;
  }

  /**
   * Specifies the style that is decorated by this style.
   */
  public void setWrapped( INodeStyle wrapped ) {
    this.wrapped = wrapped;
  }

  /**
   * Returns the layout of the decoration for the given node.
   */
  private RectD getDecorationLayout( INode node ) {
    IRectangle nodeLayout = node.getLayout();
    SizeD size = decoNode.getLayout().toSizeD();
    return new RectD(
            nodeLayout.getX() + nodeLayout.getWidth() - size.getWidth() * 0.5,
            nodeLayout.getY() - size.getHeight() * 0.5,
            size.getWidth(),
            size.getHeight()
    );
  }

  /**
   * Returns the visual creator for the given node and style.
   */
  private static IVisualCreator getCreator( INode node, INodeStyle style ) {
    return style.getRenderer().getVisualCreator(node, style);
  }

  /**
   * Returns the visual at the given index in the given group.
   */
  private static IVisual get( VisualGroup group, int index ) {
    return group.getChildren().get(index);
  }

  /**
   * Sets the visual at the given index in the given group.
   */
  private static void set( VisualGroup group, int index, IVisual visual ) {
    group.getChildren().set(index, visual);
  }


  /**
   * Returns an appropriate instance of {@link ClickHandler} when queried for
   * implementations of {@link IClickListener}.
   */
  @Override
  protected Object lookup( INode node, Class type ) {
    if (type == IClickListener.class) {
      return new ClickHandler(node);
    }
    return super.lookup(node, type);
  }

  /**
   * Handles click events that occur inside decoration images.
   * <p>
   * This example implementation opens a message dialog informing the user
   * about the handled click.
   * </p>
   */
  private static final class ClickHandler implements IClickListener, IHitTestable {
    private final INode node;

    ClickHandler( INode node ) {
      this.node = node;
    }

    /**
     * Returns itself as an {@link IHitTestable} that determines if a (click)
     * location is inside the decoration image of this click listener's
     * associated node.
     * @see #isHit(IInputModeContext, PointD)
     */
    @Override
    public IHitTestable getHitTestable() {
      return this;
    }

    /**
     * Handles click events (that occur inside the decoration image of this
     * click listener's associated node).
     * <p>
     * This example implementation opens a message dialog informing the user
     * about the handled click.
     * </p>
     */
    @Override
    public void onClicked( IInputModeContext context, PointD location ) {
      JOptionPane.showMessageDialog(context.getCanvasComponent(), "Decorator clicked");
    }


    /**
     * Determines if the given (click) location is inside the decoration image
     * of this click listener's associated node.
     * @see #getHitTestable()
     */
    @Override
    public boolean isHit( IInputModeContext context, PointD location ) {
      NodeStyleDecorator style = (NodeStyleDecorator) node.getStyle();
      return style.getDecorationLayout(node).contains(location);
    }
  }
}
