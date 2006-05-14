<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>luntbuild - make your software building manageable</title>
<link rel="stylesheet" type="text/css" href="css/luntbuild.css" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/ico"/>
</head>

<body>

<form method="POST" action="j_acegi_security_check.do">
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr bordercolor="0">
      <td bordercolor="0">
      <table width="100%" height="93%" border="0" cellpadding="6" cellspacing="0" bgcolor="#336699">
        <tr>
          <th width="84%" height="33" align="left" class="logoText">LUNTBUILD</th>
          <th width="16%" align="right" nowrap><span class="productVersion"><%=com.luntsys.luntbuild.utility.Luntbuild.buildInfos.getProperty("buildVersion")%>
          </span></th>
          <th width="4%" align="center">
          <a href="http://luntbuild.javaforge.com/" title="Goto Luntbuild's Home" >
          <img src="images/home.gif" width="22" height="18" border="0" align="absmiddle"></a></th>
        </tr>
      </table>
      </td>
    </tr>
    <tr>
      <td bordercolor="0">
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="hierarchyNavigation">
        <tr>
          <td width="68%" height="20">
          <img src="images/tree.gif" width="21" height="14" border="0" align="absmiddle">
          Home</td>
          <td width="16%" align="right">&nbsp;</td>
        </tr>
      </table>
      </td>
    </tr>
    <tr align="center">
      <td bordercolor="0">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr class="thinRow">
          <td width="7" rowspan="5" bgcolor="#EEEEEE">&nbsp;</td>
          <td height="5" bgcolor="#EEEEEE">&nbsp;</td>
          <td width="5" colspan="1" rowspan="5" bgcolor="#EEEEEE">&nbsp; </td>
        </tr>
        <tr>
          <td height="25" bgcolor="#FFFFFF">
          <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" bordercolor="#FFFFFF">
            <tr bordercolor="#FFFFFF">
              <td width="120" height="25" class="selectedTab" align="center">
              Login </td>
              <td width="5" class="tabSpace">&nbsp;</td>
              <td width="5" class="tabSpace">&nbsp;</td>
              <td bgcolor="#EEEEEE" class="tabSpace">&nbsp;</td>
              <td width="100" bgcolor="#EEEEEE" class="tabSpace">&nbsp;</td>
            </tr>
          </table>
          </td>
        </tr>
        <tr bgcolor="#CCCCCC">
          <td align="center" valign="top" bgcolor="#FFFFFF" class="tabContent">
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td height="13" align="left" valign="top" bgcolor="#CCCCCC">
              <table width="100%" border="0" cellpadding="0" cellspacing="0" class="iconTable">
                <tr class="iconTable">
                  <td>&nbsp;</td>
                  <td width="25">&nbsp;</td>
                </tr>
              </table>
              </td>
            </tr>
            <tr>
              <td align="left" valign="top">
              <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr align="left">
                  <td colspan="2" class="centerTableDescriptionRow" scope="col">
                  <img src="images/guide.gif" width="58" height="45" border="0" class="centerTableDescriptionRowIcon">
                  &nbsp;Please login to access Luntbuild. For questions about authentication please contact your Luntbuild site admin. <br>
&nbsp;</td>
                </tr>

                <tr>
                  <td width="145" align="left" valign="top" class="propertyEditorName2">
                  <img border="0" src="images/name.gif" width="18" height="14">
                  Name</td>
                  <td width="624" align="left" valign="top" class="propertyEditorValue2" >
                  <input type="text" name="j_username" size="30" tabindex="2"><br>
                  Specify your username.</td>
                  </tr>

                <tr>
                  <td align="left" valign="top" class="propertyEditorName1">
                  <img border="0" src="images/password.gif" width="18" height="14">
                  Password</td>
                  <td align="left" valign="top" class="propertyEditorValue1" >
                  <input type="password" name="j_password" size="30" tabindex="2"><br>
                  Specify your password.</td>
                  </tr>

                <tr class="propertyEditorTail2">
                  <td align="left" valign="top" class="propertyEditorTail2">&nbsp;</td>
                  <td align="left" valign="top" class="propertyListName2">
                  <input type="submit" value="Login" name="submit" tabindex="3">
                  &nbsp;&nbsp;
                  <a href="j_acegi_security_check.do?j_username=anonymous&j_password=anonymous">
                  Login as Anonymous</a>
                  </td>
                  </tr>
              </table>
              </td>
            </tr>
          </table>
          </td>
        </tr>
        <tr bgcolor="#CCCCCC">
          <td height="11" class="borderBottom">&nbsp;</td>
        </tr>
        <tr bgcolor="#CCCCCC">
          <td height="10" align="center" valign="top" bgcolor="#EEEEEE">Powered
          by <a href="http://luntbuild.javaforge.com/" title="Goto Luntbuild's Home">Luntbuild</a>
          </td>
        </tr>
      </table>
      </td>
    </tr>
  </table>
</form>

</body>

</html>