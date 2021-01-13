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
package layout;

import com.yworks.yfiles.algorithms.AlgorithmAbortedException;
import com.yworks.yfiles.layout.LayoutEventArgs;
import com.yworks.yfiles.utils.IEventListener;

import javax.swing.JOptionPane;

/**
 * Helper class providing static methods that can be registered as
 * {@link com.yworks.yfiles.layout.LayoutExecutor#addLayoutFinishedListener(IEventListener) LayoutFinishedListener}.
 */
public class LayoutFinishedListeners {

  /**
   * Checks if args contains an {@link LayoutEventArgs#getException() exception} and shows an {@link Alert} depending on the
   * exception's class.
   * @param source The source of the event.
   * @param args The event arguments to handle.
   */
  public static void handleErrors(Object source, LayoutEventArgs args) {
    if (args.getException() != null) {
      RuntimeException exception = args.getException();
      if (exception instanceof AlgorithmAbortedException) {
        String message = "The layout computation was canceled because the maximum configured runtime of 20 seconds was exceeded.";
        JOptionPane.showMessageDialog(null, message, "Layout Canceled", JOptionPane.WARNING_MESSAGE);
      } else {
        String message = exception.getMessage();
        if (message == null || message.isEmpty()) {
          message = "An error occured while calculating the layout.";
        }
        JOptionPane.showMessageDialog(null, message, "Error during Layout", JOptionPane.ERROR_MESSAGE);
      }
      args.setHandled(true);
    }
  }
}
