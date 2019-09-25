package com.globalforge.infix;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globalforge.infix.antlr.FixRulesBaseVisitor;
import com.globalforge.infix.antlr.FixRulesParser;
import com.globalforge.infix.antlr.FixRulesParser.FixrulesContext;
import com.globalforge.infix.antlr.FixRulesParser.RvalueContext;
import com.globalforge.infix.antlr.FixRulesParser.TagContext;
import com.globalforge.infix.antlr.FixRulesParser.TagnumContext;
import com.globalforge.infix.antlr.FixRulesParser.TerminalContext;
import com.globalforge.infix.api.InfixFieldInfo;

/**
 * This is the class which performs the application logic in response to the
 * parser's recognition of the rule syntax. All of the operations defined in the
 * rule syntax grammar are implemented here. Methods in this class correspond to
 * tokens in the grammer. Each method is visited as the rules are parsed. See
 * the Antlr documentation for more detail.
 *
 * @see FixRulesBaseVisitor
 * @author Michael
 */
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
public class FixRulesTransformVisitor extends FixRulesBaseVisitor<String> {
   /** logger */
   final static Logger log = LoggerFactory.getLogger(FixRulesTransformVisitor.class);
   protected static final int LEFT = 0;
   protected static final int RIGHT = 1;
   private static final String SINGLE_QUOTE = "\"";
   private static final String ESCAPED_QUOTE = "\\\\\"";
   private final SimpleDateFormat datetime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
   private final SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
   private final Stack<Boolean> conditionalStack = new Stack<>();
   private final String tag8Value;
   private final Pattern escPattern = Pattern.compile(ESCAPED_QUOTE);
   protected FixMessageMgr msgMgr = null;
   protected String tagParseCtx = "";
   protected final String fixMessage;

   /**
    * Initialize the rule engine with a Fix input string.
    *
    * @param fixMsg The input message
    */
   public FixRulesTransformVisitor(String fixMsg) {
      this(fixMsg, null);
   }

   /**
    * Initialize the rule engine with a Fix input string and custom FIX version.
    *
    * @param fixMsg The FIX input message
    * @param tag8Value The customer FIX version.
    */
   public FixRulesTransformVisitor(String fixMsg, String tag8Value) {
      this.tag8Value = tag8Value;
      this.fixMessage = fixMsg;
   }

   /**
    * Debug utility
    */
   public void printDictionary() {
      msgMgr.printDict();
   }

   /**
    * Begin parsing a set of rules.
    *
    * @return String a fully formatted Fix String representing the results of
    * rule invocations.
    * @see FixRulesParser.FixrulesContext
    */
   @Override
   public String visitFixrules(FixRulesParser.FixrulesContext ctx) {
      log.debug("visitParseRules");
      try {
         if (tag8Value != null) {
            msgMgr = new FixMessageMgr(fixMessage, tag8Value);
         } else {
            msgMgr = new FixMessageMgr(fixMessage);
         }
      } catch (Exception e) {
         FixRulesTransformVisitor.log
            .error("Bad message or unsupported fix version....parser HALT.", e);
         return null;
      }
      visitChildren(ctx);
      return msgMgr.toString();
   }

   /**
    * Handle an action. This is really the first method that get's invoked for
    * every rule. It is the place where all temporary data stores are reset.
    *
    * @see FixRulesParser.ActionContext
    */
   @Override
   public String visitAction(FixRulesParser.ActionContext ctx) {
      return visitChildren(ctx);
   }

   /**
    * Number after last '>'
    *
    * @param ctxString Full context of field reference.
    * @return String a tag number
    */
   public static String removePointers(String ctxString) {
      String mapString = ctxString.replaceAll("&", "");
      return mapString;
   }

   /**
    * Handle tag assignment.
    *
    * @return String the right hand side of an assignment.
    * @see FixRulesParser.AssignContext
    */
   @Override
   public String visitAssign(FixRulesParser.AssignContext ctx) {
      if (log.isTraceEnabled()) {
         log.trace("visitAssign");
      }
      String tagCtx = ctx.tag().tagref().getText();
      String tagVal = visit(ctx.expr());
      // This prevents any assignment
      if ((tagCtx == null) || (tagVal == null)) {
         return null;
      }
      if (tagCtx.equals("&35")) {
         throw new RuntimeException("Can't re-assign tag 35");
      }
      visitChildren(ctx);
      if (ctx.CAST() != null) {
         if ("(int)".equals(ctx.CAST().toString())) {
            int intVal = (int) Double.parseDouble(tagVal);
            tagVal = intVal + "";
         }
      }
      msgMgr.putContext(tagParseCtx, tagVal);
      return null;
   }

