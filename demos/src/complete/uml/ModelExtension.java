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
package complete.uml;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.GraphMLMemberVisibility;
import com.yworks.yfiles.graphml.GraphMLSharingPolicy;
import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles serialization and deserialization of the {@link UmlClassModel} instances.
 */
public class ModelExtension extends MarkupExtension implements IMarkupExtensionConverter {
  String name;
  String type;
  List<String> attributes;
  List<String> operations;
  boolean sectionsVisible;
  boolean attributesVisible;
  boolean operationsVisible;


  public ModelExtension() {
    name = "";
    type = "Class";
    attributes = new ArrayList<>();
    operations = new ArrayList<>();
    sectionsVisible = true;
    attributesVisible = true;
    operationsVisible = true;
  }

  public String getName() {
    return name;
  }

  public void setName( String value ) {
    this.name = value;
  }

  public String getType() {
    return type;
  }

  public void setType( String value ) {
    this.type = value;
  }

  public boolean isSectionsVisible() {
    return sectionsVisible;
  }

  public void setSectionsVisible(boolean sectionsVisible) {
    this.sectionsVisible = sectionsVisible;
  }

  public boolean isAttributesVisible() {
    return attributesVisible;
  }

  public void setAttributesVisible(boolean attributesVisible) {
    this.attributesVisible = attributesVisible;
  }

  public boolean isOperationsVisible() {
    return operationsVisible;
  }

  public void setOperationsVisible(boolean operationsVisible) {
    this.operationsVisible = operationsVisible;
  }

  @GraphML(shareable = GraphMLSharingPolicy.NEVER, visibility = GraphMLMemberVisibility.CONTENT)
  public List<String> getAttributes() {
    return attributes;
  }

  @GraphML(shareable = GraphMLSharingPolicy.NEVER, visibility = GraphMLMemberVisibility.CONTENT)
  public void setAttributes( List<String> value ) {
    this.attributes = attributes;
  }

  @GraphML(shareable = GraphMLSharingPolicy.NEVER, visibility = GraphMLMemberVisibility.CONTENT)
  public List<String> getOperations() {
    return operations;
  }

  @GraphML(shareable = GraphMLSharingPolicy.NEVER, visibility = GraphMLMemberVisibility.CONTENT)
  public void setOperations( List<String> value ) {
    this.operations = operations;
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    UmlClassModel model = new UmlClassModel();
    model.setClassName(getName());
    model.setSectionsVisible(isSectionsVisible());
    model.setAttributesVisible(isAttributesVisible());
    model.setOperationsVisible(isOperationsVisible());
    List<String> attrs = getAttributes();
    if (attrs != null && !attrs.isEmpty()) {
      model.getAttributes().addAll(attrs);
    }
    List<String> ops = getOperations();
    if (ops != null && !ops.isEmpty()) {
      model.getOperations().addAll(ops);
    }
    return model;
  }

  /*
   * #####################################################################
   * IMarkupExtensionConverter 
   * #####################################################################
   */

  @Override
  public boolean canConvert( IWriteContext context, Object value ) {
    return value instanceof UmlClassModel;
  }

  @Override
  public MarkupExtension convert( IWriteContext context, Object value ) {
    UmlClassModel model = (UmlClassModel) value;

    ModelExtension extension = new ModelExtension();
    extension.setName(model.getClassName());
    extension.setSectionsVisible(model.areSectionsVisible());
    extension.setAttributesVisible(model.areAttributesVisible());
    extension.setOperationsVisible(model.areOperationsVisible());
    extension.getAttributes().addAll(model.getAttributes());
    extension.getOperations().addAll(model.getOperations());

    return extension;
  }
}
