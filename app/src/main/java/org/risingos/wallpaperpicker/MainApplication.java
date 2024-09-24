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

package org.risingos.wallpaperpicker;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import org.risingos.wallpaperpicker.activities.MainActivity;
import org.risingos.wallpaperpicker.fragments.HomeCardFragment;
import org.risingos.wallpaperpicker.jsonparser.OnParseCompleteCallback;
import org.risingos.wallpaperpicker.jsonparser.Parser;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaperManifest;
import org.risingos.wallpaperpicker.jsonparser.objecs.flat.FlatWallpaperManifest;
import org.risingos.wallpaperpicker.jsonparser.objecs.homepage.HomepageManifest;
import org.risingos.wallpaperpicker.utils.IntentHelper;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
    private static MainApplication instance;

    private ThreadPoolExecutor threadPoolExecutor;
    private Handler mainThreadHandler;

    private FlatWallpaperManifest flatWallpaperManifest;
    private DepthWallpaperManifest depthWallpaperManifest;
    private HomepageManifest homepageManifest;

    private IntentHelper intentHelper;

    private HomeCardFragment[] homeCardFragments = {null, null, null, null};

    private MainActivity mMainActivity;

    @Override
    public void onCreate(){
        super.onCreate();

        instance = this;

        int coreCount = Runtime.getRuntime().availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(
                coreCount,
                coreCount * 2,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());

        mainThreadHandler = new Handler(getMainLooper());

        intentHelper = new IntentHelper();

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Parser.parseHomepageManifest(new OnParseCompleteCallback() {
                    @Override
                    public void onParseComplete(Object parsedData) {

                        if (homeCardFragments[0] != null & homeCardFragments[1] != null & homeCardFragments[2] != null & homeCardFragments[3] != null) {
                            for (int i = 0; i < 4; i++) {
                                homeCardFragments[i].setObject(((HomepageManifest) parsedData).getImages().get(i));
                            }
                        }

                        homepageManifest = (HomepageManifest) parsedData;


                        if (mMainActivity != null)
                            runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMainActivity.setHomepageManifest(homepageManifest);
                                }
                            });
                    }
                });
            }
        });

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                    Parser.parseFlatManifest(new OnParseCompleteCallback() {
                        @Override
                        public void onParseComplete(Object parsedData) {
                            flatWallpaperManifest = (FlatWallpaperManifest) parsedData;
                        }
                    }, MainApplication.this);
            }
        });

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Parser.parseDepthManifest(new OnParseCompleteCallback() {
                    @Override
                    public void onParseComplete(Object parsedData) {
                        depthWallpaperManifest = (DepthWallpaperManifest) parsedData;
                    }
                }, MainApplication.this);
            }
        });
    }

    public void makeToast(@StringRes int text, int duration) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, duration).show();
            }
        });
    }

    public HomepageManifest getHomepageManifest() {
        return homepageManifest;
    }

    public FlatWallpaperManifest getFlatWallpaperManifest() {
        return flatWallpaperManifest;
    }

    public DepthWallpaperManifest getDepthWallpaperManifest() {
        return depthWallpaperManifest;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void runOnMainThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }

    public static MainApplication getInstance() {
        return instance;
    }

    public void registerHomeFragment(HomeCardFragment homeCardFragment, int id) {
        if (homepageManifest != null) {
            homeCardFragment.setObject(homepageManifest.getImages().get(id));
            return;
        }

        homeCardFragments[id] = homeCardFragment;
    }

    public void registerMainActivityInstance(MainActivity mainActivity) {
        if (homepageManifest != null) {
            mainActivity.setHomepageManifest(homepageManifest);
        } else {
            mMainActivity = mainActivity;
        }
    }
}
