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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;

/**
 * Places node labels of activity nodes between the end of lead time and the
 * start of follow-up time.
 */
public class ActivityLabelModel implements ILabelModel {
  /**
   * Places labels between the end of lead time and the start of follow-up time
   * of an activity node.
   */
  public static final ILabelModelParameter DEFAULT =
    new ActivityParameter(new ActivityLabelModel());

  /**
   * Stretches the given label from the end of lead time to the start of
   * follow-up time of an activity node. 
   */
  @Override
  public IOrientedRectangle getGeometry(
    ILabel label, ILabelModelParameter layoutParameter
  ) {
    if (!(layoutParameter instanceof ActivityParameter)) {
      throw new IllegalArgumentException("Invalid parameter: " + layoutParameter);
    }

    ILabelOwner owner = label.getOwner();
    if (!(owner instanceof INode)) {
      throw new IllegalArgumentException("Invalid label owner: " + owner);
    }

    INode node = (INode) owner;
    Object tag = node.getTag();
    IRectangle nl = node.getLayout();
    double x = nl.getX();
    double w = nl.getWidth();
    if (tag instanceof Activity) {
      Activity activity = (Activity) tag;
      double leadTime = activity.leadTimeWidth();
      double followUpTime = activity.followUpTimeWidth();
      x += leadTime;
      w = Math.max(0, w - leadTime - followUpTime);
    }
    return new OrientedRectangle(x, nl.getMaxY(), w, nl.getHeight());
  }

  /**
   * Returns {@link #DEFAULT}.
   * @return {@link #DEFAULT}.
   */
  @Override
  public ILabelModelParameter createDefaultParameter() {
    return DEFAULT;
  }

  /**
   * Returns an empty context.
   * @return an empty context.
   */
  @Override
  public ILookup getContext( ILabel label, ILabelModelParameter layoutParameter ) {
    return ILookup.EMPTY;
  }

  /**
   * Returns {@code null} for all types.
   * @return {@code null}.
   */
  @Override
  public <TLookup> TLookup lookup( Class<TLookup> type ) {
    return null;
  }


  /**
   * Singleton parameter used for activity node labels.
   */
  private static class ActivityParameter implements ILabelModelParameter {
    private final ActivityLabelModel model;

    ActivityParameter( ActivityLabelModel model ) {
      this.model = model;
    }

    @Override
    public ILabelModel getModel() {
      return model;
    }

    @Override
    public boolean supports( ILabel label ) {
      return label.getOwner() instanceof INode;
    }

    @Override
    public ActivityParameter clone() {
      return this;
    }
  }
}
