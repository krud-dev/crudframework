package dev.krud.crudframework.crud.handler.krud

import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor

/**
 * Enable the experimental Krud handlers
 */
@Import(KrudScanner::class)
annotation class EnableKrud(
    @get:AliasFor("basePackages") val value: Array<String> = [],
    val basePackages: Array<String> = []
)