package tom.dev.whatgoingtoeat.repository

import io.reactivex.Single
import tom.dev.whatgoingtoeat.dto.CommonSimpleResponse
import tom.dev.whatgoingtoeat.dto.order.OrderBasketResponse
import tom.dev.whatgoingtoeat.dto.order.OrderSaveRequest
import tom.dev.whatgoingtoeat.service.OrderService
import javax.inject.Inject

class OrderRepositoryImpl
@Inject
constructor(
    private val orderService: OrderService
) : OrderRepository {
    override fun saveOrder(order: OrderSaveRequest): Single<CommonSimpleResponse> {
        return orderService.saveOrder(order)
    }

    override fun findOrders(userId: Long): Single<OrderBasketResponse> {
        return orderService.findOrders(userId)
    }
}