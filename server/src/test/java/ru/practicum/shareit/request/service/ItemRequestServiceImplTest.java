package ru.practicum.shareit.request.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.internal.ItemRequestModel;
import ru.practicum.shareit.request.mapper.ItemRequestConverter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты сервиса для {@link ItemRequestServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestConverter itemRequestConverter;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest firstRequest;

    private ItemRequest secondRequest;

    private ItemRequestModel itemRequestModel;

    private ItemRequestModel.ItemModel itemModel;

    private Item item;

    private Long userId;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userId = 1L;
        firstRequest = ItemRequest.builder()
                .id(1L)
                .description("description 1")
                .creationDate(LocalDateTime.now())
                .build();
        secondRequest = ItemRequest.builder()
                .id(2L)
                .description("description 2")
                .creationDate(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("item 1")
                .description("description")
                .available(true)
                .request(firstRequest)
                .build();
        itemModel = ItemRequestModel.ItemModel.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
        itemRequestModel = ItemRequestModel.builder()
                .id(firstRequest.getId())
                .description(firstRequest.getDescription())
                .creationDate(firstRequest.getCreationDate())
                .items(Collections.emptyList())
                .build();
        pageable = PageRequest.of(DEFAULT_PAGE_START / DEFAULT_PAGE_SIZE, DEFAULT_PAGE_SIZE);
    }

    @Test
    void create_whenItemRequestIsValid_thenItemRequestSaved() {
        when(itemRequestRepository.save(firstRequest)).thenReturn(firstRequest);
        when(userRepository.existsById(userId)).thenReturn(true);

        ItemRequest actual = itemRequestService.create(userId, firstRequest);

        assertThat(firstRequest.getId(), equalTo(actual.getId()));
        assertThat(firstRequest.getDescription(), equalTo(actual.getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(actual.getCreationDate()));

        verify(itemRequestRepository).save(firstRequest);
    }

    @Test
    void create_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, firstRequest));

        verify(itemRequestRepository, never()).save(firstRequest);
    }

    @Test
    void getByRequestorId_whenItemRequestFound_thenItemRequestReturned() {
        when(itemRequestRepository.findByRequestorIdOrderByCreationDateDesc(userId))
                .thenReturn(List.of(firstRequest, secondRequest));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllItemsByRequestIds(List.of(firstRequest.getId(), secondRequest.getId()))).thenReturn(Collections.emptyMap());

        List<ItemRequestModel> requests = itemRequestService.getByRequestorId(userId);
        assertThat(requests, hasSize(2));
        assertThat(firstRequest.getId(), equalTo(requests.get(0).getId()));
        assertThat(firstRequest.getDescription(), equalTo(requests.get(0).getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(requests.get(0).getCreationDate()));
        assertThat(secondRequest.getId(), equalTo(requests.get(1).getId()));
        assertThat(secondRequest.getDescription(), equalTo(requests.get(1).getDescription()));
        assertThat(secondRequest.getCreationDate(), equalTo(requests.get(1).getCreationDate()));

        verify(itemRequestRepository).findByRequestorIdOrderByCreationDateDesc(userId);
    }

    @Test
    void getByRequestorId_whenItemRequestNotFound_thenEmptyListReturned() {
        when(itemRequestRepository.findByRequestorIdOrderByCreationDateDesc(userId))
                .thenReturn(Collections.emptyList());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllItemsByRequestIds(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());

        List<ItemRequestModel> requests = itemRequestService.getByRequestorId(userId);
        assertThat(requests, hasSize(0));

        verify(itemRequestRepository).findByRequestorIdOrderByCreationDateDesc(userId);
    }

    @Test
    void getByRequestorId_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getByRequestorId(userId));

        verify(itemRequestRepository, never()).findByRequestorIdOrderByCreationDateDesc(userId);
    }

    @Test
    void getByRequestorId_whenItemRequestIsValidWithItem_thenItemRequestReturned() {
        when(itemRequestRepository.findByRequestorIdOrderByCreationDateDesc(userId))
                .thenReturn(List.of(firstRequest));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllItemsByRequestIds(List.of(firstRequest.getId())))
                .thenReturn(Map.of(firstRequest.getId(), List.of(item)));
        when(itemRequestConverter.convertItems(List.of(item))).thenReturn(List.of(itemModel));

        List<ItemRequestModel> requests = itemRequestService.getByRequestorId(userId);
        assertThat(requests, hasSize(1));
        assertThat(firstRequest.getId(), equalTo(requests.get(0).getId()));
        assertThat(firstRequest.getDescription(), equalTo(requests.get(0).getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(requests.get(0).getCreationDate()));
        assertThat(requests.get(0).getItems(), hasSize(1));
        assertThat(item.getId(), equalTo(requests.get(0).getItems().get(0).getId()));
        assertThat(item.getName(), equalTo(requests.get(0).getItems().get(0).getName()));
        assertThat(item.getDescription(), equalTo(requests.get(0).getItems().get(0).getDescription()));
        assertThat(item.getAvailable(), equalTo(requests.get(0).getItems().get(0).getAvailable()));
        assertThat(item.getRequest().getId(), equalTo(requests.get(0).getItems().get(0).getRequestId()));

        verify(itemRequestRepository).findByRequestorIdOrderByCreationDateDesc(userId);
    }

    @Test
    void getByUserId_whenItemRequestFound_thenItemRequestReturned() {
        when(itemRequestRepository.findByRequestorIdNot(userId, pageable))
                .thenReturn(List.of(firstRequest, secondRequest));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllItemsByRequestIds(List.of(firstRequest.getId(), secondRequest.getId())))
                .thenReturn(Collections.emptyMap());

        List<ItemRequestModel> requests = itemRequestService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
        assertThat(requests, hasSize(2));
        assertThat(firstRequest.getId(), equalTo(requests.get(0).getId()));
        assertThat(firstRequest.getDescription(), equalTo(requests.get(0).getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(requests.get(0).getCreationDate()));
        assertThat(secondRequest.getId(), equalTo(requests.get(1).getId()));
        assertThat(secondRequest.getDescription(), equalTo(requests.get(1).getDescription()));
        assertThat(secondRequest.getCreationDate(), equalTo(requests.get(1).getCreationDate()));

        verify(itemRequestRepository).findByRequestorIdNot(userId, pageable);
    }

    @Test
    void getByUserId_whenItemRequestNotFound_thenEmptyListReturned() {
        when(itemRequestRepository.findByRequestorIdNot(userId, pageable))
                .thenReturn(Collections.emptyList());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllItemsByRequestIds(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());

        List<ItemRequestModel> requests = itemRequestService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
        assertThat(requests, hasSize(0));

        verify(itemRequestRepository).findByRequestorIdNot(userId, pageable);
    }

    @Test
    void getByUserId_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getByUserId(userId, DEFAULT_PAGE_START,
                DEFAULT_PAGE_SIZE));

        verify(itemRequestRepository, never()).findByRequestorIdNot(userId,  pageable);
    }

    @Test
    void getByUserId_whenItemRequestIsValidWithItem_thenItemRequestReturned() {
        when(itemRequestRepository.findByRequestorIdNot(userId, pageable))
                .thenReturn(List.of(firstRequest));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllItemsByRequestIds(List.of(firstRequest.getId())))
                .thenReturn(Map.of(firstRequest.getId(), List.of(item)));
        when(itemRequestConverter.convertItems(List.of(item))).thenReturn(List.of(itemModel));

        List<ItemRequestModel> requests = itemRequestService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
        assertThat(requests, hasSize(1));
        assertThat(firstRequest.getId(), equalTo(requests.get(0).getId()));
        assertThat(firstRequest.getDescription(), equalTo(requests.get(0).getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(requests.get(0).getCreationDate()));
        assertThat(requests.get(0).getItems(), hasSize(1));
        assertThat(item.getId(), equalTo(requests.get(0).getItems().get(0).getId()));
        assertThat(item.getName(), equalTo(requests.get(0).getItems().get(0).getName()));
        assertThat(item.getDescription(), equalTo(requests.get(0).getItems().get(0).getDescription()));
        assertThat(item.getAvailable(), equalTo(requests.get(0).getItems().get(0).getAvailable()));
        assertThat(item.getRequest().getId(), equalTo(requests.get(0).getItems().get(0).getRequestId()));

        verify(itemRequestRepository).findByRequestorIdNot(userId, pageable);
    }

    @Test
    void getById_whenItemRequestFound_thenItemRequestReturned() {
        when(itemRequestRepository.findById(firstRequest.getId()))
                .thenReturn(Optional.of(firstRequest));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByRequestId(firstRequest.getId())).thenReturn(Collections.emptyList());
        when(itemRequestConverter.convert(firstRequest, Collections.emptyList())).thenReturn(itemRequestModel);

        ItemRequestModel request = itemRequestService.getById(userId, firstRequest.getId());
        assertThat(firstRequest.getId(), equalTo(request.getId()));
        assertThat(firstRequest.getDescription(), equalTo(request.getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(request.getCreationDate()));
        assertThat(request.getItems(), hasSize(0));

        verify(itemRequestRepository).findById(firstRequest.getId());
    }

    @Test
    void getById_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
        when(itemRequestRepository.findById(firstRequest.getId()))
                .thenThrow(EntityNotFoundException.class);
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getById(userId, firstRequest.getId()));

        verify(itemRequestRepository).findById(firstRequest.getId());
    }

    @Test
    void getById_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getByRequestorId(userId));

        verify(itemRequestRepository, never()).findById(firstRequest.getId());
    }

    @Test
    void getById_whenItemRequestIsValidWithItem_thenItemRequestReturned() {
        when(itemRequestRepository.findById(firstRequest.getId()))
                .thenReturn(Optional.of(firstRequest));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByRequestId(firstRequest.getId())).thenReturn(List.of(item));
        itemRequestModel.setItems(List.of(itemModel));
        when(itemRequestConverter.convert(firstRequest, List.of(item))).thenReturn(itemRequestModel);

        ItemRequestModel request = itemRequestService.getById(userId, firstRequest.getId());
        assertThat(firstRequest.getId(), equalTo(request.getId()));
        assertThat(firstRequest.getDescription(), equalTo(request.getDescription()));
        assertThat(firstRequest.getCreationDate(), equalTo(request.getCreationDate()));
        assertThat(request.getItems(), hasSize(1));
        assertThat(item.getId(), equalTo(request.getItems().get(0).getId()));
        assertThat(item.getName(), equalTo(request.getItems().get(0).getName()));
        assertThat(item.getDescription(), equalTo(request.getItems().get(0).getDescription()));
        assertThat(item.getAvailable(), equalTo(request.getItems().get(0).getAvailable()));
        assertThat(item.getRequest().getId(), equalTo(request.getItems().get(0).getRequestId()));

        verify(itemRequestRepository).findById(firstRequest.getId());
    }
}