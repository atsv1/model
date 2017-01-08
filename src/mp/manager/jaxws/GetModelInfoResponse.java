
package mp.manager.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getModelInfoResponse", namespace = "http://manager.mp/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getModelInfoResponse", namespace = "http://manager.mp/")
public class GetModelInfoResponse {

    @XmlElement(name = "return", namespace = "")
    private mp.manager.ModelInfoBean _return;

    /**
     * 
     * @return
     *     returns ModelInfoBean
     */
    public mp.manager.ModelInfoBean getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(mp.manager.ModelInfoBean _return) {
        this._return = _return;
    }

}
