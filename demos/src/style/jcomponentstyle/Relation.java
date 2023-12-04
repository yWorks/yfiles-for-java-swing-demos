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
package style.jcomponentstyle;

/**
 * A relation business object in our model.
 */
public class Relation {
  private Customer customer;
  private Product product;

  /**
   * Creates a new instance - for deserialization only.
   */
  public Relation() {
  }

  /**
   * Creates a new instance containing the given {@link Customer} and {@link Product}.
   */
  public Relation(Customer customer, Product product) {
    this.customer = customer;
    this.product = product;
  }

  /**
   * Returns the customer in this relation.
   */
  public Customer getCustomer() {
    return customer;
  }

  /**
   * Specifies the customer in this relation.
   */
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  /**
   * Returns the product in this relation.
   */
  public Product getProduct() {
    return product;
  }

  /**
   * Specifies the product in this relation.
   */
  public void setProduct(Product product) {
    this.product = product;
  }

  /**
   * Returns a string that can be used as an {@link com.yworks.yfiles.graph.ILabel edge label}.
   */
  @Override
  public String toString() {
    int cId = customer == null ? -1 : customer.getId();
    int pId = product == null ? -1 : product.getId();
    return cId + " -> " + pId;
  }
}