package com.globalforge.infix;

/*-
 The MIT License (MIT)

 Copyright (c) 2015 Global Forge LLC

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
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The parent class for all generated source files beginning with "FIX". This
 * class serves 2 functions. 1) It maps a MsgType to set of RepeatingGroups that
 * may be found within a message of that type. A mapping of message types to
 * repeating groups is needed because when a fix message is first parsed the
 * system needs to be aware of whether a particular tag is part of a repeating
 * group and what function that tags serves within the repeating group. This
 * knowledge is needed to assign a proper context string to the tag so it can be
 * referenced by a rule (@see FixFieldContext) and also to know when the group
 * repeats during a message parse so that any context assigned to a repeating
 * tag will be aware of it's nesting within the group. 2) The group keeps state
 * on the location of groups within a message. This is used so that any rule
 * action that adds a tag to a repeating group or adds a nested repeating group
 * within a group will be ordered correctly when the transformed message is
 * finally generated..
 * 
 * @author Michael Starkie
 */
abstract class FixGroupMgr {
    /** logger */
    final static Logger logger = LoggerFactory.getLogger(FixGroupMgr.class);
    /** A map of MsgType to repeating groups mapped by groupId */
    protected static final Map<String, Map<String, FixRepeatingGroup>> grpMap =
        new HashMap<String, Map<String, FixRepeatingGroup>>();
    /** A stack of repeating group tags and info in the process of being parsed */
    private final Deque<GroupInProgress> grpStack =
        new LinkedBlockingDeque<GroupInProgress>();
    /** A map of group context to position in a message */
    private final Map<String, BigDecimal> grpCtxPos =
        new HashMap<String, BigDecimal>();
    /**
     * Assigns an increasing number to tag contexts as they appear in a fix
     * message thus preserving their order.
     */
    private int tagCount = 0;

    /**
     * Clears the state of static data.
     */
    static void cleanStaticData() {
        grpMap.clear();
    }

    /**
     * Determines if a repeating group may be present in a particular message
     * type.
     * 
     * @param msgType The message type.
     * @param groupId The identifier of the group.
     * @return true or false.
     */
    static boolean containsGrpId(String msgType, String groupId) {
        boolean hasKey = false;
        Map<String, FixRepeatingGroup> m = FixGroupMgr.grpMap.get(msgType);
        if (m != null) {
            hasKey = m.containsKey(groupId);
        }
        return hasKey;
    }

    /**
     * Given a message type and a groupId return the repeating group.
     * 
     * @param msgType the message type being parsed or referenced.
     * @param groupId the unique tag number that identifies the start of a
     * particular repeating group.
     * @return FixRepeatingGroup the repeating group.
     */
    static FixRepeatingGroup getGroup(String msgType, String groupId) {
        FixRepeatingGroup g = null;
        Map<String, FixRepeatingGroup> m = FixGroupMgr.grpMap.get(msgType);
        if (m != null) {
            g = m.get(groupId);
        }
        return g;
    }

    /**
     * Adds a repeating group to a message type. This method is called by the
     * generated code and there should be no reason for a programmer to call it.
     * 
     * @param msgType The messgae type.
     * @param g The repeating group to add.
     */
    static void putGroup(String msgType, FixRepeatingGroup g) {
        Map<String, FixRepeatingGroup> m = FixGroupMgr.grpMap.get(msgType);
        if (m == null) {
            m = new HashMap<String, FixRepeatingGroup>();
            FixGroupMgr.grpMap.put(msgType, m);
        }
        m.put(g.groupId, g);
    }

