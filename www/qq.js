/*
message = {title:'text',description:'text',thumb:'imageUrl',webpageUrl:'webpageUrl'}
*/
(function(){
    var exec;

    exec = require('cordova/exec');

    module.exports = {
        share:function(message,onSuccess,onError){
            return exec(onSuccess,onError,'QQPlugin','share',[message]);
        },
        login:function(onSuccess,onError){
        	return exec(onSuccess,onError,'QQPlugin','login',[]);
        }

    };
}).call(this);