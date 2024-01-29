package ru.practicum.shareit.user.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты сервиса для {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private User firstUser;

    private User secondUser;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        firstUser = User.builder()
                .id(1L)
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        secondUser = User.builder()
                .id(2L)
                .name("William Turner")
                .email("bootstrap@gmail.com")
                .build();
    }

    @Test
    void create_whenUserIsValid_thenUserSaved() {
        when(userRepository.save(firstUser)).thenReturn(firstUser);

        User actual = userService.create(firstUser);

        assertThat(firstUser.getId(), equalTo(actual.getId()));
        assertThat(firstUser.getName(), equalTo(actual.getName()));
        assertThat(firstUser.getEmail(), equalTo(actual.getEmail()));

        verify(userRepository).save(firstUser);
    }

    @Test
    void create_whenUserEmailIsDuplicate_thenDataIntegrityViolationExceptionThrown() {
        when(userRepository.save(firstUser)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(firstUser));

        verify(userRepository).save(firstUser);
    }

    @Test
    void patch_whenUserFound_thenUserUpdated() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(firstUser));
        secondUser.setId(userId);
        when(userRepository.save(secondUser)).thenReturn(secondUser);

        User actual = userService.patch(userId, secondUser);

        verify(userRepository).save(userArgumentCaptor.capture());

        User updatedUser = userArgumentCaptor.getValue();
        assertThat(actual.getId(), equalTo(updatedUser.getId()));
        assertThat(actual.getName(), equalTo(updatedUser.getName()));
        assertThat(actual.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    void patch_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.patch(userId, secondUser));

        verify(userRepository, never()).save(any());
    }

    @Test
    void patch_whenUserEmailIsDuplicate_thenEntityAlreadyExistsExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(firstUser));
        when(userRepository.findByEmail(secondUser.getEmail())).thenReturn(firstUser);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.patch(userId, secondUser));

        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_whenUserFound_thenUserReturned() {
        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));

        User actual = userService.getById(firstUser.getId());
        assertThat(firstUser.getId(), equalTo(actual.getId()));
        assertThat(firstUser.getName(), equalTo(actual.getName()));
        assertThat(firstUser.getEmail(), equalTo(actual.getEmail()));
    }

    @Test
    void getById_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(userId));
    }

    @Test
    void getAll_whenUsersFound_ThenUsersReturned() {
        when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));

        List<User> users = userService.getAll();

        assertThat(users, hasSize(2));
        assertThat(firstUser.getId(), equalTo(users.get(0).getId()));
        assertThat(firstUser.getName(), equalTo(users.get(0).getName()));
        assertThat(firstUser.getEmail(), equalTo(users.get(0).getEmail()));
        assertThat(secondUser.getId(), equalTo(users.get(1).getId()));
        assertThat(secondUser.getName(), equalTo(users.get(1).getName()));
        assertThat(secondUser.getEmail(), equalTo(users.get(1).getEmail()));
    }

    @Test
    void getAll_whenUsersNotFound_ThenEmptyListReturned() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> users = userService.getAll();

        assertThat(users, hasSize(0));
        verify(userRepository).findAll();
    }

    @Test
    void delete_whenUserFound_thenUserDeleted() {
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.delete(userId);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_whenUserNotFound_thenNothingDone() {
        when(userRepository.existsById(userId)).thenReturn(false);
        userService.delete(userId);

        verify(userRepository, never()).deleteById(1L);
    }
}