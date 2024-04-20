package com.mistamek.drawablepreview.factories

import com.mistamek.drawablepreview.drawables.Utils
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object BitmapImageFactory {
    fun createBitmapImage(path: String, size: Int): BufferedImage? {
        try {
            return ImageIO.read(File(path))?.let {
                val output = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
                Utils.drawResizedIcon(it, output)
                output
            }
        } catch (e: Exception) {
            throw Exception(path, e)
        }

    }
}