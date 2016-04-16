package com.globalforge.infix.qfix;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Encapsulates fields in a repeating group
 *
 * @author Michael
 */
public class RepeatingGroupBuilder {
    protected final String groupId;
    protected final LinkedList<String> memberList = new LinkedList<String>();

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

    /**
     * Determines if a tag is part of this repeating group.
     *
     * @param tagNum The tag to check
     * @return boolean if true.
     */
    public boolean containsMember(String tagNum) {
        return memberList.contains(tagNum);
    }

    /**
     * Returns the actual set of all members of a repeating group.
     *
     * @return Set<String> The tag members belonging to this group.
     */
    public LinkedList<String> getMemberList() {
        return memberList;
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

    public String toString() {
        String ret = "groupID=" + memberList.get(0) + ", groupDelim=" + memberList.get(1)
            + ", allMembers=" + memberList;
        return ret;
    }
}
