/*
Copyright 2023. Davor Slamnig

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
package com.slamnig.recog.activity
*/

package com.slamnig.recog.persist

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONException

/**
 * Shared preferences helper.
 */
open class Prefs(context: Context, prefsName: String)
{
    private val PREFS_NAME = prefsName
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    protected fun setOptionBool(key: String, set: Boolean) {
        val ed = prefs.edit()
        ed.putBoolean(key, set)
        ed.commit()
    }

    protected fun getOptionBool(key: String, defSet: Boolean): Boolean {
        return prefs.getBoolean(key, defSet)
    }

    protected fun setOptionInt(key: String, value: Int) {
        val ed = prefs.edit()
        ed.putInt(key, value)
        ed.commit()
    }

    protected fun getOptionInt(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    protected fun setOptionLong(key: String?, value: Long) {
        val ed = prefs.edit()
        ed.putLong(key, value)
        ed.commit()
    }

    protected fun getOptionLong(key: String, defValue: Long): Long {
        return prefs.getLong(key, defValue)
    }

    protected fun setOptionFloat(key: String, value: Float) {
        val ed = prefs.edit()
        ed.putFloat(key, value)
        ed.commit()
    }

    protected fun getOptionFloat(key: String, defValue: Float): Float {
        return prefs.getFloat(key, defValue)
    }

    protected fun setOptionString(key: String, value: String?) {
        val ed = prefs.edit()
        ed.putString(key, value)
        ed.commit()
    }

    protected fun getOptionString(key: String, defValue: String?): String? {
        return prefs.getString(key, defValue)
    }

    protected fun setOptionLongArray(key: String, values: LongArray?) {
        val ed = prefs.edit()
        val a = JSONArray()

        if (values != null) {
            // first store array length:
            a.put(values.size)
            // then store array values:
            for (i in values.indices) a.put(values[i])
            ed.putString(key, a.toString())
        }
        else ed.putString(key, null)

        ed.commit()
    }

    protected fun getOptionLongArray(key: String): LongArray? {
        var values: LongArray? = null
        val json = prefs.getString(key, null)
        if (json != null) {
            try {
                val a = JSONArray(json)
                val length = a.optInt(0, 0)
                values = LongArray(length)
                for (i in 0 until length) values[i] = a.optLong(i + 1)
            } catch (e: JSONException) {
                e.printStackTrace()
                values = null
            }
        }
        return values
    }

    protected fun setOptionStringArray(key: String, values: Array<String?>?) {
        val ed = prefs.edit()
        val a = JSONArray()
        if (values != null) {
            // first store array length:
            a.put(values.size)
            // then store array values:
            for (i in values.indices) a.put(values[i])
            ed.putString(key, a.toString())
        } else {
            ed.putString(key, null)
        }
        ed.commit()
    }

    protected fun getOptionStringArray(key: String): Array<String?>? {
        val json = prefs.getString(key, null)
        var values: Array<String?>? = null
        if (json != null) {
            try {
                val a = JSONArray(json)
                val length = a.optInt(0, 0)
                values = arrayOfNulls(length)
                for (i in 0 until length) values[i] = a.optString(i + 1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return values
    }

    protected fun setOptionIntArrayList(key: String, values: ArrayList<Int?>?) {
        val ed = prefs.edit()
        val a = JSONArray()
        if (values != null && !values.isEmpty()) {
            for (i in values.indices) {
                a.put(values[i])
            }
            ed.putString(key, a.toString())
        } else {
            ed.putString(key, null)
        }
        ed.commit()
    }

    protected fun getOptionIntArrayList(key: String): ArrayList<Int>? {
        val json = prefs.getString(key, null)
        val values = ArrayList<Int>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val `val` = a.optInt(i)
                    values.add(`val`)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return values
    }

    open fun setOptionStringArrayList(key: String, values: ArrayList<String?>?) {
        val ed = prefs.edit()
        val a = JSONArray()
        if (values != null && !values.isEmpty()) {
            for (i in values.indices) {
                a.put(values[i])
            }
            ed.putString(key, a.toString())
        } else {
            ed.putString(key, null)
        }
        ed.commit()
    }

    open fun getOptionStringArrayList(key: String): ArrayList<String> {
        val json = prefs.getString(key, null)
        val values = ArrayList<String>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val `val` = a.optString(i, null)
                    if (`val` != null) values.add(`val`)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return values
    }

    open fun setOptionUriArrayList(key: String, uriList: ArrayList<Uri>?) {
        var k: Int
        val stringList = ArrayList<String?>()
        if (uriList != null) {
            k = 0
            while (k < uriList.size) {
                stringList.add(uriList[k].toString())
                ++k
            }
        }
        setOptionStringArrayList(key, stringList)
    }

    open fun getOptionUriArray(key: String): ArrayList<Uri>? {
        var k: Int
        val stringList = getOptionStringArrayList(key)
        val uriList = ArrayList<Uri>()
        k = 0
        while (k < stringList.size) {
            uriList.add(Uri.parse(stringList[k]))
            ++k
        }
        return uriList
    }

    open fun setOptionStringSet(key: String, values: Set<String?>?) {
        val ed = prefs.edit()
        ed.putStringSet(key, values)
        ed.commit()
    }

    open fun getOptionStringSet(key: String): Set<String?>? {
        var set: Set<String?>?
        set = try {
            prefs.getStringSet(key, null)
        } catch (e: ClassCastException) {
            null
        }
        return set
    }

    private val STRING_KEY_PREFIX = "OptionStringKey_"

    open fun setOptionKeyString(key: String, value: String?) {
        setOptionString(STRING_KEY_PREFIX + key, value)
    }

    open fun getOptionKeyString(key: String): String? {
        return getOptionString(STRING_KEY_PREFIX + key, null)
    }
}