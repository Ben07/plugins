/*
message = {title:'text',description:'text',scene:'0/1',thumb:'imageUrl',webpageUrl:'webpageUrl'}
pay的message直接由后台返回
*/
(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    scene: {
      session: 0,
      timeline: 1
    },
    share: function(message, onSuccess, onError) {
      return exec(onSuccess, onError, 'WeChatPlugin', 'share', [message]);
    },
    login:function(onSuccess,onError){
      return exec(onSuccess,onError,'WeChatPlugin','login',[]);
    },
    pay:function(message,onSuccess,onError){
      return exec(onSuccess,onError,'WeChatPlugin','pay',[message]);
    }
  };

}).call(this);
