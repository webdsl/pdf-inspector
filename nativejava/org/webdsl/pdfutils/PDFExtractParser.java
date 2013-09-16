package org.webdsl.pdfutils;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PDFExtractParser {

	public static PDFExtractData parse( InputStream is ) {
		final PDFExtractData data = new PDFExtractData();
		
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean inTitle = false;
				boolean inReference = false;
				
				public void startElement( String uri, String localName,
						String qName, Attributes attributes )
						throws SAXException {
					
					setInState(qName, true);
				}

				public void endElement( String uri, String localName,
						String qName ) throws SAXException {

					setInState(qName, false);

				}

				public void characters( char ch[], int start, int length )
						throws SAXException {
					
					String charsAsString = new String( ch, start, length );

					if ( inTitle ) {
						data.setTitle( charsAsString );
					} else if ( inReference ){
						data.addReference( charsAsString );
					}

				}
				
				public void setInState ( String name, boolean state){
					if ( name.equalsIgnoreCase( "TITLE" ) ) {
						inTitle = state;
					} else if ( name.equalsIgnoreCase( "REFERENCE" ) ) {
						inReference = state;
					}
				}

			};

			saxParser.parse(is, handler);

		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return data;
	}

}
