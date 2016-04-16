package com.globalforge.infix.qfix;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of all components that are not repeating groups.
 * @author Michael
 */
public class ComponentManager {
    final static Logger logger = LoggerFactory
        .getLogger(ComponentManager.class);
    private final LinkedHashMap<String, LinkedList<String>> componentMap = new LinkedHashMap<String, LinkedList<String>>();

    /**
     * Initialize a map of fields associated with the component name
     * @param name the component name.
     */
    public void initializeComponent(String name) {
        ComponentManager.logger.info("Intializing component: " + name);
        LinkedList<String> memberList = new LinkedList<String>();
        componentMap.put(name, memberList);
    }

    /**
     * Adds a member to the list of fields associated with a component.
     * @param compName The component name.
     * @param fieldName The field name.
     */
    public void addMember(String compName, String fieldName) {
        LinkedList<String> memberList = componentMap.get(compName);
        memberList.add(fieldName);
    }

    /**
     * Adds a nested component name to the list of fields associated with a
     * component.
     * @param compName
     * @param nestedCompName
     */
    public void addNestedComponent(String compName, String nestedCompName) {
        LinkedList<String> memberList = componentMap.get(compName);
        memberList.add("@" + nestedCompName);
    }

    /**
     * Sanity check to ensure there are no member sets in any component that
     * contain duplicate field names.
     */
    public void checkForDuplicateMembers() {
        Iterator<String> compMems = componentMap.keySet().iterator();
        while (compMems.hasNext()) {
            String componentName = compMems.next();
            LinkedList<String> fieldNameList = componentMap.get(componentName);
            Set<String> memSet = new HashSet<String>(fieldNameList.size());
            for (int i = 0; i < fieldNameList.size(); i++) {
                String memberName = fieldNameList.get(i);
                if (memSet.contains(memberName)) {
                    throw new RuntimeException(
                        "Duplicate member: " + memberName);
                } else {
                    memSet.add(memberName);
                }
            }
        }
    }

    /**
     * Returns an unmodifiable set of component names.
     * @return Set<String> componentNames.
     */
    public Set<String> getComponentNames() {
        return Collections.unmodifiableSet(componentMap.keySet());
    }

    /**
     * Returns a modifable list of field names given a component name.
     * @param componentName The component name.
     * @return LinkedList<String> The associated field names.
     */
    LinkedList<String> getMemberList(String componentName) {
        return componentMap.get(componentName);
    }

    /**
     * Prints the current state of the component map listing all fields in all
     * components.
     */
    public void printComponentMap() {
        Set<Entry<String, LinkedList<String>>> compMems = componentMap
            .entrySet();
        Iterator<Entry<String, LinkedList<String>>> memSetIterator = compMems
            .iterator();
        ComponentManager.logger.info("--- BEGIN Comp Map ---");
        while (memSetIterator.hasNext()) {
            Entry<String, LinkedList<String>> memSetEntry = memSetIterator
                .next();
            String compName = memSetEntry.getKey();
            LinkedList<String> orderCtx = memSetEntry.getValue();
            ComponentManager.logger.info("\t" + compName + ": " + orderCtx);
        }
        ComponentManager.logger.info("--- End Comp Map ---");
    }
}
