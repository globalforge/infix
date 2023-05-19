package com.globalforge.infix.qfix;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
The MIT License (MIT)

Copyright (c) 2019-2022 Global Forge LLC

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
 * Stores the FIX fields in infix syntax for all the components and group
 * components found in a FIX data dictionary.
 * @author Michael
 */
public class DataStore {
    final static Logger logger = LoggerFactory.getLogger(DataStore.class);
    /** all the data related to fields and their contexts */
    private final FieldDataStore fieldStore = new FieldDataStore();
    /** all the data related to groups and their members */
    private final GroupDataStore groupStore = new GroupDataStore();

    ///////////////////////////
    // GROUP RELATED ACTIONS //
    ///////////////////////////
    /**
     * Begin a repeating group in a message type.
     * @param curMessage The message type
     * @param groupId The first member of a repeating group
     * @return RepeatingGroupBuilder group in progress
     */
    public RepeatingGroupBuilder startMessageGroup(String curMessage, String groupId) {
        return groupStore.startMessageGroup(curMessage, groupId);
    }

    /**
     * Begin a repeating group in a message type.
     * @param curMessage The message type
     * @param groupId The first member of a repeating group
     * @return RepeatingGroupBuilder group in progress
     */
    public RepeatingGroupBuilder setNestedGroup(String curMessage, String groupId) {
        return groupStore.setNestedGroup(curMessage, groupId);
    }

    /**
     * Add a member field to a repeating group
     * @param curMessage the message type
     * @param groupId the group identifier
     * @param tagNum the member field
     */
    public void addMessageGroupMember(String curMessage, String groupId, String tagNum) {
        groupStore.addMessageGroupMember(curMessage, groupId, tagNum);
    }

    /**
     * Add a reference to nested repeating group inside a repeating group
     * @param curMessage the message type
     * @param groupId the outer group
     * @param tagNum the identifier of the nested group.
     */
    public void addMessageGroupReference(String curMessage, String groupId, String tagNum) {
        groupStore.addMessageGroupReference(curMessage, groupId, tagNum);
    }

    /**
     * Whether this group identifier was defined within a component block.
     * @param groupId The group identifier.
     * @return true if true
     */
    public boolean isComponentGroup(String groupId) {
        return groupStore.isComponentGroup(groupId);
    }

    /**
     * Given a group identifier return the group which was defined in a
     * component block.
     * @param groupId The group identifier.
     * @return RepeatingGroupBuilder The group.
     */
    public RepeatingGroupBuilder getComponentGroup(String groupId) {
        return groupStore.getComponentGroup(groupId);
    }

    /**
     * Whether this group identifier was defined within a message block.
     * @param curMessage the current message.
     * @param groupId The group identifier.
     * @return true if true
     */
    public boolean isMessageGroup(String curMessage, String groupId) {
        if ("48".equals(groupId)) {
            // System.out.println();
        }
        return groupStore.isMessageGroup(curMessage, groupId);
    }

    /**
     * Given a group identifier return the group which was defined in a message
     * block.
     * @param curMessage the current message.
     * @param groupId The group identifier.
     * @return RepeatingGroupBuilder The group.
     */
    public RepeatingGroupBuilder getMessageGroup(String curMessage, String groupId) {
        return groupStore.getMessageGroup(curMessage, groupId);
    }

    /**
     * Obtain a set of all message types containing at least 1 repeating group
     * @return Set{@literal <}String{@literal >} The set.
     */
    public Set<String> getRepeatingGroupMsgTypes() {
        return groupStore.getRepeatingGroupMsgTypes();
    }

    /**
     * Obtain a map of group identifiers to groups for all groups defined within
     * the message block defined by the message type argument.
     * @param msgType The message type.
     * @return Map{@literal <}String, RepeatingGroupBuilder{@literal >} The map.
     */
    public Map<String, RepeatingGroupBuilder> getGroupsInMessage(String msgType) {
        return groupStore.getGroupsInMessage(msgType);
    }

    /**
     * Store a repeating group defined in a component block.
     * @param grpId The group identifier
     * @param rg The group.
     */
    public void putComponentGroup(String grpId, RepeatingGroupBuilder rg) {
        if ("48".equals(grpId)) {
            // System.out.println();
        }
        groupStore.putComponentGroup(grpId, rg);;
    }

    /**
     * Add a member to a group defined in a component block.
     * @param grpId The group identifier
     * @param mem The member
     */
    public void addComponentGroupMember(String grpId, String mem) {
        groupStore.addComponentGroupMember(grpId, mem);
    }

    /**
     * Returns true if a given field is the start of a nested repeating group
     * defined in a message block.
     * @param curMessage The message type
     * @param groupId The group identifier of the outer group.
     * @param tagNum The group identifier of the nested group.
     * @return true if true.
     */
    public boolean isMessageGroupReference(String curMessage, String groupId, String tagNum) {
        return groupStore.isMessageGroupReference(curMessage, groupId, tagNum);
    }

    ///////////////////////////
    // FIELD RELATED ACTIONS //
    ///////////////////////////
    /**
     * Returns a list of fields in Infix context for a component
     * @param componentName The component
     * @return LinkedList{@literal <}String{@literal >} The list
     */
    public LinkedList<String> getComponentContext(String componentName) {
        return fieldStore.getComponentContext(componentName);
    }

    /**
     * @param componentName The component
     * @see FieldDataStore#getGroupContext(String)
     * @return LinkedList{@literal <}String{@literal >} The list
     */
    public LinkedList<String> getGroupContext(String componentName) {
        return fieldStore.getGroupContext(componentName);
    }

    /**
     * @param key The group component name
     * @param memList The list of fields.
     * @see FieldDataStore#putGroupContext(String, LinkedList)
     */
    public void putGroupContext(String key, LinkedList<String> memList) {
        fieldStore.putGroupContext(key, memList);
    }

    /**
     * @param key The component name.
     * @param memList The list of fields
     * @see FieldDataStore#putComponentContext(String, LinkedList)
     */
    public void putComponentContext(String key, LinkedList<String> memList) {
        fieldStore.putComponentContext(key, memList);
    }

    /**
     * @param nameKey The key
     * @see FieldDataStore#containsComponentName(String)
     * @return true if the set of component names includes the key.
     */
    public boolean containsComponentName(String nameKey) {
        return fieldStore.containsComponentName(nameKey);
    }

    /**
     * @param nameKey The component name.
     * @see FieldDataStore#removeComponent(String)
     */
    public void removeComponent(String nameKey) {
        fieldStore.removeComponent(nameKey);;
    }

    /**
     * @see FieldDataStore#getGroupNameSet()
     * @return Set{@literal <}String{@literal >} The set of component names.
     */
    public Set<String> getGroupNameSet() {
        return fieldStore.getGroupNameSet();
    }

    /**
     * @see FieldDataStore#getComponentNameSet()
     * @return Set{@literal <}String{@literal >} The set of component names.
     */
    public Set<String> getComponentNameSet() {
        return fieldStore.getComponentNameSet();
    }
}
