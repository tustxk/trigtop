package com.netxeon.lignthome.util;

import java.util.Locale;

public class LanguageUtils {

	
	public int  getLang() {
		String language = getLanguageEnv();  
		if (language != null  
                && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))  
            return 1;  
        else  
            return 0;  
		
	}
		
	private String getLanguageEnv() {  
	       Locale l = Locale.getDefault();  
	       String language = l.getLanguage();  
	       String country = l.getCountry().toLowerCase();  
	       if ("zh".equals(language)) {  
	           if ("cn".equals(country)) {  
	               language = "zh-CN";  
	           } else if ("tw".equals(country)) {  
	               language = "zh-TW";  
	           }  
	       } else if ("pt".equals(language)) {  
	           if ("br".equals(country)) {  
	               language = "pt-BR";  
	           } else if ("pt".equals(country)) {  
	               language = "pt-PT";  
	           }  
	       }  
	       return language;  
	   }  
}
