<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="yes" indent="yes" />
	<xsl:strip-space elements="*" />
	<xsl:param name="channel"/>
	<!--
		<channel id="GO"> <display-name lang="en">GO!</display-name>
		<base-url>http://www.oztivo.net/xmltv/</base-url>
		<base-url>http://xml.oztivo.net/xmltv/</base-url> </channel>
	-->
	<xsl:template match="channel">
		<xsl:if test="@id = $channel">
			<channel id="{@id}">
				<xsl:apply-templates select="display-name" />
			</channel>
		</xsl:if>
	</xsl:template>

	<xsl:template match="display-name">
		<display-name lang="{@lang}">
			<xsl:apply-templates select="child::node()" />
		</display-name>
	</xsl:template>
</xsl:stylesheet>