package dev.krud.crudframework.ro

fun <Payload> PagingDTO<Payload>.first(): Payload? {
    return data?.getOrElse(0, { null })
}