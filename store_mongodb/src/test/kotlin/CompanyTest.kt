package com.hexagonkt.store.mongodb

import com.hexagonkt.serialization.JacksonMapper
import com.hexagonkt.serialization.SerializationManager
import com.hexagonkt.settings.SettingsManager
import com.hexagonkt.store.Store
import com.hexagonkt.store.mongodb.Department.DESIGN
import com.hexagonkt.store.mongodb.Department.DEVELOPMENT
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.io.File
import java.net.URL
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@TestInstance(PER_CLASS)
internal class CompanyTest : StoreTest<Company, String>() {

    override fun createTestEntities(): List<Company> = listOf (
        Company(
            id = "id",
            foundation = LocalDate.of(2014, 1, 25),
            closeTime = LocalTime.of(11, 42),
            openTime = LocalTime.of(8, 30)..LocalTime.of(14, 51),
            web = URL("http://example.org"),
            clients = listOf(
                URL("http://c1.example.org"),
                URL("http://c2.example.org")
            ),
            logo = ByteBuffer.wrap(byteArrayOf(0, 1, 2)),
            notes = "notes",
            people = setOf(
                Person(name = "John"),
                Person(name = "Mike")
            ),
            departments = setOf(DESIGN, DEVELOPMENT),
            creationDate = LocalDateTime.of(2016, 1, 1, 0, 0, 0)
        ),
        Company(
            id = "id1",
            foundation = LocalDate.of(2014, 1, 25),
            closeTime = LocalTime.of(11, 42),
            openTime = LocalTime.of(8, 30)..LocalTime.of(14, 36),
            web = URL("http://example.org"),
            people = setOf(
                Person(name = "John"),
                Person(name = "Mike")
            )
        )
    )

    private val mongodbUrl by lazy {
        SettingsManager.instance<Map<*, *>>()["mongodbUrl"] as? String?
        ?: "mongodb://localhost:${mongoDb.getMappedPort(27017)}/test"
    }

    override fun createStore(): Store<Company, String> =
        MongoDbStore(Company::class, Company::id, mongodbUrl, "companies")

    override fun changeObject(obj: Company) =
        obj.copy(web = URL("http://change.example.org"))

    @BeforeAll fun setUpSerializationManager() {
        SerializationManager.mapper = JacksonMapper
    }

    @Test fun `New records are stored`() {
        new_records_are_stored()
    }

    @Test fun `Many records are stored`() {
        many_records_are_stored()
    }

    @Test fun `Entities are stored`() {
        entities_are_stored()
    }

    @Test fun `Insert one record returns the proper key`() {
        insert_one_record_returns_the_proper_key()
    }

    // TODO Check inserted data
    @Test fun `Resources are loaded`() {
        store.import(URL("classpath:companies.json"))
        store.drop()

        // File paths change from IDE to build tool
        val file = File("hexagon_core/src/test/resources/data/companies.json").let {
            if (it.exists()) it
            else File("src/test/resources/companies.json")
        }

        store.import(file)
        store.drop()
    }
}
