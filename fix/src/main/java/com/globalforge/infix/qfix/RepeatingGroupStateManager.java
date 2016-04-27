package com.globalforge.infix.qfix;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

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
        if (dataStore.isComponentGroup(groupId)) {
            return true;
        }
        return false;
    }

    private boolean memberToFieldStateTransition(String compCtx) {
        if (transitionState != ComponentContextState.GROUP_MEMBER) {
            throw new RuntimeException(
                "State Transition Error moving from a group member to free field");
        }
        while (!groupInProgess.isEmpty()) {
            String groupIdCtx = "&" + groupInProgess.peek() + "[*]->";
            int idx = compCtx.indexOf(groupIdCtx);
            if (idx >= 0) {
                return false;
            }
            groupInProgess.pop();
        }
        fieldMap.put(compCtx, null);
        transitionState = ComponentContextState.FREE_FIELD;
        return true;
    }

    private boolean memberToGroupIDStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_MEMBER) {
            throw new RuntimeException(
                "State Transition Error moving from a group member to group ID");
        }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (isComponentGroup(tagNum)) {
            dataStore.addMessageGroupReference(curMessage, groupInProgess.peek(), tagNum);
            groupInProgess.push(MessageParser.getTagNumber(compCtx));
            dataStore.startMessageGroup(curMessage, groupInProgess.peek(), true);
            transitionState = ComponentContextState.GROUP_START;
            fieldMap.put(compCtx, null);
            return true;
        }
        return false;
    }

    public void memberStateTransition(String curMessage, String compCtx) {
        if (memberToGroupIDStateTransition(curMessage, compCtx)) {
            return;
        }
        if (memberToFieldStateTransition(compCtx)) {
            return;
        }
        String tagNum = MessageParser.getTagNumber(compCtx);
        dataStore.addMessageGroupMember(curMessage, groupInProgess.peek(), tagNum);
        fieldMap.put(compCtx, null);
    }

    public void groupIdToMemberStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_START) {
            throw new RuntimeException(
                "State Transition Error moving from a group ID to group member");
        }
        String groupIdCtx = "&" + groupInProgess.peek() + "[*]->";
        int idx = compCtx.indexOf(groupIdCtx);
        if (idx < 0) {
            throw new RuntimeException("State Transition Error.  No members following a Group ID.");
        }
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
        if (transitionState != ComponentContextState.FREE_FIELD) {
            throw new RuntimeException(
                "State Transition Error moving from a free field to group ID");
        }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (!groupInProgess.isEmpty()) {
            throw new RuntimeException("how could there be a group in progess here?");
        }
        if (isComponentGroup(tagNum)) {
            groupInProgess.push(MessageParser.getTagNumber(compCtx));
            dataStore.startMessageGroup(curMessage, groupInProgess.peek());
            transitionState = ComponentContextState.GROUP_START;
        }
        fieldMap.put(compCtx, null);
    }
}
