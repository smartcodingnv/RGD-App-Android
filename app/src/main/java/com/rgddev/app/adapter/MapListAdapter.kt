/*
 * Copyright (c) 2011-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rgddev.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.here.android.mpa.odml.MapPackage
import com.rgddev.app.R

internal class MapListAdapter(
    context: Context,
    resource: Int,
    private val m_list: List<MapPackage>
) : ArrayAdapter<MapPackage>(context, resource, m_list) {

    override fun getCount(): Int {
        return m_list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val mapPackage = m_list[position]
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.item_maplist, parent,
                false
            )
        }

        /*
         * Display title and size information of each map package.Please refer to HERE Android SDK
         * API doc for all supported APIs.
         */
        var tv = convertView!!.findViewById<View>(R.id.mapPackageName) as TextView
        tv.text = mapPackage.title
        tv = convertView.findViewById<View>(R.id.mapPackageState) as TextView

        if (mapPackage.installationState != null) {
            when (mapPackage.installationState) {
                MapPackage.InstallationState.INSTALLED -> {
                    tv.text = context.getString(R.string.lable_installed)
                }
                MapPackage.InstallationState.NOT_INSTALLED -> {
                    tv.text = context.getString(R.string.lable_not_installed)
                }
                MapPackage.InstallationState.PARTIALLY_INSTALLED -> {
                    tv.text = context.getString(R.string.lable_partially_installed)
                }
            }
        }


        tv = convertView.findViewById<View>(R.id.mapPackageSize) as TextView
        tv.text = (mapPackage.size / 1024).toString() + "MB"
        return convertView
    }
}
