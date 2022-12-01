package dev.krud.crudframework.web.rest

import com.google.gson.Gson
import dev.krud.crudframework.crud.annotation.Deleteable
import dev.krud.crudframework.test.AbstractTestEntity
import dev.krud.crudframework.test.AbstractTestRO
import dev.krud.crudframework.test.EnableTestCrud
import dev.krud.crudframework.test.TestCrudDaoImpl
import dev.krud.crudframework.web.ro.ManyCrudResult
import dev.krud.crudframework.web.ro.ResultRO
import dev.krud.shapeshift.resolver.annotation.DefaultMappingTarget
import dev.krud.shapeshift.resolver.annotation.MappedField
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import dev.krud.crudframework.modelfilter.dsl.where

@RestController
@RequestMapping("/crud")
class TestCrudRestController : CrudRestController()

@ExtendWith(SpringExtension::class)
@EnableTestCrud
@EnableCrudControllers
@Import(CrudRestControllerTest.TestConfig::class)
class CrudRestControllerTest {
    @Autowired
    private lateinit var testCrudDao: TestCrudDaoImpl

    @Autowired
    private lateinit var testCrudRestController: TestCrudRestController

    @Configuration
    class TestConfig {
        @Bean
        fun testCrudRestController() = TestCrudRestController()
    }

    @Test
    @DirtiesContext
    fun `show happy flow with main RO`() {
        val subject = TestEntityMain(1L, "test")
        testCrudDao.entities += subject
        val response = testCrudRestController.show("test_main", 1L)
        val result = response.body as ResultRO<TestEntityMainRO>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isA<TestEntityMainRO>()
        expectThat(result.result.id)
                .isEqualTo(1L)
        expectThat(result.result.name)
                .isEqualTo(subject.name)
    }

    @Test
    @DirtiesContext
    fun `show happy flow with different RO`() {
        val subject = TestEntitySplit(1L, "test")
        testCrudDao.entities += subject
        val response = testCrudRestController.show("test_split", 1L)
        val result = response.body as ResultRO<TestEntitySplitShowRO>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isA<TestEntitySplitShowRO>()
        expectThat(result.result.id)
                .isEqualTo(1L)
        expectThat(result.result.name)
                .isEqualTo(subject.name)
    }

    @Test
    fun `show should throw if type is not registered`() {
        val response = testCrudRestController.show("nonexistent", 1L)
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Received exception IllegalArgumentException with message No crud controller definition found for resource nonexistent")
    }

    @Test
    fun `show should throw if entity show is disabled`() {
        val response = testCrudRestController.show("test_all_disabled", 1L)
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Show action is not allowed for resource test_all_disabled")
    }

    @Test
    fun `show should return null if entity is not found`() {
        val response = testCrudRestController.show("test_main", 1L)
        val result = response.body as ResultRO<*>
        expectThat(result.error).isNull()
        expectThat(result.result).isNull()
    }

    @Test
    @DirtiesContext
    fun `delete happy flow`() {
        val subject = TestEntityMain(1L, "test")
        testCrudDao.entities += subject
        val response = testCrudRestController.delete("test_main", 1L)
        val result = response.body as ResultRO<Unit>
        expectThat(result.error).isNull()
        expectThat(testCrudDao.entities).isEmpty()
    }

    @Test
    fun `delete should throw if type is not registered`() {
        val response = testCrudRestController.delete("nonexistent", 1L)
        val result = response.body as ResultRO<TestEntitySplit>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Received exception IllegalArgumentException with message No crud controller definition found for resource nonexistent")
    }

    @Test
    fun `delete should throw if entity delete is disabled`() {
        val response = testCrudRestController.delete("test_all_disabled", 1L)
        val result = response.body as ResultRO<TestEntitySplit>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Delete action is not allowed for resource test_all_disabled")
    }

    @Test
    @DirtiesContext
    fun `index happy flow with main RO`() {
        val subject = TestEntityMain(1L, "test")
        val secondSubject = TestEntityMain(2L, "test2")
        testCrudDao.entities += listOf(subject, secondSubject)
        val response = testCrudRestController.index("test_main", where<TestEntityMain> { TestEntityMain::name Equal "test" })
        val result = response.body as ResultRO<List<TestEntityMainRO>>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .hasSize(1)
        expectThat(result.result.first())
                .isA<TestEntityMainRO>()
        expectThat(result.result.first().id)
                .isEqualTo(1L)
        expectThat(result.result.first().name)
                .isEqualTo(subject.name)
    }

