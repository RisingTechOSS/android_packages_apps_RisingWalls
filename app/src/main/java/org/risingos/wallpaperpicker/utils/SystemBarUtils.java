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

package org.risingos.wallpaperpicker.utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import org.risingos.wallpaperpicker.MainApplication;

public class SystemBarUtils {
    public static void setHeightOfViewToStatusBarHeight(Context context, View view) {
        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int height = context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("status_bar_height", "dimen", "android"));

                MainApplication.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setLayoutParams(new LinearLayout.LayoutParams(0, height));
                    }
                });
            }
        });
    }

    public static void setHeightOfViewToNavBarHeight(Context context, View view) {
        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int height = context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));

                MainApplication.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setLayoutParams(new LinearLayout.LayoutParams(0, height));
                    }
                });
            }
        });
    }
}
