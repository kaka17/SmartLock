/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package com.sht.smartlock.widget.rangebar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * Class representing the blue connecting line between the two thumbs.
 */
class ConnectingLine {

    // Member Variables ////////////////////////////////////////////////////////

    private final Paint mPaint;

    private final float mConnectingLineWeight;
    private final float mY;

    // Constructor /////////////////////////////////////////////////////////////

    ConnectingLine(Context ctx, float y, float connectingLineWeight, int connectingLineColor) {

        final Resources res = ctx.getResources();

        mConnectingLineWeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                          connectingLineWeight,
                                                          res.getDisplayMetrics());

        // Initialize the paint, set values
        mPaint = new Paint();
        mPaint.setColor(connectingLineColor);
        mPaint.setStrokeWidth(mConnectingLineWeight);
        mPaint.setAntiAlias(true);

        mY = y;
    }

    // Package-Private Methods /////////////////////////////////////////////////

    /**
     * Draw the connecting line between the two thumbs.
     * 
     * @param canvas the Canvas to draw to
     * @param leftThumb the left thumb
     * @param rightThumb the right thumb
     */
    void draw(Canvas canvas, Thumb leftThumb, Thumb rightThumb) {
        canvas.drawLine(leftThumb.getX(), mY, rightThumb.getX(), mY, mPaint);
    }
}
