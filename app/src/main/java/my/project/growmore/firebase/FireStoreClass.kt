package my.project.growmore.firebase

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import my.project.growmore.R
import my.project.growmore.activities.*
import my.project.growmore.models.Board
import my.project.growmore.models.Card
import my.project.growmore.models.User
import my.project.growmore.utils.Constants

class FireStoreClass: BaseActivity() {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUUID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                _ ->
                Log.e("FireStoreClassSignUp","Error")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentID: String) {
        mFireStore.collection(Constants.Boards)
            .document(documentID)
            .get()
            .addOnSuccessListener { document ->
                val board = document.toObject(Board::class.java)
                board?.documentId = document.id
                activity.boardDetails(board!!)
            }.addOnFailureListener{ e->
                activity.hideProgressDialog()
            }
    }

    fun getBoardList(activity: MainActivity){
        mFireStore.collection(Constants.Boards)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUUID())
            .get()
            .addOnSuccessListener { document ->
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardListToUI(boardList)
            }.addOnFailureListener{ e->
                activity.hideProgressDialog()
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.Boards)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                showToast("Created", activity)
                activity.boardCreatedSuccess()
            }.addOnFailureListener {
                exception ->
                    //activity.hideProgressDialog()
                    activity.finish()
                    activity.showToast("Error Occurred Try Again", this)
                    Log.e(activity.javaClass.simpleName,
                        "Error while creating a board.", exception)
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.Boards)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener{
                if(activity is TaskListActivity)
                    activity.addUpdateTaskListSuccess()
                else if (activity is CardDetailsActivity)
                    activity.addUpdateTaskListSuccess()
            }.addOnFailureListener {
                if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
            }
    }
    fun getCurrentUUID(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun updateProfileData(activity: ProfileActivity,
                          userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUUID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "nobro")
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e ->
                //activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"dataanayltic", e)
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUUID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser!=null){
                    when(activity){
                        is SignInActivity ->{
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity ->{
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                        is ProfileActivity ->{
                            activity.getUserDataForProfile(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener {
                    _ ->
                Log.e("FireStoreClassSignUp","Error")
            }
    }

    fun getAssignedMemberListDetails(activity: Activity, assignedTo: ArrayList<String>) {
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener { listOfAllUsers ->
                val userList: ArrayList<User> = ArrayList()
                for(i in listOfAllUsers.documents) {
                    val user = i.toObject(User::class.java)!!
                    userList.add(user)
                }
                if(activity is MembersActivity) {
                    activity.setUpMemberList(userList)
                }
                else if(activity is TaskListActivity) {
                    activity.boardMemberDetailedList(userList)
                }
            }.addOnFailureListener {
                if(activity is MembersActivity) {
                    activity.hideProgressDialog()
                    activity.showError("Something Went Wrong")
                }
                else if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                    activity.showError("Something Went Wrong")
                }
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { listOfEmails ->
                if(listOfEmails.size() > 0) {
                    val user = listOfEmails.documents[0].toObject(User::class.java)!!
                    activity.membersDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showError("No such member found")
                }
            }.addOnFailureListener {
                activity.hideProgressDialog()
                activity.showError("Error while getting user details")
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board,user: User) {
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.Boards)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                activity.showError("Error while creating a board")
            }
    }
}