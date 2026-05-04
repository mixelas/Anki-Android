package com.ichi2.anki.previewer

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File

internal fun templatePreviewerViewModelFactory(cacheDir: File): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            TemplatePreviewerViewModel(cacheDir, createSavedStateHandle())
        }
    }
