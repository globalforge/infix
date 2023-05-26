package com.globalforge.infix.app;

import java.util.List;
import java.util.Scanner;

import com.globalforge.infix.FixData;
import com.globalforge.infix.FixMessageMgr;
import com.globalforge.infix.api.InfixFieldInfoPosComparator;

/**
 * Utility which accepts a FIX message copied from a Unix log file (with \0001
 * replaced by ^A) and converts into a human readable message.
 * 
 * @author Michael Starkie
 *
 */
public class PrettyPrint {
	public static String fixMsg2 = "";

	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);
			//System.out.print("Enter FIX message from logs: ");
			//String fixMsg = scanner.nextLine();
			String fixMsg = fixMsg2;
			String properFix = fixMsg.replaceAll("\\^A", "\u0001");
			properFix = properFix.replaceAll("\\n", "");
			properFix = properFix.replaceAll("\\r", "");
			FixMessageMgr msgMgr = new FixMessageMgr(properFix);
			System.out.println(msgMgr.getInfixMap().toString());
			List<FixData> listFdd = msgMgr.getFixData(new InfixFieldInfoPosComparator());
			String displayString = "";
			for (FixData fdd : listFdd) {
			   displayString += fdd.getTagNum();
			   displayString += (fdd.getTagName().isEmpty() ? "" : (" (" + fdd.getTagName() + ")"));
			   displayString += " = " + fdd.getTagVal();
			   displayString += (fdd.getTagDef().isEmpty() ? "" : (" (" + fdd.getTagDef() + ") "));
			   displayString += "\n";
			   
			}
			//String displayString = msgMgr.getInfixMap().toDisplayString(new InfixFieldInfoPosComparator());
			System.out.println(displayString);
			
			System.out.println();
			System.out.println(fixMsg);
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
