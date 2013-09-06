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
	
	<h2>"Upload new pdf: "</h2> 
	par {
	"Upload a pdf and get meta-data from that pdf. Warning: this may take seconds to minutes"
    }
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
	showInfo(req)
	<hr />
	requestForm()
  }
}

define page latestRequests(lim : Int){
	var requests := from InspectorRequest order by created asc limit ~lim;
		
	main()
	
	define body() {	
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
	output(r.created) ": "
	navigate( inspectorResult(r) ){ output(r.file.getFileName())  } 
	" --- "
	remove(r)
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
			req.result := Inspector.getInfo(req.file);
			req.save();
			return inspectorResult(req);
		}{"re-evaluate"}
	}
	par{ "remove this request: " remove(req)}
	par{
		"pdf-extract output:"
		<pre>
		  output(req.result)
		</pre>
	}
}


  native class org.webdsl.pdfutils.Inspector as Inspector {
    static getInfo(File) : String
  }