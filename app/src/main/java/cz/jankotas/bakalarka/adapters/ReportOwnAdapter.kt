package cz.jankotas.bakalarka.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.models.Report
import java.text.SimpleDateFormat
import java.util.*

class ReportOwnAdapter(private var mCtx: Context,
                       private val onClickListener: (View, Report) -> Unit) : PagedListAdapter<Report, ReportOwnAdapter.ReportViewHolder>(DIFF_CALLBACK) {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.report_list_item, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ReportViewHolder, position: Int) {

        val report = getItem(position)

        if (report != null) {

            setIcon(report, holder)
            holder.headline.text = report.title
            holder.city.text = report.address
            holder.date.text = getDate(report.created_at)

            if(report.photos!!.isNotEmpty()) {
                Glide.with(mCtx).load(report.photos[0]).into(holder.image)

                holder.itemView.setOnClickListener { view ->
                    onClickListener.invoke(view, report)
                }
            }

        } else {
            Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show()
        }

    }

    private fun setIcon(report: Report,holder: ReportViewHolder) {
        when (report.category_id) {
            1 -> holder.icon.setImageDrawable(ContextCompat.getDrawable(holder.icon.context, R.drawable.ic_avatar_environment))
            2 -> holder.icon.setImageDrawable(ContextCompat.getDrawable(holder.icon.context, R.drawable.ic_avatar_garbage))
            3 -> holder.icon.setImageDrawable(ContextCompat.getDrawable(holder.icon.context, R.drawable.ic_avatar_traffic))
        }
    }

    private fun getDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyy", Locale.GERMANY)
        return format.format(date)
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val icon: ImageView = itemView.findViewById(R.id.card_icon) as ImageView
        internal var headline: TextView = itemView.findViewById<View>(R.id.card_headline) as TextView
        internal var city: TextView = itemView.findViewById<View>(R.id.card_city) as TextView
        internal var date: TextView = itemView.findViewById<View>(R.id.card_elapsed_time) as TextView
        internal val image: ImageView = itemView.findViewById(R.id.report_card_image) as ImageView
    }

    companion object {

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