
package mp.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mp.client package. 
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

    private final static QName _GetBlockCount_QNAME = new QName("http://manager.mp/", "GetBlockCount");
    private final static QName _PauseModel_QNAME = new QName("http://manager.mp/", "PauseModel");
    private final static QName _SendDoubleValueResponse_QNAME = new QName("http://manager.mp/", "SendDoubleValueResponse");
    private final static QName _IsHistoryExistsResponse_QNAME = new QName("http://manager.mp/", "IsHistoryExistsResponse");
    private final static QName _SendDoubleValue_QNAME = new QName("http://manager.mp/", "SendDoubleValue");
    private final static QName _SendBooleanValueResponse_QNAME = new QName("http://manager.mp/", "SendBooleanValueResponse");
    private final static QName _IsManagingEnabledResponse_QNAME = new QName("http://manager.mp/", "IsManagingEnabledResponse");
    private final static QName _GetModelInfoResponse_QNAME = new QName("http://manager.mp/", "getModelInfoResponse");
    private final static QName _Compare_QNAME = new QName("http://manager.mp/", "Compare");
    private final static QName _IsManagingEnabled_QNAME = new QName("http://manager.mp/", "IsManagingEnabled");
    private final static QName _GetFormDescrAsByteArray_QNAME = new QName("http://manager.mp/", "GetFormDescrAsByteArray");
    private final static QName _GetHistoryStringValue_QNAME = new QName("http://manager.mp/", "GetHistoryStringValue");
    private final static QName _StartModelResponse_QNAME = new QName("http://manager.mp/", "StartModelResponse");
    private final static QName _GetArrayValue_QNAME = new QName("http://manager.mp/", "GetArrayValue");
    private final static QName _IsConnectionEnabled_QNAME = new QName("http://manager.mp/", "IsConnectionEnabled");
    private final static QName _StopModelResponse_QNAME = new QName("http://manager.mp/", "StopModelResponse");
    private final static QName _GetHistoryStringValueResponse_QNAME = new QName("http://manager.mp/", "GetHistoryStringValueResponse");
    private final static QName _GetModelInfo_QNAME = new QName("http://manager.mp/", "getModelInfo");
    private final static QName _GetAvailModelCount_QNAME = new QName("http://manager.mp/", "getAvailModelCount");
    private final static QName _GetBooleanValueResponse_QNAME = new QName("http://manager.mp/", "GetBooleanValueResponse");
    private final static QName _GetStringValueByAddress_QNAME = new QName("http://manager.mp/", "GetStringValueByAddress");
    private final static QName _GetBlockIndexResponse_QNAME = new QName("http://manager.mp/", "GetBlockIndexResponse");
    private final static QName _IsArray_QNAME = new QName("http://manager.mp/", "IsArray");
    private final static QName _StartModelByGuid_QNAME = new QName("http://manager.mp/", "StartModelByGuid");
    private final static QName _GetBlockIndex_QNAME = new QName("http://manager.mp/", "GetBlockIndex");
    private final static QName _StartModel_QNAME = new QName("http://manager.mp/", "StartModel");
    private final static QName _SendBooleanValue_QNAME = new QName("http://manager.mp/", "SendBooleanValue");
    private final static QName _GetValue_QNAME = new QName("http://manager.mp/", "GetValue");
    private final static QName _IsConnectionEnabledResponse_QNAME = new QName("http://manager.mp/", "IsConnectionEnabledResponse");
    private final static QName _GetIntValueResponse_QNAME = new QName("http://manager.mp/", "GetIntValueResponse");
    private final static QName _ResumeModel_QNAME = new QName("http://manager.mp/", "ResumeModel");
    private final static QName _ModelException_QNAME = new QName("http://manager.mp/", "ModelException");
    private final static QName _StartModelByGuidResponse_QNAME = new QName("http://manager.mp/", "StartModelByGuidResponse");
    private final static QName _GetFormDescr_QNAME = new QName("http://manager.mp/", "GetFormDescr");
    private final static QName _CreateModel_QNAME = new QName("http://manager.mp/", "CreateModel");
    private final static QName _GetValueResponse_QNAME = new QName("http://manager.mp/", "GetValueResponse");
    private final static QName _GetBlockCountResponse_QNAME = new QName("http://manager.mp/", "GetBlockCountResponse");
    private final static QName _GetArrayValueResponse_QNAME = new QName("http://manager.mp/", "GetArrayValueResponse");
    private final static QName _FireBlockEvent_QNAME = new QName("http://manager.mp/", "FireBlockEvent");
    private final static QName _ResumeModelResponse_QNAME = new QName("http://manager.mp/", "ResumeModelResponse");
    private final static QName _CompareResponse_QNAME = new QName("http://manager.mp/", "CompareResponse");
    private final static QName _IsHistoryExists_QNAME = new QName("http://manager.mp/", "IsHistoryExists");
    private final static QName _GetFormDescrResponse_QNAME = new QName("http://manager.mp/", "GetFormDescrResponse");
    private final static QName _GetValueType_QNAME = new QName("http://manager.mp/", "GetValueType");
    private final static QName _GetStringValueByAddressResponse_QNAME = new QName("http://manager.mp/", "GetStringValueByAddressResponse");
    private final static QName _GetArrayDimensionLength_QNAME = new QName("http://manager.mp/", "GetArrayDimensionLength");
    private final static QName _GetArrayDimensionCountResponse_QNAME = new QName("http://manager.mp/", "GetArrayDimensionCountResponse");
    private final static QName _GetArrayDimensionLengthResponse_QNAME = new QName("http://manager.mp/", "GetArrayDimensionLengthResponse");
    private final static QName _GetStringValueResponse_QNAME = new QName("http://manager.mp/", "GetStringValueResponse");
    private final static QName _GetErrorString_QNAME = new QName("http://manager.mp/", "GetErrorString");
    private final static QName _GetAvailModelCountResponse_QNAME = new QName("http://manager.mp/", "getAvailModelCountResponse");
    private final static QName _CreateModelResponse_QNAME = new QName("http://manager.mp/", "CreateModelResponse");
    private final static QName _GetFormDescrAsByteArrayResponse_QNAME = new QName("http://manager.mp/", "GetFormDescrAsByteArrayResponse");
    private final static QName _StopModel_QNAME = new QName("http://manager.mp/", "StopModel");
    private final static QName _GetIntValue_QNAME = new QName("http://manager.mp/", "GetIntValue");
    private final static QName _GetValueByAddress_QNAME = new QName("http://manager.mp/", "GetValueByAddress");
    private final static QName _GetBooleanValue_QNAME = new QName("http://manager.mp/", "GetBooleanValue");
    private final static QName _IsArrayResponse_QNAME = new QName("http://manager.mp/", "IsArrayResponse");
    private final static QName _FireBlockEventResponse_QNAME = new QName("http://manager.mp/", "FireBlockEventResponse");
    private final static QName _GetArrayDimensionCount_QNAME = new QName("http://manager.mp/", "GetArrayDimensionCount");
    private final static QName _GetErrorStringResponse_QNAME = new QName("http://manager.mp/", "GetErrorStringResponse");
    private final static QName _GetValueByAddressResponse_QNAME = new QName("http://manager.mp/", "GetValueByAddressResponse");
    private final static QName _GetStringValue_QNAME = new QName("http://manager.mp/", "GetStringValue");
    private final static QName _GetValueTypeResponse_QNAME = new QName("http://manager.mp/", "GetValueTypeResponse");
    private final static QName _PauseModelResponse_QNAME = new QName("http://manager.mp/", "PauseModelResponse");
    private final static QName _GetFormDescrAsByteArrayResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mp.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendDoubleValueResponse }
     * 
     */
    public SendDoubleValueResponse createSendDoubleValueResponse() {
        return new SendDoubleValueResponse();
    }

    /**
     * Create an instance of {@link GetModelInfo }
     * 
     */
    public GetModelInfo createGetModelInfo() {
        return new GetModelInfo();
    }

    /**
     * Create an instance of {@link GetValueTypeResponse }
     * 
     */
    public GetValueTypeResponse createGetValueTypeResponse() {
        return new GetValueTypeResponse();
    }

    /**
     * Create an instance of {@link GetArrayValueResponse }
     * 
     */
    public GetArrayValueResponse createGetArrayValueResponse() {
        return new GetArrayValueResponse();
    }

    /**
     * Create an instance of {@link GetIntValueResponse }
     * 
     */
    public GetIntValueResponse createGetIntValueResponse() {
        return new GetIntValueResponse();
    }

    /**
     * Create an instance of {@link GetIntValue }
     * 
     */
    public GetIntValue createGetIntValue() {
        return new GetIntValue();
    }

    /**
     * Create an instance of {@link SendDoubleValue }
     * 
     */
    public SendDoubleValue createSendDoubleValue() {
        return new SendDoubleValue();
    }

    /**
     * Create an instance of {@link GetAvailModelCountResponse }
     * 
     */
    public GetAvailModelCountResponse createGetAvailModelCountResponse() {
        return new GetAvailModelCountResponse();
    }

    /**
     * Create an instance of {@link CreateModelResponse }
     * 
     */
    public CreateModelResponse createCreateModelResponse() {
        return new CreateModelResponse();
    }

    /**
     * Create an instance of {@link GetValueResponse }
     * 
     */
    public GetValueResponse createGetValueResponse() {
        return new GetValueResponse();
    }

    /**
     * Create an instance of {@link FireBlockEvent }
     * 
     */
    public FireBlockEvent createFireBlockEvent() {
        return new FireBlockEvent();
    }

    /**
     * Create an instance of {@link IsHistoryExists }
     * 
     */
    public IsHistoryExists createIsHistoryExists() {
        return new IsHistoryExists();
    }

    /**
     * Create an instance of {@link GetStringValueResponse }
     * 
     */
    public GetStringValueResponse createGetStringValueResponse() {
        return new GetStringValueResponse();
    }

    /**
     * Create an instance of {@link IsManagingEnabledResponse }
     * 
     */
    public IsManagingEnabledResponse createIsManagingEnabledResponse() {
        return new IsManagingEnabledResponse();
    }

    /**
     * Create an instance of {@link StartModelByGuidResponse }
     * 
     */
    public StartModelByGuidResponse createStartModelByGuidResponse() {
        return new StartModelByGuidResponse();
    }

    /**
     * Create an instance of {@link GetValue }
     * 
     */
    public GetValue createGetValue() {
        return new GetValue();
    }

    /**
     * Create an instance of {@link ModelException }
     * 
     */
    public ModelException createModelException() {
        return new ModelException();
    }

    /**
     * Create an instance of {@link GetFormDescrAsByteArrayResponse }
     * 
     */
    public GetFormDescrAsByteArrayResponse createGetFormDescrAsByteArrayResponse() {
        return new GetFormDescrAsByteArrayResponse();
    }

    /**
     * Create an instance of {@link GetFormDescr }
     * 
     */
    public GetFormDescr createGetFormDescr() {
        return new GetFormDescr();
    }

    /**
     * Create an instance of {@link PauseModel }
     * 
     */
    public PauseModel createPauseModel() {
        return new PauseModel();
    }

    /**
     * Create an instance of {@link GetBlockCountResponse }
     * 
     */
    public GetBlockCountResponse createGetBlockCountResponse() {
        return new GetBlockCountResponse();
    }

    /**
     * Create an instance of {@link SendBooleanValue }
     * 
     */
    public SendBooleanValue createSendBooleanValue() {
        return new SendBooleanValue();
    }

    /**
     * Create an instance of {@link GetStringValue }
     * 
     */
    public GetStringValue createGetStringValue() {
        return new GetStringValue();
    }

    /**
     * Create an instance of {@link GetArrayValue }
     * 
     */
    public GetArrayValue createGetArrayValue() {
        return new GetArrayValue();
    }

    /**
     * Create an instance of {@link StopModelResponse }
     * 
     */
    public StopModelResponse createStopModelResponse() {
        return new StopModelResponse();
    }

    /**
     * Create an instance of {@link CompareResponse }
     * 
     */
    public CompareResponse createCompareResponse() {
        return new CompareResponse();
    }

    /**
     * Create an instance of {@link GetArrayDimensionCount }
     * 
     */
    public GetArrayDimensionCount createGetArrayDimensionCount() {
        return new GetArrayDimensionCount();
    }

    /**
     * Create an instance of {@link GetFormDescrAsByteArray }
     * 
     */
    public GetFormDescrAsByteArray createGetFormDescrAsByteArray() {
        return new GetFormDescrAsByteArray();
    }

    /**
     * Create an instance of {@link Compare }
     * 
     */
    public Compare createCompare() {
        return new Compare();
    }

    /**
     * Create an instance of {@link GetBlockIndex }
     * 
     */
    public GetBlockIndex createGetBlockIndex() {
        return new GetBlockIndex();
    }

    /**
     * Create an instance of {@link GetErrorString }
     * 
     */
    public GetErrorString createGetErrorString() {
        return new GetErrorString();
    }

    /**
     * Create an instance of {@link ModelAddress }
     * 
     */
    public ModelAddress createModelAddress() {
        return new ModelAddress();
    }

    /**
     * Create an instance of {@link GetBlockIndexResponse }
     * 
     */
    public GetBlockIndexResponse createGetBlockIndexResponse() {
        return new GetBlockIndexResponse();
    }

    /**
     * Create an instance of {@link GetBooleanValue }
     * 
     */
    public GetBooleanValue createGetBooleanValue() {
        return new GetBooleanValue();
    }

    /**
     * Create an instance of {@link GetHistoryStringValueResponse }
     * 
     */
    public GetHistoryStringValueResponse createGetHistoryStringValueResponse() {
        return new GetHistoryStringValueResponse();
    }

    /**
     * Create an instance of {@link FireBlockEventResponse }
     * 
     */
    public FireBlockEventResponse createFireBlockEventResponse() {
        return new FireBlockEventResponse();
    }

    /**
     * Create an instance of {@link GetFormDescrResponse }
     * 
     */
    public GetFormDescrResponse createGetFormDescrResponse() {
        return new GetFormDescrResponse();
    }

    /**
     * Create an instance of {@link ResumeModel }
     * 
     */
    public ResumeModel createResumeModel() {
        return new ResumeModel();
    }

    /**
     * Create an instance of {@link GetValueType }
     * 
     */
    public GetValueType createGetValueType() {
        return new GetValueType();
    }

    /**
     * Create an instance of {@link StartModelByGuid }
     * 
     */
    public StartModelByGuid createStartModelByGuid() {
        return new StartModelByGuid();
    }

    /**
     * Create an instance of {@link GetArrayDimensionLength }
     * 
     */
    public GetArrayDimensionLength createGetArrayDimensionLength() {
        return new GetArrayDimensionLength();
    }

    /**
     * Create an instance of {@link GetArrayDimensionLengthResponse }
     * 
     */
    public GetArrayDimensionLengthResponse createGetArrayDimensionLengthResponse() {
        return new GetArrayDimensionLengthResponse();
    }

    /**
     * Create an instance of {@link CreateModel }
     * 
     */
    public CreateModel createCreateModel() {
        return new CreateModel();
    }

    /**
     * Create an instance of {@link IsHistoryExistsResponse }
     * 
     */
    public IsHistoryExistsResponse createIsHistoryExistsResponse() {
        return new IsHistoryExistsResponse();
    }

    /**
     * Create an instance of {@link GetErrorStringResponse }
     * 
     */
    public GetErrorStringResponse createGetErrorStringResponse() {
        return new GetErrorStringResponse();
    }

    /**
     * Create an instance of {@link ModelInfoBean }
     * 
     */
    public ModelInfoBean createModelInfoBean() {
        return new ModelInfoBean();
    }

    /**
     * Create an instance of {@link GetBlockCount }
     * 
     */
    public GetBlockCount createGetBlockCount() {
        return new GetBlockCount();
    }

    /**
     * Create an instance of {@link GetValueByAddressResponse }
     * 
     */
    public GetValueByAddressResponse createGetValueByAddressResponse() {
        return new GetValueByAddressResponse();
    }

    /**
     * Create an instance of {@link GetStringValueByAddress }
     * 
     */
    public GetStringValueByAddress createGetStringValueByAddress() {
        return new GetStringValueByAddress();
    }

    /**
     * Create an instance of {@link GetModelInfoResponse }
     * 
     */
    public GetModelInfoResponse createGetModelInfoResponse() {
        return new GetModelInfoResponse();
    }

    /**
     * Create an instance of {@link GetHistoryStringValue }
     * 
     */
    public GetHistoryStringValue createGetHistoryStringValue() {
        return new GetHistoryStringValue();
    }

    /**
     * Create an instance of {@link GetValueByAddress }
     * 
     */
    public GetValueByAddress createGetValueByAddress() {
        return new GetValueByAddress();
    }

    /**
     * Create an instance of {@link IsConnectionEnabled }
     * 
     */
    public IsConnectionEnabled createIsConnectionEnabled() {
        return new IsConnectionEnabled();
    }

    /**
     * Create an instance of {@link IsConnectionEnabledResponse }
     * 
     */
    public IsConnectionEnabledResponse createIsConnectionEnabledResponse() {
        return new IsConnectionEnabledResponse();
    }

    /**
     * Create an instance of {@link GetBooleanValueResponse }
     * 
     */
    public GetBooleanValueResponse createGetBooleanValueResponse() {
        return new GetBooleanValueResponse();
    }

    /**
     * Create an instance of {@link Variable }
     * 
     */
    public Variable createVariable() {
        return new Variable();
    }

    /**
     * Create an instance of {@link GetArrayDimensionCountResponse }
     * 
     */
    public GetArrayDimensionCountResponse createGetArrayDimensionCountResponse() {
        return new GetArrayDimensionCountResponse();
    }

    /**
     * Create an instance of {@link StartModelResponse }
     * 
     */
    public StartModelResponse createStartModelResponse() {
        return new StartModelResponse();
    }

    /**
     * Create an instance of {@link Operand }
     * 
     */
    public Operand createOperand() {
        return new Operand();
    }

    /**
     * Create an instance of {@link IsArray }
     * 
     */
    public IsArray createIsArray() {
        return new IsArray();
    }

    /**
     * Create an instance of {@link GetStringValueByAddressResponse }
     * 
     */
    public GetStringValueByAddressResponse createGetStringValueByAddressResponse() {
        return new GetStringValueByAddressResponse();
    }

    /**
     * Create an instance of {@link ResumeModelResponse }
     * 
     */
    public ResumeModelResponse createResumeModelResponse() {
        return new ResumeModelResponse();
    }

    /**
     * Create an instance of {@link SendBooleanValueResponse }
     * 
     */
    public SendBooleanValueResponse createSendBooleanValueResponse() {
        return new SendBooleanValueResponse();
    }

    /**
     * Create an instance of {@link StartModel }
     * 
     */
    public StartModel createStartModel() {
        return new StartModel();
    }

    /**
     * Create an instance of {@link IsArrayResponse }
     * 
     */
    public IsArrayResponse createIsArrayResponse() {
        return new IsArrayResponse();
    }

    /**
     * Create an instance of {@link PauseModelResponse }
     * 
     */
    public PauseModelResponse createPauseModelResponse() {
        return new PauseModelResponse();
    }

    /**
     * Create an instance of {@link StopModel }
     * 
     */
    public StopModel createStopModel() {
        return new StopModel();
    }

    /**
     * Create an instance of {@link IsManagingEnabled }
     * 
     */
    public IsManagingEnabled createIsManagingEnabled() {
        return new IsManagingEnabled();
    }

    /**
     * Create an instance of {@link GetAvailModelCount }
     * 
     */
    public GetAvailModelCount createGetAvailModelCount() {
        return new GetAvailModelCount();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBlockCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetBlockCount")
    public JAXBElement<GetBlockCount> createGetBlockCount(GetBlockCount value) {
        return new JAXBElement<GetBlockCount>(_GetBlockCount_QNAME, GetBlockCount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PauseModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "PauseModel")
    public JAXBElement<PauseModel> createPauseModel(PauseModel value) {
        return new JAXBElement<PauseModel>(_PauseModel_QNAME, PauseModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendDoubleValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "SendDoubleValueResponse")
    public JAXBElement<SendDoubleValueResponse> createSendDoubleValueResponse(SendDoubleValueResponse value) {
        return new JAXBElement<SendDoubleValueResponse>(_SendDoubleValueResponse_QNAME, SendDoubleValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsHistoryExistsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsHistoryExistsResponse")
    public JAXBElement<IsHistoryExistsResponse> createIsHistoryExistsResponse(IsHistoryExistsResponse value) {
        return new JAXBElement<IsHistoryExistsResponse>(_IsHistoryExistsResponse_QNAME, IsHistoryExistsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendDoubleValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "SendDoubleValue")
    public JAXBElement<SendDoubleValue> createSendDoubleValue(SendDoubleValue value) {
        return new JAXBElement<SendDoubleValue>(_SendDoubleValue_QNAME, SendDoubleValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendBooleanValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "SendBooleanValueResponse")
    public JAXBElement<SendBooleanValueResponse> createSendBooleanValueResponse(SendBooleanValueResponse value) {
        return new JAXBElement<SendBooleanValueResponse>(_SendBooleanValueResponse_QNAME, SendBooleanValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsManagingEnabledResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsManagingEnabledResponse")
    public JAXBElement<IsManagingEnabledResponse> createIsManagingEnabledResponse(IsManagingEnabledResponse value) {
        return new JAXBElement<IsManagingEnabledResponse>(_IsManagingEnabledResponse_QNAME, IsManagingEnabledResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModelInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "getModelInfoResponse")
    public JAXBElement<GetModelInfoResponse> createGetModelInfoResponse(GetModelInfoResponse value) {
        return new JAXBElement<GetModelInfoResponse>(_GetModelInfoResponse_QNAME, GetModelInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Compare }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "Compare")
    public JAXBElement<Compare> createCompare(Compare value) {
        return new JAXBElement<Compare>(_Compare_QNAME, Compare.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsManagingEnabled }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsManagingEnabled")
    public JAXBElement<IsManagingEnabled> createIsManagingEnabled(IsManagingEnabled value) {
        return new JAXBElement<IsManagingEnabled>(_IsManagingEnabled_QNAME, IsManagingEnabled.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFormDescrAsByteArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetFormDescrAsByteArray")
    public JAXBElement<GetFormDescrAsByteArray> createGetFormDescrAsByteArray(GetFormDescrAsByteArray value) {
        return new JAXBElement<GetFormDescrAsByteArray>(_GetFormDescrAsByteArray_QNAME, GetFormDescrAsByteArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistoryStringValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetHistoryStringValue")
    public JAXBElement<GetHistoryStringValue> createGetHistoryStringValue(GetHistoryStringValue value) {
        return new JAXBElement<GetHistoryStringValue>(_GetHistoryStringValue_QNAME, GetHistoryStringValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "StartModelResponse")
    public JAXBElement<StartModelResponse> createStartModelResponse(StartModelResponse value) {
        return new JAXBElement<StartModelResponse>(_StartModelResponse_QNAME, StartModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArrayValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetArrayValue")
    public JAXBElement<GetArrayValue> createGetArrayValue(GetArrayValue value) {
        return new JAXBElement<GetArrayValue>(_GetArrayValue_QNAME, GetArrayValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsConnectionEnabled }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsConnectionEnabled")
    public JAXBElement<IsConnectionEnabled> createIsConnectionEnabled(IsConnectionEnabled value) {
        return new JAXBElement<IsConnectionEnabled>(_IsConnectionEnabled_QNAME, IsConnectionEnabled.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "StopModelResponse")
    public JAXBElement<StopModelResponse> createStopModelResponse(StopModelResponse value) {
        return new JAXBElement<StopModelResponse>(_StopModelResponse_QNAME, StopModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHistoryStringValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetHistoryStringValueResponse")
    public JAXBElement<GetHistoryStringValueResponse> createGetHistoryStringValueResponse(GetHistoryStringValueResponse value) {
        return new JAXBElement<GetHistoryStringValueResponse>(_GetHistoryStringValueResponse_QNAME, GetHistoryStringValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModelInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "getModelInfo")
    public JAXBElement<GetModelInfo> createGetModelInfo(GetModelInfo value) {
        return new JAXBElement<GetModelInfo>(_GetModelInfo_QNAME, GetModelInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailModelCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "getAvailModelCount")
    public JAXBElement<GetAvailModelCount> createGetAvailModelCount(GetAvailModelCount value) {
        return new JAXBElement<GetAvailModelCount>(_GetAvailModelCount_QNAME, GetAvailModelCount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBooleanValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetBooleanValueResponse")
    public JAXBElement<GetBooleanValueResponse> createGetBooleanValueResponse(GetBooleanValueResponse value) {
        return new JAXBElement<GetBooleanValueResponse>(_GetBooleanValueResponse_QNAME, GetBooleanValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStringValueByAddress }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetStringValueByAddress")
    public JAXBElement<GetStringValueByAddress> createGetStringValueByAddress(GetStringValueByAddress value) {
        return new JAXBElement<GetStringValueByAddress>(_GetStringValueByAddress_QNAME, GetStringValueByAddress.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBlockIndexResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetBlockIndexResponse")
    public JAXBElement<GetBlockIndexResponse> createGetBlockIndexResponse(GetBlockIndexResponse value) {
        return new JAXBElement<GetBlockIndexResponse>(_GetBlockIndexResponse_QNAME, GetBlockIndexResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsArray")
    public JAXBElement<IsArray> createIsArray(IsArray value) {
        return new JAXBElement<IsArray>(_IsArray_QNAME, IsArray.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartModelByGuid }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "StartModelByGuid")
    public JAXBElement<StartModelByGuid> createStartModelByGuid(StartModelByGuid value) {
        return new JAXBElement<StartModelByGuid>(_StartModelByGuid_QNAME, StartModelByGuid.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBlockIndex }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetBlockIndex")
    public JAXBElement<GetBlockIndex> createGetBlockIndex(GetBlockIndex value) {
        return new JAXBElement<GetBlockIndex>(_GetBlockIndex_QNAME, GetBlockIndex.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "StartModel")
    public JAXBElement<StartModel> createStartModel(StartModel value) {
        return new JAXBElement<StartModel>(_StartModel_QNAME, StartModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendBooleanValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "SendBooleanValue")
    public JAXBElement<SendBooleanValue> createSendBooleanValue(SendBooleanValue value) {
        return new JAXBElement<SendBooleanValue>(_SendBooleanValue_QNAME, SendBooleanValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetValue")
    public JAXBElement<GetValue> createGetValue(GetValue value) {
        return new JAXBElement<GetValue>(_GetValue_QNAME, GetValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsConnectionEnabledResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsConnectionEnabledResponse")
    public JAXBElement<IsConnectionEnabledResponse> createIsConnectionEnabledResponse(IsConnectionEnabledResponse value) {
        return new JAXBElement<IsConnectionEnabledResponse>(_IsConnectionEnabledResponse_QNAME, IsConnectionEnabledResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetIntValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetIntValueResponse")
    public JAXBElement<GetIntValueResponse> createGetIntValueResponse(GetIntValueResponse value) {
        return new JAXBElement<GetIntValueResponse>(_GetIntValueResponse_QNAME, GetIntValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResumeModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "ResumeModel")
    public JAXBElement<ResumeModel> createResumeModel(ResumeModel value) {
        return new JAXBElement<ResumeModel>(_ResumeModel_QNAME, ResumeModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModelException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "ModelException")
    public JAXBElement<ModelException> createModelException(ModelException value) {
        return new JAXBElement<ModelException>(_ModelException_QNAME, ModelException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartModelByGuidResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "StartModelByGuidResponse")
    public JAXBElement<StartModelByGuidResponse> createStartModelByGuidResponse(StartModelByGuidResponse value) {
        return new JAXBElement<StartModelByGuidResponse>(_StartModelByGuidResponse_QNAME, StartModelByGuidResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFormDescr }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetFormDescr")
    public JAXBElement<GetFormDescr> createGetFormDescr(GetFormDescr value) {
        return new JAXBElement<GetFormDescr>(_GetFormDescr_QNAME, GetFormDescr.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "CreateModel")
    public JAXBElement<CreateModel> createCreateModel(CreateModel value) {
        return new JAXBElement<CreateModel>(_CreateModel_QNAME, CreateModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetValueResponse")
    public JAXBElement<GetValueResponse> createGetValueResponse(GetValueResponse value) {
        return new JAXBElement<GetValueResponse>(_GetValueResponse_QNAME, GetValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBlockCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetBlockCountResponse")
    public JAXBElement<GetBlockCountResponse> createGetBlockCountResponse(GetBlockCountResponse value) {
        return new JAXBElement<GetBlockCountResponse>(_GetBlockCountResponse_QNAME, GetBlockCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArrayValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetArrayValueResponse")
    public JAXBElement<GetArrayValueResponse> createGetArrayValueResponse(GetArrayValueResponse value) {
        return new JAXBElement<GetArrayValueResponse>(_GetArrayValueResponse_QNAME, GetArrayValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FireBlockEvent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "FireBlockEvent")
    public JAXBElement<FireBlockEvent> createFireBlockEvent(FireBlockEvent value) {
        return new JAXBElement<FireBlockEvent>(_FireBlockEvent_QNAME, FireBlockEvent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResumeModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "ResumeModelResponse")
    public JAXBElement<ResumeModelResponse> createResumeModelResponse(ResumeModelResponse value) {
        return new JAXBElement<ResumeModelResponse>(_ResumeModelResponse_QNAME, ResumeModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompareResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "CompareResponse")
    public JAXBElement<CompareResponse> createCompareResponse(CompareResponse value) {
        return new JAXBElement<CompareResponse>(_CompareResponse_QNAME, CompareResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsHistoryExists }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsHistoryExists")
    public JAXBElement<IsHistoryExists> createIsHistoryExists(IsHistoryExists value) {
        return new JAXBElement<IsHistoryExists>(_IsHistoryExists_QNAME, IsHistoryExists.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFormDescrResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetFormDescrResponse")
    public JAXBElement<GetFormDescrResponse> createGetFormDescrResponse(GetFormDescrResponse value) {
        return new JAXBElement<GetFormDescrResponse>(_GetFormDescrResponse_QNAME, GetFormDescrResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetValueType")
    public JAXBElement<GetValueType> createGetValueType(GetValueType value) {
        return new JAXBElement<GetValueType>(_GetValueType_QNAME, GetValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStringValueByAddressResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetStringValueByAddressResponse")
    public JAXBElement<GetStringValueByAddressResponse> createGetStringValueByAddressResponse(GetStringValueByAddressResponse value) {
        return new JAXBElement<GetStringValueByAddressResponse>(_GetStringValueByAddressResponse_QNAME, GetStringValueByAddressResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArrayDimensionLength }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetArrayDimensionLength")
    public JAXBElement<GetArrayDimensionLength> createGetArrayDimensionLength(GetArrayDimensionLength value) {
        return new JAXBElement<GetArrayDimensionLength>(_GetArrayDimensionLength_QNAME, GetArrayDimensionLength.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArrayDimensionCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetArrayDimensionCountResponse")
    public JAXBElement<GetArrayDimensionCountResponse> createGetArrayDimensionCountResponse(GetArrayDimensionCountResponse value) {
        return new JAXBElement<GetArrayDimensionCountResponse>(_GetArrayDimensionCountResponse_QNAME, GetArrayDimensionCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArrayDimensionLengthResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetArrayDimensionLengthResponse")
    public JAXBElement<GetArrayDimensionLengthResponse> createGetArrayDimensionLengthResponse(GetArrayDimensionLengthResponse value) {
        return new JAXBElement<GetArrayDimensionLengthResponse>(_GetArrayDimensionLengthResponse_QNAME, GetArrayDimensionLengthResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStringValueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetStringValueResponse")
    public JAXBElement<GetStringValueResponse> createGetStringValueResponse(GetStringValueResponse value) {
        return new JAXBElement<GetStringValueResponse>(_GetStringValueResponse_QNAME, GetStringValueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetErrorString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetErrorString")
    public JAXBElement<GetErrorString> createGetErrorString(GetErrorString value) {
        return new JAXBElement<GetErrorString>(_GetErrorString_QNAME, GetErrorString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailModelCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "getAvailModelCountResponse")
    public JAXBElement<GetAvailModelCountResponse> createGetAvailModelCountResponse(GetAvailModelCountResponse value) {
        return new JAXBElement<GetAvailModelCountResponse>(_GetAvailModelCountResponse_QNAME, GetAvailModelCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "CreateModelResponse")
    public JAXBElement<CreateModelResponse> createCreateModelResponse(CreateModelResponse value) {
        return new JAXBElement<CreateModelResponse>(_CreateModelResponse_QNAME, CreateModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFormDescrAsByteArrayResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetFormDescrAsByteArrayResponse")
    public JAXBElement<GetFormDescrAsByteArrayResponse> createGetFormDescrAsByteArrayResponse(GetFormDescrAsByteArrayResponse value) {
        return new JAXBElement<GetFormDescrAsByteArrayResponse>(_GetFormDescrAsByteArrayResponse_QNAME, GetFormDescrAsByteArrayResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopModel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "StopModel")
    public JAXBElement<StopModel> createStopModel(StopModel value) {
        return new JAXBElement<StopModel>(_StopModel_QNAME, StopModel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetIntValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetIntValue")
    public JAXBElement<GetIntValue> createGetIntValue(GetIntValue value) {
        return new JAXBElement<GetIntValue>(_GetIntValue_QNAME, GetIntValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetValueByAddress }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetValueByAddress")
    public JAXBElement<GetValueByAddress> createGetValueByAddress(GetValueByAddress value) {
        return new JAXBElement<GetValueByAddress>(_GetValueByAddress_QNAME, GetValueByAddress.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBooleanValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetBooleanValue")
    public JAXBElement<GetBooleanValue> createGetBooleanValue(GetBooleanValue value) {
        return new JAXBElement<GetBooleanValue>(_GetBooleanValue_QNAME, GetBooleanValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsArrayResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "IsArrayResponse")
    public JAXBElement<IsArrayResponse> createIsArrayResponse(IsArrayResponse value) {
        return new JAXBElement<IsArrayResponse>(_IsArrayResponse_QNAME, IsArrayResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FireBlockEventResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "FireBlockEventResponse")
    public JAXBElement<FireBlockEventResponse> createFireBlockEventResponse(FireBlockEventResponse value) {
        return new JAXBElement<FireBlockEventResponse>(_FireBlockEventResponse_QNAME, FireBlockEventResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetArrayDimensionCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetArrayDimensionCount")
    public JAXBElement<GetArrayDimensionCount> createGetArrayDimensionCount(GetArrayDimensionCount value) {
        return new JAXBElement<GetArrayDimensionCount>(_GetArrayDimensionCount_QNAME, GetArrayDimensionCount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetErrorStringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetErrorStringResponse")
    public JAXBElement<GetErrorStringResponse> createGetErrorStringResponse(GetErrorStringResponse value) {
        return new JAXBElement<GetErrorStringResponse>(_GetErrorStringResponse_QNAME, GetErrorStringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetValueByAddressResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetValueByAddressResponse")
    public JAXBElement<GetValueByAddressResponse> createGetValueByAddressResponse(GetValueByAddressResponse value) {
        return new JAXBElement<GetValueByAddressResponse>(_GetValueByAddressResponse_QNAME, GetValueByAddressResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStringValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetStringValue")
    public JAXBElement<GetStringValue> createGetStringValue(GetStringValue value) {
        return new JAXBElement<GetStringValue>(_GetStringValue_QNAME, GetStringValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetValueTypeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "GetValueTypeResponse")
    public JAXBElement<GetValueTypeResponse> createGetValueTypeResponse(GetValueTypeResponse value) {
        return new JAXBElement<GetValueTypeResponse>(_GetValueTypeResponse_QNAME, GetValueTypeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PauseModelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://manager.mp/", name = "PauseModelResponse")
    public JAXBElement<PauseModelResponse> createPauseModelResponse(PauseModelResponse value) {
        return new JAXBElement<PauseModelResponse>(_PauseModelResponse_QNAME, PauseModelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = GetFormDescrAsByteArrayResponse.class)
    public JAXBElement<byte[]> createGetFormDescrAsByteArrayResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_GetFormDescrAsByteArrayResponseReturn_QNAME, byte[].class, GetFormDescrAsByteArrayResponse.class, ((byte[]) value));
    }

}
