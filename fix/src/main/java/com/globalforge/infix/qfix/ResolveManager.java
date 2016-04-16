package com.globalforge.infix.qfix;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves references to components and groups. An algorithm to convert
 * component names into an Infix like context map.
 *
 * @author Michael
 */
public class ResolveManager {
    final static Logger logger = LoggerFactory.getLogger(ResolveManager.class);
    private final LinkedHashMap<String, LinkedList<String>> intermidiateCompMap =
        new LinkedHashMap<String, LinkedList<String>>();
    private final LinkedHashMap<String, LinkedList<String>> intermidiateGroupMap =
        new LinkedHashMap<String, LinkedList<String>>();
    private final ComponentManager compMgr;
    private final GroupManager grpMgr;
    private final FieldParser fParser;
    private final DataStore ctxStore;

    /**
     * @param compMgr {@link ComponentManager}
     * @param grpMgr {@link GroupManager}
     * @param fParser {@link FieldParser}
     */
    public ResolveManager(ComponentManager compMgr, GroupManager grpMgr, FieldParser fParser) {
        this.compMgr = compMgr;
        this.grpMgr = grpMgr;
        this.fParser = fParser;
        this.ctxStore = new DataStore();
    }

    /**
     * Runs the resolver algorithm which converts quick fix component names to
     * infix.
     */
    public void runAlgo() {
        compMgr.checkForDuplicateMembers();
        grpMgr.checkForDuplicateGroups();
        distinguishGroupReferencesInComponents();
        distinguishGroupReferencesInGroups();
        resolveComponentChainsInComponents();
        resolveComponentChainsInGroups();
        resolveGroupChainsInGroups();
        resolveGroupChainsInComponents();
        removeGroupsFromComponents();
        checkForAmbiguousNames();
        checkFinalForDuplicates();
        // printGroupManager();
        // printIntermediateGroups();
    }

    public DataStore getContextStore() {
        return this.ctxStore;
    }

    /**
     * Replaces the reference indicator '@' with '#' if a component name points
     * to a group in the list of fields associated with a component.
     * Distinguishes component references from group references.
     */
    private void distinguishGroupReferencesInComponents() {
        Iterator<String> compMems = compMgr.getComponentNames().iterator();
        while (compMems.hasNext()) {
            String componentName = compMems.next();
            LinkedList<String> fieldNameList = compMgr.getMemberList(componentName);
            for (int i = 0; i < fieldNameList.size(); i++) {
                String memberName = fieldNameList.get(i);
                int refIdx = memberName.indexOf("@");
                if (refIdx >= 0) {
                    String referredName = memberName.substring(refIdx + 1);
                    if (grpMgr.containsGroupName(referredName)) {
                        memberName = memberName.replace('@', '#');
                        fieldNameList.remove(i);
                        fieldNameList.add(i, memberName);
                    }
                }
            }
        }
    }

    /**
     * Replaces the reference indicator '@' with '#' if a group name points to a
     * group in the list of fields associated with a group. Distinguishes
     * component references from group references.
     */
    private void distinguishGroupReferencesInGroups() {
        Iterator<String> grpMems = grpMgr.getGroupNames().iterator();
        while (grpMems.hasNext()) {
            String groupName = grpMems.next();
            LinkedList<String> fieldNameList = grpMgr.getGroup(groupName).getMemberList();
            for (int i = 0; i < fieldNameList.size(); i++) {
                String memberName = fieldNameList.get(i);
                int refIdx = memberName.indexOf("@");
                if (refIdx >= 0) {
                    String referredName = memberName.substring(refIdx + 1);
                    if (grpMgr.containsGroupName(referredName)) {
                        memberName = memberName.replace('@', '#');
                        fieldNameList.remove(i);
                        fieldNameList.add(i, memberName);
                    }
                }
            }
        }
    }

