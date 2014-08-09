define([
  'jquery',
  'underscore',
  'backbone',
  'text!templates/user/datasource/registerDatasource.html',
  'mvc/user/collections/datasources'  
 ], function($, _, Backbone,registerDatasourceTpl, dataSourceCollection){
	var self;
	var registerDatasourceView = Backbone.View.extend({
    el: $("#dsTableContainer"),

	initialize: function(opts){
		  self = this;
		  self.app_router = opts.router;
		  this.sessionModel = opts.sessionModel;		 
	  },
	  
    render: function(){
    	
    	this.fetchDatasource();

    },
    fetchDatasource: function() {
    	
    	var dsList = new dataSourceCollection();
    	//window.ds_data = dsList;
		//dsList.toJSON();
    	dsOptions='';
		dsList.fetch({
			
			data: $.param({"userId":this.sessionModel.id}),
		    success: function(){
		    	//console.log("DS View: "+JSON.stringify(dsList));
		    	window.ds_data = dsList;		    	
			    var q_temp = JSON.parse(JSON.stringify(dsList));
			 	//var variables = [{  "srcName": "asthama", "access": "Public", "userListAssToDSAccess": null, "srcID": 13, "url": null, "creater": 0, "archive": 0, "unit": 0, "createdDate": null, "emailOfCreater": null, "resolution_type": null, "boundingbox": "1,1,1,1", "dsTitle": null, "finalParam": null, "type": null, "control": 0, "format": "visual", "desc": null, "status": "Connecting" }, { "srcName": "asthama", "access": "Public", "userListAssToDSAccess": null, "srcID": 14, "url": null, "creater": 0, "archive": 0, "unit": 0, "createdDate": null, "emailOfCreater": null, "resolution_type": null, "boundingbox": "1,2,1,1", "dsTitle": null, "finalParam": null, "type": null, "control": 0, "format": "visual", "desc": null, "status": "Connecting" }, { "srcName": "asthama", "access": "Public", "userListAssToDSAccess": null, "srcID": 16, "url": null, "creater": 0, "archive": 0, "unit": 0, "createdDate": null, "emailOfCreater": null, "resolution_type": null, "boundingbox": "1,1,1,1", "dsTitle": null, "finalParam": null, "type": null, "control": 0, "format": "visual", "desc": null, "status": "Connecting" }];
				var compiledTemplate = _.template(registerDatasourceTpl,{Data: q_temp});
			    //console.log("After Compile inside fetch : "+compiledTemplate);
			    self.$el.html(compiledTemplate);
			   /* dsOptions +='<option>Select Datasource</option>';*/
	            $.each(q_temp, function(i, obj) {

	            dsOptions += '<option value="ds'+obj['srcID']+'">DS'+obj['srcID']+':'+obj['srcName']+'</option>';   
	              });
	            
	            $('#sSource').html(dsOptions);
	            $('#sSource').multiSelect('refresh');
	          
	           
	            $('#gSource').html(dsOptions);
	            $('#gSource').multiSelect('refresh');
	            $('#aSource').html(dsOptions);
	            $('#aSource').multiSelect('refresh');
	            $('#pSource').html(dsOptions);
	            $('#pSource').multiSelect('refresh');
	            $('#tSource').html(dsOptions);
	            $('#tSource').multiSelect('refresh');
	            $('#spaceCharSource').html(dsOptions);
	            $('#spaceCharSource').multiSelect('refresh');
	            $('#tempCharSource').html(dsOptions);
	            $('#tempCharSource').multiSelect('refresh');
			    
			    
			}			   
	 });
    }

  });

  return registerDatasourceView;
  
});
