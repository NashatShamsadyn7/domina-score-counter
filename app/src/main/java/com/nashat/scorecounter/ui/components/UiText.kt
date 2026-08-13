package com.nashat.scorecounter.ui.components

import com.nashat.scorecounter.model.AppLanguage
import com.nashat.scorecounter.model.GameMode

data class UiText(
    val choosePoints: String,
    val customPoints: String,
    val cancel: String,
    val confirm: String,
    val enterPoints: String,
    val scoreHint: String,
    val pointsLabel: String,
    val modeTitle: String,
    val settingsTitle: String,
    val theme: String,
    val sound: String,
    val vibration: String,
    val animation: String,
    val celebration: String,
    val fontSize: String,
    val language: String,
    val developer: String,
    val difference: String,
    val resetAll: String,
    val backToMode: String,
    val resetConfirmTitle: String,
    val resetConfirmBody: String,
    val light: String,
    val dark: String,
    val system: String,
    val kurdish: String,
    val arabic: String,
    val english: String
)

fun uiText(language: AppLanguage): UiText = when (language) {
    AppLanguage.KURDISH -> UiText(
        choosePoints = "هەڵبژاردنی خاڵ",
        customPoints = "خاڵی دڵخواز",
        cancel = "پاشگەزبوونەوە",
        confirm = "پاشەکەوتکردن",
        enterPoints = "خاڵ بنووسە:",
        scoreHint = "بۆ زیادکردن یان کەمکردنی خاڵ، کلیک بکە",
        pointsLabel = "خاڵ",
        modeTitle = "شێوازی یاری هەڵبژێرە",
        settingsTitle = "ڕێکخستنەکان",
        theme = "ڕووکار",
        sound = "دەنگ",
        vibration = "لەرزین",
        animation = "ئەنیمەیشن",
        celebration = "کاریگەرییەکانی ئاهەنگ",
        fontSize = "قەبارەی فۆنت",
        language = "زمان",
        developer = "Nashat Shamsadyn",
        difference = "جیاوازی خاڵەکان",
        resetAll = "سفرکردنەوەی هەموو",
        backToMode = "گەرانەوە بۆ شێوازی سەرەکی",
        resetConfirmTitle = "دڵنیایت؟",
        resetConfirmBody = "هەموو خاڵەکان دەگەڕێنرێنەوە بۆ 0 و مێژووی undo/redo دەسڕدرێتەوە.",
        light = "ڕووناک",
        dark = "تاریک",
        system = "سیستەم",
        kurdish = "کوردی",
        arabic = "عەرەبی",
        english = "English"
    )

    AppLanguage.ARABIC -> UiText(
        choosePoints = "اختيار النقاط",
        customPoints = "نقاط مخصصة",
        cancel = "إلغاء",
        confirm = "تأكيد",
        enterPoints = "أدخل النقاط:",
        scoreHint = "اضغط لإضافة النقاط أو خصمها",
        pointsLabel = "نقطة",
        modeTitle = "اختر وضع اللعب",
        settingsTitle = "الإعدادات",
        theme = "المظهر",
        sound = "الصوت",
        vibration = "الاهتزاز",
        animation = "الرسوم المتحركة",
        celebration = "مؤثرات الاحتفال",
        fontSize = "حجم الخط",
        language = "اللغة",
        developer = "Nashat Shamsadyn",
        difference = "فارق النقاط",
        resetAll = "إعادة تعيين الكل",
        backToMode = "العودة إلى الوضع الرئيسي",
        resetConfirmTitle = "هل أنت متأكد؟",
        resetConfirmBody = "سيتم إعادة جميع النقاط إلى 0 ومسح سجل التراجع والإعادة.",
        light = "فاتح",
        dark = "داكن",
        system = "النظام",
        kurdish = "الكردية",
        arabic = "العربية",
        english = "English"
    )

    AppLanguage.ENGLISH -> UiText(
        choosePoints = "Choose Points",
        customPoints = "Custom Score",
        cancel = "Cancel",
        confirm = "Confirm",
        enterPoints = "Enter score:",
        scoreHint = "Tap to add or subtract points",
        pointsLabel = "POINTS",
        modeTitle = "Choose A Game Mode",
        settingsTitle = "Settings",
        theme = "Theme",
        sound = "Sound",
        vibration = "Vibration",
        animation = "Animation",
        celebration = "Celebration Effects",
        fontSize = "Font Size",
        language = "Language",
        developer = "Nashat Shamsadyn",
        difference = "Score Difference",
        resetAll = "Reset All",
        backToMode = "Back To Main Mode",
        resetConfirmTitle = "Are you sure?",
        resetConfirmBody = "All scores will be reset to 0 and the undo/redo history will be cleared.",
        light = "Light",
        dark = "Dark",
        system = "System",
        kurdish = "Kurdish",
        arabic = "Arabic",
        english = "English"
    )
}

fun gameModeLabel(mode: GameMode, language: AppLanguage): String = when (language) {
    AppLanguage.KURDISH -> when (mode) {
        GameMode.TWO_TEAMS -> "دوو تیم"
        GameMode.THREE_PLAYERS -> "سێ یاریزان"
        GameMode.FOUR_PLAYERS -> "چوار یاریزان"
    }

    AppLanguage.ARABIC -> when (mode) {
        GameMode.TWO_TEAMS -> "فريقان"
        GameMode.THREE_PLAYERS -> "3 لاعبين"
        GameMode.FOUR_PLAYERS -> "4 لاعبين"
    }

    AppLanguage.ENGLISH -> when (mode) {
        GameMode.TWO_TEAMS -> "Two Teams"
        GameMode.THREE_PLAYERS -> "Three Players"
        GameMode.FOUR_PLAYERS -> "Four Players"
    }
}
