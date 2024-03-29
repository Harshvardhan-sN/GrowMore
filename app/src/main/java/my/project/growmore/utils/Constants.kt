package my.project.growmore.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import my.project.growmore.R
import my.project.growmore.activities.ProfileActivity
import my.project.growmore.models.User
import java.io.IOException

object Constants {
    const val USERS: String = "users"
    const val Boards: String = "boards"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val PHONENUMBER: String = "phoneNumber"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL: String = "boardDetail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val TASK_LIST_ITEM_POSITION: String = "taskListItemPosition"
    const val CARD_LIST_ITEM_POSITION: String = "cardListItemPosition"
    const val BOARD_MEMBERS_LIST: String = "boardMembersList"
    const val SELECT: String = "select"
    const val UNSELECT: String = "unSelect"
    fun getFileExtension(activity: Activity ,uri: Uri?): String?{
        return MimeTypeMap.getSingleton().
        getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}