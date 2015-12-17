package com.globalforge.infix;

import java.util.ArrayList;
import com.globalforge.infix.api.InfixField;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public class StaticTestingUtils {
    public static final ListMultimap<Integer, String> parseMessage(
        String baseMsg) {
        ListMultimap<Integer, String> dict = LinkedListMultimap.create();
        char[] baseMsgArray = baseMsg.toCharArray();
        int index = 0;
        for (int i = 0; i < baseMsgArray.length; i++) {
            if (baseMsgArray[i] == 0x01) {
                String fixField = baseMsg.substring(index, i);
                char[] fixFieldArray = fixField.toCharArray();
                for (int j = 0; j < fixFieldArray.length; j++) {
                    if (fixFieldArray[j] == '=') {
                        String num = fixField.substring(0, j);
                        int n = Integer.parseInt(num);
                        String val =
                            fixField.substring(j + 1, fixField.length());
                        dict.put(n, val);
                        break;
                    }
                }
                index = i + 1;
            }
        }
        return dict;
    }

    public static final ArrayList<InfixField> parseMessageIntoList(
        String baseMsg) {
        ArrayList<InfixField> myArray = new ArrayList<InfixField>();
        char[] baseMsgArray = baseMsg.trim().toCharArray();
        int index = 0;
        for (int i = 0; i < baseMsgArray.length; i++) {
            if (baseMsgArray[i] == 0x01) {
                // locate the next field (35=D)
                String fixField = baseMsg.substring(index, i);
                // System.out.println("fixfield: " + fixField);
                StaticTestingUtils.parseField(fixField, myArray);
                index = i + 1;
            }
        }
        String fixField = baseMsg.substring(index, baseMsgArray.length);
        StaticTestingUtils.parseField(fixField, myArray);
        return myArray;
    }

    public static final void parseField(String fixField,
        ArrayList<InfixField> myArray) {
        char[] fixFieldArray = fixField.toCharArray();
        // break up the field into tagNum and tagVal
        for (int j = 0; j < fixFieldArray.length; j++) {
            if (fixFieldArray[j] == '=') {
                String tagNum = fixField.substring(0, j);
                String tagVal = fixField.substring(j + 1, fixField.length());
                myArray.add(new InfixField(Integer.parseInt(tagNum), tagVal));
                return;
            }
        }
    }

    public static String rs(String ins) {
        return ins.replaceAll("\u0001", "|");
    }

    public static String showCount(String ins) {
        return ins.replaceAll("\u0001", "|");
    }

    public static String ck(String str) {
        StringBuilder sb = new StringBuilder();
        char[] inputChars = str.toString().toCharArray();
        for (char aChar : inputChars) {
            sb.append((int) aChar);
            sb.append(" ");
        }
        return sb.toString();
    }
}
