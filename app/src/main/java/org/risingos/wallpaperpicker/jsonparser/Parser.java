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

package org.risingos.wallpaperpicker.jsonparser;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaper;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaperManifest;
import org.risingos.wallpaperpicker.jsonparser.objecs.flat.FlatWallpaper;
import org.risingos.wallpaperpicker.jsonparser.objecs.flat.FlatWallpaperManifest;
import org.risingos.wallpaperpicker.jsonparser.objecs.homepage.HomepageManifest;
import org.risingos.wallpaperpicker.utils.NetworkUtils;

import java.lang.annotation.Target;
import java.util.List;

public class Parser {

    public static void parseHomepageManifest(OnParseCompleteCallback callback) {
        String json = NetworkUtils.getStringFromUrl("https://raw.githubusercontent.com/RisingTechOSS/risingwalls_storage/fourteen/homepage_manifest.json");
        HomepageManifest homepageManifest = new Gson().fromJson(json, HomepageManifest.class);
        homepageManifest.notifyParseComplete();
        callback.onParseComplete(homepageManifest);
    }

    public static void parseFlatManifest(OnParseCompleteCallback callback, Context context) {
        String json = NetworkUtils.getStringFromUrl("https://raw.githubusercontent.com/RisingTechOSS/risingwalls_storage/fourteen/flat_manifest.json");

        FlatWallpaperManifest flatWallpaperManifest = new Gson().fromJson(json, FlatWallpaperManifest.class);

        callback.onParseComplete(flatWallpaperManifest);

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<FlatWallpaper> wallpapers = flatWallpaperManifest.getWallpapers();

                for (int i = 0; i < wallpapers.size(); i++) {
                    Glide.with(context).load(wallpapers.get(i).getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                }
            }
        });
    }

    public static void parseDepthManifest(OnParseCompleteCallback callback, Context context) {
        String json = NetworkUtils.getStringFromUrl("https://raw.githubusercontent.com/RisingTechOSS/risingwalls_storage/fourteen/depth_manifest.json");

        DepthWallpaperManifest depthWallpaperManifest = new Gson().fromJson(json, DepthWallpaperManifest.class);

        callback.onParseComplete(depthWallpaperManifest);

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<DepthWallpaper> wallpapers = depthWallpaperManifest.getWallpapers();

                for (int i = 0; i < wallpapers.size(); i++) {
                    Glide.with(context).load(wallpapers.get(i).getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                }
            }
        });
    }
}
