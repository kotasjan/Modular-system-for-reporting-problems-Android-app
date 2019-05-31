package cz.jankotas.bakalarka.models

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import cz.jankotas.bakalarka.R

data class Category constructor(@NonNull val id: Int,
                                @NonNull val name: String,
                                @NonNull val icon: Drawable) {

    companion object {
        var categories: ArrayList<Category> = ArrayList()

        fun setCategories(context: Context) {
            categories.add(Category(1, context.getString(R.string.category_greens), context.getDrawable(R.drawable.ic_avatar_environment)!!))
            categories.add(Category(2, context.getString(R.string.category_garbage), context.getDrawable(R.drawable.ic_avatar_garbage)!!))
            categories.add(Category(3, context.getString(R.string.category_traffic), context.getDrawable(R.drawable.ic_avatar_traffic)!!))
        }
    }
}