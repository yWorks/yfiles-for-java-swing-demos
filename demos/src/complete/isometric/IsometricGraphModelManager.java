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
package complete.isometric;

import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphModelManager;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.ItemModelManager;

import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.function.Function;

/**
 * A {@link GraphModelManager} that uses a sensible render order for its nodes in an isometric view.
 * The manager ensures that the 3D-nodes overlap other nodes that are farther away.
 */
public class IsometricGraphModelManager extends GraphModelManager {

  private final IsometricComparator comparator;

  public IsometricGraphModelManager(CanvasComponent canvas, ICanvasObjectGroup contentGroup) {
    super(canvas, contentGroup);
    comparator = new IsometricComparator(canvas);
    setProvideUserObjectOnMainCanvasObject(true);
  }

  @Override
  protected ItemModelManager<INode> createNodeModelManager(ICanvasObjectDescriptor descriptor, Function<INode, ICanvasObjectGroup> callback) {
    ItemModelManager<INode> nodeModelManager = super.createNodeModelManager(descriptor, callback);
    nodeModelManager.setComparator(comparator);
    return nodeModelManager;

  }

  /**
   * Called when the projection changes and the node overlaps can change as a result.
   */
  public void update() {
    comparator.update();
    if (getGraph() != null) {
      getGraph().getNodes().forEach(this::updateDescriptor);
    }
  }

  private static class IsometricComparator implements Comparator<INode> {

    private final CanvasComponent canvasComponent;
    private AffineTransform projection;

    private boolean leftFaceVisible;
    private boolean backFaceVisible;

    public IsometricComparator(CanvasComponent canvasComponent) {
      this.canvasComponent = canvasComponent;
      update();
    }

    /**
     * Updates which faces are visible and therefore which corners should be used for the z-order comparison.
     * This method has to be called when the {@link CanvasComponent}'s projection has changed.
     */
    private void update() {
      AffineTransform projection = canvasComponent.getProjection();
      if (!projection.equals(this.projection)) {
        this.projection = projection;
        PointD upVector = IsometricNodeStyle.calculateHeightVector(this.projection);
        leftFaceVisible = upVector.getX() > 0;
        backFaceVisible = upVector.getY() > 0;
      }
    }


    @Override
    public int compare(INode x, INode y) {
      PointD xViewCenter = PointD.ORIGIN;
      PointD yViewCenter = PointD.ORIGIN;
      PointD xViewRight = PointD.ORIGIN;
      PointD yViewRight = PointD.ORIGIN;
      PointD xViewLeft = PointD.ORIGIN;
      PointD yViewLeft = PointD.ORIGIN;
      if (leftFaceVisible && backFaceVisible) {
        xViewCenter = x.getLayout().getTopLeft();
        yViewCenter = y.getLayout().getTopLeft();
        xViewRight = x.getLayout().getBottomLeft();
        yViewRight = y.getLayout().getBottomLeft();
        xViewLeft = x.getLayout().getTopRight();
        yViewLeft = y.getLayout().getTopRight();
      } else if (!leftFaceVisible && backFaceVisible) {
        xViewCenter = x.getLayout().getTopRight();
        yViewCenter = y.getLayout().getTopRight();
        xViewRight = x.getLayout().getTopLeft();
        yViewRight = y.getLayout().getTopLeft();
        xViewLeft = x.getLayout().getBottomRight();
        yViewLeft = y.getLayout().getBottomRight();
      } else if (!leftFaceVisible && !backFaceVisible) {
        xViewCenter = x.getLayout().getBottomRight();
        yViewCenter = y.getLayout().getBottomRight();
        xViewRight = x.getLayout().getTopRight();
        yViewRight = y.getLayout().getTopRight();
        xViewLeft = x.getLayout().getBottomLeft();
        yViewLeft = y.getLayout().getBottomLeft();
      } else if (leftFaceVisible && !backFaceVisible) {
        xViewCenter = x.getLayout().getBottomLeft();
        yViewCenter = y.getLayout().getBottomLeft();
        xViewRight = x.getLayout().getBottomRight();
        yViewRight = y.getLayout().getBottomRight();
        xViewLeft = x.getLayout().getTopLeft();
        yViewLeft = y.getLayout().getTopLeft();
      }

      int sgnX = leftFaceVisible ? -1 : 1;
      int sgnY = backFaceVisible ? -1 : 1;

      Matrix2D projectionMatrix = Matrix2D.fromTransform(canvasComponent.getProjection());
      PointD dViewCenter = PointD.subtract(projectionMatrix.transform(yViewCenter), projectionMatrix.transform(xViewCenter));

      // determine order in two steps:
      // 1) compare view coordinates of ViewCenter values to determine which node corners to compare in step 2
      // 2) compare the world coordinates of the corners found in step 1 considering which faces are visible
      if (dViewCenter.getX() < 0 && dViewCenter.getY() < 0) {
        PointD vector = PointD.subtract(yViewRight, xViewLeft);
        if (vector.getX() * sgnX > 0 && vector.getY() * sgnY > 0) {
          return -1;
        } else {
          return 1;
        }
      } else if (dViewCenter.getX() > 0 && dViewCenter.getY() > 0) {
        PointD vector = PointD.subtract(yViewLeft, xViewRight);
        if (vector.getX() * sgnX < 0 && vector.getY() * sgnY < 0) {
          return 1;
        } else {
          return -1;
        }
      } else if (dViewCenter.getX() > 0) {
        PointD vector = PointD.subtract(yViewCenter, xViewRight);
        if (vector.getX() * sgnX > 0 && vector.getY() * sgnY > 0) {
          return -1;
        } else {
          return 1;
        }
      } else {
        PointD vector = PointD.subtract(yViewRight, xViewCenter);
        if (vector.getX() * sgnX < 0 && vector.getY() * sgnY < 0) {
          return 1;
        } else {
          return -1;
        }
      }
    }
  }
}
