package com.globalforge.infix.qfix;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FixGroupMgr {
    /** logger */
    final Logger logger = LoggerFactory.getLogger(FixGroupMgr.class);
    /** A map of MsgType to repeating groups mapped by groupId */
    protected final Map<String, FixRepeatingGroup> grpMap =
        new HashMap<String, FixRepeatingGroup>();
    /**
     * A stack of repeating group tags and info in the process of being parsed
     */
    private final Deque<FixGroupInProgress> grpStack = new ArrayDeque<FixGroupInProgress>(10000);

    /**
     * Assigns the correct context string to a Fix tag taking into account
     * whether that tag number exists as part of a repeating group and which
     * level of nesting within a group it may appear.
     * 
     * @param msgType The message type that owns the tag.
     * @param tagNum The fix tag number
     * @return FixFieldContext the context and it's order.
     */
    public String getContext(String tagNum) {
        String ctxString = '&' + tagNum;
        FixRepeatingGroup group = getGroup(tagNum);
        // Start of a group. Might be nested.
        if (group != null) {
            FixGroupInProgress gip = grpStack.peek();
            if (gip != null) {
                FixRepeatingGroup curGrp = gip.getGroup();
                if (!curGrp.containsMember(tagNum)) {
                    // 2 repeating groups, side by side (not nested). We're done
                    // with the current group.
                    grpStack.pop();
                    return getContext(tagNum);
                }
            }
            ctxString = getCurrentGoupContext() + ctxString;
            gip = new FixGroupInProgress(group);
            grpStack.push(gip);
            return ctxString;
        }
        FixGroupInProgress gip = grpStack.peek();
        // Not part of any group.
        if (gip == null) {
            return ctxString;
        }
        // Stack not empty and tagNum not groupID. Must be member of group or
        // group on stack is done.
        FixRepeatingGroup curGrp = gip.getGroup();
        if (curGrp.containsMember(tagNum)) {
            if (tagNum.equals(curGrp.getDelimiter())) {
                gip.incCurGoupNumber();
            }
            ctxString = getCurrentGoupContext() + ctxString;
        } else {
            grpStack.pop();
            ctxString = getContext(tagNum);
        }
        return ctxString;
    }

    /**
     * Determines if a repeating group may be present in a particular message
     * type.
     * 
     * @param msgType The message type.
     * @param groupId The identifier of the group.
     * @return true or false.
     */
    public boolean containsGrpId(String groupId) {
        return grpMap.containsKey(groupId);
    }

    /**
     * Given a message type and a groupId return the repeating group.
     * 
     * @param msgType the message type being parsed or referenced.
     * @param groupId the unique tag number that identifies the start of a
     * particular repeating group.
     * @return FixRepeatingGroup the repeating group.
     */
    public FixRepeatingGroup getGroup(String groupId) {
        return grpMap.get(groupId);
    }

    /**
     * Adds a repeating group to a message type. This method is called by the
     * generated code and there should be no reason for a programmer to call it.
     * 
     * @param msgType The messgae type.
     * @param g The repeating group to add.
     */
    protected void putGroup(String groupId, FixRepeatingGroup g) {
        grpMap.put(groupId, g);
    }

    private String getCurrentGoupContext() {
        String grpCtx = "";
        Iterator<FixGroupInProgress> it = grpStack.descendingIterator();
        while (it.hasNext()) {
            FixGroupInProgress gip = it.next();
            grpCtx += gip.toString();
        }
        return grpCtx;
    }
}
