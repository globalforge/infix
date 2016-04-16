package com.globalforge.infix.qfix;

public class CodeGenerator {
    public static void main(String[] args) {
        DataGenerator dataGen = new DataGenerator();
        String fixVersion = null;
        MessageDataCodeGenerator msgGen = null;
        FieldOrderMapCodeGenerator fieldGen = null;
        try {
            // FIX4.0
            /*
             * fixVersion = "FIX.4.0"; dataGen.parseFIX40(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX4.1 fixVersion =
             * "FIX.4.1"; dataGen.parseFIX41(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX4.2 fixVersion =
             * "FIX.4.2"; dataGen.parseFIX42(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX4.3 fixVersion =
             * "FIX.4.3"; dataGen.parseFIX43(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX4.4 fixVersion =
             * "FIX.4.4"; dataGen.parseFIX44(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX5.0 fixVersion =
             * "FIX.5.0"; dataGen.parseFIX50(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX5.0.SP1
             * fixVersion = "FIX.5.0SP1"; dataGen.parseFIX50SP1(); msgGen = new
             * FIXMessageDataCodeGenerator(fixVersion, dataGen);
             * msgGen.generateClass(); fieldGen = new
             * FieldOrderMapCodeGenerator(fixVersion, dataGen); //
             * fieldGen.generateClass(); groupGen = new
             * GroupManagerCodeGenerator(fixVersion, dataGen); //
             * groupGen.generateClass(); dataGen.clear(); // FIX5.0.SP2
             */
            fixVersion = "FIX.5.0SP2";
            dataGen.parseFIX50SP2();
            msgGen = new MessageDataCodeGenerator(fixVersion, dataGen);
            msgGen.generateClass();
            fieldGen = new FieldOrderMapCodeGenerator(fixVersion, dataGen);
            fieldGen.generateClass();
            //groupGen = new GroupManagerCodeGenerator(fixVersion, dataGen);
            // groupGen.generateClass();
            dataGen.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
