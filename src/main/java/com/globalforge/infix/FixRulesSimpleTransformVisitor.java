package com.globalforge.infix;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.globalforge.infix.antlr.FixRulesParser;
import com.globalforge.infix.antlr.FixRulesParser.TagnumContext;
import com.globalforge.infix.api.InfixFieldInfo;

/**
 * Simple: No support for repeating groups, you get back only the tags that were
 * given unless provided by rules, no message type or FIX version required or
 * enforced. You can modify any tag at your own risk.
 * 
 * @author mstarkie
 */
public class FixRulesSimpleTransformVisitor extends FixRulesTransformVisitor {
   public FixRulesSimpleTransformVisitor(String fixMsg) {
      super(fixMsg);
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
         msgMgr = new FixSimpleMessageMgr(fixMessage);
      } catch (Exception e) {
         FixRulesTransformVisitor.log
            .error("Bad message or unsupported fix version....parser HALT.", e);
         return null;
      }
      visitChildren(ctx);
      return msgMgr.toString();
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
            tagCtx = removePointers(tagCtx);
            msgMgr.removeContext(tagCtx);
         }
      }
      return null;
   }

   /**
    * Deletes all tags and keeps only those indicated.
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
            infixKeyIt.remove();
         }
      }
      return null;
   }
}
