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
import com.yworks.yfiles.graph.PortDefaults;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;PortDefaults&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class PortDefaultsExtension extends MarkupExtension {
  private boolean autoCleanupEnabled;
  private IPortLocationModelParameter locationParameter;
  private boolean locationParameterInstanceSharingEnabled;
  private IPortStyle style;
  private boolean styleInstanceSharingEnabled;

  public PortDefaultsExtension() {
    PortDefaults prototype = new PortDefaults();
    autoCleanupEnabled = prototype.isAutoCleanUpEnabled();
    locationParameter = prototype.getLocationParameter();
    locationParameterInstanceSharingEnabled = prototype.isLocationParameterInstanceSharingEnabled();
    style = prototype.getStyle();
    styleInstanceSharingEnabled = prototype.isStyleInstanceSharingEnabled();
  }


  /**
   * Handles the GraphML alias <code>AutoCleanup</code> used in yFiles for
   * Java 3.0.x for property <code>AutoCleanupEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoCleanupEnabled()
   */
  public boolean isAutoCleanup() {
    return isAutoCleanupEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoCleanup</code> used in yFiles for
   * Java 3.0.x for property <code>AutoCleanupEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoCleanupEnabled(boolean)
   */
  public void setAutoCleanup( boolean value ) {
    setAutoCleanupEnabled(value);
  }

  public boolean isAutoCleanupEnabled() {
    return autoCleanupEnabled;
  }

  public void setAutoCleanupEnabled( boolean value ) {
    autoCleanupEnabled = value;
  }

  /**
   * Handles the GraphML alias <code>LocationModelParameter</code> used in yFiles for
   * Java 3.0.x for property <code>LocationParameter</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getLocationParameter()
   */
  public IPortLocationModelParameter getLocationModelParameter() {
    return getLocationParameter();
  }

  /**
   * Handles the GraphML alias <code>LocationModelParameter</code> used in yFiles for
   * Java 3.0.x for property <code>LocationParameter</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setLocationParameter(IPortLocationModelParameter)
   */
  public void setLocationModelParameter( IPortLocationModelParameter value ) {
    setLocationParameter(value);
  }

  public IPortLocationModelParameter getLocationParameter() {
    return locationParameter;
  }

  public void setLocationParameter( IPortLocationModelParameter value ) {
    locationParameter = value;
  }

  /**
   * Handles the GraphML alias <code>ShareLocationModelParameterInstance</code> used in yFiles for
   * Java 3.0.x for property <code>LocationParameterInstanceSharingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isLocationParameterInstanceSharingEnabled()
   */
  public boolean isShareLocationModelParameterInstance() {
    return isLocationParameterInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ShareLocationModelParameterInstance</code> used in yFiles for
   * Java 3.0.x for property <code>LocationParameterInstanceSharingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setLocationParameterInstanceSharingEnabled(boolean)
   */
  public void setShareLocationModelParameterInstance( boolean value ) {
    setLocationParameterInstanceSharingEnabled(value);
  }

  public boolean isLocationParameterInstanceSharingEnabled() {
    return locationParameterInstanceSharingEnabled;
  }

  public void setLocationParameterInstanceSharingEnabled( boolean value ) {
    locationParameterInstanceSharingEnabled = value;
  }

  public IPortStyle getStyle() {
    return style;
  }

  public void setStyle( IPortStyle value ) {
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
  public Object provideValue( ILookup serviceProvider ) {
    PortDefaults defaults = new PortDefaults();
    defaults.setAutoCleanUpEnabled(isAutoCleanupEnabled());
    IPortLocationModelParameter param = getLocationParameter();
    if (param != null) {
      defaults.setLocationParameter(param);
    }
    defaults.setLocationParameterInstanceSharingEnabled(isLocationParameterInstanceSharingEnabled());
    IPortStyle style = getStyle();
    if (style != null) {
      defaults.setStyle(style);
    }
    defaults.setStyleInstanceSharingEnabled(isStyleInstanceSharingEnabled());
    return defaults;
  }
}
