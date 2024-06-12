<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
        <Name>country</Name>
        <UserStyle>
            <Title>Highlighted Countries</Title>
            <FeatureTypeStyle>
                <Rule>
                    <ogc:Filter>
                        <ogc:Or>
                            ${highlightedCountries}
                        </ogc:Or>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">#0000FF</CssParameter> <!-- Blue color for highlighted countries -->
                        </Fill>
                        <Stroke>
                            <CssParameter name="stroke">#FFFFFF</CssParameter>
                            <CssParameter name="stroke-width">1</CssParameter>
                        </Stroke>
                    </PolygonSymbolizer>
                </Rule>
                <Rule>
                    <ElseFilter/>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">#808080</CssParameter> <!-- Gray color for non-highlighted countries -->
                        </Fill>
                        <Stroke>
                            <CssParameter name="stroke">#FFFFFF</CssParameter>
                            <CssParameter name="stroke-width">1</CssParameter>
                        </Stroke>
                    </PolygonSymbolizer>
                </Rule>
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>
