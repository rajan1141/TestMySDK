package com.test.my.app.common.pdfviewer.listener;

public interface OnPageErrorListener {

    /**
     * Called if error occurred while loading PDF page
     * @param t Throwable with error
     */
    void onPageError(int page, Throwable t);
}
