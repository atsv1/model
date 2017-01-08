
package mp.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modelAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modelAddress">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FModelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FBlockName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FBlockIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FParamName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modelAddress", propOrder = {
    "fModelName",
    "fBlockName",
    "fBlockIndex",
    "fParamName"
})
public class ModelAddress {

    @XmlElement(name = "FModelName")
    protected String fModelName;
    @XmlElement(name = "FBlockName")
    protected String fBlockName;
    @XmlElement(name = "FBlockIndex")
    protected int fBlockIndex;
    @XmlElement(name = "FParamName")
    protected String fParamName;

    /**
     * Gets the value of the fModelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFModelName() {
        return fModelName;
    }

    /**
     * Sets the value of the fModelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFModelName(String value) {
        this.fModelName = value;
    }

    /**
     * Gets the value of the fBlockName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFBlockName() {
        return fBlockName;
    }

    /**
     * Sets the value of the fBlockName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFBlockName(String value) {
        this.fBlockName = value;
    }

    /**
     * Gets the value of the fBlockIndex property.
     * 
     */
    public int getFBlockIndex() {
        return fBlockIndex;
    }

    /**
     * Sets the value of the fBlockIndex property.
     * 
     */
    public void setFBlockIndex(int value) {
        this.fBlockIndex = value;
    }

    /**
     * Gets the value of the fParamName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFParamName() {
        return fParamName;
    }

    /**
     * Sets the value of the fParamName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFParamName(String value) {
        this.fParamName = value;
    }

}
