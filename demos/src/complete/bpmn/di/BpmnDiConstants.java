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
package complete.bpmn.di;


public final class BpmnDiConstants {
  private BpmnDiConstants() {
  }

  public static final String PROCESS_ELEMENT = "process";

  public static final String BPMN_DIAGRAM_ELEMENT = "BPMNDiagram";

  public static final String BPMN_PLANE_ELEMENT = "BPMNPlane";

  public static final String BPMN_EDGE_ELEMENT = "BPMNEdge";

  public static final String BPMN_LABEL_ELEMENT = "BPMNLabel";

  public static final String BPMN_SHAPE_ELEMENT = "BPMNShape";

  public static final String BPMN_LABEL_STYLE_ELEMENT = "BPMNLabelStyle";

  public static final String BOUNDS_ELEMENT = "Bounds";

  public static final String WAYPOINT_ELEMENT = "waypoint";

  public static final String GATEWAY_SUFFIX = "Gateway";

  public static final String EXCLUSIVE_GATEWAY_ELEMENT = "exclusiveGateway";

  public static final String INCLUSIVE_GATEWAY_ELEMENT = "inclusiveGateway";

  public static final String PARALLEL_GATEWAY_ELEMENT = "parallelGateway";

  public static final String EVENT_BASED_GATEWAY_ELEMENT = "eventBasedGateway";

  public static final String COMPLEX_GATEWAY_ELEMENT = "complexGateway";

  public static final String USER_TASK_ELEMENT = "userTask";

  public static final String MANUAL_TASK_ELEMENT = "manualTask";

  public static final String SEND_TASK_ELEMENT = "sendTask";

  public static final String RECEIVE_TASK_ELEMENT = "receiveTask";

  public static final String SCRIPT_TASK_ELEMENT = "scriptTask";

  public static final String SERVICE_TASK_ELEMENT = "serviceTask";

  public static final String BUSINESS_RULE_TASK_ELEMENT = "businessRuleTask";

  public static final String TASK_ELEMENT = "task";

  public static final String SUB_PROCESS_ELEMENT = "subProcess";

  public static final String AD_HOC_SUB_PROCESS_ELEMENT = "adHocSubProcess";

  public static final String TRANSACTION_ELEMENT = "transaction";

  public static final String CONVERSATION_ELEMENT = "conversation";

  public static final String CONVERSATION_LINK_ELEMENT = "conversationLink";

  public static final String SUB_CONVERSATION_ELEMENT = "subConversation";

  public static final String GLOBAL_CONVERSATION_ELEMENT = "globalConversation";

  public static final String CALL_CONVERSATION_ELEMENT = "callConversation";

  public static final String COLLABORATION_ELEMENT = "collaboration";

  public static final String CALL_ACTIVITY_ELEMENT = "callActivity";

  public static final String START_EVENT_ELEMENT = "startEvent";

  public static final String MESSAGE_EVENT_DEFINITION_ELEMENT = "messageEventDefinition";

  public static final String TIMER_EVENT_DEFINITION_ELEMENT = "timerEventDefinition";

  public static final String TERMINATE_EVENT_DEFINITION_ELEMENT = "terminateEventDefinition";

  public static final String ERROR_EVENT_DEFINITION_ELEMENT = "errorEventDefinition";

  public static final String CONDITIONAL_EVENT_DEFINITION_ELEMENT = "conditionalEventDefinition";

  public static final String COMPENSATE_EVENT_DEFINITION_ELEMENT = "compensateEventDefinition";

  public static final String CANCEL_EVENT_DEFINITION_ELEMENT = "cancelEventDefinition";

  public static final String SIGNAL_EVENT_DEFINITION_ELEMENT = "signalEventDefinition";

  public static final String MULTIPLE_EVENT_DEFINITION_ELEMENT = "multipleEventDefinition";

  public static final String PARALLEL_EVENT_DEFINITION_ELEMENT = "parallelEventDefinition";

  public static final String ESCALATION_EVENT_DEFINITION_ELEMENT = "escalationEventDefinition";

  public static final String LINK_EVENT_DEFINITION_ELEMENT = "linkEventDefinition";

  public static final String END_EVENT_ELEMENT = "endEvent";

  public static final String INTERMEDIATE_THROW_EVENT_ELEMENT = "intermediateThrowEvent";

  public static final String INTERMEDIATE_CATCH_EVENT_ELEMENT = "intermediateCatchEvent";

  public static final String BOUNDARY_EVENT_ELEMENT = "boundaryEvent";

  public static final String LANE_ELEMENT = "lane";

  public static final String CHOREOGRAPHY_ELEMENT = "choreography";

  public static final String SUB_CHOREOGRAPHY_ELEMENT = "subChoreography";

  public static final String CALL_CHOREOGRAPHY_ELEMENT = "callChoreography";

  public static final String CHOREOGRAPHY_TASK_ELEMENT = "choreographyTask";

  public static final String PARTICIPANT_ELEMENT = "participant";

  public static final String PARTICIPANT_MULTIPLICITY_ELEMENT = "participantMultiplicity";

