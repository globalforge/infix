package com.globalforge.infix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*-
The MIT License (MIT)

Copyright (c) 2017 Global Forge LLC

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
    TestAndOr.class, TestAssignGroupTerminals.class, TestAssignTemplate.class,
    TestAssignTerminals.class, TestCkSumBodyLen.class, TestConditionalBoundaries.class,
    TestConditionals.class, TestExprAdd.class, TestExprCat.class, TestExprDiv.class,
    TestExprGroupAdd.class, TestExprMul.class, TestExprParen.class, TestExprSub.class,
    TestFixField.class, TestGroup.class, TestIs.class, TestIsEqual.class, TestIsGreater.class,
    TestIsGreaterEquals.class, TestIsLess.class, TestIsLessEquals.class, TestIsNotEqual.class,
    TestNot.class, TestUnary.class, TestUserDev.class, TestExchange.class, TestGrp44Mgr.class,
	TestGrp50Mgr.class, TestFixFieldOrderHash.class, TestMsgMgr.class,
	TestCustomDictionary.class, TestMessageMap.class })
public class TestAllSuite {
}
