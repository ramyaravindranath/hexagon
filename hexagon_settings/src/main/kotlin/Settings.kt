package com.hexagonkt.settings

import com.hexagonkt.logging.Logger
import com.hexagonkt.serialization.SerializationManager
import com.hexagonkt.serialization.convertToObject
import com.hexagonkt.serialization.serialize
import kotlin.reflect.KClass

class Settings<T : Any>(
    private val type: KClass<T>,
    private val sources: List<SettingsSource> = emptyList()
) {

    constructor(type: KClass<T>, vararg sources: SettingsSource) :
        this(type, sources.toList())

    private val log: Logger = Logger(this::class)

    val instance: T = loadSettings()

    private fun loadSettings(): T {

        val sourcesProperties = sources
            .map {
                it.load().also { s ->
                    if (s.isEmpty()) {
                        log.info { "No settings found for $it" }
                    }
                    else {
                        val serialize =
                            if (SerializationManager.defaultFormat == null) s.toString()
                            else s.serialize().prependIndent(" ".repeat(4))
                        log.info { "Settings loaded from $it:\n\n$serialize" }
                    }
                }
            }

        return if (sourcesProperties.isEmpty())
            emptyMap<Any, Any>().convertToObject(type)
        else
            sourcesProperties.reduce { a, b -> a + b }.convertToObject(type)
    }

    override fun toString(): String =
        "Settings wrapped in $type class"
}