    /**
     * Given a component name from the quick fix data dictionary, convert all
     * the member fields contained within it to infix syntax.
     *
     * @param member The component name as defined by a quickfix dictionary.
     * @param resolveList The list of field members conververted to infix
     * syntax.
     * @return LinkedList<String> THe list of member fields converted to infix
     * syntax.
     */
    private LinkedList<String> componentNameToContext(String member,
        LinkedList<String> resolveList) {
        LinkedList<String> instExt = compMgr.getMemberList(member);
        for (String mem : instExt) {
            int cIdx = mem.indexOf("@");
            int gIdx = mem.indexOf("#");
            if ((cIdx < 0) && (gIdx < 0)) {
                String ctx = "&" + this.fParser.getTagNum(mem);
                resolveList.add(ctx);
            } else {
                if (gIdx < 0) {
                    String referredName = mem.substring(cIdx + 1);
                    resolveList
                        .addAll(componentNameToContext(referredName, new LinkedList<String>()));
                } else {
                    resolveList.add(mem);
                }
            }
        }
        return resolveList;
    }

    /**
     * Traverse all component names that are not groups in a quick fix data
     * dictionary and convert their member fields into infix syntax. Operates
     * over the set of pure components (i.e., components that are not groups).
     */
    private void resolveComponentChainsInComponents() {
        Iterator<String> compMems = compMgr.getComponentNames().iterator();
        while (compMems.hasNext()) {
            String key = compMems.next();
            LinkedList<String> memList = componentNameToContext(key, new LinkedList<String>());
            intermidiateCompMap.put(key, memList);
        }
    }

    /**
     * Given a group component name from the quick fix data dictionary, convert
     * all the component (excluding group components) member fields contained
     * within it to infix syntax.
     *
     * @param member The name of the group component as defined by the quickfix
     * data dictionary.
     * @return LinkedList<String> The list of all resolved component member
     * fields
     */
    private LinkedList<String> groupNameToContext(String member) {
        LinkedList<String> instExt = grpMgr.getGroup(member).getMemberList();
        LinkedList<String> resolveList = new LinkedList<String>();
        for (int i = 0; i < instExt.size(); i++) {
            String mem = instExt.get(i);
            int cIdx = mem.indexOf("@");
            if (cIdx >= 0) {
                String referredName = mem.substring(cIdx + 1);
                LinkedList<String> resolvedCompList = this.intermidiateCompMap.get(referredName);
                resolveList.addAll(resolvedCompList);
            } else {
                int gIdx = mem.indexOf("#");
                if (gIdx < 0) {
                    String ctx = "&" + this.fParser.getTagNum(mem);
                    resolveList.add(ctx);
                } else {
                    resolveList.add(mem);
                }
            }
        }
        return resolveList;
    }

    /**
     * Traverse all component names that are not groups in a quick fix data
     * dictionary and convert their member fields into infix syntax. Operates
     * over the set of group components.
     */
    private void resolveComponentChainsInGroups() {
        Iterator<String> compMems = grpMgr.getGroupNames().iterator();
        while (compMems.hasNext()) {
            String key = compMems.next();
            LinkedList<String> resolveList = groupNameToContext(key);
            ResolveManager.logger.info(key + resolveList);
            intermidiateGroupMap.put(key, resolveList);
        }
    }

    private void startGroup(String grpId) {
        grpId = grpId.substring(1);
        RepeatingGroupBuilder rg = new RepeatingGroupBuilder(grpId);
        ctxStore.putComponentGroup(grpId, rg);
    }

    private void addGroupMember(String grpId, String mem) {
        grpId = grpId.substring(1);
        mem = mem.substring(1);
        ctxStore.addComponentGroupMember(grpId, mem);
    }

