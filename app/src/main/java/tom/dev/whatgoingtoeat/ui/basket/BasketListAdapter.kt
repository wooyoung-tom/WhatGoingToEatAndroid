package tom.dev.whatgoingtoeat.ui.basket

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tom.dev.whatgoingtoeat.databinding.ItemBasketBinding
import tom.dev.whatgoingtoeat.dto.order.OrderBasketItem
import tom.dev.whatgoingtoeat.dto.order.OrderDetailMenu

class BasketListAdapter(
    private val viewModel: BasketViewModel,
    private val onClickListeners: ClickListeners
) : ListAdapter<OrderBasketItem, BasketListAdapter.BasketViewHolder>(Companion) {

    interface ClickListeners {
        fun onDeleteButtonClickListener(item: OrderBasketItem, position: Int)
        fun onEditButtonClickListener(item: OrderBasketItem)
    }

    companion object : DiffUtil.ItemCallback<OrderBasketItem>() {
        override fun areItemsTheSame(oldItem: OrderBasketItem, newItem: OrderBasketItem) = oldItem.orderId == newItem.orderId
        override fun areContentsTheSame(oldItem: OrderBasketItem, newItem: OrderBasketItem) = oldItem == newItem
    }

    inner class BasketViewHolder(val binding: ItemBasketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderBasketItem) {
            binding.apply {
                tvItemBasketRestaurantName.text = item.restaurant.name
                tvItemBasketPrice.text = getTotalPriceStr(item.totalPrice)
                setMenuListTextView(item.orderDetailList)
            }
        }

        private fun getTotalPriceStr(price: Int) = "${price}원"

        private fun setMenuListTextView(menuList: List<OrderDetailMenu>) {
            var menuText = ""
            menuList.forEachIndexed { index, menu ->
                menuText += if (index != menuList.size - 1) {
                    "${getMenuDetailInfo(menu)}\n"
                } else getMenuDetailInfo(menu)
            }

            binding.tvItemBasketMenu.text = menuText
        }

        private fun getMenuDetailInfo(menu: OrderDetailMenu) = "${menu.menu.name} (${menu.menuCount})"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        return BasketViewHolder(
            ItemBasketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.binding.btnItemBasketEdit.setOnClickListener {
            onClickListeners.onEditButtonClickListener(getItem(position))
        }

        holder.binding.btnItemBasketDelete.setOnClickListener {
            onClickListeners.onDeleteButtonClickListener(getItem(position), position)
        }

        holder.binding.cardviewItemBasket.setOnClickListener {
            when (holder.binding.cardviewItemBasket.isChecked) {
                false -> {
                    holder.binding.cardviewItemBasket.isChecked = true
                    viewModel.addOrder(getItem(position))
                }
                else -> {
                    holder.binding.cardviewItemBasket.isChecked = false
                    viewModel.removeOrder(getItem(position))
                }
            }
        }
    }
}