package com.globalforge.infix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestAndOr.class, TestAndOrSimple.class, TestAssignGroupTerminals.class,
    TestAssignTemplate.class, TestAssignTemplateSimple.class, TestAssignTerminals.class,
    TestAssignTerminalsSimple.class, TestCkSumBodyLen.class, TestConditionalBoundaries.class,
    TestConditionalBoundariesSimple.class, TestConditionals.class, TestConditionalsSimple.class,
    TestCustomDictionary.class, TestExchange.class, TestExchangeSimple.class, TestExprAdd.class,
    TestExprAddSimple.class, TestExprCat.class, TestExprCatSimple.class, TestExprDiv.class,
    TestExprDivSimple.class, TestExprGroupAdd.class, TestExprMul.class, TestExprMulSimple.class,
    TestExprParen.class, TestExprParenSimple.class, TestExprSub.class, TestExprSubSimple.class,
    TestFixField.class, TestFixFieldOrderHash.class, TestFunction.class, TestFunctionSimple.class,
    TestGroup.class, TestGrp44Mgr.class, TestGrp50Mgr.class, TestIs.class, TestIsSimple.class,
    TestIsEqual.class, TestIsEqualSimple.class, TestIsGreater.class, TestIsGreaterSimple.class,
    TestIsGreaterEquals.class, TestIsGreaterEqualsSimple.class, TestIsLess.class,
    TestIsLessSimple.class, TestIsLessEquals.class, TestIsLessEqualsSimple.class,
    TestIsNotEqual.class, TestIsNotEqualSimple.class, TestMessageMap.class, TestMsgMgr.class,
    TestNot.class, TestNotSimple.class, TestUnary.class, TestUnarySimple.class, TestUserDev.class,
    TestUserDevSimple.class })
public class TestAllSuite {
}
