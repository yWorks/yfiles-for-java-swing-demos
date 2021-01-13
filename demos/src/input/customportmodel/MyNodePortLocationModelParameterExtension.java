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
package input.customportmodel;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.MarkupExtension;

/**
 * Handles reading and writing port location parameters created by
 * {@link MyNodePortLocationModel} from and to GraphML.
 * <p>
 * A markup extension is necessary in this case because the aforementioned
 * parameter implementation is immutable and does not expose public API for
 * accessing its internal state. As a result, the generic, reflection-based
 * (de-)serialization support built into yFiles' GraphML framework cannot handle
 * these parameters.
 * </p>
 * <p>
 * This helper class needs to be public in order to be accessible by GraphML's reflection code
 * </p>
 */
public class MyNodePortLocationModelParameterExtension extends MarkupExtension {
  private MyNodePortLocationModel model;
  private PortLocation location = PortLocation.CENTER;

  public MyNodePortLocationModelParameterExtension() {
  }

  public MyNodePortLocationModelParameterExtension(MyNodePortLocationModel model, PortLocation location) {
    this.model = model;
    this.location = location;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public MyNodePortLocationModel getModel() {
    return model;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public void setModel(MyNodePortLocationModel value) {
    model = value;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortLocation.class, stringValue = "CENTER")
  public PortLocation getLocation() {
    return location;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortLocation.class, stringValue = "CENTER")
  public void setLocation(PortLocation value) {
    location = value;
  }

  @Override
  public Object provideValue(ILookup lookup) {
    return (model != null ? model : new MyNodePortLocationModel()).createParameter(location);
  }
}
