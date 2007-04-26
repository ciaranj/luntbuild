<%@ taglib uri="http://jakarta.apache.org/tapestry/tld/tapestry_1_0.tld" prefix="tapestry" %>
	
<html>
<head>
<title>Tapestry JSP Page</title>
</head>
<body>


<form action="<tapestry:external-url page="JSPForm"/>" method="POST">
	
Enter your name: <input type="text" name="userName" maxlength="100" size="40"/>
<br/>
<input type="submit"/>

</form>



</body>
</html>
