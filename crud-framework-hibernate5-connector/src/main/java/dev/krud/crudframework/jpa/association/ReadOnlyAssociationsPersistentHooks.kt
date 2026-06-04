package dev.krud.crudframework.jpa.association

import dev.krud.crudframework.crud.hooks.interfaces.UpdateFromHooks
import dev.krud.crudframework.crud.hooks.interfaces.UpdateHooks
import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.util.ReflectionUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.EntityManager
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PersistenceContext
import org.hibernate.Hibernate
import org.hibernate.Session

class ReadOnlyAssociationsPersistentHooks :
    UpdateHooks<Long, BaseCrudEntity<Long>>,
    UpdateFromHooks<Long, BaseCrudEntity<Long>> {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun onUpdate(entity: BaseCrudEntity<Long>) {
        markAssociationsReadOnly(entity)
    }

    override fun onUpdateFrom(entity: BaseCrudEntity<Long>, ro: Any) {
        markAssociationsReadOnly(entity)
    }

    private fun markAssociationsReadOnly(entity: BaseCrudEntity<Long>) {
        val session = entityManager.unwrap(Session::class.java)
        ReflectionUtils.doWithFields(entity::class.java) { field ->
            val cascade = when {
                field.isAnnotationPresent(ManyToOne::class.java) -> field.getAnnotation(ManyToOne::class.java).cascade
                field.isAnnotationPresent(OneToOne::class.java) -> field.getAnnotation(OneToOne::class.java).cascade
                field.isAnnotationPresent(ManyToMany::class.java) -> field.getAnnotation(ManyToMany::class.java).cascade
                field.isAnnotationPresent(OneToMany::class.java) -> field.getAnnotation(OneToMany::class.java).cascade
                else -> return@doWithFields
            }
            if (CascadeType.ALL in cascade || CascadeType.MERGE in cascade) return@doWithFields
            ReflectionUtils.makeAccessible(field)
            val value = field.get(entity) ?: return@doWithFields
            if (value is Collection<*>) {
                if (Hibernate.isInitialized(value)) {
                    value.filterNotNull().forEach { session.setReadOnly(it, true) }
                }
            } else {
                session.setReadOnly(value, true)
            }
        }
    }
}
