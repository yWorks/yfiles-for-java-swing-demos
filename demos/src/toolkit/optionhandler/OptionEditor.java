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
package toolkit.optionhandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

/**
 * Builds the editor component for an option configuration. 
 * @author Thomas Behr
 */
public class OptionEditor {
  static final String OPTION_KEY= "toolkit.optionhandler.OptionEditor";
  static final Color BORDER_COLOR = Color.LIGHT_GRAY;
  static final double WEIGHT_NONE = 0;
  static final double WEIGHT_ALL = 1;
  static final int TEXT_WIDTH = 300;
  static final int DEFAULT_INSET = 6;


  private Object configuration;

  public Object getConfiguration() {
    return configuration;
  }

  public void setConfiguration( Object configuration ) {
    this.configuration = configuration;
  }

  public JComponent buildEditor() {
    final ConfigConverter converter = new ConfigConverter();
    final OptionGroup options = converter.convert(getConfiguration());
    final ConstraintManager cm = new ConstraintManager();
    final JComponent editor = newEditorComponent(options, 0, cm);
    editor.putClientProperty(OPTION_KEY, options);

    // set initial enabled/disabled states
    cm.valueChanged();

    return editor;
  }

  public void resetEditor( final JComponent editor ) {
    final Object value = editor.getClientProperty(OPTION_KEY);
    if (value instanceof OptionGroup) {
      final ConstraintManager cm = find((OptionGroup) value);
      cm.setEnabled(false);
      try {
        final ArrayList<JComponent> stack = new ArrayList<JComponent>();
        stack.add(editor);
        while (!stack.isEmpty()) {
          final JComponent c = stack.remove(stack.size() - 1);

          final Object cv = c.getClientProperty(OPTION_KEY);
          if (cv instanceof Option) {
            final Option option = (Option) cv;
            final Object defaultValue = option.getDefaultValue();

            switch (option.getComponentType()) {
              case CHECKBOX:
                ((JCheckBox) c).setSelected(Boolean.TRUE.equals(defaultValue));
                break;
              case COMBOBOX:
                ((JComboBox) c).setSelectedItem(defaultValue);
                break;
              case FORMATTED_TEXT:
                ((JTextComponent) c).setText(wrap((String) defaultValue, TEXT_WIDTH));
                break;
              case SLIDER:
                ((JSpinner) c).setValue(defaultValue);
                break;
              case SPINNER:
                ((JSpinner) c).setValue(defaultValue);
                break;
              case OPTION_GROUP:
                break;
              default:
                // RADIO_BUTTON
                // TEXT
                throw new UnsupportedOperationException(
                        "Component type " +
                        option.getComponentType() +
                        " not yet supported.");
            }
          }

          if (c instanceof JPanel) {
            for (int i = 0, n = c.getComponentCount(); i < n; ++i) {
              stack.add((JComponent) c.getComponent(i));
            }
          }
        }
      } finally {
        cm.setEnabled(true);
        cm.valueChanged();
      }
    }
  }