   /**
    * Exchange tags. Exchanges value between two tags without resorting to
    * assignment to a temporary variable using other infix operators.
    */
   @Override
   public String visitExchange(FixRulesParser.ExchangeContext ctx) {
      String lCtx = ctx.tag(FixRulesTransformVisitor.LEFT).tagref().getText();
      String rCtx = ctx.tag(FixRulesTransformVisitor.RIGHT).tagref().getText();
      // No one messes with 8 or 35. Too bad!
      if (lCtx.equals("&8") || lCtx.equals("&35")) {
         return null;
      }
      if (rCtx.equals("&8") || rCtx.equals("&35")) {
         return null;
      }
      visit(ctx.tag(FixRulesTransformVisitor.LEFT));
      String lRef = tagParseCtx;
      visit(ctx.tag(FixRulesTransformVisitor.RIGHT));
      String rRef = tagParseCtx;
      InfixFieldInfo lValue = msgMgr.getContext(lRef);
      InfixFieldInfo rValue = msgMgr.getContext(rRef);
      if ((lValue == null) || (rValue == null)) {
         return null;
      }
      msgMgr.putContext(lRef, rValue.getField().getTagVal());
      msgMgr.putContext(rRef, lValue.getField().getTagVal());
      return null;
   }

   /**
    * Do something with a tag. Just continues on visiting the children.
    *
    * @see FixRulesParser.TagContext
    */
   @Override
   public String visitTag(FixRulesParser.TagContext ctx) {
      tagParseCtx = "";
      return visitChildren(ctx);
   }

   /**
    * Do something with a reference. Just continues on visiting the children.
    *
    * @see FixRulesParser.RefContext
    */
   @Override
   public String visitRef(FixRulesParser.RefContext ctx) {
      return visitChildren(ctx);
   }

   @Override
   public String visitTagref(FixRulesParser.TagrefContext ctx) {
      String txt = ctx.getText();
      txt = removePointers(txt);
      tagParseCtx += txt;
      return visitChildren(ctx);
   }

   /**
    * Do something with a reference to a tag index. A tag index is defined as
    * the nesting level of a repeating group. Also used to build the stack based
    * tree of references described in
    * {@link FixRulesTransformVisitor#visitTagref}
    *
    * @see FixRulesTransformVisitor#visitTagref
    */
   @Override
   public String visitIdxref(FixRulesParser.IdxrefContext ctx) {
      String idx = visit(ctx.idx().expr());
      tagParseCtx += "[" + idx + "]->";
      return visitChildren(ctx);
   }

   /**
    * Do something with a terminal node. Just continues on visiting the
    * children.
    *
    * @see FixRulesParser.TermContext
    */
   @Override
   public String visitTerm(FixRulesParser.TermContext ctx) {
      return visitChildren(ctx);
   }

   /**
    * Given a tag in rule syntax lookup and return it's value.
    *
    * @return String the value of a fix tag given a tag number.
    * @see FixRulesParser.MyTagContext
    */
   @Override
   public String visitMyTag(FixRulesParser.MyTagContext ctx) {
      String context = ctx.tag().getText();
      return getTag(context);
   }

   /**
    * Given a tag context, lookup up the value.
    */
   private String getTag(String context) {
      String tagValue = null;
      context = removePointers(context);
      InfixFieldInfo field = msgMgr.getContext(context);
      if (field != null) {
         tagValue = field.getField().getTagVal();
      }
      return tagValue;
   }

   /**
    * Return the value of an integer in rule syntax.
    *
    * @return String An integer in string format.
    * @see FixRulesParser.IntContext
    */
   @Override
   public String visitInt(FixRulesParser.IntContext ctx) {
      return ctx.getText();
   }

   /**
    * Return the value of a float in rule syntax.
    *
    * @return String a float in string format.
    * @see FixRulesParser.FloatContext
    */
   @Override
   public String visitFloat(FixRulesParser.FloatContext ctx) {
      BigDecimal f = new BigDecimal(ctx.getText(), MathContext.DECIMAL32);
      return f.toString();
   }

