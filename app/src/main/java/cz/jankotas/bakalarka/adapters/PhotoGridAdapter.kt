package cz.jankotas.bakalarka.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nguyenhoanglam.imagepicker.model.Image
import cz.jankotas.bakalarka.common.Common
import kotlinx.android.synthetic.main.activity_report_get_photos.view.*
import kotlinx.android.synthetic.main.photo_image_view.view.*
import android.R
import android.app.Activity
import android.widget.Button
import android.widget.TextView



class PhotoGridAdapter(private var context: Context, private val view: View, private var images: List<Image>) :
    RecyclerView.Adapter<PhotoGridAdapter.ImageViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(context).inflate(cz.jankotas.bakalarka.R.layout.photo_image_view, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(context)
            .load(image.path)
            .centerCrop()
            .apply(RequestOptions().placeholder(cz.jankotas.bakalarka.R.drawable.image_placeholder).error(cz.jankotas.bakalarka.R.drawable.image_placeholder))
            .placeholder(cz.jankotas.bakalarka.R.drawable.photo_placeholder)
            .into(holder.reportPhotoLayout.report_photo_iv)

        holder.reportPhotoLayout.delete_photo_iv.setOnClickListener {
            Common.selectedImages.remove(image)
            this.images = Common.selectedImages
            if (images.isEmpty()) view.visibility = View.GONE
            notifyDataSetChanged()
        }
    }

    fun setData(images: ArrayList<Image>?) {
        if (images != null) this.images = images
        notifyDataSetChanged()
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reportPhotoLayout: View = view.report_photo_layout
    }
}