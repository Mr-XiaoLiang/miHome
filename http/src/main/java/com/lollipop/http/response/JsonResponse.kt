package com.lollipop.http.response

import com.lollipop.http.safe.SafeResult
import com.lollipop.http.safe.safeMap
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KProperty

inline fun <reified J : JsonResponse> SafeResult<ResponseParser>.json(): SafeResult<J> {
    return safeMap {
        val body = it.body
        val jsonObj = if (body == null) {
            JSONObject()
        } else {
            JSONObject(body.string())
        }
        val constructor = J::class.java.getDeclaredConstructor()
        val newInstance = constructor.newInstance()
        newInstance.update(jsonObj)
        newInstance
    }
}

open class JsonResponse() {

    var json: JSONObject? = null
        protected set

    fun update(json: JSONObject) {
        this.json = json
    }

    fun opt(name: String, def: String): String {
        return json?.optString(name, def) ?: def
    }

    fun opt(name: String, def: Int): Int {
        return json?.optInt(name, def) ?: def
    }

    fun opt(name: String, def: Long): Long {
        return json?.optLong(name, def) ?: def
    }

    fun opt(name: String, def: Double): Double {
        return json?.optDouble(name, def) ?: def
    }

    fun opt(name: String, def: Boolean): Boolean {
        return json?.optBoolean(name, def) ?: def
    }

    fun optArray(name: String): JSONArray {
        return json?.optJSONArray(name) ?: JSONArray()
    }

    fun optObj(name: String): JsonResponse {
        return json?.optJSONObject(name)?.let {
            val response = JsonResponse()
            response.update(it)
            response
        } ?: JsonResponse()
    }

    protected fun stringValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        def: String = ""
    ) = StringType(name, alias, def)

    protected fun intValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        def: Int = 0
    ) = IntType(name, alias, def)

    protected fun longValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        def: Long = 0L
    ) = LongType(name, alias, def)

    protected fun doubleValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        def: Double = 0.0
    ) = DoubleType(name, alias, def)

    protected fun booleanValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        def: Boolean = false
    ) = BooleanType(name, alias, def)

    protected fun <T : JsonResponse> objectValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        instanceProvider: () -> T
    ) = ObjectType(name, alias, instanceProvider)

    protected inline fun <reified T : JsonResponse> objectValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
    ) = objectValue(name, alias) {
        val constructor = T::class.java.getDeclaredConstructor()
        constructor.newInstance()
    }

    protected fun arrayValue(
        name: String = "",
        alias: Array<String> = arrayOf(),
        def: JSONArray = JSONArray()
    ) = ArrayType(name, alias, def)

    protected abstract class Delegate<T>(
        protected val name: String,
        protected val alias: Array<String>,
        protected val def: T
    ) {

        protected fun optKeyName(property: KProperty<*>): String {
            if (name.isEmpty()) {
                return property.name
            }
            return name
        }

        // 获取值
        operator fun getValue(jsonResponse: JsonResponse, property: KProperty<*>): T {
            val json = jsonResponse.json ?: return def
            val mainKey = optKeyName(property)
            if (json.has(mainKey)) {
                return optValue(json, mainKey, def)
            }
            for (aliasName in alias) {
                if (json.has(aliasName)) {
                    return optValue(json, aliasName, def)
                }
            }
            return def
        }

        // 设置值（可选）
        operator fun setValue(jsonResponse: JsonResponse, property: KProperty<*>, value: T) {
            jsonResponse.json?.put(optKeyName(property), value)
        }

        protected abstract fun optValue(json: JSONObject, key: String, def: T): T

    }

    protected class StringType(
        name: String, alias: Array<String>, def: String
    ) : Delegate<String>(name, alias, def) {
        override fun optValue(json: JSONObject, key: String, def: String): String {
            return json.optString(key, def)
        }
    }

    protected class IntType(
        name: String, alias: Array<String>, def: Int
    ) : Delegate<Int>(name, alias, def) {
        override fun optValue(json: JSONObject, key: String, def: Int): Int {
            return json.optInt(key, def)
        }
    }

    protected class LongType(
        name: String, alias: Array<String>, def: Long
    ) : Delegate<Long>(name, alias, def) {
        override fun optValue(json: JSONObject, key: String, def: Long): Long {
            return json.optLong(key, def)
        }
    }

    protected class DoubleType(
        name: String, alias: Array<String>, def: Double
    ) : Delegate<Double>(name, alias, def) {
        override fun optValue(json: JSONObject, key: String, def: Double): Double {
            return json.optDouble(key, def)
        }
    }

    protected class BooleanType(
        name: String, alias: Array<String>, def: Boolean
    ) : Delegate<Boolean>(name, alias, def) {
        override fun optValue(json: JSONObject, key: String, def: Boolean): Boolean {
            return json.optBoolean(key, def)
        }
    }

    protected class ArrayType(
        name: String, alias: Array<String>, def: JSONArray
    ) : Delegate<JSONArray>(name, alias, def) {
        override fun optValue(json: JSONObject, key: String, def: JSONArray): JSONArray {
            return json.optJSONArray(key) ?: def
        }
    }

    protected class ObjectType<T : JsonResponse>(
        name: String, alias: Array<String>, instanceProvider: () -> T
    ) : Delegate<T>(name, alias, instanceProvider()) {

        private var currentInstance: T? = null

        override fun optValue(json: JSONObject, key: String, def: T): T {
            val instance = currentInstance
            if (instance != null) {
                return instance
            }
            currentInstance = def
            val obj = json.optJSONObject(key)
            if (obj != null) {
                def.update(obj)
            }
            return def
        }
    }

}

