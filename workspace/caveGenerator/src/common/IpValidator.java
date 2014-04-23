package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpValidator {
	private static final String PATTERN = 
	        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static boolean validateIpV4(final String ip){          

	      Pattern pattern = Pattern.compile(PATTERN);
	      Matcher matcher = pattern.matcher(ip);
	      return matcher.matches();             
	}
	
	
}
