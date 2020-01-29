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
package complete.bpmn.view.config;

import complete.bpmn.view.DataObjectNodeStyle;
import complete.bpmn.view.DataObjectType;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration class for {@link DataObjectNodeStyle}.
 * <p>
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer
 * applications will likely provide their own property configuration framework and won't need this part of the library.
 * </p>
 */
@Label("DataObject Node")
public class DataObjectNodeStyleConfiguration extends NodeStyleConfiguration<DataObjectNodeStyle> {

  @OptionGroupAnnotation(name = "RootGroup", position = 0)
  @Label("Data Object Type")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Input", value = "INPUT")
  @EnumValueAnnotation(label = "Output", value = "OUTPUT")
  public final DataObjectType getType() {
    return getStyleTemplate().getType();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 0)
  @Label("Data Object Type")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Input", value = "INPUT")
  @EnumValueAnnotation(label = "Output", value = "OUTPUT")
  public final void setType( DataObjectType value ) {
    getStyleTemplate().setType(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 1)
  @Label("Collection")
  public final boolean isCollection() {
    return getStyleTemplate().isCollection();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 1)
  @Label("Collection")
  public final void setCollection( boolean value ) {
    getStyleTemplate().setCollection(value);
  }

  @Override
  protected DataObjectNodeStyle createDefault() {
    return new DataObjectNodeStyle();
  }
}
