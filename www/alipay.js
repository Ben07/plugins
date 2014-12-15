/*
message 为后台返回
*/
(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    pay:function(message,onSuccess,onError){
      return exec(onSuccess,onError,'WeChatPlugin','pay',message);
    }
  };

}).call(this);
