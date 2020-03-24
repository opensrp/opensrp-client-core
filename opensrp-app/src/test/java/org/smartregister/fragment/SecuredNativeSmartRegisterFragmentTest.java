package org.smartregister.fragment;

import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 3/3/20.
 */
public class SecuredNativeSmartRegisterFragmentTest {

   private SecuredNativeSmartRegisterFragment fragment;

   public void setUp(){
       fragment.setupSearchView(null);
       doNothing().when(fragment).setupSearchView(null);
   }
}
