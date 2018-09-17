package org.lappsgrid.eager.mining.core

/**
 *
 */
public class Factory
{
    public Factory()
    {

    }

    static XmlParser newXmlParser() {
        XmlParser parser = new XmlParser();
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        return parser;
    }
}
