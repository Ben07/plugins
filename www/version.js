(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    get: function( onSuccess, onError) {
      return exec(onSuccess, onError, 'VersionPlugin', 'get', []);
    }
  };

}).call(this);
