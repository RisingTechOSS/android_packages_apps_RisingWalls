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

package org.risingos.wallpaperpicker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.R;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaper;
import org.risingos.wallpaperpicker.jsonparser.objecs.homepage.HomepageManifest;
import org.risingos.wallpaperpicker.utils.IntentHelper;

import java.util.List;

public class CreditsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credits);

        TextView creditsView = findViewById(R.id.wallpaper_contributors);

        List<String> contributors = MainApplication.getInstance().getHomepageManifest().getContributors();
        String credits = new String();
        for (int i = 0; i < contributors.size(); i++) {
            credits += contributors.get(i) + "     ";
        }
        creditsView.setText(credits);
    }
}
