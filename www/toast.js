/*
	message = {message:'text'}
*/
(function(){
	var exec;

	exec = require('cordova/exec');

	module.exports = {
		show:function(message,onSuccess,onError){
			if(message.time === undefined){
				message.time = 0;
			}
			if(message.position === undefined){
				message.position = 0;
			}
			return exec(onSuccess,onError,'ToastPlugin','show',[message]);
		}
	};
}).call(this);