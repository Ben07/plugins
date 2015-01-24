(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    clear: function() {
      return exec(function(){}, function(){}, 'ClearPlugin', 'clear', []);
    }
  };

}).call(this);
