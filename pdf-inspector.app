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
    "Hello world!"
    
    submit run() { "run" }
    action run(){
    	Inspector.getInfo();
    }
  }
}


  native class org.webdsl.pdfutils.Inspector as Inspector {
    static getInfo() : String
  }