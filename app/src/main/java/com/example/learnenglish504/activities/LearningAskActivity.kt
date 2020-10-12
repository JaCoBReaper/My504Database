package com.example.learnenglish504.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.learnenglish504.R
import com.example.learnenglish504.fragment.*
import kotlin.collections.ArrayList


class LearningAskActivity : AppCompatActivity(), IOnTimeExpiredOrWrongAnswer, IOnGotoNextClick,
    IOnAskNewWord {

// region Variables

    private lateinit var viewedList: ArrayList<String>
    var lessonNumberInActivity = 2
// endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_test)


        viewedList = intent.getStringArrayListExtra(Constants.SEND_LIST_TO_TEST)!!
        lessonNumberInActivity = intent.getIntExtra(Constants.INTENT_LESSON_NUMBER, 2)


        // setting fragments
        setFragmentTest(viewedList)
    }

    private fun setFragmentTest(list: ArrayList<String>) {

        val bundle = Bundle()
        bundle.putStringArrayList(Constants.SEND_LIST_TO_TEST_FRAGMENT, list)
        val fragment = FragmentTest(this, this)
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun setFragmentRepeat(inputWord: String) {

        val bundle = Bundle()
        bundle.putString(Constants.SEND_WORD_TO_REPEAT_FRAGMENT, inputWord)
        val rFragment = FragmentRepeat(this)
        rFragment.arguments = bundle
        replaceFragment(rFragment)
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentPlaceHolder, fragment)
        fragmentTransaction.commit()
    }

    override fun gotoRepeatFragment(inputWord: String) = setFragmentRepeat(inputWord)

    override fun gotoFragmentTest() = setFragmentTest(viewedList)

    override fun onBackPressed() {

        val exitDialog = AlertDialog.Builder(this)
            .setMessage("آیا میخواهید از یادگیری کلمات خارج شوید؟")

            .setPositiveButton("بله") { dialog, _ ->

//                countDownTimer.cancel()
                dialog.dismiss()
                finishAffinity()
                startActivity(Intent(this, HomeActivity::class.java))
            }
            .setNegativeButton("خیر") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun updateFragment(newList: ArrayList<String>) = setFragmentTest(newList)
}
