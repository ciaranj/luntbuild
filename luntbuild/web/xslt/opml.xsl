<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no"/>
  <xsl:template match='/opml'>
    <html>
      <head>
        <title>
          <xsl:value-of select='head/title' />
        </title>
      </head>
      <body>
        <p>
          Learn more about web feeds in the <a href="/luntbuild/docs/api/api.html#rss">API documentation</a>.
        </p>
        <p>
          Note:  Build notification feeds will follow their schedule's notify strategy by default.  A different notify strategy
          can be passed in the <a href="/luntbuild/docs/api/api.html#rssnotify">notify</a> parameter:  <a href="/luntbuild/api/rss/builds?notify=failed">http://&lt;hostname&gt;:&lt;serverport&gt;/luntbuild/api/rss/builds?notify=failed</a>
        </p>
        <ul>
          <xsl:apply-templates select='body/outline' />
        </ul>
      </body>
    </html>
  </xsl:template>

  <xsl:template match='outline'>
      <li>
        <a>
          <xsl:attribute name='href'>
            <xsl:value-of select='@htmlUrl' />
          </xsl:attribute>
          <xsl:value-of select='@title' disable-output-escaping='no'  />
        </a>
        <xsl:text xml:space='preserve'>  </xsl:text>
        <xsl:if test="@xmlUrl">
          <a>
            <xsl:attribute name='href'>
              <xsl:value-of select='@xmlUrl' />
            </xsl:attribute>
            <img src='/luntbuild/images/feed.gif' border='0' />
          </a>
        </xsl:if>
      </li>
    <xsl:apply-templates select='outline' />    
  </xsl:template>
</xsl:stylesheet>