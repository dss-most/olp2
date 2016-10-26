/**
 * 
 */
//Globally register console
if (typeof console === 'undefined') {
    console = { log: function() {} };
}
// Globally appName
function appUrl(url) {
	return '/olp/'+url;
}