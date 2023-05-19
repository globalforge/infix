package com.globalforge.infix.qfix;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
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
 * Keeps track of all components that are repeating groups during dictionary
 * parse.
 * @author Michael
 */
public class GroupManager {
    final static Logger logger = LoggerFactory.getLogger(GroupManager.class);
    private final LinkedHashMap<String, RepeatingGroupBuilder> groupMap =
        new LinkedHashMap<String, RepeatingGroupBuilder>();

    /**
     * The first member of every member set in a group is the goup id. The group
     * id is the only non-repeating field in a group.
     * @param groupComponentName The component name for the group
     * @param groupId The first field in the group
     */
    public void setGroupId(String groupComponentName, String groupId) {
        RepeatingGroupBuilder grp = new RepeatingGroupBuilder(groupId);
        groupMap.put(groupComponentName, grp);
    }

    /**
     * Adds a member to the list of fields associated with a group.
     * @param compName The component or group name.
     * @param fieldName The field name.
     */
    public void addMember(String compName, String fieldName) {
        RepeatingGroupBuilder grp = groupMap.get(compName);
        grp.addMember(fieldName);
    }

    /**
     * Adds a nested component name to the list of fields associated with a
     * component.
     * @param compName the component name.
     * @param nestedCompName the nested component name.
     */
    public void addNestedComponent(String compName, String nestedCompName) {
        RepeatingGroupBuilder grp = groupMap.get(compName);
        grp.addMember("@" + nestedCompName);
    }

    /**
     * During a linear xml parse, insert a reference marker for any group that
     * has not been encountered yet and it thus not defined.
     * @param compName component name
     * @param nestedCompName nested component name
     */
    public void addNestedGroup(String compName, String nestedCompName) {
        RepeatingGroupBuilder grp = groupMap.get(compName);
        grp.addMember("#" + nestedCompName);
    }

    /**
     * Returns true if argument is a group name.
     * @param referredName the group name
     * @return boolean
     */
    public boolean containsGroupName(String referredName) {
        return groupMap.containsKey(referredName);
    }

    /**
     * Returns an unmodifiable set of group names.
     * @return Set{@literal <}String{@literal >} group names.
     */
    public Set<String> getGroupNames() {
        return Collections.unmodifiableSet(groupMap.keySet());
    }

    /**
     * Returns the group associated with a component or group name;
     * @param componentName The component name of the group
     * @return FixRepeatingGroup
     */
    public RepeatingGroupBuilder getGroup(String componentName) {
        return groupMap.get(componentName);
    }

    /**
     * Sanity check to ensure there are no member sets in any component that
     * contain duplicate field names.
     */
    public void checkForDuplicateGroups() {
        Iterator<String> compMems = groupMap.keySet().iterator();
        while (compMems.hasNext()) {
            String componentName = compMems.next();
            RepeatingGroupBuilder group = groupMap.get(componentName);
            List<String> groupMembers = group.getUnmodifiableMemberList();
            Set<String> memSet = new HashSet<String>(groupMembers.size());
            for (int i = 0; i < groupMembers.size(); i++) {
                String memberName = groupMembers.get(i);
                if (memSet.contains(memberName)) {
                    throw new RuntimeException("Duplicate member: " + memberName);
                } else {
                    memSet.add(memberName);
                }
            }
        }
    }

    /**
     * Print all group component names along with their list of members.
     */
    public void printGroupMap() {
        Set<Entry<String, RepeatingGroupBuilder>> compMems = groupMap.entrySet();
        Iterator<Entry<String, RepeatingGroupBuilder>> memSetIterator = compMems.iterator();
        GroupManager.logger.info("--- BEGIN Group Map ---");
        while (memSetIterator.hasNext()) {
            Entry<String, RepeatingGroupBuilder> memSetEntry = memSetIterator.next();
            String compName = memSetEntry.getKey();
            RepeatingGroupBuilder orderCtx = memSetEntry.getValue();
            GroupManager.logger.info("\t" + compName + ": " + orderCtx.getUnmodifiableMemberList());
        }
        GroupManager.logger.info("--- END Group Map ---");
    }
}
