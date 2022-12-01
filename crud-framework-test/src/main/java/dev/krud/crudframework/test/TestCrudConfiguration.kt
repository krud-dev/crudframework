package dev.krud.crudframework.test

import dev.krud.shapeshift.spring.ShapeShiftAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import dev.krud.crudframework.crud.handler.CrudDao

@Configuration
@Import(ShapeShiftAutoConfiguration::class)
class TestCrudConfiguration {
    @Bean
    fun testCrudDao(): CrudDao = TestCrudDaoImpl()
}