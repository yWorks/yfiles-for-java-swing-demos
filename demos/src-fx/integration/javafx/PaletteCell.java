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
package integration.javafx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/**
 * A {@link javafx.scene.control.ListCell} that shows images for the enum constants of {@link
 * NodeTemplate} in a {@link javafx.scene.control.ListView}.
 */
class PaletteCell extends ListCell<NodeTemplate> {
  private ImageView graphic;

   @Override
  protected void updateItem(NodeTemplate item, boolean empty) {
    super.updateItem(item, empty);

    if (item == null || empty) {
      setGraphic(null);
      setText(null);
    } else {
      // set an image of the given node template as graphic
      setGraphic(getGraphic(item));
      // set a description of the given node template as text
      setText(item.description());
    }
  }

  /**
   * Returns an {@link ImageView} for the given NodeTemplate constant.
   */
  private ImageView getGraphic(NodeTemplate template) {
    if (graphic == null) {
      BufferedImage image = template.createImage();
      graphic = new ImageView(SwingFXUtils.toFXImage(image, null));
    }
    return graphic;
  }
}
