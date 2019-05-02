package apps.raymond.kinect.login;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import apps.raymond.kinect.FireBaseRepo.FireBase_Repository;
import apps.raymond.kinect.UserProfile.User_Model;

class LoginViewModel extends ViewModel {

    private FireBase_Repository mRepository;

    LoginViewModel(){
        this.mRepository = new FireBase_Repository();
    }

    Task<Void> createUserByEmail(User_Model userModel, String password){
        return mRepository.createUserByEmail(userModel,password);
    }

    Task<AuthResult> signIn(String name, String password){
        return mRepository.emailSignIn(name,password);
    }
}
