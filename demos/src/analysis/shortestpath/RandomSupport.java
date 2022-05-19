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
package analysis.shortestpath;

import java.util.Random;

/**
 * Helper class to create and permute arrays.
 */
class RandomSupport {

  private RandomSupport() {
  }

  /**
   * Permutes the positions of the elements within the given array.
   */
  public static void permute(Random random, Object[] elements) {
    // forth...
    for (int i = 0; i < elements.length; i++) {
      int j = random.nextInt(elements.length);
      Object tmp = elements[i];
      elements[i] = elements[j];
      elements[j] = tmp;
    }
    // back...
    for (int i = elements.length - 1; i >= 0; i--) {
      int j = random.nextInt(elements.length);
      Object tmp = elements[i];
      elements[i] = elements[j];
      elements[j] = tmp;
    }
  }

  /**
   * Returns an array of <code>n</code> unique random integers that lie within the range <code>min</code> (inclusive)
   * and <code>max</code> (exclusive). If <c>max - min &lt; n</c> then <c>null</c> is returned.
   */
  public static int[] getUniqueArray(Random random, int n, int min, int max) {
    max--;

    int[] ret = null;
    int l = max - min + 1;
    if (l >= n && n > 0) {
      int[] accu = new int[l];
      ret = new int[n];
      for (int i = 0, j = min; i < l; i++, j++) {
        accu[i] = j;
      }
      for (int j = 0, m = l - 1; j < n; j++, m--) {
        int r = random.nextInt(m + 1);
        ret[j] = accu[r];
        if (r < m) {
          accu[r] = accu[m];
        }
      }
    }
    return ret;
  }

  /**
   * Returns an array of <code>n</code> randomly chosen boolean values of which <code>trueCount</code> of them are
   * <code>true</code>. If the requested numbers of true values is bigger than the number of requested boolean values,
   * an Exception is raised.
   */
  public static boolean[] getBoolArray(Random random, int n, int trueCount) {
    if (trueCount > n) {
      throw new IllegalArgumentException("RandomSupport.getBoolArray( " + n + ", " + trueCount + " )");
    }

    int[] a = getUniqueArray(random, trueCount, 0, n);
    boolean[] b = new boolean[n];
    if (a != null) {
      for (int i = 0; i < a.length; i++) {
        b[a[i]] = true;
      }
    }
    return b;
  }
}
