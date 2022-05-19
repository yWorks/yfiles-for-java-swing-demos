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
package viewer.backgroundimage;

import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Creates visuals that display an image.
 */
public class ImageVisualCreator implements IVisualCreator {
  /**
   * The image displayed by this creator's visuals.
   */
  private final BufferedImage image;

  /**
   * Initializes a new {@code ImageVisualCreator} instance
   * @param image The image to display by the creator's visuals. 
   */
  public ImageVisualCreator( BufferedImage image ) {
    this.image = image;
  }

  /**
   * Creates the visual for the background.
   * @param context The context which describes where the visual will be used.
   * @return {@link com.yworks.yfiles.view.IVisual} The visual for the background.
   */
  @Override
  public IVisual createVisual( IRenderContext context ) {
    if (image == null) {
      return null;
    } else {
      return new ImageVisual(image);
    }
  }

  /**
   * Updates the visual for the background.
   * @param context The context which describes where the visual will be used.
   * @param oldVisual The old visual
   * @return {@link com.yworks.yfiles.view.IVisual} The visual for the background.
   */
  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    if (oldVisual instanceof ImageVisual &&
        ((ImageVisual) oldVisual).image == image) {
      return oldVisual;
    }
    return createVisual(context);
  }

  /**
   * Displays an image centered on the origin of the (graph) coordinate space.
   */
  private static final class ImageVisual implements IVisual {
    final BufferedImage image;
    final int x;
    final int y;

    ImageVisual( BufferedImage image ) {
      this.image = image;
      this.x = -image.getWidth() / 2;
      this.y = -image.getHeight() / 2;
    }

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      g.drawImage(image, x, y, null);
    }
  }
}
