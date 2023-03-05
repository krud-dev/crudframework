package dev.krud.crudframework.util

import dev.krud.crudframework.model.BaseCrudEntity
import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.dsl.where
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.util.Date

private class Customer(
    val name: String = "Test",
    val age: Int = 55,
    val creationTime: Date = Date(1000L),
    val email: String = "test@example.com",
    val address: Address = Address(),
    val favoriteFruits: List<String> = listOf("Apple", "Orange", "Apricot", "Banana"),
    val zodiacSign: String? = null
) : BaseCrudEntity<Long>() {
    override var id: Long = 1

    override fun exists(): Boolean {
        return true
    }
}

private class Address(
    val street: String = "Example Street",
    val city: String = "Colorado Springs",
    val state: State = State(),
    val zipCode: String = "55234"
) : PersistentEntity

private class State(
    val code: String = "CO",
    val name: String = "Colorado",
    val active: Boolean = true
) : PersistentEntity

class DynamicModelFilterUtilsTest {
    @Test
    fun `test Equal operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::address Sub {
                Address::street Equal "Example Street"
            }
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test Equal operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::address Sub {
                Address::street Equal "Example"
            }
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test NotEqual operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::address Sub {
                Address::street NotEqual "Example"
            }
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test NotEqual operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::address Sub {
                Address::street NotEqual "Example Street"
            }
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test GreaterThan operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age GreaterThan 50
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test GreaterThan operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age GreaterThan 56
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test GreaterEqual operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age GreaterOrEqual 55
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test GreaterEqual operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age GreaterOrEqual 56
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test LowerThan operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age LowerThan 60
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test LowerThan operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age LowerThan 54
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test LowerEqual operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age LowerOrEqual 55
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test LowerEqual operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::age LowerOrEqual 54
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test Between operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::creationTime Between Date(0) And Date(2000)
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test Between operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::creationTime Between Date(1500) And Date(2000)
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test Contains operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::name Contains "Te"
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test Contains operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::name Contains "John"
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test IsNull operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::zodiacSign.isNull()
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test IsNull operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::name.isNull()
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test IsNotNull operation happy flow`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::name.isNotNull()
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test IsNotNull operation with false outcome`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::zodiacSign.isNotNull()
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }

    @Test
    fun `test nested field filtersMatch()`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::address Sub {
                Address::state Sub {
                    State::code Equal "CO"
                }
            }
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isTrue()
    }

    @Test
    fun `test nested field doesn't match`() {
        val customer = Customer()
        val filter = where<Customer> {
            Customer::address Sub {
                Address::state Sub {
                    State::active NotEqual true
                }
            }
        }
        expectThat(
            filter.filtersMatch(customer)
        ).isFalse()
    }
}