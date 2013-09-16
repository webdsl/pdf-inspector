application exampleapp

description {
  A simple app that uses CRUD page generation for managing a Person entity
}

imports templates
imports search/searchconfiguration
section pages

define page root() {
  main()
  define body() {
	requestForm()
  }
}

entity InspectorRequest {
	file : File
	result <> ExtractionResult
}

entity ExtractionResult {
	title : Text
	references <> List<ReferenceEntry>
	programOutput : Text
	executionLog : Text	
}

entity ReferenceEntry {
	data : Text
}

define requestForm(){
	var file : File
	
	<h2>"Upload new pdf: "</h2> 
	par {
	"Upload a pdf and get meta-data from that pdf. Warning: this may take seconds to minutes"
    }
	form {
		input( file )
		submit action {
			validate( (file != null ) , "Select a valid file");
			var ir := InspectorRequest{ file := file };
			inspect( ir );
			
			return inspectorResult(ir);
		}{ "upload and check" }
	}  	
}

function inspect( ir : InspectorRequest ) : InspectorRequest{
	var result := Inspector.getInfo( ir.file );
	var refs := List<ReferenceEntry>();
	
	for ( ref : String in result.getReferences() ){
		refs.add( ReferenceEntry{ data := ref } );
	}
	
	var eResult := ExtractionResult{
					title := result.getTitle()
					references := refs
					executionLog := result.getExecutionLog()
					programOutput := result.getProgramOutput()
				   };
	ir.result := eResult;
	return ir;
}

define page inspectorResult(req : InspectorRequest){
  main()
  define body() {
	showInfo(req)
	<hr />
	requestForm()
  }
}

define page latestRequests(lim : Int){
	var requests := from InspectorRequest order by modified desc limit ~lim;
		
	main()
	
	define body() {	
		<h2> "Latest evaluations:" </h2>
		list{
			for(r: InspectorRequest in requests){
				listitem{ output( r ) }
			}
		}
		par{
			navigate( latestRequests(lim+100) ){ "more results" }
		}
	}
}

define output(r : InspectorRequest){
	output(r.modified) ": "
	navigate( inspectorResult(r) ){ output(r.file.getFileName())  } 
	" --- "
	remove(r)
}

define output(r : ExtractionResult){
	var counter:= 1;
	table{
		row {
			column{ <strong>"Execution"</strong> } column{ <pre> output( r.executionLog ) </pre> }
		}
		row {
			column{ <strong>"Title"</strong> } column{ output( r.title ) }			
		}
		
		row { column2{ <strong>"References (" output(r.references.length) ")"</strong>	} }	
		for( re : ReferenceEntry in r.references){
			row { column{ output( counter ) } column{ output( re.data ) } }
			render{
				counter := counter + 1;
			}
		}
		
		row { column2{ <strong>"Original program output"</strong> } }
		row { column2{ <pre> output(r.programOutput) </pre> } }
		
	}
}

define remove(r : InspectorRequest){
	submit remove(r){"remove"}
	action remove( r: InspectorRequest ){
		r.file := null;
		r.delete();
		return latestRequests(50);
	}
}

define showInfo(req : InspectorRequest){
	<h2> "Pdf-extract results for file: " output(req.file.getFileName()) </h2>
	par{ "download pdf-file: " output(req.file) }
	par{ "rerun pdf-extract: "
		submit action {
			inspect( req );
			return inspectorResult(req);
		}{"re-evaluate"}
	}
	par{ "remove this request: " remove(req)}
	par{ "extraced data:"
	    if( req.result == null) {
	    	" NO DATA, please re-evaluate"
	    } else {
			output(req.result)
		}
	}
}

define page pdfextract_config(){
	var flexValue :Float := Inspector.getFlexValue()
	
	main()	
	
	define body() {
		par{
			form{
				"Flex-value: " input(flexValue)
				submit action{ Inspector.setFlexValue(flexValue); }{ "set"}
			}
		}
	}
}

native class org.webdsl.pdfutils.Inspector as Inspector {
	static getInfo(File) : PDFExtractData
	static getFlexValue(): Float
	static setFlexValue(Float)
}

native class org.webdsl.pdfutils.PDFExtractData as PDFExtractData {
	getTitle() : String
	getReferences() : List<String>
	getExecutionLog() : String
	getProgramOutput() : String
}