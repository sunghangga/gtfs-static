package com.maestronic.gtfs.util;

import com.maestronic.gtfs.dto.siri.Gtfs;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.bind.v2.WellKnownNamespace;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;

public class GtfsXml {

    public static String objectToStrXml(Gtfs gtfs) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Gtfs.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
                @Override
                public String[] getPreDeclaredNamespaceUris() {
                    return new String[] {
                            XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI
                    };
                }

                @Override
                public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                    if (namespaceUri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI))
                        return "xsi";
                    if (namespaceUri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI))
                        return "xs";
                    if (namespaceUri.equals(WellKnownNamespace.XML_MIME_URI))
                        return "xmime";
                    return suggestion;
                }
            });

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(gtfs, byteArrayOutputStream);

            return byteArrayOutputStream.toString();
        } catch (JAXBException e) {

            e.printStackTrace();
            return null;
        }
    }
}
