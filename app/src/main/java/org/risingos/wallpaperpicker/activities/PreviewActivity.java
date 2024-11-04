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

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.palette.graphics.Palette;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.risingos.wallpaperpicker.MainApplication;
import org.risingos.wallpaperpicker.R;
import org.risingos.wallpaperpicker.jsonparser.objecs.depth.DepthWallpaper;
import org.risingos.wallpaperpicker.jsonparser.objecs.flat.FlatWallpaper;
import org.risingos.wallpaperpicker.utils.IntentHelper;
import org.risingos.wallpaperpicker.utils.NetworkUtils;
import org.risingos.wallpaperpicker.utils.SystemBarUtils;
import org.risingos.wallpaperpicker.utils.WallpaperUtils;
import org.risingos.wallpaperpicker.views.ImageButtonView;
import org.risingos.wallpaperpicker.views.TextButtonView;

public class PreviewActivity extends FragmentActivity {
    private String resolutionText;
    private ImageView previewImage;
    private Bitmap wallpaper;
    private ApplyDialog applyDialog;
    private InfoDialog infoDialog;
    private String wallpaperName;
    private TextView nameText;
    private TextView authorText;
    private ImageView arrowBack;
    private ImageButtonView saveButton;
    private boolean isDepth;
    private Bitmap subject;
    private int type;
    String wallpaperAuthor = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);
        previewImage = findViewById(R.id.preview_image_background);
        nameText = findViewById(R.id.title_preview);
        authorText = findViewById(R.id.text_author);
        arrowBack = findViewById(R.id.arrow_back);
        saveButton = findViewById(R.id.button_save);

        SystemBarUtils.setHeightOfViewToStatusBarHeight(this, findViewById(R.id.statusbar_space));
        SystemBarUtils.setHeightOfViewToNavBarHeight(this, findViewById(R.id.navbar_space));

        Bundle extras = getIntent().getExtras();
        IntentHelper intentHelper = IntentHelper.getInstance();
        type = extras.getInt("type");

        switch (type) {
            case 0:
                setBitmap((Bitmap) intentHelper.getItem(extras.getInt("bitmap_index")));
                wallpaperName = getString(R.string.image_from_gallery);
                ((ViewGroup) saveButton.getParent()).removeView(saveButton);
                ((ViewGroup) authorText.getParent()).removeView(authorText);
                break;

            case 1:
                FlatWallpaper wallpaperData = (FlatWallpaper) intentHelper.getItem(extras.getInt("data_index"));
                MainApplication.getInstance().getThreadPoolExecutor().execute(new NetworkUtils.NetworkRunnable("Bitmap", wallpaperData.getWallpaper(), new NetworkUtils.onFetchCompleteCallback() {
                    @Override
                    public void onFetchComplete(Object fetchedData) {
                        setBitmap((Bitmap) fetchedData);
                    }
                }));
                wallpaperAuthor = wallpaperData.getAuthor();
                wallpaperName = wallpaperData.getTitle();

                isDepth = wallpaperData instanceof DepthWallpaper;

                if (isDepth) {
                    MainApplication.getInstance().getThreadPoolExecutor().execute(new NetworkUtils.NetworkRunnable("Bitmap", ((DepthWallpaper) wallpaperData).getSubject(), new NetworkUtils.onFetchCompleteCallback() {
                        @Override
                        public void onFetchComplete(Object fetchedData) {
                            subject = (Bitmap) fetchedData;
                        }
                    }));
                }
                break;

            case 2:
                break;
        }

        applyDialog = new ApplyDialog(this);
        applyDialog.setContentView(R.layout.diag_apply);
        applyDialog.setCancelable(true);
        applyDialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        applyDialog.init();

        infoDialog = new InfoDialog(this);
        infoDialog.setContentView(R.layout.diag_info);
        infoDialog.setCancelable(true);
        infoDialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        infoDialog.init();

        if (type != 0)
            authorText.setText(getString(R.string.by) + wallpaperAuthor);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nameText.setText(wallpaperName);
    }

    private void setBitmap(Bitmap bitmap) {
        resolutionText = bitmap.getWidth() + "x" + bitmap.getHeight();
        previewImage.setImageBitmap(bitmap);
        wallpaper = bitmap;

        ImageButtonView applyButton = findViewById(R.id.button_apply);
        ImageButtonView infoButton = findViewById(R.id.button_info);

        applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    applyDialog.show();
                }
            }
        );

        infoButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               infoDialog.show();
                                           }
                                       }
        );

        saveButton.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  WallpaperUtils.saveImage(bitmap, wallpaperName);
                                                              }
                                                          }
        );

        MainApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
                int scaleColor = mBitmap.getPixel(0, 0);
                mBitmap.recycle();

                int isDarkMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                int baseColor = Palette.from(bitmap).generate().getDominantColor(scaleColor);
                int colorLight = setColorBrightness(baseColor, 1f);
                int colorDark = setColorBrightness(baseColor, 0.3f);
                int backgroundColor = setColorBrightness(baseColor, 0.11f);
                int colorLightVibrant = setColorBrightness(baseColor, 0.7f);

                CardView card_title_background = findViewById(R.id.card_title_background);

                int colorBackgroundLight = Color.argb(Color.alpha(colorLight), (int) Math.min(Math.round(Color.red(colorLight) * 1.1),255), (int) Math.min((Color.green(colorLight) * 1.1),255), (int) Math.min((Color.blue(colorLight) * 1.1),255));

                MainApplication.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (isDarkMode) {
                            case Configuration.UI_MODE_NIGHT_NO:
                                card_title_background.getBackground().setTint(colorBackgroundLight);

                                nameText.setTextColor(colorDark);
                                authorText.setTextColor(colorDark);

                                arrowBack.getDrawable().setTint(colorDark);

                                break;

                            case Configuration.UI_MODE_NIGHT_YES:
                                card_title_background.getBackground().setTint(colorDark);

                                nameText.setTextColor(colorLight);
                                authorText.setTextColor(colorLight);

                                arrowBack.getDrawable().setTint(colorLight);

                                break;
                        }

                        saveButton.setBackgroundColor(colorLight);
                        applyButton.setBackgroundColor(colorLight);
                        infoButton.setBackgroundColor(colorLight);

                        saveButton.setDrawableColor(colorDark);
                        applyButton.setDrawableColor(colorDark);
                        infoButton.setDrawableColor(colorDark);

                        applyDialog.setColors(colorBackgroundLight, backgroundColor, colorDark, colorLight, colorLightVibrant, isDarkMode);
                        infoDialog.setColors(colorBackgroundLight, backgroundColor, colorDark, colorLight, baseColor, isDarkMode);
                    }
                });
            }
        });
    }

    private int setColorBrightness(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = factor;
        return Color.HSVToColor(hsv);
    }

    private class ApplyDialog extends Dialog {
        private boolean cardStates[] = new boolean[2];

        private int lightColor;
        private int darkColor;

        private TextButtonView button_apply;
        private TextButtonView button_cancel;

        private Drawable windowDrawable = getDrawable(R.drawable.background_diag);

        public ApplyDialog(@NonNull Context context) {
            super(context);
        }

        public void init() {
            ImageView card_home_background = findViewById(R.id.card_home_background);
            ImageView card_home_icon = findViewById(R.id.card_home_icon);
            ImageView card_lock_background = findViewById(R.id.card_lock_background);
            ImageView card_lock_icon = findViewById(R.id.card_lock_icon);

            TextView text_lock = findViewById(R.id.text_lock);
            TextView text_home = findViewById(R.id.text_home);

            CardView card_home = findViewById(R.id.card_home);
            CardView card_lock = findViewById(R.id.card_lock);

            button_cancel = findViewById(R.id.button_cancel);
            button_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                }
            );

            button_apply = findViewById(R.id.button_apply_diag);
            button_apply.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!cardStates[0] & !cardStates[1]) {
                            Toast.makeText(PreviewActivity.this, "Please select something to apply to", Toast.LENGTH_LONG).show();
                            return;
                        }

                        setContentView(R.layout.diag_applying);
                        TextView text_title = findViewById(R.id.text_title);
                        text_title.setTextColor(lightColor);

                        LinearProgressIndicator progress_apply = findViewById(R.id.progress_apply);
                        progress_apply.setTrackColor(darkColor);
                        progress_apply.setIndicatorColor(lightColor);

                        WallpaperUtils.OnSetCompleteCallback callback = new WallpaperUtils.OnSetCompleteCallback() {
                            @Override
                            public void onSetComplete() {
                                dismiss();
                                Intent intent = new Intent(PreviewActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        };

                        if (isDepth & cardStates[0]) {
                            new WallpaperUtils(PreviewActivity.this).setDepthWallpaper(wallpaper, subject, cardStates, callback);
                        } else {
                            new WallpaperUtils(PreviewActivity.this).setFlatWallpaper(wallpaper, cardStates, false, callback);
                        }
                    }
                }
            );

            card_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchCardState(card_home_background, card_home_icon, text_home, 1);
                }
            });

            card_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchCardState(card_lock_background, card_lock_icon, text_lock, 0);
                }
            });
        }

        private void switchCardState(ImageView background, ImageView icon, TextView textView, int cardNum) {
            cardStates[cardNum] = !cardStates[cardNum];

            LayerDrawable backgroundDrawableTmp = (LayerDrawable) background.getDrawable();
            Drawable backgroundDrawable = backgroundDrawableTmp.getDrawable(0);

            Drawable iconDrawable = icon.getDrawable();

            ValueAnimator colorChangeAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), cardStates[cardNum] ? darkColor : lightColor, cardStates[cardNum] ? lightColor : darkColor);
            colorChangeAnimation.setDuration(250);
            colorChangeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();

                    backgroundDrawable.setTint(value);
                    iconDrawable.setTint(value);
                    textView.setTextColor(value);
                }
            });
            colorChangeAnimation.start();
        }

        // This part is very shitty
        public void setColors(int backgroundColorLight, int mBackgroundColor, int mLightColor, int mDarkColor, int lightColorVibrant, int isDarkMode) {
            ImageView card_home_background = findViewById(R.id.card_home_background);
            ImageView card_home_icon = findViewById(R.id.card_home_icon);
            ImageView card_lock_background = findViewById(R.id.card_lock_background);
            ImageView card_lock_icon = findViewById(R.id.card_lock_icon);
            TextView text_lock = findViewById(R.id.text_lock);
            TextView text_home = findViewById(R.id.text_home);
            TextView text_title = findViewById(R.id.text_title);

            LayerDrawable layerDrawable;

            switch (isDarkMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    lightColor = mLightColor;
                    darkColor = lightColorVibrant;

                    windowDrawable.setTint(backgroundColorLight);

                    text_title.setTextColor(mBackgroundColor);
                    text_home.setTextColor(darkColor);
                    text_lock.setTextColor(darkColor);

                    layerDrawable = (LayerDrawable) card_home_background.getDrawable();
                    layerDrawable.getDrawable(0).setTint(darkColor);
                    layerDrawable.getDrawable(1).setTint(backgroundColorLight);

                    layerDrawable = (LayerDrawable) card_lock_background.getDrawable();
                    layerDrawable.getDrawable(0).setTint(darkColor);
                    layerDrawable.getDrawable(1).setTint(backgroundColorLight);

                    button_apply.setTextColor(mBackgroundColor);
                    button_cancel.setTextColor(mLightColor);

                    button_apply.setBackgroundColor(lightColorVibrant);
                    button_cancel.setBackgroundColor(mDarkColor);

                    card_home_icon.getDrawable().setTint(darkColor);
                    card_lock_icon.getDrawable().setTint(darkColor);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    lightColor = mDarkColor;
                    darkColor = mLightColor;

                    windowDrawable.setTint(mBackgroundColor);

                    text_title.setTextColor(backgroundColorLight);
                    text_home.setTextColor(darkColor);
                    text_lock.setTextColor(darkColor);

                    layerDrawable = (LayerDrawable) card_home_background.getDrawable();
                    layerDrawable.getDrawable(0).setTint(darkColor);
                    layerDrawable.getDrawable(1).setTint(mBackgroundColor);

                    layerDrawable = (LayerDrawable) card_lock_background.getDrawable();
                    layerDrawable.getDrawable(0).setTint(darkColor);
                    layerDrawable.getDrawable(1).setTint(mBackgroundColor);

                    button_apply.setTextColor(mBackgroundColor);
                    button_cancel.setTextColor(mBackgroundColor);

                    button_apply.setBackgroundColor(mDarkColor);
                    button_cancel.setBackgroundColor(mLightColor);

                    card_home_icon.getDrawable().setTint(darkColor);
                    card_lock_icon.getDrawable().setTint(darkColor);
                    break;
            }
            getWindow().getDecorView().setBackground(windowDrawable);
        }
    }

    private class InfoDialog extends Dialog {
        public InfoDialog(@NonNull Context context) {
            super(context);
        }

        private Drawable windowDrawable = getDrawable(R.drawable.background_diag);

        public void init() {
            String typeString = null;

            if (type == 0) {
                typeString = getString(R.string.image_from_gallery);

                ViewGroup mainConstraintLayout = (ViewGroup) findViewById(R.id.line0).getParent();
                mainConstraintLayout.removeView(findViewById(R.id.line0));
                mainConstraintLayout.removeView(findViewById(R.id.text_name));
                mainConstraintLayout.removeView(findViewById(R.id.text_name_description));
                mainConstraintLayout.removeView(findViewById(R.id.line1));
                mainConstraintLayout.removeView(findViewById(R.id.text_author));
                mainConstraintLayout.removeView(findViewById(R.id.text_author_description));

            } else if (type == 1) {
                ((TextView) findViewById(R.id.text_name)).setText(wallpaperName);
                ((TextView) findViewById(R.id.text_author)).setText(wallpaperAuthor);

                if (isDepth)
                    typeString = getString(R.string.depth_wallpaper);
                else
                    typeString = getString(R.string.rising_wallpaper);
            }

            ((TextView) findViewById(R.id.text_type)).setText(typeString);
        }

        public void setColors(int backgroundColorLight, int mBackgroundColor, int darkColor, int lightColor, int baseColor, int isDarkMode) {
            int linesColor = 0;
            int textColor = 0;
            int backgroundColor = 0;

            switch (isDarkMode) {
                case Configuration.UI_MODE_NIGHT_NO:

                    linesColor = lightColor;
                    textColor = mBackgroundColor;
                    backgroundColor = backgroundColorLight;

                    break;
                case Configuration.UI_MODE_NIGHT_YES:

                    linesColor = darkColor;
                    textColor = lightColor;
                    backgroundColor = mBackgroundColor;

                    break;
            }

            findViewById(R.id.line0).setBackgroundColor(linesColor);
            findViewById(R.id.line1).setBackgroundColor(linesColor);
            findViewById(R.id.line2).setBackgroundColor(linesColor);
            findViewById(R.id.line3).setBackgroundColor(linesColor);
            findViewById(R.id.line4).setBackgroundColor(linesColor);

            if (type != 0) {
                ((TextView) findViewById(R.id.text_name)).setTextColor(textColor);
                ((TextView) findViewById(R.id.text_name_description)).setTextColor(textColor);

                ((TextView) findViewById(R.id.text_author)).setTextColor(textColor);
                ((TextView) findViewById(R.id.text_author_description)).setTextColor(textColor);
            }

            ((TextView) findViewById(R.id.text_title_info)).setTextColor(textColor);

            ((TextView) findViewById(R.id.text_type)).setTextColor(textColor);
            ((TextView) findViewById(R.id.text_type_description)).setTextColor(textColor);

            ((TextView) findViewById(R.id.text_color_description)).setTextColor(textColor);

            ((TextView) findViewById(R.id.text_resolution)).setTextColor(textColor);
            ((TextView) findViewById(R.id.text_resolution_description)).setTextColor(textColor);

            String dominantColorString = "#" + Integer.toHexString(baseColor & 0x00FFFFFF);

            View text_color_background = findViewById(R.id.text_color_background);
            text_color_background.getBackground().setTint(baseColor);
            text_color_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText(dominantColorString, dominantColorString));
                }
            });
            ((TextView) findViewById(R.id.text_color)).setText(dominantColorString);


            windowDrawable.setTint(backgroundColor);
            getWindow().getDecorView().setBackground(windowDrawable);
        }

        @Override
        public void show() {
            ((TextView) findViewById(R.id.text_resolution)).setText(resolutionText);
            super.show();
        }
    }
}