   /**
    * Strips the quotes off of a String value in rule syntax.
    *
    * @return String A string literal stripped of quotes.
    * @see FixRulesParser.ValContext
    */
   @Override
   public String visitVal(FixRulesParser.ValContext ctx) {
      return removeSpecialChars(ctx.VAL().getText());
   }

   /**
    * Strips the quotes off of a String value in rule syntax. <code>
    *    1. &49="FOO"     = 49=FOO
    *    2. &49=""FOO""   = &49="FOO"
    *    3. &49="FO\"O"   = &49="FO"O"
    * </code>
    */
   private String removeSpecialChars(String value) {
      String r = value.substring(1, value.length() - 1); // remove the quotes
      r = escPattern.matcher(r).replaceAll(SINGLE_QUOTE);
      return r;
   }

   /**
    * Formats a time stamp string in rule syntax into a FIX UTC time stamp.
    *
    * @return String the datetime in format yyyyMMdd-HH:mm:ss.SSS
    * @see FixRulesParser.DateTimeContext
    */
   @Override
   public String visitDateTime(FixRulesParser.DateTimeContext ctx) {
      return datetime.format(new Date());
   }

   /**
    * Formats a date string in rule syntax into a FIX UTC timestamp.
    *
    * @return String the date in format yyyyMMdd
    * @see FixRulesParser.DateContext
    */
   @Override
   public String visitDate(FixRulesParser.DateContext tx) {
      return date.format(new Date());
   }

   /**
    * Perform addition on operands in rule syntax.
    *
    * @return String the result of an addition of two operands depending upon
    * the context.
    * @see FixRulesParser.AddContext
    */
   @Override
   public String visitAdd(FixRulesParser.AddContext ctx) {
      // get value of left subexpression
      String left = visit(ctx.expr(FixRulesTransformVisitor.LEFT));
      // get value of right subexpression
      String right = visit(ctx.expr(FixRulesTransformVisitor.RIGHT));
      if ((left == null) || (right == null)) {
         FixRulesTransformVisitor.log.error("INFIX ERROR: null field in 'Add'. No assignment: {}",
            ctx.getText());
         return null;
      }
      BigDecimal result = null;
      result = new BigDecimal(left).add(new BigDecimal(right), MathContext.DECIMAL32);
      return result.toString();
   }

   /**
    * Perform subtraction on operands in rule syntax.
    *
    * @return String the result of a subtraction of two operands depending upon
    * the context.
    * @see FixRulesParser.SubContext
    */
   @Override
   public String visitSub(FixRulesParser.SubContext ctx) {
      // get value of left subexpression
      String left = visit(ctx.expr(FixRulesTransformVisitor.LEFT));
      // get value of right subexpression
      String right = visit(ctx.expr(FixRulesTransformVisitor.RIGHT));
      if ((left == null) || (right == null)) {
         FixRulesTransformVisitor.log.error("INFIX ERROR: null field in 'Add'. No assignment: {}",
            ctx.getText());
         return null;
      }
      BigDecimal result = null;
      result = new BigDecimal(left).subtract(new BigDecimal(right), MathContext.DECIMAL32);
      return result.toString();
   }

   /**
    * Perform multiplication & division on operands in rule syntax depending
    * upon the context.
    *
    * @return String the result of a multiplication or division of two operands.
    * @see FixRulesParser.MulDivContext
    */
   @Override
   public String visitMulDiv(FixRulesParser.MulDivContext ctx) {
      // get value of left subexpression
      String left = visit(ctx.expr(FixRulesTransformVisitor.LEFT));
      // get value of right subexpression
      String right = visit(ctx.expr(FixRulesTransformVisitor.RIGHT));
      if ((left == null) || (right == null)) {
         FixRulesTransformVisitor.log
            .error("INFIX ERROR: null field in 'Mul/Div'. No assignment: {}", ctx.getText());
         return null;
      }
      BigDecimal result = null;
      if (ctx.op.getType() == FixRulesParser.MUL) {
         result = new BigDecimal(left).multiply(new BigDecimal(right), MathContext.DECIMAL32);
      } else {
         result = new BigDecimal(left).divide(new BigDecimal(right), MathContext.DECIMAL32);
      }
      return result.toString();
   }

