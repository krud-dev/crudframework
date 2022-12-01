package dev.krud.crudframework.util.startup.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import dev.krud.crudframework.util.startup.PostStartupHandler

@Configuration
internal class PostStartupConfiguration {
    @Bean
    fun postStartupHandler(): PostStartupHandler {
        return PostStartupHandler()
    }
}