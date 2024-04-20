package com.mistamek.drawablepreview.factories

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO

object SvgImageFactory {

    fun createSvgImage(
        path: String,
        size: Int
    ): BufferedImage? {
        val transcoder = PNGTranscoder()
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, size.toFloat())
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, size.toFloat())

        try {
            val inputStream = FileInputStream(File(path))
            val input = TranscoderInput(inputStream)

            val outputStream = ByteArrayOutputStream()
            val output = TranscoderOutput(outputStream)

            transcoder.transcode(input, output)

            outputStream.flush()
            outputStream.close()

            val imgData = outputStream.toByteArray()
            return ImageIO.read(ByteArrayInputStream(imgData))
        } catch (exception: Exception) {
            return null
        }
    }
}