package com.rotarola.portafolio_kotlin.core.utils

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
    previewSize: Size,
    density: Float = 1f
): Bitmap {
    val centerX = previewSize.width / 2f + rectOffset.x * density
    val centerY = previewSize.height / 2f + rectOffset.y * density

    val rectWidthPx = rectWidth.value * density
    val rectHeightPx = rectHeight.value * density

    val left = (centerX - rectWidthPx / 2f).coerceAtLeast(0f)
    val top = (centerY - rectHeightPx / 2f).coerceAtLeast(0f)
    val right = (left + rectWidthPx).coerceAtMost(bitmap.width.toFloat())
    val bottom = (top + rectHeightPx).coerceAtMost(bitmap.height.toFloat())

    // Calcular las proporciones de escala
    val scaleX = bitmap.width.toFloat() / previewSize.width
    val scaleY = bitmap.height.toFloat() / previewSize.height

    val scaledLeft = (left * scaleX).toInt().coerceAtLeast(0)
    val scaledTop = (top * scaleY).toInt().coerceAtLeast(0)
    val scaledWidth = ((right - left) * scaleX).toInt().coerceAtMost(bitmap.width - scaledLeft)
    val scaledHeight = ((bottom - top) * scaleY).toInt().coerceAtMost(bitmap.height - scaledTop)

    return if (scaledWidth > 0 && scaledHeight > 0) {
        Bitmap.createBitmap(
            bitmap,
            scaledLeft,
            scaledTop,
            scaledWidth,
            scaledHeight
        )
    } else {
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