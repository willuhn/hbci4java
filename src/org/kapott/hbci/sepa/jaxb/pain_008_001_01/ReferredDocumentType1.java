
package org.kapott.hbci.sepa.jaxb.pain_008_001_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferredDocumentType1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferredDocumentType1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="Cd" type="{urn:sepade:xsd:pain.008.001.01}DocumentType2Code"/>
 *           &lt;element name="Prtry" type="{urn:sepade:xsd:pain.008.001.01}Max35Text"/>
 *         &lt;/choice>
 *         &lt;element name="Issr" type="{urn:sepade:xsd:pain.008.001.01}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferredDocumentType1", namespace = "urn:sepade:xsd:pain.008.001.01", propOrder = {
    "cd",
    "prtry",
    "issr"
})
public class ReferredDocumentType1 {

    @XmlElement(name = "Cd", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected DocumentType2Code cd;
    @XmlElement(name = "Prtry", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String prtry;
    @XmlElement(name = "Issr", namespace = "urn:sepade:xsd:pain.008.001.01")
    protected String issr;

    /**
     * Gets the value of the cd property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentType2Code }
     *     
     */
    public DocumentType2Code getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentType2Code }
     *     
     */
    public void setCd(DocumentType2Code value) {
        this.cd = value;
    }

    /**
     * Gets the value of the prtry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrtry() {
        return prtry;
    }

    /**
     * Sets the value of the prtry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrtry(String value) {
        this.prtry = value;
    }

    /**
     * Gets the value of the issr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssr() {
        return issr;
    }

    /**
     * Sets the value of the issr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssr(String value) {
        this.issr = value;
    }

}