  public static final String TEXT_ANNOTATION_ELEMENT = "textAnnotation";

  public static final String TEXT_ELEMENT = "text";

  public static final String FONT_ELEMENT = "Font";

  public static final String STANDARD_LOOP_CHARACTERISTICS_ELEMENT = "standardLoopCharacteristics";

  public static final String MULTI_INSTANCE_LOOP_CHARACTERISTICS_ELEMENT = "multiInstanceLoopCharacteristics";

  public static final String PROPERTY_ELEMENT = "property";

  public static final String IO_SPECIFICATION_ELEMENT = "ioSpecification";

  public static final String SOURCE_REF_ELEMENT = "sourceRef";

  public static final String TARGET_REF_ELEMENT = "targetRef";

  public static final String GROUP_ELEMENT = "group";

  public static final String DATA_INPUT_ELEMENT = "dataInput";

  public static final String DATA_OUTPUT_ELEMENT = "dataOutput";

  public static final String SEQUENCE_FLOW_ELEMENT = "sequenceFlow";

  public static final String MESSAGE_FLOW_ELEMENT = "messageFlow";

  public static final String MESSAGE_FLOW_REF_ELEMENT = "messageFlowRef";

  public static final String DATA_OBJECT_REFERENCE_ELEMENT = "dataObjectReference";

  public static final String DATA_STORE_REFERENCE_ELEMENT = "dataStoreReference";

  public static final String CONDITION_EXPRESSION_ELEMENT = "conditionExpression";

  public static final String ASSOCIATION_ELEMENT = "association";

  public static final String DATA_ASSOCIATION_ELEMENT = "dataAssociation";

  public static final String DATA_INPUT_ASSOCIATION_ELEMENT = "dataInputAssociation";

  public static final String DATA_OUTPUT_ASSOCIATION_ELEMENT = "dataOutputAssociation";

  public static final String ID_ATTRIBUTE = "id";

  public static final String NAME_ATTRIBUTE = "name";

  public static final String DOCUMENTATION_ATTRIBUTE = "documentation";

  public static final String RESOLUTION_ATTRIBUTE = "resolution";

  public static final String X_ATTRIBUTE = "x";

  public static final String Y_ATTRIBUTE = "y";

  public static final String WIDTH_ATTRIBUTE = "width";

  public static final String HEIGHT_ATTRIBUTE = "height";

  public static final String BPMN_ELEMENT_ATTRIBUTE = "bpmnElement";

  public static final String SOURCE_REF_ATTRIBUTE = "sourceRef";

  public static final String TARGET_REF_ATTRIBUTE = "targetRef";

  public static final String PROCESS_REF_ATTRIBUTE = "processRef";

  public static final String CATEGORY_VALUE_REF_ATTRIBUTE = "categoryValueRef";

  public static final String CALLED_CHOREOGRAPHY_REF_ATTRIBUTE = "calledChoreographyRef";

  public static final String CALLED_ELEMENT_ATTRIBUTE = "calledElement";

  public static final String MESSAGE_VISIBLE_KIND_ATTRIBUTE = "messageVisibleKind";

  public static final String IS_SEQUENTIAL_ATTRIBUTE = "isSequential";

  public static final String SIZE_ATTRIBUTE = "size";

  public static final String IS_BOLD_ATTRIBUTE = "isBold";

  public static final String IS_ITALIC_ATTRIBUTE = "isItalic";

  public static final String IS_UNDERLINE_ATTRIBUTE = "isUnderline";

  public static final String IS_STRIKE_THROUGH_ATTRIBUTE = "isStrikeThrough";

  public static final String IS_HORIZONTAL_ATTRIBUTE = "isHorizontal";

  public static final String IS_EXPANDED_ATTRIBUTE = "isExpanded";

  public static final String IS_MARKER_VISIBLE_ATTRIBUTE = "isMarkerVisible";

  public static final String IS_MESSAGE_VISIBLE_ATTRIBUTE = "isMessageVisible";

  public static final String CHOREOGRAPHY_ACTIVITY_SHAPE_ATTRIBUTE = "choreographyActivityShape";

  public static final String PARTICIPANT_BAND_KIND_ATTRIBUTE = "participantBandKind";

  public static final String LABEL_STYLE_ATTRIBUTE = "labelStyle";

  public static final String TRIGGERED_BY_EVENT_ATTRIBUTE = "triggeredByEvent";

  public static final String IS_INTERRUPTING_ATTRIBUTE = "isInterrupting";

  public static final String CALLED_COLLABORATION_REF_ATTRIBUTE = "calledCollaborationRef";

  public static final String IS_COLLECTION_ATTRIBUTE = "isCollection";

  public static final String DATA_OBJECT_REF_ATTRIBUTE = "dataObjectRef";

  public static final String ASSOCIATION_DIRECTION_ATTRIBUTE = "associationDirection";

  public static final String IS_FOR_COMPENSATION_ATTRIBUTE = "associationDirection";

  public static final String ATTACHED_TO_REF_ATTRIBUTE = "attachedToRef";

  public static final String CANCEL_ACTIVITY_ATTRIBUTE = "cancelActivity";

}
