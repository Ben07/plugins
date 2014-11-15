/*
message = {title:'text',description:'text',thubm:'imageUrl',webpageUrl:'webpageUrl'}
*/
(function(){
    var exec;

    exec = require('cordova/exec');

    module.exports = {
        share:function(message,onSuccess,onError){
            return exec(onSuccess,onError,'QQPlugin','share',[message]);
        }
    };
}).call(this);