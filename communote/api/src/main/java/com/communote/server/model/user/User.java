package com.communote.server.model.user;

import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.communote.common.encryption.HashCodeGenerator;
import com.communote.common.util.LocaleHelper;
import com.communote.server.model.global.GlobalId;
import com.communote.server.model.user.security.AuthenticationFailedStatus;

/**
 * A user.
 *
 * @author Communote GmbH - <a href="http://www.communote.com/">http://www.communote.com/</a>
 */
public class User extends CommunoteEntity {
    /**
     * Constructs new instances of {@link User}.
     */
    public static final class Factory {
        /**
         * Constructs a new instance of {@link User}.
         */
        public static User newInstance() {
            return new User();
        }

        /**
         * Constructs a new instance of {@link User}, taking all required and/or read-only
         * properties as arguments.
         */
        public static User newInstance(String email, String languageCode, UserStatus status,
                boolean termsAccepted, boolean reminderMailSent, java.sql.Timestamp statusChanged,
                UserProfile profile) {
            final User entity = new User();
            entity.setEmail(email);
            entity.setLanguageCode(languageCode);
            entity.setStatus(status);
            entity.setTermsAccepted(termsAccepted);
            entity.setReminderMailSent(reminderMailSent);
            entity.setStatusChanged(statusChanged);
            entity.setProfile(profile);
            return entity;
        }

    }

    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = -6870902145348733900L;

    private String password;

    private String email;

    private String languageCode;

    private java.sql.Timestamp lastLogin;

    private UserStatus status;

    private String alias;

    private boolean termsAccepted;

    private boolean reminderMailSent;

    private java.sql.Timestamp statusChanged;

    private String authenticationToken;

    private java.util.Set<UserAuthority> userAuthorities = new java.util.HashSet<UserAuthority>();

    private java.util.Set<AuthenticationFailedStatus> failedAuthentication = new java.util.HashSet<>();

    private UserProfile profile;

    private java.util.Set<ExternalUserAuthentication> externalAuthentications = new java.util.HashSet<ExternalUserAuthentication>();

    private java.util.Set<com.communote.server.model.global.GlobalId> followedItems = new java.util.HashSet<com.communote.server.model.global.GlobalId>();

    private java.util.Set<UserProperty> properties = new java.util.HashSet<UserProperty>();

    private Locale locale;

    /**
     * Builds a string showing the current attribute values
     */
    @Override
    public String attributesToString() {
        StringBuilder sb = new StringBuilder();

        sb.append("class='");
        sb.append(this.getClass().getName());
        sb.append("', ");

        sb.append("password='");
        sb.append(password);
        sb.append("', ");

        sb.append("email='");
        sb.append(email);
        sb.append("', ");

        sb.append("languageCode='");
        sb.append(languageCode);
        sb.append("', ");

        sb.append("lastLogin='");
        sb.append(lastLogin);
        sb.append("', ");

        sb.append("status='");
        sb.append(status);
        sb.append("', ");

        sb.append("alias='");
        sb.append(alias);
        sb.append("', ");

        sb.append("termsAccepted='");
        sb.append(termsAccepted);
        sb.append("', ");

        sb.append("reminderMailSent='");
        sb.append(reminderMailSent);
        sb.append("', ");

        sb.append("statusChanged='");
        sb.append(statusChanged);
        sb.append("', ");

        sb.append("authenticationToken='");
        sb.append(authenticationToken);
        sb.append("', ");

        sb.append(super.attributesToString());

        return sb.toString();
    }

    /**
     * This entity does not have any identifiers but since it extends the
     * <code>com.communote.server.persistence.user.KenmeiEntityImpl</code> class it will simply
     * delegate the call up there.
     *
     * @see CommunoteEntity#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    /**
     *
     */
    @Override
    public String getAlias() {
        return this.alias;
    }

    /**
     * <p>
     * A token, which can be used for authentication processes. The format is CREATION_TIME:TOKEN,
     * where CREATION_TIME is the timestamp in millis. This token should not be used directly,
     * instead the AuthorizationTokenManagement should be used.
     * </p>
     */
    public String getAuthenticationToken() {
        return this.authenticationToken;
    }

    /**
     * <p>
     * The email of the user
     * </p>
     */
    public String getEmail() {
        return this.email;
    }

