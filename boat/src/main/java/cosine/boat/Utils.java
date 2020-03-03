package cosine.boat;

import java.io.*;
import java.util.*;
import android.content.res.*;
import org.apache.commons.compress.compressors.xz.*;
import org.apache.commons.compress.archivers.tar.*;
import org.apache.commons.compress.archivers.examples.*;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.utils.*;

public final class Utils {
	
	
	public static File createFile(String filePath){
		File file = new File(filePath);
		return Utils.createFile(file);
	}
	public static File createFile(File file){
		if (file.exists()){
			file.delete();
		}
		file.getParentFile().mkdirs();

		try
		{
			file.createNewFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return file;
	}
	public static byte[] readFile(String filePath){
		return Utils.readFile(new File(filePath));
	}
	public static byte[] readFile(File file){
		FileInputStream fis = null;
		try{
			
			fis=new FileInputStream(file);
			byte result[]=new byte[(int)file.length()];
			fis.read(result);
			fis.close();
			return result;
		}catch(Exception e){

			e.printStackTrace();
		}
		finally{
			if (fis != null){
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	
	public static boolean writeFile(File file, byte[] bytes){
		
		file = Utils.createFile(file);
		
		if (file == null){
			return false;
		}
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.flush();
			fos.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if (fos != null){
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	public static boolean writeFile(File file, String str){

		boolean retval = false;
		try
		{
			retval = Utils.writeFile(file, str.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return retval;
	}
	
	public static boolean writeFile(String outFile, String str){
		return writeFile(new File(outFile), str);
	}
	
	public static boolean extractAsset(AssetManager am, String src, File targetFile ){
		FileOutputStream fos = null;
		InputStream is = null;
		
		try
		{
			targetFile = Utils.createFile(targetFile);
			
			fos = new FileOutputStream(targetFile);
			
			is = am.open(src);
			byte[] buf = new byte[1024];
			int count = 0;
			
			while ((count = is.read(buf)) != -1) {
				fos.write(buf, 0, count);
			}

			fos.flush();
			fos.close();
			is.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			
			
			return false;
		}
		finally{
			
			if (is != null){
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (fos != null){
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
		}
	

	
	}
	public static boolean extractAsset(AssetManager am, String src, String target ){
		return extractAsset(am, src, new File(target));
	}
	
	public static void extractTarXZ(File tarFile, File destDir){
		
		FileInputStream fis = null;
		XZCompressorInputStream xzcis = null;
		TarArchiveInputStream tais = null;          
		
		OutputStream fos = null;
		try {
			fis = new FileInputStream(tarFile);             
			xzcis = new XZCompressorInputStream(fis);		          
			tais = new TarArchiveInputStream(xzcis, 1024);
			
			TarArchiveEntry entry;		    
			while((entry = tais.getNextTarEntry()) != null){
				File target = new File(destDir, entry.getName());
				if (entry.isDirectory()) {			      
					target.mkdirs();
				} else {		   
					fos = new FileOutputStream(target);
					
					IOUtils.copy(tais, fos);
					fos.flush();
					fos.close();
				}	   
			}
			
			tais.close();
			xzcis.close();
			fis.close();

		} catch (IOException e) {	    
			e.printStackTrace();	       
		}finally {	        
			try {		  
				if(fis != null){			
					fis.close();		
				}		   
				if(fos != null){			
					fos.close();			
				}	   
				if(xzcis != null){
					xzcis.close();			
				}		
				if(tais != null){
					tais.close();			
				}		
			} catch (IOException e) {		  
                e.printStackTrace();
			}	
		}  
		
		
	}

	
	public static void extractTarXZ(String tar, File destDir){
		extractTarXZ(new File(tar), destDir);
	}
	public static void extractTarXZ(File tarFile, String dir){
		extractTarXZ(tarFile, new File(dir));
	}
	
	public static void extractTarXZ(String tar, String dir){
		extractTarXZ(new File(tar), new File(dir));
	}
	
	public static boolean setExecutable(File file){
		boolean retval = true;
		if (file.isDirectory()){
			File subFiles[] = file.listFiles();
			for (File subFile : subFiles){
				retval = retval && setExecutable(subFile);
			}
		}
		retval = retval && file.setExecutable(true);
		return retval;
	}
	
	public static boolean setExecutable(String file){
		return setExecutable(new File(file));
	}
}


