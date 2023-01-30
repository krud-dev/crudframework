package dev.krud.crudframework.jpa.lazyinitializer

import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.ro.PagingDTO
import dev.krud.crudframework.ro.PagingRO
import org.junit.jupiter.api.Test

class LazyInitializerPersistentHooksTest {

    @Test
    fun `test index hook doesn't fail on null result`() {
        val subject = LazyInitializerPersistentHooks()
        subject.onIndex(DynamicModelFilter(),
            PagingDTO(
                PagingRO(
                    0,
                    20,
                    100
                ), null
            )
        )
    }
}