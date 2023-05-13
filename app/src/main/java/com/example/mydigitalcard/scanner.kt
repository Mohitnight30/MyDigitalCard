package com.example.mydigitalcard

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mydigitalcard.databinding.ActivityScannerBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.io.IOException


class scanner : AppCompatActivity() {

    lateinit var binding: ActivityScannerBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private var scannedValue = ""
    lateinit var cameraSource: CameraSource
    lateinit var Userid: String
    lateinit var tcheck: String
    lateinit var tname: String
    lateinit var temail: String
    lateinit var tnumber: String
    lateinit var tgithub: String
    lateinit var taddresh: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.carsview.visibility = View.GONE
        binding.txtBarcodeValue.visibility = View.GONE
        binding.btnAction.visibility = View.INVISIBLE

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        }
        setupControls()
        binding.btnAction.setOnClickListener(View.OnClickListener {
            addcontact()
        })
    }

    private fun addcontact() {
        val user = Firebase.auth.currentUser
        user?.let {
            Userid = it.uid
        }
        var contactDetails = contactDetails(
            tname,
            tnumber,
            tgithub,
            taddresh,
            temail)

        var refrence = FirebaseDatabase.getInstance().getReference("users").child(Userid)
        refrence.child("contact").child(temail).setValue(contactDetails).addOnCompleteListener(
            OnCompleteListener(fun(it: Task<Void>) {
                if (it.isSuccessful()) {
                    Toast.makeText(this, "Data save Succrssfully", Toast.LENGTH_SHORT)
                        .show()
                    var i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                } else {
                    Toast.makeText(this, " Please try again later", Toast.LENGTH_LONG)
                        .show()
                }

            })
        )
    }

    private fun setupControls() {
        var barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(binding.surfaceView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) {
                try {
                    cameraSource.start(binding.surfaceView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
//                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                binding.txtBarcodeValue.visibility = View.GONE
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    scannedValue = barcodes.valueAt(0).displayValue
                    val valuesArray = scannedValue.split("|").toTypedArray()
                    if (valuesArray.isNotEmpty()) {
                        var tcheck = valuesArray[0]
                        var tname = valuesArray[1]
                        var temail = valuesArray[2]
                        var tnumber = valuesArray[3]
                        var tgithub = valuesArray[4]
                        var taddresh = valuesArray[5]

                        binding.txtBarcodeValue.post(Runnable {
                            if (tcheck == "MYDC") {
                                binding.name.setText("Name : $tname")
                                binding.email.setText("Email : $temail")
                                binding.number.setText("Contact : $tnumber")
                                binding.github.setText("Github : $tgithub")
                                binding.addresh.setText("Addresh : $taddresh")
                                binding.carsview.visibility = View.VISIBLE
                                binding.surfaceView.visibility = View.GONE
                                binding.btnAction.visibility = View.VISIBLE
                            } else {
                                binding.txtBarcodeValue.visibility = View.VISIBLE
                            }
                        })
                    }

                }

            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        setupControls()
    }

}
