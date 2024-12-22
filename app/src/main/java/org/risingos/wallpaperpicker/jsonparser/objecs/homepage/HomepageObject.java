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

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class HomepageObject {
    private String image;

    public String getImage() {
        return image;
    }

    public void notifyParseComplete(Context context) {
        Glide.with(context).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).preload();
    }
}
