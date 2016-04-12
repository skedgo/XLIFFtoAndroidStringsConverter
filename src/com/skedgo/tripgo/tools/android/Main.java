package com.skedgo.tripgo.tools.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		// arg[0] android string path
		// arg[1] translations path
		// arg[2] android specific strings file name (android_localizable_strings.xml|void)
		// arg[3] Languages to generate separated by "#" (for example, en#es#de#fi#zh-Hant#zh-Hans)
		// arg[4] xliff file name (iOS.xliff)
		// arg[5] xliff file name (android_localizable_strings.xliff)
		// ...
		// arg[n] xliff file name
		
		
		if(args != null && args.length > 4 ){
			String androidStringPath = args[0] ;
			String translationsPath = args[1] ;
			String androidSpecificStringsFile = args[2] ;
			String[] langsArray = args[3].split("#");
			List<String> langs = new ArrayList<>(Arrays.asList(langsArray));
			
			List<String> xliffStringsList = new ArrayList<>((args.length-4));
			
			for (int i = 4; i < args.length; i++) {	
				xliffStringsList.add(args[i]);
			}
			
			StringsGeneratorUtils.getInstance().transformAllStrings(androidStringPath,translationsPath,
					androidSpecificStringsFile, xliffStringsList, langs);			
			
		}else{
			throw new Error("Wrong parameters...");
		}
		
		
		
		
		System.out.println("Strings done! Time: " + (System.currentTimeMillis() - startTime) + "milisecs");
		
		
	}
	

}
