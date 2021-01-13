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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import java.awt.Paint;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Data Store according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class DataStoreNodeStyle extends BpmnNodeStyle {
  private Paint background = BpmnConstants.DATA_STORE_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the data store.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataStoreDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the data store.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataStoreDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
    }
  }

  private Paint outline = BpmnConstants.DATA_STORE_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the data store.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataStoreDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the data store.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataStoreDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
    }
  }

  /**
   * Creates a new instance.
   */
  public DataStoreNodeStyle() {
    setMinimumSize(new SizeD(30, 20));
  }

  @Override
  void updateIcon( INode node ) {
    setIcon(IconFactory.createDataStore(getBackground(), getOutline()));
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    final double halfEllipseHeight = 0.125;
    GeneralPath path = new GeneralPath(16);

    path.moveTo(0, halfEllipseHeight);
    path.lineTo(0, 1 - halfEllipseHeight);
    path.cubicTo(0, 1, 1, 1, 1, 1 - halfEllipseHeight);
    path.lineTo(1, halfEllipseHeight);
    path.cubicTo(1, 0, 0, 0, 0, halfEllipseHeight);
    path.close();

    Matrix2D transform = new Matrix2D();
    RectD layout = node.getLayout().toRectD();
    transform.translate(layout.getTopLeft());
    transform.scale(layout.getWidth(), layout.getHeight());
    path.transform(transform);
    return path;
  }

}
