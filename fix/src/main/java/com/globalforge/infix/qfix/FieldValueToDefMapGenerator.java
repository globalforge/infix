package com.globalforge.infix.qfix;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-
The MIT License (MIT)

Copyright (c) 2019-2022 Global Forge LLC

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

public class FieldValueToDefMapGenerator {
	/** logger */
	private final static Logger logger = LoggerFactory.getLogger(FieldValueToDefMapGenerator.class);
	private String fileNamePrefix = null;
	private final String afixVer;
	private String qfixverLowerCase = null;
	private PrintStream out = null;
	private final ContextOrderMap msgCtxMap;
	private final DataDictionaryParser ddp;

	/**
	 * @param fVer FIX Version
	 * @param d    all the data parsed from the FIX Version
	 */
	public FieldValueToDefMapGenerator(String fVer, DataGenerator d, DataDictionaryParser ddp) {
		this.afixVer = fVer.replace(".", "");
		this.msgCtxMap = d.getContextOrderMap(fVer);
		this.ddp = ddp;
	}

	/**
	 * Does the deed. Generates all the java classes.
	 * 
	 * @throws Exception Error generating class.
	 */
	public void generateClass() throws Exception {
		initOutputStreams();
		handleStartClass();
		handleConstructor();
		handleMessages();
		finish();
	}

	/**
	 * Determines where on the file system to write the code. Each message type has
	 * it's own package location. Must set SRC_DIR environment variable to absolute
	 * path of com.globalforge.infix.qfix package <br>
	 * Example: <br>
	 * SRC_DIR=/projects/infix/fix/src/main/java/com/globalforge/infix/qfix
	 * 
	 * @param msgType
	 * @throws Exception
	 */
	private void initOutputStreams() throws Exception {
		String SRC_DIR = System.getenv("SRC_DIR");
		if (SRC_DIR != null) {
			FieldValueToDefMapGenerator.logger.info("SRC_DIR is an ENV variable: {}", SRC_DIR);
		} else {
			SRC_DIR = System.getProperty("SRC_DIR");
			if (SRC_DIR != null) {
				FieldValueToDefMapGenerator.logger.info("SRC_DIR is a System property: {}", SRC_DIR);
			} else {
				SRC_DIR = null;
			}
		}
		if (SRC_DIR == null) {
			FieldValueToDefMapGenerator.logger.warn("No SRC_DIR provided.  Output stream is CONSOLE");
			out = System.out;
		} else {
			fileNamePrefix = afixVer + "FieldValueToDefMap";
			qfixverLowerCase = afixVer.toLowerCase();
			File fOut = new File(SRC_DIR + System.getProperty("file.separator") + qfixverLowerCase
					+ System.getProperty("file.separator") + "auto" + System.getProperty("file.separator")
					+ fileNamePrefix + ".java");
			FieldValueToDefMapGenerator.logger.info("building java file: {}", fOut.getAbsolutePath());
			fOut.getParentFile().mkdirs();
			out = new PrintStream(fOut, "UTF-8");
		}
	}

	/**
	 * Begin constructing the java source when this rule is invoked by antlr.
	 * 
	 * @param version The fix version. Not used.
	 */
	private void handleStartClass() {
		out.println("package com.globalforge.infix.qfix." + this.qfixverLowerCase + ".auto;");
		out.println();
		out.println("import java.util.Map;");
		out.println("import java.util.concurrent.ConcurrentHashMap;");
		out.println("import com.globalforge.infix.qfix.FieldValueToDefMap;");
		doCopyright();
		out.println();
		out.println("/**");
		out.println("* This class is auto-generated. It should never be coded by hand. If you find");
		out.println("* yourself coding this class then you have failed to understand how to build");
		out.println("* the tool. It would actually be faster to do it the right way.");
		out.println("*/");
		out.println("public class " + fileNamePrefix + " extends FieldValueToDefMap {");
	}

