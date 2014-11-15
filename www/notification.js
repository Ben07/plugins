/*
the function of onSuccess is used to send device_token
*/

(function(){
	var exec;
	exec = require('cordova/exec');

	module.exports = {
		open:function(onSuccess,onError){
			return exec(onSuccess,onError,'NotificationPlugin','open',[]);
		}
	};
}).call(this);