package com.example.mydigitalcard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mydigitalcard.databinding.ActivityEditProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.ByteArrayOutputStream
import java.util.EnumMap

class Edit_profile : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    lateinit var profilename: String
    lateinit var profilemail: String
    lateinit var profilephone: String
    lateinit var profilegithub: String
    lateinit var profileaddress: String
    lateinit var myText: String
    lateinit var mAuth: FirebaseAuth
    lateinit var Userid: String
    lateinit var name: String
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        user?.let {
            name = it.displayName.toString()
            email = it.email.toString()
            Userid = it.uid
        }

        binding.EditprofileprogressBar.visibility = View.GONE
        binding.inputemail.setText(email)

        if (user != null) {
            binding.inputemail.setText(email)
            binding.inputemail.isEnabled = false
            binding.inputname.setText(name)
        }
        profilename=binding.inputname.text.toString().trim()
        if (!TextUtils.isEmpty(profilename)) {
            binding.layoutname.isHelperTextEnabled = false
        }
        binding.inputname.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.layoutname.isHelperTextEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.inputphone.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.layoutphone.isHelperTextEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        binding.saveGenrate.setOnClickListener(View.OnClickListener {
                binding.EditprofileprogressBar.visibility = View.VISIBLE
                genrate_save()
        })
    }

    private fun genrate_save() {
        profilename = binding.inputname.text.toString().trim()
        profilemail = binding.inputemail.text.toString().trim()
        profilephone = binding.inputphone.text.toString().trim()
        profilegithub = binding.inputgithub.text.toString().trim()
        profileaddress = binding.inputaddress.text.toString().trim()

        if (TextUtils.isEmpty(profilephone) && TextUtils.isEmpty(profilename)) {
            binding.EditprofileprogressBar.visibility = View.GONE
            Toast.makeText(this, "Fill The Details", Toast.LENGTH_LONG).show()
        }

        try {
            myText = "MYDC|$profilename|$profilemail|$profilephone|$profilegithub|$profileaddress"
            val qrCode = generateQRCode(myText)
            val byteArray = bitmapToByteArray(qrCode)

            var storage = FirebaseStorage.getInstance()
            var storageRef = storage.reference

            var mountainsRef = storageRef.child("genrated_QR/$profilemail.jpg")
            var uploadTask = mountainsRef.putBytes(byteArray)

            uploadTask.addOnFailureListener {
                    Log.w("Edit profile","Fail to upload")
            }.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener(OnSuccessListener {
                    var uri = it
                    var user_details = user_details(
                        profilename,
                        profilephone,
                        profilegithub,
                        profileaddress,
                        uri.toString()
                    )
                    var refrence = FirebaseDatabase.getInstance().getReference("users")
                    refrence.child(Userid).setValue(user_details).addOnCompleteListener(
                        OnCompleteListener(fun(it: Task<Void>) {
                            if (it.isSuccessful()) {
                                binding.EditprofileprogressBar.visibility = View.GONE
                                var i = Intent(this, MainActivity::class.java)
                                startActivity(i)
                                Toast.makeText(this, "Data save Succrssfully", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                binding.EditprofileprogressBar.visibility = View.GONE
                                Toast.makeText(this, " Please try again later", Toast.LENGTH_LONG)
                                    .show()
                            }

                        })
                    )

                })

            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }


    private fun generateQRCode(content: String): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 800, 800, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}