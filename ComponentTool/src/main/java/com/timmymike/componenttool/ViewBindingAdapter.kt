package com.timmymike.componenttool


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**複製自 'com.github.carousell:MonoAdapter:2.0.0' 的MonaAdapter。
 * 使用方法：
 * val adapter = ViewBindingAdapter.create<AdapterMyDataBinding, MyData>(AdapterMyDataBinding::inflate) {
        // 寫在這裡的內容，會於 onBindViewHolder 內呼叫。
        val adapterPosition = listOf(MyData(...)).indexOf(it)
            textView.text = it.text1
            button.setOnClickListener {
                textView.text = it.text2
            }
        }.apply{
           viewHolderInitialCallback = { it -> // 第一次產生
               (it.binding.root as? ViewGroup)?.resetLayoutTextSize()
           }
           viewAttachedToWindowCallback = { it, position -> // 每一次更新畫面
                it.binding.ivSample.isSelected = selectedList[position]
            }
            submitList(listOf(MyData(...))) // 填入資料
        }
 // 指定adapter
   binding.vp2.adapter = adpater
 * 感謝Jintin大大，
 * 提供這麼簡潔有力的程式碼！
 * 因為有些小缺失，所以加了一個部分：viewHolderInitialCallback
 */
class ViewBindingAdapter<V : ViewBinding, T : Any>(
    private val viewProvider: (ViewGroup) -> V,
    private val binder: V.(T) -> Unit,
    diffCheck: DiffUtil.ItemCallback<T> = DefaultDiffCheck()
) : ListAdapter<T, ViewBindingAdapter.ViewHolder<V>>(diffCheck) {

    private class DefaultDiffCheck<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem?.equals(newItem)?:false
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder<V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)

    var viewRecycledCallback: ((ViewHolder<V>) -> Unit)? = null // View被回收時的callback
    var viewAttachedToWindowCallback: ((ViewHolder<V>, Int) -> Unit)? = null //  View每一次更新畫面時的callback
    var viewDetachedFromWindowCallback: ((ViewHolder<V>) -> Unit)? = null // View離開畫面上時的callback
    var attachedToRecyclerViewCallback: ((RecyclerView) -> Unit)? = null // View結合進RecycleView時的callback
    var viewHolderInitialCallback: ((ViewHolder<V>) -> Unit)? = null // 第一次新增物件，顯示在畫面上時的callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(viewProvider.invoke(parent)).apply{
            viewHolderInitialCallback?.invoke(this)
        }

    override fun onBindViewHolder(holder: ViewHolder<V>, position: Int) {
        binder.invoke(holder.binding, getItem(position))
    }

    override fun onViewRecycled(holder: ViewHolder<V>) {
        super.onViewRecycled(holder)
        viewRecycledCallback?.invoke(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedToRecyclerViewCallback?.invoke(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder<V>) {
        super.onViewAttachedToWindow(holder)
        viewAttachedToWindowCallback?.invoke(holder,holder.layoutPosition)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder<V>) {
        super.onViewDetachedFromWindow(holder)
        viewDetachedFromWindowCallback?.invoke(holder)
    }

    companion object {

        fun <V : ViewBinding, T:Any> create(
            bindingProvider: (LayoutInflater, ViewGroup?, Boolean) -> V,
            binder: V.(T) -> Unit
        ): ViewBindingAdapter<V, T> {
            return ViewBindingAdapter({
                bindingProvider.invoke(LayoutInflater.from(it.context), it, false)
            }, binder)
        }

        fun <V : ViewBinding, T:Any> create(
            bindingProvider: (LayoutInflater, ViewGroup?, Boolean) -> V,
            diffCheck: DiffUtil.ItemCallback<T>,
            binder: V.(T) -> Unit
        ): ViewBindingAdapter<V, T> {
            return ViewBindingAdapter({
                bindingProvider.invoke(LayoutInflater.from(it.context), it, false)
            }, binder, diffCheck)
        }

    }
}