    @Test
    @DirtiesContext
    fun `index happy flow with index RO`() {
        val subject = TestEntitySplit(1L, "test")
        val secondSubject = TestEntitySplit(2L, "test2")
        testCrudDao.entities += listOf(subject, secondSubject)
        val response = testCrudRestController.index("test_split", where<TestEntitySplit> { TestEntitySplit::name Equal "test" })
        val result = response.body as ResultRO<List<TestEntitySplitIndexRO>>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .hasSize(1)
        expectThat(result.result.first())
                .isA<TestEntitySplitIndexRO>()
        expectThat(result.result.first().id)
                .isEqualTo(1L)
        expectThat(result.result.first().name)
                .isEqualTo(subject.name)
    }

    @Test
    fun `index should throw if type is not registered`() {
        val response = testCrudRestController.index("nonexistent", where<TestEntitySplit> { TestEntitySplit::name Equal "test" })
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Received exception IllegalArgumentException with message No crud controller definition found for resource nonexistent")
    }

    @Test
    fun `index should throw if entity index is disabled`() {
        val response = testCrudRestController.index("test_all_disabled", where<TestEntitySplit> { TestEntitySplit::name Equal "test" })
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Index action is not allowed for resource test_all_disabled")
    }

    @Test
    @DirtiesContext
    internal fun `indexCount happy flow`() {
        val subject = TestEntityMain(1L, "test")
        val secondSubject = TestEntityMain(2L, "test2")
        testCrudDao.entities += listOf(subject, secondSubject)
        val response = testCrudRestController.indexCount("test_main", where<TestEntityMain> { })
        val result = response.body as ResultRO<Long>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isEqualTo(2)
    }

    @Test
    fun `indexCount should throw if type is not registered`() {
        val response = testCrudRestController.indexCount("nonexistent", where<TestEntitySplit> { TestEntitySplit::name Equal "test" })
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Received exception IllegalArgumentException with message No crud controller definition found for resource nonexistent")
    }

    @Test
    fun `indexCount should throw if entity index is disabled`() {
        val response = testCrudRestController.indexCount("test_all_disabled", where<TestEntitySplit> { TestEntitySplit::name Equal "test" })
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Index action is not allowed for resource test_all_disabled")
    }

    @Test
    @DirtiesContext
    internal fun `create happy flow with main RO`() {
        val ro = TestEntityMainRO("newTest")
        val gson = Gson()
        val response = testCrudRestController.create("test_main", gson.toJson(ro))
        val result = response.body as ResultRO<TestEntityMainRO>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isA<TestEntityMainRO>()
        expectThat(result.result.name)
                .isEqualTo("newTest")
        expectThat(testCrudDao.entities.size)
                .isEqualTo(1)
    }

    @Test
    @DirtiesContext
    internal fun `create happy flow with create and show RO`() {
        val ro = TestEntitySplitCreateRO("newTest")
        val gson = Gson()
        val response = testCrudRestController.create("test_split", gson.toJson(ro))
        val result = response.body as ResultRO<TestEntitySplitShowRO>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isA<TestEntitySplitShowRO>()
        expectThat(result.result.name)
                .isEqualTo("newTest")
        expectThat(testCrudDao.entities.size)
                .isEqualTo(1)
    }

    @Test
    fun `create should throw if type is not registered`() {
        val response = testCrudRestController.create("nonexistent", "{}")
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Received exception IllegalArgumentException with message No crud controller definition found for resource nonexistent")
    }

    @Test
    fun `create should throw if entity create is disabled`() {
        val response = testCrudRestController.create("test_all_disabled", "{}")
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Create action is not allowed for resource test_all_disabled")
    }

    @Test
    @DirtiesContext
    internal fun `update happy flow with main RO`() {
        val subject = TestEntityMain(2L, "test_main")
        testCrudDao.entities += subject
        val ro = TestEntityMainRO("newTest")
        val gson = Gson()
        val response = testCrudRestController.update("test_main", 2L, gson.toJson(ro))
        val result = response.body as ResultRO<TestEntityMainRO>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isA<TestEntityMainRO>()
        expectThat(result.result.name)
                .isEqualTo("newTest")
    }

