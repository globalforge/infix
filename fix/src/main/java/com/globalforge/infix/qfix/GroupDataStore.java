package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDataStore {
    private final static Logger logger = LoggerFactory.getLogger(GroupDataStore.class);
    /** A map of MsgType to repeating groups mapped by groupId */
    protected final Map<String, Map<String, RepeatingGroupBuilder>> messageGroups =
        new HashMap<String, Map<String, RepeatingGroupBuilder>>();
    /**
     * A map of repeating groupId to repeating group for groups defined outside
     * of message types
     */
    private final Map<String, RepeatingGroupBuilder> componentGroups =
        new HashMap<String, RepeatingGroupBuilder>();

    public Set<String> getRepeatingGroupMsgTypes() {
        return messageGroups.keySet();
    }

    public Map<String, RepeatingGroupBuilder> getGroupsInMessage(String msgType) {
        return messageGroups.get(msgType);
    }

    public boolean isMessageGroup(String curMessage, String groupId) {
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(curMessage);
        if (rgmap == null) {
            return false;
        }
        if (rgmap.containsKey(groupId)) {
            return true;
        }
        return false;
    }

    public boolean isMessageGroupReference(String curMessage, String groupId, String tagNum) {
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(curMessage);
        if (rgmap == null) {
            return false;
        }
        if (rgmap.containsKey(groupId)) {
            if (rgmap.get(groupId).containsReference(tagNum)) {
                return true;
            }
        }
        return false;
    }

    public RepeatingGroupBuilder startMessageGroup(String curMessage, String groupId) {
        Map<String, RepeatingGroupBuilder> rm = messageGroups.get(curMessage);
        if (rm == null) {
            rm = new HashMap<String, RepeatingGroupBuilder>();
            messageGroups.put(curMessage, rm);
        }
        RepeatingGroupBuilder rg = new RepeatingGroupBuilder(groupId);
        rm.put(groupId, rg);
        return rg;
    }

    public RepeatingGroupBuilder startMessageGroup(String curMessage, String groupId,
        boolean isNested) {
        RepeatingGroupBuilder rg = this.startMessageGroup(curMessage, groupId);
        rg.setNested(isNested);
        return rg;
    }

    public RepeatingGroupBuilder getMessageGroup(String curMessage, String groupId) {
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(curMessage);
        if (rgmap == null) {
            return null;
        }
        return rgmap.get(groupId);
    }

    public void addMessageGroupMember(String curMessage, String groupId, String tagNum) {
        GroupDataStore.logger.info("Add Group Member: curMessage=" + curMessage + ", groupId="
            + groupId + ", tagNum=" + tagNum);
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(curMessage);
        RepeatingGroupBuilder rg = rgmap.get(groupId);
        rg.addMember(tagNum);
        GroupDataStore.logger.info(rg.toString());
    }

    public void addMessageGroupReference(String curMessage, String groupId, String tagNum) {
        GroupDataStore.logger.info("Add Group Reference: curMessage=" + curMessage + ", groupId="
            + groupId + ", tagNum=" + tagNum);
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(curMessage);
        RepeatingGroupBuilder rg = rgmap.get(groupId);
        rg.addReference(tagNum);
        GroupDataStore.logger.info(rg.toString());
    }

    public void putComponentGroup(String grpId, RepeatingGroupBuilder rg) {
        componentGroups.put(grpId, rg);
    }

    public void addComponentGroupMember(String grpId, String mem) {
        RepeatingGroupBuilder rg = componentGroups.get(grpId);
        rg.addMember(mem);
    }

    public RepeatingGroupBuilder getComponentGroup(String groupId) {
        return componentGroups.get(groupId);
    }

    public boolean isComponentGroup(String groupId) {
        return componentGroups.containsKey(groupId);
    }
}
