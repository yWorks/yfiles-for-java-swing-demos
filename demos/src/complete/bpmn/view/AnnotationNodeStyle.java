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

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import java.awt.Paint;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Annotation according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class AnnotationNodeStyle extends BpmnNodeStyle {
  private boolean left;

  /**
   * Gets a value indicating whether the bracket of the open rectangle is shown on the left side.
   * @return The Left.
   * @see #setLeft(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isLeft() {
    return left;
  }

  /**
   * Sets a value indicating whether the bracket of the open rectangle is shown on the left side.
   * @param value The Left to set.
   * @see #isLeft()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setLeft( boolean value ) {
    if (value != left) {
      incrementModCount();
      left = value;
    }
  }

  private Paint background = BpmnConstants.ANNOTATION_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the annotation.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "AnnotationDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the annotation.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "AnnotationDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
    }
  }

  private Paint outline = BpmnConstants.ANNOTATION_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the annotation.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "AnnotationDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the annotation.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "AnnotationDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
    }
  }

  @Override
  void updateIcon( INode node ) {
    setIcon(IconFactory.createAnnotation(isLeft(), getBackground(), getOutline()));
  }

  /**
   * Creates a new instance.
   */
  public AnnotationNodeStyle() {
    setLeft(true);
    setMinimumSize(new SizeD(30, 10));
  }

}
