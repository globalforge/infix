package com.globalforge.infix.qfix;

/*-
The MIT License (MIT)

Copyright (c) 2016 Global Forge LLC

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
/**
 * Code generation main starting point. Produces all the java code that Infix
 * depends on.
 * 
 * @author Michael C. Starkie
 */
public class CodeGenerator {
    public static void main(String[] args) {
        DataGenerator dataGen = new DataGenerator();
        String fixVersion = null;
        MessageDataCodeGenerator msgGen = null;
        FieldOrderMapCodeGenerator fieldGen = null;
        GroupManagerCodeGenerator groupGen = null;
        try {
            // custom dictionary
            if (args.length > 0) {
                fixVersion = args[0];
                dataGen.parseCustom(fixVersion);
                msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
                msgGen.generateClass();
                fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
                fieldGen.generateClass();
                groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
                groupGen.generateClass();
                dataGen.clear();
                return;
            }
            // FIX4.0
            fixVersion = "FIX.4.0";
            dataGen.parseFIX40();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX4.1
            fixVersion = "FIX.4.1";
            dataGen.parseFIX41();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX4.2
            fixVersion = "FIX.4.2";
            dataGen.parseFIX42();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX4.3
            fixVersion = "FIX.4.3";
            dataGen.parseFIX43();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX4.4
            fixVersion = "FIX.4.4";
            dataGen.parseFIX44();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX5.0
            fixVersion = "FIX.5.0";
            dataGen.parseFIX50();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX5.0.SP1
            fixVersion = "FIX.5.0SP1";
            dataGen.parseFIX50SP1();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen); //
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen); //
            groupGen.generateClass();
            dataGen.clear();
            // FIX5.0.SP2
            fixVersion = "FIX.5.0SP2";
            dataGen.parseFIX50SP2();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen);
            fieldGen.generateClass();
            groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen);
            groupGen.generateClass();
            dataGen.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