  private JComponent newEditorComponent(
          final OptionGroup group,
          final int level,
          final ConstraintManager cm
  ) {
    final int columnCount = 3;
    final int firstColumn = 0;

    final int levelInset = DEFAULT_INSET;
    final int additionalInset = level > 0 ? levelInset : 0;

    final boolean collapsed = level == 1;
    final JPanel root = new JPanel();
    root.setName(group.getName());
    if (level > 0) {
      final EditorLayout layout = new EditorLayout();
      root.setLayout(layout);
      layout.setCollapsed(root, collapsed);

      if (level > 1) {
        root.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        layout.setOffset(new Insets(levelInset, levelInset, levelInset, levelInset));
      } else {
        layout.setOffset(new Insets(0, levelInset, levelInset, levelInset));
      }
    } else {
      root.setLayout(new GridLayout(1, 1));
    }

    final JPanel contents = new JPanel(new GridBagLayout());
    final GridBagConstraints gbc = new GridBagConstraints();
    final Insets defaultInsets = gbc.insets;
    final Insets insets = new Insets(
            defaultInsets.top + levelInset,
            defaultInsets.left + additionalInset,
            defaultInsets.bottom + levelInset,
            defaultInsets.right + additionalInset);
    gbc.gridx = firstColumn;
    gbc.gridy = 0;
    gbc.insets = insets;

    final String label = group.getLabel();
    if (level > 0 && label != null) {
      final CollapseHandler handler = new CollapseHandler(root);
      final JPanel header = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
      header.add(new JLabel(new Arrow(BORDER_COLOR, collapsed)));
      header.add(newLabelPane(label));
      header.setBorder(new Separator(BORDER_COLOR));
      for (int i = 0, n = header.getComponentCount(); i < n; ++i) {
        header.getComponent(i).addMouseListener(handler);
      }

      // although the constraint is ignored, it is nevertheless necessary to
      // trigger method EditorLayout.addLayoutComponent
      root.add(header, BorderLayout.NORTH);
    }

    for (Option child : group.getChildOptions()) {
      gbc.insets = insets;

      if (child instanceof OptionGroup) {
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = columnCount;
        gbc.gridx = firstColumn;
        gbc.weightx = WEIGHT_ALL;
        contents.add(newEditorComponent((OptionGroup) child, level + 1, cm), gbc);
      } else {
        gbc.gridwidth = 1;

        final Consumer<Object> setter = child.getSetter();
        if (setter != null) {
          child.setSetter(new NotifyValueChanged(setter, cm));
        }

        final Supplier<Boolean> disabled = child.getCheckDisabled();

        switch (child.getComponentType()) {
          case CHECKBOX:
            final JLabel checkBoxLabel = newLabel(child.getLabel(), false);
            final JCheckBox checkBox = new JCheckBox();
            checkBox.putClientProperty(OPTION_KEY, child);
            checkBox.setSelected(Boolean.TRUE.equals(child.getValue()));
            checkBox.addItemListener(new CheckBoxHandler(child));

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(checkBoxLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(checkBox, disabled));
            }

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 2;
            gbc.gridx = firstColumn;
            gbc.weightx = WEIGHT_ALL;
            contents.add(checkBoxLabel, gbc);

            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridwidth = 1;
            gbc.gridx = 2;
            gbc.weightx = WEIGHT_NONE;
            contents.add(checkBox, gbc);
            break;
          case COMBOBOX:
            final JLabel comboBoxLabel = newLabel(child.getLabel(), false);
            final JComboBox comboBox = newComboBox(child);
            comboBox.putClientProperty(OPTION_KEY, child);

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(comboBoxLabel, disabled));
              cm.addValueChangedListener(new SetEnabled(comboBox, disabled));
            }

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridwidth = 1;
            gbc.gridx = firstColumn;
            gbc.weightx = WEIGHT_NONE;
            contents.add(comboBoxLabel, gbc);

            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 2;
            gbc.gridx = 1;
            gbc.insets = new Insets(insets.top, defaultInsets.left, insets.bottom, insets.right);
            gbc.weightx = WEIGHT_ALL;
            contents.add(comboBox, gbc);
            break;
          case FORMATTED_TEXT:
            final JComponent textPane = newTextPane((String) child.getValue(), true);
            textPane.putClientProperty(OPTION_KEY, child);

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = columnCount;
            gbc.gridx = firstColumn;
            gbc.weightx = WEIGHT_ALL;
            contents.add(textPane, gbc);
            break;
          case SLIDER:
            final JComponent component = newExtendedSlider(child);

            if (disabled != null) {
              for (int i = 0, n = component.getComponentCount(); i < n; ++i) {
                final JComponent jc = (JComponent) component.getComponent(i);
                cm.addValueChangedListener(new SetEnabled(jc, disabled));
              }
            }

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = columnCount;
            gbc.gridx = firstColumn;
            gbc.weightx = WEIGHT_ALL;
            contents.add(component, gbc);
            break;
          case SPINNER:
            final JLabel spinnerLabel = newLabel(child.getLabel(), false);
            final JComponent spinner = newSpinner(child);

            if (disabled != null) {
              cm.addValueChangedListener(new SetEnabled(spinnerLabel, disabled));
              final JComponent jc = (JComponent) spinner.getComponent(0);
              cm.addValueChangedListener(new SetEnabled(jc, disabled));
            }

            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridwidth = 1;
            gbc.gridx = firstColumn;
            gbc.weightx = WEIGHT_NONE;
            contents.add(spinnerLabel, gbc);

            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 2;
            gbc.gridx = 1;
            gbc.insets = new Insets(insets.top, defaultInsets.left, insets.bottom, insets.right);
            gbc.weightx = WEIGHT_ALL;
            contents.add(spinner, gbc);
            break;
          case OPTION_GROUP:
            break;
          default:
            // RADIO_BUTTON
            // TEXT
            throw new UnsupportedOperationException(
                    "Component type " +
                    child.getComponentType() +
                    " not yet supported.");
        }
      }

      ++gbc.gridy;
    }

    if (level == 0) {
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridwidth = columnCount;
      gbc.gridx = firstColumn;
      gbc.weightx = WEIGHT_ALL;
      gbc.weighty = WEIGHT_ALL;
      contents.add(newSpacer(), gbc);
    }

    // although the constraint is ignored, it is nevertheless necessary to
    // trigger method EditorLayout.addLayoutComponent
    root.add(contents, BorderLayout.CENTER);

    return root;
  }

  private JComboBox<Object> newComboBox( final Option option ) {
    final HashMap<Object, String> map = new HashMap<Object, String>();
    final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>();
    for (EnumValue ev : option.getEnumValues()) {
      final Object value = ev.getValue();
      model.addElement(value);
      map.put(value, ev.getName());
    }

    final JComboBox<Object> comboBox = new JComboBox<Object>();
    comboBox.setFont(newDefaultFont(-1));
    comboBox.setRenderer(new MappedRenderer(map));
    comboBox.setModel(model);
    comboBox.setSelectedItem(option.getValue());
    comboBox.addItemListener(new ComboBoxHandler(option));
    return comboBox;
  }

  /**
   * Creates a compound component consisting of a {@link JSlider} and a
   * {@link JSpinner} instance. The values of the slider and the spinner will
   * be synchronized (i.e. changing one will also change the other).
   */
  private JComponent newExtendedSlider( final Option option ) {
    final JSlider slider = new JSlider();
    final JSpinner spinner = new JSpinner();
    spinner.putClientProperty(OPTION_KEY, option);
    configure(option, slider, spinner);

    final GridBagConstraints gbc = new GridBagConstraints();
    final JPanel pane = new JPanel(new GridBagLayout());

    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = WEIGHT_NONE;
    gbc.weighty = WEIGHT_NONE;
    pane.add(newLabel(option.getLabel(), false), gbc);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    gbc.weightx = WEIGHT_ALL;
    pane.add(slider, gbc);
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0, 4, 0, 1);
    gbc.gridx = 1;
    gbc.weightx = WEIGHT_NONE;
    pane.add(spinner, gbc);
    return pane;
  }

  private void configure(
          final Option option, final JSlider slider, final JSpinner spinner
  ) {
    slider.setSnapToTicks(true);

    final Object value = option.getValue();
    final MinMax minMax = option.getMinMax();
    if (Integer.TYPE.equals(option.getValueType())) {
      final int min = minMax == null ? 0 : (int) minMax.min();
      final int max = minMax == null ? 0 : (int) minMax.max();
      final int step = minMax == null ? 1 : (int) minMax.step();
      final int v = value instanceof Number ? ((Number) value).intValue() : min;

      final ChangeListener handler = new IntValueHandler(option, slider, spinner, min, step);

      slider.setMinorTickSpacing(step);
      slider.setMaximum(max);
      slider.setMinimum(min);
      slider.setValue(v);
      slider.addChangeListener(handler);

      spinner.setModel(new SpinnerNumberModel(v, min, max, step));
      spinner.addChangeListener(handler);
    } else {
      final double min = minMax == null ? 0 : minMax.min();
      final double max = minMax == null ? 0 : minMax.max();
      final double step = minMax == null ? 1.0 : minMax.step();
      final double v = value instanceof Number ? ((Number) value).doubleValue() : min;

      final int smin = (int) Math.rint(min / step);
      final int smax = (int) Math.rint(max / step);
      final int sv = (int) Math.rint(v / step);

      final ChangeListener handler = new DoubleValueHandler(option, slider, spinner, min, step);

      slider.setMinorTickSpacing(1);
      slider.setMaximum(smax);
      slider.setMinimum(smin);
      slider.setValue(sv);
      slider.addChangeListener(handler);

      spinner.setModel(new SpinnerNumberModel(v, min, max, step));
      spinner.addChangeListener(handler);
    }

    final JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
    final JFormattedTextField jtf = editor.getTextField();
    jtf.setColumns(4);
  }

  private JComponent newSpinner( final Option option ) {
    final JSpinner spinner = new JSpinner();
    spinner.putClientProperty(OPTION_KEY, option);

    final Object value = option.getValue();
    final MinMax minMax = option.getMinMax();
    if (Integer.TYPE.equals(option.getValueType())) {
      final int min = minMax == null ? Integer.MIN_VALUE : (int) minMax.min();
      final int max = minMax == null ? Integer.MAX_VALUE : (int) minMax.max();
      final int step = minMax == null ? 1 : (int) minMax.step();
      final int v = value instanceof Number ? ((Number) value).intValue() : min;

      spinner.setModel(new SpinnerNumberModel(v, min, max, step));
      spinner.addChangeListener(new SimpleValueHandler(option, min));
    } else {
      final double min = minMax == null ? -Double.MAX_VALUE : minMax.min();
      final double max = minMax == null ? Double.MAX_VALUE : minMax.max();
      final double step = minMax == null ? 1.0 : minMax.step();
      final double v = value instanceof Number ? ((Number) value).doubleValue() : min;

      spinner.setModel(new SpinnerNumberModel(v, min, max, step));
      spinner.addChangeListener(new SimpleValueHandler(option, min));
    }

    final JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
    final JFormattedTextField jtf = editor.getTextField();
    jtf.setColumns(4);

    final JPanel spinnerPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    spinnerPane.add(spinner);
    return spinnerPane;
  }

  private JPanel newLabelPane( final String label ) {
    final JPanel labelPane = new JPanel(new GridBagLayout());

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = WEIGHT_NONE;
    gbc.weighty = WEIGHT_NONE;
    labelPane.add(newLabel(label, true), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = WEIGHT_ALL;
    labelPane.add(newSpacer(), gbc);

    return labelPane;
  }

  private JLabel newLabel( final String label, final boolean headline ) {
    final JLabel jl = new JLabel(label);
    if (headline) {
      final Font font = jl.getFont();
      jl.setFont(font.deriveFont(font.getSize() + 2.0f));
    } else {
      final Font font = jl.getFont();
      jl.setFont(font.deriveFont(Font.PLAIN));
    }
    return jl;
  }

  private JComponent newTextPane( final String text, final boolean html ) {
    final String contentType = html ? "text/html" : "text/plain";
    final JEditorPane jep = new JEditorPane(contentType, "");
    jep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    if (html) {
      jep.setText(wrap(text, TEXT_WIDTH));
    } else {
      jep.setText(text);
      jep.setPreferredSize(new Dimension(TEXT_WIDTH, 250));
    }
    jep.setFont(newDefaultFont(0));
    jep.setCaretPosition(0);
    jep.setEditable(false);
    jep.setOpaque(false);
    return jep;
  }

  /**
   * Wraps the given HTML snippet in a <code>&lt;div&gt;</code> block element
   * that restricts the display width of the HTML snippet to the given
   * preferred width.
   */
  private String wrap( final String text, final int preferredWidth ) {
    final int w = preferredWidth;
    return "<div width=\"" + w + "\">" + text + "</div>";
  }

  private JPanel newSpacer() {
    final JPanel spacer = new JPanel();
    spacer.setOpaque(false);
    return spacer;
  }

  private Font newDefaultFont( int inc ) {
    final Font font = new JLabel().getFont();
    return font.deriveFont(Font.PLAIN, font.getSize() + inc * 2.0f);
  }



  private static ConstraintManager find( final OptionGroup group ) {
    final Consumer<Object> setter = group.getSetter();
    if (setter instanceof NotifyValueChanged) {
      return ((NotifyValueChanged) setter).cm;
    }

    final ConstraintManager cm = findImpl(group);
    return cm == null ? new ConstraintManager() : cm;
  }

  private static ConstraintManager findImpl( final OptionGroup group ) {
    for (Option option : group.getChildOptions()) {
      final Consumer<Object> setter = option.getSetter();
      if (setter instanceof NotifyValueChanged) {
        return ((NotifyValueChanged) setter).cm;
      }

      if (option instanceof OptionGroup) {
        final ConstraintManager cm = findImpl((OptionGroup) option);
        if (cm != null) {
          return cm;
        }
      }
    }

    return null;
  }


  /**
   * Displays a colored line at the bottom of the associated component.
   */
  private static final class Separator extends AbstractBorder {
    int thickness;
    Color lineColor;

    Separator( final Color color ) {
      thickness = 1;
      lineColor = color;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      if ((this.thickness > 0) && (g instanceof Graphics2D)) {
        Graphics2D g2d = (Graphics2D) g;

        Color oldColor = g2d.getColor();
        g2d.setColor(this.lineColor);

        int offs = this.thickness;

        g2d.fill(new Rectangle2D.Float(x, y + height - offs, width, offs));
        g2d.setColor(oldColor);
      }
    }

    public Insets getBorderInsets(Component c, Insets insets) {
      insets.set(0, 0, thickness, 0);
      return insets;
    }
  }



  /**
   * Visualizes the collapsed and expanded states of an option group.
   */
  private static final class Arrow implements Icon {
    static final double FACTOR = Math.sqrt(0.75);
    static final int ARROW_SIZE = 9;
    static final int ICON_SIZE = 16;

    Color fillColor;
    boolean collapsed;

    Arrow( final Color color, final boolean collapsed ) {
      this.fillColor = color;
      this.collapsed = collapsed;
    }

    @Override
    public int getIconWidth() {
      return ICON_SIZE;
    }

    @Override
    public int getIconHeight() {
      return ICON_SIZE;
    }

    @Override
    public void paintIcon( Component c, Graphics g, int x, int y ) {
      final int length = (int) Math.round(ARROW_SIZE * FACTOR);

      final GeneralPath path = new GeneralPath();

      if (collapsed) {
        final int xOffset = (ICON_SIZE - length) / 2;
        final int yOffset = (ICON_SIZE - ARROW_SIZE) / 2;

        path.moveTo(x + xOffset, y + yOffset + ARROW_SIZE);
        path.lineTo(x + xOffset, y + yOffset);
        path.lineTo(x + xOffset + length, y + yOffset + ARROW_SIZE / 2);
        path.closePath();
      } else {
        final int xOffset = (ICON_SIZE - ARROW_SIZE) / 2;
        final int yOffset = (ICON_SIZE - length) / 2;

        path.moveTo(x + xOffset, y + yOffset);
        path.lineTo(x + xOffset + ARROW_SIZE, y + yOffset);
        path.lineTo(x + xOffset + ARROW_SIZE / 2, y + yOffset + length);
        path.closePath();
      }

      final Graphics2D gfx = (Graphics2D) g.create();
      gfx.setRenderingHint(
              RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      gfx.setColor(fillColor);
      gfx.fill(path);
      gfx.dispose();
    }

    boolean isCollapsed() {
      return collapsed;
    }

    void setCollapsed( final boolean collapsed ) {
      this.collapsed = collapsed;
    }
  }

  /**
   * Converts the <code>selected</code> state of a check box to a value for the
   * associated option.
   */
  private static final class CheckBoxHandler implements ItemListener {
    final Option option;

    CheckBoxHandler( final Option option ) {
      this.option = option;
    }

    @Override
    public void itemStateChanged( final ItemEvent e ) {
      final JCheckBox src = (JCheckBox) e.getSource();
      option.setValue(src.isSelected() ? Boolean.TRUE : Boolean.FALSE);
    }
  }

  /**
   * Passes the value in a combo box to the associated option.
   */
  private static final class ComboBoxHandler implements ItemListener {
    final Option option;

    ComboBoxHandler( final Option option ) {
      this.option = option;
    }

    @Override
    public void itemStateChanged( final ItemEvent e ) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        option.setValue(e.getItem());
      }
    }
  }

  /**
   * Displays text values for arbitrary value types in combo boxes.
   */
  private static final class MappedRenderer implements ListCellRenderer<Object> {
    final Map map;
    final DefaultListCellRenderer r;

    MappedRenderer( Map map ) {
      this.map = map;
      this.r = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
      Object label = map.get(value);
      return r.getListCellRendererComponent(
              list, label == null ? value : label, index, isSelected, cellHasFocus);
    }
  }



  /**
   * Abstract base class for synchronizing values between a {@link JSlider} and
   * a {@link JSpinner} instance. 
   */
  private abstract static class AbstractValueHandler implements ChangeListener {
    final Option option;
    final JSlider slider;
    final JSpinner spinner;

    boolean armed;

    AbstractValueHandler(
            final Option option, final JSlider slider, final JSpinner spinner
    ) {
      this.option = option;
      this.slider = slider;
      this.spinner = spinner;
      this.armed = true;
    }

    @Override
    public void stateChanged( final ChangeEvent e ) {
      armed = false;
      try {
        stateChangedCore(e);
      } finally {
        armed = true;
      }
    }

    abstract void stateChangedCore( ChangeEvent e );
  }

  /**
   * Synchronizes the current value of a {@link JSlider} and a {@link JSpinner}
   * instance that display <code>double</code> values.
   */
  private static final class DoubleValueHandler extends AbstractValueHandler {
    final double min;
    final double step;

    DoubleValueHandler(
            final Option option,
            final JSlider slider, final JSpinner spinner,
            final double min, final double step
    ) {
      super(option, slider, spinner);
      this.min = min;
      this.step = step;
    }

    @Override
    void stateChangedCore( final ChangeEvent e ) {
      final Object src = e.getSource();
      if (src instanceof JSlider) {
        final int sv = ((JSlider) src).getValue();
        final Double value = Double.valueOf(sv * step);
        option.setValue(value);
        spinner.setValue(value);
      } else if (src instanceof JSpinner) {
        final Object ov = ((JSpinner) src).getValue();
        final double pv = ov instanceof Number ? ((Number) ov).doubleValue() : min;
        final Double value = Double.valueOf(pv);
        option.setValue(value);
        slider.setValue((int) Math.rint(pv / step));
      }
    }
  }

  /**
   * Synchronizes the current value of a {@link JSlider} and a {@link JSpinner}
   * instance that display <code>int</code> values.
   */
  private static final class IntValueHandler extends AbstractValueHandler {
    final int min;
    final int step;

    IntValueHandler(
            final Option option,
            final JSlider slider, final JSpinner spinner,
            final int min, final int step
    ) {
      super(option, slider, spinner);
      this.min = min;
      this.step = step;
    }

    @Override
    void stateChangedCore( final ChangeEvent e ) {
      final Object src = e.getSource();
      if (src instanceof JSlider) {
        final int sv = ((JSlider) src).getValue();
        final Integer value = Integer.valueOf(sv * step);
        option.setValue(value);
        spinner.setValue(value);
      } else if (src instanceof JSpinner) {
        final Object ov = ((JSpinner) src).getValue();
        final int pv = ov instanceof Number ? ((Number) ov).intValue() : min;
        final Integer value = Integer.valueOf(pv);
        option.setValue(value);
        slider.setValue((int) Math.rint(pv / step));
      }
    }
  }

  /**
   * Propagates numeric values of a {@link JSpinner} instance to the associated
   * numeric {@link Option} instance.
   */
  private static final class SimpleValueHandler implements ChangeListener {
    final Option option;
    final Number min;
    final boolean integral;

    SimpleValueHandler( final Option option, final int min ) {
      this.option = option;
      this.min = Integer.valueOf(min);
      this.integral = true;
    }

    SimpleValueHandler( final Option option, final double min ) {
      this.option = option;
      this.min = Double.valueOf(min);
      this.integral = false;
    }

    @Override
    public void stateChanged( final ChangeEvent e ) {
      final JSpinner src = (JSpinner) e.getSource();
      option.setValue(convert(src.getValue()));
    }

    /**
     * Forces the given value to a specific numerical type (either
     * {@link Integer} or {@link Double}).
     */
    Number convert( final Object value ) {
      if (integral) {
        final int primitive = value instanceof Number
                ? ((Number) value).intValue() : min.intValue();
        return Integer.valueOf(primitive);
      } else {
        final double primitive = value instanceof Number
                ? ((Number) value).doubleValue() : min.doubleValue();
        return Double.valueOf(primitive);
      }
    }
  }


  /**
   * Collapses option groups on mouse clicks.
   */
  private static final class CollapseHandler extends MouseAdapter {
    final JPanel collapsible;

    CollapseHandler( final JPanel collapsible ) {
      this.collapsible = collapsible;
    }

    @Override
    public void mouseEntered( final MouseEvent e ) {
      final JComponent jc = (JComponent) e.getSource();
      jc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseClicked( final MouseEvent e ) {
      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
        final JComponent jc = (JComponent) e.getSource();
        final JLabel jl = (JLabel) jc.getParent().getComponent(0);
        final Arrow icon = (Arrow) jl.getIcon();
        final boolean collapsed = !icon.isCollapsed();
        icon.setCollapsed(collapsed);
        jl.repaint();

        final LayoutManager layout = collapsible.getLayout();
        if (layout instanceof EditorLayout) {
          ((EditorLayout) layout).setCollapsed(collapsible, collapsed);
        }
      }
    }
  }


  /**
   * Layout manager that arranges components in a single column and supports
   * hiding and unhiding all components but the first.
   */
  private static class EditorLayout implements LayoutManager {
    Insets offset;
    boolean collapsed;

    EditorLayout() {
      final int i = DEFAULT_INSET;
      offset = new Insets(i, i, i, i);
    }

    @Override
    public void addLayoutComponent( final String name, final Component comp ) {
      final Container parent = comp.getParent();
      if (parent.getComponent(0) == comp) {
        comp.setVisible(true);
      } else {
        comp.setVisible(!collapsed);
      }
    }

    @Override
    public void removeLayoutComponent( final Component comp ) {
    }

    @Override
    public Dimension preferredLayoutSize( final Container parent ) {
      synchronized (parent.getTreeLock()) {
        return _preferredLayoutSize(parent);
      }
    }

    private Dimension _preferredLayoutSize( final Container parent ) {
      final Dimension size = new Dimension();

      final Insets offset = getOffset();
      final int n = parent.getComponentCount();
      if (n > 0) {
        final Dimension d = parent.getComponent(0).getPreferredSize();
        size.width = d.width;
        size.height = d.height + offset.top;

        final boolean expanded = !isCollapsed(parent);
        for (int i = 1; i < n; ++i) {
          final Dimension e = parent.getComponent(i).getPreferredSize();
          size.width = Math.max(size.width, e.width);
          if (expanded) {
            size.height += e.height + offset.bottom;
          }
        }
        if (!expanded) {
          size.height += offset.bottom;
        }
      }

      final Insets insets = parent.getInsets();
      size.width += insets.left + insets.right + offset.left + offset.right;
      size.height += insets.top + insets.bottom;

      return size;
    }

    @Override
    public Dimension minimumLayoutSize( final Container parent ) {
      synchronized (parent.getTreeLock()) {
        return _minimumLayoutSize(parent);
      }
    }

    private Dimension _minimumLayoutSize( final Container parent ) {
      final Dimension size = new Dimension();

      final Insets offset = getOffset();
      final int n = parent.getComponentCount();
      if (n > 0) {
        final Dimension d0 = parent.getComponent(0).getMinimumSize();
        size.width = d0.width;
        size.height = d0.height + offset.top;

        final boolean expanded = !isCollapsed(parent);
        for (int i = 1; i < n; ++i) {
          final Dimension d1 = parent.getComponent(i).getMinimumSize();
          size.width = Math.max(size.width, d1.width);
          if (expanded) {
            size.height += d1.height + offset.bottom;
          }
        }
        if (!expanded) {
          size.height += offset.bottom;
        }
      }

      final Insets insets = parent.getInsets();
      size.width += insets.left + insets.right + offset.left + offset.right;
      size.height += insets.top + insets.bottom;

      return size;
    }

    @Override
    public void layoutContainer( final Container parent ) {
      synchronized (parent.getTreeLock()) {
        _layoutContainer(parent);
      }
    }

    private void _layoutContainer( final Container parent ) {
      final int n = parent.getComponentCount();
      if (n == 0) {
        return;
      }

      final Insets offset = getOffset();
      final Insets insets = parent.getInsets();
      int top = insets.top + offset.top;
      final int left = insets.left + offset.left;
      final int right = parent.getWidth() - insets.right - offset.right;

      for (int i = 0; i < n; ++i) {
        final Component c = parent.getComponent(i);
        c.setSize(right - left, c.getHeight());
        final Dimension d = c.getPreferredSize();
        c.setBounds(left, top, right - left, d.height);
        top += d.height + offset.bottom;
      }
    }

    boolean isCollapsed( final Container parent ) {
      return collapsed && parent.getLayout() == this;
    }

    void setCollapsed( final Container parent, final boolean collapsed ) {
      if (parent.getLayout() == this) {
        this.collapsed = collapsed;

        synchronized (parent.getTreeLock()) {
          final int n = parent.getComponentCount();
          if (n > 0) {
            parent.getComponent(0).setVisible(true);
            for (int i = 1; i < n; ++i) {
              parent.getComponent(i).setVisible(!collapsed);
            }
            parent.revalidate();
          } 
        }
      }
    }

    Insets getOffset() {
      return offset;
    }

    void setOffset( final Insets offset ) {
      this.offset = offset;
    }
  }


  /**
   * Handles enabled state changes on value changed events.
   */
  private static final class ConstraintManager {
    final List<Runnable> listeners;
    boolean enabled;

    ConstraintManager() {
      listeners = new ArrayList<Runnable>();
      enabled = true;
    }

    void addValueChangedListener( final Runnable l ) {
      listeners.add(l);
    }

    void valueChanged() {
      if (isEnabled()) {
        for (Runnable l : listeners) {
          l.run();
        }
      }
    }

    void setEnabled( final boolean enabled ) {
      this.enabled = enabled;
    }

    boolean isEnabled() {
      return enabled;
    }
  }

  /**
   * Sets the enabled state of the associated component depending on the
   * value of the associated supplier.
   */
  private static final class SetEnabled implements Runnable {
    final Supplier<Boolean> disabled;
    final JComponent component;

    SetEnabled( final JComponent component, final Supplier<Boolean> disabled ) {
      this.disabled = disabled;
      this.component = component;
    }

    @Override
    public void run() {
      setEnabled(!Boolean.TRUE.equals(disabled.get()));
    }

    void setEnabled( final boolean enabled ) {
      component.setEnabled(enabled);
    }
  }

  /**
   * Notifies the associated constraint manager whenever a new option value
   * is set.
   */
  private static final class NotifyValueChanged implements Consumer<Object> {
    final Consumer<Object> setter;
    final ConstraintManager cm;

    NotifyValueChanged(
            final Consumer<Object> setter,
            final ConstraintManager cm
    ) {
      this.setter = setter;
      this.cm = cm;
    }

    @Override
    public void accept( final Object o ) {
      setter.accept(o);
      cm.valueChanged();
    }
  }
}