    @Test
    @DirtiesContext
    internal fun `update happy flow with create and show RO`() {
        val subject = TestEntitySplit(2L, "test")
        testCrudDao.entities += subject
        val ro = TestEntitySplitUpdateRO("newTest")
        val gson = Gson()
        val response = testCrudRestController.update("test_split", 2L, gson.toJson(ro))
        val result = response.body as ResultRO<TestEntitySplitShowRO>
        expectThat(result.error).isNull()
        expectThat(result.result)
                .isA<TestEntitySplitShowRO>()
        expectThat(result.result.name)
                .isEqualTo("newTest")
    }

    @Test
    fun `update should throw if type is not registered`() {
        val response = testCrudRestController.update("nonexistent", 1L, "{}")
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Received exception IllegalArgumentException with message No crud controller definition found for resource nonexistent")
    }

    @Test
    fun `update should throw if entity update is disabled`() {
        val response = testCrudRestController.update("test_all_disabled", 1L, "{}")
        val result = response.body as ResultRO<*>
        expectThat(result.isSuccess).isFalse()
        expectThat(result.error).isEqualTo("Update action is not allowed for resource test_all_disabled")
    }

    @Test
    internal fun `updateMany happy flow with split ROs`() {
        val subjects = listOf(
            TestEntitySplit(1L, "test"),
            TestEntitySplit(2L, "test2")
        )
        testCrudDao.entities += subjects
        val ros = listOf(
            TestEntitySplitUpdateRO("newTest").apply { id = 1L },
            TestEntitySplitUpdateRO("newTest2").apply { id = 2L }
        )
        val gson = Gson()
        val response = testCrudRestController.updateMany("test_split", gson.toJson(ros))
        val result = response.body as ResultRO<ManyCrudResult<TestEntitySplitShowRO, TestEntitySplitUpdateRO>>
        expectThat(result.error).isNull()
        expectThat(result.result.successful)
                .hasSize(2)
        expectThat(result.result.successful.toList()[0].name)
                .isEqualTo("newTest")
        expectThat(result.result.successful.toList()[1].name)
                .isEqualTo("newTest2")
    }
}

@CrudController(
    resourceName = "test_split",
    roMapping = RoMapping(
        showRoClass = TestEntitySplitShowRO::class,
        indexRoClass = TestEntitySplitIndexRO::class,
        createRoClass = TestEntitySplitCreateRO::class,
        updateRoClass = TestEntitySplitUpdateRO::class
    )
)
@Deleteable(softDelete = false)
class TestEntitySplit(id: Long = 0L, @MappedField(target = TestEntitySplitShowRO::class) @MappedField(target = TestEntitySplitIndexRO::class) var name: String = "test") : AbstractTestEntity(id)

@DefaultMappingTarget(TestEntitySplit::class)
class TestEntitySplitCreateRO(@MappedField var name: String? = null) : AbstractTestRO() {
    override var id: Long? = null
}

@DefaultMappingTarget(TestEntitySplit::class)
class TestEntitySplitUpdateRO(@MappedField var name: String? = null) : AbstractTestRO() {
    override var id: Long? = null
}

@DefaultMappingTarget(TestEntitySplit::class)
class TestEntitySplitShowRO(@MappedField var name: String? = null) : AbstractTestRO() {
    override var id: Long? = null
}

@DefaultMappingTarget(TestEntitySplit::class)
class TestEntitySplitIndexRO(@MappedField var name: String? = null) : AbstractTestRO() {
    override var id: Long? = null
}

@CrudController(
    resourceName = "test_main",
    roMapping = RoMapping(
        mainRoClass = TestEntityMainRO::class
    )
)
@DefaultMappingTarget(TestEntityMainRO::class)
@Deleteable(softDelete = false)
class TestEntityMain(id: Long = 0L, @MappedField var name: String = "test") : AbstractTestEntity(id)

@DefaultMappingTarget(TestEntityMain::class)
class TestEntityMainRO(@MappedField var name: String? = null) : AbstractTestRO() {
    override var id: Long? = null
}

@CrudController(
    resourceName = "test_all_disabled",
    CrudActions(
        show = false,
        index = false,
        create = false,
        update = false,
        delete = false
    )
)
class TestEntityShowDisabled: AbstractTestEntity(1L)