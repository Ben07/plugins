/*
message为{message:message}
*/
(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    pay:function(message,onSuccess,onError){
      return exec(onSuccess,onError,'AliPayPlugin','pay',[message]);
    }
  };

}).call(this);
