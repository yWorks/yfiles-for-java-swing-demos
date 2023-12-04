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
package toolkit;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Parses XML documents in a way that is not susceptible to external entity
 * injection.
 * <p>
 * See
 * <a href="https://owasp.org/www-project-web-security-testing-guide/assets/archive/OWASP_Testing_Guide_v3.pdf">OWASP_Testing_Guide_v3.pdf</a>
 * page 245 for related exploits.
 * <br>
 * See
 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java">XML_External_Entity_Prevention_Cheat_Sheet.html</a>
 * for measures preventing the above exploits.
 * </p>
 */
public class XmlUtils {
  private static final String MESSAGE =
    "XML processing is disallowed because the XML processor implementation " +
    "is vulnerable to external entity injection and/or remote code execution.";

  private XmlUtils() {
  }

  public static Document parse(
    final InputStream is
  ) throws IOException, ParserConfigurationException, SAXException {
    return parseDOMImpl(new InputSource(is));
  }
  private static Document parseDOMImpl(
    final InputSource is
  ) throws IOException, ParserConfigurationException, SAXException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final boolean hardened = configureDOM(factory);

    if (hardened) {
      final DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(is);
    } else {
      throw new IOException(MESSAGE);
    }
  }

  private static boolean configureDOM( final DocumentBuilderFactory dbf ) {
    final boolean hardened = disableExternalEntitiesDOM(dbf);

    dbf.setNamespaceAware(true);

    // XInclude should be disabled by default, but better safe than sorry
    dbf.setXIncludeAware(false);

    dbf.setExpandEntityReferences(false);

    return hardened;
  }

  private static boolean disableExternalEntitiesDOM(
    final DocumentBuilderFactory dbf
  ) {
    // exploits:
    //  https://owasp.org/www-project-web-security-testing-guide/assets/archive/OWASP_Testing_Guide_v3.pdf
    //  page 245
    // prevention:
    //  https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java

    boolean hardened = false;
    hardened |= setFeatureDOM(dbf, "http://javax.xml.XMLConstants/feature/secure-processing", true);

    // disable all doc type declarations
    hardened |= setFeatureDOM(dbf, "http://apache.org/xml/features/disallow-doctype-decl", true);
    // Xerces 2
    hardened |= setFeatureDOM(dbf, "http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl", true);


    // turn off entity resolution

    // Java 7
    boolean hardJ7 = true;
    hardJ7 &= setFeatureDOM(dbf, "http://xml.org/sax/features/external-general-entities", false);
    hardJ7 &= setFeatureDOM(dbf, "http://xml.org/sax/features/external-parameter-entities", false);
    hardened |= hardJ7;

    // Xerces 2
    boolean hardX2 = true;
    hardX2 &= setFeatureDOM(dbf, "http://xerces.apache.org/xerces2-j/features.html#external-general-entities", false);
    hardX2 &= setFeatureDOM(dbf, "http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities", false);
    hardened |= hardX2;

    // Xerces 1
    boolean hardX1 = true;
    hardX1 &= setFeatureDOM(dbf, "http://xerces.apache.org/xerces-j/features.html#external-general-entities", false);
    hardX1 &= setFeatureDOM(dbf, "http://xerces.apache.org/xerces-j/features.html#external-parameter-entities", false);
    hardened |= hardX1;

    return hardened;
  }

  private static boolean setFeatureDOM(
    final DocumentBuilderFactory dbf,
    final String featureUri, final boolean enabled
  ) {
    try {
      dbf.setFeature(featureUri, enabled);
      return true;
    } catch (ParserConfigurationException pce) {
      return false;
    }
  }
}
