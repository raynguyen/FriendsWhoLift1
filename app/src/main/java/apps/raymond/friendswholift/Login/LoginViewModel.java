package apps.raymond.friendswholift.Login;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import apps.raymond.friendswholift.FireBaseRepository;
import apps.raymond.friendswholift.UserProfile.UserModel;

class LoginViewModel extends ViewModel {

    private FireBaseRepository mRepository;

    LoginViewModel(){
        this.mRepository = new FireBaseRepository();
    }

    Task<Void> createUserByEmail(UserModel userModel, String password){
        return mRepository.createUserByEmail(userModel,password);
    }

    Task<AuthResult> signIn(String name, String password){
        return mRepository.signInWithEmail(name,password);
    }
}
