package com.communote.server.core.messaging;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.communote.server.api.core.common.NotFoundException;
import com.communote.server.api.core.event.EventDispatcher;
import com.communote.server.api.core.property.PropertyManagement;
import com.communote.server.api.core.property.PropertyType;
import com.communote.server.api.core.property.StringPropertyTO;
import com.communote.server.api.core.security.AuthorizationException;
import com.communote.server.core.blog.NoteManagement;
import com.communote.server.core.messaging.definitions.DiscussionNotificationDefinition;
import com.communote.server.core.messaging.definitions.LikeNotificationDefinition;
import com.communote.server.core.messaging.definitions.MentionNotificationDefinition;
import com.communote.server.core.user.UserManagement;
import com.communote.server.model.note.Note;
import com.communote.server.model.property.StringProperty;
import com.communote.server.model.user.User;

/**
 * @author Communote GmbH - <a href="http://www.communote.com/">http://www.communote.com/</a>
 */
@Service
public class NotificationService {

    private static final NotificationScheduleTypes DEFAULT_NOTIFICATION_SCHEDULE = NotificationScheduleTypes.IMMEDIATE;

    @Autowired
    private PropertyManagement propertyManagement;
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private NotificationManagement notificationManagement;
    @Autowired
    private NoteManagement noteManagement;
    @Autowired
    private UserManagement userManagement;

    private final static String DEFINITION_KEY_PREFIX = "notification-definition.";

    private final Map<String, NotificationDefinition> registeredDefinitions =
            new HashMap<String, NotificationDefinition>();

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(NotificationService.class);

    /**
     * @return Collection of all registered definitions.
     */
    public Collection<NotificationDefinition> getRegisteredDefinitions() {
        return Collections.unmodifiableCollection(registeredDefinitions.values());
    }

    /**
     * Method to get a map of all set notification definitions and schedules for a given user.
     * 
     * @param userId
     *            The users id.
     * @return A map of all set notification definitions and schedules
     * @throws NotFoundException
     *             Thrown, when the user was not found.
     * @throws AuthorizationException
     *             Thrown, when the current user is not allowed to access the given properties.
     */
    public Map<NotificationDefinition, NotificationScheduleTypes> getUserNotificationSchedules(
            Long userId)
            throws NotFoundException, AuthorizationException {
        Map<NotificationDefinition, NotificationScheduleTypes> result =
                new HashMap<NotificationDefinition, NotificationScheduleTypes>();
        for (NotificationDefinition definition : registeredDefinitions.values()) {
            StringProperty objectProperty = propertyManagement.getObjectProperty(
                    PropertyType.UserProperty, userId,
                    PropertyManagement.KEY_GROUP, DEFINITION_KEY_PREFIX + definition.getId());
            if (objectProperty == null || objectProperty.getPropertyValue() == null
                    || objectProperty.getPropertyValue().length() == 0) {
                result.put(definition, DEFAULT_NOTIFICATION_SCHEDULE);
            } else {
                result.put(definition,
                        NotificationScheduleTypes.valueOf(objectProperty.getPropertyValue()));
            }
        }
        return result;
    }

    /**
     * Post constructor to register default definitions.
     */
    @PostConstruct
    public void postConstruct() {
        register(LikeNotificationDefinition.INSTANCE, DiscussionNotificationDefinition.INSTANCE,
                MentionNotificationDefinition.INSTANCE);
        eventDispatcher.register(new NotifyAboutLikeEventListener(this, noteManagement,
                userManagement, propertyManagement));
    }

    /**
     * Method to register new definitions.
     * 
     * @param definitions
     *            The definitions to register.
     */
    public void register(NotificationDefinition... definitions) {
        for (NotificationDefinition definition : definitions) {
            propertyManagement.addObjectPropertyFilter(PropertyType.UserProperty,
                    PropertyManagement.KEY_GROUP, DEFINITION_KEY_PREFIX + definition.getId());
            registeredDefinitions.put(definition.getId(), definition);
            LOGGER.debug("Added new notification definition: {}", definition.getId());
        }
    }

