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
package input.hyperlink;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.styles.HtmlLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.HtmlLabelStyle.Hyperlink;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.AbstractInputMode;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.IHitTester;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.graph.IModelItem;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Point;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Use {@link HtmlLabelStyle#findHyperlink(IInputModeContext, ILabel, PointD)}
 * to trigger and process hyperlink events with HTML formatted label text.
 * <p>
 * When clicking on an external link such as
 * <blockquote>
 * <code>&lt;a href="http://www.yworks.com/products/yfiles"&gt;yFiles for Java&lt;/a&gt;</code>,
 * </blockquote>
 * the link's destination is opened in a browser if
 * {@link java.awt.Desktop.Action#BROWSE} is supported. Otherwise a dialog is
 * opened that displays the link's destination.
 * </p>
 * <p>
 * Additionally, a custom protocol <code>graph</code> is used to allow
 * in-graph navigation. E.g. clicking on
 * <blockquote>
 * <code>&lt;a href="graph://yfilesforjava"&gt;yFiles for Java&lt;/a&gt;</code>
 * </blockquote>
 * will navigate to the first node in the graph that has a corresponding
 * <blockquote>
 * <code>&lt;a name="yfilesforjava"&gt;&lt;/a&gt;</code>
 * </blockquote>
 * declaration in its label text.
 * </p>
 * @author Thomas Behr
 */
public class HyperlinkDemo extends AbstractDemo {
  /**
   * Registers a {@link input.hyperlink.HyperlinkDemo.HyperlinkInputMode}
   * instance for hyperlink handling on mouse moved and mouse clicked events.
   * Opens a sample graph with several HTML formatted labels.
   */
  @Override
  public void initialize() {
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NONE);
    gvim.setFocusableItems(GraphItemTypes.NONE);
    // register the custom HyperlinkInputMode
    gvim.add(new HyperlinkInputMode());
    graphComponent.setInputMode(gvim);
    DemoStyles.initDemoStyles(graphComponent.getGraph());

    // open a sample graph with several HTML formatted labels
    URL url = getClass().getResource("resources/HyperlinkDemo.graphml");
    if (url != null) {
      try {
        graphComponent.importFromGraphML(url);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Shows the center node when the window is initially displayed.
   */
  @Override
  public void onVisible() {
    graphComponent.setZoom(1);
    graphComponent.setCenter(graphComponent.getGraph().getNodes().first().getLayout().getCenter());
  }


  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new HyperlinkDemo().start();
    });
  }



  /**
   * Performs hyperlink handling on mouse moved and mouse clicked events.
   */
  public static final class HyperlinkInputMode extends AbstractInputMode {
    /**
     * Triggers hyperlink updates when the mouse is moved over a hyperlink in
     * HTML formatted label text.
     */
    final IEventListener<Mouse2DEventArgs> onMoved;
    /**
     * Activates hyperlinks when a mouse click occurs on a hyperlink in HTML
     * formatted label text.
     */
    final IEventListener<Mouse2DEventArgs> onClicked;
    /**
     * The last hyperlink the mouse has been moved over.
     */
    Hyperlink link;

    /**
     * Initializes a new instance of <code>HyperlinkInputMode</code>.
     */
    public HyperlinkInputMode() {
      onMoved = (source, args) -> onMovedImpl(args.getLocation());
      onClicked = (source, args) -> onClickedImpl(args.getLocation());
    }

    /**
     * Registers the required mouse listeners for this input mode.
     */
    @Override
    public void install(
            IInputModeContext context,
            ConcurrencyController controller
    ) {
      super.install(context, controller);
      CanvasComponent cc = context.getCanvasComponent();
      if (cc != null) {
        cc.addMouse2DMovedListener(onMoved);
        cc.addMouse2DClickedListener(onClicked);
      }
    }

    /**
     * Deregisters the mode's mouse listeners.
     */
    @Override
    public void uninstall( IInputModeContext context ) {
      CanvasComponent cc = context.getCanvasComponent();
      if (cc != null) {
        cc.removeMouse2DMovedListener(onClicked);
        cc.removeMouse2DMovedListener(onMoved);
      }
      super.uninstall(context);
    }


    /**
     * Retrieves the first label at the given location.
     */
    protected ILabel findLabel( PointD location ) {
      ILabel label = null;
      for (IModelItem item : getHitItemsAt(location)) {
        if (item instanceof ILabel) {
          label = (ILabel) item;
          break;
        }
      }
      return label;
    }

    /**
     * Retrieves all the graph elements at the given location.
     */
    protected Iterable<IModelItem> getHitItemsAt( PointD location ) {
      IInputModeContext ctx = getInputModeContext();
      IHitTester<IModelItem> hte = ctx.lookup(IHitTester.class);
      return hte == null
             ? IEnumerable.EMPTY
             : hte.enumerateHits(IInputModeContext.create(this, ctx, ILookup.EMPTY), location);
    }


    /**
     * Handles mouse clicks for the given location.
     * This method ...
     * <ol>
     *   <li>
     *     ... checks if there is a HTML formatted label at the given location,
     *   </li>
     *   <li>
     *     ... if yes, checks if the click occurred on a HTML hyperlink,
     *   </li>
     *   <li>
     *     ... if yes, activates the hyperlink.
     *   </li>
     * </ol>
     * @see #activateHyperlink(ILabel, Hyperlink)
     */
    protected void onClicked( IInputModeContext context, PointD location ) {
      ILabel label = findLabel(location);
      if (label != null) {
        ILabelStyle style = label.getStyle();
        if (style instanceof HtmlLabelStyle) {
          HtmlLabelStyle hls = (HtmlLabelStyle) style;
          activateHyperlink(label, hls.findHyperlink(context, label, location));
        }
      }
    }

    /**
     * Handles mouse move events for the given location.
     * This method checks if there is an HTML-formatted label at the given
     * location and updates the mode's cached hyperlink accordingly/
     * @see #updateHyperlink(Hyperlink)
     */
    protected void onMoved( IInputModeContext context, PointD location ) {
      ILabel label = findLabel(location);
      if (label != null) {
        ILabelStyle style = label.getStyle();
        if (style instanceof HtmlLabelStyle) {
          HtmlLabelStyle hls = (HtmlLabelStyle) style;
          updateHyperlink(hls.findHyperlink(context, label, location));
        }
      }
    }

    /**
     * Activates the specified hyperlink.
     * @see #isGraphNavigationEvent(Hyperlink)
     * @see #displayExternalLink(ILabel, Hyperlink)
     * @see #navigateTo(Hyperlink)
     */
    protected void activateHyperlink( ILabel label, Hyperlink link ) {
      if (link == null) {
        return;
      }

      // determine if the event is triggered from a link that uses the demo's
      // custom "graph" protocol that can be used to navigate the current
      // graph
      if (isGraphNavigationEvent(link)) {
        navigateTo(link);
      } else {
        displayExternalLink(label, link);
      }
    }

    /**
     * Determines whether the specified hyperlink
     * uses the demo's custom <code>graph</code> protocol.
     * @param link the hyperlink to check.
     * @return <code>true</code> the hyperlink uses the demo's custom
     * <code>graph</code> protocol; <code>false</code> otherwise.
     */
    private boolean isGraphNavigationEvent( Hyperlink link ) {
      URL url = link.getURL();
      if (url == null) {
        String desc = link.getDescription();
        return desc != null && desc.startsWith("graph://");
      } else {
        return "graph".equals(url.getProtocol());
      }
    }

    /**
     * Displays the specified hyperlink's destination.
     * If {@link java.awt.Desktop.Action#BROWSE} is supported, the link's
     * destination is opened in a browser. Otherwise the link's destination is
     * displayed in a dialog.
     * @param link the hyperlink whose destination has to be displayed.
     */
    private void displayExternalLink( ILabel label, Hyperlink link ) {
      // try to open the link destination in a browser
      if (Desktop.isDesktopSupported()) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
          URL url = link.getURL();
          if (url != null) {
            try {
              desktop.browse(url.toURI());
              return;
            } catch (Exception e) {
              e.printStackTrace();
            }
          }

          try {
            desktop.browse(new URI(link.getDescription()));
            return;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }

      // opening the link destination in a browser failed
      // display the link destination in a dialog instead
      IOrientedRectangle lbox = label.getLayout();
      CanvasComponent view = getInputModeContext().getCanvasComponent();
      Point l = view.getLocationOnScreen();
      PointD p = view.toViewCoordinates(lbox.getAnchorLocation());
      int vx = l.x + (int) Math.round(p.getX());
      int vy = l.y + (int) Math.round(p.getY());

      String title = "External Link";
      String message =
              title +
              "\nHref: " + link.getDescription();
      JOptionPane jop = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
      JDialog jd = jop.createDialog(view, title);
      jd.setLocation(vx, vy);
      jd.setVisible(true);
    }

    /**
     * Navigates to the node that is referenced in the specified hyperlink's
     * destination.
     * @param link a hyperlink that uses the demo's custom <code>graph</code>
     * protocol.
     */
    private void navigateTo( Hyperlink link ) {
      String destination;
      URL url = link.getURL();
      if (url == null) {
        destination = link.getDescription().substring(8);
      } else {
        destination = url.getPath();
      }

      // search for a node that has an anchor which corresponds to the
      // desired destination
      GraphComponent view = (GraphComponent) getInputModeContext().getCanvasComponent();
      IGraph graph = view.getGraph();
      for (ILabel label : graph.getLabels()) {
        String s = label.getText();
        if (s.indexOf("<a name=\"" + destination + "\">") > -1) {
          ILabelOwner owner = label.getOwner();
          if (owner instanceof INode) {
            navigateTo(view, (INode) owner);
            break;
          }
        }
      }
    }

    /**
     * Centers the specified node in the demo's graph view.
     */
    private void navigateTo( GraphComponent view, INode node ) {
      IRectangle nl = node.getLayout();
      double z = view.getZoom();
      view.zoomToAnimated(nl.getCenter(), z);
    }


    /**
     * Updates the cached hyperlink and adjusts the mouse cursor for the
     * graph component associated to this mode.
     * @param newLink the hyperlink that has to be cached.
     * May be <code>null</code>.
     * @see #onEntered(Hyperlink)
     * @see #onExited(Hyperlink) (Hyperlink)
     */
    protected void updateHyperlink( Hyperlink newLink ) {
      Hyperlink oldLink = this.link;
      if (!Objects.equals(oldLink, newLink)) {
        if (oldLink != null) {
          onExited(oldLink);
        }

        this.link = newLink;

        if (newLink != null) {
          onEntered(newLink);
        }
      }
    }

    /**
     * Changes the mouse cursor to a hand cursor for the graph component
     * associated to this mode when the mouse is moved over a HTML hyperlink.
     * Additionally, the component's tool tip text to the given hyperlink's
     * {@link Hyperlink#getDescription() description}.
     * @param link the hyperlink under the mouse cursor.
     */
    protected void onEntered( Hyperlink link ) {
      setToolTipText(link.getDescription());
      setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Changes the mouse cursor to the platform default cursor for the graph
     * component associated to this mode when the mouse is moved away from a
     * HTML hyperlink. Additionally, the component's tool tip text is removed.
     * @param link the hyperlink from which the mouse is moved away. 
     */
    protected void onExited( Hyperlink link ) {
      setCursor(Cursor.DEFAULT_CURSOR);
      setToolTipText(null);
    }


    void onMovedImpl( PointD location ) {
      onMoved(getInputModeContext(), location);
    }

    void onClickedImpl( PointD location ) {
      onClicked(getInputModeContext(), location);
    }

    /**
     * Sets the mouse cursor identified by the given symbolic cursor type for
     * the graph component associated to this mode. 
     * @param cursor the cursor type of a predefined mouse cursor.
     */
    void setCursor( int cursor ) {
      ConcurrencyController cntrllr = getController();
      if (cntrllr != null) {
        cntrllr.setPreferredCursor(Cursor.getPredefinedCursor(cursor));
      }
    }

    /**
     * Sets the specified text as tool tip text for the graph component
     * associated to this mode.
     */
    void setToolTipText( String text ) {
      CanvasComponent canvas = getInputModeContext().getCanvasComponent();
      if (canvas != null) {
        canvas.setToolTipText(text);
      }
    }
  }
}
