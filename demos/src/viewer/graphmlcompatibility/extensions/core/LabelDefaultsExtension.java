/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.LabelDefaults;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;LabelDefaults&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class LabelDefaultsExtension extends MarkupExtension {
  private boolean autoAdjustingPreferredSizeEnabled;
  private ILabelModelParameter layoutParameter;
  private boolean layoutParameterInstanceSharingEnabled;
  private ILabelStyle style;
  private boolean styleInstanceSharingEnabled;

  public LabelDefaultsExtension() {
    final LabelDefaults prototype = new LabelDefaults();
    autoAdjustingPreferredSizeEnabled = prototype.isAutoAdjustingPreferredSizeEnabled();
    layoutParameter = prototype.getLayoutParameter();
    layoutParameterInstanceSharingEnabled = prototype.isLayoutParameterInstanceSharingEnabled();
    style = prototype.getStyle();
    styleInstanceSharingEnabled = prototype.isStyleInstanceSharingEnabled();
  }


  /**
   * Handles the GraphML alias <code>AutoAdjustPreferredSize</code> used in yFiles for
   * Java 3.0.x for property <code>AutoAdjustingPreferredSizeEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoAdjustingPreferredSizeEnabled()
   */
  public boolean isAutoAdjustPreferredSize() {
    return isAutoAdjustingPreferredSizeEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoAdjustPreferredSize</code> used in yFiles for
   * Java 3.0.x for property <code>AutoAdjustingPreferredSizeEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoAdjustingPreferredSizeEnabled(boolean)
   */
  public void setAutoAdjustPreferredSize( boolean value ) {
    setAutoAdjustingPreferredSizeEnabled(value);
  }

  public boolean isAutoAdjustingPreferredSizeEnabled() {
    return autoAdjustingPreferredSizeEnabled;
  }

  public void setAutoAdjustingPreferredSizeEnabled( boolean value ) {
    autoAdjustingPreferredSizeEnabled = value;
  }

  /**
   * Handles the GraphML alias <code>LabelModelParameter</code> used in yFiles for
   * Java 3.0.x for property <code>LayoutParameter</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getLayoutParameter()
   */
  public ILabelModelParameter getLabelModelParameter() {
    return getLayoutParameter();
  }

  /**
   * Handles the GraphML alias <code>LabelModelParameter</code> used in yFiles for
   * Java 3.0.x for property <code>LayoutParameter</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setLayoutParameter(ILabelModelParameter)
   */
  public void setLabelModelParameter( ILabelModelParameter value ) {
    setLayoutParameter(value);
  }

  public ILabelModelParameter getLayoutParameter() {
    return layoutParameter;
  }

  public void setLayoutParameter( ILabelModelParameter value ) {
    layoutParameter = value;
  }

  /**
   * Handles the GraphML alias <code>ShareLabelModelParameterInstance</code> used in yFiles for
   * Java 3.0.x for property <code>LayoutParameterInstanceSharingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isLayoutParameterInstanceSharingEnabled()
   */
  public boolean isShareLabelModelParameterInstance() {
    return isLayoutParameterInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ShareLabelModelParameterInstance</code> used in yFiles for
   * Java 3.0.x for property <code>LayoutParameterInstanceSharingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setLayoutParameterInstanceSharingEnabled(boolean)
   */
  public void setShareLabelModelParameterInstance( boolean value ) {
    setLayoutParameterInstanceSharingEnabled(value);
  }

  public boolean isLayoutParameterInstanceSharingEnabled() {
    return layoutParameterInstanceSharingEnabled;
  }

  public void setLayoutParameterInstanceSharingEnabled( boolean value ) {
    layoutParameterInstanceSharingEnabled = value;
  }

  public ILabelStyle getStyle() {
    return style;
  }

  public void setStyle( ILabelStyle value ) {
    style = value;
  }

  /**
   * Handles the GraphML alias <code>ShareStyleInstance</code> used in yFiles for
   * Java 3.0.x for property <code>StyleInstanceSharingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isStyleInstanceSharingEnabled()
   */
  public boolean isShareStyleInstance() {
    return isStyleInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ShareStyleInstance</code> used in yFiles for
   * Java 3.0.x for property <code>StyleInstanceSharingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setStyleInstanceSharingEnabled(boolean)
   */
  public void setShareStyleInstance( boolean value ) {
    setStyleInstanceSharingEnabled(value);
  }

  public boolean isStyleInstanceSharingEnabled() {
    return styleInstanceSharingEnabled;
  }

  public void setStyleInstanceSharingEnabled( boolean value ) {
    styleInstanceSharingEnabled = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    LabelDefaults defaults = new LabelDefaults();
    defaults.setAutoAdjustingPreferredSizeEnabled(isAutoAdjustPreferredSize());
    defaults.setLayoutParameterInstanceSharingEnabled(isLayoutParameterInstanceSharingEnabled());
    defaults.setLayoutParameter(getLayoutParameter());
    ILabelStyle style = getStyle();
    if (style != null) {
      defaults.setStyle(style);
    }
    defaults.setStyleInstanceSharingEnabled(isShareStyleInstance());
    return defaults;
  }
}
