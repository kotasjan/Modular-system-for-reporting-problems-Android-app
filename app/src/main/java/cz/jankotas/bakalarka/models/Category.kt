package cz.jankotas.bakalarka.models

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import cz.jankotas.bakalarka.R

/**
 * Datová třída pro kategorie, obsahuje i globální objekt obsahující seznam kategorií
 */
data class Category constructor(@NonNull val id: Int,
                                @NonNull val name: String,
                                @NonNull val icon: Drawable) {

    companion object {
        var categories: ArrayList<Category> = ArrayList()

        fun setCategories(context: Context) {
            categories.add(Category(1, context.getString(R.string.category_greens), context.getDrawable(R.drawable.ic_avatar_environment)!!))
            categories.add(Category(2, context.getString(R.string.category_garbage), context.getDrawable(R.drawable.ic_avatar_garbage)!!))
            categories.add(Category(3, context.getString(R.string.category_traffic), context.getDrawable(R.drawable.ic_avatar_traffic)!!))
            categories.add(Category(4, context.getString(R.string.category_street_furniture), context.getDrawable(R.drawable.ic_avatar_street_furniture)!!))
            categories.add(Category(5, context.getString(R.string.category_public_lighting), context.getDrawable(R.drawable.ic_avatar_public_lighting)!!))
        }
    }
}