   /**
    * Performs tag concatenation of values.
    *
    * @return String the concatenation of two values
    * @see FixRulesParser.CatContext
    */
   @Override
   public String visitCat(FixRulesParser.CatContext ctx) {
      // get value of left subexpression
      String left = visit(ctx.expr(FixRulesTransformVisitor.LEFT));
      // get value of right subexpression
      String right = visit(ctx.expr(FixRulesTransformVisitor.RIGHT));
      if ((left == null) || (right == null)) {
         FixRulesTransformVisitor.log.error("INFIX ERROR: null field in '|'. No assignment: {}",
            ctx.getText());
         return null;
      }
      return left + right;
   }

   /**
    * Compares the tag value associated with a tag context to a string value
    * converted to a BigDecimal. An assumption is made that the comparison is
    * between numbers. If any exceptions result from comparing non-numbers then
    * a string comparison is attempted. Any boolean operations on alphabetic
    * strings that end up here may have unexpected results.
    *
    * @param lVal A value on the left side of an expression to compare.
    * @param rVal A value on the right side of an to compare the left against.
    * @return -1, 0, or 1 if the tag values associated with the context is
    * numerically less than, equal to, or greater than {@code rVal}.
    */
   private int doCompare(String lVal, String rVal) {
      boolean wasException = false;
      BigDecimal valInQuestion = null;
      BigDecimal condFieldVal = null;
      try {
         valInQuestion = new BigDecimal(lVal, MathContext.DECIMAL32);
      } catch (NumberFormatException e) {
         wasException = true;
      }
      try {
         condFieldVal = new BigDecimal(rVal, MathContext.DECIMAL32);
      } catch (NumberFormatException e) {
         wasException = true;
      }
      int compareTo = Integer.MIN_VALUE;
      if (!wasException) {
         compareTo = valInQuestion.compareTo(condFieldVal);
      } else {
         compareTo = lVal.compareTo(rVal);
      }
      return compareTo;
   }

   /**
    * updates the current state of a conditional expression currently being
    * evaluated.
    *
    * @param isStatementTrue the new state of the conditional expression.
    */
   private void updateConditionalState(boolean isStatementTrue) {
      conditionalStack.pop();
      conditionalStack.push(isStatementTrue);
   }

