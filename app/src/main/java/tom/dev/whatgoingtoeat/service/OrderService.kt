package tom.dev.whatgoingtoeat.service

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import tom.dev.whatgoingtoeat.dto.order.OrderSaveRequestItem
import tom.dev.whatgoingtoeat.dto.order.OrderSaveResponse

interface OrderService {

    @POST("/market/orders/{id}")
    fun saveOrder(
        @Path("id") userId: Long,
        @Body order: List<OrderSaveRequestItem>
    ): Single<OrderSaveResponse>
}