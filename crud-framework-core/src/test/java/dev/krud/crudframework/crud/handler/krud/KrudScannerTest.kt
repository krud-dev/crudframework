package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.crud.handler.CrudCreateHandler
import dev.krud.crudframework.crud.handler.CrudDeleteHandler
import dev.krud.crudframework.crud.handler.CrudReadHandler
import dev.krud.crudframework.crud.handler.CrudUpdateHandler
import dev.krud.crudframework.model.BaseCrudEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expect
import strikt.assertions.isEqualTo

@EnableKrud
private class App

private class Entity : BaseCrudEntity<Int>() {
    override var id: Int = 1

    override fun getCacheKey(): String? = ""

    override fun exists(): Boolean = true
}

@ExtendWith(SpringExtension::class)
@Import(App::class)
class KrudScannerTest {
    @MockBean
    private lateinit var crudCreateHandler: CrudCreateHandler

    @MockBean
    private lateinit var crudReadHandler: CrudReadHandler

    @MockBean
    private lateinit var crudUpdateHandler: CrudUpdateHandler

    @MockBean
    private lateinit var crudDeleteHandler: CrudDeleteHandler

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `scanner should create beans for each entity in package`() {
        val beans = applicationContext.getBeansOfType(Krud::class.java)
        val firstBean = beans.values.first()
        expect {
            that(beans.size).isEqualTo(1)
            that(firstBean.entityClazz).isEqualTo(Entity::class.java)
            that(beans["entityKrud"]).isEqualTo(firstBean)
        }
    }
}