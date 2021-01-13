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
package complete.uml;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;

/**
 * Fixes ports at a specific, absolute location.
 * This model will keep port locations in place during UML node animations even
 * if the size of the animated UML node changes.
 */
public class AbsolutePortLocationModel implements IPortLocationModel {
  /**
   * Shared instance of the {@link AbsolutePortLocationModel}.
   * Since the model does not have any state, there is no need for individual
   * instances.
   */
  public static final IPortLocationModel INSTANCE = new AbsolutePortLocationModel();

  /**
   * Prevents instantiation of {@link AbsolutePortLocationModel}.
   * Since the model does not have any state, there is no need for individual
   * instances.
   */
  private AbsolutePortLocationModel() {
  }

  /**
   * Returns {@code null}.
   * There are no implementation interfaces for {@link AbsolutePortLocationModel}.
   */
  public final <TLookup> TLookup lookup(Class<TLookup> type) {
    return null;
  }

  /**
   * Returns the given parameter's location.
   * @see #createParameter(IPortOwner, PointD)
   */
  public final PointD getLocation(IPort port, IPortLocationModelParameter locationParameter) {
    return ((AbsolutePortLocationParameter) locationParameter).location;
  }

  /**
   * Creates new parameter for the given absolute location.
   * @param location the absolute port location.
   */
  public final IPortLocationModelParameter createParameter(IPortOwner owner, PointD location) {
    return new AbsolutePortLocationParameter(location);
  }

  /**
   * Returns an empty lookup implementation.
   * There are no implementation interfaces for {@link AbsolutePortLocationModel}.
   */
  public final ILookup getContext(IPort port, IPortLocationModelParameter locationParameter) {
    return ILookup.EMPTY;
  }

  /**
   * Stores an absolute location used as port location with
   * {@link AbsolutePortLocationModel}.
   */
  static final class AbsolutePortLocationParameter implements IPortLocationModelParameter {
    final PointD location;

    /**
     * Initializes a new {@link AbsolutePortLocationParameter} instance
     * for the given absolute location.
     * @param location the absolute port location.
     */
    AbsolutePortLocationParameter(PointD location) {
      this.location = location;
    }

    /**
     * Returns this instance.
     * Since {@link AbsolutePortLocationParameter} is immutable, there is no
     * need for creating a new instance.
     */
    public final AbsolutePortLocationParameter clone() {
      return this;
    }

    /**
     * Returns the shared {@link AbsolutePortLocationModel} instance.
     * Since the model does not have any state, there is no need for individual
     * instances.
     * @see AbsolutePortLocationModel#INSTANCE
     */
    public final IPortLocationModel getModel() {
      return INSTANCE;
    }

    /**
     * Returns {@code true}.
     */
    public final boolean supports(IPortOwner owner) {
      return true;
    }
  }
}
