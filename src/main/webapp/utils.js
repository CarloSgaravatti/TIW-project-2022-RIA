/**
 * AJAX call management
 */

	function makeCall(method, url, formElement, callbak, reset = true) {
	    var req = new XMLHttpRequest();
	    req.onreadystatechange = function() {
	      callbak(req)
	    };
	    req.open(method, url);
	    if (formElement == null) {
	      req.send();
	    } else {
	      req.send(new FormData(formElement));
	    }
	    if (formElement !== null && reset === true) {
	      formElement.reset();
	    }
    }
