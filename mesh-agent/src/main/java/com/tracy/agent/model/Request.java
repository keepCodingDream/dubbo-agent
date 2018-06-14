package com.tracy.agent.model;

/**
 * @author tracy.
 * @create 2018-06-07 15:12
 **/
public class Request {
    private Long requestId;
    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;

    public Request() {
        requestId = System.nanoTime();
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public void setParameterTypesString(String parameterTypesString) {
        this.parameterTypesString = parameterTypesString;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
