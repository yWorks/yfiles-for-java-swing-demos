/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package toolkit.optionhandler;


/**
 * A data holder that represents a single enum value with a display name used for the ui component.
 */
public class EnumValue {
  private String name;

  /**
   * The display name of the enum value.
   * @return The Name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * The display name of the enum value.
   * @param value The Name to set.
   * @see #getName()
   */
  private final void setName( String value ) {
    this.name = value;
  }

  private Object value;

  /**
   * The enum value.
   * @return The Value.
   */
  public final Object getValue() {
    return this.value;
  }

  /**
   * The enum value.
   * @param value The Value to set.
   * @see #getValue()
   */
  private final void setValue( Object value ) {
    this.value = value;
  }

  /**
   * Creates a new EnumValue instance.
   * @param name The display name of the enum value.
   * @param value The enum value.
   */
  public EnumValue( String name, Object value ) {
    setName(name);
    setValue(value);
  }

}
