package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class PostingActivity : AppCompatActivity() {

    private lateinit var imageIv: ImageView
    private lateinit var titleEt: EditText
    private lateinit var storyEt: EditText
    private lateinit var tag1Et: EditText
    private lateinit var tag2Et: EditText
    private lateinit var tag3Et: EditText
    private lateinit var uploadBtn: Button

    private var fbAuth: FirebaseAuth? = null
    private var selectedImageUri: Uri? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)

        // Firebase 초기화
        fbAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 뷰 초기화
        imageIv = findViewById(R.id.profileImg)
        titleEt = findViewById(R.id.title)
        storyEt = findViewById(R.id.story)
        tag1Et = findViewById(R.id.hashtag1)
        tag2Et = findViewById(R.id.hashtag2)
        tag3Et = findViewById(R.id.hashtag3)
        uploadBtn = findViewById(R.id.add_movie_button)

        // 프로필 이미지 선택
        imageIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK)
        }

        // 영화추가 버튼 클릭
        uploadBtn.setOnClickListener {
            if (selectedImageUri != null) {
                // 업로드할 이미지 파일 이름 생성
                val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())

                // Firebase Storage에 이미지 업로드
                storage.reference.child("image").child(fileName)
                    .putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                            // 업로드한 이미지의 다운로드 URL 얻기
                            val imageUrl = it.toString()

                            // ContentDTO 객체 생성
                            val currentUser = fbAuth?.currentUser
                            val contentDTO = ContentDTO(
                                uid = fbAuth?.uid.toString(),
                                userId = currentUser?.email,
                                title = titleEt.text.toString(),
                                story = storyEt.text.toString(),
                                tag1 = tag1Et.text.toString(),
                                tag2 = tag2Et.text.toString(),
                                tag3 = tag3Et.text.toString(),
                                imageUrl = imageUrl
                            )


                            // Firestore에 ContentDTO 객체 업로드
                            firestore.collection("post")
                                .document()
                                .set(contentDTO)
                                .addOnSuccessListener {
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "플레이리스트 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, MakePlaylistMovieSearchActivity ::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageIv.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val IMAGE_PICK = 1111
    }
}
