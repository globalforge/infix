package com.globalforge.infix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestAndOr.class, TestAssignGroupTerminals.class, TestAssignTemplate.class,
    TestAssignTerminals.class, TestCkSumBodyLen.class,
    TestConditionalBoundaries.class, TestConditionals.class, TestExprAdd.class,
    TestExprCat.class, TestExprDiv.class, TestExprGroupAdd.class,
    TestExprMul.class, TestExprParen.class, TestExprSub.class,
    TestFixField.class, TestGroup.class, TestIs.class, TestIsEqual.class,
    TestIsGreater.class, TestIsGreaterEquals.class, TestIsLess.class,
    TestIsLessEquals.class, TestIsNotEqual.class, TestNot.class,
    TestUnary.class, TestUserDev.class })
public class TestAllSuite {
}
