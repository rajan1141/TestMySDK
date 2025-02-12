package com.test.my.app.common.pdfviewer.listener;

import android.view.MotionEvent;

import com.test.my.app.common.pdfviewer.link.LinkHandler;
import com.test.my.app.common.pdfviewer.model.LinkTapEvent;


public class Callbacks {

    /**
     * Call back object to call when the PDF is loaded
     */
    private OnLoadCompleteListener onLoadCompleteListener;

    /**
     * Call back object to call when document loading error occurs
     */
    private OnErrorListener onErrorListener;

    /**
     * Call back object to call when the page load error occurs
     */
    private OnPageErrorListener onPageErrorListener;

    /**
     * Call back object to call when the document is initially rendered
     */
    private OnRenderListener onRenderListener;

    /**
     * Call back object to call when the page has changed
     */
    private OnPageChangeListener onPageChangeListener;

    /**
     * Call back object to call when the page is scrolled
     */
    private OnPageScrollListener onPageScrollListener;

    /**
     * Call back object to call when the above layer is to drawn
     */
    private OnDrawListener onDrawListener;

    private OnDrawListener onDrawAllListener;

    /**
     * Call back object to call when the user does a tap gesture
     */
    private OnTapListener onTapListener;

    /**
     * Call back object to call when the user does a long tap gesture
     */
    private OnLongPressListener onLongPressListener;

    /**
     * Call back object to call when clicking link
     */
    private LinkHandler linkHandler;

    public void setOnLoadComplete(OnLoadCompleteListener onLoadCompleteListener) {
        this.onLoadCompleteListener = onLoadCompleteListener;
    }

    public void callOnLoadComplete(int pagesCount) {
        if (onLoadCompleteListener != null) {
            onLoadCompleteListener.loadComplete(pagesCount);
        }
    }

    public void setOnError(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public OnErrorListener getOnError() {
        return onErrorListener;
    }

    public void setOnPageError(OnPageErrorListener onPageErrorListener) {
        this.onPageErrorListener = onPageErrorListener;
    }

    public boolean callOnPageError(int page, Throwable error) {
        if (onPageErrorListener != null) {
            onPageErrorListener.onPageError(page, error);
            return true;
        }
        return false;
    }

    public void setOnRender(OnRenderListener onRenderListener) {
        this.onRenderListener = onRenderListener;
    }

    public void callOnRender(int pagesCount) {
        if (onRenderListener != null) {
            onRenderListener.onInitiallyRendered(pagesCount);
        }
    }

    public void setOnPageChange(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void callOnPageChange(int page, int pagesCount) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageChanged(page, pagesCount);
        }
    }

    public void setOnPageScroll(OnPageScrollListener onPageScrollListener) {
        this.onPageScrollListener = onPageScrollListener;
    }

    public void callOnPageScroll(int currentPage, float offset) {
        if (onPageScrollListener != null) {
            onPageScrollListener.onPageScrolled(currentPage, offset);
        }
    }

    public void setOnDraw(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

    public OnDrawListener getOnDraw() {
        return onDrawListener;
    }

    public void setOnDrawAll(OnDrawListener onDrawAllListener) {
        this.onDrawAllListener = onDrawAllListener;
    }

    public OnDrawListener getOnDrawAll() {
        return onDrawAllListener;
    }

    public void setOnTap(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public boolean callOnTap(MotionEvent event) {
        return onTapListener != null && onTapListener.onTap(event);
    }

    public void setOnLongPress(OnLongPressListener onLongPressListener) {
        this.onLongPressListener = onLongPressListener;
    }

    public void callOnLongPress(MotionEvent event) {
        if (onLongPressListener != null) {
            onLongPressListener.onLongPress(event);
        }
    }

    public void setLinkHandler(LinkHandler linkHandler) {
        this.linkHandler = linkHandler;
    }

    public void callLinkHandler(LinkTapEvent event) {
        if (linkHandler != null) {
            linkHandler.handleLinkEvent(event);
        }
    }
}
