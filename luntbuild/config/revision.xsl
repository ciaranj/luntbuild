<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<!-- This XSL is transformation of Luntbuild xml revisions format to Html -->
<xsl:decimal-format decimal-separator="." grouping-separator="," />

<xsl:template match="/">
<html>
  <head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <style type="text/css">
    .bannercell {
      border: 0px;
      padding: 0px;
    }
    body {
      margin: 0;
      font:normal 100% arial,helvetica,sanserif;
      background-color:#FFFFFF;
      color:#000000;
    }
    table.status {
      font:bold 80% arial,helvetica,sanserif;
      background-color:#525D76;
      color:#ffffff;
    }
    table.log tr td, tr th {
      font-size: 80%;
    }
    .error {
      color:red;
    }
    .warn {
      color:brown;
    }
    .info {
      color:gray;
    }
    .debug{
      color:gray;
    }
    .failed {
      font-size:80%;
      background-color: red;
      color:#FFFFFF;
      font-weight: bold
    }
    .complete {
      font-size:80%;
      background-color: #525D76;
      color:#FFFFFF;
      font-weight: bold
    }
    .a td {
      background: #efefef;
    }
    .b td {
      background: #fff;
    }
    th, td {
      text-align: left;
      vertical-align: top;
    }
    th {
      background: #ccc;
      color: black;
    }
    table, th, td {
      border: none
    }
    h3 {
      font:bold 80% arial,helvetica,sanserif;
      background: #525D76;
      color: white;
      text-decoration: none;
      padding: 5px;
      margin-right: 2px;
      margin-left: 2px;
      margin-bottom: 0;
    }
    </style>
  </head>
  <body>

    <table border="0" width="100%">
    <tr><td><hr noshade="yes" size="1"/></td></tr>
    </table>

    <xsl:apply-templates select="revisions"/>

    <table width="100%">
      <tr><td><hr noshade="yes" size="1"/></td></tr>
     </table>
  </body>
</html>
</xsl:template>

<xsl:template match="revisions">
  <!-- Revisions information -->
  <h3>Revisions</h3>
  <table class="log" border="1" cellspacing="2" cellpadding="3" width="100%">
  <tr>
    <th nowrap="yes" align="left">Change Log</th>
  </tr>
  <xsl:apply-templates select=".//changelog"/>
  </table>
</xsl:template>

<!-- report every message -->
<xsl:template match="changelog">
  <tr valign="top">
    <!-- alternated row style -->
    <xsl:attribute name="class">
      <xsl:if test="position() mod 2 = 1">a</xsl:if>
      <xsl:if test="position() mod 2 = 0">b</xsl:if>
    </xsl:attribute>
    <td nowrap="no">
            <xsl:value-of select="text()" disable-output-escaping="yes"/>
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
