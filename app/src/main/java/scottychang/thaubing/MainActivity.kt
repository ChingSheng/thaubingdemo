package scottychang.thaubing

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import scottychang.thaubing.ui.scan.ScanFragment

class MainActivity : AppCompatActivity() {
    private val PERMISSION_ALL = 1
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (hasPermissions()) {
            if (savedInstanceState == null) {
                showScanFragment()
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
    }

    private fun showScanFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ScanFragment.newInstance())
            .commitNow()
    }

    private fun hasPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for (grantResult:Int in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Need camera permission!", Toast.LENGTH_LONG).show()
                Handler().postDelayed({finish()}, 3000)
                return
            }
        }
        showScanFragment()
    }
}
