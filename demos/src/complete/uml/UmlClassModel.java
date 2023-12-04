/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.uml;

import com.yworks.yfiles.graphml.GraphML;

import java.util.ArrayList;
import java.util.List;

/**
 * The class model holds the class details like its name, attributes and operations. This part will be stored when the
 * diagram is saved.
 * <p>
 * In addition it stores the visual state of the class: which section is currently open and which list item is currently
 * selected. This part will not be (de)serialized.
 * </p>
 */
@GraphML(markupExtensionConverter = ModelExtension.class)
public class UmlClassModel implements Cloneable {

  /** Constant that is used to specify that the currently selected item belongs to the attribute list. */
  public static final int LIST_ATTRIBUTES =  0;

  /** Constant that is used to specify that the currently selected item belongs to the operation list. */
  public static final int LIST_OPERATIONS =  1;

  /** Constant that is used to notify that no list item is currently selected. */
  public static final int LIST_INDEX_NONE =  -1;

  private String className;
  private List<String> attributes;
  private List<String> operations;

  private boolean sectionsVisible;
  private boolean attributesVisible;
  private boolean operationsVisible;
  private int selectedList;
  private int selectedListIndex;

  public UmlClassModel() {
    this("Name", new ArrayList<>(), new ArrayList<>());
  }

  public UmlClassModel(String name, List<String> attributes, List<String> operations) {
    this.attributes = attributes;
    this.className = name;
    this.operations = operations;

    sectionsVisible = true;
    attributesVisible = true;
    operationsVisible = true;
    selectedList = LIST_ATTRIBUTES;
    selectedListIndex = LIST_INDEX_NONE;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }

  public List<String> getOperations() {
    return operations;
  }

  public void setOperations(List<String> operations) {
    this.operations = operations;
  }

  /**
   * Checks whether or not the sections for attributes and operations are visible or if only the name of the class is
   * visible.
   */
  public boolean areSectionsVisible() {
    return sectionsVisible;
  }

  /**
   * Specifies whether or not the sections for attributes and operations are visible or if only the name of the class is
   * visible.
   */
  public void setSectionsVisible(boolean sectionsVisible) {
    this.sectionsVisible = sectionsVisible;
  }

  /**
   * Checks whether or not the list of attributes is visible.
   */
  public boolean areAttributesVisible() {
    return attributesVisible;
  }

  /**
   * Specifies whether or not the list of attributes is visible.
   */
  public void setAttributesVisible(boolean attributesVisible) {
    this.attributesVisible = attributesVisible;
  }

  /**
   * Checks whether or not the list of attributes is visible.
   */
  public boolean areOperationsVisible() {
    return operationsVisible;
  }

  /**
   * Specifies whether or not the list of operations is visible.
   */
  public void setOperationsVisible(boolean operationsVisible) {
    this.operationsVisible = operationsVisible;
  }

  /**
   * Returns the list of the currently selected item. There are the following lists:
   * <ul>
   *   <li>{@link #LIST_ATTRIBUTES}</li>
   *   <li>{@link #LIST_OPERATIONS}</li>
   * </ul>
   */
  public int getSelectedList() {
    return selectedList;
  }

  /**
   * Specifies the list of the currently selected item. There are the following lists:
   * <ul>
   *   <li>{@link #LIST_ATTRIBUTES}</li>
   *   <li>{@link #LIST_OPERATIONS}</li>
   * </ul>
   */
  public void setSelectedList(int list) {
    switch (list) {
      case LIST_ATTRIBUTES:
      case LIST_OPERATIONS:
        selectedList = list;
        return;
      default:
        throw new IllegalArgumentException("Unknown list" + list);
    }
  }

  /**
   * Returns the list index of the currently selected list.
   */
  public int getSelectedListIndex() {
    return selectedListIndex;
  }

  /**
   * Specifies the list index of the currently selected list.
   */
  public void setSelectedListIndex(int listIndex) {
    selectedListIndex = listIndex;
  }

  public Object clone() throws CloneNotSupportedException {
    UmlClassModel clone = (UmlClassModel) super.clone();
    clone.attributes = new ArrayList<>(attributes.size());
    clone.attributes.addAll(attributes);
    clone.operations = new ArrayList<>(operations.size());
    clone.operations.addAll(operations);
    return clone;
  }
}