   /**
    * Determines if a boolean "=" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitIs_equal(FixRulesParser.Is_equalContext ctx) {
      TerminalContext lVal = ctx.tg;
      TerminalContext rVal = ctx.op;
      String rResult = visit(rVal);
      String lResult = visit(lVal);
      boolean isStatementTrue = false;
      if ((rResult != null) && (lResult != null)) {
         isStatementTrue = rResult.equals(lResult);
      } else {
         isStatementTrue = false;
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean "!=" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitNot_equal(FixRulesParser.Not_equalContext ctx) {
      TerminalContext lVal = ctx.tg;
      TerminalContext rVal = ctx.op;
      String rResult = visit(rVal);
      String lResult = visit(lVal);
      boolean isStatementTrue = false;
      if ((rResult != null) && (lResult != null)) {
         isStatementTrue = !rResult.equals(lResult);
      } else {
         isStatementTrue = false;
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean ">" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitIs_greater(FixRulesParser.Is_greaterContext ctx) {
      TerminalContext lVal = ctx.tg;
      TerminalContext rVal = ctx.op;
      String rResult = visit(rVal);
      String lResult = visit(lVal);
      boolean isStatementTrue = false;
      if ((rResult != null) && (lResult != null)) {
         int compareTo = doCompare(lResult, rResult);
         isStatementTrue = compareTo > 0 ? true : false;
      } else {
         isStatementTrue = false;
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean "<" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitIs_less(FixRulesParser.Is_lessContext ctx) {
      TerminalContext lVal = ctx.tg;
      TerminalContext rVal = ctx.op;
      String rResult = visit(rVal);
      String lResult = visit(lVal);
      boolean isStatementTrue = false;
      if ((rResult != null) && (lResult != null)) {
         int compareTo = doCompare(lResult, rResult);
         isStatementTrue = compareTo < 0 ? true : false;
      } else {
         isStatementTrue = false;
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean "<=" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitIs_lessEq(FixRulesParser.Is_lessEqContext ctx) {
      TerminalContext lVal = ctx.tg;
      TerminalContext rVal = ctx.op;
      String rResult = visit(rVal);
      String lResult = visit(lVal);
      boolean isStatementTrue = false;
      if ((rResult != null) && (lResult != null)) {
         int compareTo = doCompare(lResult, rResult);
         isStatementTrue = compareTo <= 0 ? true : false;
      } else {
         isStatementTrue = false;
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean ">=" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitIs_greatEq(FixRulesParser.Is_greatEqContext ctx) {
      TerminalContext lVal = ctx.tg;
      TerminalContext rVal = ctx.op;
      String rResult = visit(rVal);
      String lResult = visit(lVal);
      boolean isStatementTrue = false;
      if ((rResult != null) && (lResult != null)) {
         int compareTo = doCompare(lResult, rResult);
         isStatementTrue = compareTo >= 0 ? true : false;
      } else {
         isStatementTrue = false;
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean "!" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitNot(FixRulesParser.NotContext ctx) {
      TerminalContext lVal = ctx.tg;
      String lResult = visit(lVal);
      boolean isStatementTrue = lResult == null || lResult.isEmpty() ? true : false;
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Determines if a boolean "^" operation is true or false. The operation may
    * exists as part of a larger boolean expression consisting of multiple
    * boolean operators within a single action.
    */
   @Override
   public String visitIs(FixRulesParser.IsContext ctx) {
      TerminalContext lVal = ctx.tg;
      String lResult = visit(lVal);
      boolean isStatementTrue = lResult != null && !lResult.isEmpty() ? true : false;
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * The entry and exit point of a conditional statement including the if/else
    * and all rule parses, assignments, etc. in between. Once the children have
    * been visited we reset the flag to false. This completes the conditional
    * and both the if and else have been parsed or ignored. Onced the
    * isStatementTrue flag has been reset we can parse a new conditional.
    */
   @Override
   public String visitConditional(FixRulesParser.ConditionalContext ctx) {
      conditionalStack.push(false);
      visitChildren(ctx);
      conditionalStack.pop();
      return null;
   }

   /**
    * The outcome of a boolean expression is always a tag assignment. If the
    * outcome is true an assignment will take place.
    */
   @Override
   public String visitThen(FixRulesParser.ThenContext ctx) {
      boolean isStatementTrue = conditionalStack.peek();
      if (!isStatementTrue) {
         return null;
      }
      // ActionContext actionCtx = ctx.action();
      RvalueContext actionCtx = ctx.rvalue();
      FixrulesContext rulesCtx = ctx.fixrules();
      if (actionCtx != null) {
         return visit(actionCtx);
      }
      if (rulesCtx != null) {
         return visit(rulesCtx);
      }
      return null;
   }

   /**
    * The outcome of a boolean expression is always a tag assignment. If the
    * outcome is false an assignment will take place.
    */
   @Override
   public String visitEls(FixRulesParser.ElsContext ctx) {
      boolean isStatementTrue = conditionalStack.peek();
      if (isStatementTrue) {
         return null;
      }
      // ActionContext actionCtx = ctx.action();
      RvalueContext actionCtx = ctx.rvalue();
      FixrulesContext rulesCtx = ctx.fixrules();
      if (actionCtx != null) {
         return visit(actionCtx);
      }
      if (rulesCtx != null) {
         return visit(rulesCtx);
      }
      return null;
   }

   /**
    * token within parenthesis are considered an expression collectively.
    */
   @Override
   public String visitParens(FixRulesParser.ParensContext ctx) {
      return visit(ctx.expr());
   }

   /**
    * The result of a boolean expression on the left side of an AND (&&) or OR
    * (||) compared with the result of boolean expression on the right.
    */
   @Override
   public String visitAndOr(FixRulesParser.AndOrContext ctx) {
      visit(ctx.iff(FixRulesTransformVisitor.LEFT)); // left
      boolean leftIsTrue = conditionalStack.peek();
      visit(ctx.iff(FixRulesTransformVisitor.RIGHT)); // right
      boolean isStatementTrue = false;
      switch (ctx.op.getType()) {
         case FixRulesParser.AND:
            isStatementTrue = leftIsTrue && conditionalStack.peek();
            ;
            break;
         case FixRulesParser.OR:
            isStatementTrue = leftIsTrue || conditionalStack.peek();
            ;
            break;
         default:
            throw new RuntimeException(
               "Don't know how to handle boolean and/or operation: " + ctx.getText());
      }
      updateConditionalState(isStatementTrue);
      return null;
   }

   /**
    * Deletes a tag from a fix message.
    */
   @Override
   public String visitDeleteTag(FixRulesParser.DeleteTagContext ctx) {
      String txt = ctx.tg.getText();
      txt = removePointers(txt);
      msgMgr.removeContext(txt);
      return null;
   }

   /**
    * Deletes a series of tags from a fix message.
    */
   @Override
   public String visitDeleteTagSet(FixRulesParser.DeleteTagSetContext ctx) {
      List<TagnumContext> tagList = ctx.tagnum();
      Map<String, InfixFieldInfo> msgMap = msgMgr.getInfixMessageMap();
      Set<String> infixKeys = msgMap.keySet();
      Iterator<TagnumContext> it = tagList.iterator();
      while (it.hasNext()) {
         String tagCtx = it.next().getText();
         if (infixKeys.contains(tagCtx)) {
            if (tagCtx.equals("&8") || tagCtx.equals("&35")) {
               continue;
            }
            tagCtx = removePointers(tagCtx);
            msgMgr.removeContext(tagCtx);
         }
      }
      return null;
   }

   /**
    * Deletes all tags and keeps only those indicated (always keeps 8, 9, 10, &
    * 35)
    */
   @Override
   public String visitKeepTagSet(FixRulesParser.KeepTagSetContext ctx) {
      List<TagnumContext> tagList = ctx.tagnum();
      Set<String> keepSet = new HashSet<String>();
      Iterator<TagnumContext> it = tagList.iterator();
      while (it.hasNext()) {
         String tagCtx = it.next().getText();
         keepSet.add(tagCtx);
      }
      Map<String, InfixFieldInfo> msgMap = msgMgr.getInfixMessageMap();
      Set<String> infixKeys = msgMap.keySet();
      Iterator<String> infixKeyIt = infixKeys.iterator();
      while (infixKeyIt.hasNext()) {
         String infixKey = infixKeyIt.next();
         String rootTagNum = FixFieldOrderHash.getRootTagNumber(infixKey);
         if (!keepSet.contains(rootTagNum)) {
            if (infixKey.equals("8") || infixKey.equals("35")) {
               continue;
            }
            infixKeyIt.remove();
         }
      }
      return null;
   }

   /**
    * Invokes a user-defined class that must implement
    * com.globalforge.infix.InfixUserContext
    */
   @Override
   public String visitUserdef(FixRulesParser.UserdefContext ctx) {
      String className = ctx.className.getText();
      String methodName = ctx.methodName == null ? null : ctx.methodName.getText();
      msgMgr.handleUserDefinedContext(className, methodName);
      return null;
   }

   /**
    * Invokes a user-defined class that is responsible for generating the right
    * hand side of an assignment.
    *
    * @return String The right hand side of the assignment.
    */
   @Override
   public String visitMyUserTerm(FixRulesParser.MyUserTermContext ctx) {
      String className = ctx.userTerm().className.getText();
      return msgMgr.handleUserDefinedTerminal(className);
   }

   /**
    * visit a function
    */
   @Override
   public String visitFunction(FixRulesParser.FunctionContext ctx) {
      return visitChildren(ctx);
   }

   /**
    * Splits the value of a tag context similar to {@link String#split(String)}
    * <br>
    */
   @Override
   public String visitSplit(FixRulesParser.SplitContext ctx) {
      List<TagContext> tagList = ctx.tag();
      String regex = removeSpecialChars(ctx.regex.getText());
      TagContext sourceTagCtx = ctx.sourceTag;
      visit(sourceTagCtx);
      String sourceTag = tagParseCtx;
      InfixFieldInfo fieldInfo = msgMgr.getContext(sourceTag);
      String sourceTagValue = fieldInfo.getField().getTagVal();
      String[] splitResult = sourceTagValue.split(regex);
      /*
       * The first element is the tag we split (sourceTagCtx), so skip it. Also,
       * we consider how many destination tags we were supplied by the user more
       * importantly than we do how many elements resulted from the split.
       * Although they should be equal in number.
       */
      for (int i = 1; i < tagList.size(); i++) {
         TagContext tCtx = tagList.get(i);
         visit(tCtx);
         String destTag = tagParseCtx;
         // splitResult.length must be >= tagList.size();
         if (i <= splitResult.length) {
            msgMgr.putContext(destTag, splitResult[i - 1]);
         } else {
            log.error(
               "INFIX ERROR: function::split() given too many result tags. Some tags omitted from result.");
         }
      }
      return visitChildren(ctx);
   }
}