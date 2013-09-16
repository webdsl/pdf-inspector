package org.webdsl.pdfutils;

import java.util.ArrayList;
import java.util.List;

public class PDFExtractData {

	private String title, executionLog, programOutput;
	private List<String> references;

	public PDFExtractData() {
		title = "";
		references = new ArrayList<String>();
		executionLog = "";
		programOutput = "";
	}

	public void setExecutionLog( String l ) {
		this.executionLog = l;
	}

	public String getExecutionLog() {
		return executionLog;
	}

	public void setProgramOutput( String o ) {
		this.programOutput = o;
	}

	public String getProgramOutput() {
		return programOutput;
	}

	public void setTitle( String t ) {
		this.title = t;
	}

	public String getTitle() {
		return title;
	}

	public void addReference( String r ) {
		this.references.add( r );
	}

	public List<String> getReferences() {
		return references;
	}

}
