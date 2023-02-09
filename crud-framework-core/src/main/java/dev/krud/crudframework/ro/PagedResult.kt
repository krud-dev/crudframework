package dev.krud.crudframework.ro

data class PagedResult<T>(
    val start: Long?,
    val limit: Long?,
    val total: Long,
    val hasMore: Boolean,
    val results: List<T>
) {
    companion object {
        private val EMPTY = PagedResult(null, null, 0, false, emptyList<Any>())
        fun <T> empty(): PagedResult<T> = EMPTY as PagedResult<T>
        fun <T> of(results: List<T>): PagedResult<T> =
            PagedResult(0, results.size.toLong(), results.size.toLong(), false, results)

        fun <T, N> PagedResult<T>.from(
            results: List<N>,
            start: Long? = this.start,
            limit: Long? = this.limit,
            total: Long = this.total,
            hasMore: Boolean = this.hasMore
        ): PagedResult<N> = PagedResult(start, limit, total, hasMore, results)
    }
}