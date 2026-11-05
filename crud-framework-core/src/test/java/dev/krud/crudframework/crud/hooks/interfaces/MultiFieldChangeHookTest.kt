package dev.krud.crudframework.crud.hooks.interfaces

import dev.krud.crudframework.model.BaseCrudEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

// Final, like every Kotlin class that isn't explicitly opened
class FinalHookTestEntity(
    override var id: Long = 0L,
    var phonePrefix: String? = null,
    var phone: String? = null,
    var phoneVerified: Boolean = false,
) : BaseCrudEntity<Long>() {
    lateinit var nickname: String

    override fun exists() = id != 0L
}

class MultiFieldChangeHookTest {

    private val properties = listOf(FinalHookTestEntity::phonePrefix, FinalHookTestEntity::phone)

    private fun originalAndUpdated(): Pair<FinalHookTestEntity, FinalHookTestEntity> {
        val updated = FinalHookTestEntity(1L, "+1", "5550000", phoneVerified = true)
        val original = updated.saveOrGetCopy() as FinalHookTestEntity
        updated.phone = "5551111"
        return original to updated
    }

    @Test
    fun `original handed to a callback holds the original values of a final entity`() {
        val (original, updated) = originalAndUpdated()
        var seenPhone: String? = null
        var seenPrefix: String? = null
        val hook = properties.preChange { o, _ ->
            seenPhone = o.phone
            seenPrefix = o.phonePrefix
        }

        hook.runPreChange(updated, original)

        assertEquals("5550000", seenPhone)
        assertEquals("+1", seenPrefix)
    }

    @Test
    fun `updated is the entity being saved so changes to it are kept`() {
        val (original, updated) = originalAndUpdated()
        var seenUpdated: FinalHookTestEntity? = null
        val hook = properties.preChange { _, u ->
            seenUpdated = u
            u.phoneVerified = false
        }

        hook.runPreChange(updated, original)

        assertSame(updated, seenUpdated)
        assertFalse(updated.phoneVerified)
    }

    @Test
    fun `changing original inside a callback does not change the shared original`() {
        val (original, updated) = originalAndUpdated()
        var seenOriginal: FinalHookTestEntity? = null
        val hook = properties.onChange { o, _ ->
            seenOriginal = o
            o.phone = "changed"
        }

        hook.runOnChange(updated, original)

        assertNotSame(original, seenOriginal)
        assertEquals("5550000", original.phone)
    }

    @Test
    fun `callback does not run when none of the properties changed`() {
        val updated = FinalHookTestEntity(1L, "+1", "5550000")
        val original = updated.saveOrGetCopy() as FinalHookTestEntity
        var ran = false
        val hook = properties.postChange { _, _ -> ran = true }

        hook.runPostChange(updated, original)

        assertFalse(ran)
    }

    @Test
    fun `ALL only runs when every property changed`() {
        val (original, updated) = originalAndUpdated()
        var ran = false
        val hook = properties.preChange(MultiFieldChangeCheckType.ALL) { _, _ -> ran = true }

        hook.runPreChange(updated, original)
        assertFalse(ran)

        updated.phonePrefix = "+44"
        hook.runPreChange(updated, original)
        assertEquals(true, ran)
    }

    @Test
    fun `an uninitialized lateinit property does not stop the copy`() {
        val (original, updated) = originalAndUpdated()
        var seenPhone: String? = null
        val hook = properties.preChange { o, _ -> seenPhone = o.phone }

        hook.runPreChange(updated, original)

        assertEquals("5550000", seenPhone)
    }
}