    /**
     * Given a group component name from the quick fix data dictionary, convert
     * all the group component member fields contained within it to infix
     * syntax.
     *
     * @param member The name of the group component defined in the quickfix
     * data dictionary.
     * @param grpCtx The infix context in progress.
     * @param resolveList The final list of field members of the group in infix
     * syntax.
     */
    private void resolveGroupsInGroup(String member, String grpCtx,
        LinkedList<String> resolveList) {
        LinkedList<String> instExt = this.intermidiateGroupMap.get(member);
        String groupId = null;
        for (int i = 0; i < instExt.size(); i++) {
            String mem = instExt.get(i);
            int cIdx = mem.indexOf("#");
            if (cIdx >= 0) {
                String referredName = mem.substring(cIdx + 1);
                resolveGroupsInGroup(referredName, grpCtx, resolveList);
            } else {
                String memCtx = grpCtx + mem;
                if (i == 0) {
                    groupId = mem;
                    resolveList.add(memCtx);
                    startGroup(groupId);
                    grpCtx = memCtx + "[*]->";
                } else {
                    resolveList.add(memCtx);
                    addGroupMember(groupId, mem);
                }
            }
        }
    }

    /**
     * Traverse all component names that are groups in a quick fix data
     * dictionary and convert their member fields into infix syntax. Operates
     * over the set of group components.
     */
    private void resolveGroupChainsInGroups() {
        Iterator<String> compMems = this.intermidiateGroupMap.keySet().iterator();
        while (compMems.hasNext()) {
            String key = compMems.next();
            LinkedList<String> memList = new LinkedList<String>();
            resolveGroupsInGroup(key, "", memList);
            ctxStore.putGroupContext(key, memList);
        }
    }

    /**
     * Given a pure component (not a group) name from the quick fix data
     * dictionary, convert all the group component member fields contained
     * within it to infix syntax.
     *
     * @param member The name of the component.
     * @param grpCtx The nested infix syntax in progress.
     * @param resolveList The final list of member fields converted to infix
     * syntax.
     */
    private void resolveGroupsInComp(String member, String grpCtx, LinkedList<String> resolveList) {
        LinkedList<String> instExt = this.intermidiateCompMap.get(member);
        for (int i = 0; i < instExt.size(); i++) {
            String mem = instExt.get(i);
            int cIdx = mem.indexOf("#");
            if (cIdx >= 0) {
                String referredName = mem.substring(cIdx + 1);
                LinkedList<String> completeGroup = ctxStore.getGroupContext(referredName);
                resolveList.addAll(completeGroup);
            } else {
                resolveList.add(mem);
            }
        }
    }

    /**
     * Traverse all component names that are components (not groups) in a quick
     * fix data dictionary and convert their group component member fields into
     * infix syntax. Operates over the set of group components.
     */
    private void resolveGroupChainsInComponents() {
        Iterator<String> compMems = this.intermidiateCompMap.keySet().iterator();
        while (compMems.hasNext()) {
            String key = compMems.next();
            LinkedList<String> memList = new LinkedList<String>();
            resolveGroupsInComp(key, "", memList);
            ctxStore.putComponentContext(key, memList);
        }
    }

    /**
     * Removes references to group components in the map of pure components
     * because they are already defined in the map of group components.
     */
    private void removeGroupsFromComponents() {
        Set<String> groupNames = ctxStore.getGroupNameSet();
        Iterator<String> groupIt = groupNames.iterator();
        while (groupIt.hasNext()) {
            String grpKey = groupIt.next();
            if (ctxStore.containsComponentName(grpKey)) {
                ctxStore.removeComponent(grpKey);
            }
        }
    }

    /**
     * Checks for duplicates components and groups.
     */
    private void checkForAmbiguousNames() {
        Set<String> compNames = ctxStore.getComponentNameSet();
        Iterator<String> compIt = compNames.iterator();
        HashSet<String> ckSet = new HashSet<String>();
        while (compIt.hasNext()) {
            String compKey = compIt.next();
            boolean added = ckSet.add(compKey);
            if (!added) { throw new RuntimeException(
                "duplicate key in complete comp map: " + compKey); }
        }
        Set<String> groupNames = ctxStore.getGroupNameSet();
        Iterator<String> groupIt = groupNames.iterator();
        while (groupIt.hasNext()) {
            String grpKey = groupIt.next();
            boolean added = ckSet.add(grpKey);
            if (!added) { throw new RuntimeException(
                "duplicate key in complete group map: " + grpKey); }
        }
    }

