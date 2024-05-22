package dev.krud.crudframework.modelfilter.dsl

import dev.krud.crudframework.model.PersistentEntity
import dev.krud.crudframework.modelfilter.FilterField
import dev.krud.crudframework.modelfilter.enums.FilterFieldDataType
import dev.krud.crudframework.modelfilter.enums.FilterFieldOperation
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

enum class TestEnum {
    One, Two, Three
}

class TestClazz : PersistentEntity {
    val testString: String = "test"
    val testInt: Int = 1
    val testLong: Long = 1L
    val testDouble: Double = 1.0
    val testDate: Date = Date(0)
    val testBoolean: Boolean = true
    val testEnum: TestEnum = TestEnum.One
    val testStringList: List<String> = emptyList()
    val testIntList: List<Int> = emptyList()
    val testLongList: List<Long> = emptyList()
    val testDoubleList: List<Double> = emptyList()
    val testDateList: List<Date> = emptyList()
    val testBooleanList: List<Boolean> = emptyList()
    val testEnumList: List<TestEnum> = emptyList()
    val testSubClazz: TestSubClazz = TestSubClazz()
}

class TestSubClazz: PersistentEntity {
    val testString: String = "test"
    val testSubSubClazz: TestSubSubClazz = TestSubSubClazz()
}

class TestSubSubClazz: PersistentEntity {
    val testString: String = "test"
}

class FilterFieldDslTest {
    @Test
    fun `test Equal#String`() {
        val filterField = and<TestClazz> {
            TestClazz::testString Equal "value"
        }.children.first()

        filterField.runAssertions(
            "testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test Equal#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt Equal 1
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.Equal,
            FilterFieldDataType.Integer,
            1,
            arrayOf(1)
        )
    }