    /**
     * Returns a FixFieldContext which describes a fix field in terms of how it
     * might be referenced in a rule and also it's order as it was found in the
     * message.
     * 
     * @see {@link FixFieldContext}
     * @param msgType The fix message type that owns the field number and value
     * @param tagNum The fix field number
     * @param tagVal The fix field value
     * @param rememberGroups whether to keep track of fields found in a
     * repeating group.
     * @return FixFieldContext The field context.
     */
    FixFieldContext getContext(String msgType, String tagNum, String tagVal,
        boolean rememberGroups) {
        tagCount += 1;
        String ctxString = '&' + tagNum;
        FixFieldContext ctx = getFieldContext(ctxString);
        if (msgType == null) { return ctx; }
        // if (!FixGroupMgr.isGroupMember(msgType, tagNum)) { return ctx; }
        // If the nesting level is not zero then build up the context
        if (FixGroupMgr.containsGrpId(msgType, tagNum)) {
            // tagNum is the start of a repeating group and group fields
            // use special syntax in the context string.
            ctx = getNestedCtx(msgType, tagNum, true);
            // if not parsing a new message there is no need to keep track
            // of repeating groups in order to assign the correct context string
            // to each field. It's up to the user to ensure the proper rule
            // context and context order of any new field added to a message
            // that may be part of a repeating group.
            if (rememberGroups) {
                FixRepeatingGroup repGrp =
                    FixGroupMgr.getGroup(msgType, tagNum);
                grpStack.push(new GroupInProgress(repGrp, Integer
                    .parseInt(tagVal), tagNum));
            }
        } else {
            // if empty, tagNum is not part of any repeating group.
            boolean canBeNested = doRepeatingBlock(tagNum);
            if (canBeNested) {
                // otherwise, tagNum is a member of repeating group but not the
                // start of one.
                ctx = getNestedCtx(msgType, tagNum, false);
            }
        }
        return ctx;
    }

    /**
     * Associates a field context with an ordering of where that field exists in
     * a fix message. Repeating groups use a decimal ordering so that nested
     * tags can be inserted in the right order by incrementing the decimal
     * units.
     * 
     * @param ctxString The context describing the fix field.
     * @return FixFieldContext the context and it's order.
     */
    FixFieldContext getFieldContext(String ctxString) {
        return new FixFieldContext(ctxString,
            Integer.toString(tagCount) + '.' + 0);
    }

    /**
     * Keeps a record of a repeating group as it is being parsed. Particulary
     * where a group begins and ends and it's level of nesting if the group does
     * repeat within a message.
     * 
     * @param tagNum Needed so that we can tell when a group repeats if the
     * tagnum is defined as the groups delimiter.
     * @return true if the given tagNum is not associated with the current
     * repeating group in any way.
     */
    private boolean doRepeatingBlock(String tagNum) {
        boolean canBeNested = true;
        if (grpStack.size() == 0) { return false; }
        GroupInProgress grpMgr = grpStack.peek();
        FixRepeatingGroup grp = grpMgr.getRepeatingGroup();
        if (tagNum.equals(grp.getDelimiter())) {
            grpMgr.incCurGoupNumber();
        } else if (!grp.containsMember(tagNum)) {
            // we're out of group
            grpStack.pop();
            canBeNested = doRepeatingBlock(tagNum);
        }
        return canBeNested;
    }

    /**
     * Assigns the correct context string to a Fix tag taking into account
     * whether that tag number exists as part of a repeating group and which
     * level of nexting within a group it may appear.
     * 
     * @param msgType The message type that owns the tag.
     * @param tagNum The fix tag number
     * @return FixFieldContext the context and it's order.
     */
    private FixFieldContext getNestedCtx(String msgType, String tagNum,
        boolean isGroupId) {
        GroupInProgress gm = null;
        if (isGroupId) {
            gm = grpStack.peek();
            if (gm != null && !gm.getRepeatingGroup().containsNestedGrp(tagNum)) {
                grpStack.clear();
            }
        }
        // if the stack is empty, tagNum is the start of the outermost repeating
        // group.
        if (grpStack.isEmpty()) {
            String ctxString = '&' + tagNum;
            FixFieldContext fc = getFieldContext(ctxString);
            grpCtxPos.put(ctxString, new BigDecimal(fc.getValue().toString(),
                MathContext.DECIMAL32));
            return fc;
        }
        StringBuilder ctxB = new StringBuilder();
        Iterator<GroupInProgress> it = grpStack.descendingIterator();
        while (it.hasNext()) {
            gm = it.next();
            ctxB.append(gm.toString());
        }
        String ctxKey = ctxB.toString();
        String ctxString = ctxKey + '&' + tagNum;
        FixFieldContext fc = getFieldContext(ctxString);
        // we need to keep marks on the last position of a group member
        // and the group identifier so we know where to insert new members.
        if (FixGroupMgr.containsGrpId(msgType, tagNum)) {
            grpCtxPos.put(ctxString, new BigDecimal(fc.getValue().toString(),
                MathContext.DECIMAL32));
        } else {
            grpCtxPos.put(ctxKey, new BigDecimal(fc.getValue().toString(),
                MathContext.DECIMAL32));
        }
        return fc;
    }

