/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package toolkit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.TransferHandler;

/**
 * Provides utility methods for inner-JVM Drag and Drop operations. 
 */
public class DragAndDropSupport {
  private DragAndDropSupport() {
  }

  /**
   * Sets a custom 1x1 drag image to avoid the Mac OS Java default behavior
   * that displays a rectangle of the size of the cell from the drag source
   * as the drag image.
   */
  public static void disableDefaultPreview( TransferHandler handler ) {
    handler.setDragImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
  }

  /**
   * Creates a new {@link DataFlavor} instance
   * for a
   * {@link DataFlavor#javaJVMLocalObjectMimeType JVM local object}
   * of the given type.
   */
  public static DataFlavor newFlavor( Class<?> type )  {
    try {
      return new DataFlavor(
        DataFlavor.javaJVMLocalObjectMimeType + ";class=" + type.getName(),
        null,
        type.getClassLoader());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a new {@link Transferable} for transferring data from one Swing
   * component to another one in the same JVM.
   */
  public static Transferable newTransferable( DataFlavor flavor, Object data ) {
    return new MyTransferable(flavor, data);
  }


  /**
   * Transfers data from one Swing component to another one in the same JVM.
   */
  private static class MyTransferable implements Transferable {
    private DataFlavor flavor;
    /** The transferred data. */
    private Object data;

    /**
     * Initializes a new <code>MyTransferable</code> instance for transferring
     * the given data from one Swing component to another one in the same JVM.
     * @param flavor  the type of data to be transferred. The flavor should
     * represent a
     * {@link java.awt.datatransfer.DataFlavor#javaJVMLocalObjectMimeType JVM local object}.
     * The flavor's representation class should match the type of the given
     * data.
     * @param data the data to be transferred. The type of the data should match
     * the given flavor's representation class.
     */
    MyTransferable( DataFlavor flavor, Object data ) {
      this.flavor = flavor;
      this.data = data;
    }

    /**
     * Returns the transferable's single supported data flavor.
     * @return the transferable's single supported data flavor.
     * @see #MyTransferable(java.awt.datatransfer.DataFlavor, Object)
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {flavor};
    }

    /**
     * Determines whether or not the transferred data is represented
     * by the specified flavor.
     * @param flavor the flavor to check against the transferable's single
     * supported data flavor. 
     * @return <code>true</code> if the specified flavor matches the
     * transferable's single supported data flavor; <code>false</code>
     * otherwise.
     */
    @Override
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
      return flavor.equals(this.flavor);
    }

    /**
     * Returns the transferred data if the specified flavor matches the
     * transferable's single supported data flavor.
     * @param flavor the flavor to check against the transferable's single
     * supported data flavor.
     * @return the transferred data.
     * @throws UnsupportedFlavorException if the specified flavor does not
     * match the transferable's single supported data flavor.
     * @see #MyTransferable(java.awt.datatransfer.DataFlavor, Object)
     */
    @Override
    public Object getTransferData(
            DataFlavor flavor
    ) throws UnsupportedFlavorException, IOException {
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return data;
    }
  }
}
