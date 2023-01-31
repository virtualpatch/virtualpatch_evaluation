package dev.virtualpatch.patch;

import android.util.Log;

import java.util.LinkedList;

public class Utils {
    public static boolean sqli_detection(String selection) {
        LinkedList<String> sqli_verifier = new LinkedList<>();
        for (char c : selection.toCharArray()) {
            if (c == '(' || c == ')') sqli_verifier.add(String.valueOf(c));
        }
        boolean complete = false;
        boolean modified;
        while (!complete) {
            modified = false;
            int curr = -1;
	            for (int i = 0; i < sqli_verifier.size(); i++) {
	                if (curr != -1 && sqli_verifier.get(curr).equals("(") && sqli_verifier.get(i).equals(")")) {
	                	sqli_verifier.remove(curr);
	                	sqli_verifier.remove(i-1);
	                	curr = -1;
	                	modified = true;
	                }
	            	curr = i;
	            }
            
            if (!modified) complete = true;
        }
        if (!sqli_verifier.isEmpty()) {
            Log.wtf(Patch.TAG, "SQLi detected");
            return true;
        }
        return false;
    }
}
