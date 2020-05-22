package org.smartregister.view.dialog;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.ClientForm;
import org.smartregister.listener.RollbackDialogCallback;
import org.smartregister.repository.ClientFormRepository;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-05-2020.
 */
public class FormRollbackDialogTest extends BaseUnitTest {

    @Test
    public void selectFormShouldReturnFalseWhenCurrentCorruptedFormIsChosen() {
        ClientForm clientForm = new ClientForm();

        assertFalse(FormRollbackDialog.selectForm(0, "0.0.3" + RuntimeEnvironment.application.getString(R.string.current_corrupted_form)
                , RuntimeEnvironment.application, new ArrayList<ClientForm>(), clientForm, Mockito.mock(RollbackDialogCallback.class)));
    }

    @Test
    public void selectFormShouldReturnFalseWhenItemIndexDoesNotExist() {
        ClientForm clientForm = new ClientForm();

        assertFalse(FormRollbackDialog.selectForm(2, "0.0.3"
                , RuntimeEnvironment.application, new ArrayList<ClientForm>(), clientForm, Mockito.mock(RollbackDialogCallback.class)));
    }

    @Test
    public void selectFormShouldReturnTrueWhenAConfigurableFormIsSelected() {
        ClientForm highClientFormVersion = new ClientForm();
        highClientFormVersion.setVersion("0.0.3");

        ClientForm clientForm = new ClientForm();
        clientForm.setVersion("0.0.2");
        ArrayList<ClientForm> clientFormsList = new ArrayList<ClientForm>();
        clientFormsList.add(clientForm);

        ClientFormRepository clientFormRepository = Mockito.mock(ClientFormRepository.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "clientFormRepository", clientFormRepository);

        RollbackDialogCallback rollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        assertTrue(FormRollbackDialog.selectForm(0, "0.0.2"
                , RuntimeEnvironment.application, clientFormsList, highClientFormVersion, rollbackDialogCallback));
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientForm);


        ArgumentCaptor<ClientForm> updateClientFormArgumentCaptor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(clientFormRepository, Mockito.times(2)).addOrUpdate(updateClientFormArgumentCaptor.capture());
        ClientForm updatedClientForm1 = updateClientFormArgumentCaptor.getAllValues().get(0);
        assertEquals("0.0.2", updatedClientForm1.getVersion());
        assertTrue(updatedClientForm1.isActive());

        ClientForm updatedClientForm2 = updateClientFormArgumentCaptor.getAllValues().get(1);
        assertEquals("0.0.3", updatedClientForm2.getVersion());
        assertFalse(updatedClientForm2.isActive());


        ArgumentCaptor<ClientForm> clientFormArgumentCaptor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientFormArgumentCaptor.capture());
        ClientForm selectedClientForm = clientFormArgumentCaptor.getValue();
        assertEquals("0.0.2", selectedClientForm.getVersion());
    }


    @Test
    public void selectFormShouldReturnTrueWhenBaseFormIsSelected() {
        ClientForm highClientFormVersion = new ClientForm();
        highClientFormVersion.setVersion("0.0.3");

        ClientForm clientForm = new ClientForm();
        clientForm.setVersion("0.0.2");
        ArrayList<ClientForm> clientFormsList = new ArrayList<ClientForm>();
        clientFormsList.add(highClientFormVersion);
        clientFormsList.add(clientForm);

        ClientFormRepository clientFormRepository = Mockito.mock(ClientFormRepository.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "clientFormRepository", clientFormRepository);

        RollbackDialogCallback rollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        assertTrue(FormRollbackDialog.selectForm(0, "base version"
                , RuntimeEnvironment.application, clientFormsList, highClientFormVersion, rollbackDialogCallback));

        ArgumentCaptor<ClientForm> clientFormArgumentCaptor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientFormArgumentCaptor.capture());
        ClientForm selectedClientForm = clientFormArgumentCaptor.getValue();
        assertEquals(AllConstants.CLIENT_FORM_ASSET_VERSION, selectedClientForm.getVersion());
    }
}