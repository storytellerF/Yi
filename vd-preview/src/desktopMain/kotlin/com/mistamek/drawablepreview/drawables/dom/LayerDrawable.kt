package com.mistamek.drawablepreview.drawables.dom

import com.mistamek.drawablepreview.drawables.ItemDrawableInflater
import com.mistamek.drawablepreview.drawables.Utils
import com.mistamek.drawablepreview.drawables.forEachAsElement
import org.w3c.dom.Element
import java.awt.image.BufferedImage
import java.lang.UnsupportedOperationException

class LayerDrawable : Drawable() {

    companion object {
        private const val ITEM_TAG = "item"
    }

    private val drawables = ArrayList<LayerDrawableItem>()

    override fun inflate(element: Element) {
        super.inflate(element)

        element.childNodes.forEachAsElement { childNode ->
            if (childNode.tagName == ITEM_TAG) {
                drawables.add(LayerDrawableItem((childNode)))
            }
        }
    }

    override fun draw(outputImage: BufferedImage) {
        super.draw(outputImage)
        resolveDimens(outputImage)
        drawables.forEach { it.draw(outputImage) }
    }

    private fun resolveDimens(image: BufferedImage) {
        val maxAttrSize = drawables.flatMap { listOf(it.height, it.width) }.max()
        if (maxAttrSize > 0) {
            throw UnsupportedOperationException()
        }

        val maxPaddingArg = drawables.flatMap { listOf(it.left, it.top, it.right, it.bottom) }.max().toFloat()
        val maxPaddingWidth = image.width * 0.3F

        drawables.forEach { layerDrawableItem ->
            layerDrawableItem.width = image.width
            layerDrawableItem.height = image.height

            layerDrawableItem.left = ((layerDrawableItem.left / maxPaddingArg) * maxPaddingWidth).toInt()
            layerDrawableItem.top = ((layerDrawableItem.top / maxPaddingArg) * maxPaddingWidth).toInt()
            layerDrawableItem.right = ((layerDrawableItem.right / maxPaddingArg) * maxPaddingWidth).toInt()
            layerDrawableItem.bottom = ((layerDrawableItem.bottom / maxPaddingArg) * maxPaddingWidth).toInt()
        }
    }
}

class LayerDrawableItem(element: Element) : Drawable() {
    companion object {
        private const val WIDTH = "android:width"
        private const val HEIGHT = "android:height"
        private const val TOP = "android:top"
        private const val LEFT = "android:left"
        private const val RIGHT = "android:right"
        private const val BOTTOM = "android:bottom"
        private const val START = "android:start"
        private const val END = "android:end"
    }

    var width = 0
    var height = 0

    var top = 0
    var left = 0
    var right = 0
    var bottom = 0

    var drawable: Drawable? = null

    init {
        inflate(element)
    }

    override fun inflate(element: Element) {
        super.inflate(element)

        width = Utils.parseAttributeAsInt(element.getAttribute(WIDTH), width)
        height = Utils.parseAttributeAsInt(element.getAttribute(HEIGHT), height)

        top = Utils.parseAttributeAsInt(element.getAttribute(TOP), top)
        left = Utils.parseAttributeAsInt(element.getAttribute(LEFT), left)
        right = Utils.parseAttributeAsInt(element.getAttribute(RIGHT), right)
        bottom = Utils.parseAttributeAsInt(element.getAttribute(BOTTOM), bottom)
        left = Utils.parseAttributeAsInt(element.getAttribute(START), left)
        right = Utils.parseAttributeAsInt(element.getAttribute(END), right)

        drawable = ItemDrawableInflater.getDrawableWithInflate(element)
    }

    override fun draw(outputImage: BufferedImage) {
        super.draw(outputImage)
        drawable?.also { drawable ->
            val resolvedWidth = width - left - right
            val resolvedHeight = height - top - bottom
            if (resolvedWidth <= 0 || resolvedHeight <= 0) {
                return
            }

            BufferedImage(resolvedWidth, resolvedHeight, BufferedImage.TYPE_INT_ARGB).also { imageWithInsets ->
                drawable.draw(imageWithInsets)
                outputImage.graphics.apply {
                    drawImage(imageWithInsets, left, top, resolvedWidth, resolvedHeight, null)
                    dispose()
                }
            }
        }
    }
}