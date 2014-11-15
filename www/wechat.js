/*
message = {title:'text',description:'text',scene:'0/1',thumb:'imageUrl',webpageUrl:'webpageUrl'}
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
    }
  };

}).call(this);
