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

import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;

/**
 * Creates a markup extension that is required for serializing label location parameters
 * created by {@link MyNodeLabelLocationModel}.
 * <p>
 * This helper class needs to be public in order to be accessible by GraphML's reflection code.
 * </p>
 */
public class MyNodeLabelLocationModelParameterConverter implements IMarkupExtensionConverter {
  @Override
  public boolean canConvert(IWriteContext context, Object value) {
    return value instanceof MyNodeLabelLocationModel.MyNodeCustomNodeLabelModelParameter;
  }

  @Override
  public MarkupExtension convert(IWriteContext iWriteContext, Object value) {
    if (value instanceof MyNodeLabelLocationModel.MyNodeCustomNodeLabelModelParameter) {
      MyNodeLabelLocationModel.MyNodeCustomNodeLabelModelParameter parameter = (MyNodeLabelLocationModel.MyNodeCustomNodeLabelModelParameter) value;
      return new MyNodeLabelLocationModelParameterExtension(parameter.getModel(), parameter.getRatio());
    }
    throw new IllegalArgumentException(value + " must be of type MyNodeLabelLocationModel.MyNodeCustomNodeLabelModelParameter");
  }
}
