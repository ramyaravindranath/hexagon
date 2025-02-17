package com.hexagonkt.settings

import com.hexagonkt.serialization.JacksonMapper
import com.hexagonkt.serialization.Json
import com.hexagonkt.serialization.SerializationManager
import com.hexagonkt.serialization.Yaml
import org.junit.jupiter.api.Test

internal class HexagonSettingsSamplesTest {

    @Test fun settingsUsage() {

        SerializationManager.formats = linkedSetOf(Json, Yaml)
        SerializationManager.mapper = JacksonMapper

        // settingsUsage
        data class Configuration(
            val stringProperty: String,
            val integerProperty: Int,
            val booleanProperty: Boolean,
        )

        SettingsManager.settings = Settings(
            Configuration::class,
            ObjectSource(
                "stringProperty" to "str",
                "integerProperty" to 101,
                "booleanProperty" to true
            )
        )

        val configuration = SettingsManager.instance<Configuration>()
        assert(configuration.stringProperty == "str")
        assert(configuration.integerProperty == 101)
        assert(configuration.booleanProperty)
        // settingsUsage
    }
}
