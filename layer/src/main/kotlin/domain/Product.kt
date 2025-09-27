package domain

import com.github.michaelbull.result.*
import infrastructure.ProductEntity
import infrastructure.findProduct
import infrastructure.saveProduct

data class Product(
    val id: Int,
    var name: String,
    var count: Int
) {

    private fun checkAvailability(targetCount: Int): Boolean {
        return count >= targetCount
    }

    fun withdraw(targetCount: Int): Result<Boolean,String> {
        return if (checkAvailability(targetCount)) {
            count -= targetCount
            Ok(true)
        } else {
            Err("exceed request count")
        }
    }

    fun save() {
        val entity = ProductEntity(
            id = id,
            name = name,
            count = count
        )
        saveProduct(entity)
    }

    companion object {
        fun find(id: Int): Result<Product, String> {
            val entity = findProduct(id)
                .onFailure { err -> return Err(err)}
                .unwrap()
            return Ok(
                Product(
                    id = entity.id,
                    name = entity.name,
                    count = entity.count
                )
            )
        }
    }
}
