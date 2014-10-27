package com.remindnote.remind;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regex 
 * @author guangxiang.liang
 *
 */
public class RegexUtil {
	private Pattern p=null;
	private Matcher m=null;
	
	public static final String REGEX_NOT_NULL="\\S";
	public static final String REGEX_WORD="\\w";
	
	public RegexUtil(String regexExp,String value){
		p=Pattern.compile(regexExp);
		m=p.matcher(value);
	}
	public boolean isMatch(){
		return m.find();
	}
}
