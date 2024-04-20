package com.mistamek.drawablepreview.drawables

import com.mistamek.drawablepreview.drawables.dom.AdaptiveIconDrawable
import com.mistamek.drawablepreview.drawables.dom.BitmapDrawable
import com.mistamek.drawablepreview.drawables.dom.ColorDrawable
import com.mistamek.drawablepreview.drawables.dom.Drawable
import com.mistamek.drawablepreview.drawables.dom.GradientDrawable
import com.mistamek.drawablepreview.drawables.dom.InsetDrawable
import com.mistamek.drawablepreview.drawables.dom.LayerDrawable
import com.mistamek.drawablepreview.drawables.dom.LevelListDrawable
import com.mistamek.drawablepreview.drawables.dom.RippleDrawable
import com.mistamek.drawablepreview.drawables.dom.RotateDrawable
import com.mistamek.drawablepreview.drawables.dom.ScaleDrawable
import com.mistamek.drawablepreview.drawables.dom.SelectorDrawable
import org.w3c.dom.Element

object DrawableInflater {

    private const val SELECTOR_DRAWABLE = "selector"
    private const val ANIMATED_SELECTOR_DRAWABLE = "animated-selector"
    private const val LEVEL_LIST_DRAWABLE = "level-list"
    private const val LAYER_DRAWABLE = "layer-list"
    private const val TRANSITION_DRAWABLE = "transition"
    private const val RIPPLE = "ripple"
    private const val ADAPTIVE_ICON = "adaptive-icon"
    private const val COLOR_DRAWABLE = "color"
    private const val SHAPE_DRAWABLE = "shape"
    private const val SCALE = "scale"
    private const val CLIP = "clip"
    private const val ROTATE = "rotate"
    private const val ANIMATED_ROTATE = "animated-rotate"
    private const val INSET_DRAWABLE = "inset"
    private const val BITMAP = "bitmap"

    fun getDrawable(element: Element, size: Int): Drawable? {
        val drawable = createDrawable(element, size)
        drawable?.inflate(element)
        return drawable
    }

    private fun createDrawable(element: Element, size: Int): Drawable? {
        return when (element.tagName) {
            SELECTOR_DRAWABLE -> SelectorDrawable(size)
            ANIMATED_SELECTOR_DRAWABLE -> SelectorDrawable(size)
            LEVEL_LIST_DRAWABLE -> LevelListDrawable(size)
            LAYER_DRAWABLE -> LayerDrawable(size)
            TRANSITION_DRAWABLE -> LayerDrawable(size)
            RIPPLE -> RippleDrawable(size)
            ADAPTIVE_ICON -> AdaptiveIconDrawable(size)
            COLOR_DRAWABLE -> ColorDrawable(size)
            SHAPE_DRAWABLE -> GradientDrawable(size)
            SCALE -> ScaleDrawable(size)
            CLIP -> ItemDrawableInflater.getDrawable(element, size).second
            ROTATE -> RotateDrawable(size)
            ANIMATED_ROTATE -> RotateDrawable(size)
            INSET_DRAWABLE -> InsetDrawable(size)
            BITMAP -> BitmapDrawable(size)
            else -> null
        }
    }
}