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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.risingos.wallpaperpicker.R;
import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaper;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaperManifest;
import org.risingos.wallpaperpicker.utils.IntentHelper;
import org.risingos.wallpaperpicker.utils.SystemBarUtils;

import java.util.List;

public class DepthPickerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setDecorFitsSystemWindows(false);

        setContentView(R.layout.activity_picker);
        TextView titleText = findViewById(R.id.text_activity_title);
        titleText.setText(R.string.depth_wallpapers);

        Space statusbarSpace = findViewById(R.id.statusbar_space);
        SystemBarUtils.setHeightOfViewToStatusBarHeight(this, statusbarSpace);

        RecyclerView pickerRecycler = findViewById(R.id.picker_recycler);
        DepthPickerAdapter depthPickerAdapter = new DepthPickerAdapter();

        DepthWallpaperManifest depthWallpaperManifest = MainApplication.getInstance().getDepthWallpaperManifest();

        depthPickerAdapter.setDataSet(depthWallpaperManifest.getWallpapers());

        pickerRecycler.setAdapter(depthPickerAdapter);
        pickerRecycler.setHasFixedSize(true);
        pickerRecycler.setLayoutManager(new GridLayoutManager(this, 2));

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int bottom = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

                MainApplication.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        pickerRecycler.setPadding(0, getResources().getDimensionPixelSize(R.dimen.margin_secondary), 0, bottom);
                    }
                });
            }
        });
    }

    private class DepthPickerAdapter extends RecyclerView.Adapter<DepthPickerAdapter.ViewHolder> {
        private List<DepthWallpaper> localDataSet;

        public void setDataSet(List<DepthWallpaper> newDataSet) {
            localDataSet = newDataSet;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.picker_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.setData(localDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return localDataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView titleView;
            private ImageView imageView;
            private View mainView;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mainView = itemView;
                titleView = itemView.findViewById(R.id.text_title);
                imageView = itemView.findViewById(R.id.background_image);
            }

            public void setData(DepthWallpaper data) {
                titleView.setText(data.getTitle());

                Glide.with(getBaseContext()).load(data.getThumbnail()).into(imageView);

                mainView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(DepthPickerActivity.this, PreviewActivity.class);
                                                    intent.putExtra("type", 1);
                                                    intent.putExtra("data_index", IntentHelper.getInstance().addItem(data));
                                                    startActivity(intent);
                                                }
                                            }
                );
            }
        }
    }
}
