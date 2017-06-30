// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.events;

import java.util.Map;

public class RequestContext {

    private final String requestId;

    private final String transactionId;

    private final String requestType;

    private final Map<String, String> requestParameters;

    public RequestContext(String requestId, String transactionId, String requestType, Map<String, String> requestParameters) {
        this.requestId = requestId;
        this.transactionId = transactionId;
        this.requestType = requestType;
        this.requestParameters = requestParameters;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getRequestType() {
        return requestType;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }
}
