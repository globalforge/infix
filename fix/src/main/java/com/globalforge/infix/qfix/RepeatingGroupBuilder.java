package com.globalforge.infix.qfix;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
 * Encapsulates fields in a repeating group
 *
 * @author Michael
 */
public class RepeatingGroupBuilder {
    protected final String groupId;
    protected final LinkedList<String> memberList = new LinkedList<String>();
    protected final LinkedList<String> referenceList = new LinkedList<String>();
    protected boolean isNested = false;

    public RepeatingGroupBuilder(String groupId) {
        this.groupId = groupId;
        memberList.add(groupId);
    }

    /**
     * @return String The tag in the fix spec that is used to indicate the
     * number of groups present in a particular repeating group (e.g.,
     * NoContraBrokers (Tag = 382)).
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @return String The first tag in the repeating group. Used to determine
     * when a group repeats (e.g., ContraGroup (Tag = 375)).
     */
    public String getGroupDelim() {
        return memberList.get(1);
    }

    /**
     * Add member to the set of fields
     *
     * @param member field member of repeating group.
     */
    public void addMember(String member) {
        memberList.add(member);
    }

    public void addReference(String groupId) {
        referenceList.add(groupId);
    }

    public boolean isNested() {
        return isNested;
    }

    public void setNested(boolean n) {
        isNested = n;
    }

    /**
     * Determines if a tag is part of this repeating group.
     *
     * @param tagNum The tag to check
     * @return boolean if true.
     */
    public boolean containsMember(String tagNum) {
        return memberList.contains(tagNum);
    }

    public boolean containsReference(String tagNum) {
        return referenceList.contains(tagNum);
    }

    /**
     * Returns the actual set of all members of a repeating group.
     *
     * @return Set<String> The tag members belonging to this group.
     */
    public LinkedList<String> getMemberList() {
        return memberList;
    }

    public LinkedList<String> getReferenceList() {
        return referenceList;
    }

    /**
     * Returns an unmodifiable set copy of all members of a repeating group.
     *
     * @return Set<String> The tag members belonging to this group.
     */
    public List<String> getUnmodifiableMemberList() {
        return Collections.unmodifiableList(memberList);
    }

    /**
     * Returns a modifiable set copy of all members of a repeating group.
     *
     * @return Set<String> The tag members belonging to this group.
     */
    public LinkedList<String> getCopyOfMemberList() {
        return new LinkedList<String>(memberList);
    }

    @Override
    public String toString() {
        String ret = "groupID=" + memberList.get(0) + ", allMembers=" + memberList
            + ", allReferences=" + referenceList;
        return ret;
    }
}
