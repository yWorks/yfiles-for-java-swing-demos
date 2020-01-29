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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ImageNodeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

import java.net.URL;

/**
 * Handles the yFiles for Java (Swing) (Swing) 3.0.x version of GraphML element
 * <code>&lt;ImageNodeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class ImageNodeStyleExtension extends MarkupExtension {
  private GeneralPath normalizedOutline;
  private URL url;

  public ImageNodeStyleExtension() {
    ImageNodeStyle prototype = new ImageNodeStyle();
    normalizedOutline = prototype.getNormalizedOutline();
    url = prototype.getUrl();
  }

  public GeneralPath getNormalizedOutline() {
    return normalizedOutline;
  }

  public void setNormalizedOutline( GeneralPath value ) {
    normalizedOutline = value;
  }

  /**
   * Handles the GraphML alias <code>ImageUrl</code> used in yFiles for
   * Java 3.0.x for property <code>Url</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getUrl()
   */
  public URL getImageUrl() {
    return getUrl();
  }

  /**
   * Handles the GraphML alias <code>ImageUrl</code> used in yFiles for
   * Java 3.0.x for property <code>Url</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setUrl(URL)
   */
  public void setImageUrl( URL value ) {
    setUrl(value);
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl( URL value ) {
    url = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    ImageNodeStyle style = new ImageNodeStyle();
    style.setNormalizedOutline(getNormalizedOutline());
    style.setUrl(getUrl());
    return style;
  }
}
