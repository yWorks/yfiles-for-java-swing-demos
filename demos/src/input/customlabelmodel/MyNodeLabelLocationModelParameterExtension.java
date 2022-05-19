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
package input.customlabelmodel;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.MarkupExtension;

/**
 * Used to read and write label model parameters created by {@link MyNodeLabelLocationModel} from and to GraphML.
 * <p>
 * A markup extension is necessary in this case because the aforementioned
 * parameter implementation is immutable and does not expose public API for
 * accessing its internal state. As a result, the generic, reflection-based
 * (de-)serialization support built into yFiles' GraphML framework cannot handle
 * these parameters.
 * </p>
 * <p>
 * This helper class and its properties needs to be public in order to be accessible by GraphML's reflection code.
 * </p>
 */
public class MyNodeLabelLocationModelParameterExtension extends MarkupExtension {
  private MyNodeLabelLocationModel model;
  private double ratio;

  /**
   * A default constructor is necessary in order to be accessible by GraphML's reflection code.
   */
  public MyNodeLabelLocationModelParameterExtension() {
  }

  MyNodeLabelLocationModelParameterExtension(MyNodeLabelLocationModel model, double ratio){
    this.model = model;
    this.ratio = ratio;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public MyNodeLabelLocationModel getModel() {
    return model;
  }

  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public void setModel(MyNodeLabelLocationModel model) {
    this.model = model;
  }

  @DefaultValue(doubleValue = 0.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public double getRatio() {
    return ratio;
  }

  @DefaultValue(doubleValue = 0.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public void setRatio(double ratio) {
    this.ratio = ratio;
  }

  @Override
  public Object provideValue(ILookup lookup) {
    return (model != null ? model : new MyNodeLabelLocationModel()).createParameter(this.ratio);
  }
}
