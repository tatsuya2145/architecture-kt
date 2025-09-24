package infrastructure

import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import com.github.michaelbull.result.*
import org.ktorm.entity.update


interface ProductEntity : Entity<ProductEntity> {
    companion object : Entity.Factory<ProductEntity>()

    var id: Int
    var name: String
    var count: Int
}

object ProductTable : Table<ProductEntity>("products") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val count = int("count").bindTo { it.count }
}


fun findProduct(
    productId: Int
): Result<ProductEntity, String>  {
    return db.sequenceOf(ProductTable)
            .find { it.id eq productId }
        .toResultOr { "not found" }
}

fun saveProduct(
    productEntity: ProductEntity
) {
    db.sequenceOf(ProductTable)
        .update(productEntity)
}