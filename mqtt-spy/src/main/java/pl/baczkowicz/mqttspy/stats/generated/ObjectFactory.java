//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.26 at 09:00:50 PM BST 
//


package pl.baczkowicz.mqttspy.stats.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the pl.baczkowicz.mqttspy.stats.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MqttSpyStats_QNAME = new QName("http://baczkowicz.pl/mqtt-spy-stats", "MqttSpyStats");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pl.baczkowicz.mqttspy.stats.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MqttSpyStats }
     * 
     */
    public MqttSpyStats createMqttSpyStats() {
        return new MqttSpyStats();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MqttSpyStats }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://baczkowicz.pl/mqtt-spy-stats", name = "MqttSpyStats")
    public JAXBElement<MqttSpyStats> createMqttSpyStats(MqttSpyStats value) {
        return new JAXBElement<MqttSpyStats>(_MqttSpyStats_QNAME, MqttSpyStats.class, null, value);
    }

}
