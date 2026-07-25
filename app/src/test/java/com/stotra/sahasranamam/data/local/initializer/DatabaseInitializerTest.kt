package com.stotra.sahasranamam.data.local.initializer

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DatabaseInitializerTest {

    private val gson = Gson()

    @Test
    fun parseAdityaHrudayamJson_validatesJsonStructure() {
        val assetFile = File("src/main/assets/stotras/aditya_hrudayam.json")
        assertTrue("Asset file aditya_hrudayam.json must exist", assetFile.exists())

        val jsonContent = assetFile.readText()
        val jsonMap = gson.fromJson(jsonContent, Map::class.java)

        assertNotNull(jsonMap["stotra"])
        val stotraObj = jsonMap["stotra"] as Map<*, *>
        assertEquals("aditya_hrudayam", stotraObj["id"])

        val shlokas = jsonMap["shlokas"] as List<*>
        assertTrue("Aditya Hrudayam shlokas list must not be empty", shlokas.isNotEmpty())
    }

    @Test
    fun parseSriSuktamJson_validates16VersesStructure() {
        val assetFile = File("src/main/assets/stotras/sri_suktam.json")
        assertTrue("Asset file sri_suktam.json must exist", assetFile.exists())

        val jsonContent = assetFile.readText()
        val jsonMap = gson.fromJson(jsonContent, Map::class.java)

        assertNotNull(jsonMap["stotra"])
        val stotraObj = jsonMap["stotra"] as Map<*, *>
        assertEquals("sri_suktam", stotraObj["id"])

        val shlokas = jsonMap["shlokas"] as List<*>
        assertEquals(16, shlokas.size)
    }
}
