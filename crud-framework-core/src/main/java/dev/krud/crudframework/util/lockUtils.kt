package dev.krud.crudframework.util

import dev.krud.crudframework.ParameterLock
import dev.krud.crudframework.model.BaseCrudEntity
import java.util.concurrent.locks.Lock
import kotlin.reflect.KClass

fun BaseCrudEntity<*>.getLock(): Lock {
    return this::class.getLock(this.id)
}

fun <T> BaseCrudEntity<*>.doInLock(block: () -> T): T {
    val lock = this.getLock()
    try {
        lock.lock()
        return block()
    } finally {
        lock.unlock()
    }
}

fun BaseCrudEntity<*>.doInTryLock(block: () -> Unit) {
    val lock = this.getLock()
    var locked = false
    try {
        locked = lock.tryLock()
        if (locked) {
            block()
        }
    } finally {
        if (locked) {
            lock.unlock()
        }
    }
}

fun <T: BaseCrudEntity<*>> KClass<T>.getLock(id: Any): Lock {
    return ParameterLock.getCanonicalParameterLock(this.java.name, id.toString())
}

fun <T: BaseCrudEntity<*>> KClass<T>.doInLock(id: Any, block: () -> T): T {
    val lock = this.getLock(id)
    try {
        lock.lock()
        return block()
    } finally {
        lock.unlock()
    }
}

fun <T: BaseCrudEntity<*>> KClass<T>.doInTryLock(id: Any, block: () -> Unit) {
    val lock = this.getLock(id)
    var locked = false
    try {
        locked = lock.tryLock()
        if (locked) {
            block()
        }
    } finally {
        if (locked) {
            lock.unlock()
        }
    }
}