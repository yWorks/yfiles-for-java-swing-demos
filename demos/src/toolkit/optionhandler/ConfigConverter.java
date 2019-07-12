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

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.PropertyInfo;
import com.yworks.yfiles.utils.FlagsEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ConfigConverter is used to convert an input configuration into nested {@link Option}s usable by an option editor.
 * <p>
 * The output configuration {@link Option} is later interpreted by an option editor and turned into ui components. The
 * input configuration is a regular class with additional attributes attached to public member declarations. These
 * attributes specify how a member is turned into an ui component. The input configuration format Components: - a default {@link Option}
 * is created for each public property and field according to the property/field's type. - components are available for
 * built-in types: enum, bool, int, double, string Component labels: - component labels are defined using the {@link Label}
 * Component minimum, maximum and step: - component minimum, maximum and step are defined using the {@link MinMax}
 * Component type: - component type is one of {@link ComponentTypes} and defined using the {@link ComponentType} Component
 * grouping and ordering: - public fields and properties with an {@link ComponentType} with value {@link ComponentTypes#OPTION_GROUP}
 * are interpreted as ui groups - {@link Option} or {@link OptionGroup}s are assigned to another {@link OptionGroup} using
 * the {@link OptionGroupAnnotation} - if a members {@link OptionGroupAnnotation} has name "RootGroup" or there is no {@link OptionGroupAnnotation}
 * the corresponding component is added to the toplevel group. - display order of components is defined through position
 * parameter of {@link OptionGroupAnnotation} Conditionally disabling a component: - a public boolean property named
 * "[memberName]Disabled" defines a condition to disable the component created for the corresponding member.
 * </p>
 */
public class ConfigConverter {
  // all members that need to be added to the top-level OptionGroup - including groups
  private ArrayList<Member> toplevelItems = new ArrayList<Member>();

  // the methods - methods aren't used right now
  private ArrayList<Method> methods = new ArrayList<Method>();

  // map a group name to a list of its inner members
  private HashMap<String, ArrayList<Member>> groupMapping = new HashMap<String, ArrayList<Member>>();

  // map for the ShouldDisable functions for properties
  private HashMap<String, Supplier<Boolean>> isDisabledMapping = new HashMap<String, Supplier<Boolean>>();

  // map for the ShouldHide functions for properties
  private HashMap<String, Supplier<Boolean>> isHiddenMapping = new HashMap<String, Supplier<Boolean>>();

  /**
   * Convert the input configuration into an {@link OptionGroup} usable by an option editor.
   * <p>
   * First, all members are *collected* and stored in their respective maps and lists. Next, members are sorted by the
   * position information contained in the corresponding {@link OptionGroupAnnotation}. Finally, all members are visited and
   * the extracted data relevant to build ui components are written to an {@link Option}.
   * </p>
   * @param config The input configuration written by the developer.
   * @return The output configuration usable by an option editor.
   */
  public final OptionGroup convert( Object config ) {
    Class type = config.getClass();

    // collect members
    collectMembers(type, config);

    // sort members
    Collections.sort(toplevelItems, new MemberComparator());
    for (Map.Entry<String, ArrayList<Member>> entry : groupMapping.entrySet()) {
      Collections.sort(entry.getValue(), new MemberComparator());
    }

    // visit members
    OptionGroup topLevelGroup = new OptionGroup();
    setLabel(type, topLevelGroup);
    visitMembers(topLevelGroup, config);
    return topLevelGroup;
  }

  /**
   * Write member information into {@link Option}s.
   * @param optionGroup The OptionGroup to contain the configuration.
   * @param config The input configuration.
   */
  private void visitMembers( OptionGroup optionGroup, Object config ) {
    for (Member member : toplevelItems) {
      visitMember(member, optionGroup, config);
    }
    for (Method method : methods) {
      visitMethod(method, optionGroup, config);
    }
  }

  /**
   * Collect all public members of a given type and store them in the respective field.
   * @param type The type object to collect members for.
   * @param config The input configuration.
   */
  private void collectMembers( Class type, Object config ) {
    clearMappings();
    List<Field> fieldList = Arrays.stream(type.getDeclaredFields())
        .filter(field -> {
          int modifiers = field.getModifiers();
          return ((modifiers & Modifier.STATIC) == 0) && ((modifiers & Modifier.PUBLIC) != 0);
        }).collect(Collectors.toList());
    List<PropertyInfo> publicPropertyList = getProperties(type);
    List<Method> methodList = Arrays.stream(type.getDeclaredMethods())
        .filter(method -> {
          int modifiers = method.getModifiers();
          return ((modifiers & Modifier.STATIC) == 0) && ((modifiers & Modifier.PUBLIC) != 0);
        }).collect(Collectors.toList());

    for (Field member : fieldList) {
      collectMember(member, config);
    }
    for (PropertyInfo propertyInfo : publicPropertyList) {
      collectMember(propertyInfo, config);
    }
    for (Method method : methodList) {
      collectMember(method, config);
    }
  }

  /**
   * Clear members from dictionaries and lists.
   */
  private void clearMappings() {
    groupMapping.clear();
    toplevelItems.clear();
    methods.clear();
    isDisabledMapping.clear();
    isHiddenMapping.clear();
  }

  /**
   * Collect a member.
   * <p>
   * The function dispatches between different collect functions based on type of member.
   * </p>
   * @param member The member to be processed.
   * @param config The input configuration.
   */
  private void collectMember( Member member, Object config ) {
    if (collectUtilityProperty(member, config)) {
      return;
    }
    if (member instanceof Field) {
      collectField((Field)member);
    } else if (member instanceof PropertyInfo) {
      collectProperty((PropertyInfo)member);
    } else if (member instanceof Method) {
      collectMethod((Method)member);
    }
  }

  /**
   * Collect an utility property that disables a control.
   * @param member The member to be processed.
   * @param config The input configuration.
   * @return {@code true} if property is an utility property
   */
  private boolean collectUtilityProperty( Member member, Object config ) {
    if (endsWith(member.getName(), "Hidden")) {
      Supplier<Boolean> invoker = getInvoker(member, config);
      if (invoker != null) {
        isHiddenMapping.put(member.getName().substring(0, member.getName().length() - 6).toLowerCase(), invoker);
        return true;
      }
    }
    if (endsWith(member.getName(), "Disabled")) {
      Supplier<Boolean> invoker = getInvoker(member, config);
      if (invoker != null) {
        isDisabledMapping.put(member.getName().substring(0, member.getName().length() - 8).toLowerCase(), invoker);
        return true;
      }
    }
    return false;
  }

  private static boolean endsWith( String text, String pattern ) {
    int textLength = text.length();
    int patternLength = pattern.length();
    return textLength > patternLength && text.substring(textLength - patternLength, textLength).equals(pattern);
  }

  private Supplier<Boolean> getInvoker( Member member, final Object config ) {
    final Field fieldInfo = (member instanceof Field) ? (Field)member : null;
    if (fieldInfo != null) {
      if (Boolean.TYPE.equals(fieldInfo.getGenericType())) {
        return (new Supplier<Boolean>(){
          public Boolean get() {
            try {
              return (Boolean)fieldInfo.get(config);
            } catch (IllegalAccessException e) {
              return false;
            }
          }
        });
      }
    }
    final PropertyInfo propertyInfo = (member instanceof PropertyInfo) ? (PropertyInfo)member : null;
    if (propertyInfo != null) {
      if (Boolean.TYPE.equals(propertyInfo.getType())) {
        return (new Supplier<Boolean>(){
          public Boolean get() {
            try {
              return (Boolean)propertyInfo.getValue(config);
            } catch (InvocationTargetException e) {
              return false;
            } catch (IllegalAccessException e) {
              return false;
            }
          }
        });
      }
    }
    final Method methodInfo = (member instanceof Method) ? (Method)member : null;
    if (methodInfo != null) {
      if (Boolean.TYPE.equals(methodInfo.getReturnType())) {
        return (new Supplier<Boolean>(){
          public Boolean get() {
            try {
              return (Boolean)methodInfo.invoke(config);
            } catch (IllegalAccessException e) {
              return false;
            } catch (InvocationTargetException e) {
              return false;
            }
          }
        });
      }
    }

    return null;
  }

  /**
   * Collect a field.
   * <p>
   * If the field has an {@link OptionGroupAnnotation}, add it to the corresponding {@link #groupMapping}, otherwise add it
   * to {@link #toplevelItems}.
   * </p>
   * @param field The field to process.
   */
  private void collectField( Field field ) {
    String group = getGroup(field);
    if (group != null && !"rootgroup".equals(group)) {
      if (!groupMapping.containsKey(group)) {
        groupMapping.put(group, new ArrayList<Member>());
      }
      groupMapping.get(group).add(field);
    } else {
      toplevelItems.add(field);
    }
  }

  /**
   * Collect a property.
   * <p>
   * If the property has an {@link OptionGroupAnnotation}, add it to the corresponding {@link #groupMapping}, otherwise add
   * it to {@link #toplevelItems}.
   * </p>
   * @param property The property to process.
   */
  private void collectProperty( PropertyInfo property ) {
    String group = getGroup(property);
    if (group != null && !"rootgroup".equals(group)) {
      if (!groupMapping.containsKey(group)) {
        groupMapping.put(group, new ArrayList<Member>());
      }
      groupMapping.get(group).add(property);
    } else {
      toplevelItems.add(property);
    }
  }

  /**
   * Collect a method.
   * <p>
   * The method is added to the {@link #methods} list.
   * </p>
   * @param method The method to process.
   */
  private void collectMethod( Method method ) {
    if (!"getClass".equals(method.getName())) {
      methods.add(method);
    }
  }

  /**
   * Visit a member.
   * <p>
   * The function dispatches between different visit functions based on type of the member.
   * </p>
   * @param member The member to visit.
   * @param optionGroup The output data object.
   * @param config The input configuration.
   */
  private void visitMember( Member member, OptionGroup optionGroup, Object config ) {
    if (shouldIgnoreMember(member)) {
      return;
    }
    if (isOptionGroup(member)) {
      optionGroup.getChildOptions().add(visitGroup(member, config));
    } else {
      if (member instanceof Field) {
        optionGroup.getChildOptions().add(visitField((Field)member, config));
      } else if (member instanceof PropertyInfo) {
        optionGroup.getChildOptions().add(visitProperty((PropertyInfo)member, config));
      }
    }
  }

  /**
   * Visit a field and return an {@link Option} with all relevant information to build a component.
   * <p>
   * The following information is written: - the internally used name - the value type of the field - the text label - the
   * options if it has
   * {@link EnumValueAnnotation}s - the initial value - the default value - the component type - the utility properties
   * (condition to disable the component) - all custom attributes
   * </p>
   * @param field The field to visit.
   * @param config The input configuration.
   * @return a new {@link Option} containing all information collected for the field
   */
  private Option visitField( final Field field, final Object config ) {
    Option f = new Option();

    f.setName(field.getName());
    f.setValueType(field.getGenericType());

    setLabel(field, f);
    setEnumValues(field, field.getGenericType(), f);

    f.setGetter(new Supplier<Object>(){
      public Object get() {
        try {
          return field.get(config);
        } catch (IllegalAccessException e) {
          return null;
        }
      }
    });
    f.setSetter(new Consumer<Object>(){
      public void accept( Object value ) {
        try {
          field.set(config, value);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    });
    Annotation defaultValueAnnotation = getCustomAnnotation(field, DefaultValue.class);
    if (defaultValueAnnotation != null) {
      Object defaultValue = getDefaultValueFromAnnotation(((DefaultValue) defaultValueAnnotation));
      f.setDefaultValue(defaultValue);
    } else {
      f.setDefaultValue(f.getValue());
    }

    setComponent(field, field.getGenericType(), f);
    setUtilityProperties(field, f);
    for (Annotation attribute : field.getDeclaredAnnotations()) {
      visitAttribute(attribute, f);
    }
    return f;
  }

  /**
   * Visit a property and return an {@link Option} with all relevant information to build a component.
   * <p>
   * The following information is written: - the internally used name - the value type of the field - the text label - the
   * options if it has
   * {@link EnumValueAnnotation}s - the initial value - the default value - the component type - the utility properties
   * (condition to disable the component) - all custom attributes
   * </p>
   * @param property The property to visit.
   * @param config The input configuration.
   * @return a new {@link Option} containing all information collected for the property
   */
  private Option visitProperty( final PropertyInfo property, final Object config ) {
    Option p = new Option();

    p.setName(property.getName());
    p.setValueType(property.getType());

    setLabel(property, p);
    setEnumValues(property, property.getType(), p);

    p.setGetter(new Supplier<Object>(){
      public Object get() {
        try {
          return property.getValue(config);
        } catch (InvocationTargetException e) {
          return null;
        } catch (IllegalAccessException e) {
          return null;
        }
      }
    });
    p.setSetter(new Consumer<Object>(){
      public void accept( Object value ) {
        try {
          property.setValue(config, value);
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("The value " + value + " can't be assigned to the property " + property.getName() + " of type " + property.getType());
        }
      }
    });

    Annotation defaultValueAnnotation = getCustomAnnotation(property, DefaultValue.class);
    if (defaultValueAnnotation != null) {
      Object defaultValue = getDefaultValueFromAnnotation(((DefaultValue) defaultValueAnnotation));
      p.setDefaultValue(defaultValue);
    } else {
      p.setDefaultValue(p.getValue());
    }

    setComponent(property, property.getType(), p);
    setUtilityProperties(property, p);
    for (Annotation attribute : property.getDeclaredAnnotations()) {
      visitAttribute(attribute, p);
    }
    return p;
  }

  private static void visitMethod( Method method, OptionGroup optionGroup, Object config ) {
    // methods aren't used right now
  }

  /**
   * Visit an attribute and set all relevant information on {@code obj} to build a component.
   * <p>
   * The following information is written: - minimum value - maximum value - increment/decrement step
   * </p>
   * @param attribute The attribute to process.
   * @param obj The object containing the extracted information
   */
  private void visitAttribute( Annotation attribute, Option obj ) {
    if (attribute instanceof MinMax) {
      obj.setMinMax((MinMax)attribute);
    }
  }

  /**
   * Visit a group and return an {@link OptionGroup} with all relevant information to build a component.
   * <p>
   * The following information is written: - group name - text label - custom attributes
   * </p>
   * <p>
   * If the {@link #groupMapping} contains child members of this groups, these are {@link #visitMember(Member, OptionGroup, Object) visited}
   * as well.
   * </p>
   * @param groupMember The group to process.
   * @param config The input configuration.
   */
  private OptionGroup visitGroup( Member groupMember, Object config ) {
    OptionGroup g = new OptionGroup();
    g.setName(groupMember.getName());
    g.setComponentType(ComponentTypes.OPTION_GROUP);
    setLabel(groupMember, g);
    if (groupMember instanceof AnnotatedElement) {
      Annotation[] attrs = ((AnnotatedElement) groupMember).getDeclaredAnnotations();
      for (Annotation attribute : attrs) {
        visitAttribute(attribute, g);
      }
    }
    String name = g.getName().toLowerCase();
    if (groupMapping.containsKey(name)) {
      ArrayList<Member> memberInfos = groupMapping.get(name);
      for (Member memberInfo : memberInfos) {
        visitMember(memberInfo, g, config);
      }
    }
    return g;
  }

  /**
   * Sets the component type to {@code obj}.
   * <p>
   * If a {@link ComponentType} is set, the given component type is set. If an {@link EnumValueAnnotation} is present {@link ComponentTypes#COMBOBOX}
   * is set. Otherwise a default component is determined based on {@code type}.
   * </p>
   * @param member The member to process.
   * @param type The type of the member.
   * @param obj The {@link Option} containing the extracted information.
   */
  private void setComponent( Member member, Type type, Option obj ) {
    ComponentType componentTypeAnnotation = null;
    EnumValueAnnotation[] enumValueAnnotations = null;
    if (member instanceof Field) {
      componentTypeAnnotation = ((Field) member).getAnnotation(ComponentType.class);
      enumValueAnnotations = ((Field) member).getAnnotationsByType(EnumValueAnnotation.class);
    } else if (member instanceof PropertyInfo) {
      componentTypeAnnotation = ((PropertyInfo) member).getAnnotation(ComponentType.class);
      enumValueAnnotations = ((PropertyInfo) member).getAnnotationsByType(EnumValueAnnotation.class);
    }
    if (componentTypeAnnotation != null) {
      obj.setComponentType(componentTypeAnnotation.value());
    } else if (enumValueAnnotations != null && enumValueAnnotations.length > 0) {
      // use combobox for members with EnumValues attribute
      obj.setComponentType(ComponentTypes.COMBOBOX);
    } else {
      obj.setComponentType(getDefaultComponent(type));
    }
  }

  /**
   * Sets the label text on {@code obj}.
   * @param member The member to process.
   * @param obj The {@link Option} containing the extracted information.
   */
  private void setLabel( Member member, Option obj ) {
    if (member instanceof AnnotatedElement) {
      Annotation labelAnnotation = ((AnnotatedElement) member).getDeclaredAnnotation(Label.class);
      String label;
      if (labelAnnotation != null) {
        Label attr = (Label)labelAnnotation;
        label = attr.value();
      } else {
        label = member.getName();
      }
      obj.setLabel(label);
    }
  }

  /**
   * Sets the {@link Option#getEnumValues() EnumValues} to {@code obj}.
   * <p>
   * If an {@link EnumValueAnnotation} is set, options are extracted from it. If the member is of type enum, options are
   * extracted from the enum.
   * </p>
   * @param member The member to process.
   * @param type The type of the member.
   * @param obj The {@link Option} containing the extracted information.
   */
  private <T extends Enum<T>> void setEnumValues(Member member, Type type, Option obj ) {
    if (!(type instanceof Class)) {
      return;
    }
    boolean isEnum = ((Class) type).isEnum();
    boolean isFlagsEnum = FlagsEnum.class.isAssignableFrom((Class) type);

    Annotation[] attributes = (member instanceof AnnotatedElement) ? ((AnnotatedElement) member).getDeclaredAnnotationsByType(EnumValueAnnotation.class) : null;
    if (attributes != null && attributes.length > 0) {
      ArrayList<EnumValue> enumValues = new ArrayList<EnumValue>();
      for (Object attribute : attributes) {
        EnumValueAnnotation enumValueAnnotation = ((EnumValueAnnotation)attribute);

        String value = enumValueAnnotation.value();
        Object enumValue;
        if (isEnum) {
          Class<T> enumClass = (Class<T>) type;
          enumValue = Enum.valueOf(enumClass, value);
        } else {
          if (isFlagsEnum){
            try {
              enumValue = ((Class)type).getMethod("fromName", String.class).invoke(null, value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
              throw new IllegalArgumentException("Cannot parse "+ value + " of type " + type);
            }
          } else {
            if (Double.TYPE.equals(type)) {
              enumValue = Double.parseDouble(value);
            } else {
              throw new IllegalArgumentException("No enum values for type " + type + " supported!");
            }
          }
        }
        enumValues.add(new EnumValue(enumValueAnnotation.label(), enumValue));
      }
      obj.setEnumValues(enumValues);
    } else if (isEnum) {
      Class<T> enumClass = (Class<T>) type;
      ArrayList<EnumValue> enumValues = new ArrayList<EnumValue>();
      T[] values = enumClass.getEnumConstants();
      for (T value : values) {
        enumValues.add(new EnumValue(value.toString(), value));
      }
      obj.setEnumValues(enumValues);
    } else if (isFlagsEnum) {
      List<EnumValue> enumValues = Arrays.stream(((Class) type).getDeclaredFields())
          .filter((field) -> {
            int modifiers = field.getModifiers();
            return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && field.getGenericType().equals(type);
          }).map((field) -> {
            Object enumConstant = null;
            try {
              enumConstant = field.get(null);
            } catch (IllegalAccessException e) {
              throw new IllegalStateException("Can't determine values of type " + type);
            }
            EnumValue enumValue = new EnumValue(field.getName(), enumConstant);
            return enumValue;})
          .collect(Collectors.toList());

      obj.setEnumValues(enumValues);
    }
  }

  /**
   * Set utility properties on {@code obj}.
   * @param member The member to process.
   * @param obj The {@link Option} containing the extracted information.
   */
  private void setUtilityProperties( Member member, Option obj ) {
    String name = member.getName().toLowerCase();
    if (isDisabledMapping.containsKey(name)) {
      obj.setCheckDisabled(isDisabledMapping.get(name));
    }
    if (isHiddenMapping.containsKey(name)) {
      obj.setCheckHidden(isHiddenMapping.get(name));
    }
  }

  private static boolean shouldIgnoreMember( Member member ) {
    return endsWith(member.getName(), "Disabled");
  }

  private static boolean isOptionGroup( Member groupMember ) {
    if (!(groupMember instanceof AnnotatedElement)) {
      return false;
    }
    Annotation componentTypeAttributes = ((AnnotatedElement) groupMember).getDeclaredAnnotation(ComponentType.class);
    if (componentTypeAttributes != null) {
      return ((ComponentType)componentTypeAttributes).value() == ComponentTypes.OPTION_GROUP;
    }
    return false;
  }

  private static String getGroup( Member member ) {
    if (!(member instanceof AnnotatedElement)) {
      return null;
    }
    Annotation optionGroupAnnotation = ((AnnotatedElement) member).getDeclaredAnnotation(OptionGroupAnnotation.class);
    if (optionGroupAnnotation != null) {
      return ((OptionGroupAnnotation)optionGroupAnnotation).name().toLowerCase();
    }
    return null;
  }

  private static ComponentTypes getDefaultComponent( Type type ) {
    if (type instanceof Class && ((Class)type).isEnum() || type instanceof FlagsEnum) {
      return ComponentTypes.COMBOBOX;
    } else if (Boolean.TYPE.equals(type)) {
      return ComponentTypes.CHECKBOX;
    } else if (Integer.TYPE.equals(type)) {
      return ComponentTypes.SPINNER;
    }
    return ComponentTypes.TEXT;
  }

  /**
   * {@link Comparator} that compares members position information contained in {@link OptionGroupAnnotation}.
   */
  public static class MemberComparator implements Comparator<Member> {
    public final int compare( Member x, Member y ) {
      int posX = 0, posY = 0;
      Annotation attributesX = getCustomAnnotation(x, OptionGroupAnnotation.class);
      if (attributesX != null) {
        posX = ((OptionGroupAnnotation)attributesX).position();
      }
      Annotation attributesY = getCustomAnnotation(y, OptionGroupAnnotation.class);
      if (attributesY != null) {
        posY = ((OptionGroupAnnotation)attributesY).position();
      }
      if (posX > posY) {
        return 1;
      } else if (posX < posY) {
        return -1;
      }
      return 0;
    }

  }

  /**
   * Set the label text on the Option.
   * @param classobject The member to process.
   * @param option The Option containing the extracted information.
   */
  private void setLabel( Class classobject, Option option ) {
    Annotation labelAnnotation = classobject.getDeclaredAnnotation(Label.class);
    String label;
    if (labelAnnotation != null) {
      Label attr = (Label)labelAnnotation;
      label = attr.value();
    } else {
      label = classobject.getName();
    }
    option.setLabel(label);
  }

  public static List<PropertyInfo> getProperties(final Class aClass) {
    List<PropertyInfo> result = new ArrayList<PropertyInfo>();
    Method[] methods = aClass.getDeclaredMethods();
    List<Method> methodList = Arrays.stream(aClass.getDeclaredMethods()).filter(method1 -> (method1.getModifiers() & Modifier.STATIC) == 0).collect(Collectors.toList());


    HashMap<String, Method> getterCache = new HashMap<String, Method>();
    HashMap<String, Method> setterCache = new HashMap<String, Method>();

    for (Method method : methods) {
      if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
        continue;
      }
      final String name = method.getName();
      String propertyName;
      if (name.startsWith("get") && name.length() > 3 && method.getParameters().length == 0){
        getterCache.put(propertyName = name.substring(3), method);
      } else if (name.startsWith("is") && name.length() > 2 && method.getReturnType().equals(Boolean.TYPE) && method.getParameters().length == 0){
        getterCache.put(propertyName = name.substring(2), method);
      } else if (name.startsWith("set") && name.length() > 3 && Void.TYPE.equals(method.getReturnType())){
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 1){
          propertyName = name.substring(3);
          setterCache.put(propertyName, method);
        }
      }
    }

    for (Map.Entry<String, Method> getterEntry : getterCache.entrySet()) {
      final Method setterInfo = setterCache.get(getterEntry.getKey());
      if (setterInfo != null && setterInfo.getParameters()[0].getType().equals(getterEntry.getValue().getReturnType())){
        result.add(new PropertyInfo(getterEntry.getKey(), Modifier.PUBLIC, getterEntry.getValue().getDeclaringClass(), getterEntry.getValue(), setterInfo));
      } else {
        result.add(new PropertyInfo(getterEntry.getKey(), Modifier.PUBLIC, getterEntry.getValue().getDeclaringClass(), getterEntry.getValue(), null));
      }
    }

    return result;
  }

  private static Annotation getCustomAnnotation(Member member, Class annotationClass) {
    if (!(member instanceof AnnotatedElement)) {
      return null;
    }
    return ((AnnotatedElement) member).getDeclaredAnnotation(annotationClass);
  }

  private static Object getDefaultValueFromAnnotation(final DefaultValue inst) {    {
      switch (inst.valueType()) {
        case INT_TYPE:
          return inst.intValue();
        case BOOLEAN_TYPE:
          return inst.booleanValue();
        case STRING_TYPE:
          return inst.stringValue();
        case CLASS_TYPE:
          return inst.classValue();
        case DOUBLE_TYPE:
          return inst.doubleValue();
        case CHAR_TYPE:
          return inst.charValue();
        case BYTE_TYPE:
          return inst.byteValue();
        case LONG_TYPE:
          return inst.longValue();
        case FLOAT_TYPE:
          return inst.floatValue();
        case NULL:
          return null;
        case ENUM_TYPE:
          if (inst.classValue().isEnum()) {
            return Enum.valueOf((Class<? extends Enum>)inst.classValue(), inst.stringValue());
          }
          if (FlagsEnum.class.isAssignableFrom(inst.classValue())) {
            try {
              return inst.classValue().getMethod("fromName", String.class).invoke(null, inst.stringValue());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            }
          }
          throw new IllegalStateException();
        default:
          throw new IllegalStateException();
      }
    }
  }
}
