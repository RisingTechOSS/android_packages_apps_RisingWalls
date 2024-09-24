/*
 * Copyright (C)  2024 The RisingOS Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.risingos.wallpaperpicker.jsonparser.objecs.homepage;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.utils.NetworkUtils;

public class HomepageObject {
    private ImageView imageView;
    private Bitmap bitmap;
    private String image;

    public void setImageView(ImageView mImageView) {
        if (bitmap == null) {
            imageView = mImageView;
        } else {
            mImageView.setImageBitmap(bitmap);
        }
    }

    public void notifyParseComplete() {
        MainApplication.getInstance().getThreadPoolExecutor().execute(new NetworkUtils.NetworkRunnable("Bitmap", image, new NetworkUtils.onFetchCompleteCallback() {
            @Override
            public void onFetchComplete(Object fetchedData) {
                if (imageView != null) {
                    MainApplication.getInstance().runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap((Bitmap) fetchedData);
                        }
                    });
                }
                bitmap = (Bitmap) fetchedData;
            }
        }));
    }
}
