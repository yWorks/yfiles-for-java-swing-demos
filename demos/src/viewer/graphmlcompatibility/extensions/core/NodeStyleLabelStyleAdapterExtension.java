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
package viewer.graphmlcompatibility.extensions.core;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.NodeStyleLabelStyleAdapter;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;NodeStyleLabelStyleAdapter&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class NodeStyleLabelStyleAdapterExtension extends MarkupExtension {
  private boolean autoFlippingEnabled;
  private ILabelStyle labelStyle;
  private InsetsD labelStyleInsets;
  private INodeStyle nodeStyle;

  public NodeStyleLabelStyleAdapterExtension() {
    NodeStyleLabelStyleAdapter prototype = new NodeStyleLabelStyleAdapter();
    autoFlippingEnabled = prototype.isAutoFlippingEnabled();
    labelStyle = prototype.getLabelStyle();
    labelStyleInsets = prototype.getLabelStyleInsets();
    nodeStyle = prototype.getNodeStyle();
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * Java 3.0.x for property <code>AutoFlippingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoFlippingEnabled()
   */
  public boolean isAutoFlip() {
    return isAutoFlippingEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * Java 3.0.x for property <code>AutoFlippingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoFlippingEnabled(boolean)
   */
  public void setAutoFlip( boolean value ) {
    setAutoFlippingEnabled(value);
  }

  public boolean isAutoFlippingEnabled() {
    return autoFlippingEnabled;
  }

  public void setAutoFlippingEnabled( boolean value ) {
    autoFlippingEnabled = value;
  }

  public ILabelStyle getLabelStyle() {
    return labelStyle;
  }

  public void setLabelStyle( ILabelStyle value ) {
    labelStyle = value;
  }

  public InsetsD getLabelStyleInsets() {
    return labelStyleInsets;
  }

  public void setLabelStyleInsets( InsetsD value ) {
    labelStyleInsets = value;
  }

  public INodeStyle getNodeStyle() {
    return nodeStyle;
  }

  public void setNodeStyle( INodeStyle value ) {
    nodeStyle = value;
  }


  @Override
  public Object provideValue( ILookup serviceProvider ) {
    NodeStyleLabelStyleAdapter style = new NodeStyleLabelStyleAdapter();
    style.setAutoFlippingEnabled(isAutoFlippingEnabled());
    ILabelStyle labelStyle = getLabelStyle();
    if (labelStyle != null) {
      style.setLabelStyle(labelStyle);
    }
    style.setLabelStyleInsets(getLabelStyleInsets());
    INodeStyle nodeStyle = getNodeStyle();
    if (nodeStyle != null) {
      style.setNodeStyle(nodeStyle);
    }
    return style;
  }
}
