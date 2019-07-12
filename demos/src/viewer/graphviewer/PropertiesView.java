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
package viewer.graphviewer;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Provides a view of the properties of a node using a JPanel that contains an JEditorPane which displays html.
 * The JPanel can be acquired by the method #getContenPane.
 * The main method of this class is {@link #showProperties(String, String, String)} which takes several properties
 * and builds html markup to display those properties in the JEditorPane.
 * The html is build using an {@link javax.xml.stream.XMLStreamWriter}.
 */
public class PropertiesView {

  /**
   * The content pane that contains the JEditorPane that displays the properties.
   */
  private JPanel contentPane = new JPanel(new GridLayout(1, 1));

  /**
   * The JEditorPane that displays the html for the properties of an employee.
   */
  private JEditorPane editorPane = new JEditorPane();

  /**
   * The pre-text to display as default.
   */
  private static final String PRE_TEXT = "<html><body style='font-family:\"Segoe UI\"; font-size:14;'>Focus a node in the graph component to show its properties.</body></html>";

  /**
   * Instantiates a new view and sets up the necessary swing widgets. After the constructor is called JPanel to use in an application is available via #getContentPane().
   */
  public PropertiesView() {

    createEditorPane();

    JScrollPane scrollPane = new JScrollPane(editorPane);
    contentPane.add(scrollPane);

    contentPane.setBorder(BorderFactory.createTitledBorder("Properties"));
    contentPane.setPreferredSize(new Dimension(250, 350));
    editorPane.setText(PRE_TEXT);
    editorPane.setCaretPosition(0);
  }

  /**
   * Creates the editor pane that displays the html.
   */
  private void createEditorPane() {
    editorPane.setEditable(false);
    editorPane.setContentType("text/html");

    // define the css style for the content
    HTMLEditorKit kit = new HTMLEditorKit();
    StyleSheet styleSheet = kit.getStyleSheet();
    styleSheet.addRule("dt { font-weight: bold; margin-top: 4px; color: #333333; }");
    styleSheet.addRule("dt > i { font-weight: normal; margin-top: 4px; color: #333333; }");
    styleSheet.addRule("dd { margin-left: 10px; }");
    editorPane.setEditorKit(kit);

    // register a hyperlink listener that actually opens the given url in a browser.
    editorPane.addHyperlinkListener(e -> {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        try {
          GraphViewerDemo.openUrlInBrowser(e.getURL());
        } catch (URISyntaxException | IOException e1) {
          e1.printStackTrace();
        }
      }
    });
  }

  /**
   * Returns the content pane to use in a swing application.
   */
  public JPanel getContentPane(){
    return contentPane;
  }

  /**
   * Resets the properties view to show the predefined text when nothing is focused.
   */
  public void reset() {
    editorPane.setText(PRE_TEXT);
  }

  /**
   * Builds the html to display the properties of the employee that is the tag of the given node and updates the view.
   * This method uses a {@link javax.xml.stream.XMLStreamWriter} to build the html document.
   */
  public void showProperties(String label, String description, String link) {

    boolean labelAvailable = !label.equals("Empty");
    boolean descriptionAvailable = !description.equals("Empty");
    boolean linkAvailable = !link.equals("None");

    if (!labelAvailable && !descriptionAvailable && !linkAvailable ) {
      // there is actually nothing to display, restore the original pre text.
      reset();
      return;
    }

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      // set up an XML writer to build the html more conveniently
      XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
      XMLStreamWriter xml = outputFactory.createXMLStreamWriter(baos);

      // set up the document
      xml.writeStartDocument();
      xml.writeStartElement("html");
      xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
      xml.writeStartElement("body");
      xml.writeAttribute("style", "font-family:'Segoe UI'; font-size:14;");

      xml.writeStartElement("dl");


      if (labelAvailable) {
        writeDescriptionListElement(xml, "Label", label);
      }
      if (descriptionAvailable) {
        writeDescriptionListElement(xml, "Description", description);
      }
      if (linkAvailable) {
        writeElement(xml, "dt", "Link");
        xml.writeStartElement("dd");
        createLinkEntry(xml, link);
        xml.writeEndElement();
      }

      xml.writeEndElement(); // dl
      xml.writeEndElement(); // body
      xml.writeEndElement(); // html
      xml.writeEndDocument(); // we're done
      xml.flush();
      editorPane.setText(baos.toString().replaceFirst("<\\?.*\\?>", "")); // remove the xml tag that the XMLStreamWriter produces because the JEditorPane doesn't support xhtml
      // reset the caret position to scroll to top
      editorPane.setCaretPosition(0);
      xml.close();
      baos.close();
    } catch (XMLStreamException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Convenience method that writes a dt tag with the given title as content and a dd tag with the given description as content on the given XMLStreamWriter.
   */
  private void writeDescriptionListElement(XMLStreamWriter xml, String title, String description) throws XMLStreamException {
    writeElement(xml, "dt", title);
    writeElement(xml, "dd", description);
  }

  /**
   * Writes a DOM element with the specified text content.
   */
  private void writeElement(XMLStreamWriter xml, String tagName, String textContent) throws XMLStreamException {
    xml.writeStartElement(tagName);
    writeText(xml, textContent);
    xml.writeEndElement();
  }

  /**
   * Writes multiline text by splitting up the given text content at the newline escape characters and writes line by line separated by break tags.
   */
  private void writeText(XMLStreamWriter xml, String textContent) throws XMLStreamException {
    String[] lines = textContent.split("\\n");
    for (int i = 0; i < lines.length; ) {
      String line = lines[i];
      xml.writeCharacters(line);
      if (i++ < line.length()) {
        xml.writeStartElement("br");
      }
    }
  }

  /**
   * Creates a hyperlink element with the given destination.
   * Clicking a link will bring up the browser and navigate to that address.
   */
  private void createLinkEntry(XMLStreamWriter xml, String link) throws XMLStreamException {
    xml.writeStartElement("a");
    xml.writeAttribute("href", link);
    xml.writeCharacters("External");
    xml.writeEndElement();
  }
}

