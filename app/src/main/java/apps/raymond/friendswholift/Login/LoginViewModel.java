package apps.raymond.friendswholift.Login;

import android.arch.lifecycle.ViewModel;

import apps.raymond.friendswholift.FireStoreClasses.TestFirebaseRepository;

public class LoginViewModel extends ViewModel {

    private TestFirebaseRepository mFirebaseRepository;

    public LoginViewModel(){
        this.mFirebaseRepository = new TestFirebaseRepository();
    }

    public void createUserDoc(String userName){
        mFirebaseRepository.createUserDoc(userName);
    }
}
