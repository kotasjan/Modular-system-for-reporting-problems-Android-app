package cz.jankotas.bakalarka.adapters

import android.content.Context
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.PopupMenu
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.models.Category
import cz.jankotas.bakalarka.models.Report
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptér pro zobrazení uzavřených reportů ve fragmentu MainTabClosed
 */
class ReportClosedAdapter(private var mCtx: Context, private val onClickListener: (View, Report) -> Unit) :
    PagedListAdapter<Report, ReportClosedAdapter.ReportViewHolder>(DIFF_CALLBACK) {

    // předání ViewHolderu obsahujícího layout položky podnětu
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.report_list_item, parent, false)
        return ReportViewHolder(view)
    }

    // naplnění položky layoutu daty podnětu
    override fun onBindViewHolder(@NonNull holder: ReportViewHolder, position: Int) {

        val report = getItem(position) // získání objektu podnětu

        if (report != null) {

            setIcon(report, holder) // nastavení ikony kategorie
            holder.headline.text = report.title // nastavení titulku
            holder.city.text = report.address // nastavení adresy
            holder.date.text = getDate(report.created_at) // datum přidání

            // vložení první fotografie podnětu jako jeho náhled
            if (report.photos!!.isNotEmpty()) Glide.with(mCtx).load(report.photos[0]).placeholder(R.drawable.photo_placeholder).into(
                holder.image)

            holder.itemView.setOnClickListener { view ->
                onClickListener.invoke(view, report)
            }

            // po kliknutí na ikonku teček, zobrazit menu
            holder.btnMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, holder.btnMenu, Gravity.START)
                popupMenu.menuInflater.inflate(R.menu.menu_cardview_report, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.action_detail -> {
                            onClickListener.invoke(view, report) // zobrazení detailu podnětu
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
                popupMenu.show() // zobrazení menu
            }

        } else {
            Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show()
        }
    }

    // nastavení ikony kategorie v položce podnětu
    private fun setIcon(report: Report, holder: ReportViewHolder) {
        for (category in Category.categories) {
            if (category.id == report.category_id) {
                holder.icon.setImageDrawable(category.icon)
                break
            }
        }
    }

    // funkce vrací datum ve tvaru DD/MM/YYYY
    private fun getDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyy", Locale.GERMANY)
        return format.format(date)
    }

    // vnitřní třída ViewHolderu, která obsahuje položky layoutu, které je možné upravovat/naplnit daty
    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val icon: ImageView = itemView.findViewById(R.id.card_icon) as ImageView
        internal var headline: TextView = itemView.findViewById<View>(R.id.card_headline) as TextView
        internal var city: TextView = itemView.findViewById<View>(R.id.card_city) as TextView
        internal var date: TextView = itemView.findViewById<View>(R.id.card_elapsed_time) as TextView
        internal val image: ImageView = itemView.findViewById(R.id.report_card_image) as ImageView
        internal val btnMenu: ImageButton = itemView.findViewById(R.id.card_more_button) as ImageButton
    }

    companion object {

        // srovnání, zda se předchozí zobrazené podněty shodují s aktuálními (důležité pro RecyclerView, jestli se má znovu vykreslovat)
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Report>() {
            override fun areItemsTheSame(oldReport: Report, newReport: Report): Boolean {
                return oldReport.id == newReport.id
            }

            override fun areContentsTheSame(oldReport: Report, newReport: Report): Boolean {
                return oldReport == newReport
            }
        }
    }
}