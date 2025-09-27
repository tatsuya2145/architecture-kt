package infrastructure

import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import com.github.michaelbull.result.*
import org.ktorm.schema.BaseTable
import org.ktorm.support.postgresql.insertOrUpdate

data class ProductEntity(
    val id: Int,
    var name: String,
    var count: Int
)

open class ProductTable(alias: String?) : BaseTable<ProductEntity>("products", alias) {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val count = int("count")

    override fun aliased(alias: String) = ProductTable(alias)

    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): ProductEntity {
        return ProductEntity(
            id = row.getValue(id),
            name = row.getValue(name),
            count = row.getValue(count),
        )
    }

    companion object : ProductTable(null)
}


fun findProduct(
    productId: Int
): Result<ProductEntity, String>  {
    return db.sequenceOf(ProductTable)
            .find { it.id eq productId }
        .toResultOr {
            "not found"
        }
}

fun updateProduct(
    productEntity: ProductEntity
) {
    db.update(ProductTable) {
        set(it.name, productEntity.name)
        set(it.count, productEntity.count)
        where {
            it.id eq productEntity.id
        }
    }
}

fun saveProduct(
    productEntity: ProductEntity
) {
    db.insertOrUpdate(ProductTable) {
        set(it.id, productEntity.id)
        set(it.name, productEntity.name)
        set(it.count, productEntity.count)
        onConflict(it.id) {
            set(it.name, productEntity.name)
            set(it.count, productEntity.count)
        }
    }
}


