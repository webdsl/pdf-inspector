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

entity InspectorRequest{
	file : File
	result : Text
}

define requestForm(){
	var file : File
	
	"Upload a pdf and get meta-data from that pdf. Warning: this may take seconds to minutes"
	
	form {
		input( file )
		submit action {
			validate( (file != null ) , "Select a valid file");
			var ir := InspectorRequest{ file := file };
			ir.result := Inspector.getInfo(file);
			ir.save();
			return inspectorResult(ir);
		}{ "upload and check" }
	}  	
}

define page inspectorResult(req : InspectorRequest){
  main()
  define body() {
	requestForm()
	showInfo(req)
  }
}

define showInfo(req : InspectorRequest){
	header{ output(req.file.getFileName()) }
	<pre>
	  output(req.result)
	</pre>
}


  native class org.webdsl.pdfutils.Inspector as Inspector {
    static getInfo(File) : String
  }