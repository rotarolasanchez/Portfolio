package com.rotarola.portafolio_kotlin.core.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp


fun cropBitmapToGuideRect(
    bitmap: Bitmap,
    rectWidth: Dp,
    rectHeight: Dp,
    rectOffset: Offset,
    previewSize: Size
): Bitmap {
    val density = Resources.getSystem().displayMetrics.density

    // Convertir las dimensiones del rectángulo guía a píxeles
    val guideWidthPx = rectWidth.value * density
    val guideHeightPx = rectHeight.value * density

    // Calcular la posición del rectángulo en el preview considerando el offset
    val previewCenterX = previewSize.width / 2f
    val previewCenterY = previewSize.height / 2f

    // Aplicar el offset desde el centro
    val guideLeft = previewCenterX - (guideWidthPx / 2f) + (rectOffset.x * density)
    val guideTop = previewCenterY - (guideHeightPx / 2f) + (rectOffset.y * density)

    // Calcular las proporciones para mapear del preview al bitmap original
    val scaleX = bitmap.width.toFloat() / previewSize.width
    val scaleY = bitmap.height.toFloat() / previewSize.height
    val scale = minOf(scaleX, scaleY)

    // Calcular las dimensiones del bitmap escalado
    val scaledBitmapWidth = bitmap.width / scale
    val scaledBitmapHeight = bitmap.height / scale

    // Calcular el offset para centrar el bitmap escalado en el preview
    val bitmapOffsetX = (previewSize.width - scaledBitmapWidth) / 2f
    val bitmapOffsetY = (previewSize.height - scaledBitmapHeight) / 2f

    // Calcular las coordenadas de recorte en el bitmap original
    val cropLeft = ((guideLeft - bitmapOffsetX) * scale).toInt()
        .coerceIn(0, bitmap.width - 1)
    val cropTop = ((guideTop - bitmapOffsetY) * scale).toInt()
        .coerceIn(0, bitmap.height - 1)
    val cropWidth = (guideWidthPx * scale).toInt()
        .coerceAtMost(bitmap.width - cropLeft)
    val cropHeight = (guideHeightPx * scale).toInt()
        .coerceAtMost(bitmap.height - cropTop)

    return try {
        Bitmap.createBitmap(
            bitmap,
            cropLeft,
            cropTop,
            cropWidth,
            cropHeight
        )
    } catch (e: Exception) {
        Log.e("CropBitmap", "Error cropping bitmap: ${e.message}")
        bitmap
    }
}

fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
    try {
        val exif = ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        Log.e("ImageOrientation", "Error correcting image orientation: ${e.message}")
        return bitmap
    }
}