package com.communote.server.core.mail.messages;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.communote.server.model.security.SecurityCode;
import com.communote.server.model.user.User;
import com.communote.server.persistence.user.client.ClientUrlHelper;

/**
 * @author Communote GmbH - <a href="http://www.communote.com/">http://www.communote.com/</a>
 */
public class SecurityCodeMailMessage extends MailMessage {

    /** The Constant CONFIRMATION_LINK_PREFIX. */
    private final static String CONFIRMATION_LINK_PREFIX = "/user/confirmcode.do";

    /** The Constant CONFIRMATION_PARAM_CODE. */
    private final static String CONFIRMATION_PARAM_CODE = "securityCode";

    /** The Constant CONFIRMATION_PARAM_ACTION. */
    private final static String CONFIRMATION_PARAM_ACTION = "action";

    private static final String CONFIRMATION_PARAM_LANGUAGE = "lang=";

    /**
     * Construct a new message with the given message key and locale.
     * 
     * @param messageKey
     *            Key of the message template.
     * @param locale
     *            the locale to use
     * @param receivers
     *            List of receivers.
     */
    public SecurityCodeMailMessage(String messageKey, Locale locale, User... receivers) {
        super(messageKey, locale, receivers);
    }

    /**
     * Gets the link param action.
     * 
     * @return the link param action
     */
    public String getLinkParamAction() {
        return CONFIRMATION_PARAM_ACTION;
    }

    /**
     * Gets the link param code.
     * 
     * @return the link param code
     */
    public String getLinkParamCode() {
        return CONFIRMATION_PARAM_CODE;
    }

    /**
     * Gets the link prefix.
     * 
     * @return the link prefix
     */
    public String getLinkPrefix() {
        return CONFIRMATION_LINK_PREFIX;
    }

    /**
     * Get the link for confirmation of the security code.
     * 
     * @param securityCode
     *            the security code
     * @return the link
     */
    public String getSecurityCodeConfirmationLink(SecurityCode securityCode) {
        return getSecurityCodeConfirmationLink(securityCode, null);
    }

    /**
     * @param securityCode
     *            the security code
     * @param query
     *            some addtional parameters to be append to the url
     * @return the final absolute url for confirmation
     */
    public String getSecurityCodeConfirmationLink(SecurityCode securityCode, String query) {
        String urlPrefix = ClientUrlHelper.renderConfiguredAbsoluteUrl(getLinkPrefix(), true);
        StringBuilder securityConfirmLink = new StringBuilder(urlPrefix);
        securityConfirmLink.append(securityConfirmLink.indexOf("?") > -1 ? "&" : "?");
        securityConfirmLink.append(getLinkParamCode() + "=" + securityCode.getCode());
        if (securityCode.getAction() != null) {
            securityConfirmLink.append("&");
            securityConfirmLink.append(getLinkParamAction() + "=" + securityCode.getAction());
        }
        if (StringUtils.isNotBlank(query)) {
            securityConfirmLink.append("&");
            securityConfirmLink.append(query);
        }
        securityConfirmLink.append("&");
        securityConfirmLink.append(CONFIRMATION_PARAM_LANGUAGE);
        securityConfirmLink.append(getLocale().getLanguage());
        return securityConfirmLink.toString();
    }

    /**
     * Does nothing.
     * 
     * {@inheritDoc}
     */
    @Override
    protected void prepareModel(Map<String, Object> model) {
        // Does nothing.
    }
}
