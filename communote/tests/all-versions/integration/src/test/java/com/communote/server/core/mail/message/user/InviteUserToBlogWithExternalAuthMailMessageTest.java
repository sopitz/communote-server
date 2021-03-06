package com.communote.server.core.mail.message.user;

import com.communote.server.core.mail.MailManagement;
import com.communote.server.core.mail.messages.user.InviteUserToBlogWithExternalAuthMailMessage;
import com.communote.server.model.blog.Blog;
import com.communote.server.model.user.User;
import com.communote.server.test.mail.MailMessageCommunoteIntegrationTest;
import com.communote.server.test.util.TestUtils;


/**
 * @author Communote GmbH - <a href="http://www.communote.com/">http://www.communote.com/</a>
 */
public class InviteUserToBlogWithExternalAuthMailMessageTest extends
        MailMessageCommunoteIntegrationTest {
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(MailManagement mailManagement, User... receivers) throws Exception {
        Blog blog = TestUtils.createRandomBlog(true, true, receivers);
        for (User receiver : receivers) {
            mailManagement.sendMail(new InviteUserToBlogWithExternalAuthMailMessage(receiver,
                    receiver,
                    blog));
        }
    }
}