    /**
     * Does a check of the fully resolved fields and ensures there are no
     * duplicate fields in any components member list
     */
    private void checkFinalForDuplicates() {
        Set<String> compNames = ctxStore.getComponentNameSet();
        Iterator<String> compMems = compNames.iterator();
        while (compMems.hasNext()) {
            String componentName = compMems.next();
            LinkedList<String> fieldNameList = ctxStore.getComponentContext(componentName);
            Set<String> memSet = new HashSet<String>(fieldNameList.size());
            for (int i = 0; i < fieldNameList.size(); i++) {
                String memberName = fieldNameList.get(i);
                if (memSet.contains(memberName)) {
                    throw new RuntimeException("Duplicate member: " + memberName);
                } else {
                    memSet.add(memberName);
                }
            }
        }
        Set<String> groupNames = ctxStore.getGroupNameSet();
        compMems = groupNames.iterator();
        while (compMems.hasNext()) {
            String componentName = compMems.next();
            if (ctxStore.containsComponentName(componentName)) { throw new RuntimeException(
                "Can't have component in both comp table and group table: " + componentName); }
            LinkedList<String> fieldNameList = ctxStore.getGroupContext(componentName);
            Set<String> memSet = new HashSet<String>(fieldNameList.size());
            for (int i = 0; i < fieldNameList.size(); i++) {
                String memberName = fieldNameList.get(i);
                if (memSet.contains(memberName)) {
                    throw new RuntimeException("Duplicate member: " + memberName);
                } else {
                    memSet.add(memberName);
                }
            }
        }
    }

    /**
     * Prints out all members for the set of components.
     */
    public void printCompletedComponents() {
        ResolveManager.logger.info("--- BEGIN COMPLETED COMPONENTS ---");
        Set<String> compNames = ctxStore.getComponentNameSet();
        Iterator<String> compMems = compNames.iterator();
        while (compMems.hasNext()) {
            String compName = compMems.next();
            LinkedList<String> fieldNameList = ctxStore.getComponentContext(compName);
            ResolveManager.logger.info("\t" + compName + ": " + fieldNameList);
        }
        ResolveManager.logger.info("--- END COMPLETED COMPONENTS ---");
    }

    /**
     * Prints out all members for the set of group components.
     */
    public void printCompletedGroups() {
        ResolveManager.logger.info("--- BEGIN COMPLETED GROUPS ---");
        Set<String> groupNames = ctxStore.getGroupNameSet();
        Iterator<String> grpMems = groupNames.iterator();
        while (grpMems.hasNext()) {
            String groupName = grpMems.next();
            LinkedList<String> fieldNameList = ctxStore.getGroupContext(groupName);
            ResolveManager.logger.info("\t" + groupName + ": " + fieldNameList);
        }
        ResolveManager.logger.info("--- END COMPLETED GROUPS ---");
    }

    /**
     * Prints out all members for the set of group components.
     */
    public void printIntermediateGroups() {
        ResolveManager.logger.info("--- BEGIN INTERMEDIATE GROUPS ---");
        Set<String> groupNames = intermidiateGroupMap.keySet();
        Iterator<String> grpMems = groupNames.iterator();
        while (grpMems.hasNext()) {
            String groupName = grpMems.next();
            LinkedList<String> fieldNameList = intermidiateGroupMap.get(groupName);
            ResolveManager.logger.info("\t" + groupName + ": " + fieldNameList);
        }
        ResolveManager.logger.info("--- END INTERMEDIATE GROUPS ---");
    }

    public void printGroupManager() {
        this.grpMgr.printGroupMap();
    }
}
