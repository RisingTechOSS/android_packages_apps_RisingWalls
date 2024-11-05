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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lineageos.providers.LineageSettings;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.R;
import org.risingos.wallpaperpicker.jsonparser.objecs.homepage.HomepageManifest;
import org.risingos.wallpaperpicker.utils.RecentsUtils;
import org.risingos.wallpaperpicker.utils.SystemBarUtils;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        MainApplication.getInstance().registerMainActivityInstance(this);

        SystemBarUtils.setHeightOfViewToStatusBarHeight(this, findViewById(R.id.statusbar_space));

        if (LineageSettings.System.getInt(getContentResolver(), "navigation_bar_hint", 1) == 1)
            SystemBarUtils.setHeightOfViewToNavBarHeight(this, findViewById(R.id.navbar_space));

        ((TextView) findViewById(R.id.text_title)).setText(getText(R.string.hi) + " " + userManager.getUserName());

        findViewById(R.id.credits_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView resentsView = findViewById(R.id.recents);
        resentsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        resentsView.setAdapter(new RecentsAdapter());

        View resentsScroll = findViewById(R.id.recents_scroll);
        ((ViewGroup) resentsScroll.getParent()).removeView(resentsScroll);
        View lastViewed = findViewById(R.id.last_viewed_title);
        ((ViewGroup) lastViewed.getParent()).removeView(lastViewed);

    }

    public void setHomepageManifest(HomepageManifest homepageManifest) {
        int target_level = homepageManifest.getTarget_level();
        int compatible_target_level = getResources().getInteger(R.integer.config_target_level);

        if (target_level != compatible_target_level) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.manifest_incompatible_error);

            String description = getString(R.string.manifest_incompatible_description) + "\n" +
                    "\n" +
                    getString(R.string.compatible_version) + " " + compatible_target_level + "\n" +
                    getString(R.string.found_version) + " " + target_level + "\n" +
                    "\n" +
                    getString(R.string.attempt_normal_function);

            builder.setPositiveButton(R.string.ok, null);
            builder.setMessage(description);
            builder.setCancelable(true);
            builder.create().show();
        }
    }

    private class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.ViewHolder> {
        private ArrayList<RecentsUtils.RecentsData> localDataSet = new RecentsUtils().getDataSet();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d");

        @NonNull
        @Override
        public RecentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recents_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecentsAdapter.ViewHolder holder, final int position) {
            holder.setData(localDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return localDataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView titleView;
            private TextView dateView;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                titleView = itemView.findViewById(R.id.text_recents_title);
                dateView = itemView.findViewById(R.id.text_recents_date);
            }

            public void setData(RecentsUtils.RecentsData data) {
                titleView.setText(data.localDataSetEntry);
                dateView.setText(dateFormatter.format(data.localDataSetDate));
            }
        }
    }
}
