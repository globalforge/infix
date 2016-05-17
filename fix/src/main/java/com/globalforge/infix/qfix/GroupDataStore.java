package com.globalforge.infix.qfix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Contains all information related to repeating groups as parsed from a quick
 * fix data dictionary.
 * @author Michael C. Starkie
 */
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

    /**
     * returns a list of all message types that contain at least 1 repeating
     * group.
     * @return Set<String> The set of all message types.
     */
    public Set<String> getRepeatingGroupMsgTypes() {
        return messageGroups.keySet();
    }

    /**
     * Given a message type return a map of all repeating groups found within
     * the message keyed by group identifier.
     * @param msgType The message type.
     * @return Map<String, RepeatingGroupBuilder>
     */
    public Map<String, RepeatingGroupBuilder> getGroupsInMessage(String msgType) {
        return messageGroups.get(msgType);
    }

    /**
     * Given a message type and a group identifier, determine if a repeating
     * group was defined within the message block of the given message type.
     * @param msgType The message type
     * @param groupId The group identifier.
     * @return boolean
     */
    public boolean isMessageGroup(String msgType, String groupId) {
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(msgType);
        if (rgmap == null) { return false; }
        if (rgmap.containsKey(groupId)) { return true; }
        return false;
    }

    /**
     * Given a message type, a group identifier and a field number, determine if
     * the field is the start of a nested repeating group.
     * @param msgType a message type
     * @param groupId a group identifier
     * @param tagNum a field within a group
     * @return
     */
    public boolean isMessageGroupReference(String msgType, String groupId, String tagNum) {
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(msgType);
        if (rgmap == null) { return false; }
        if (rgmap.containsKey(groupId)) {
            if (rgmap.get(groupId).containsReference(tagNum)) { return true; }
        }
        return false;
    }

    /**
     * Given a message type and a group identifier, begin a new repeating group
     * @param msgType the message type
     * @param groupId the group identifier
     * @return RepeatingGroupBuilder A new "in progress" repeating group.
     */
    public RepeatingGroupBuilder startMessageGroup(String msgType, String groupId) {
        Map<String, RepeatingGroupBuilder> rm = messageGroups.get(msgType);
        if (rm == null) {
            rm = new HashMap<String, RepeatingGroupBuilder>();
            messageGroups.put(msgType, rm);
        }
        RepeatingGroupBuilder rg = new RepeatingGroupBuilder(groupId);
        rm.put(groupId, rg);
        return rg;
    }

    /**
     * Given a message type and a group identifier, begin a new repeating group
     * @param msgType the message type
     * @param groupId the group identifier
     * @return RepeatingGroupBuilder A new "in progress" repeating group.
     */
    public RepeatingGroupBuilder setNestedGroup(String msgType, String groupId) {
        RepeatingGroupBuilder rg = this.startMessageGroup(msgType, groupId);
        rg.setNested(true);
        return rg;
    }

    /**
     * Given a message type and a group identifier, return the repeating group
     * as it is defined for that message type.
     * @param msgType the message type
     * @param groupId the group identifier
     * @return
     */
    public RepeatingGroupBuilder getMessageGroup(String msgType, String groupId) {
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(msgType);
        if (rgmap == null) { return null; }
        return rgmap.get(groupId);
    }

    /**
     * Given a msgType, a group identifier and a member tag number, add the
     * member to the group defined by groupId.
     * @param msgType
     * @param groupId
     * @param tagNum
     */
    public void addMessageGroupMember(String msgType, String groupId, String tagNum) {
        GroupDataStore.logger.info(
            "Add Group Member: msgType=" + msgType + ", groupId=" + groupId + ", tagNum=" + tagNum);
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(msgType);
        RepeatingGroupBuilder rg = rgmap.get(groupId);
        rg.addMember(tagNum);
        GroupDataStore.logger.info(rg.toString());
    }

    /**
     * Given a message type, an outer group identifier and an inner group
     * identifier, add the inner group identifier as a reference (nested group)
     * member of the outer group.
     * @param msgType
     * @param groupId
     * @param nestedGroupId
     */
    public void addMessageGroupReference(String msgType, String groupId, String nestedGroupId) {
        GroupDataStore.logger.info("Add Group Reference: curMessage=" + msgType + ", groupId="
            + groupId + ", tagNum=" + nestedGroupId);
        Map<String, RepeatingGroupBuilder> rgmap = messageGroups.get(msgType);
        RepeatingGroupBuilder rg = rgmap.get(groupId);
        rg.addReference(nestedGroupId);
        GroupDataStore.logger.info(rg.toString());
    }

    /**
     * insert a repeating group in the set of groups defined group component
     * groups. Groups whose definition appear in the components section of a
     * data dictionary.
     * @param grpId the group identifier.
     * @param rg the repeating group to insert.
     */
    public void putComponentGroup(String grpId, RepeatingGroupBuilder rg) {
        if (grpId.contains("48")) {
            // System.out.println();
        }
        componentGroups.put(grpId, rg);
    }

    /**
     * Given a group identifier and a field member, add the field as a member to
     * the group. The group definition comes from the component section of a
     * data dictionary and not the message section.
     * @param grpId the group identifier
     * @param mem the field member
     */
    public void addComponentGroupMember(String grpId, String mem) {
        RepeatingGroupBuilder rg = componentGroups.get(grpId);
        rg.addMember(mem);
    }

    /**
     * Return from the set of groups defined in the components section the group
     * associated with a given group identifier.
     * @param groupId
     * @return RepeatingGroupBuilder
     */
    public RepeatingGroupBuilder getComponentGroup(String groupId) {
        return componentGroups.get(groupId);
    }

    /**
     * Return true if the given group identifier belongs to the set of groups
     * defined in the components section.
     * @param groupId
     * @return boolean
     */
    public boolean isComponentGroup(String groupId) {
        return componentGroups.containsKey(groupId);
    }
}
