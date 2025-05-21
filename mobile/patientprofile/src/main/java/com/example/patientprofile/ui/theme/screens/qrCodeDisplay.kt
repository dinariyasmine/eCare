import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

import android.util.Base64
import androidx.annotation.RequiresApi

@Composable
fun QRCodeComposable(qrString: String, modifier: Modifier = Modifier, size: Int = 300) {
    val bitmap = generateQRCodeBitmap(qrString, size, size)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = modifier.size(size.dp)
    )
}

fun generateQRCodeBitmap(text: String, width: Int, height: Int): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

@Composable
fun QRCodeScreen(qrString: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            QRCodeComposable(qrString = qrString)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
fun QRCodeFromBase64(base64Str: String, modifier: Modifier = Modifier) {
    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Decoded QR Code",
            modifier = modifier.size(300.dp)
        )
    }
}