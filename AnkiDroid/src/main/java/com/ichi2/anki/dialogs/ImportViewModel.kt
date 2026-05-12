/*
 *  Copyright (c) 2026 Georgios Michelakis <michelakisgio@gmail.com
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ImportViewModel : ViewModel() {
    private val _importAddFlow = MutableSharedFlow<String>()
    val importAddFlow = _importAddFlow.asSharedFlow()

    private val _importReplaceFlow = MutableSharedFlow<String>()
    val importReplaceFlow = _importReplaceFlow.asSharedFlow()

    fun triggerImportAdd(path: String) {
        viewModelScope.launch { _importAddFlow.emit(path) }
    }

    fun triggerImportReplace(path: String) {
        viewModelScope.launch { _importReplaceFlow.emit(path) }
    }
}
