package twelveutil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import twelveengine.Log;
import twelveengine.Settings;

/** 
 * 
 * @author jon 
 */ 
public class Decompress { 

	//Trys to extract <file> from <zip>. if it can't it will return null.
  public static InputStream unzip(String zip, String file) { 
      try  { 
      FileInputStream fin = new FileInputStream(zip); 
      ZipInputStream zin = new ZipInputStream(fin); 
      ZipEntry ze = null; 
      
      Log.log("Decompress: " + zip + "." + file, "Engine ", 0);
      while ((ze = zin.getNextEntry()) != null) { 
        Log.log("File: " + ze.getName().toString(), "Engine", 0);
        
        if(ze.isDirectory()) { 
        	//SKIP!
        } 
        else { 
            /**** Changes made below ****/
            if (ze.getName().toString().equals(file)) {
            	return zin;
            }
            else {
            	
            }
        }

          zin.closeEntry(); 
      }
      zin.close(); 
    } catch(Exception e) { 
      Log.log("Failed to unzip: " + zip + "." + file, "Engine", 2); 
      e.printStackTrace();
    }
      return null;
  }
}