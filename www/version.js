/*
message = {title:'text',description:'text',scene:'0/1',thumb:'imageUrl',webpageUrl:'webpageUrl'}
pay的message直接由后台返回
*/
(function() {
  var exec;

  exec = require('cordova/exec');

  module.exports = {
    get: function( onSuccess, onError) {
      return exec(onSuccess, onError, 'VersionPlugin', 'get', []);
    }
  };

}).call(this);
