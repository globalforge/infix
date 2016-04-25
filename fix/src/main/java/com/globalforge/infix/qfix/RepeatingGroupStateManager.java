package com.globalforge.infix.qfix;

import java.util.Map;

public class RepeatingGroupStateManager {
    private ComponentContextState transitionState = ComponentContextState.FREE_FIELD;
    private String groupInProgess;
    private final DataStore dataStore;
    private final Map<String, String> fieldMap;

    public static enum ComponentContextState {
        FREE_FIELD, GROUP_START, GROUP_MEMBER
    }

    public RepeatingGroupStateManager(DataStore d, Map<String, String> f) {
        this.dataStore = d;
        this.fieldMap = f;
    }

    public void setStartGroupState(String groupId) {
        transitionState = ComponentContextState.GROUP_START;
        groupInProgess = groupId;
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
        String groupIdCtx = "&" + groupInProgess;
        int idx = compCtx.indexOf(groupIdCtx + "[*]->");
        if (idx >= 0) {
            return false;
        }
        groupInProgess = null;
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
            groupInProgess = MessageParser.getTagNumber(compCtx);
            dataStore.startMessageGroup(curMessage, groupInProgess);
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
        dataStore.addMessageGroupMember(curMessage, groupInProgess, tagNum);
        fieldMap.put(compCtx, null);
    }

    public void groupIdToMemberStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.GROUP_START) {
            throw new RuntimeException(
                "State Transition Error moving from a group ID to group member");
        }
        String groupIdCtx = "&" + groupInProgess;
        int idx = compCtx.indexOf(groupIdCtx + "[*]->");
        if (idx < 0) {
            throw new RuntimeException("State Transition Error.  No members following a Group ID.");
        }
        String tagNum = MessageParser.getTagNumber(compCtx);
        dataStore.addMessageGroupMember(curMessage, groupInProgess, tagNum);
        fieldMap.put(compCtx, null);
        transitionState = ComponentContextState.GROUP_MEMBER;
    }

    public void fieldToGroupIDStateTransition(String curMessage, String compCtx) {
        if (transitionState != ComponentContextState.FREE_FIELD) {
            throw new RuntimeException(
                "State Transition Error moving from a free field to group ID");
        }
        String tagNum = MessageParser.getTagNumber(compCtx);
        if (isComponentGroup(tagNum)) {
            groupInProgess = MessageParser.getTagNumber(compCtx);
            dataStore.startMessageGroup(curMessage, groupInProgess);
            transitionState = ComponentContextState.GROUP_START;
        }
        fieldMap.put(compCtx, null);
    }
}
