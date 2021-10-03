package com.example.summer_part4_chapter05

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutinesTest01 {

    @Test
    fun test01() = runBlocking {

        val time = measureTimeMillis {
            val name = getFirstName()
            val lastName = getLastName()
            print("Hello, $name $lastName")
        }
        print("measure Time : $time")
    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis {
            val name = async { getFirstName() }
            val lastName = async { getLastName() }
            print("Hello, ${name.await()} ${lastName.await()}")
        }
        print("measure Time : $time")
    }

    suspend fun getFirstName(): String {
        delay(1000)
        return "안"
    }

    suspend fun getLastName(): String {
        delay(1000)
        return "영준"
    }
}