/*
	message = {message:'text'}
*/
(function(){
	var exec;

	exec = require('cordova/exec');

	module.exports = {
		show:function(message,onSuccess,onError){
			return exec(onSuccess,onError,'ToastPlugin','show',[message]);
		}
	};
}).call(this);