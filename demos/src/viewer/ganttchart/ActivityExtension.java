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
package viewer.ganttchart;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;

import java.time.LocalDateTime;

/**
 * Handles deserialization of {@link Activity} instances from GraphML.
 */
public class ActivityExtension extends MarkupExtension {
  private String startDate;
  private String endDate;
  private String name;
  private double leadTime;
  private double followUpTime;
  private Task task;

  public ActivityExtension() {
    name = "";
    leadTime = 0;
    followUpTime = 0;
  }

  public Task getTask() {
    return task;
  }

  public void setTask( Task task ) {
    this.task = task;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public double getLeadTime() {
    return leadTime;
  }

  public void setLeadTime( double leadTime ) {
    this.leadTime = leadTime;
  }

  public double getFollowUpTime() {
    return followUpTime;
  }

  public void setFollowUpTime( double followUpTime ) {
    this.followUpTime = followUpTime;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate( String startDate ) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate( String endDate ) {
    this.endDate = endDate;
  }

  /**
   * Returns a new {@link Activity} instance that corresponds to the data
   * read from GraphML.
   */
  @Override
  public Object provideValue( ILookup serviceProvider ) {
    Activity activity = new Activity();
    activity.setName(getName());

    activity.setLeadTime(getLeadTime());
    activity.setFollowUpTime(getFollowUpTime());

    activity.setEndDate(LocalDateTime.parse(getEndDate()));
    activity.setStartDate(LocalDateTime.parse(getStartDate()));

    activity.setTask(getTask());
    return activity;
  }
}
