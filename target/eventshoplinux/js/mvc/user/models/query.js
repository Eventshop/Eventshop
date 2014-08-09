define([
'underscore',
'backbone'
], function(_, Backbone) {
    var queryListModel = Backbone.Model.extend({
    
        defaults: {
            qID:"",
            queryName : "",
            boundingbox : "",
            dsmasterId : "",
            control : "",
            status : ""                          
        },
        url: "/eventshoplinux/webresources/queryservice/queries",
            
    initialize: function(){
        
    }

    });
      
  return queryListModel;

});
