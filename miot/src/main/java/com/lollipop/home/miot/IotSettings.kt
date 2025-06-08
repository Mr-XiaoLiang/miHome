package com.lollipop.home.miot

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

object IotSettings {

    private var preferences: SharedPreferences? = null

    private fun opt(context: Context): SharedPreferences {
        return preferences ?: context.getSharedPreferences(
            "lollipop_miot", Context.MODE_PRIVATE
        ).apply {
            preferences = this
        }
    }

    fun init(context: Context) {
        opt(context)
    }



    private fun stringType(def: String = "") = StringType(def)
    private fun intType(def: Int = 0) = IntType(def)
    private fun longType(def: Long = 0L) = LongType(def)
    private fun floatType(def: Float = 0f) = FloatType(def)
    private fun booleanType(def: Boolean = false) = BooleanType(def)

    private abstract class Delegate<T>(protected val def: T) {

        // 获取值
        operator fun getValue(settings: IotSettings, property: KProperty<*>): T {
            val name = property.name
            val preferences = settings.preferences
            if (preferences == null) {
                return def
            }
            return optValue(preferences, name, def)
        }

        // 设置值（可选）
        operator fun setValue(settings: IotSettings, property: KProperty<*>, value: T) {
            settings.preferences?.edit {
                setValue(this, property.name, value)
            }
        }

        protected abstract fun optValue(
            sharedPreferences: SharedPreferences,
            key: String,
            def: T
        ): T

        protected abstract fun setValue(
            editor: SharedPreferences.Editor,
            key: String,
            value: T
        )

    }

    private class StringType(def: String) : Delegate<String>(def) {
        override fun optValue(
            sharedPreferences: SharedPreferences,
            key: String,
            def: String
        ): String {
            return sharedPreferences.getString(key, def) ?: def
        }

        override fun setValue(
            editor: SharedPreferences.Editor,
            key: String,
            value: String
        ) {
            editor.putString(key, value)
        }
    }

    private class IntType(def: Int) : Delegate<Int>(def) {
        override fun optValue(
            sharedPreferences: SharedPreferences,
            key: String,
            def: Int
        ): Int {
            return sharedPreferences.getInt(key, def) ?: def
        }

        override fun setValue(
            editor: SharedPreferences.Editor,
            key: String,
            value: Int
        ) {
            editor.putInt(key, value)
        }
    }

    private class LongType(def: Long) : Delegate<Long>(def) {
        override fun optValue(
            sharedPreferences: SharedPreferences,
            key: String,
            def: Long
        ): Long {
            return sharedPreferences.getLong(key, def) ?: def
        }

        override fun setValue(
            editor: SharedPreferences.Editor,
            key: String,
            value: Long
        ) {
            editor.putLong(key, value)
        }
    }

    private class FloatType(def: Float) : Delegate<Float>(def) {
        override fun optValue(
            sharedPreferences: SharedPreferences,
            key: String,
            def: Float
        ): Float {
            return sharedPreferences.getFloat(key, def) ?: def
        }

        override fun setValue(
            editor: SharedPreferences.Editor,
            key: String,
            value: Float
        ) {
            editor.putFloat(key, value)
        }
    }

    private class BooleanType(def: Boolean) : Delegate<Boolean>(def) {
        override fun optValue(
            sharedPreferences: SharedPreferences,
            key: String,
            def: Boolean
        ): Boolean {
            return sharedPreferences.getBoolean(key, def) ?: def
        }

        override fun setValue(
            editor: SharedPreferences.Editor,
            key: String,
            value: Boolean
        ) {
            editor.putBoolean(key, value)
        }
    }

}