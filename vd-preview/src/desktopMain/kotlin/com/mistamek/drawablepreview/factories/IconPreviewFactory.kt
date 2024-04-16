package com.mistamek.drawablepreview.factories

//import com.android.tools.adtui.ImageUtils
//import com.intellij.psi.PsiManager
//import com.intellij.util.ui.UIUtil
import java.awt.image.BufferedImage

object IconPreviewFactory {
//    var psiManager: PsiManager? = null
//        private set

//    fun createIcon(element: okio.Path): Icon? {
//        return try {
//            val result = getIcon(element)
////            psiManager = null
//            result
//        } catch (e: Exception) {
//            e.printStackTrace(System.out)
//            null
//        } finally {
////            psiManager = null
//        }
//    }
//
//    private fun getIcon(virtualFile: okio.Path): Icon? {
//        return getImage(virtualFile)?.let { image ->
//            if (UIUtil.isRetina()) {
//                val retinaIcon = getRetinaIcon(image)
//                if (retinaIcon != null) {
//                    return retinaIcon
//                }
//            }
//
//            return ImageIcon(image)
//        }
//    }

    fun getImage(virtualFile: okio.Path): BufferedImage? {
        virtualFile.toString().let { path ->
            return when {
                path.endsWith(Constants.XML_TYPE) -> XmlImageFactory.createXmlImage(path)
                path.endsWith(Constants.SVG_TYPE) -> SvgImageFactory.createSvgImage(path)
                else -> BitmapImageFactory.createBitmapImage(path)
            }
        }
    }

//    private fun getRetinaIcon(image: BufferedImage) =
//        takeIf { UIUtil.isRetina() }?.let { ImageUtils.convertToRetina(image) }?.let { RetinaImageIcon(it) }
}