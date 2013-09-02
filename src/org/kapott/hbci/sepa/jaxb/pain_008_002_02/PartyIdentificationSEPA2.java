
package org.kapott.hbci.sepa.jaxb.pain_008_002_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyIdentificationSEPA2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyIdentificationSEPA2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nm" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}Max70Text"/>
 *         &lt;element name="PstlAdr" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PostalAddressSEPA" minOccurs="0"/>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:pain.008.002.02}PartySEPAChoice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyIdentificationSEPA2", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", propOrder = {
    "nm",
    "pstlAdr",
    "id"
})
public class PartyIdentificationSEPA2 {

    @XmlElement(name = "Nm", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02", required = true)
    protected String nm;
    @XmlElement(name = "PstlAdr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected PostalAddressSEPA pstlAdr;
    @XmlElement(name = "Id", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.008.002.02")
    protected PartySEPAChoice id;

    /**
     * Gets the value of the nm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNm() {
        return nm;
    }

    /**
     * Sets the value of the nm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNm(String value) {
        this.nm = value;
    }

    /**
     * Gets the value of the pstlAdr property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressSEPA }
     *     
     */
    public PostalAddressSEPA getPstlAdr() {
        return pstlAdr;
    }

    /**
     * Sets the value of the pstlAdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressSEPA }
     *     
     */
    public void setPstlAdr(PostalAddressSEPA value) {
        this.pstlAdr = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link PartySEPAChoice }
     *     
     */
    public PartySEPAChoice getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartySEPAChoice }
     *     
     */
    public void setId(PartySEPAChoice value) {
        this.id = value;
    }

}
