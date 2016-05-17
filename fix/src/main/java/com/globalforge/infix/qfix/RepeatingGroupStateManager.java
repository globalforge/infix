package com.globalforge.infix.qfix;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/*-
The MIT License (MIT)

Copyright (c) 2016 Global Forge LLC

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
/**
 * Builds repeating group information from a stream of Infix strings. Keeps
 * track of state changes between Infix strings as they relate to groups. Needed
 * to distinguish group identifiers, members, nested groups and plain fields.
 * @author Michael C. Starkie
 */
public class RepeatingGroupStateManager {
    private ComponentContextState transitionState = ComponentContextState.FREE_FIELD;
    private final Deque<String> groupInProgess = new ArrayDeque<String>(100);
    private final DataStore dataStore;
    private final Map<String, String> fieldMap;

    /**
     * An Infix references can be 1 of 3 things. A plain old field, the start of
     * a group, or a field that is a member of a group.
     * @author Michael C. Starkie
     */
    public static enum ComponentContextState {
        FREE_FIELD, GROUP_START, GROUP_MEMBER
    }

    /**
     * @param d the data store
     * @param f a field map.
     */
    public RepeatingGroupStateManager(DataStore d, Map<String, String> f) {
        this.dataStore = d;
        this.fieldMap = f;
    }

    /**
     * Sets the initial state of a stream of Infix to just after receiving a new
     * group identifier.
     * @param groupId
     */
    public void setGroupInProgressState(String groupId) {
        transitionState = ComponentContextState.GROUP_MEMBER;
        groupInProgess.push(groupId);
    }

    /**
     * Returns the current state of the stream.
     * @return ComponentContextState
     */
    public ComponentContextState getState() {
        return this.transitionState;
    }

    /**
     * Returns whether the group identifier is a group id found in a component
     * section. We only care about groups defined in the component section
     * because this class only deals with components.
     * @param groupId
     * @return boolean
     */
    private boolean isComponentGroup(String groupId) {
        if (dataStore.isComponentGroup(groupId)) { return true; }
        return false;
    }

    /**
     * When building a repeating group from a stream of Infix strings you need
     * to know when you are done parsing a repeating group and have just
     * encountered a plain old field.
     * @param compCtx The nest Infix string in a chain.
     * @return boolean
     */
    private boolean memberToFieldStateTransition(String compCtx) {
        if (transitionState != ComponentContextState.GROUP_MEMBER) { throw new RuntimeException(
            "State Transition Error moving from a group member to free field"); }
        while (!groupInProgess.isEmpty()) {
            String groupIdCtx = groupInProgess.peek() + "[*]->";
            int idx = compCtx.indexOf(groupIdCtx);
            if (idx >= 0) { return false; }
            groupInProgess.pop();
        }
        fieldMap.put(compCtx, null);
        transitionState = ComponentContextState.FREE_FIELD;
        return true;
    }

    /**
     * When building a repeating group from a stream of Infix strings you need
     * to know when you are parsing members of a repeating group and have just
     * encountered a nested group.
     * @param compCtx The nested Infix string in a chain.
     * @param msgType the current message type.
     * @return boolean
     */
    private boolean memberToGroupIDStateTransition(String msgType, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_MEMBER) { throw new RuntimeException(
            "State Transition Error moving from a group member to group ID"); }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (isComponentGroup(tagNum)) {
            dataStore.addMessageGroupReference(msgType, groupInProgess.peek(), tagNum);
            groupInProgess.push(MessageParser.getTagNumber(compCtx));
            dataStore.setNestedGroup(msgType, groupInProgess.peek());
            transitionState = ComponentContextState.GROUP_START;
            fieldMap.put(compCtx, null);
            return true;
        }
        return false;
    }

    /**
     * When parsing the infix contexts belonging to a message in the order as
     * they appear in the message, then in order to build repeating groups you
     * need to understand the possible state transitions. You can go from a
     * member to a nested group, exit a repeating group or encounter another
     * member.
     * @param msgType The message type
     * @param compCtx The current infix field
     */
    public void memberStateTransition(String msgType, String compCtx) {
        if (memberToGroupIDStateTransition(msgType, compCtx)) { return; }
        if (memberToFieldStateTransition(compCtx)) { return; }
        String tagNum = MessageParser.getTagNumber(compCtx);
        dataStore.addMessageGroupMember(msgType, groupInProgess.peek(), tagNum);
        fieldMap.put(compCtx, null);
    }

    /**
     * When parsing the infix contexts belonging to a message in the order as
     * they appear in the message, then in order to build repeating groups you
     * need to understand the possible state transitions. You can go from a
     * member to a nested group, exit a repeating group or encounter another
     * member. If you have encountered a group identifier previously then the
     * only legal state transformation is to encountered a member of that group.
     * @param msgType The message type
     * @param compCtx The current infix field
     */
    public void groupIdToMemberStateTransition(String msgType, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_START) { throw new RuntimeException(
            "State Transition Error moving from a group ID to group member"); }
        String groupIdCtx = groupInProgess.peek() + "[*]->";
        int idx = compCtx.indexOf(groupIdCtx);
        if (idx < 0) { throw new RuntimeException(
            "State Transition Error.  Member not part of group. [msgtyp=" + msgType + ", group="
                + groupIdCtx + ", member=" + compCtx + "]"); }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (isComponentGroup(tagNum)) {
            String help =
                "msg=" + msgType + ", tag=" + tagNum + ", grpInPrgress=" + groupInProgess.peek();
            throw new RuntimeException("Can't have consecutive GroupIDs. " + help);
        }
        dataStore.addMessageGroupMember(msgType, groupInProgess.peek(), tagNum);
        fieldMap.put(compCtx, null);
        transitionState = ComponentContextState.GROUP_MEMBER;
    }

    /**
     * When parsing the infix contexts belonging to a message in the order as
     * they appear in the message, then in order to build repeating groups you
     * need to understand the possible state transitions. You can go from a
     * member to a nested group, exit a repeating group or encounter another
     * member. If you are parsing free fields in a fix message the only legal
     * state transition you can make is either to another free floating field or
     * to a group identifier.
     * @param msgType The message type
     * @param compCtx The current infix field
     */
    public void fieldToGroupIDStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.FREE_FIELD) { throw new RuntimeException(
            "State Transition Error moving from a free field to group ID"); }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (!groupInProgess.isEmpty()) { throw new RuntimeException(
            "how could there be a group in progess here?"); }
        if (isComponentGroup(tagNum)) {
            groupInProgess.push(MessageParser.getTagNumber(compCtx));
            dataStore.startMessageGroup(curMessage, groupInProgess.peek());
            transitionState = ComponentContextState.GROUP_START;
        }
        fieldMap.put(compCtx, null);
    }
}
