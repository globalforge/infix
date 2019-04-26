package com.globalforge.infix.qfix;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
The MIT License (MIT)

Copyright (c) 2019-2020 Global Forge LLC

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
 * Contains all information needed by Infix regarding fields, their contexts and
 * orders as derived from the quick fix data dictionaries.
 * @author Michael C. Starkie
 */
public class FieldDataStore {
    private final static Logger logger = LoggerFactory.getLogger(FieldDataStore.class);
    /** component name to a list of fields in infix syntax */
    private final LinkedHashMap<String, LinkedList<String>> componentToInfixFieldsMap =
        new LinkedHashMap<String, LinkedList<String>>();
    /** group component name to a list of fields in infix syntax */
    private final LinkedHashMap<String, LinkedList<String>> groupToInfixFieldsMap =
        new LinkedHashMap<String, LinkedList<String>>();

    /**
     * Associates a group component name with a list of fields in Infix syntax.
     * @param key The group component name
     * @param memList The list of fields.
     */
    void putGroupContext(String key, LinkedList<String> memList) {
        groupToInfixFieldsMap.put(key, memList);
    }

    /**
     * Returns the list of fields in Infix syntax associated with a group
     * component name.
     * @param referredName the name of the group component.
     * @return LinkedList<String> the list of fields.
     */
    public LinkedList<String> getGroupContext(String referredName) {
        return groupToInfixFieldsMap.get(referredName);
    }

    /**
     * Associates a component name with a list of fields in Infix syntax.
     * @param key The component name.
     * @param memList The list of fields.
     */
    void putComponentContext(String key, LinkedList<String> memList) {
        componentToInfixFieldsMap.put(key, memList);
    }

    /**
     * Returns a list of fields in Infix syntax associated with a component.
     * @param referredName The name of the component.
     * @return LinkedList<String> The list of fields.
     */
    public LinkedList<String> getComponentContext(String referredName) {
        return componentToInfixFieldsMap.get(referredName);
    }

    /**
     * Returns the complete set of group component names if a FIX dictionary.
     * @return Set<String> The set of group names.
     */
    Set<String> getGroupNameSet() {
        return groupToInfixFieldsMap.keySet();
    }

    /**
     * Tests if the set of component names contains the given key.
     * @param nameKey The key
     * @return true if the set of component names includes the key.
     */
    boolean containsComponentName(String nameKey) {
        return componentToInfixFieldsMap.containsKey(nameKey);
    }

    /**
     * Tests if the set of group names contains the given key.
     * @param nameKey The key.
     * @return true if the set of group names includes the key.
     */
    boolean containsGroupName(String nameKey) {
        return groupToInfixFieldsMap.containsKey(nameKey);
    }

    /**
     * Removes the set of fields associated with a component name.
     * @param nameKey The component name.
     */
    void removeComponent(String nameKey) {
        componentToInfixFieldsMap.remove(nameKey);
    }

    /**
     * Returns the complete set of component names (non-groups) found in a data
     * dictionary.
     * @return Set<String> The set of component names.
     */
    Set<String> getComponentNameSet() {
        return componentToInfixFieldsMap.keySet();
    }
}
