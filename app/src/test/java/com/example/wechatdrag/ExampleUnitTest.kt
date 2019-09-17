package com.example.wechatdrag

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Test
import kotlinx.serialization.serializerByTypeToken

import org.junit.Assert.*
import java.lang.reflect.Type

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun <T> addition_isCorrect() {
//        assertEquals(4, 2 + 2)
        TestSeri.serializer()
        Json.asConverterFactory()
    }
}

@Serializable
data class TestSeri(
    val name: String = "haha"
)

@JvmName("create")
fun StringFormat.asConverterFactory() {

}