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
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;CollapsibleNodeStyleDecorator&gt;</code>.
 */
@GraphML(contentProperty = "Wrapped")
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class CollapsibleNodeStyleDecoratorExtension extends MarkupExtension {
  private ILabelModelParameter buttonLocationParameter;
  private InsetsD insets;
  private INodeStyle wrapped;

  public CollapsibleNodeStyleDecoratorExtension() {
    CollapsibleNodeStyleDecorator prototype = new CollapsibleNodeStyleDecorator();
    buttonLocationParameter = prototype.getButtonPlacement();
    insets = prototype.getInsets();
    wrapped = prototype.getWrapped();
  }


  public ILabelModelParameter getButtonLocationParameter() {
    return buttonLocationParameter;
  }

  public void setButtonLocationParameter( ILabelModelParameter value ) {
    buttonLocationParameter = value;
  }

  public InsetsD getInsets() {
    return insets;
  }

  public void setInsets( InsetsD value ) {
    insets = value;
  }

  public INodeStyle getWrapped() {
    return wrapped;
  }

  public void setWrapped( INodeStyle value ) {
    wrapped = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    CollapsibleNodeStyleDecorator style = new CollapsibleNodeStyleDecorator();
    ILabelModelParameter param = getButtonLocationParameter();
    if (param != null) {
      style.setButtonPlacement(param);
    }
    style.setInsets(getInsets());
    INodeStyle wrapped = getWrapped();
    if (wrapped != null) {
      style.setWrapped(wrapped);
    }
    return style;
  }
}
