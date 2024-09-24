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

package org.risingos.wallpaperpicker.jsonparser.objecs.depth;

import java.util.List;

public class DepthWallpaperManifest {
    int target_level;
    int target_sublevel;
    List<DepthWallpaper> wallpapers;

    public int getTarget_level() {
        return target_level;
    }

    public int getTarget_sublevel() {
        return target_sublevel;
    }

    public List<DepthWallpaper> getWallpapers() {
        return wallpapers;
    }

    public void setTarget_level(int target_level) {
        this.target_level = target_level;
    }

    public void setTarget_sublevel(int target_sublevel) {
        this.target_sublevel = target_sublevel;
    }

    public void setWallpapers(List<DepthWallpaper> wallpapers) {
        this.wallpapers = wallpapers;
    }
}
