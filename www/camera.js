/*
type=0不需要水印,需要水印时需要加userId
message = {type:'0/1',userId:'50'}
*/
(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    camera: function(message, onSuccess, onError) {
      return exec(onSuccess, onError, 'CameraPlugin', 'camera', [message]);
    },
    picture:function(message,onSuccess,onError){
      return exec(onSuccess,onError,'CameraPlugin','picture',[message]);
    }
  };

}).call(this);
