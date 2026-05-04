package com.ichi2.anki.ui.windows.reviewer

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File

internal fun reviewerViewModelFactory(cacheDir: File): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            ReviewerViewModel(cacheDir, createSavedStateHandle())
        }
    }
