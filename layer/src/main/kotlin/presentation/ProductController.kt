package presentation

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.unwrap
import common.logger
import domain.Product
import infrastructure.db
import infrastructure.findProduct
import infrastructure.updateProduct


data class DeliverRequest (
    val productId : Int,
    val count : Int
)

data class DeliverResponse (
    val saved : Boolean = false,
    val errorMessage: String? = null
)

/**
 * オンライン注文サイトの商品に関するAPI
 */
class ProductController {
    // トランザクションスクリプトの例
    fun deliverProductForTransactionScript(
        deliver: DeliverRequest,
    ):DeliverResponse {
        db.useTransaction {
            val product = findProduct(deliver.productId)
                .onFailure { err ->
                    logger.error { err }
                    return DeliverResponse( errorMessage = err)
                }.unwrap()

            if(product.count < deliver.count) {
                val message = "exceed request count"
                logger.error { message }
                return DeliverResponse( errorMessage = message )
            }
            product.count -= deliver.count
            updateProduct(product)
            return DeliverResponse( saved = true )
        }
    }

    //アクティブレコードの例
    fun deliverProductForActiveRecord(
        deliver: DeliverRequest,
    ):DeliverResponse {
        db.useTransaction {
            val product = Product.find(deliver.productId)
                .onFailure { err ->
                    logger.error { err }
                    return DeliverResponse( errorMessage = err )
                }.unwrap()

            product.withdraw(deliver.count)
                .onFailure { err ->
                    logger.error { err }
                    return DeliverResponse( errorMessage = err )
                }

            product.save()
            return DeliverResponse( saved = true )

        }
    }
}