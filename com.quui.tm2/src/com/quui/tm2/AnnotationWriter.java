package com.quui.tm2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Fabian Steeg (fsteeg)
 */
public class AnnotationWriter {
    /***/
    static final String XML = ".xml";
    /***/
    private String corpusLocation;
    /***/
    private Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard;

    /**
     * @param blackboard The blackboard of annotations to write
     * @param corpusLocation The location of the data that the annotations
     *            annotate
     */
    public AnnotationWriter(
            final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard,
            final String corpusLocation) {
        this.blackboard = blackboard;
        this.corpusLocation = corpusLocation;
    }

    /**
     * Writes the annotations produced in this experiment to XML. Uses STAX for
     * efficient writing, as the annotations will become rather large for any
     * realistic scenario, and the format is quite simple.
     * @param outputLocation The location to write the annotations to
     */
    public void writeAnnotations(final String outputLocation) {
        if (blackboard == null) {
            throw new IllegalStateException("No results yet!");
        }
        OutputStream outx = null;
        try {
            URI uri = new URL(outputLocation + XML).toURI();
            outx = new FileOutputStream(new File(uri));
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(outx);
            // writer.setDefaultNamespace("http://www.quui.com/tm2");
            // writer.setPrefix("tm2", "http://www.quui.com/tm2");
            // TODO do this optional, prefs ("pretty-print")
            writer = new IndentingXMLStreamWriter(writer);
            /* ---------------------- */
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("experiment");
            writer.writeAttribute("data", corpusLocation);
            for (Class<? extends Agent<?, ?>> agentClass : blackboard.keySet()) {
                writeForAgent(writer, agentClass);
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            /* ---------------------- */
            writer.flush();
            writer.close();
            outx.flush();
            outx.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outx != null) {
                    outx.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeForAgent(final XMLStreamWriter writer,
            final Class<? extends Agent<?, ?>> agentClass) throws XMLStreamException {
        String agent = agentClass.getName();
        writer.writeStartElement("agent");
        writer.writeAttribute("name", agent);
        List<Annotation<?>> list = blackboard.get(agentClass);
        for (Annotation<?> anno : list) {
            writer.writeStartElement("a");
            writer.writeAttribute("start", anno.getStart().toString());
            writer.writeAttribute("end", anno.getEnd().toString());
            final Object value = anno.getValue();
            /*
             * We store both the human-radable toString representation
             * as well as the serialized form:
             */
            writer.writeAttribute("label", value.toString());
            writer.writeAttribute("object", encode(value));
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    /**
     * @param object The object to encode as a Base64 string
     * @return A Base64 encoded string representing the serialized form of the
     *         given object
     */
    private String encode(final Object object) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(
                    arrayOutputStream);
            outStream.writeObject(object);
            outStream.flush();
            outStream.close();
            arrayOutputStream.flush();
            arrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encoded = new String(Base64.encodeBase64(arrayOutputStream
                .toByteArray()));
        return encoded;
    }
}