    /**
     * Method to save a setting for the given user.
     * 
     * @param userId
     *            The user to save the setting for.
     * @param definition
     *            The definition to save.
     * @param scheduleType
     *            The schedule type to set. Use null to delete the setting.
     * @throws NotFoundException
     *             Thrown, when the user doesn't exists.
     * @throws AuthorizationException
     *             Thrown, when the current user is not allowed to access this settings.
     */
    public void saveUserNotificationSchedule(Long userId, NotificationDefinition definition,
            NotificationScheduleTypes scheduleType) throws NotFoundException,
            AuthorizationException {
        propertyManagement.setObjectProperty(PropertyType.UserProperty, userId,
                PropertyManagement.KEY_GROUP, DEFINITION_KEY_PREFIX + definition.getId(),
                scheduleType == null ? null : scheduleType.name());
    }

    /**
     * Method to save schedules for the given user.
     * 
     * @param userId
     *            The user to save the setting for.
     * @param mappings
     *            Mappings from definitions to schedules.
     * @throws NotFoundException
     *             Thrown, when the user doesn't exists.
     * @throws AuthorizationException
     *             Thrown, when the current user is not allowed to access this settings.
     */
    public void saveUserNotificationSchedules(Long userId, Map<NotificationDefinition,
            NotificationScheduleTypes> mappings) throws NotFoundException,
            AuthorizationException {
        Set<StringPropertyTO> properties = new HashSet<StringPropertyTO>();
        for (Entry<NotificationDefinition, NotificationScheduleTypes> mapping : mappings.entrySet()) {
            StringPropertyTO property = new StringPropertyTO();
            property.setKeyGroup(PropertyManagement.KEY_GROUP);
            property.setPropertyKey(DEFINITION_KEY_PREFIX + mapping.getKey().getId());
            NotificationScheduleTypes scheduleType = mapping.getValue();
            property.setPropertyValue(scheduleType == null ? null : scheduleType.name());
            properties.add(property);
        }
        propertyManagement.setObjectProperties(PropertyType.UserProperty, userId, properties);
    }

    /**
     * Send notifications to users to inform about created or edited notes.
     * 
     * @param noteId
     *            Id of the note, which should be send. the note to inform about
     * @param userToNotify
     *            Id of the user to notify.
     * @param notificationDefinition
     *            The current definition of the notification.
     * @param model
     *            Additional elements used for the velocity context.
     */
    public void sendMessage(Long noteId, Long userToNotify,
            NotificationDefinition notificationDefinition, Map<String, Object> model) {
        notificationManagement.sendMessage(noteId, userToNotify, notificationDefinition, model);
    }

    /**
     * Send notifications to users to inform about created or edited notes.
     * 
     * @param note
     *            the note to inform about
     * @param usersToNotify
     *            the users to be notified
     * @param notificationDefinition
     *            The current definition of the notification.
     */
    public void sendMessage(Note note, Collection<User> usersToNotify,
            NotificationDefinition notificationDefinition) {
        notificationManagement.sendMessage(note, usersToNotify, notificationDefinition);
    }

    /**
     * Method to remove registered definitions.
     * 
     * @param definitions
     *            The definitions to remove.
     */
    public void unregister(NotificationDefinition... definitions) {
        for (NotificationDefinition definition : definitions) {
            propertyManagement.removeObjectPropertyFilter(PropertyType.UserNoteProperty,
                    PropertyManagement.KEY_GROUP, DEFINITION_KEY_PREFIX + definition.getId());
            registeredDefinitions.remove(definition.getId());
            LOGGER.debug("Removed notification definition: {}" + definition.getId());
        }
    }

    /**
     * 
     * @param userId
     *            The user.
     * @param definition
     *            The definition to check.
     * @param schedule
     *            The schedule to check.
     * @return True, if the use has set the requested scheduling.
     */
    public boolean userHasSchedule(Long userId, NotificationDefinition definition,
            NotificationScheduleTypes schedule) {
        try {
            StringProperty objectProperty = propertyManagement.getObjectProperty(
                    PropertyType.UserProperty, userId,
                    PropertyManagement.KEY_GROUP, DEFINITION_KEY_PREFIX + definition.getId());
            NotificationScheduleTypes storedType = objectProperty == null ? DEFAULT_NOTIFICATION_SCHEDULE
                    : NotificationScheduleTypes.valueOf(objectProperty.getPropertyValue());
            return storedType.equals(schedule);
        } catch (NotFoundException e) {
            LOGGER.debug(e.getMessage(), e);
        } catch (AuthorizationException e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return false;
    }
}
