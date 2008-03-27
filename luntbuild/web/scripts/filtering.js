// ***************************************************************
//
//		GLOBAL VARS
//
// ***************************************************************

/**
 * Main function
 */ 
function filter(textField, event) {
	var filterValue = textField.value;
	
	var row = document.getElementById("buildElement-" + 0);
	var index = 0;
	while (row != null) {
		setVisibility(row, filterValue);
		// go next
		index++;
		row = document.getElementById("buildElement-" + index);
	}
}

function setVisibility(row, filterValue) {
	if (filterValue == '') {
		show(row.id);
		return;
	}
	
	var found = containsString(row, filterValue);
	if (found == true) {
		show(row.id);
	} else {
		hide(row.id);
	}
}

function containsString(element, filterValue) {
	if (element.nodeName == "#text") {
		var text = element.data;
		if (text.indexOf(filterValue) >= 0) {
			return true;
		} else {
			return false;
		}
	} else {
		var children = element.childNodes;
		for (var i =0; i<children.length; i++) {
			var found = containsString(children[i], filterValue);
			if (found == true) {
				return found;
			}
		}
	}
}

function hide(id) {
	//safe function to hide an element with a specified id
	if (document.getElementById) { // DOM3 = IE5, NS6
		document.getElementById(id).style.display = 'none';
	}
	else {
		if (document.layers) { // Netscape 4
			document.id.display = 'none';
		}
		else { // IE 4
			document.all.id.style.display = 'none';
		}
	}
}

function show(id) {
	//safe function to show an element with a specified id
		  
	if (document.getElementById) { // DOM3 = IE5, NS6
		document.getElementById(id).style.display = '';
	}
	else {
		if (document.layers) { // Netscape 4
			document.id.display = '';
		}
		else { // IE 4
			document.all.id.style.display = '';
		}
	}
}

// For debugging purpose only : add <div id ="console"></div> somewhere in the page
function console(message) {
	var div = document.getElementById('console');
	div.style.border="solid thin blue";
	var currentText = div.innerHTML;
	div.innerHTML = div.innerHTML + "<br/>" + message;
}