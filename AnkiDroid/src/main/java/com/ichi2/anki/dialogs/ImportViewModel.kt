/*
 *  Copyright (c) 2026 Georgios Michelakis <michelakisgio@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ichi2.anki.dialogs

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class ImportViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val pendingImportRequest: StateFlow<ImportRequest?> =
        savedStateHandle.getStateFlow(PENDING_IMPORT_REQUEST_KEY, null)

    fun setPendingImportRequest(request: ImportRequest) {
        Timber.d("Setting pending import request: %s", request.dialogType)
        savedStateHandle[PENDING_IMPORT_REQUEST_KEY] = request
    }

    fun clearPendingImportRequest() {
        Timber.d("Clearing pending import request")
        savedStateHandle[PENDING_IMPORT_REQUEST_KEY] = null
    }

    companion object {
        private const val PENDING_IMPORT_REQUEST_KEY = "pending_import_request"
    }

    @Parcelize
    data class ImportRequest(
        val dialogType: ImportDialog.Type,
        val importPath: String,
    ) : Parcelable
}
