/*
 * For each Group tag stored under the user's Document, we want to pull the GroupBase object from
 * FireStore and display the information in a CardView.
 */

package apps.raymond.friendswholift.FireStoreClasses;

import android.arch.lifecycle.ViewModel;

import java.util.Set;

public class FirebaseViewModel extends ViewModel {

    private static FirebaseRepository fbRepository;
    private static Set<String> groupKeys;

    public FirebaseViewModel(){
        fbRepository = new FirebaseRepository();
        //mAllGroups = fbRepository.;
    }


}