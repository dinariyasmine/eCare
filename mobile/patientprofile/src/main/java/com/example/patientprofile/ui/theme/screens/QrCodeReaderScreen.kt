import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.patientprofile.ui.theme.screens.QrCodeScannerComposable

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
fun QrCodeReaderScreen(navController: NavController) {
    var scannedText by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box {
        if (scannedText == null) {
            QrCodeScannerComposable(
                onQrCodeScanned = { result ->
                    scannedText = result

                    // ✅ Log to Logcat
                    Log.d("QrCodeReader", "Scanned QR Code: $result")

                    // Optional: show a toast
                    Toast.makeText(context, "Scanned: $result", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            QRCodeScreen(qrString = scannedText!!)
        }
    }
}
