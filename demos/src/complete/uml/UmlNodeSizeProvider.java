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
package complete.uml;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;

/**
 * Restricts the minimum size of UML nodes to ensure all attributes and
 * operations are visible depending on the corresponding properties of
 * the UML node's model data.
 */
public class UmlNodeSizeProvider implements INodeSizeConstraintProvider {
  /**
   * Calculates the size required to display all attributes and operations.
   */
  @Override
  public SizeD getMinimumSize(INode context ) {
    IRectangle nl = context.getLayout();
    double minY = nl.getY();
    double minX = nl.getX();

    double maxY = getMaxYOfLabel(UmlClassLabelSupport.getNameLabel(context));
    double maxX = getNameLabelMaxX(context);
    UmlClassModel model = UmlClassLabelSupport.getModel(context);
    if (model.areSectionsVisible()) {
      maxX = Math.max(maxX, getCaptionLabelMaxX(UmlClassLabelSupport.getAttributeCaptionLabel(context)));
      maxX = Math.max(maxX, getCaptionLabelMaxX(UmlClassLabelSupport.getOperationCaptionLabel(context)));
      for (ILabel label : context.getLabels()) {
        maxX = Math.max(maxX, getMaxXOfLabel(label));
        maxY = Math.max(maxY, getMaxYOfLabel(label));
      }
    }
    double INSET_RIGHT = 10;
    double INSET_BOTTOM = model.areSectionsVisible() ? 5 : 0;
    return new SizeD(maxX - minX + INSET_RIGHT, maxY - minY + INSET_BOTTOM);
  }

  @Override
  public SizeD getMaximumSize( INode node ) {
    return SizeD.INFINITE;
  }

  @Override
  public RectD getMinimumEnclosedArea(INode node ) {
    return RectD.EMPTY;
  }


  /**
   * Returns the maximum x-coordinate of the given label.
   */
  private static double getMaxXOfLabel( ILabel label ) {
    IOrientedRectangle ll = label.getLayout();
    return ll.getAnchorX() + ll.getWidth();
  }

  /**
   * Returns the maximum y-coordinate of the given label.
   */
  private static double getMaxYOfLabel( ILabel label ) {
    IOrientedRectangle ll = label.getLayout();
    return ll.getAnchorY();
  }

  /**
   * Returns the maximum x-coordinate of the name label including the open/close button.
   */
  private static double getNameLabelMaxX( INode context ) {
    ILabel label = UmlClassLabelSupport.getNameLabel(context);
    IOrientedRectangle ll = label.getLayout();
    return ll.getAnchorX() + ll.getWidth() +
           UmlNodeStyle.DETAILS_ICON_SIZE + UmlNodeStyle.DETAILS_ICON_GAP;
  }

  /**
   * Returns the maximum x-coordinate of the given caption label including the add/remove button.
   */
  private double getCaptionLabelMaxX( ILabel label ) {
    return getMaxXOfLabel(label) +
           2 * (UmlNodeStyle.SECTION_ICON_SIZE + UmlNodeStyle.SECTION_ICON_GAP);
  }
}
