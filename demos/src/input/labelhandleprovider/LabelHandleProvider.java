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
package input.labelhandleprovider;

import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for label handles. Depending on the label model of the given
 * label, this class provides custom handle implementations of
 * type {@link LabelResizeHandle} and {@link LabelRotateHandle}.
 * @see IHandle
 * @see ILabel
 * @see ILabelModel
 */
public class LabelHandleProvider implements IHandleProvider {
  private final ILabel label;

  /**
   * Initializes a new {@code LabelHandleProvider} for the given label.
   */
  public LabelHandleProvider( ILabel label ) {
    this.label = label;
  }

  /**
   * Returns rotation and resize handles as appropriate for the provider's
   * associated label.
   */
  @Override
  public Iterable<IHandle> getHandles( IInputModeContext context ) {
    List<IHandle> handles = new ArrayList<>();

    ILabelModel labelModel = label.getLayoutParameter().getModel();
    if (labelModel instanceof InteriorStretchLabelModel) {
      // some label models are not resizable at all - don't provide any handles
    } else if (isRotatable(labelModel)) {
      // these models support resizing in one direction
      handles.add(new LabelResizeHandle(label, false));
      // and rotation as well
      handles.add(new LabelRotateHandle(label));
    } else {
      // for all other models, we assume the center needs to stay the same
      // this requires that the label must be resized symmetrically in both directions
      handles.add(new LabelResizeHandle(label, true));
    }

    return handles;
  }

  /**
   * Determines if the label model supports rotated labels.
   */
  private static boolean isRotatable( ILabelModel model ) {
    return model instanceof FreeNodeLabelModel ||
           model instanceof FreeEdgeLabelModel ||
           model instanceof FreeLabelModel;
  }
}
