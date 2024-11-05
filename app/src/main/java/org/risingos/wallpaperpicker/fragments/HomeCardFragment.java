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

package org.risingos.wallpaperpicker.fragments;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.R;
import org.risingos.wallpaperpicker.activities.DepthPickerActivity;
import org.risingos.wallpaperpicker.activities.FlatPickerActivity;
import org.risingos.wallpaperpicker.activities.PreviewActivity;
import org.risingos.wallpaperpicker.jsonparser.objecs.homepage.HomepageObject;
import org.risingos.wallpaperpicker.utils.IntentHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class HomeCardFragment extends Fragment {
    private String title;
    private int id;
    private Activity activity;

    public HomeCardFragment() {
        super();
    }

    public void setObject(HomepageObject object) {
        ImageView imageView = getView().findViewById(R.id.background_image);
        object.setImageView(imageView);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.homecard_text_title)).setText(title);


        MainApplication.getInstance().registerHomeFragment(this, id);


        activity = getActivity();

        switch (id) {
            case 0:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePickerActivityResultLauncher.launch(Intent.createChooser(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), "Select Image"));
                    }
                });
                break;
            case 1:
                view.setOnClickListener( new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                                            startActivity(intent);
                                        }
                                    }
                );
                break;
            case 2:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, FlatPickerActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 3:
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, DepthPickerActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }
    }

    private ActivityResultLauncher<Intent> imagePickerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        InputStream inputStream = null;

                        Intent intent = new Intent(activity, PreviewActivity.class);
                        try {
                            inputStream = activity.getContentResolver().openInputStream(result.getData().getData());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        intent.putExtra("type", 0);
                        intent.putExtra("inputstream_index", IntentHelper.getInstance().addItem(inputStream));
                        startActivity(intent);
                    }
                }
            });

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HomeCardFragment, 0, 0);
        id = a.getInt(R.styleable.HomeCardFragment_id, 0);
        title = a.getString(R.styleable.HomeCardFragment_label);
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_wallpaper_card_home, container, false);
    }
}
