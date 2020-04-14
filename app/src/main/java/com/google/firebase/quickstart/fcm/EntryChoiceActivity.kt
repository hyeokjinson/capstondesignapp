package com.google.firebase.quickstart.fcm

import android.content.Intent
import com.firebase.example.internal.BaseEntryChoiceActivity
import com.firebase.example.internal.Choice
import com.google.firebase.quickstart.fcm.java.MainActivity

class EntryChoiceActivity : BaseEntryChoiceActivity() {

    override fun getChoices(): List<Choice> {
        return kotlin.collections.listOf(
                Choice(
                        "차량 주변확인",
                        "차량주변확인하여 차량제어를 하십시오",
                        Intent(this, MainActivity::class.java))

        )
    }
}