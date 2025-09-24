package presentation

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.unwrap
import common.logger
import infrastructure.db
import infrastructure.findProduct
import infrastructure.saveProduct

data class DeliverRequest (
    val productId : Int,
    val count : Int
)

data class DeliverResponse (
    val saved : Boolean,
)

/**
 * オンライン注文サイトの商品に関するAPI
 */
class ProductController {
    // トランザクションスクリプトの例
    fun deliverProduct(
        deliver: DeliverRequest
    ):DeliverResponse {
        db.useTransaction {
            val product = findProduct(deliver.productId)
                .onFailure { err ->
                    logger.error { err }
                    return DeliverResponse(
                        saved = false
                    )
                }.unwrap()

            if(product.count < deliver.count) {
                logger.error { "exceed deliver request count" }
                return DeliverResponse(
                    saved = false
                )
            }
            product.count -= deliver.count
            saveProduct(product)
            return DeliverResponse(
                saved = true
            )
        }
    }
}