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
package style.jcomponentstyle;

import com.yworks.yfiles.graph.styles.AbstractJComponentPortStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IPort;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Port component using a {@link javax.swing.JRadioButton} as port visualization.
 */
public class ComponentPortStyle extends AbstractJComponentPortStyle {

  // the size used as renderSize
  private static final SizeD SIZE;

  static {
    Dimension size = new JRadioButton().getPreferredSize();
    SIZE = new SizeD(size.getWidth(), size.getHeight());
  }

  /**
   * Creates a new ComponentPortStyle.
   */
  public ComponentPortStyle() {
    // as an IPort has no own size in the graph model we need to specify the size it is rendered with.
    setRenderSize(SIZE);
  }

  /**
   * Creates the component representing a port.
   * <p>
   * This implementation returns a configured JRadioButton.
   * </p>
   * @param ctx The context for which the component should be created.
   * @param port The port that will be rendered.
   */
  @Override
  public JComponent createComponent(IRenderContext ctx, IPort port) {
    JRadioButton radioButton = new JRadioButton();
    radioButton.setBackground(Colors.TRANSPARENT);

    // use selected radio buttons for ports of 'Customer' nodes and unselected ones for ports of 'Product' nodes
    radioButton.setSelected(port.getOwner().getTag() instanceof Customer);

    // clear all mouse listener so mouse events over the radio button are used for normal input gestures (like edge creation)
    clearListener(radioButton);

    return radioButton;
  }

  /**
   * Removes all registered {@link java.awt.event.MouseListener}, {@link java.awt.event.MouseMotionListener} and
   * {@link java.awt.event.MouseWheelListener} from the specified component.
   * @param component The component to remove the listener from.
   */
  private void clearListener(Component component) {
    MouseListener[] mouseListeners = component.getMouseListeners();
    for (MouseListener mouseListener : mouseListeners) {
      component.removeMouseListener(mouseListener);
    }
    MouseMotionListener[] mouseMotionListeners = component.getMouseMotionListeners();
    for (MouseMotionListener mouseMotionListener : mouseMotionListeners) {
      component.removeMouseMotionListener(mouseMotionListener);
    }
    MouseWheelListener[] mouseWheelListeners = component.getMouseWheelListeners();
    for (MouseWheelListener mouseWheelListener : mouseWheelListeners) {
      component.removeMouseWheelListener(mouseWheelListener);
    }
  }
}
