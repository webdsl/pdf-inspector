package org.webdsl.pdfutils;

import java.io.*;
import java.sql.SQLException;

import com.google.common.io.Files;

public class Inspector {
	
	private static final String PDFEXTRACT_CMD_PREFIX = "pdf-extract extract --references ";

	public Inspector() {
	}

	public static String getInfo(utils.File pdfFile) {

		InputStream is = null;
		OutputStream os = null;
		File dstDir = null;
		File dstFile = null;
		StringBuilder resultSb = new StringBuilder(256);
				
		// create temp dir and file, and write content stream to file
		try {
			is = pdfFile.getContentStream();
			dstDir = Files.createTempDir();
			dstFile = File.createTempFile(pdfFile.getFileName(), "", dstDir);
			os = new FileOutputStream(dstFile);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					// outputStream.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 
			}
		}		

		

		try {
			Runtime rt = Runtime.getRuntime();
			// Process pr = rt.exec("cmd /c dir");
			
			String path =  dstFile.getAbsolutePath();
			Process pr = rt.exec(PDFEXTRACT_CMD_PREFIX + path);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {
				resultSb.append(line);
				resultSb.append("\n");
				System.out.println(line);
			}

			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		// delete temp dir and content
		try {
			delete(dstDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resultSb.toString();
	}

	private static void delete(File f) throws IOException {
		if (f == null)
			return;
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

}