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
package input.orthogonaledges;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeHelper;
import com.yworks.yfiles.view.input.SegmentOrientation;

/**
 * The {@link com.yworks.yfiles.view.input.OrthogonalEdgeHelper} for blue edges. Orthogonal edge
 * editing is enabled for the inner segments of these edges but not for the first and last one.
 */
class BlueOrthogonalEdgeHelper extends OrthogonalEdgeHelper {

  /**
   * Returns the {@link com.yworks.yfiles.view.input.SegmentOrientation#NON_ORTHOGONAL NON_ORTHOGONAL}
   * segment orientation for the first and last segment, and the default for all other segments.
   */
  @Override
  public SegmentOrientation getSegmentOrientation(IInputModeContext inputModeContext, IEdge edge, int segmentIndex) {
    return segmentIndex == 0 || segmentIndex == edge.getBends().size()
        ? SegmentOrientation.NON_ORTHOGONAL
        : super.getSegmentOrientation(inputModeContext, edge, segmentIndex);
  }
}
