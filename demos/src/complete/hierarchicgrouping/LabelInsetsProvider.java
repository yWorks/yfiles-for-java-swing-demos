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
package complete.hierarchicgrouping;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.view.input.INodeInsetsProvider;

/**
 * Provides insets for node instances via the insets suggested by the labels.
 */
public class LabelInsetsProvider implements INodeInsetsProvider {

  private InsetsD outerInsets;

  /**
   * Initializes a new <code>LabelInsetsProvider</code> instance with empty outer insets.
   */
  public LabelInsetsProvider() {
    this(InsetsD.EMPTY);
  }

  /**
   * Initializes a new <code>LabelInsetsProvider</code> instance with the given outer insets.
   * @param outerInsets the outer insets to consider
   */
  public LabelInsetsProvider(InsetsD outerInsets) {
    this.outerInsets = outerInsets;
  }

  @Override
  public InsetsD getInsets(INode item) {
    InsetsD result = InsetsD.EMPTY;
    for (ILabel label : item.getLabels()) {
      INodeInsetsProvider provider = label.getLayoutParameter().getModel().lookup(INodeInsetsProvider.class);
      if (provider != null) {
        InsetsD insets = provider.getInsets(item);
        result = result.createUnion(insets);
      }
    }
    // add outer insets for labels and a small gap to the group bounds
    return result.createUnion(outerInsets).getEnlarged(new InsetsD(5));
  }
}
