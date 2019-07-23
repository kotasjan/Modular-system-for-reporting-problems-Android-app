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
import cz.jankotas.bakalarka.common.Common
import cz.jankotas.bakalarka.models.Category

/**
 * Adaptér pro zobrazení seznamu kategorií v aktivitě ReportGetCategoryActivity
 */
class CategoryAdapter(private var mCtx: Context, private var categories: List<Category>, private val onClickListener: (View, Category) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var lastHolder: CategoryViewHolder? = null

    // počet položek/kategorií
    override fun getItemCount(): Int {
        return categories.size
    }

    // předání ViewHolderu obsahujícího položku seznamu (kategorie)
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.category_list_item, parent, false))
    }

    // naplnění položky layoutu daty kategorie
    override fun onBindViewHolder(@NonNull holder: CategoryViewHolder, position: Int) {

        val category = categories[position] // získání objektu kategorie

        holder.icon.setImageDrawable(category.icon) // nastavení ikony kategorie
        holder.name.text = category.name // nastavení názvu kategorie

        // pokud je kategorie již označena, nastavit tmavé pozadí položky
        Common.newReport.category_id?.let {id ->
            if (category.id == id) {
                holder.card.setBackgroundColor(holder.itemView.context.getColor(R.color.colorPrimary))
                lastHolder = holder
            }
        }

        // pokud je na položku kliknuto, označit ji tmavým pozadím
        holder.itemView.setOnClickListener { view ->
            if (lastHolder != null) lastHolder!!.card.setBackgroundColor(view.context.getColor(R.color.colorWhitePrimary))
            holder.card.setBackgroundColor(view.context.getColor(R.color.colorPrimary))
            lastHolder = holder
            onClickListener.invoke(view, category)
        }
    }

    // vnitřní třída ViewHolderu, která obsahuje položky layoutu, které je možné upravovat/naplnit daty
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val icon: ImageView = itemView.findViewById(R.id.category_icon) as ImageView
        internal var name: TextView = itemView.findViewById(R.id.category_name) as TextView
        internal var card: CardView = itemView.findViewById(R.id.category_card) as CardView
    }
}