    @Test
    fun `test Equal#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong Equal 1L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.Equal,
            FilterFieldDataType.Long,
            1,
            arrayOf(1L)
        )
    }

    @Test
    fun `test Equal#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble Equal 1.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.Equal,
            FilterFieldDataType.Double,
            1,
            arrayOf(1.0)
        )
    }

    @Test
    fun `test Equal#Boolean`() {
        val filterField = and<TestClazz> {
            TestClazz::testBoolean Equal true
        }.children.first()

        filterField.runAssertions(
            "testBoolean",
            FilterFieldOperation.Equal,
            FilterFieldDataType.Boolean,
            1,
            arrayOf(true)
        )
    }

    @Test
    fun `test Equal#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate Equal Date(0)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.Equal,
            FilterFieldDataType.Date,
            1,
            arrayOf(Date(0))
        )
    }

    @Test
    fun `test Equal#Enum`() {
        val filterField = and<TestClazz> {
            TestClazz::testEnum Equal TestEnum.One
        }.children.first()

        filterField.runAssertions(
            "testEnum",
            FilterFieldOperation.Equal,
            FilterFieldDataType.Enum,
            1,
            arrayOf(TestEnum.One),
            TestEnum::class.java.canonicalName
        )
    }

    @Test
    fun `test NotEqual#String`() {
        val filterField = and<TestClazz> {
            TestClazz::testString NotEqual "value"
        }.children.first()

        filterField.runAssertions(
            "testString",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test NotEqual#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt NotEqual 1
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Integer,
            1,
            arrayOf(1)
        )
    }

    @Test
    fun `test NotEqual#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong NotEqual 1L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Long,
            1,
            arrayOf(1L)
        )
    }

    @Test
    fun `test NotEqual#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble NotEqual 1.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Double,
            1,
            arrayOf(1.0)
        )
    }

    @Test
    fun `test NotEqual#Boolean`() {
        val filterField = and<TestClazz> {
            TestClazz::testBoolean NotEqual true
        }.children.first()

        filterField.runAssertions(
            "testBoolean",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Boolean,
            1,
            arrayOf(true)
        )
    }

    @Test
    fun `test NotEqual#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate NotEqual Date(0)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Date,
            1,
            arrayOf(Date(0))
        )
    }

    @Test
    fun `test NotEqual#Enum`() {
        val filterField = and<TestClazz> {
            TestClazz::testEnum NotEqual TestEnum.One
        }.children.first()

        filterField.runAssertions(
            "testEnum",
            FilterFieldOperation.NotEqual,
            FilterFieldDataType.Enum,
            1,
            arrayOf(TestEnum.One),
            TestEnum::class.java.canonicalName
        )
    }

    @Test
    fun `test Contains#String`() {
        val filterField = and<TestClazz> {
            TestClazz::testString Contains "value"
        }.children.first()

        filterField.runAssertions(
            "testString",
            FilterFieldOperation.Contains,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test GreaterThan#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt GreaterThan 1
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Integer,
            1,
            arrayOf(1)
        )
    }

    @Test
    fun `test GreaterThan#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong GreaterThan 1L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Long,
            1,
            arrayOf(1L)
        )
    }

    @Test
    fun `test GreaterThan#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble GreaterThan 1.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Double,
            1,
            arrayOf(1.0)
        )
    }

    @Test
    fun `test GreaterThan#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate GreaterThan Date(0)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.GreaterThan,
            FilterFieldDataType.Date,
            1,
            arrayOf(Date(0))
        )
    }

    @Test
    fun `test GreaterOrEqual#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt GreaterOrEqual 1
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Integer,
            1,
            arrayOf(1)
        )
    }

    @Test
    fun `test GreaterOrEqual#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong GreaterOrEqual 1L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Long,
            1,
            arrayOf(1L)
        )
    }

    @Test
    fun `test GreaterOrEqual#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble GreaterOrEqual 1.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Double,
            1,
            arrayOf(1.0)
        )
    }

    @Test
    fun `test GreaterOrEqual#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate GreaterOrEqual Date(0)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.GreaterEqual,
            FilterFieldDataType.Date,
            1,
            arrayOf(Date(0))
        )
    }

    @Test
    fun `test LowerThan#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt LowerThan 1
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Integer,
            1,
            arrayOf(1)
        )
    }

    @Test
    fun `test LowerThan#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong LowerThan 1L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Long,
            1,
            arrayOf(1L)
        )
    }

    @Test
    fun `test LowerThan#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble LowerThan 1.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Double,
            1,
            arrayOf(1.0)
        )
    }

    @Test
    fun `test LowerThan#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate LowerThan Date(0)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.LowerThan,
            FilterFieldDataType.Date,
            1,
            arrayOf(Date(0))
        )
    }

    @Test
    fun `test LowerOrEqual#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt LowerOrEqual 1
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Integer,
            1,
            arrayOf(1)
        )
    }

    @Test
    fun `test LowerOrEqual#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong LowerOrEqual 1L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Long,
            1,
            arrayOf(1L)
        )
    }

    @Test
    fun `test LowerOrEqual#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble LowerOrEqual 1.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Double,
            1,
            arrayOf(1.0)
        )
    }

    @Test
    fun `test LowerOrEqual#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate LowerOrEqual Date(0)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.LowerEqual,
            FilterFieldDataType.Date,
            1,
            arrayOf(Date(0))
        )
    }

    @Test
    fun `test Between#Int`() {
        val filterField = and<TestClazz> {
            TestClazz::testInt Between 1 And 2
        }.children.first()

        filterField.runAssertions(
            "testInt",
            FilterFieldOperation.Between,
            FilterFieldDataType.Integer,
            2,
            arrayOf(1, 2)
        )
    }

    @Test
    fun `test Between#Long`() {
        val filterField = and<TestClazz> {
            TestClazz::testLong Between 1L And 2L
        }.children.first()

        filterField.runAssertions(
            "testLong",
            FilterFieldOperation.Between,
            FilterFieldDataType.Long,
            2,
            arrayOf(1L, 2L)
        )
    }

    @Test
    fun `test Between#Double`() {
        val filterField = and<TestClazz> {
            TestClazz::testDouble Between 1.0 And 2.0
        }.children.first()

        filterField.runAssertions(
            "testDouble",
            FilterFieldOperation.Between,
            FilterFieldDataType.Double,
            2,
            arrayOf(1.0, 2.0)
        )
    }

    @Test
    fun `test Between#Date`() {
        val filterField = and<TestClazz> {
            TestClazz::testDate Between Date(0) And Date(1)
        }.children.first()

        filterField.runAssertions(
            "testDate",
            FilterFieldOperation.Between,
            FilterFieldDataType.Date,
            2,
            arrayOf(Date(0), Date(1))
        )
    }

    @Test
    fun `test isNull`() {
        val filterField = and<TestClazz> {
            TestClazz::testString.isNull()
        }.children.first()

        filterField.runAssertions(
            "testString",
            FilterFieldOperation.IsNull,
            FilterFieldDataType.Object,
            2,
            arrayOf(null, null)
        )
    }

    @Test
    fun `test isNotNull`() {
        val filterField = and<TestClazz> {
            TestClazz::testString.isNotNull()
        }.children.first()

        filterField.runAssertions(
            "testString",
            FilterFieldOperation.IsNotNull,
            FilterFieldDataType.Object,
            2,
            arrayOf(null, null)
        )
    }

    @Test
    fun `test SubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testString Equal "value"
            }
        }.children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test SubSubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    TestSubSubClazz::testString Equal "value"
                }
            }
        }.children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test or#SubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                or {
                    TestSubClazz::testString Equal "value"
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test or#SubSubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    or {
                        TestSubSubClazz::testString Equal "value"
                    }
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test and#SubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                and {
                    TestSubClazz::testString Equal "value"
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test and#SubSubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    and {
                        TestSubSubClazz::testString Equal "value"
                    }
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test not#SubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                not {
                    TestSubClazz::testString Equal "value"
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test not#SubSubClazz`() {
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    not {
                        TestSubSubClazz::testString Equal "value"
                    }
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#SubFilterField`() {
        val fieldToAdd = and { TestSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                this.add(fieldToAdd)
            }
        }.children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#SubSubFilterField`() {
        val fieldToAdd = and { TestSubSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    this.add(fieldToAdd)
                }
            }
        }.children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#or#SubFilterField`() {
        val fieldToAdd = and { TestSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                or {
                    this.add(fieldToAdd)
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#or#SubSubFilterField`() {
        val fieldToAdd = and { TestSubSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    or {
                        this.add(fieldToAdd)
                    }
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#and#SubFilterField`() {
        val fieldToAdd = and { TestSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                and {
                    this.add(fieldToAdd)
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#and#SubSubFilterField`() {
        val fieldToAdd = and { TestSubSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    and {
                        this.add(fieldToAdd)
                    }
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#not#SubFilterField`() {
        val fieldToAdd = and { TestSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                not {
                    this.add(fieldToAdd)
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    @Test
    fun `test add#not#SubSubFilterField`() {
        val fieldToAdd = and { TestSubSubClazz::testString Equal "value" }.children.first()
        val filterField = and<TestClazz> {
            TestClazz::testSubClazz.Sub {
                TestSubClazz::testSubSubClazz.Sub {
                    not {
                        this.add(fieldToAdd)
                    }
                }
            }
        }.children.first().children.first()

        filterField.runAssertions(
            "testSubClazz/testSubSubClazz.testString",
            FilterFieldOperation.Equal,
            FilterFieldDataType.String,
            1,
            arrayOf("value")
        )
    }

    private fun FilterField.runAssertions(
        expectedFieldName: String,
        expectedOperation: FilterFieldOperation,
        expectedDataType: FilterFieldDataType,
        expectedValuesLength: Int,
        expectedValues: Array<Any?>,
        expectedEnumType: String? = null
    ) {
        expectThat(this.fieldName).isEqualTo(expectedFieldName)
        expectThat(this.operation).isEqualTo(expectedOperation)
        expectThat(this.dataType).isEqualTo(expectedDataType)
        expectThat(this.values.size).isEqualTo(expectedValuesLength)
        expectThat(this.values).isEqualTo(expectedValues)

        if (expectedEnumType != null) {
            expectThat(this.enumType).isEqualTo(enumType)
        }
    }
}