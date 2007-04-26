<%@ taglib uri="http://jakarta.apache.org/tapestry/tld/tapestry_1_0.tld" prefix="tapestry" %>
	
<html>
<head>
<title>Tapestry JSP Page</title>
</head>
<body>
<script>
<!--

function raiseTarget(targetURL)
{
  var width = 200;
  var height = 200;
	var screenX = Math.floor((screen.width)/2)-Math.floor(width/2);
	var screenY = Math.floor((screen.height)/2)-Math.floor(height/2)-20;


	var features = 
		"toolbar=no," +
		"scrollbars=no," +
		"status=no," +
		"top=" + screenY  + "," +
		"left=" + screenX + "," +
		"screenX=" + screenX + "," +
		"screenY=" + screenY + "," +
		"width=" + width + "," +
		"height=" + height;

	window.open(targetURL, "TargetPage", features).focus();
}
-->	
</script>

Two examples of invoking Tapestry pages from a JSP using the tag library:

<ul>
	<li>Using &lt;tapestry:page&gt; to <tapestry:page page="Target">display the target page</tapestry:page></li>	
	<li>Using &lt;tapestry:page-url&gt; to <a href="javascript:raiseTarget('<tapestry:page-url page="Target"/>');">raise the target page in a new window</a></li>
</ul>



</body>
</html>
