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

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperUtils {
    WallpaperManager wallpaperManager;
    Context context;

    public WallpaperUtils(Context c) {
        wallpaperManager = WallpaperManager.getInstance(c);
        context = c;
    }

    public void setFlatWallpaper(Bitmap wallpaper, boolean which[], boolean isDepth, OnSetCompleteCallback callback) {

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int whichInt = getWhichInt(which);
                boolean success;
                MainApplication mainApplication = MainApplication.getInstance();

                try {
                    if (whichInt != 0)
                        wallpaperManager.setBitmap(wallpaper, null, true, whichInt);

                } catch (IOException e) {
                    e.printStackTrace();
                    if (!isDepth)
                        mainApplication.makeToast(R.string.apply_fail, Toast.LENGTH_LONG);
                }

                if (callback != null) {
                    mainApplication.makeToast(R.string.apply_complete, Toast.LENGTH_LONG);
                    callback.onSetComplete();
                }
            }
        });
    }

    public void setDepthWallpaper(Bitmap background, Bitmap subject, boolean which[], OnSetCompleteCallback callback) {

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                setFlatWallpaper(background, which, false, null);

                String basePath = context.getFilesDir() + "/" + Long.toString(System.currentTimeMillis() / 1000L);

                String localPath = basePath + "(1).png";

                if (new File(basePath + ".png").exists()) {
                    for (int i = 2; new File(localPath).exists(); i++) {
                        localPath = basePath + "(" + i + ")" + ".png";
                    }
                } else
                    localPath = basePath + ".png";

                try (FileOutputStream out = new FileOutputStream(localPath)) {
                    subject.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // This requires inline building
                //Settings.System.putStringForUser(context.getContentResolver(), "depth_wallpaper_subject_image_uri", localPath, UserHandle.USER_CURRENT);

                MainApplication.getInstance().makeToast(R.string.apply_complete, Toast.LENGTH_LONG);
                callback.onSetComplete();
            }
        });
    }

    public static void saveImage(Bitmap bitmap, String name) {
        MainApplication.getInstance().makeToast(R.string.saving_wallpaper, Toast.LENGTH_LONG);
        String basePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name);

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String localPath = basePath + "(1).png";

                if (new File(basePath + ".png").exists()) {
                    for (int i = 2; new File(localPath).exists(); i++) {
                        localPath = basePath + "(" + i + ")" + ".png";
                    }
                } else
                    localPath = basePath + ".png";

                try (FileOutputStream out = new FileOutputStream(localPath)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int getWhichInt(boolean which[]) {
        int ret = 0;

        if (which[0])
            ret += WallpaperManager.FLAG_LOCK;

        if (which[1])
            ret += WallpaperManager.FLAG_SYSTEM;

        return ret;
    }

    public interface OnSetCompleteCallback {
        void onSetComplete();
    }
}
