package com.example.wechatdrag

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

class GsonTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        {"A":null,"B":null,"C":null,"D":null,"E":null,"F":null,"G":null,"H":null,"I":null,"J":null}
        val testJson = """
            null
        """.trimIndent()
        val newGson = GsonBuilder().registerTypeAdapterFactory(KotlinAdapterFactory()).create()
        val person = newGson.fromJson(testJson, Person::class.java)
        println(person)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@GsonTestActivity, TestActivity::class.java))
        }, 2000)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        println("GsonTestActivity onDestroy")
        super.onDestroy()
    }
}

data class Person(
    val A: Int = 0,
    val B: Char = '0',
    val C: Short = 0,
    val D: Byte = 0,
    val E: Boolean = false,
    val F: Long = 0,
    val G: Float = .0F,
    val H: Double = .0,
    val I: String = "",
    val J: List<InnerPerson> = arrayListOf()
)

data class InnerPerson(
    var A: String = "",
    var B: Int = 0
) {
}

class KotlinAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val kClass = (type.rawType as Class<T>).kotlin
        val delegateAdapter = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            override fun write(writer: JsonWriter, value: T) {
                delegateAdapter.write(writer, value)
            }

            override fun read(reader: JsonReader): T? {
                return delegateAdapter.read(reader)?.apply {
                    kClass.declaredMemberProperties.forEach { prop ->
                        prop.isAccessible = true
                        if (!prop.returnType.isMarkedNullable && prop(this) == null) {
                            when (val kClassType = prop.returnType.classifier as KClass<*>) {
                                Int::class, Short::class -> {
                                    prop.javaField?.set(this, 0)
                                }
                                Short::class -> {
                                    prop.javaField?.set(this, 0.toShort())
                                }
                                Long::class -> {
                                    prop.javaField?.set(this, 0L)
                                }
                                Char::class -> {
                                    prop.javaField?.set(this, '\u0000')
                                }
                                Boolean::class -> {
                                    prop.javaField?.set(this, false)
                                }
                                Byte::class -> {
                                    prop.javaField?.set(this, 0.toByte())
                                }
                                Float::class -> {
                                    prop.javaField?.set(this, .0F)
                                }
                                Double::class -> {
                                    prop.javaField?.set(this, .0)
                                }
                                String::class -> {
                                    prop.javaField?.set(this, "")
                                }
                                List::class -> {
                                    prop.javaField?.set(this, ArrayList<Any>())
                                }
                                else -> {
                                    prop.javaField?.set(this, kClassType.createInstance())
                                }
                            }
                        }
                    }
                } ?: kClass.createInstance()
            }
        }
    }
}