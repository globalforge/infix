package com.globalforge.infix;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.globalforge.infix.api.InfixField;

public class TestFixField {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testHashCode() {
        InfixField fld1 = new InfixField(8, "FIX.4.2");
        int hash1 = fld1.hashCode();
        InfixField fld2 = new InfixField(8, "FOOBAR");
        int hash2 = fld2.hashCode();
        Assert.assertFalse(hash1 == hash2);
        InfixField fld = new InfixField(9, "FOOBAR");
        Assert.assertFalse(fld.hashCode() == hash2);
        fld = new InfixField(8, "FOOBAR");
        Assert.assertTrue(hash2 == fld.hashCode());
        fld = new InfixField(10, "MIKE");
        Assert.assertFalse(hash2 == fld.hashCode());
    }

    @Test
    public void testFixField() {
        InfixField fld1 = new InfixField(8, "FIX.4.2");
        InfixField fld2 = new InfixField(8, "FOOBAR");
        Assert.assertFalse(fld1 == fld2);
        InfixField fld3 = new InfixField(8, "FIX.4.2");
        Assert.assertFalse(fld1 == fld3);
        Assert.assertTrue(fld1.equals(fld3));;
        Assert.assertFalse(fld1 == null);
        Assert.assertFalse(fld1.equals(fld2));;
        InfixField fld9 = new InfixField(9, "FOOBAR");
        Assert.assertFalse(fld9.equals(fld2));;
        Assert.assertFalse(fld9.equals(new String("FOO")));;
    }

    @Test
    public void testGetTag_num() {
        InfixField fld9 = new InfixField(9, "FOOBAR");
        Assert.assertTrue(9 == fld9.getTagNum());
    }

    @Test
    public void testGetTag_val() {
        InfixField fld9 = new InfixField(9, "FOOBAR");
        Assert.assertTrue("FOOBAR" == fld9.getTagVal());
    }

    @Test
    public void testToString() {
        InfixField fld1 = new InfixField(8, "FIX.4.2");
        Assert.assertTrue("8=FIX.4.2".equals(fld1.toString()));
    }
}
