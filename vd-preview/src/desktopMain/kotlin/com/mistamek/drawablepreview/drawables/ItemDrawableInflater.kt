package com.mistamek.drawablepreview.drawables

import com.mistamek.drawablepreview.factories.IconPreviewFactory
import com.mistamek.drawablepreview.factories.XmlImageFactory
import com.mistamek.drawablepreview.drawables.dom.ColorDrawable
import com.mistamek.drawablepreview.drawables.dom.Drawable
import com.mistamek.drawablepreview.drawables.dom.IconDrawable
import org.w3c.dom.Element

object ItemDrawableInflater {
    private const val DRAWABLE = "android:drawable"

    fun getDrawableWithInflate(element: Element, size: Int): Drawable? {
        return getDrawable(element, size).let {
            val drawable = it.second
            val elementToUse = it.first
            if (elementToUse != null) {
                drawable?.inflate(elementToUse)
            }
            drawable
        }
    }

    fun getDrawable(element: Element, size: Int): Pair<Element?, Drawable?> {
        if (element.hasAttribute(DRAWABLE)) {
            return null to getDrawableFromAttribute(element, size)
        } else if (element.hasChildNodes()) {
            return getDrawableFromChild(element, size)
        }
        return element to null
    }

    private fun getDrawableFromAttribute(element: Element, size: Int): Drawable {
        val drawableAttr = element.getAttribute(DRAWABLE)
        return if (drawableAttr.startsWith("#")) {
            ColorDrawable(drawableAttr, size)
        } else {
            XmlImageFactory.getDrawable(drawableAttr, size)
                ?: Utils.getPsiFileFromPath(drawableAttr).let {
                    IconDrawable().apply { childImage = IconPreviewFactory.getImage(it, size) }
                }
        }
    }

    private fun getDrawableFromChild(element: Element, size: Int): Pair<Element, Drawable?> {
        element.childNodes.forEachAsElement { childNode ->
            return childNode to DrawableInflater.getDrawable(childNode, size)
        }
        return element to null
    }
}