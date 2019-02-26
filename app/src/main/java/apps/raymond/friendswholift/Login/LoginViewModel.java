package apps.raymond.friendswholift.Login;

import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import apps.raymond.friendswholift.TestFirebaseRepository;

class LoginViewModel extends ViewModel {

    private TestFirebaseRepository mRepository;

    LoginViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    Task<Void> createUserDoc(String userName){
        return mRepository.createUserDoc(userName);
    }

    Task<Void> createUser(Context context, String name, String password){
        return mRepository.createUser(context,name,password);
    }

    Task<AuthResult> signIn(String name, String password){
        return mRepository.signInWithEmail(name,password);
    }
}
