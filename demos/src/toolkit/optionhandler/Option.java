/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.List;

/**
 * A data holder for a single configuration option.
 * <p>
 * The {@link ConfigConverter} scanns a configuration object in its {@link ConfigConverter#convert(Object)} method and
 * creates {@link Option} items for the public fields and properties of this object.
 * </p>
 */
public class Option {
  private String name;

  /**
   * The internally used name of this option.
   * @return The Name.
   * @see #setName(String)
   */
  public final String getName() {
    return this.name;
  }

  /**
   * The internally used name of this option.
   * @param value The Name to set.
   * @see #getName()
   */
  public final void setName( String value ) {
    this.name = value;
  }

  private String label;

  /**
   * The displayed label of this option.
   * @return The Label.
   * @see #setLabel(String)
   */
  public final String getLabel() {
    return this.label;
  }

  /**
   * The displayed label of this option.
   * @param value The Label to set.
   * @see #getLabel()
   */
  public final void setLabel( String value ) {
    this.label = value;
  }

  private Object defaultValue;

  /**
   * The default value this option has.
   * @return The DefaultValue.
   * @see #setDefaultValue(Object)
   */
  public final Object getDefaultValue() {
    return this.defaultValue;
  }

  /**
   * The default value this option has.
   * @param value The DefaultValue to set.
   * @see #getDefaultValue()
   */
  public final void setDefaultValue( Object value ) {
    this.defaultValue = value;
  }

  /**
   * The current value of the field or property in the scanned configuration object.
   * <p>
   * Note that this property is read from and written to the configuration object this option was created for.
   * </p>
   * @return The Value.
   * @see #setValue(Object)
   */
  public final Object getValue() {
    if (getGetter() != null) {
      return getGetter().get();
    }
    return getDefaultValue();
  }

  /**
   * The current value of the field or property in the scanned configuration object.
   * <p>
   * Note that this property is read from and written to the configuration object this option was created for.
   * </p>
   * @param value The Value to set.
   * @see #getValue()
   */
  public final void setValue( Object value ) {
    if (getSetter() != null) {
      getSetter().accept(value);
    }
  }

  private Type valueType;

  /**
   * The {@link Type} of this option.
   * @return The ValueType.
   * @see #setValueType(Type)
   */
  public final Type getValueType() {
    return this.valueType;
  }

  /**
   * The {@link Type} of this option.
   * @param value The ValueType to set.
   * @see #getValueType()
   */
  public final void setValueType( Type value ) {
    this.valueType = value;
  }

  private ComponentTypes componentType = ComponentTypes.OPTION_GROUP;

  /**
   * The type of the ui component that shall be used to represent this option.
   * @return The ComponentType.
   * @see #setComponentType(ComponentTypes)
   */
  public final ComponentTypes getComponentType() {
    return this.componentType;
  }

  /**
   * The type of the ui component that shall be used to represent this option.
   * @param value The ComponentType to set.
   * @see #getComponentType()
   */
  public final void setComponentType( ComponentTypes value ) {
    this.componentType = value;
  }

  private List<EnumValue> enumValues;

  /**
   * A list of the available enum values for this option.
   * @return The EnumValues.
   * @see #setEnumValues(List)
   */
  public final List<EnumValue> getEnumValues() {
    return this.enumValues;
  }

  /**
   * A list of the available enum values for this option.
   * @param value The EnumValues to set.
   * @see #getEnumValues()
   */
  public final void setEnumValues( List<EnumValue> value ) {
    this.enumValues = value;
  }

  private MinMax minMax;

  /**
   * The {@link MinMax} containing the minimum and maximum value for this option.
   * @return The MinMax.
   * @see #setMinMax(MinMax)
   */
  public final MinMax getMinMax() {
    return this.minMax;
  }

  /**
   * The {@link MinMax} containing the minimum and maximum value for this option.
   * @param value The MinMax to set.
   * @see #getMinMax()
   */
  public final void setMinMax( MinMax value ) {
    this.minMax = value;
  }

  private Supplier<Boolean> checkDisabled;

  /**
   * A utility method that returns whether this option should currently be disabled.
   * @return The CheckDisabled.
   * @see #setCheckDisabled(Supplier)
   */
  public final Supplier<Boolean> getCheckDisabled() {
    return this.checkDisabled;
  }

  /**
   * A utility method that returns whether this option should currently be disabled.
   * @param value The CheckDisabled to set.
   * @see #getCheckDisabled()
   */
  public final void setCheckDisabled( Supplier<Boolean> value ) {
    this.checkDisabled = value;
  }

  public final boolean isEnabled() {
    return getCheckDisabled() == null || !getCheckDisabled().get();
  }

  private Supplier<Boolean> checkHidden;

  /**
   * A utility method that returns whether this option should currently be hidden.
   * @return The CheckHidden.
   * @see #setCheckHidden(Supplier)
   */
  public final Supplier<Boolean> getCheckHidden() {
    return this.checkHidden;
  }

  /**
   * A utility method that returns whether this option should currently be hidden.
   * @param value The CheckHidden to set.
   * @see #getCheckHidden()
   */
  public final void setCheckHidden( Supplier<Boolean> value ) {
    this.checkHidden = value;
  }


  private Supplier<Object> getter;

  final Supplier<Object> getGetter() {
    return this.getter;
  }

  final void setGetter( Supplier<Object> value ) {
    this.getter = value;
  }

  private Consumer<Object> setter;

  final Consumer<Object> getSetter() {
    return this.setter;
  }

  final void setSetter( Consumer<Object> value ) {
    this.setter = value;
  }


  /**
   * Resets the {@link #getValue() Value} to the {@link #getDefaultValue() DefaultValue}.
   */
  public void reset() {
    setValue(getDefaultValue());
  }

}
