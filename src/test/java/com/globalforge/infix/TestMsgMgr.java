package com.globalforge.infix;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import com.globalforge.infix.api.InfixFieldInfo;

public class TestMsgMgr {
    @Test
    public void t1() throws Exception {
        FixMessageMgr msgMgr = new FixMessageMgr(StaticTestingUtils.FIX_44_NEW_ORDER_CROSS);
        Map<String, InfixFieldInfo> msgMap = msgMgr.getInfixMessageMap();
        System.out.println(msgMap);
        Assert.assertTrue(msgMap.containsKey("&8"));
    }
}
