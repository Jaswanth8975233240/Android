package com.intelliq.appengine;

import java.util.List;

public class MergeHelper {

	public static String mergeStrings(String existingValue, String newValue) {
		if (!ParserHelper.containsAnyValue(existingValue)) {
			return newValue;
		} else {
			return existingValue;
		}
	}
	
	public static List<String> mergeListsOfString(List<String> existingList, List<String> newList) {
		if (existingList == null || existingList.size() == 0) {
			return newList;
		}
		for (String newString: newList) {
			if (!existingList.contains(newString)) {
				existingList.add(newString);
			}
		}
		return existingList;
	}
	
	public static List<Long> mergeListsOfLong(List<Long> existingList, List<Long> newList) {
		if (existingList == null || existingList.size() == 0) {
			return newList;
		}
		for (long newValue: newList) {
			if (!existingList.contains(newValue)) {
				existingList.add(newValue);
			}
		}
		return existingList;
	}
	
}
