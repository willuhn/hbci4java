
package org.kapott.hbci.sepa.jaxb.pain_002_003_03;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RemittanceInformationSEPA2Choice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemittanceInformationSEPA2Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Ustrd" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}Max140Text"/>
 *           &lt;element name="Strd" type="{urn:iso:std:iso:20022:tech:xsd:pain.002.003.03}StructuredRemittanceInformationSEPA2"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemittanceInformationSEPA2Choice", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03", propOrder = {
    "ustrd",
    "strd"
})
public class RemittanceInformationSEPA2Choice {

    @XmlElement(name = "Ustrd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected String ustrd;
    @XmlElement(name = "Strd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.002.003.03")
    protected StructuredRemittanceInformationSEPA2 strd;

    /**
     * Gets the value of the ustrd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUstrd() {
        return ustrd;
    }

    /**
     * Sets the value of the ustrd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUstrd(String value) {
        this.ustrd = value;
    }

    /**
     * Gets the value of the strd property.
     * 
     * @return
     *     possible object is
     *     {@link StructuredRemittanceInformationSEPA2 }
     *     
     */
    public StructuredRemittanceInformationSEPA2 getStrd() {
        return strd;
    }

    /**
     * Sets the value of the strd property.
     * 
     * @param value
     *     allowed object is
     *     {@link StructuredRemittanceInformationSEPA2 }
     *     
     */
    public void setStrd(StructuredRemittanceInformationSEPA2 value) {
        this.strd = value;
    }

}
