package org.webdsl.pdfutils;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PDFExtractParser {

	public static PDFExtractData parse( InputStream is ) {
		final PDFExtractData data = new PDFExtractData();
		
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				String currentValue;
				
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

					currentValue += charsAsString;

				}
				
				public void setInState ( String name, boolean state){
					if ( name.equalsIgnoreCase( "TITLE" ) ) {
						if(!state)
							data.setTitle( currentValue );
					} else if ( name.equalsIgnoreCase( "REFERENCE" ) ) {
						if(!state)
							data.addReference( currentValue );								
					}
					currentValue = "";
				}

			};
			InputSource inputSource = new InputSource(new InputStreamReader(is));
			saxParser.parse(inputSource, handler);

		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return data;
	}

}
