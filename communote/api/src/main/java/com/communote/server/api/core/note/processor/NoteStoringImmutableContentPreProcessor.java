package com.communote.server.api.core.note.processor;

import com.communote.common.util.Orderable;
import com.communote.server.api.core.note.NoteManagementAuthorizationException;
import com.communote.server.api.core.note.NoteStoringTO;

/**
 * A preprocessor which is invoked before storing a note. This kind of preprocessor is not allowed
 * to edit the content of the note, but all the other members of the TO can be modified. These
 * preprocessors are called before the those that can edit the content.
 *
 *
 * @author Communote GmbH - <a href="http://www.communote.com/">http://www.communote.com/</a>
 *
 */
public interface NoteStoringImmutableContentPreProcessor extends Orderable {
    /**
     * the default value for the order. This value should be used if the preprocessor has no
     * specific requirements to the invocation order.
     */
    public static final int DEFAULT_ORDER = 1000;

    /**
     *
     * @return the order value which is interpreted as the priority of the processor. The higher the
     *         priority, the earlier this extension will be called.
     */
    @Override
    public int getOrder();

    /**
     * @return whether the PreProcessor should also be called when doing an autosave. This is
     *         normally not required.
     */
    public boolean isProcessAutosave();

    /**
     * Invokes the processor <b>before</b> the note is stored.
     *
     * @param noteStoringTO
     *            The note to work on.
     * @return The altered NoteStoringTO.
     * @throws NoteStoringPreProcessorException
     *             thrown to indicate that the pre-processing failed and the note cannot be created
     * @throws NoteManagementAuthorizationException
     *             thrown to indicate that the note cannot be created because of access restrictions
     */
    // TODO extend signature and pass the plaintext version of the content
    public NoteStoringTO process(NoteStoringTO noteStoringTO)
            throws NoteStoringPreProcessorException, NoteManagementAuthorizationException;
}