    /**
     * When a tag is added to a message as part of a rule a decision must be
     * made as to where to insert the tag so it appears in the right order. A
     * general rule of thumb is if the tag is not part of a group it is inserted
     * at the end of a message. If the tag belongs to a group it is inserted at
     * the end of the group. The program has to keep track of the insert points
     * for each repeating group. <br>
     * <br>
     * Example: The repeating group starts at the 5th postion in the message.
     * The position of the last tag in the first nesting is 7. The position of
     * the last tag in the second nesting is 9. <br>
     * <code>
     * Ref: &382[0]->, Order: 7.0
     * Ref: &382, Order: 5.0
     * Ref: &382[1]->, Order: 9.0
     * </code>
     * 
     * @param parentContexts A stack of references. A list of reference keys are
     * provided that represent all possible reference points to check for the
     * order in which to insert a message.<br>
     * <br>
     * Example: User wants to insert &382[1]->&218, tag 218 in the second
     * nesting of group 382. We need to find the reference that points to the
     * last tag in the second nesting for the insertion point. The order in
     * which to search is provided by the argument. <br>
     * <code>
     * &382[1]->
     * &382[0]->
     * &382
     * </code> <br>
     * The insertion point is an increment of the order associated with ref
     * "&382[1]->" if there are tags already in the second nesting. If it's the
     * first tag in the second nesting then the insertion point in an increment
     * of the order associated with ref "&382[0]->".<br>
     * <br>
     * @see FixRulesTransformVisitor#visitTagref(com.globalforge.infix.antlr.FixRulesParser.TagrefContext)
     * @return BigDecimal The order in which the tag appears.
     */
    BigDecimal getCxtPosition(Deque<String> parentContexts) {
        String prevCtx = null;
        Iterator<String> it = parentContexts.iterator();
        while (it.hasNext()) {
            String ctx = it.next();
            // do we have this context already?
            BigDecimal ordKey = grpCtxPos.get(ctx);
            if (ordKey != null) {
                // a convenient way to insert between two consecutive floating
                // point numbers.
                ordKey =
                    ordKey
                        .add(new BigDecimal("0.00001"), MathContext.DECIMAL32);
                if (prevCtx == null) {
                    grpCtxPos.put(ctx, ordKey);
                } else {
                    grpCtxPos.put(prevCtx, ordKey);
                }
                return ordKey;
            } else {
                prevCtx = ctx;
            }
        }
        return null;
    }

    /**
     * Removes any markers for repeating groups. Called by the message manager
     * when deleting a tag.
     * 
     * @param ctx The context to remove.
     */
    void removeCtxPosition(String ctx) {
        if (grpCtxPos.containsKey(ctx)) {
            grpCtxPos.remove(ctx);
        }
    }

    /**
     * Used for debugging. Prints the locations of repeating groups.
     */
    void printMarks() {
        logger.info("--- Group Marks ---");
        Iterator<String> iter = grpCtxPos.keySet().iterator();
        while (iter.hasNext()) {
            String k = iter.next();
            logger.info("Ctx: {}, Mark: {}", k, grpCtxPos.get(k));
        }
    }
}
