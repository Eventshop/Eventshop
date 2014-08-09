define([
'jquery',
'underscore',
'backbone',
'mvc/user/models/query'
], function($, _, Backbone, queryListModel) {
	var queryListCollection = Backbone.Collection.extend({
		
		model: queryListModel,
		//url: "/eventshoplinux/webresources/adminservice/getquerylist",
		url: "/eventshoplinux/webresources/queryservice/queries",
		parse: function(response){
				//console.log("Response From Server: "+JSON.stringify(response));
		    	return response;
		}
			
	});
	
	return queryListCollection;
});