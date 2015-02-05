(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    clear: function(onSuccess,onError) {
      return exec(onSuccess, onError, 'ClearPlugin', 'clear', []);
    }
  };

}).call(this);
