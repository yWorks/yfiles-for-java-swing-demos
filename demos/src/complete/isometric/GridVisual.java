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
package complete.isometric;

import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Paints an isometric grid. This visual is used as the background of an
 * isometric graph view to emphasize the isometric perspective of the view.
 */
class GridVisual implements IVisual {
  Image grid;

  /**
   * Paints the isometric background grid in view coordinates.
   * I.e. the background grid is unaffected by zooming and panning operations.
   */
  @Override
  public void paint( IRenderContext context, Graphics2D g ) {
    Image image = getGridImage();
    if (image == null) {
      return;
    }
    int iw = image.getWidth(null);
    int ih = image.getHeight(null);

    CanvasComponent component = context.getCanvasComponent();
    int cw = component.getWidth();
    int ch = component.getHeight();


    AffineTransform oldTransform = g.getTransform();
    g.transform(context.getToViewTransform());

    for (int x = 0; x < cw; x += iw) {
      for (int y = 0; y < ch; y += ih) {
        g.drawImage(image, x, y, null);
      }
    }

    g.setTransform(oldTransform);
  }

  private Image getGridImage() {
    if (grid == null) {
      grid = getGridImageImpl();
    }
    return grid;
  }

  private Image getGridImageImpl() {
    URL url = getClass().getResource("resources/grid.png");
    if (url == null) {
      return null;
    } else {
      try {
        return ImageIO.read(url);
      } catch (Exception ex) {
        return null;
      }
    }
  }
}
