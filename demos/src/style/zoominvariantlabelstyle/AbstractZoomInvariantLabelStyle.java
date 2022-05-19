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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ISelectionIndicatorInstaller;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.OrientedRectangleIndicatorInstaller;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 * A label style that may render labels at the same size regardless of the zoom level.
 * The style is implemented as a wrapper for an existing label style.
 */
public abstract class AbstractZoomInvariantLabelStyle extends AbstractLabelStyle {
  private ILabelStyle innerLabelStyle;
  private final OrientedRectangle dummyLabelLayout;
  private final OrientedRectangle dummyLabelBounds;
  private final SimpleLabel dummyLabel;
  
  protected AbstractZoomInvariantLabelStyle() {
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    this.innerLabelStyle = labelStyle;
    this.dummyLabelLayout = new OrientedRectangle();
    this.dummyLabelBounds = new OrientedRectangle();
    this.dummyLabel = new SimpleLabel(null, "", FreeLabelModel.INSTANCE.createDynamic(dummyLabelLayout));
  }

  /**
   * Gets the wrapped label style used to request the {@link #getPreferredSize(ILabel) preferred size} and to render the
   * label.
   * @return The wrapped label style.
   */
  public ILabelStyle getInnerLabelStyle() {
    return innerLabelStyle;
  }

  /**
   * Sets the wrapped label style used to request the {@link #getPreferredSize(ILabel) preferred size} and to render the
   * label.
   * @param labelStyle The wrapped label style.
   */
  public void setInnerLabelStyle( ILabelStyle labelStyle ) {
    this.innerLabelStyle = labelStyle;
  }

  /**
   * Determines the scale factor for the given label and zoom level.
   * @param label the current label which will be styled
   * @param zoom the current zoom level
   */
  protected abstract double getScaleForZoom( ILabel label, double zoom );

  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    return innerLabelStyle.getRenderer().getPreferredSize(label, innerLabelStyle);
  }

  @Override
  protected IVisual createVisual( IRenderContext context, ILabel label ) {
    // creates the container for the visual and sets a transform for view coordinates
    VisualGroup container = new VisualGroup();
    double scale = getScaleForZoom(label, context.getZoom());

    updateDummyLabel(context, label, scale);
    container.setTransform(new AffineTransform(
      scale,
      0,
      0,
      scale,
      label.getLayout().getCenter().getX(),
      label.getLayout().getCenter().getY()
    ));

    IVisualCreator creator = innerLabelStyle.getRenderer().getVisualCreator(dummyLabel, innerLabelStyle);
    // the wrapped style should always think it's rendering with the zoom factor
    // induced through the style's minimum and/or maximum zoom value
    AffineTransform inverse = null;
    try {
      inverse = container.getTransform().createInverse();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    IVisual visual = creator.createVisual(new DummyContext(context, scale, inverse));

    if (visual != null) {
      // add the created visual to the container
      container.add(visual);
    }
    return container;
  }

  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, ILabel label ) {
    if (!(oldVisual instanceof VisualGroup) || ((VisualGroup) oldVisual).getChildren().isEmpty()) {
      return createVisual(context, label);
    }
    VisualGroup container = (VisualGroup) oldVisual;
    double scale = getScaleForZoom(label, context.getZoom());

    updateDummyLabel(context, label, scale);
    container.setTransform(new AffineTransform(
      scale,
      0,
      0,
      scale,
      label.getLayout().getCenter().getX(),
      label.getLayout().getCenter().getY()
    ));

    // update the visual created by the inner style renderer
    IVisual visual = container.getChildren().get(0);
    IVisualCreator creator = innerLabelStyle.getRenderer().getVisualCreator(dummyLabel, innerLabelStyle);

    AffineTransform inverse = null;
    try {
      inverse = container.getTransform().createInverse();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    IVisual updatedVisual = creator.updateVisual(new DummyContext(context, scale, inverse), visual);

    if (updatedVisual != visual) {
      container.remove(visual);

      if (updatedVisual != null) {
        container.add(updatedVisual);
      }
    }

    return container;
  }

  /**
   * Updates the internal label to match the given original label.
   */
  private void updateDummyLabel( ICanvasContext context, ILabel original, double scale ) {
    dummyLabel.setOwner(original.getOwner());
    dummyLabel.setStyle(original.getStyle());
    dummyLabel.setTag(original.getTag());
    dummyLabel.setText(original.getText());

    IOrientedRectangle originalLayout = original.getLayout();
    dummyLabelLayout.reshape(originalLayout);
    dummyLabelLayout.setCenter(new PointD(0, 0));
    dummyLabel.setPreferredSize(dummyLabelLayout.toSizeD());

    dummyLabelBounds.reshape(originalLayout);
    dummyLabelBounds.resize(originalLayout.getWidth() * scale, originalLayout.getHeight() * scale);
    dummyLabelBounds.setCenter(originalLayout.getCenter());
  }

  @Override
  protected RectD getBounds( ICanvasContext context, ILabel label ) {
    return getUpdatedLabelBounds(context, label).getBounds();
  }

  @Override
  protected boolean isVisible( ICanvasContext context, RectD clip, ILabel label ) {
    return clip.intersects(getUpdatedLabelBounds(context, label), 0);
  }

  @Override
  protected boolean isHit( IInputModeContext context, PointD location, ILabel label ) {
    return getUpdatedLabelBounds(context, label).contains(location, 0.001);
  }

  @Override
  protected boolean isInBox( IInputModeContext context, RectD box, ILabel label ) {
    return box.intersects(getUpdatedLabelBounds(context, label), 0);
  }

  private OrientedRectangle getUpdatedLabelBounds(ICanvasContext context, ILabel label) {
    double scale = getScaleForZoom(label, context.getZoom());
    updateDummyLabel(context, label, scale);
    return this.dummyLabelBounds;
  }

  @Override
  protected Object lookup( ILabel label, Class type ) {
    if (type == ISelectionIndicatorInstaller.class) {
      return new OrientedRectangleIndicatorInstaller(dummyLabelBounds);
    }
    return super.lookup(label, type);
  }
}
