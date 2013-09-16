package org.webdsl.pdfutils;

import java.io.*;
import java.sql.SQLException;

import com.google.common.io.Files;

public class Inspector {

	private static float FLEX_VALUE = 0.20f;
	private static String FLEX_SETTING;
	private static String PDFEXTRACT_OPTIONS;
	private static String PDFEXTRACT_CMD;
	private static String PDFEXTRACT_CMD_PREFIX;

	static {
		buildCommandStrings();
	}

	public Inspector() {
	}

	private static void buildCommandStrings() {
		FLEX_SETTING = " --set reference_flex:" + FLEX_VALUE;
		PDFEXTRACT_OPTIONS = " --titles --references" + FLEX_SETTING;
		PDFEXTRACT_CMD = "pdf-extract extract" + PDFEXTRACT_OPTIONS;
		PDFEXTRACT_CMD_PREFIX = PDFEXTRACT_CMD + " ";
	}

	public static void setFlexValue( float f ) {
		FLEX_VALUE = f;
		buildCommandStrings();
	}

	public static float getFlexValue() {
		return FLEX_VALUE;
	}

	public static PDFExtractData getInfo( utils.File pdfFile ) {

		InputStream is = null;
		OutputStream os = null;
		File dstDir = null;
		File dstFile = null;
		StringBuilder resultSb = new StringBuilder( 256 );
		StringBuilder executionSb = new StringBuilder( 256 );
		PDFExtractData pdfExtractData = null;

		// create temp dir and file, and write content stream to file
		try {
			is = pdfFile.getContentStream();
			dstDir = Files.createTempDir();
			dstFile = File.createTempFile( "Inspector-File-", ".pdf", dstDir );
			os = new FileOutputStream( dstFile );

			int read = 0;
			byte[] bytes = new byte[1024];

			while ( ( read = is.read( bytes ) ) != -1 ) {
				os.write( bytes, 0, read );
			}

		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( SQLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if ( is != null ) {
				try {
					is.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
			if ( os != null ) {
				try {
					// outputStream.flush();
					os.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}

			}
		}

		try {
			Runtime rt = Runtime.getRuntime();
			// Process pr = rt.exec("cmd /c dir");

			String path = dstFile.getAbsolutePath();
			String command = PDFEXTRACT_CMD_PREFIX + path;
			System.out.println( "Executing:" + command );
			long start = System.currentTimeMillis();
			Process pr = rt.exec( command );

			BufferedReader input = new BufferedReader( new InputStreamReader(
					pr.getInputStream() ) );

			String line = null;

			while ( ( line = input.readLine() ) != null ) {
				resultSb.append( line );
				resultSb.append( "\n" );
				System.out.println( line );
			}

			int exitVal = pr.waitFor();
			System.out.println( "Exited with error code " + exitVal );
			if ( exitVal != 0 ) {
				executionSb
						.append( "Error: pdf-extract exited with error code "
								+ exitVal );
			}
			executionSb.append( "\n*executed command: " ).append( command )
					.append( "\n*pdf-extract execution time (ms): " )
					.append( System.currentTimeMillis() - start )
					.append( "\n*pdf flex value: " ).append( FLEX_VALUE )
					.append( "\n\n" );

			String programOutput = resultSb.toString();
			InputStream xmlIs = new ByteArrayInputStream( programOutput.getBytes() );
			pdfExtractData = PDFExtractParser.parse( xmlIs );
			pdfExtractData.setExecutionLog( executionSb.toString() );
			pdfExtractData.setProgramOutput( programOutput );

		} catch ( Exception e ) {
			System.out.println( e.toString() );
			e.printStackTrace();
		}

		// delete temp dir and content
		try {
			delete( dstDir );
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		return pdfExtractData;
	}

	private static void delete( File f ) throws IOException {
		if ( f == null )
			return;
		if ( f.isDirectory() ) {
			for ( File c : f.listFiles() )
				delete( c );
		}
		if ( !f.delete() )
			throw new FileNotFoundException( "Failed to delete file: " + f );
	}

}