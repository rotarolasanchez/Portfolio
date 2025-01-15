package com.rotarola.portafolio_kotlin.presentation.viewmodels;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.compose.material3.SnackbarHostState;

import com.rotarola.portafolio_kotlin.data.model.RequestState;
import com.rotarola.portafolio_kotlin.data.model.UserApp;
import com.rotarola.portafolio_kotlin.domain.entities.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;

import static org.mockito.Mockito.*;

public class LoginViewModelTest {
    //Field loginUseCase of type LoginUseCase - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    //Field realmDBService of type RealmDBService - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    MutableStateFlow<String> _userCode;
    @Mock
    MutableStateFlow<String> _userPassword;
    @Mock
    MutableStateFlow<RequestState<List<User>>> _usersRequest;
    @Mock
    MutableStateFlow<Boolean> _isSnackBackBarSucessful;
    @Mock
    MutableStateFlow<SnackbarHostState> _snackbarHostState;
    //Field impl of type ViewModelImpl - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @InjectMocks
    LoginViewModel loginViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserCode() throws Exception {
        StateFlow<String> result = loginViewModel.getUserCode();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetUserPassword() throws Exception {
        StateFlow<String> result = loginViewModel.getUserPassword();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetUsersRequest() throws Exception {
        StateFlow<RequestState<List<User>>> result = loginViewModel.getUsersRequest();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testIsSnackBackBarSucessful() throws Exception {
        StateFlow<Boolean> result = loginViewModel.isSnackBackBarSucessful();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetSnackbarHostState() throws Exception {
        StateFlow<SnackbarHostState> result = loginViewModel.getSnackbarHostState();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testSetSnackbarHostState() throws Exception {
        loginViewModel.setSnackbarHostState(new SnackbarHostState());
    }

    @Test
    public void testUpdateIsSnackBarSuccessful() throws Exception {
        loginViewModel.updateIsSnackBarSuccessful(true);
    }

    @Test
    public void testUpdateUserLogin() throws Exception {
        loginViewModel.updateUserLogin("user");
    }

    @Test
    public void testUpdatePasswordLogin() throws Exception {
        loginViewModel.updatePasswordLogin("password");
    }

    @Test
    public void testValidateUserPassword() throws Exception {
        loginViewModel.validateUserPassword("user", "password");
    }

    @Test
    public void testInsertUser() throws Exception {
        loginViewModel.insertUser(new UserApp());
    }

    @Test
    public void testGetUsersApp() throws Exception {
        loginViewModel.getUsersApp("code", "password");
    }

    @Test
    public void testClear$lifecycle_viewmodel_release() throws Exception {
        loginViewModel.clear$lifecycle_viewmodel_release();
    }

    @Test
    public void testAddCloseable() throws Exception {
        loginViewModel.addCloseable("key", null);
    }

    @Test
    public void testAddCloseable2() throws Exception {
        loginViewModel.addCloseable(null);
    }

}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme