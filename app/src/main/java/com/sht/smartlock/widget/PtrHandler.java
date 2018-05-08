package com.sht.smartlock.widget;

import android.view.View;

public interface PtrHandler {

    /**
     * Check can do refreshData or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link in.srain.cube.views.ptr.PtrDefaultHandler#checkContentCanBePulledDown}
     */
    boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * When refreshData begin
     *
     * @param frame
     */
    void onRefreshBegin(final PtrFrameLayout frame);
}