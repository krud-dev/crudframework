package dev.krud.crudframework.jpa.lazyinitializer

import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.ro.PagedResult
import org.junit.jupiter.api.Test

class LazyInitializerPersistentHooksTest {

    @Test
    fun `test index hook doesn't fail on null result`() {
        val subject = LazyInitializerPersistentHooks()
        subject.onIndex(
            DynamicModelFilter(),
            PagedResult(
                0,
                20,
                100,
                false,
                emptyList()
            )
        )
    }
}