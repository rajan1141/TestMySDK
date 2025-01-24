
package com.test.my.app.common.pdfviewer.link;


import com.test.my.app.common.pdfviewer.model.LinkTapEvent;

public interface LinkHandler {

    /**
     * Called when link was tapped by user
     *
     * @param event current event
     */
    void handleLinkEvent(LinkTapEvent event);
}
