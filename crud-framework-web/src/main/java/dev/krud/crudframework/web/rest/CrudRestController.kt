package dev.krud.crudframework.web.rest

import dev.krud.crudframework.modelfilter.DynamicModelFilter
import dev.krud.crudframework.web.controller.BaseController
import dev.krud.crudframework.web.ro.ResultRO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.io.Serializable

abstract class CrudRestController : BaseController() {
    @Autowired
    private lateinit var crudRestService: CrudRestService

    open fun wrapResult(response: ResultRO<*>?): ResponseEntity<*> = ResponseEntity.ok(response)

    @GetMapping("/{resourceName}/{id}")
    fun show(@PathVariable resourceName: String, @PathVariable id: Serializable): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.show(resourceName, id)
        }
        return this.wrapResult(result)
    }

    @PostMapping("/{resourceName}/index")
    fun index(@PathVariable resourceName: String, @RequestBody filter: DynamicModelFilter): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.index(resourceName, filter)
        }
        return this.wrapResult(result)
    }

    @PostMapping("/{resourceName}/index/count")
    fun indexCount(@PathVariable resourceName: String, @RequestBody filter: DynamicModelFilter): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.indexCount(resourceName, filter)
        }
        return this.wrapResult(result)
    }

    @PostMapping("/{resourceName}")
    fun create(@PathVariable resourceName: String, @RequestBody body: String): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.create(resourceName, body)
        }
        return this.wrapResult(result)
    }

    @PostMapping("/{resourceName}/createMany")
    fun createMany(@PathVariable resourceName: String, @RequestBody body: String): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.createMany(resourceName, body)
        }
        return this.wrapResult(result)
    }

    @PutMapping("/{resourceName}/updateMany")
    fun updateMany(@PathVariable resourceName: String, @RequestBody body: String): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.updateMany(resourceName, body)
        }
        return this.wrapResult(result)
    }

    @PutMapping("/{resourceName}/{id}")
    fun update(@PathVariable resourceName: String, @PathVariable id: Serializable, @RequestBody body: String): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.update(resourceName, id, body)
        }
        return this.wrapResult(result)
    }

    @DeleteMapping("/{resourceName}/{id}")
    fun delete(@PathVariable resourceName: String, @PathVariable id: Serializable): ResponseEntity<*> {
        val result = wrapResult {
            crudRestService.delete(resourceName, id)
        }
        return this.wrapResult(result)
    }
}