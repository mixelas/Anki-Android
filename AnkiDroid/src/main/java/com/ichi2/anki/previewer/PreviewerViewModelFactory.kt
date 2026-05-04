package com.ichi2.anki.previewer

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File

internal fun previewerViewModelFactory(cacheDir: File): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            PreviewerViewModel(cacheDir, createSavedStateHandle())
        }
    }
