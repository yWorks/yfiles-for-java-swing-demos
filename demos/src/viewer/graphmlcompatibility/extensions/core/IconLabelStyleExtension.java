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

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.IconLabelStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

import java.io.File;
import java.net.URL;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;IconLabelStyle&gt;</code>.
 */
@GraphML(contentProperty = "InnerStyle")
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class IconLabelStyleExtension extends MarkupExtension {
  private boolean autoFlippingEnabled;
  private ILabelModelParameter iconPlacement;
  private SizeD iconSize;
  private ILabelStyle innerStyle;
  private InsetsD innerStyleInsets;
  private String url;

  public IconLabelStyleExtension() {
    IconLabelStyle prototype = new IconLabelStyle();
    autoFlippingEnabled = prototype.isAutoFlippingEnabled();
    iconPlacement = prototype.getIconPlacement();
    iconSize = prototype.getIconSize();
    innerStyle = prototype.getWrapped();
    innerStyleInsets = prototype.getWrappedInsets();
    url = asFilePath(prototype.getUrl());
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

  public ILabelModelParameter getIconPlacement() {
    return iconPlacement;
  }

  public void setIconPlacement( ILabelModelParameter value ) {
    iconPlacement = value;
  }

  public SizeD getIconSize() {
    return iconSize;
  }

  public void setIconSize( SizeD value ) {
    iconSize = value;
  }

  public ILabelStyle getInnerStyle() {
    return innerStyle;
  }

  public void setInnerStyle( ILabelStyle value ) {
    innerStyle = value;
  }

  public InsetsD getInnerStyleInsets() {
    return innerStyleInsets;
  }

  public void setInnerStyleInsets( InsetsD value ) {
    innerStyleInsets = value;
  }

  /**
   * Handles the GraphML alias <code>Icon</code> used in yFiles for
   * Java 3.0.x for property <code>Url</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getUrl()
   */
  public String getIcon() {
    return getUrl();
  }

  /**
   * Handles the GraphML alias <code>Icon</code> used in yFiles for
   * Java 3.0.x for property <code>Url</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setUrl(String)
   */
  public void setIcon( String value ) {
    setUrl(value);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl( String value ) {
    url = value;
  }


  @Override
  public Object provideValue( ILookup serviceProvider ) {
    IconLabelStyle style = new IconLabelStyle();
    style.setAutoFlippingEnabled(isAutoFlippingEnabled());
    ILabelModelParameter placement = getIconPlacement();
    if (placement != null) {
      style.setIconPlacement(placement);
    }
    style.setIconSize(getIconSize());
    ILabelStyle wrapped = getInnerStyle();
    if (wrapped != null) {
      style.setWrapped(wrapped);
    }
    style.setWrappedInsets(getInnerStyleInsets());

    String url = getUrl();
    if (url != null) {
      File file = new File(url);
      if (file.isAbsolute()) {
        try {
          style.setUrl(file.toURI().toURL());
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      } else {
        URL resource = style.getClass().getResource(url);
        if (resource != null) {
          style.setUrl(resource);
        }
      }
    }

    return style;
  }

  private static String asFilePath( URL url ) {
    if (url == null) {
      return null;
    } else {
      try {
        return new File(url.toURI()).getAbsolutePath();
      } catch (Exception ex) {
        return null;
      }
    }
  }
}
