package tom.dev.whatgoingtoeat.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tom.dev.whatgoingtoeat.dto.user.User
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor() : ViewModel() {

    var userInstance: User? = null
}