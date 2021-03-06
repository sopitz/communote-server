package com.communote.server.core.mail.message.fetching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.communote.server.core.mail.MailManagement;
import com.communote.server.core.mail.messages.fetching.WarningMailMessage;
import com.communote.server.model.user.User;
import com.communote.server.test.mail.MailMessageCommunoteIntegrationTest;


/**
 * @author Communote GmbH - <a href="http://www.communote.com/">http://www.communote.com/</a>
 */
public class WarningMailMessageTest extends MailMessageCommunoteIntegrationTest {

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(MailManagement mailManagement, User... receivers) {
        Collection<String> unresolvableUsers = new ArrayList<String>();
        unresolvableUsers.add(UUID.randomUUID().toString());
        unresolvableUsers.add(UUID.randomUUID().toString());
        Collection<String> uninformableUsers = new ArrayList<String>(unresolvableUsers);
        Collection<String> unresolvableBlogs = new ArrayList<String>(unresolvableUsers);
        Collection<String> unwritableBlogs = new ArrayList<String>(unresolvableUsers);
        for (User receiver : receivers) {
            mailManagement.sendMail(new WarningMailMessage(receiver, unresolvableUsers,
                    uninformableUsers, unresolvableBlogs, unwritableBlogs, UUID.randomUUID()
                            .toString(), true));
            mailManagement.sendMail(new WarningMailMessage(receiver, unresolvableUsers,
                    uninformableUsers, unresolvableBlogs, unwritableBlogs, UUID.randomUUID()
                            .toString(), false));
        }
    }
}