	/**
	 * mucho importante
	 */
	private void doCopyright() {
		out.println();
		out.println("/*-");
		out.println("The MIT License (MIT)");
		out.println();
		out.println("Copyright (c) 2019-2022 Global Forge LLC");
		out.println();
		out.println("Permission is hereby granted, free of charge, to any person obtaining a copy");
		out.println("of this software and associated documentation files (the \"Software\"), to deal");
		out.println("in the Software without restriction, including without limitation the rights");
		out.println("to use, copy, modify, merge, publish, distribute, sublicense, and/or sell");
		out.println("copies of the Software, and to permit persons to whom the Software is");
		out.println("furnished to do so, subject to the following conditions:");
		out.println();
		out.println("The above copyright notice and this permission notice shall be included in all");
		out.println("copies or substantial portions of the Software.");
		out.println();
		out.println("THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR");
		out.println("IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,");
		out.println("FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE");
		out.println("AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER");
		out.println("LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,");
		out.println("OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE");
		out.println("SOFTWARE.");
		out.println("*/");
	}

	/**
	 * Build the constructors which initializes each message type
	 */
	private void handleConstructor() {
	}
	
	private void handleMessages() {

		Map<String, Map<String, String>> tNameToDef = ddp.getFieldParser().getTagNameToTagValueDesc();
		out.println("\t{");
		out.println("\t\tmap = new ConcurrentHashMap<>();");
		ArrayList<Entry<String, Map<String, String>>> tNameToDefList = new ArrayList<>(tNameToDef.entrySet());
		for (int j = 0; j < tNameToDefList.size(); j++) {
			Entry<String, Map<String, String>> tNameToDefListEntry = tNameToDefList.get(j);
			Map<String, String> valToDefMap = tNameToDefListEntry.getValue();
			if (valToDefMap == null || valToDefMap.isEmpty()) {
				continue;
			}
			out.println("");
			String tagName = tNameToDefListEntry.getKey();
			String mapName = tagName + "Map";
			out.println("\t\tMap<String, String> " + mapName + " = new ConcurrentHashMap<>();");
			List<Map.Entry<String, String>> list = new ArrayList<>(valToDefMap.entrySet());
			for (int i = 0; i < list.size(); i++) {
				Entry<String, String> valToDefEntry = list.get(i);
				String tagVal = valToDefEntry.getKey();
				String tagDef = valToDefEntry.getValue();
				out.println("\t\t" + mapName + ".put(" + "\"" + tagVal + "\", " + "\"" + tagDef + "\");");				
			}
			out.println("\t\tmap.put(" + "\"" + tagName + "\", " + mapName + ");");

		}
		out.println("\t}");
	}


	/**
	 * build the methods which populate the runtime data.
	 */
	@Deprecated
	private void handleAltMessages() {

		Map<String, Map<String, String>> tNameToDef = ddp.getFieldParser().getTagNameToTagValueDesc();
		out.println("\t{");
		out.println("\t\tmap = Map.ofEntries(");
		boolean needClosure = false;
		ArrayList<Entry<String, Map<String, String>>> tNameToDefList = new ArrayList<>(tNameToDef.entrySet());
		for (int j = 0; j < tNameToDefList.size(); j++) {
			
			Entry<String, Map<String, String>> tNameToDefListEntry = tNameToDefList.get(j);
			Map<String, String> valToDefMap = tNameToDefListEntry.getValue();
			if (valToDefMap == null || valToDefMap.isEmpty()) {
				continue;
			}
			if (needClosure) {
				out.println(",");
				needClosure = false;
			}
			String tagName = tNameToDefListEntry.getKey();
			out.println("\t\t\t\t\tMap.entry(\"" + tagName + "\"" + ",");
			out.println("\t\t\t\t\t\tMap.ofEntries(");
			List<Map.Entry<String, String>> list = new ArrayList<>(valToDefMap.entrySet());

			for (int i = 0; i < list.size(); i++) {
				Entry<String, String> valToDefEntry = list.get(i);
				String tagVal = valToDefEntry.getKey();
				String tagDef = valToDefEntry.getValue();
				if (i < (list.size() - 1)) {
					out.println("\t\t\t\t\t\t\tMap.entry(" + "\"" + tagVal + "\"" + ", " + "\"" + tagDef + "\"),");
				} else {
					out.print("\t\t\t\t\t\t\tMap.entry(" + "\"" + tagVal + "\"" + ", " + "\"" + tagDef + "\")))");
					needClosure = true;
				}
			}

		}
	
		out.println(");");
		out.println("\t}");
	}

	/**
	 * It's a good thing to finish
	 */
	private void finish() {
		out.println("}");
	}
}
