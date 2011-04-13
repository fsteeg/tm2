package com.quui.tm2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Fabian Steeg (fsteeg)
 */
public/* yes? */class AnnotationReader {
    /***/
    private XMLStreamReader reader;
    /***/
    private String location;
    /***/
    private String data;
    private boolean validating;

    /**
     * @param location The location of the XML file containing the annotations
     *            to read
     */
    public AnnotationReader(final String location) {
        this.location = location;
        this.validating = true;
        init();
    }

    /**
   * 
   */
    private void init() {
        try {
            File f = new File(new URL(this.location).toURI());
            if (!f.exists()) {
                throw new IllegalStateException("File does not exist: "
                        + this.location);
            }
            this.reader = createReader(this.location, validating);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public final List<Annotation<?>> readRawAnnotations(
    // final Class<? extends Agent<?, ?>> agentClass) {
    // return readAnnotations(agentClass);
    // }
    /**
     * @param <T> The value type of the annotations to read
     * @param agentClass The class of the agent whose annotation to read
     * @return Returns the annotations that were written by the specified agent
     */
    public final <T extends Comparable<T> & Serializable> List<Annotation<T>> readAnnotations(
            final Class<? extends Agent<?, T>> agentClass) {
        init();
        List<Annotation<T>> result = new ArrayList<Annotation<T>>();
        try {
            boolean read = false;
            while (reader.hasNext()) {
                if (reader.isStartElement()) {
                    if (reader.getLocalName().equals("agent")
                            && agentClass == null
                            || reader.getAttributeValue(0).equals(
                                    agentClass.getName())) {
                        /* We wanna read the annotations of this agent: */
                        read = true;
                    }
                }
                if (reader.isStartElement()) {
                    /* And this is where we do it: */
                    if (reader.getLocalName().equals("a") && read) {

                        // old approach, when passing the class of T
                        // Class<T> tClass = new Class<T>();
                        /*
                         * What we'd want is something like: Class<T> tClass =
                         * T.class; to use further down when creating the
                         * annotation. But as generic types are not reified in
                         * Java, this is not possible. Instead... we find the
                         * generic interface implemented by our agent (as the
                         * types of subclasses or interface implementations of
                         * generic classes or interfaces are reifiable):
                         */
                        // Type[] genericInterfaces =
                        // agentClass.getGenericInterfaces();
                        @SuppressWarnings("unchecked")
						Class<T> annotationClass = getAgentClass(agentClass);
                        /*
                         * This class object is then used to instantiate a
                         * type-safe annotation object from the XML file
                         */
                        /*
                         * To instantiate an annotation object from the XML, the
                         * value of an annotation needs to have a constructor
                         * with a single String parameter (as common Java
                         * classes like String, Integer and Double have)
                         */

                        String attributeValue = reader.getAttributeValue("",
                                "object");
                        T value = null;
                        /*
                         * If we have a serialized form, we instantiate from
                         * that:
                         */
                        if (attributeValue != null) {
                            /*
                             * Using decode(...) non-qualified/generic here does
                             * not work; it does compile fine in Eclipse, but
                             * not with current Sun Java; cf.
                             */
                            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=98379
                            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
                            value = this.<T> decode(attributeValue);
                        } else {
                            /*
                             * Else, we try to instantiate from the string
                             * representation, using the string constructor
                             * (this works by default for the data types
                             * included in Java, like String, Integer, etc.)
                             */
                            attributeValue = reader.getAttributeValue("", //$NON-NLS-1$
                                    "label");
                            Constructor<T> constructor = annotationClass
                                    .getConstructor(String.class);
                            if (constructor == null) {
                                /*
                                 * TODO Is there any way to enforce this at
                                 * compile time? We can't demand an interface,
                                 * as we want library classes as String, Double
                                 * etc. to be supported...
                                 */
                                throw new IllegalArgumentException(
                                        "Can't instantiate annotation value of type "
                                                + annotationClass
                                                + ": does not have a constructor taking a single string");
                            }
                            value = constructor.newInstance(attributeValue);
                        }
                        Annotation<T> annotation = null;
                        try {
                            annotation = ImmutableAnnotation
                                    .getInstance(agentClass, value,
                                            new BigInteger(reader
                                                    .getAttributeValue(0)),
                                            new BigInteger(reader
                                                    .getAttributeValue(1)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        result.add(annotation);
                    }
                }
                if (reader.isStartElement()
                        && reader.getLocalName().equals("agent")
                        && !reader.getAttributeValue(0).equals(
                                agentClass.getName()) && read) {
                    /* We only read annotation for one agent in one go: */
                    return result;
                }
                reader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

	static <T extends Comparable<T> & Serializable> Class<T> getAgentClass(final Class<? extends Agent<?, T>> agentClass) {
		/*
		 * TODO we need to search for the interface in the type
		 * hierarchy... or supply a method that does let you
		 * specify the classes...
		 */

		Type agentInterface = findAgentInterface(agentClass);
		if(agentInterface==null){
			 Exception e = new IllegalArgumentException(
             "Only direct implementations of the Agent interface are supported!");
			 e.printStackTrace();
			 return null;
		}

		/*
		 * If we are a skeletal implementation and no direct
		 * implementor, get the superclasses interface TODO this
		 * is not very robust... what if the implementor does
		 * implement some other interface? We should check if it
		 * is the agent interface... and it is not even working
		 * like this...
		 */

		/* We have only one interface: */
		ParameterizedType t = (ParameterizedType) agentInterface;
		/* The second parameter is the agent's output: */
		Type output = t.getActualTypeArguments()[1];
		/* ...and that must be a class of T: */
		/*
		 * This one unsafe cast is the price we pay for cutting
		 * the redundancy of supplying the class as a parameter;
		 * but this should not ever go wrong, as we get it from
		 * the original class... T is the second parameter of
		 * the given agent class, then we get the second actual
		 * parameter above, so now we can cast it to a class of
		 * T:
		 */
		@SuppressWarnings("unchecked") Class<T> annotationClass = (Class<T>) output;
		return annotationClass;
	}

    /**
     * @param <T> The type of the object to be desialized
     * @param string The serialized, Base64 encoded form of the object
     * @return Retruns an instance of type T created from the serialized form
     */
    private <T> T decode(final String string) {
        if (string == null) {
            throw new NullPointerException("Can't decode a null string;");
        }
        try {
            byte[] decodeBuffer = Base64.decodeBase64(string.getBytes());
            ObjectInputStream inStream = new ObjectInputStream(
                    new BufferedInputStream(new ByteArrayInputStream(
                            decodeBuffer)));
            /*
             * We only read annotations produced by the agent producing type T,
             * so at this point the serialized form must be of type T
             */
            @SuppressWarnings("unchecked") T value = (T) inStream.readObject();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not deserialize value object");
    }

    /**
     * @param <T> The type created by the agent
     * @param agentClass The concrete agent class
     * @return Returns the type created by the given agent
     */
    public static <T extends Comparable<T> & Serializable> Type findAgentInterface(
            final Class<? extends Agent<?, T>> agentClass) {
        Type[] genericInterfaces = agentClass.getGenericInterfaces();
        if (genericInterfaces.length < 1) {
        	return null;
        }
        for (Type type : genericInterfaces) {
          if(type.toString().contains(Agent.class.getName()))
            return type;
        }
        // FIXME does not work for agents that implement Agent indirectly, impl rec. search?
        throw new IllegalStateException("No agent interface found for: " + agentClass);
    }

    /**
     * @return Returns a list of all agents that wrote Annotations
     */
    public final List<String> readAgents() {
        init();
        List<String> result = new ArrayList<String>();
        try {
            while (this.reader.hasNext()) {
                if (reader.isStartElement()) {
                    if (reader.getLocalName().equals("agent")) {
                        result.add(reader.getAttributeValue(0));
                    }
                }
                reader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * @param fileLocation The location of the annotations file
     * @param validating
     * @return Returns the reader for the location
     * @throws XMLStreamException while reading
     * @throws URISyntaxException
     * @throws IOException
     */
    private XMLStreamReader createReader(final String fileLocation,
            boolean validating) throws XMLStreamException, URISyntaxException,
            IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
                new File(new URL(fileLocation).toURI())));
        XMLInputFactory factory = XMLInputFactory.newInstance();

        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                Boolean.FALSE);

        SchemaFactory schemaFactory = SchemaFactory
                .newInstance("http://www.w3.org/2001/XMLSchema");

        Source source = new StreamSource(new URL(fileLocation).openStream());

        XMLStreamReader newReader;
        System.setProperty("javax.xml.stream.isCoalescing", "true");
        newReader = factory.createXMLStreamReader(source);

        if (validating) {
            try {
                URL resource = this.getClass().getResource("amas.xsd");
                if (resource == null) {
                    throw new IllegalStateException("Could not load XSD");
                }
                // File f = null;
                // try {
                // f = new File(resource.toURI());
                // } catch (URISyntaxException e) {
                // f = new File(resource.getPath());
                // }

                // String loc = resource.getPath();
                Schema schemaGrammar = schemaFactory.newSchema(resource);

                Validator schemaValidator = schemaGrammar.newValidator();
                schemaValidator.setErrorHandler(new ErrorHandler() {

                    public void warning(SAXParseException exception)
                            throws SAXException {
                    // TODO Auto-generated method stub

                    }

                    public void fatalError(SAXParseException exception)
                            throws SAXException {
                    // TODO Auto-generated method stub

                    }

                    public void error(SAXParseException exception)
                            throws SAXException {
                    // TODO Auto-generated method stub

                    }
                });
                schemaValidator.validate(new StreamSource(bufferedReader));
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        while (newReader.hasNext()) {
            if (newReader.isStartElement()) {
                if (newReader.getLocalName().equals("experiment")) {
                    this.data = newReader.getAttributeValue(0);
                    return newReader;
                }
            }
            newReader.next();
        }
        return newReader;
    }

    // private String string(BufferedReader bufferedReader) {
    // StringBuilder builder = new StringBuilder();
    // Scanner s = new Scanner(bufferedReader);
    // while (s.hasNextLine()) {
    // builder.append(s.nextLine());
    // }
    // return builder.toString();
    // }

    /**
     * @return Returns the data, the actual XML string of annotations
     */
    public final String getData() {
        // TODO We should find a different solution for this
        return data;
    }

}
