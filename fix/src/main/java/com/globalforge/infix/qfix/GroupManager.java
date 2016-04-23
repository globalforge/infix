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

/**
 * Keeps track of all group components that are not repeating groups.
 * 
 * @author Michael
 */
public class GroupManager {
    final static Logger logger = LoggerFactory.getLogger(GroupManager.class);
    private final LinkedHashMap<String, RepeatingGroupBuilder> groupMap =
        new LinkedHashMap<String, RepeatingGroupBuilder>();

    /**
     * The first member of every member set in a group is the goup id. The group
     * id is the only non-repeating field in a group.
     * 
     * @param groupComponentName The component name for the group
     * @param groupId The first field in the group
     */
    public void setGroupId(String groupComponentName, String groupId) {
        RepeatingGroupBuilder grp = new RepeatingGroupBuilder(groupId);
        groupMap.put(groupComponentName, grp);
    }

    /**
     * Adds a member to the list of fields associated with a group.
     * 
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
     * 
     * @param compName
     * @param nestedCompName
     */
    public void addNestedComponent(String compName, String nestedCompName) {
        RepeatingGroupBuilder grp = groupMap.get(compName);
        grp.addMember("@" + nestedCompName);
    }

    public void addNestedGroup(String compName, String nestedCompName) {
        RepeatingGroupBuilder grp = groupMap.get(compName);
        grp.addMember("#" + nestedCompName);
    }

    /**
     * Returns true if argument is a group name.
     * 
     * @param referredName the group name
     * @return boolean
     */
    public boolean containsGroupName(String referredName) {
        return groupMap.containsKey(referredName);
    }

    /**
     * Returns an unmodifiable set of group names.
     * 
     * @return Set<String> group names.
     */
    public Set<String> getGroupNames() {
        return Collections.unmodifiableSet(groupMap.keySet());
    }

    /**
     * Returns the group associated with a component or group name;
     * 
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
