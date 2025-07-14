package com.soaprestadapter.interceptor;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;


/**
 * JwtTokenExtractorInterceptor
 */
@Component
public class JwtTokenExtractorInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    /**
     * JwtTokenExtractorInterceptor
     */
    public JwtTokenExtractorInterceptor() {
        super(Phase.PRE_PROTOCOL); // can also use Phase.PRE_PROTOCOL or Phase.UNMARSHAL if needed
    }


    @Override
    public void handleMessage(final SoapMessage message) {
        List<Header> headers = message.getHeaders();

        for (Header header : headers) {
            QName name = header.getName();
            if ("Authorization".equalsIgnoreCase(name.getLocalPart())) {
                Element element = (Element) header.getObject();
                String token = element.getTextContent();
                message.put("jwt_token", token);

                break;
            }
        }
    }
}

