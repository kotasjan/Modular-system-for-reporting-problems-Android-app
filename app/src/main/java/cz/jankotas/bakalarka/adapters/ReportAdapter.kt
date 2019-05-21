package cz.jankotas.bakalarka.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.jankotas.bakalarka.R
import cz.jankotas.bakalarka.models.Report
import kotlin.collections.ArrayList

class ReportAdapter(private val mDataList: ArrayList<Report>) : RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.report_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.icon.setImageDrawable(ContextCompat.getDrawable(holder.icon.context, R.drawable.ic_avatar_environment))
        holder.headline.text = mDataList[position].title
        holder.city.text = mDataList[position].address
        holder.elapsedTime.text = "před 2 dny" // TODO dodelat rozdil casu

        holder.image.setImageDrawable(ContextCompat.getDrawable(holder.image.context, R.drawable.default_profile))
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val icon: ImageView = itemView.findViewById(R.id.card_icon) as ImageView
        internal var headline: TextView = itemView.findViewById<View>(R.id.card_headline) as TextView
        internal var city: TextView = itemView.findViewById<View>(R.id.card_city) as TextView
        internal var elapsedTime: TextView = itemView.findViewById<View>(R.id.card_elapsed_time) as TextView
        internal val image: ImageView = itemView.findViewById(R.id.report_card_image) as ImageView
    }
}
