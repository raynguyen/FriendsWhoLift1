package apps.raymond.kinect.login;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

class LoginViewModel extends ViewModel {

    private Core_FireBaseRepo mRepository;

    LoginViewModel(){
        this.mRepository = new Core_FireBaseRepo();
    }

    Task<Void> createUserByEmail(User_Model userModel, String password){
        return mRepository.createUserByEmail(userModel,password);
    }

    Task<AuthResult> signIn(String name, String password){
        return mRepository.emailSignIn(name,password);
    }
}
