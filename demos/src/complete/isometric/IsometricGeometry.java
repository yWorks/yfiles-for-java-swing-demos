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
package complete.isometric;

import com.yworks.yfiles.graph.ITagOwner;

/**
 * Stores the isometrically transformed dimensions (width, depth, and height)
 * of a solid figure. This data is used to get its bounds in the view space and
 * the bounds of its base area used in the layout space.
 */
public class IsometricGeometry {
  private double width;
  private double depth;
  private double height;
  private boolean horizontal;

  /**
   * Initializes a new isometric geometry instance.
   */
  public IsometricGeometry() {
    this(0, 0, 0, false);
  }

  /**
   * Initializes a new isometric geometry instance.
   */
  public IsometricGeometry(double width, double depth, double height, boolean isHorizontal) {
    this.width = width;
    this.depth = depth;
    this.height = height;
    this.horizontal = isHorizontal;
  }

  /**
   * Returns the width of the solid figure.
   */
  public double getWidth() {
    return width;
  }

  /**
   * Sets the width of the solid figure.
   */
  public void setWidth( double value ) {
    this.width = value;
  }

  /**
   * Returns the depth of the solid figure.
   */
  public double getDepth() {
    return this.depth;
  }

  /**
   * Sets the depth of the solid figure.
   */
  public void setDepth( double value ) {
    this.depth = value;
  }

  /**
   * Returns the height of the solid figure.
   */
  public double getHeight() {
    return this.height;
  }

  /**
   * Sets the height of the solid figure.
   */
  public void setHeight( double value ) {
    this.height = value;
  }

  /**
   * Determines whether or not the base of the solid figure is horizontal in layout space.
   * This is important for labels that may be rotated during layout.
   */
  public boolean isHorizontal() {
    return this.horizontal;
  }

  /**
   * Specifies whether or not the base of the solid figure is horizontal in layout space.
   * This is important for labels that may be rotated during layout.
   */
  public void setHorizontal( boolean value ) {
    this.horizontal = value;
  }


  /**
   * Retrieves the {@link IsometricGeometry} instance stored as the tag of the
   * given object.
   */
  static IsometricGeometry get( ITagOwner owner ) {
    return (IsometricGeometry) owner.getTag();
  }
}
