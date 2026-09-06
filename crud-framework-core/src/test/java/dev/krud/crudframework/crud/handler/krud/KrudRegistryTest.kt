package dev.krud.crudframework.crud.handler.krud

import dev.krud.crudframework.crud.handler.CrudCreateHandler
import dev.krud.crudframework.crud.handler.CrudDeleteHandler
import dev.krud.crudframework.crud.handler.CrudReadHandler
import dev.krud.crudframework.crud.handler.CrudUpdateHandler
import dev.krud.crudframework.model.BaseCrudEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expect
import strikt.assertions.isNull
import strikt.assertions.isSameInstanceAs

private class FakeEntity : BaseCrudEntity<Int>() {
    override var id: Int = 1
    override fun getCacheKey(): String? = ""
    override fun exists(): Boolean = true
}

private class OtherFakeEntity : BaseCrudEntity<Int>() {
    override var id: Int = 1
    override fun getCacheKey(): String? = ""
    override fun exists(): Boolean = true
}

class KrudRegistryUnitTest {
    @Test
    fun `register makes a krud retrievable by its entity class`() {
        val registry = KrudRegistry()
        val krud = Mockito.mock(Krud::class.java)

        registry.register(FakeEntity::class.java, krud)

        expect {
            that(registry.getOrNull<FakeEntity, Int>(FakeEntity::class.java)).isSameInstanceAs(krud)
        }
    }

    @Test
    fun `unregistered entity classes resolve to null`() {
        val registry = KrudRegistry()

        expect {
            that(registry.getOrNull<OtherFakeEntity, Int>(OtherFakeEntity::class.java)).isNull()
        }
    }
}

@EnableKrud
private class RegistryIntegrationApp {
    @Bean
    fun krudRegistry(): KrudRegistry = KrudRegistry()
}

private class RegistryTestEntity : BaseCrudEntity<Int>() {
    override var id: Int = 1
    override fun getCacheKey(): String? = ""
    override fun exists(): Boolean = true
}

@ExtendWith(SpringExtension::class)
@Import(RegistryIntegrationApp::class)
class KrudRegistryIntegrationTest {
    @MockBean
    private lateinit var crudCreateHandler: CrudCreateHandler

    @MockBean
    private lateinit var crudReadHandler: CrudReadHandler

    @MockBean
    private lateinit var crudUpdateHandler: CrudUpdateHandler

    @MockBean
    private lateinit var crudDeleteHandler: CrudDeleteHandler

    @Autowired
    private lateinit var krudRegistry: KrudRegistry

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `krud beans created by the scanner self-register into the registry`() {
        val krudFromContext = applicationContext.getBeansOfType(Krud::class.java).values.first()
        val krudFromRegistry = krudRegistry.getOrNull<RegistryTestEntity, Int>(RegistryTestEntity::class.java)

        expect {
            that(krudFromRegistry).isSameInstanceAs(krudFromContext)
        }
    }

    @Test
    fun `unregistered entity classes resolve to null`() {
        expect {
            that(krudRegistry.getOrNull<OtherFakeEntity, Int>(OtherFakeEntity::class.java)).isNull()
        }
    }
}
