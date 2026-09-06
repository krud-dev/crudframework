package dev.krud.crudframework.registrytest

import dev.krud.crudframework.crud.handler.krud.Krud
import dev.krud.crudframework.crud.handler.krud.KrudRegistry
import dev.krud.crudframework.model.BaseCrudEntity
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import strikt.api.expect
import strikt.assertions.isNull
import strikt.assertions.isSameInstanceAs

// Isolated outside dev.krud.crudframework.crud.handler.krud entirely (not a subpackage of it):
// this test never triggers a classpath scan itself, but a BaseCrudEntity subclass sitting
// anywhere under that package tree would still be picked up by KrudScannerTest's @EnableKrud
// scan there (which recursively scans its own package, defaulting basePackage to the annotated
// class's package) — regardless of Kotlin-level `private`, since scanning works at the bytecode
// level. A sibling package (dev.krud.crudframework.registrytest) avoids that entirely.

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
