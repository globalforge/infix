package com.globalforge.infix.qfix;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the FIX fields in infix syntax for all the components and group
 * components found in a FIX data dictionary.
 * @author Michael
 */
public class DataStore {
    final static Logger logger = LoggerFactory.getLogger(DataStore.class);
    private final FieldDataStore fieldStore = new FieldDataStore();
    private final GroupDataStore groupStore = new GroupDataStore();

    ///////////////////////////
    // GROUP RELATED ACTIONS //
    ///////////////////////////
    public RepeatingGroupBuilder startMessageGroup(String curMessage,
        String groupId) {
        return groupStore.startMessageGroup(curMessage, groupId);
    }

    public void addMessageGroupMember(String curMessage, String groupId,
        String tagNum) {
        groupStore.addMessageGroupMember(curMessage, groupId, tagNum);
    }

    public boolean isComponentGroup(String groupId) {
        return groupStore.isComponentGroup(groupId);
    }

    public RepeatingGroupBuilder getComponentGroup(String groupId) {
        return groupStore.getComponentGroup(groupId);
    }

    public boolean isMessageGroup(String curMessage, String groupId) {
        return groupStore.isMessageGroup(curMessage, groupId);
    }

    public RepeatingGroupBuilder getMessageGroup(String curMessage,
        String groupId) {
        return groupStore.getMessageGroup(curMessage, groupId);
    }

    public Set<String> getRepeatingGroupMsgTypes() {
        return groupStore.getRepeatingGroupMsgTypes();
    }

    public Map<String, RepeatingGroupBuilder> getGroupsInMessage(
        String msgType) {
        return groupStore.getGroupsInMessage(msgType);
    }

    public void putComponentGroup(String grpId, RepeatingGroupBuilder rg) {
        groupStore.putComponentGroup(grpId, rg);;
    }

    public void addComponentGroupMember(String grpId, String mem) {
        groupStore.addComponentGroupMember(grpId, mem);
    }

    ///////////////////////////
    // FIELD RELATED ACTIONS //
    ///////////////////////////
    public LinkedList<String> getComponentContext(String componentName) {
        return fieldStore.getComponentContext(componentName);
    }

    public LinkedList<String> getGroupContext(String componentName) {
        return fieldStore.getGroupContext(componentName);
    }

    public void putGroupContext(String key, LinkedList<String> memList) {
        fieldStore.putGroupContext(key, memList);
    }

    public void putComponentContext(String key, LinkedList<String> memList) {
        fieldStore.putComponentContext(key, memList);
    }

    public boolean containsComponentName(String nameKey) {
        return fieldStore.containsComponentName(nameKey);
    }

    public void removeComponent(String nameKey) {
        fieldStore.removeComponent(nameKey);;
    }

    public Set<String> getGroupNameSet() {
        return fieldStore.getGroupNameSet();
    }

    public Set<String> getComponentNameSet() {
        return fieldStore.getComponentNameSet();
    }
}
