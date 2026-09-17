package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.Dhikr
import com.example.data.model.DhikrCategory
import com.example.data.model.ReadingProgressEntity
import com.example.data.model.TasbeehRecordEntity
import com.example.data.model.UserSettingsEntity
import com.example.data.repository.HisnContentProvider
import com.example.data.repository.HisnRepository
import com.example.service.AudioReciterService
import com.example.service.HapticSoundHelper
import com.example.service.ReminderHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HisnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HisnRepository
    val audioReciterService = AudioReciterService(application)
    val hapticSoundHelper = HapticSoundHelper(application)
    val reminderHelper = ReminderHelper(application)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HisnRepository(
            favoriteDao = db.favoriteDao(),
            readingProgressDao = db.readingProgressDao(),
            tasbeehDao = db.tasbeehDao(),
            userSettingsDao = db.userSettingsDao()
        )
    }

    val categories: List<DhikrCategory> = repository.categories
    val allDhikrs: List<Dhikr> = repository.allDhikrs

    // User Settings Flow
    val userSettings: StateFlow<UserSettingsEntity> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettingsEntity())

    // Favorites Flow
    val favoriteIds: StateFlow<List<Int>> = repository.favoriteIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteDhikrs: StateFlow<List<Dhikr>> = repository.favoriteDhikrs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reading Progress
    val allProgress: StateFlow<List<ReadingProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastRead: StateFlow<ReadingProgressEntity?> = repository.lastRead
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Reader State
    private val _currentReaderDhikrList = MutableStateFlow<List<Dhikr>>(emptyList())
    val currentReaderDhikrList: StateFlow<List<Dhikr>> = _currentReaderDhikrList.asStateFlow()

    private val _currentReaderIndex = MutableStateFlow(0)
    val currentReaderIndex: StateFlow<Int> = _currentReaderIndex.asStateFlow()

    private val _currentRemainingCount = MutableStateFlow(1)
    val currentRemainingCount: StateFlow<Int> = _currentRemainingCount.asStateFlow()

    private val _currentCompletedCount = MutableStateFlow(0)
    val currentCompletedCount: StateFlow<Int> = _currentCompletedCount.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSearchCategory = MutableStateFlow<String?>(null)
    val selectedSearchCategory: StateFlow<String?> = _selectedSearchCategory.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Dhikr>>(allDhikrs)
    val searchResults: StateFlow<List<Dhikr>> = _searchResults.asStateFlow()

    // Tasbeeh
    val tasbeehRecords: StateFlow<List<TasbeehRecordEntity>> = repository.allTasbeehRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTasbeehPhrase = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val currentTasbeehPhrase: StateFlow<String> = _currentTasbeehPhrase.asStateFlow()

    private val _currentTasbeehCount = MutableStateFlow(0)
    val currentTasbeehCount: StateFlow<Int> = _currentTasbeehCount.asStateFlow()

    private val _currentTasbeehTarget = MutableStateFlow(33)
    val currentTasbeehTarget: StateFlow<Int> = _currentTasbeehTarget.asStateFlow()

    private val _currentTasbeehTotal = MutableStateFlow(0)
    val currentTasbeehTotal: StateFlow<Int> = _currentTasbeehTotal.asStateFlow()

    // Splash navigation state
    private val _isSplashDismissed = MutableStateFlow(false)
    val isSplashDismissed: StateFlow<Boolean> = _isSplashDismissed.asStateFlow()

    fun dismissSplash() {
        _isSplashDismissed.value = true
    }

    fun openDhikrReader(categoryOrList: List<Dhikr>, startIndex: Int = 0) {
        if (categoryOrList.isEmpty()) return
        _currentReaderDhikrList.value = categoryOrList
        val validIndex = startIndex.coerceIn(0, categoryOrList.size - 1)
        _currentReaderIndex.value = validIndex
        val dhikr = categoryOrList[validIndex]
        _currentRemainingCount.value = dhikr.count
        _currentCompletedCount.value = 0

        // record progress in DB
        viewModelScope.launch {
            repository.saveProgress(dhikr.id, dhikr.categoryId, 0, dhikr.count)
        }
    }

    fun openDhikrById(dhikrId: Int) {
        val dhikr = allDhikrs.find { it.id == dhikrId } ?: return
        val list = repository.getDhikrsForCategory(dhikr.categoryId)
        val index = list.indexOfFirst { it.id == dhikrId }.coerceAtLeast(0)
        openDhikrReader(list, index)
    }

    fun onCounterTap() {
        val list = _currentReaderDhikrList.value
        val index = _currentReaderIndex.value
        if (list.isEmpty() || index !in list.indices) return
        val dhikr = list[index]
        val settings = userSettings.value

        if (_currentRemainingCount.value > 1) {
            _currentRemainingCount.value -= 1
            _currentCompletedCount.value += 1
            hapticSoundHelper.vibrateTick(settings.isHapticEnabled)
            hapticSoundHelper.playClickSound(settings.isSoundEnabled)

            viewModelScope.launch {
                repository.saveProgress(
                    dhikr.id,
                    dhikr.categoryId,
                    _currentCompletedCount.value,
                    dhikr.count
                )
            }
        } else if (_currentRemainingCount.value == 1) {
            _currentRemainingCount.value = 0
            _currentCompletedCount.value = dhikr.count
            hapticSoundHelper.vibrateComplete(settings.isHapticEnabled)
            hapticSoundHelper.playClickSound(settings.isSoundEnabled)

            viewModelScope.launch {
                repository.saveProgress(
                    dhikr.id,
                    dhikr.categoryId,
                    dhikr.count,
                    dhikr.count
                )
            }

            if (settings.autoAdvance && index < list.size - 1) {
                nextDhikr()
            }
        }
    }

    fun nextDhikr() {
        val list = _currentReaderDhikrList.value
        if (list.isEmpty()) return
        val nextIdx = (_currentReaderIndex.value + 1).coerceAtMost(list.size - 1)
        if (nextIdx != _currentReaderIndex.value) {
            _currentReaderIndex.value = nextIdx
            val nextDhikr = list[nextIdx]
            _currentRemainingCount.value = nextDhikr.count
            _currentCompletedCount.value = 0
            audioReciterService.stop()
        }
    }

    fun prevDhikr() {
        val list = _currentReaderDhikrList.value
        if (list.isEmpty()) return
        val prevIdx = (_currentReaderIndex.value - 1).coerceAtLeast(0)
        if (prevIdx != _currentReaderIndex.value) {
            _currentReaderIndex.value = prevIdx
            val prevDhikr = list[prevIdx]
            _currentRemainingCount.value = prevDhikr.count
            _currentCompletedCount.value = 0
            audioReciterService.stop()
        }
    }

    fun resetCurrentCounter() {
        val list = _currentReaderDhikrList.value
        val index = _currentReaderIndex.value
        if (list.isEmpty() || index !in list.indices) return
        val dhikr = list[index]
        _currentRemainingCount.value = dhikr.count
        _currentCompletedCount.value = 0
    }

    fun toggleFavorite(dhikrId: Int) {
        val isFav = favoriteIds.value.contains(dhikrId)
        viewModelScope.launch {
            repository.toggleFavorite(dhikrId, isFav)
        }
    }

    // Tasbeeh logic
    fun selectTasbeehPhrase(phrase: String, target: Int = 33) {
        _currentTasbeehPhrase.value = phrase
        _currentTasbeehTarget.value = target
        _currentTasbeehCount.value = 0
        viewModelScope.launch {
            val record = repository.getOrCreateTasbeeh(phrase, target)
            _currentTasbeehCount.value = record.currentCount
            _currentTasbeehTotal.value = record.totalCount
            _currentTasbeehTarget.value = record.targetCount
        }
    }

    fun onTasbeehClick() {
        val settings = userSettings.value
        val newCurrent = _currentTasbeehCount.value + 1
        val newTotal = _currentTasbeehTotal.value + 1
        _currentTasbeehCount.value = newCurrent
        _currentTasbeehTotal.value = newTotal

        if (newCurrent >= _currentTasbeehTarget.value) {
            hapticSoundHelper.vibrateComplete(settings.isHapticEnabled)
            hapticSoundHelper.playClickSound(settings.isSoundEnabled)
        } else {
            hapticSoundHelper.vibrateTick(settings.isHapticEnabled)
            hapticSoundHelper.playClickSound(settings.isSoundEnabled)
        }

        viewModelScope.launch {
            val record = repository.getOrCreateTasbeeh(_currentTasbeehPhrase.value, _currentTasbeehTarget.value)
            repository.updateTasbeehCount(record, newCurrent, newTotal)
        }
    }

    fun resetTasbeehCounter() {
        _currentTasbeehCount.value = 0
        viewModelScope.launch {
            val record = repository.getOrCreateTasbeeh(_currentTasbeehPhrase.value, _currentTasbeehTarget.value)
            repository.updateTasbeehCount(record, 0, _currentTasbeehTotal.value)
        }
    }

    fun setTasbeehTarget(target: Int) {
        _currentTasbeehTarget.value = target
        viewModelScope.launch {
            val record = repository.getOrCreateTasbeeh(_currentTasbeehPhrase.value, target)
            repository.updateTasbeehTarget(record, target)
        }
    }

    // Search logic
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterSearchResults()
    }

    fun selectSearchCategory(categoryId: String?) {
        _selectedSearchCategory.value = categoryId
        filterSearchResults()
    }

    private fun filterSearchResults() {
        val query = _searchQuery.value.trim().lowercase()
        val cat = _selectedSearchCategory.value

        var results = if (cat == null) allDhikrs else allDhikrs.filter { it.categoryId == cat }
        if (query.isNotEmpty()) {
            results = results.filter {
                it.title.lowercase().contains(query) ||
                it.text.lowercase().contains(query) ||
                it.source.lowercase().contains(query) ||
                it.benefit.lowercase().contains(query)
            }
        }
        _searchResults.value = results
    }

    // Settings actions
    fun setDarkMode(isDark: Boolean?) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveSettings(current.copy(isDarkMode = isDark))
        }
    }

    fun setFontSize(sizeSp: Float) {
        val clamped = sizeSp.coerceIn(16f, 34f)
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveSettings(current.copy(fontSizeSp = clamped))
        }
    }

    fun toggleHaptic(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveSettings(current.copy(isHapticEnabled = enabled))
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveSettings(current.copy(isSoundEnabled = enabled))
        }
    }

    fun toggleAutoAdvance(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.saveSettings(current.copy(autoAdvance = enabled))
        }
    }

    fun toggleReminder(type: String, enabled: Boolean, time: String? = null) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = when (type) {
                "morning" -> current.copy(
                    morningReminderEnabled = enabled,
                    morningReminderTime = time ?: current.morningReminderTime
                )
                "evening" -> current.copy(
                    eveningReminderEnabled = enabled,
                    eveningReminderTime = time ?: current.eveningReminderTime
                )
                "sleep" -> current.copy(
                    sleepReminderEnabled = enabled,
                    sleepReminderTime = time ?: current.sleepReminderTime
                )
                else -> current
            }
            repository.saveSettings(updated)
            if (enabled) {
                reminderHelper.showReminderNotification(
                    "تفعيل التذكير",
                    "تم تفعيل تذكير الأذكار بنجاح في تطبيق حصن المسلم"
                )
            }
        }
    }

    fun resetAllReadingProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }

    // Helper functions for sharing & copying
    fun copyDhikr(dhikr: Dhikr) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val formatted = "${dhikr.title}\n\n${dhikr.text}\n\n${dhikr.source}\n\n(من تطبيق حصن المسلم)"
        val clip = ClipData.newPlainText("Dhikr", formatted)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    fun shareDhikr(dhikr: Dhikr) {
        val context = getApplication<Application>()
        val shareText = "﴿ ${dhikr.title} ﴾\n\n${dhikr.text}\n\nالمصدر: ${dhikr.source}\n\n(تطبيق حصن المسلم – أذكار وأدعية)"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, dhikr.title)
            putExtra(Intent.EXTRA_TEXT, shareText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(intent, "مشاركة الذكر عبر").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooser)
    }

    override fun onCleared() {
        super.onCleared()
        audioReciterService.shutdown()
        hapticSoundHelper.release()
    }
}
