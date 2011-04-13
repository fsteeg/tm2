<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Transform an AMAS experiment to a Planets XCDL -->
	<xsl:template match="experiment">
		<xcdl xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		      xsi:schemaLocation="http://www.planets-project.eu/xcl/schemas/xcl 
		      ../schemas/xcdl/XCDLCore.xsd"
			xmlns="http://www.planets-project.eu/xcl/schemas/xcl" id="0">
			<object id="o1">
				<normData type="text" id="nd1">
					<xsl:value-of select="@data" />
				</normData>
				<property id="p1" source="raw" cat="descr">
					<name id="id58">textualAnnotation</name>
					<xsl:apply-templates mode="valueSets" />
				</property>
				<xsl:apply-templates mode="propertySets" />
			</object>
		</xcdl>
	</xsl:template>

	<!-- Transform an AMAS annotation label to an XCDL value set -->
	<xsl:template match="a" mode="valueSets">
		<valueSet id="i_i1_i36_i1_i{position()}">
			<labValue>
				<val>
					<xsl:value-of select="@label" />
				</val>
				<type>string</type>
			</labValue>
			<dataRef ind="normSpecific" propertySetId="id_{position()}" />
		</valueSet>
	</xsl:template>

	<!-- Transform AMAS annotation positional information to an XCDL property set -->
	<xsl:template match="a" mode="propertySets">
		<propertySet id="id_{position()}">
			<valueSetRelations>
				<ref valueSetId="i_i1_i36_i1_i{position()}" name="textualAnnotation" />
			</valueSetRelations>
			<dataRef>
				<ref begin="{@start}" end="{@end}" id="nd1" />
			</dataRef>
		</propertySet>
	</xsl:template>
	
</xsl:stylesheet>