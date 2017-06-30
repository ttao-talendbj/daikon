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

public class EventMetadataFactory {

    private final EventIdGenerator eventIdGenerator;

    private final ServiceInfoProvider serviceInfoProvider;

    private final TimestampProvider timestampProvider;

    private final UserProvider userProvider;

    private final AccountIdProvider accountIdProvider;

    public EventMetadataFactory(EventIdGenerator eventIdGenerator, ServiceInfoProvider serviceInfoProvider,
                                TimestampProvider timestampProvider, UserProvider userProvider, AccountIdProvider accountIdProvider) {
        this.eventIdGenerator = eventIdGenerator;
        this.serviceInfoProvider = serviceInfoProvider;
        this.timestampProvider = timestampProvider;
        this.userProvider = userProvider;
        this.accountIdProvider = accountIdProvider;
    }

    public EventMetadata createEventMetadataBuilder(RequestContextProvider requestContextProvider) {
        final RequestContext requestContext = requestContextProvider.getRequestContext();
        return EventMetadata.newBuilder().setId(eventIdGenerator.generateEventId())
                .setTimestamp(timestampProvider.getCurrentTimestamp())
                .setIssuer(new EventIssuer(serviceInfoProvider.getServiceName(), serviceInfoProvider.getServiceVersion()))
                .setAccountId(accountIdProvider.getAccountId()).setUserId(userProvider.getUserId())
                .setRequestId(requestContext.getRequestId()).setTransactionId(requestContext.getTransactionId())
                .setRequestDetails(createRequestDetails(requestContext))
                .build();
    }

    private RequestDetails createRequestDetails(RequestContext requestContext) {
        final String requestType = hasRequestType(requestContext) ? requestContext.getRequestType() : null;
        final Map<String, String> requestParameters = hasRequestParameters(requestContext) ? requestContext.getRequestParameters()
                : null;
        return requestType != null ? new RequestDetails(requestType, requestParameters) : null;
    }

    private static boolean hasRequestType(RequestContext requestContext) {
        return requestContext.getRequestType() != null && requestContext.getRequestType().equals("");
    }

    private static boolean hasRequestParameters(RequestContext requestContext) {
        return requestContext.getRequestParameters() != null && !requestContext.getRequestParameters().isEmpty();
    }

}
