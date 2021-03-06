package ch.hearc.fidarc.ui.client.scan

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import ch.hearc.fidarc.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.*
import java.lang.IllegalStateException


class ScanFragmentUser : Fragment() {


    private var bitmap: Bitmap? = null
    private var iv: ImageView? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_scan_user, container, false)

        val sharedPref = activity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userID = sharedPref.getInt("id", -1)


        val textView: TextView = root.findViewById(R.id.text_scan)
        textView.text = resources.getString(R.string.text_qrCode_display)

        serviceScope.launch(Dispatchers.IO) {
            if (isAdded) {
                bitmap = textToImageEncode(userID.toString())
                iv = root.findViewById(R.id.iv)
                withContext(Dispatchers.Main) {
                    iv!!.setImageBitmap(bitmap)
                }
            }
        }

        return root
    }

    @Throws(WriterException::class)
    private fun textToImageEncode(Value: String): Bitmap? {

        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(Value, BarcodeFormat.QR_CODE, 800, 800, null)
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {

                if(isAdded) {
                    pixels[offset + x] = if (bitMatrix.get(x, y))
                        resources.getColor(R.color.green)
                    else
                        resources.getColor(R.color.white)
                } else {
                    return null
                }
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

        bitmap.setPixels(pixels, 0, 800, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}