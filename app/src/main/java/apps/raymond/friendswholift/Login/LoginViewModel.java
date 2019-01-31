package apps.raymond.friendswholift.Login;

import android.arch.lifecycle.ViewModel;

import apps.raymond.friendswholift.FireStoreClasses.FirebaseRepository;

public class LoginViewModel extends ViewModel {

    private FirebaseRepository mFirebaseRepository;

    public LoginViewModel(){
        this.mFirebaseRepository = new FirebaseRepository();
    }

    public void createUserDoc(){
        mFirebaseRepository.createUserDoc();
    }
}
