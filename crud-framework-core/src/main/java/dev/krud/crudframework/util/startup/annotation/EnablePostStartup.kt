package dev.krud.crudframework.util.startup.annotation

import org.springframework.context.annotation.Import
import dev.krud.crudframework.util.startup.configuration.PostStartupConfiguration

@Target(AnnotationTarget.CLASS)
@Import(PostStartupConfiguration::class)
annotation class EnablePostStartup