    /**
     *
     */
    public java.util.Set<ExternalUserAuthentication> getExternalAuthentications() {
        return this.externalAuthentications;
    }

    /**
     *
     */
    public java.util.Set<AuthenticationFailedStatus> getFailedAuthentication() {
        return this.failedAuthentication;
    }

    /**
     *
     */
    public java.util.Set<com.communote.server.model.global.GlobalId> getFollowedItems() {
        return this.followedItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalId getFollowId() {
        return getGlobalId();
    }

    /**
     * @return The language code or the language of {@link Locale#ENGLISH} as default.
     */
    public String getLanguageCode() {
        String code = this.languageCode;
        if (StringUtils.isBlank(code)) {
            code = Locale.ENGLISH.getLanguage();
        }
        return code;
    }

    public Locale getLanguageLocale() {
        if (locale == null) {
            locale = LocaleHelper.toLocale(getLanguageCode());
        }
        return locale;
    }

    /**
     *
     */
    public java.sql.Timestamp getLastLogin() {
        return this.lastLogin;
    }

    /**
     * <p>
     * The password as MD5 hashcode
     * </p>
     */
    public String getPassword() {
        return this.password;
    }

    /**
     *
     */
    public UserProfile getProfile() {
        return this.profile;
    }

    /**
     *
     */
    @Override
    public java.util.Set<UserProperty> getProperties() {
        return this.properties;
    }

    public UserRole[] getRoles() {
        Set<UserAuthority> authorities = getUserAuthorities();
        UserRole[] result = null;
        if (authorities != null && authorities.size() > 0) {
            result = new UserRole[authorities.size()];
            int i = 0;
            for (UserAuthority authority : authorities) {
                result[i] = authority.getRole();
                i++;
            }
        }
        return result;
    }

    /**
     *
     */
    public UserStatus getStatus() {
        return this.status;
    }

    /**
     *
     */
    public java.sql.Timestamp getStatusChanged() {
        return this.statusChanged;
    }

    /**
     *
     */
    public java.util.Set<UserAuthority> getUserAuthorities() {
        return this.userAuthorities;
    }

    /**
     * This entity does not have any identifiers but since it extends the
     * <code>com.communote.server.persistence.user.KenmeiEntityImpl</code> class it will simply
     * delegate the call up there.
     *
     * @see CommunoteEntity#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Test whether the user has the given role.
     *
     * @param role
     *            the role to test
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        Set<UserAuthority> authorities = getUserAuthorities();
        if (authorities != null) {
            for (UserAuthority authority : authorities) {
                if (authority.getRole().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return true if the user has the given status
     */
    public boolean hasStatus(UserStatus status) {
        return getStatus() != null && getStatus().equals(status);
    }

    /**
     * @return true if the user has the status ACTIVE
     */
    public boolean isActivated() {
        return getStatus() != null && getStatus().equals(UserStatus.ACTIVE);
    }

    /**
     *
     */
    public boolean isReminderMailSent() {
        return this.reminderMailSent;
    }

    /**
     *
     */
    public boolean isTermsAccepted() {
        return this.termsAccepted;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExternalAuthentications(
            java.util.Set<ExternalUserAuthentication> externalAuthentications) {
        this.externalAuthentications = externalAuthentications;
    }

    public void setFailedAuthentication(
            java.util.Set<AuthenticationFailedStatus> failedAuthentication) {
        this.failedAuthentication = failedAuthentication;
    }

    public void setFollowedItems(
            java.util.Set<com.communote.server.model.global.GlobalId> followedItems) {
        this.followedItems = followedItems;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        locale = null;
    }

    public void setLanguageLocale(Locale languageLocale) {
        this.locale = languageLocale;
        setLanguageCode(languageLocale.getLanguage());
    }

    public void setLastLogin(java.sql.Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlainPassword(String plainPassword) {
        setPassword(HashCodeGenerator.generateMD5HashCode(plainPassword));
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public void setProperties(java.util.Set<UserProperty> properties) {
        this.properties = properties;
    }

    public void setReminderMailSent(boolean reminderMailSent) {
        this.reminderMailSent = reminderMailSent;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setStatusChanged(java.sql.Timestamp statusChanged) {
        this.statusChanged = statusChanged;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public void setUserAuthorities(java.util.Set<UserAuthority> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }
}