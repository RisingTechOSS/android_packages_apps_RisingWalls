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

// Dump objects that you can't send though an intent here and pick them up in the new activity

import java.util.ArrayList;

public class IntentHelper {
    private ArrayList<Object> data;
    private static IntentHelper instance;

    public IntentHelper() {
        data = new ArrayList<>();
        instance = this;
    }

    public static IntentHelper getInstance() {
        return instance;
    }

    public int addItem(Object item) {
        int index = data.size();
        data.add(index, item);
        return index;
    }

    public Object getItem(int index) {
        return data.get(index);
    }
}
