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
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class ImportViewModel : ViewModel() {
    private val pendingImportRequestState = MutableStateFlow<ImportRequest?>(null)

    val pendingImportRequest: StateFlow<ImportRequest?> = pendingImportRequestState

    fun registerImportRequest(request: ImportRequest) {
        Timber.d("Import dialog requested: %s", request.dialogType)
        pendingImportRequestState.value = request
    }

    fun clearImportRequest() {
        Timber.d("Clearing pending import dialog request")
        pendingImportRequestState.value = null
    }

    @Parcelize
    data class ImportRequest(
        val dialogType: ImportDialog.Type,
        val importPath: String,
    ) : Parcelable
}
