package infrastructure

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.Column

fun <T: Any> QueryRowSet.getValue(column: Column<T>): T {
    return get(column) ?: error("Column $column is null")
}