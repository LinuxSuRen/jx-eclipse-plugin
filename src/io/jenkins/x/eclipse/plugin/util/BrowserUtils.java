/**
 * 
 */
package io.jenkins.x.eclipse.plugin.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author suren
 */
public abstract class BrowserUtils {
	public static void main(String[] args) {
		open("http://surenpi.com");
	}
	
    public static boolean open(String url) {
    	if(url == null) {
    		return false;
    	}
    	
        String osName = System.getProperty("os.name", "");  
        if (osName.startsWith("Mac OS")) {  
			try {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
	            Method openURL = fileMgr.getDeclaredMethod("openURL",  
	                    new Class[] { String.class });  
	            openURL.invoke(null, new Object[] { url }); 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}   
        } else if (osName.startsWith("Windows")) {  
            try {
				Runtime.getRuntime().exec(  
				        "rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}  
        } else {
            String[] browsers = {"chromium-browser", "chrome", "firefox", "opera", "konqueror", "epiphany",  
                    "mozilla", "netscape" };  
            String browser = null;  
            for (int count = 0; count < browsers.length && browser == null; count++)
				try {
					if (Runtime.getRuntime()  
					        .exec(new String[] { "which", browsers[count] })  
					        .waitFor() == 0)  
					    browser = browsers[count];
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}  
            if (browser == null){
            	return false;
            } else {
            	try {
					Runtime.getRuntime().exec(new String[] {browser, url});
				} catch (IOException e) {
					e.printStackTrace();
				}  
            }
        }
        
        return true;
    }
}
