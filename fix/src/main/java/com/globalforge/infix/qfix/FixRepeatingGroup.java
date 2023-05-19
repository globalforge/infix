package com.globalforge.infix.qfix;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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
 * The definition of a repeating group.
 * @author Michael
 */
public class FixRepeatingGroup {
    protected final String groupId;
    protected final String groupDelim;
    protected final LinkedHashSet<String> memberSet = new LinkedHashSet<String>();
    protected final LinkedHashSet<String> referenceSet = new LinkedHashSet<String>();

    /**
     * A repeating group is defined principally by it's id and delimiter.
     * @param id The tag in the fix spec that is used to indicate the number of
     * groups present in a particular repeating group (e.g., NoContraBrokers
     * (Tag = 382)).
     * @param delim The first tag in the repeating group. Used to determine when
     * a group repeats (e.g., ContraGroup (Tag = 375)).
     */
    public FixRepeatingGroup(String id, String delim) {
        groupId = id;
        groupDelim = delim;
    }

    /**
     * @return String The tag in the fix spec that is used to indicate the
     * number of groups present in a particular repeating group (e.g.,
     * NoContraBrokers (Tag = 382)).
     */
    public String getId() {
        return groupId;
    }

    /**
     * @return String The first tag in the repeating group. Used to determine
     * when a group repeats (e.g., ContraGroup (Tag = 375)).
     */
    public String getDelimiter() {
        return groupDelim;
    }

    /**
     * Determines if a tag is part of this repeating group.
     * @param tagNum The tag to check
     * @return boolean if true.
     */
    public boolean containsMember(String tagNum) {
        return memberSet.contains(tagNum);
    }

    /**
     * Determines of a field is the beginning of a nested repeating group within
     * a repeating group.
     * @param tagNum the member field of a group
     * @return boolean
     */
    public boolean containsReference(String tagNum) {
        return referenceSet.contains(tagNum);
    }

    /**
     * Returns the set of all members of a repeating group. This does not
     * include the id tag but does include the delimiter tag.
     * @return Set{@literal <}String{@literal >} The tag members belonging to this group.
     */
    public Set<String> getMemberSet() {
        return Collections.unmodifiableSet(memberSet);
    }

    /**
     * Returns all the nested group identifiers found within a repeating group
     * @return Set{@literal <}String{@literal >}
     */
    public Set<String> getReferenceSet() {
        return Collections.unmodifiableSet(referenceSet);
    }
}
