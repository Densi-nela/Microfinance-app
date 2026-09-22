package com.shieldflow.claim.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class JsonMapConverter : AttributeConverter<Map<String, Any>, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, Any>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any>? {
        if (dbData.isNullOrBlank()) return emptyMap()
        return try {
            objectMapper.readValue(dbData, object : TypeReference<Map<String, Any>>() {})
        } catch (e: Exception) {
            emptyMap()
        }
    }
}

@Converter
class JsonListConverter : AttributeConverter<List<Map<String, Any>>, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<Map<String, Any>>?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): List<Map<String, Any>>? {
        if (dbData.isNullOrBlank()) return emptyList()
        return try {
            objectMapper.readValue(dbData, object : TypeReference<List<Map<String, Any>>>() {})
        } catch (e: Exception) {
            emptyList()
        }
    }
}
