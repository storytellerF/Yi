package com.mistamek.drawablepreview.factories

import com.android.ide.common.vectordrawable.VdPreview
import com.mistamek.drawablepreview.drawables.DrawableInflater
import com.mistamek.drawablepreview.drawables.dom.Drawable
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.image.BufferedImage
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XmlImageFactory {

    fun createXmlImage(path: String, size: Int): BufferedImage? {
        val document = parseDocument(path) ?: return null

        val drawableImage = getDrawableImage(document.documentElement, size)
        if (drawableImage != null) {
            return drawableImage
        } else {
            val clazz = VdPreview.TargetSize::class.java
            val imageTargetSize = clazz.methods.firstNotNullOfOrNull { method ->
                when (method.name) {
                    "createSizeFromWidth", "createFromMaxDimension" ->
                        method.invoke(null, size) as? VdPreview.TargetSize

                    else -> null
                }
            }

            return imageTargetSize?.let {
                val builder = StringBuilder(100)
                VdPreview.getPreviewFromVectorDocument(imageTargetSize, document, builder).apply {
                    println(builder.toString())
                }
            }
        }
    }

    fun getDrawable(path: String, size: Int): Drawable? =
        parseDocument(path)?.let { DrawableInflater.getDrawable(it.documentElement, size) }

    private fun parseDocument(path: String): Document? {
        val supportedFolder = Constants.SUPPORTED_FOLDERS.fold(false) { acc, next -> acc || path.contains(next) }
        if (!(path.endsWith(Constants.XML_TYPE) && supportedFolder)) {
            return null
        }

        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(File(path)) ?: return null
//        val root = document.documentElement ?: return null
//        val resolver = getResourceResolver(Utils.getPsiFileFromPath(path))
//        if (resolver != null) {
//            replaceResourceReferences(root, resolver)
//        }
        return document
    }

    private fun getDrawableImage(rootElement: Element, size: Int): BufferedImage? {
        return DrawableInflater.getDrawable(rootElement, size)?.let { drawable ->
            return@let BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB).also { image ->
                drawable.draw(image)
            }
        }
    }

//    private fun replaceResourceReferences(node: Node, resolver: ResourceResolver) {
//        if (node.nodeType == Node.ELEMENT_NODE) {
//            node.attributes.forEach { attribute ->
//                val value = attribute.nodeValue
//                if (isReference(value)) {
//                    val resolvedValue = resolveStringValue(resolver, value)
//                    if (!isReference(resolvedValue)) {
//                        attribute.nodeValue = resolvedValue
//                    }
//                }
//            }
//        }
//
//        var newNode = node.firstChild
//        while (newNode != null) {
//            replaceResourceReferences(newNode, resolver)
//            newNode = newNode.nextSibling
//        }
//    }

//    private fun resolveStringValue(resolver: ResourceResolver, value: String): String {
//        val resValue = findResValue(resolver, value) ?: return value
//        return resolveNullableResValue(resolver, resValue)?.value ?: value
//    }
//
//    private fun findResValue(resolver: ResourceResolver, value: String): ResourceValue? {
//        return resolver.dereference(ResourceValueImpl(ResourceNamespace.RES_AUTO, ResourceType.ID, "com.android.ide.common.rendering.api.RenderResources", value))
//    }
//
//    private fun resolveNullableResValue(resolver: ResourceResolver, res: ResourceValue?): ResourceValue? {
//        if (res == null) {
//            return null
//        }
//        return resolver.resolveResValue(res)
//    }
//
//    private fun isReference(attributeValue: String) = ResourceUrl.parse(attributeValue) != null
//
//    private fun getResourceResolver(element: PsiFile?): ResourceResolver? {
//        return element?.let {
//            ProjectRootManager.getInstance(element.project).fileIndex.getModuleForFile(element.virtualFile)?.let {
//                ConfigurationManager.getOrCreateInstance(it).getConfiguration(element.virtualFile).resourceResolver
//            }
//        }
//    }
}