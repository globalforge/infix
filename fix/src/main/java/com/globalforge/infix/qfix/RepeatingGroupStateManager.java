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
public class RepeatingGroupStateManager {
    private ComponentContextState transitionState = ComponentContextState.FREE_FIELD;
    private final Deque<String> groupInProgess = new ArrayDeque<String>(100);
    private final DataStore dataStore;
    private final Map<String, String> fieldMap;

    public static enum ComponentContextState {
        FREE_FIELD, GROUP_START, GROUP_MEMBER
    }

    public RepeatingGroupStateManager(DataStore d, Map<String, String> f) {
        this.dataStore = d;
        this.fieldMap = f;
    }

    public void setGroupInProgressState(String groupId) {
        transitionState = ComponentContextState.GROUP_MEMBER;
        groupInProgess.push(groupId);
    }

    public ComponentContextState getState() {
        return this.transitionState;
    }

    private boolean isComponentGroup(String groupId) {
        if (dataStore.isComponentGroup(groupId)) { return true; }
        return false;
    }

    private boolean memberToFieldStateTransition(String compCtx) {
        if (transitionState != ComponentContextState.GROUP_MEMBER) { throw new RuntimeException(
            "State Transition Error moving from a group member to free field"); }
        while (!groupInProgess.isEmpty()) {
            String groupIdCtx = "&" + groupInProgess.peek() + "[*]->";
            int idx = compCtx.indexOf(groupIdCtx);
            if (idx >= 0) { return false; }
            groupInProgess.pop();
        }
        fieldMap.put(compCtx, null);
        transitionState = ComponentContextState.FREE_FIELD;
        return true;
    }

    private boolean memberToGroupIDStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_MEMBER) { throw new RuntimeException(
            "State Transition Error moving from a group member to group ID"); }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (isComponentGroup(tagNum)) {
            dataStore.addMessageGroupReference(curMessage, groupInProgess.peek(), tagNum);
            groupInProgess.push(MessageParser.getTagNumber(compCtx));
            dataStore.setNestedGroup(curMessage, groupInProgess.peek());
            transitionState = ComponentContextState.GROUP_START;
            fieldMap.put(compCtx, null);
            return true;
        }
        return false;
    }

    public void memberStateTransition(String curMessage, String compCtx) {
        if (memberToGroupIDStateTransition(curMessage, compCtx)) { return; }
        if (memberToFieldStateTransition(compCtx)) { return; }
        String tagNum = MessageParser.getTagNumber(compCtx);
        dataStore.addMessageGroupMember(curMessage, groupInProgess.peek(), tagNum);
        fieldMap.put(compCtx, null);
    }

    public void groupIdToMemberStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_START) { throw new RuntimeException(
            "State Transition Error moving from a group ID to group member"); }
        String groupIdCtx = "&" + groupInProgess.peek() + "[*]->";
        int idx = compCtx.indexOf(groupIdCtx);
        if (idx < 0) { throw new RuntimeException(
            "State Transition Error.  No members following a Group ID."); }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (isComponentGroup(tagNum)) {
            String help =
                "msg=" + curMessage + ", tag=" + tagNum + ", grpInPrgress=" + groupInProgess.peek();
            throw new RuntimeException("Can't have consecutive GroupIDs. " + help);
        }
        dataStore.addMessageGroupMember(curMessage, groupInProgess.peek(), tagNum);
        fieldMap.put(compCtx, null);
        transitionState = ComponentContextState.GROUP_MEMBER;
    }

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
