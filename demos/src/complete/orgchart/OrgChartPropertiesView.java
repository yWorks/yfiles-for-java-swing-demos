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
package complete.orgchart;

import com.yworks.yfiles.graph.INode;

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
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a view of the properties of an employee using a JPanel that contains an JEditorPane which displays html.
 * The JPanel can be acquired by the method #getContenPane.
 * The main method of this class is {@link #showProperties(com.yworks.yfiles.graph.INode)} which takes an INode
 * and configures html to display in the JEditorPane which depicts the properties of the employee tag of the INode.
 * The html is build using an {@link javax.xml.stream.XMLStreamWriter}.
 */
public class OrgChartPropertiesView {

  /**
   * The content pane that contains the JEditorPane that displays the properties.
   */
  private JPanel contentPane = new JPanel(new GridLayout(1, 1));

  /**
   * The JEditorPane that displays the html for the properties of an employee.
   */
  private JEditorPane editorPane = new JEditorPane();


  public OrgChartPropertiesView(OrgChartDemo controller) {

    createEditorPane(controller);

    JScrollPane scrollPane = new JScrollPane(editorPane);
    contentPane.add(scrollPane);

    contentPane.setBorder(BorderFactory.createTitledBorder("Properties"));
    contentPane.setPreferredSize(new Dimension(250, 350));
    String preText = "<html><body style='font-family:\"Segoe UI\"; font-size:14;'>Focus an employee in the organization chart to show his properties.</body></html>";
    editorPane.setText(preText);
    editorPane.setCaretPosition(0);
  }

  /**
   * Creates the editor pane that displays the html.
   */
  private void createEditorPane(OrgChartDemo controller) {
    editorPane.setEditable(false);
    editorPane.setContentType("text/html");

    // define the css style for the content
    HTMLEditorKit kit = new HTMLEditorKit();
    StyleSheet styleSheet = kit.getStyleSheet();
    styleSheet.addRule("dt { font-weight: bold; margin-top: 4px; color: #333333; }");
    styleSheet.addRule("dt > i { font-weight: normal; margin-top: 4px; color: #333333; }");
    styleSheet.addRule("dd { margin-left: 10px; }");
    editorPane.setEditorKit(kit);

    // register a hyperlink listener to react to clicks on the employee links by calling a method in the OrgChartDemo class.
    editorPane.addHyperlinkListener(e -> {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        controller.focusAndZoomToNodeWithEmail(e.getDescription());
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
   * Builds the html to display the properties of the employee stored in the tag of the given node and updates the view.
   * This method uses an {@link javax.xml.stream.XMLStreamWriter} to build the html document.
   * @param currentItem the INode that has a tag which is the employee to display the properties of.
   */
  public void showProperties(INode currentItem) {
    try {
      if(currentItem.getTag() instanceof Employee) {
        Employee employee = (Employee) currentItem.getTag();

        // set up an XML writer to build the html more conveniently
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xml = outputFactory.createXMLStreamWriter(baos);

        // set up the document
        xml.writeStartDocument();
        xml.writeStartElement("html");
        xml.writeDefaultNamespace("http://www.w3.org/1999/xhtml");
        xml.writeStartElement("body");
        xml.writeAttribute("style", "font-family:'Segoe UI'; font-size:14;");

        // write the full employee name right on top of the document
        writeElement(xml, "h2", employee.getFirstName()+" "+employee.getName());

        // write the image tag that links to the icon for the employee
        if(employee.getIcon() != null) {
          writeImage(xml, employee.getIcon());
        }

        // display the individual properties
        xml.writeStartElement("dl");
        // write all properties of an employee: "position", "business unit", "status", "email", "phone", "fax"
        writeDescriptionListElement(xml, "Position", employee.getPosition());
        writeDescriptionListElement(xml, "Business Unit", employee.getBusinessUnit());
        writeStatus(employee, xml);
        writeDescriptionListElement(xml, "Email", employee.getEmail());
        writeDescriptionListElement(xml, "Phone", employee.getPhone());
        writeDescriptionListElement(xml, "Fax", employee.getFax());

        // create links to the superior and colleague nodes.
        // (note that these "parent" references were added to the
        // source data in method demo.OrgChartDemo#initializeEmployeeHierarchy()).
        Employee parent = employee.getSuperior();
        if(parent != null) {
          writeElement(xml, "dt", "Superior");
          xml.writeStartElement("dd");
          createLinkEntry(xml, parent);
          xml.writeEndElement();

          Collection<Employee> colleagues = parent.getSubordinates();
          if(colleagues.size() > 1) {
            writeElement(xml, "dt", "Colleagues");
            xml.writeStartElement("dd");
            for (Iterator<Employee> iterator = colleagues.iterator(); iterator.hasNext(); ) {
              Employee colleague = iterator.next();
              if(colleague != employee) {
                this.createLinkEntry(xml, colleague);
                if(iterator.hasNext()) {
                  xml.writeCharacters(", ");
                }
              }
            }
            xml.writeEndElement();
          }
        }

        // create links to subordinate nodes
        Collection<Employee> subs = employee.getSubordinates();
        if(!subs.isEmpty()) {
          writeElement(xml, "dt", "Subordinates");
          xml.writeStartElement("dd");
          for (Iterator<Employee> iterator = subs.iterator(); iterator.hasNext(); ) {
            Employee sub = iterator.next();
            this.createLinkEntry(xml, sub);
            if(iterator.hasNext()) {
              xml.writeCharacters(", ");
            }
          }
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
      }
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
   * Writes a description list entry for the status of an employee. Chooses between different colors dependent on the status.
   */
  private void writeStatus(Employee employee, XMLStreamWriter xml) throws XMLStreamException {
    writeElement(xml, "dt", "Status");
    xml.writeStartElement("dd");
    xml.writeStartElement("b");
    xml.writeStartElement("font");
    String color = "black";
    switch (employee.getStatus()) {
      case "Present":
        color = "#008000";
        break;
      case "Travel":
        color = "#800080";
        break;
      case "Unavailable":
        color = "#ff0000";
        break;
    }
    xml.writeAttribute("color", color);
    xml.writeCharacters(employee.getStatus());
    xml.writeEndElement(); // b
    xml.writeEndElement(); // font
    xml.writeEndElement(); // dd
  }

  /**
   * Writes a DOM element with the specified text content.
   */
  private void writeElement(XMLStreamWriter xml, String tagName, String textContent) throws XMLStreamException {
    xml.writeStartElement(tagName);
    xml.writeCharacters(textContent);
    xml.writeEndElement();
  }

  /**
   * Writes the image element for the given reference to the icon.
   */
  private void writeImage(XMLStreamWriter xml, String iconRef) throws XMLStreamException {
    xml.writeStartElement("img");
    xml.writeAttribute("src", getClass().getResource("resources/" + iconRef + ".png").toString());
    xml.writeEndElement();
  }

  /**
   * Creates a hyperlink for the given employee.
   * Clicking a link to another employee in the properties view will focus
   * and zoom to the corresponding node in the organization chart.
   * We use the E-Mail address to identify individual employees.
   */
  private void createLinkEntry(XMLStreamWriter xml, Employee employee) throws XMLStreamException {
    xml.writeStartElement("a");
    xml.writeAttribute("href", employee.getEmail());
    xml.writeCharacters(employee.getName());
    xml.writeEndElement();
  }
}
