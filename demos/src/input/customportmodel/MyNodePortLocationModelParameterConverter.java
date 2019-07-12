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
package input.customportmodel;

import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;

/**
 * Creates the markup extension that is required for writing port location parameters
 * created by {@link MyNodePortLocationModel}.
 * <p>
 * This helper class needs to be public in order to be accessible by GraphML's reflection code.
 * </p>
 */
public class MyNodePortLocationModelParameterConverter implements IMarkupExtensionConverter {

  @Override
  public boolean canConvert(IWriteContext context, Object value ) {
    return value instanceof MyNodePortLocationModel.MyNodePortLocationModelParameter;
  }

  @Override
  public MarkupExtension convert( IWriteContext context, Object value ) {
    if (value instanceof MyNodePortLocationModel.MyNodePortLocationModelParameter) {
      MyNodePortLocationModel.MyNodePortLocationModelParameter parameter = (MyNodePortLocationModel.MyNodePortLocationModelParameter) value;
      return new MyNodePortLocationModelParameterExtension((MyNodePortLocationModel)parameter.getModel(), parameter.getLocation());
    } else {
      return null;
    }
  }
}