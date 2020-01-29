/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.view.CanvasComponent;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Displays an error dialog with the option to send an error report to yWorks GmbH detailing
 * the error with stacktrace as well as java and os specifics, an email address for inquiries
 * and optionally a user comment.
 */
class ErrorReporter implements Runnable {
  // Some constants to use for the UI
  private static final String REQUESTING_EMAIL_ADDRESS =
          "Please consider adding your email address in your report.\n" +
          "Your email address gives us the possibility to get in contact with you regarding your report. \n" +
          "This can help us in fixing reported issues. \n" +
          "We will never use your email address for any other purpose.";
  private static final String SENDING_REPORT_FAILED =
          "Unfortunately there was a problem sending your report.\n" +
          "Please try again later or contact us via yfilesjava@yworks.com";
  private static final String SENDING_ERROR_REPORT_SUCCESS =
          "Thank you for helping us improve yFiles for Java (Swing).";


  private final JFrame frame;
  private final Throwable error;
  private final String title;

  private final String boundary;

  ErrorReporter( JFrame frame, Throwable error, String title ) {
    this.frame = frame;
    this.error = error;
    this.title = title;
    this.boundary = (int) (Math.random() * 100000.0) + "boundary" + (int) (Math.random() * 100000.0);
  }

  @Override
  public void run() {
    int extraOffset = 8;
    GridBagConstraints gbc = new GridBagConstraints();
    Insets defaultInsets = gbc.insets;
    Insets rightSmall = new Insets(defaultInsets.top, defaultInsets.left, defaultInsets.bottom, defaultInsets.right + extraOffset);
    Insets bottomSmall = new Insets(defaultInsets.top, defaultInsets.left, defaultInsets.bottom + extraOffset, defaultInsets.right);
    Insets bottomLarge = new Insets(bottomSmall.top, bottomSmall.left, bottomSmall.bottom + extraOffset, bottomSmall.right);


    // begin first card: user information
    JButton detailsJb = new JButton("Show Details");
    JButton sendJb = new JButton("Send Report");
    JButton ignoreJb = new JButton("Ignore");

    JPanel contentPane = new JPanel(new ContentPaneLayout());
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = bottomLarge;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    contentPane.add(newLabel(
            "The following exception occurred:\n" + getErrorMessage()),
                    gbc);

    ++gbc.gridy;
    gbc.insets = bottomSmall;
    contentPane.add(newLabel(
            "We apologize for the error, this shouldn't happen.\n" +
            "Please help us fixing this issue by sending this error report."),
                    gbc);

    gbc.fill = GridBagConstraints.NONE;
    ++gbc.gridy;
    gbc.gridwidth = 1;
    gbc.insets = rightSmall;
    contentPane.add(new JLabel("E-Mail:"), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridx;
    gbc.insets = bottomSmall;
    gbc.weightx = 1;
    JTextField emailJtf = new JTextField();
    emailJtf.setToolTipText(format(REQUESTING_EMAIL_ADDRESS));
    contentPane.add(emailJtf, gbc);

    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.insets = rightSmall;
    gbc.weightx = 0;
    JTextArea commentJta = new JTextArea();
    contentPane.add(new JLabel("Comment:"), gbc);

    gbc.fill = GridBagConstraints.BOTH;
    ++gbc.gridx;
    gbc.insets = bottomLarge;
    gbc.weightx = 1;
    gbc.weighty = 1;
    JScrollPane commentJsp = new JScrollPane(commentJta);
    commentJsp.setPreferredSize(new Dimension(384, 192));
    contentPane.add(commentJsp, gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 2;
    gbc.insets = defaultInsets;
    gbc.weighty = 0;
    JLabel detailsJl = new JLabel("The exception stacktrace was:");
    detailsJl.setVisible(false);
    contentPane.add(detailsJl, gbc);

    gbc.fill = GridBagConstraints.BOTH;
    ++gbc.gridy;
    gbc.insets = bottomLarge;
    gbc.weighty = 1;
    JTextArea detailsJta = new JTextArea(dump(error));
    detailsJta.setEditable(false);
    JScrollPane detailsJsp = new JScrollPane(detailsJta);
    detailsJsp.setPreferredSize(new Dimension(384, 192));
    detailsJsp.setVisible(false);
    contentPane.add(detailsJsp, gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.insets = defaultInsets;
    gbc.weightx = 1;
    gbc.weighty = 0;
    JPanel buttonPane = new JPanel(new BorderLayout());
    buttonPane.add(detailsJb, BorderLayout.WEST);
    buttonPane.add(newButtonPane(sendJb, ignoreJb), BorderLayout.EAST);
    contentPane.add(buttonPane, gbc);
    // end first card: user information


    // begin second card: email request explanation
    JButton requestJb = new JButton("Ok");

    JPanel requestPane = new JPanel(new GridBagLayout());
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets = bottomLarge;
    gbc.weightx = 0;
    gbc.weighty = 0;
    requestPane.add(newLabel("Missing or invalid email address"), gbc);

    ++gbc.gridy;
    requestPane.add(newLabel(REQUESTING_EMAIL_ADDRESS), gbc);

    gbc.fill = GridBagConstraints.BOTH;
    ++gbc.gridy;
    gbc.insets = defaultInsets;
    gbc.weightx = 1;
    gbc.weighty = 1;
    requestPane.add(new JPanel(), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.weighty = 0;
    requestPane.add(newButtonPane(requestJb), gbc);
    // end second card: email request explanation


    // begin third card: send progress
    JButton hideJb = new JButton("Hide");

    JPanel sendProgressPane = new JPanel(new GridBagLayout());
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.insets = bottomLarge;
    gbc.weightx = 0;
    gbc.weighty = 0;
    sendProgressPane.add(newLabel("Sending error report..."), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.weightx = 1;
    JProgressBar jpb = new JProgressBar();
    jpb.setIndeterminate(true);
    sendProgressPane.add(jpb, gbc);

    gbc.fill = GridBagConstraints.BOTH;
    ++gbc.gridy;
    gbc.insets = defaultInsets;
    gbc.weightx = 1;
    gbc.weighty = 1;
    sendProgressPane.add(new JPanel(), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.weighty = 0;
    sendProgressPane.add(newButtonPane(hideJb), gbc);
    // end third card: send progress


    // begin fourth card: error report successfully sent to yWorks
    JButton closeAfterSuccessJb = new JButton("Ok");
    JComponent sendSuccessPane = newSendResultPane(
            "Error report sent", SENDING_ERROR_REPORT_SUCCESS,
            closeAfterSuccessJb,
            bottomLarge, defaultInsets);
    // end fourth card: error report successfully sent to yWorks

    // begin fifth card: sending error report failed
    JButton closeAfterFailureJb = new JButton("Ok");
    JComponent sendFailurePane = newSendResultPane(
            "Sending error report failed", SENDING_REPORT_FAILED,
            closeAfterFailureJb,
            bottomLarge, defaultInsets);
    // end fifth card: sending error report failed


    // putting it all together
    JPanel stackPane = new JPanel(new CardLayout());
    stackPane.add(contentPane, "CONTENT");
    stackPane.add(requestPane, "REQUEST_EMAIL");
    stackPane.add(sendProgressPane, "SEND_PROGRESS");
    stackPane.add(sendSuccessPane, "SEND_SUCCESS");
    stackPane.add(sendFailurePane, "SEND_FAILURE");


    JOptionPane jop = new JOptionPane(stackPane, JOptionPane.ERROR_MESSAGE);
    jop.setOptions(new Object[0]);
    JDialog jd = jop.createDialog(frame, "Report Error");
    jd.setModal(true);
    jd.setResizable(true);
    jd.getRootPane().setDefaultButton(sendJb);


    // begin controllers
    CloseHandler closeHandler = new CloseHandler(jd);

    detailsJb.addActionListener((e) -> {
      boolean show = !detailsJl.isVisible();
      detailsJl.setVisible(show);
      detailsJsp.setVisible(show);

      ((JButton) e.getSource()).setText(show ? "Hide Details" : "Show Details");

      ContentPaneLayout layout = (ContentPaneLayout) detailsJl.getParent().getLayout();
      int detailsHeight =
              detailsJl.getPreferredSize().height +
              layout.getVerticalInsets(detailsJl) +
              detailsJsp.getPreferredSize().height +
              layout.getVerticalInsets(detailsJsp);

      Dimension size = jd.getSize();
      jd.setSize(size.width, size.height + (show ? 1 : -1) * detailsHeight);
    });
    sendJb.addActionListener(new SendHandler(jd, stackPane, emailJtf, commentJta));
    ignoreJb.addActionListener(closeHandler);
    requestJb.addActionListener((e) -> {
      stackPane.putClientProperty("ErrorReporter.forceSend", Boolean.TRUE);
      ((CardLayout) stackPane.getLayout()).show(stackPane, "CONTENT");
    });
    hideJb.addActionListener(e -> jd.setVisible(false));
    closeAfterSuccessJb.addActionListener(closeHandler);
    closeAfterFailureJb.addActionListener(closeHandler);
    // end controllers


    jd.setVisible(true);
  }

  /**
   * Creates HTML formatted labels for displaying multiple lines of text.
   */
  private static JLabel newLabel( String s ) {
    JLabel jl = new JLabel(format(s));
    jl.setFont(jl.getFont().deriveFont(Font.PLAIN));
    return jl;
  }

  /**
   * Creates a container for several buttons with equal sizes.
   */
  private static JComponent newButtonPane( JComponent... buttons ) {
    JPanel buttonPane = new JPanel(new GridLayout(1, buttons.length, 8, 0));
    for (int i = 0; i < buttons.length; ++i) {
      buttonPane.add(buttons[i]);
    }
    JPanel flowPane = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
    flowPane.add(buttonPane);
    return flowPane;
  }

  /**
   * Creates a component for displaying the results (success/failure) of sending
   * the error report to the user. 
   */
  private static JComponent newSendResultPane(
          String header, String message,
          JComponent closeButton,
          Insets bottomLarge, Insets defaultInsets
  ) {
    JPanel resultPane = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = bottomLarge;
    gbc.weightx = 0;
    gbc.weighty = 0;
    resultPane.add(newLabel(header), gbc);

    ++gbc.gridy;
    resultPane.add(newLabel(message), gbc);

    gbc.fill = GridBagConstraints.BOTH;
    ++gbc.gridy;
    gbc.insets = defaultInsets;
    gbc.weightx = 1;
    gbc.weighty = 1;
    resultPane.add(new JPanel(), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.weighty = 0;
    resultPane.add(newButtonPane(closeButton), gbc);

    return resultPane;
  }

  /**
   * Chops up the possibly long error message (inserts newline escape characters after every 140 characters)
   */
  private String getErrorMessage() {
    int wrappingWidth = 140;
    String message = error.getMessage();
    if (message == null) {
      return "null";
    } else {
      StringBuilder result = new StringBuilder();
      for (int i = 0, n = message.length(); i < n; i += wrappingWidth) {
        result.append(message.substring(i, i + Math.min(wrappingWidth, n - i)));
        result.append("\n");
      }
      return result.toString();
    }
  }

  /**
   * Converts the given plain text into HTML-formatted text with explicit
   * &lt;br&gt; elements for each newline character in the original text.
   */
  private static String format( String s ) {
    StringBuilder sb = new StringBuilder(s.length() + 128);
    sb.append("<html><head></head><body><p>\n");
    for (int i = 0, n = s.length(); i < n; ++i) {
      final char c = s.charAt(i);
      switch (c) {
        case '\n':
          sb.append("<br>\n");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        default:
          sb.append(c);
          break;
      }
    }
    sb.append("</p></body></html>");
    return sb.toString();
  }

  /**
   * Returns the stacktrace text of the given error.
   */
  private static String dump( Throwable error ) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    error.printStackTrace(pw);
    return sw.toString();
  }

  /**
   * Sends the error report with the given email and comment of the user.
   * Opens a {@link java.net.HttpURLConnection} and writes the error report
   * to its output stream. Retrieves the response and returns it.
   * @return the response of the server
   */
  private String send( String email, String comment ) throws IOException {
    HashMap<String, String> properties = new HashMap<String, String>();

    // data of the user
    properties.put("user_email", email);
    properties.put("user_comment", comment);

    // data of the yFiles version and demo
    properties.put("url", this.title + " / " + error.toString());
    properties.put("exact_product", "yFiles for Java (Swing)");
    properties.put("product_distribution", CanvasComponent.class.getPackage().getImplementationTitle());
    properties.put("product_version", CanvasComponent.class.getPackage().getImplementationVersion());

    // data of the used java version and the os
    addSystemInfo(properties, "java.version");
    addSystemInfo(properties, "java.vendor");
    addSystemInfo(properties, "java.vm.name");
    addSystemInfo(properties, "os.name");
    addSystemInfo(properties, "os.arch");
    addSystemInfo(properties, "os.version");


    final URL phpMailer = new URL("http://kb.yworks.com/errorFeedback.html");

    /* connect to given URL */
    final HttpURLConnection con = (HttpURLConnection) phpMailer.openConnection();

    /* initialize post */
    con.setRequestProperty( "Content-Type", "multipart/form-data; boundary=" + boundary );
    con.setDoOutput( true );
    con.setDoInput( true );
    con.setUseCaches( false );
    con.setRequestMethod( "POST" );
    con.connect();

    try {
      return sendImpl(con, properties);
    } finally {
      con.disconnect();
    }
  }

  /**
   * Writes the data to the outputstream of the connection and returns the response.
   * @return the response of the server for the request.
   */
  private String sendImpl(
          HttpURLConnection con, Map<String, String> properties
  ) throws IOException {

    /* write post data to output */
    try (OutputStream out = con.getOutputStream()) {
      writeContent(out, properties);
      out.flush();
    }

    /* get response */
    final StringBuilder response = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        response.append( line );
        response.append( '\n' );
      }
    }
    return response.toString();
  }

  /**
   * Writes all properties in the appropriate format to the output stream.
   */
  private void writeContent(
          OutputStream os, Map<String, String> properties
  ) throws UnsupportedEncodingException {
    final PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
    out.write( "\n\n" );
    for ( Map.Entry<String, String> entry : properties.entrySet() ) {
      out.println("--" + boundary);
      final String name = entry.getKey();
      final String value = entry.getValue();
      String message = "Content-Disposition: form-data; name=\"error_dialog_" + name + '\"';
      out.println(message);
      out.println();
      out.println(value);
    }
    // add stacktrace
    out.println("--" + boundary);
    out.println("Content-Disposition: form-data; name=\"error_dialog_stacktrace\"");
    out.println();
    error.printStackTrace(out);
    out.println("--" + boundary + "--" );

    out.flush();
  }

  /**
   * Adds the result of {@link java.lang.System#getProperty(String)}
   * with the given key to the given map.
   */
  private void addSystemInfo(Map<String, String> map, String key) {
    try {
      map.put(key, System.getProperty(key));
    } catch (Exception ignored) {
    }
  }


  /**
   * Provides access to the vertical insets of components managed by this
   * layout manager.
   */
  private static final class ContentPaneLayout extends GridBagLayout {
    int getVerticalInsets( JComponent c ) {
      Insets insets = getInsets(c);
      return insets == null ? 0 : insets.top + insets.bottom;
    }

    private Insets getInsets( JComponent c ) {
      GridBagConstraints gbc = lookupConstraints(c);
      return gbc == null ? new GridBagConstraints().insets : gbc.insets;
    }
  }

  /**
   * Hides and disposes dialogs.
   */
  private static final class CloseHandler implements ActionListener {
    private final JDialog jd;

    CloseHandler( JDialog jd ) {
      this.jd = jd;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
      jd.setVisible(false);
      jd.dispose();
    }
  }

  /**
   * Handles the actual sending of error reports to yWorks GmbH. 
   */
  private final class SendHandler implements ActionListener {
    private final JDialog jd;
    private final JComponent stackPane;
    private final JTextField emailJtf;
    private final JTextArea commentJta;

    private boolean reportSent;

    SendHandler(
            JDialog jd, JComponent stackPane, JTextField emailJtf, JTextArea commentJta
    ) {
      this.jd = jd;
      this.stackPane = stackPane;
      this.emailJtf = emailJtf;
      this.commentJta = commentJta;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
      String email = emailJtf.getText();
      if (!email.contains("@") &&
          !Boolean.TRUE.equals(stackPane.getClientProperty("ErrorReporter.forceSend"))) {
        showCard(stackPane, "REQUEST_EMAIL");
      } else {
        showCard(stackPane, "SEND_PROGRESS");

        reportSent = false;

        new Thread(() -> {
          try {
            send(email, commentJta.getText());
            reportSent = true;
          } catch (Exception ex) {
            // ignore
          }

          EventQueue.invokeLater(() -> {
            if (!jd.isVisible()) {
              jd.setVisible(true);
            }
            showCard(stackPane, reportSent ? "SEND_SUCCESS" : "SEND_FAILURE");
          });
        }).start();
      }
    }

    private void showCard( JComponent container, String cardId ) {
      ((CardLayout) container.getLayout()).show(container, cardId);
    }
  }
}
