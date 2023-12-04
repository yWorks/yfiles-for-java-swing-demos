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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.SelectionIndicatorManager;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;

/**
 * Demonstrates zoom-invariant label rendering.
 */
public class ZoomInvariantLabelStyleDemo extends AbstractDemo {
  static final int SLIDER_VALUE_SCALE = 10;

  /**
   * Adds controls for choosing the label rendering mode as well as minimum
   * and maximum zoom values for the used label styles.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);

    // display the current zoom factor of the demo's graph component
    JLabel zoomLbl = newLabel("1.00");
    graphComponent.addZoomChangedListener(new ZoomHandler(zoomLbl));

    // create slider for changing the minimum zoom value for styles
    // ZoomInvariantBelowThresholdLabelStyle and ZoomInvariantOutsideRangeLabelStyle
    JLabel minZoomValue = newLabel("");
    JSlider minZoomJs = newSlider(1, minZoomValue, this::onMinZoomChanged);
    JLabel minZoomLbl = newLabel("Min. Zoom: ");
    Consumer<Boolean> minZoomControlsState = enabled -> {
      boolean state = enabled != Boolean.FALSE;
      minZoomLbl.setEnabled(state);
      minZoomJs.setEnabled(state);
      minZoomValue.setEnabled(state);
    };
    minZoomControlsState.accept(Boolean.TRUE);

    // create slider for changing the maximum zoom value for styles
    // ZoomInvariantAboveThresholdLabelStyle and ZoomInvariantOutsideRangeLabelStyle
    JLabel maxZoomValue = newLabel("");
    JSlider maxZoomJs = newSlider(3, maxZoomValue, this::onMaxZoomChanged);
    JLabel maxZoomLbl = newLabel("Max. Zoom: ");
    Consumer<Boolean> maxZoomControlsState = enabled -> {
      boolean state = enabled != Boolean.FALSE;
      maxZoomLbl.setEnabled(state);
      maxZoomJs.setEnabled(state);
      maxZoomValue.setEnabled(state);
    };
    maxZoomControlsState.accept(Boolean.FALSE);

    // create drop-down list for choosing label styles with different rendering behavior
    DefaultComboBoxModel<Mode> modes = new DefaultComboBoxModel<Mode>();
    for (Mode mode : Mode.values()) {
      modes.addElement(mode);
    }
    JComboBox<Mode> modeJcb = new JComboBox<>(modes);
    modeJcb.setSelectedItem(Mode.FIXED_BELOW_THRESHOLD);
    modeJcb.addActionListener(e -> {
      Mode mode = (Mode) ((JComboBox<Mode>) e.getSource()).getSelectedItem();

      setLabelStyles(mode,
        minZoomJs.getValue() / (double) SLIDER_VALUE_SCALE,
        maxZoomJs.getValue() / (double) SLIDER_VALUE_SCALE);

      switch (mode) {
        case FIXED_ABOVE_THRESHOLD:
          minZoomControlsState.accept(Boolean.FALSE);
          maxZoomControlsState.accept(Boolean.TRUE);
          break;
        case FIXED_BELOW_THRESHOLD:
          minZoomControlsState.accept(Boolean.TRUE);
          maxZoomControlsState.accept(Boolean.FALSE);
          break;
        case INVARIANT_OUTSIDE_RANGE:
          minZoomControlsState.accept(Boolean.TRUE);
          maxZoomControlsState.accept(Boolean.TRUE);
          break;
        default:
          minZoomControlsState.accept(Boolean.FALSE);
          maxZoomControlsState.accept(Boolean.FALSE);
          break;
      }
    });


    toolBar.addSeparator();
    toolBar.add(newLabel("Zoom Mode: "));
    toolBar.add(modeJcb);
    toolBar.addSeparator();
    toolBar.add(newLabel("Current Zoom Level: "));
    toolBar.add(zoomLbl);
    toolBar.addSeparator();
    toolBar.add(minZoomLbl);
    toolBar.add(minZoomJs);
    toolBar.add(minZoomValue);
    toolBar.addSeparator();
    toolBar.add(maxZoomLbl);
    toolBar.add(maxZoomJs);
    toolBar.add(maxZoomValue);
  }

  /**
   * Configures user interaction and creates a sample diagram.
   */
  @Override
  public void initialize() {
    // prevent adding, removing, or editing elements but support selecting
    // elements and panning the view
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE).or(GraphItemTypes.LABEL));
    graphComponent.setInputMode(gvim);

    // load a sample graph
    loadGraph();
  }

  /**
   * Centers the sample diagram in the visible area.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Set the label styles appropriate for the given label rendering policy.
   */
  private void setLabelStyles( Mode mode, double minZoom, double maxZoom ) {
    IGraph graph = graphComponent.getGraph();
    IGraphSelection selection = graphComponent.getSelection();
    SelectionIndicatorManager<IModelItem> manager = graphComponent.getSelectionIndicatorManager();

    for (ILabel label : graph.getLabels()) {
      boolean updateSelectionHighlight = selection.isSelected(label);
      if (updateSelectionHighlight) {
        manager.removeSelection(label);
      }

      graph.setStyle(label, newLabelStyle(mode, minZoom, maxZoom));

      if (updateSelectionHighlight) {
        manager.addSelection(label);
      }
    }
  }

  /**
   * Updates the maximum zoom value of the used label styles.
   */
  private void onMaxZoomChanged( Double value ) {
    if (value == null) {
      return;
    }

    double newValue = value.doubleValue();
    for (ILabel label : graphComponent.getGraph().getLabels()) {
      ILabelStyle style = label.getStyle();
      if (style instanceof ZoomInvariantOutsideRangeLabelStyle) {
        ((ZoomInvariantOutsideRangeLabelStyle) style).setMaxZoom(newValue);
      } else if (style instanceof ZoomInvariantAboveThresholdLabelStyle) {
        ((ZoomInvariantAboveThresholdLabelStyle) style).setMaxZoom(newValue);
      }
    }
    graphComponent.repaint();
  }

  /**
   * Updates the minimum zoom value of the used label styles.
   */
  private void onMinZoomChanged( Double value ) {
    if (value == null) {
      return;
    }

    double newValue = value.doubleValue();
    for (ILabel label : graphComponent.getGraph().getLabels()) {
      ILabelStyle style = label.getStyle();
      if (style instanceof ZoomInvariantOutsideRangeLabelStyle) {
        ((ZoomInvariantOutsideRangeLabelStyle) style).setMinZoom(newValue);
      } else if (style instanceof ZoomInvariantBelowThresholdLabelStyle) {
        ((ZoomInvariantBelowThresholdLabelStyle) style).setMinZoom(newValue);
      }
    }
    graphComponent.repaint();
  }

  /**
   * Loads a sample graph.
   */
  private void loadGraph() {
    DemoStyles.initDemoStyles(graphComponent.getGraph());
    GraphMLIOHandler graphMLIOHandler = graphComponent.getGraphMLIOHandler();
    graphMLIOHandler.addXamlNamespaceMapping(
      "http://www.yworks.com/yfiles-for-java/demos/zoominvariantlabelstyle/1.0",
      AbstractZoomInvariantLabelStyle.class);

    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new ZoomInvariantLabelStyleDemo().start();
    });
  }

  /**
   * Create a label for the given text.
   */
  private static JLabel newLabel( String text ) {
    JLabel jl = new JLabel(text);
    jl.setFont(jl.getFont().deriveFont(Font.PLAIN));
    return jl;
  }

  /**
   * Creates a slider for the range {@code 1} to {@code 4} with the given initial value.
   * @param valueLabel displays the current slider value as text
   * @param onValueChanged called when the slider value has changed
   */
  private static JSlider newSlider(int initialValue, JLabel valueLabel, Consumer<Double> onValueChanged) {
    valueLabel.setText(Double.toString(initialValue));
    int min = 10;
    int max = 40;
    int value = Math.max(min, Math.min(max, initialValue * SLIDER_VALUE_SCALE));
    JSlider slider = new JSlider(min, max, value);
    slider.addChangeListener(e -> {
      JSlider src = (JSlider) e.getSource();
      double newValue = src.getValue() / (double) SLIDER_VALUE_SCALE;
      valueLabel.setText(Double.toString(newValue));
      if (!src.getValueIsAdjusting()) {
        onValueChanged.accept(Double.valueOf(newValue));
      }
    });
    return slider;
  }

  /**
   * Create a new label style instance that demonstrates the given policy
   * for zoom-invariant label rendering.
   */
  private static ILabelStyle newLabelStyle( Mode mode, double minZoom, double maxZoom ) {
    switch (mode) {
      case DEFAULT:
        DefaultLabelStyle defaultStyle = new DefaultLabelStyle();
        defaultStyle.setUsingFractionalFontMetricsEnabled(true);
        return defaultStyle;
      case FIXED_ABOVE_THRESHOLD:
        ZoomInvariantAboveThresholdLabelStyle invariantAboveStyle =
          new ZoomInvariantAboveThresholdLabelStyle();
        invariantAboveStyle.setMaxZoom(maxZoom);
        return invariantAboveStyle;
      case FIXED_BELOW_THRESHOLD:
        ZoomInvariantBelowThresholdLabelStyle invariantBelowStyle =
           new ZoomInvariantBelowThresholdLabelStyle();
        invariantBelowStyle.setMinZoom(minZoom);
        return invariantBelowStyle;
      case INVARIANT_OUTSIDE_RANGE:
        ZoomInvariantOutsideRangeLabelStyle invariantRangeStyle =
          new ZoomInvariantOutsideRangeLabelStyle();
        invariantRangeStyle.setMaxZoom(maxZoom);
        invariantRangeStyle.setMinZoom(minZoom);
        return invariantRangeStyle;
      case FIT_OWNER:
        return new FitOwnerLabelStyle();
      default:
        throw new IllegalArgumentException();
    }
  }



  /**
   * Displays the current zoom factor of the demo's graph view as text.
   */
  private static final class ZoomHandler implements IEventListener {
    private final JLabel label;
    private final NumberFormat format;

    ZoomHandler( final JLabel label ) {
      this.label = label;
      this.format = newFormat();
    }

    @Override
    public void onEvent( final Object source, final IEventArgs args ) {
      label.setText(format.format(((GraphComponent) source).getZoom()));
    }

    private static NumberFormat newFormat() {
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(2);
      nf.setMinimumFractionDigits(2);
      return nf;
    }
  }

  /**
   * Enumerates the supported zoom-invariant label rendering policies.
   */
  enum Mode {
    DEFAULT("Default Label Style"),
    FIXED_ABOVE_THRESHOLD("Fixed above maximum zoom"),
    FIXED_BELOW_THRESHOLD("Fixed below minimum zoom"),
    INVARIANT_OUTSIDE_RANGE("Fixed when outside specified range"),
    FIT_OWNER("Fit into the label's owner");


    private final String description;

    Mode( String description ) {
      this.description = description;
    }


    @Override
    public String toString() {
      return description;
    }
  }
}
