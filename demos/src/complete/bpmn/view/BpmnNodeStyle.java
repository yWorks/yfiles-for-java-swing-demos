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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import com.yworks.yfiles.view.input.NodeSizeConstraintProvider;

import java.awt.geom.AffineTransform;

/**
 * A {@link AbstractNodeStyle} implementation used as base class for nodes styles representing BPMN elements.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class BpmnNodeStyle extends AbstractNodeStyle {

  private SizeD minimumSize = SizeD.EMPTY;

  /**
   * Gets the minimum node size for nodes using this style.
   * @return The MinimumSize.
   * @see #setMinimumSize(SizeD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Empty", classValue = SizeD.class)
  public final SizeD getMinimumSize() {
    return this.minimumSize;
  }

  /**
   * Sets the minimum node size for nodes using this style.
   * @param value The MinimumSize to set.
   * @see #getMinimumSize()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Empty", classValue = SizeD.class)
  public final void setMinimumSize( SizeD value ) {
    this.minimumSize = value;
  }

  private IIcon icon;

  final IIcon getIcon() {
    return this.icon;
  }

  final void setIcon( IIcon value ) {
    this.icon = value;
  }

  private int modCount;

  final int getModCount() {
    return this.modCount;
  }

  final void setModCount( int value ) {
    this.modCount = value;
  }

  private CountingClickListener clickListener;



  final int incrementModCount() {
    ++this.modCount;
    return this.modCount;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected IVisual createVisual( IRenderContext context, INode node ) {
    updateIcon(node);
    if (getIcon() == null) {
      return null;
    }

    RectD bounds = node.getLayout().toRectD();
    getIcon().setBounds(new RectD(PointD.ORIGIN, bounds.toSizeD()));
    IVisual visual = getIcon().createVisual(context);

    MyContainer container = new MyContainer(modCount, bounds);
    if (visual != null) {
      container.add(visual);
    }
    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));

    return container;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, INode node ) {
    if (getIcon() == null) {
      return null;
    }

    MyContainer container = (oldVisual instanceof MyContainer) ? (MyContainer)oldVisual : null;

    if (container == null || container.getModCount() != getModCount()) {
      return createVisual(context, node);
    }

    RectD newBounds = node.getLayout().toRectD();

    if (RectD.equals(container.getBounds(), newBounds)) {
      // node bounds didn't change
      return oldVisual;
    }

    if (!SizeD.equals(container.getBounds().getSize(), newBounds.getSize())) {
      RectD newIconBounds = new RectD(PointD.ORIGIN, newBounds.getSize());
      getIcon().setBounds(newIconBounds);

      IVisual oldIconVisual = null;
      IVisual newIconVisual = null;
      if (container.getChildren().size() == 0) {
        newIconVisual = getIcon().createVisual(context);
      } else {
        oldIconVisual = container.getChildren().get(0);
        newIconVisual = getIcon().updateVisual(context, oldIconVisual);
      }

      // update visual
      if (oldIconVisual != newIconVisual) {
        if (oldIconVisual != null) {
          container.remove(oldIconVisual);
        }
        if (newIconVisual != null) {
          container.add(newIconVisual);
        }
      }
    }

    // update the transformation that moves the container to the location of the node
    double layoutX = newBounds.getX();
    double layoutY = newBounds.getY();
    AffineTransform transform = container.getTransform();
    transform.setToTranslation(layoutX, layoutY);

    container.setBounds(newBounds);

    return container;
  }


  /**
   * Updates the {@link #getIcon() Icon}.
   * <p>
   * This method is called by {@link #createVisual(IRenderContext, INode)}.
   * </p>
   * @param node The node to which this style instance is assigned.
   */
  void updateIcon( INode node ) {
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Object lookup( INode node, Class type ) {
    Object lookup = super.lookup(node, type);
    if (lookup == null && type == INodeSizeConstraintProvider.class) {
      if (!minimumSize.isEmpty()) {
        return new NodeSizeConstraintProvider(minimumSize, SizeD.INFINITE);
      }
    }
    return lookup;
  }

  final IClickListener getDelegatingClickListener( IIcon icon ) {
    IClickListener coreListener = icon.lookup(IClickListener.class);
    if (coreListener == null) {
      return null;
    }
    if (clickListener == null) {
      clickListener = new CountingClickListener(coreListener);
    } else {
      clickListener.setCoreProvider(coreListener);
    }
    return clickListener;
  }

  private class CountingClickListener implements IClickListener {

    public CountingClickListener( IClickListener coreProvider ) {
      this.setCoreProvider(coreProvider);
    }

    private IClickListener coreProvider;

    public final IClickListener getCoreProvider() {
      return this.coreProvider;
    }

    public final void setCoreProvider( IClickListener value ) {
      this.coreProvider = value;
    }

    public final IHitTestable getHitTestable() {
      return getCoreProvider().getHitTestable();
    }

    public final void onClicked( IInputModeContext context, PointD location ) {
      getCoreProvider().onClicked(context, location);
      incrementModCount();
    }

  }

  private static class MyContainer extends VisualGroup {
    private int modCount;
    private RectD bounds;

    public MyContainer(int modCount, RectD bounds) {
      this.modCount = modCount;
      this.bounds = bounds;
    }

    public int getModCount() {
      return modCount;
    }

    public void setModCount(int modCount) {
      this.modCount = modCount;
    }

    public RectD getBounds() {
      return bounds;
    }

    public void setBounds(RectD bounds) {
      this.bounds = bounds;
    }
  }

}
