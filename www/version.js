(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    get: function(onSuccess, onError) {
      return exec(onSuccess, onError, 'VersionPlugin', 'get', []);
    },
    check:function(onSuccess, onError){
    	return exec(onSuccess, onError, 'VersionPlugin', 'check', []);
    }
  };

}).call(this);
