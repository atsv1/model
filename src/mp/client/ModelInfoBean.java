
package mp.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modelInfoBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modelInfoBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="encoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="formFileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelDescr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelFileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modelInfoBean", propOrder = {
    "modelName",
    "encoding",
    "formFileName",
    "modelDescr",
    "modelFileName"
})
public class ModelInfoBean {

    protected String modelName;
    protected String encoding;
    protected String formFileName;
    protected String modelDescr;
    protected String modelFileName;

    /**
     * Gets the value of the modelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the value of the modelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelName(String value) {
        this.modelName = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the formFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormFileName() {
        return formFileName;
    }

    /**
     * Sets the value of the formFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormFileName(String value) {
        this.formFileName = value;
    }

    /**
     * Gets the value of the modelDescr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelDescr() {
        return modelDescr;
    }

    /**
     * Sets the value of the modelDescr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelDescr(String value) {
        this.modelDescr = value;
    }

    /**
     * Gets the value of the modelFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelFileName() {
        return modelFileName;
    }

    /**
     * Sets the value of the modelFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelFileName(String value) {
        this.modelFileName = value;
    }

}
