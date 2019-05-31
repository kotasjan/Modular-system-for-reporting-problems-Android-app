package cz.jankotas.bakalarka.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.models.Category

class CategoryAdapter(private var mCtx: Context, private var categories: List<Category>, private val onClickListener: (View, Category) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var lastHolder: CategoryViewHolder? = null

    override fun getItemCount(): Int {
        return categories.size
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.category_list_item, parent, false))
    }

    override fun onBindViewHolder(@NonNull holder: CategoryViewHolder, position: Int) {

        val category = categories[position]

        holder.icon.setImageDrawable(category.icon)
        holder.name.text = category.name

        holder.itemView.setOnClickListener { view ->
            if (lastHolder != null) lastHolder!!.card.setBackgroundColor(view.context.getColor(R.color.colorWhitePrimary))
            holder.card.setBackgroundColor(view.context.getColor(R.color.colorPrimary))
            lastHolder = holder
            onClickListener.invoke(view, category)
        }
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val icon: ImageView = itemView.findViewById(R.id.category_icon) as ImageView
        internal var name: TextView = itemView.findViewById(R.id.category_name) as TextView
        internal var card: CardView = itemView.findViewById(R.id.category_card) as CardView
    }
}