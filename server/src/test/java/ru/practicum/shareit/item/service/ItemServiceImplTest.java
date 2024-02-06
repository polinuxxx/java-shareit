package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.internal.BookingModel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.OperationConstraintException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.internal.CommentModel;
import ru.practicum.shareit.item.internal.ItemModel;
import ru.practicum.shareit.item.mapper.ItemModelConverter;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для {@link ItemServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemModelConverter itemModelConverter;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private User user;

    private User booker;

    private Item firstItem;

    private Item secondItem;

    private Comment comment;

    private Booking lastBooking;

    private Booking nextBooking;

    private Long itemRequestId;

    private Long userId;

    private Long itemId;

    private ItemModel itemModel;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemRequestId = 1L;
        itemId = 1L;
        user = User.builder()
                .id(1L)
                .name("Denis")
                .email("denis@gmail.com")
                .build();
        booker = User.builder()
                .id(2L)
                .name("Anton")
                .email("anton@gmail.com")
                .build();
        firstItem = Item.builder()
                .id(1L)
                .name("thing")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        secondItem = Item.builder()
                .id(2L)
                .name("something")
                .description("des")
                .available(false)
                .owner(user)
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(user)
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .item(firstItem)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.SECONDS))
                .item(firstItem)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        itemModel = ItemModel.builder()
                .id(1L)
                .name("thing")
                .description("description")
                .available(true)
                .comments(List.of(CommentModel.builder()
                        .id(1L)
                        .text("comment")
                        .authorName("Denis")
                        .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                        .build()))
                .lastBooking(BookingModel.builder()
                        .id(1L)
                        .bookerId(1L)
                        .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                        .build())
                .nextBooking(BookingModel.builder()
                        .id(2L)
                        .bookerId(2L)
                        .start(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.SECONDS))
                        .build())
                .build();
        pageable = PageRequest.of(DEFAULT_PAGE_START / DEFAULT_PAGE_SIZE, DEFAULT_PAGE_SIZE);
    }

    @Test
    void create_whenItemIsValidWithoutRequest_thenItemSaved() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(firstItem)).thenReturn(firstItem);

        Item actual = itemService.create(userId, firstItem);

        assertThat(actual.getId(), equalTo(firstItem.getId()));
        assertThat(actual.getName(), equalTo(firstItem.getName()));
        assertThat(actual.getDescription(), equalTo(firstItem.getDescription()));
        assertThat(actual.getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(actual.getOwner().getId(), equalTo(firstItem.getOwner().getId()));
        assertThat(actual.getRequest(), nullValue());

        verify(itemRepository).save(firstItem);
    }

    @Test
    void create_whenItemIsValidWithRequest_thenItemSaved() {
        firstItem.setRequest(ItemRequest.builder().id(itemRequestId).build());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(itemRequestId)).thenReturn(true);
        when(itemRepository.save(firstItem)).thenReturn(firstItem);

        Item actual = itemService.create(userId, firstItem);

        assertThat(actual.getId(), equalTo(firstItem.getId()));
        assertThat(actual.getName(), equalTo(firstItem.getName()));
        assertThat(actual.getDescription(), equalTo(firstItem.getDescription()));
        assertThat(actual.getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(actual.getOwner().getId(), equalTo(firstItem.getOwner().getId()));
        assertThat(actual.getRequest().getId(), equalTo(firstItem.getRequest().getId()));

        verify(itemRepository).save(firstItem);
    }

    @Test
    void create_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.create(userId, firstItem));

        verify(itemRepository, never()).save(firstItem);
    }

    @Test
    void create_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
        firstItem.setRequest(ItemRequest.builder().id(itemRequestId).build());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(itemRequestId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.create(userId, firstItem));

        verify(itemRepository, never()).save(firstItem);
    }

    @Test
    void patch_whenItemIsValid_thenItemUpdated() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(firstItem));
        secondItem.setId(itemId);
        when(itemRepository.save(secondItem)).thenReturn(secondItem);

        Item actual = itemService.patch(userId, itemId, secondItem);

        verify(itemRepository).save(itemArgumentCaptor.capture());

        Item updatedItem = itemArgumentCaptor.getValue();
        assertThat(actual.getName(), equalTo(updatedItem.getName()));
        assertThat(actual.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(actual.getAvailable(), equalTo(updatedItem.getAvailable()));
    }

    @Test
    void patch_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.patch(userId, itemId, secondItem));

        verify(itemRepository, never()).save(itemArgumentCaptor.capture());
    }

    @Test
    void patch_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.patch(userId, itemId, secondItem));

        verify(itemRepository, never()).save(itemArgumentCaptor.capture());
    }

    @Test
    void patch_whenNotOwner_thenOperationConstraintExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(firstItem));
        secondItem.setId(itemId);
        secondItem.getOwner().setId(2L);

        assertThrows(OperationConstraintException.class, () -> itemService.patch(userId, itemId, secondItem));

        verify(itemRepository, never()).save(itemArgumentCaptor.capture());
    }

    @Test
    void getById_whenItemFound_thenItemReturned() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(firstItem));
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusIsOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemIdAndStartGreaterThanAndStatusIsOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(nextBooking);
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        when(itemModelConverter.convert(firstItem, lastBooking, nextBooking, List.of(comment)))
                .thenReturn(itemModel);
        ItemModel actual = itemService.getById(userId, itemId);
        assertThat(actual.getId(), equalTo(firstItem.getId()));
        assertThat(actual.getName(), equalTo(firstItem.getName()));
        assertThat(actual.getDescription(), equalTo(firstItem.getDescription()));
        assertThat(actual.getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(actual.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(actual.getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(actual.getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(actual.getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(actual.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(actual.getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(actual.getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(actual.getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
        assertThat(actual.getComments(), hasSize(1));
        assertThat(actual.getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(actual.getComments().get(0).getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(actual.getComments().get(0).getCreationDate(), equalTo(comment.getCreationDate()));
        assertThat(actual.getComments().get(0).getText(), equalTo(comment.getText()));
    }

    @Test
    void getById_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getById(userId, itemId));
    }

    @Test
    void getByUserId_whenItemFound_thenItemReturned() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable))
                .thenReturn(List.of(firstItem));
        when(bookingRepository.findAllLastBookings(anyList(), any(), any()))
                .thenReturn(Map.of(itemId, lastBooking));
        when(bookingRepository.findAllNextBookings(anyList(), any(), any()))
                .thenReturn(Map.of(itemId, nextBooking));
        when(commentRepository.findByItemIds(List.of(itemId))).thenReturn(Map.of(itemId, List.of(comment)));

        when(itemModelConverter.convert(lastBooking))
                .thenReturn(itemModel.getLastBooking());
        when(itemModelConverter.convert(nextBooking))
                .thenReturn(itemModel.getNextBooking());
        when(itemModelConverter.convertComments(List.of(comment)))
                .thenReturn(itemModel.getComments());

        List<ItemModel> actual = itemService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
        assertThat(actual.get(0).getId(), equalTo(firstItem.getId()));
        assertThat(actual.get(0).getName(), equalTo(firstItem.getName()));
        assertThat(actual.get(0).getDescription(), equalTo(firstItem.getDescription()));
        assertThat(actual.get(0).getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(actual.get(0).getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(actual.get(0).getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(actual.get(0).getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(actual.get(0).getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));
        assertThat(actual.get(0).getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(actual.get(0).getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(actual.get(0).getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(actual.get(0).getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
        assertThat(actual.get(0).getComments(), hasSize(1));
        assertThat(actual.get(0).getComments().get(0).getId(), equalTo(comment.getId()));
        assertThat(actual.get(0).getComments().get(0).getAuthorName(), equalTo(comment.getAuthor().getName()));
        assertThat(actual.get(0).getComments().get(0).getCreationDate(), equalTo(comment.getCreationDate()));
        assertThat(actual.get(0).getComments().get(0).getText(), equalTo(comment.getText()));
    }

    @Test
    void getByUserId_whenItemNotFound_thenEmptyListReturned() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable))
                .thenReturn(Collections.emptyList());
        List<ItemModel> items = itemService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(items, hasSize(0));
    }

    @Test
    void search_whenTextIsBlank_thenEmptyListReturned() {
        List<Item> items = itemService.search(null, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(items, hasSize(0));
    }

    @Test
    void search_whenItemFound_thenItemReturned() {
        when(itemRepository.search(anyString(), any())).thenReturn(List.of(firstItem));

        List<Item> items = itemService.search("th", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getId(), equalTo(firstItem.getId()));
        assertThat(items.get(0).getName(), equalTo(firstItem.getName()));
        assertThat(items.get(0).getDescription(), equalTo(firstItem.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(items.get(0).getOwner(), equalTo(firstItem.getOwner()));
        assertThat(items.get(0).getRequest(), equalTo(firstItem.getRequest()));
    }

    @Test
    void createComment_whenCommentIsValid_thenCommentSaved() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndLessThan(anyLong(), anyLong(), any()))
                .thenReturn(Booking.builder().build());
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment actual = itemService.createComment(userId, itemId, comment);
        assertThat(comment.getId(), equalTo(actual.getId()));
        assertThat(comment.getText(), equalTo(actual.getText()));
        assertThat(comment.getAuthor().getName(), equalTo(actual.getAuthor().getName()));
        assertThat(comment.getCreationDate(), equalTo(actual.getCreationDate()));

        verify(commentRepository).save(comment);
    }

    @Test
    void createComment_whenAuthorNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(comment);
    }

    @Test
    void createComment_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(comment);
    }

    @Test
    void createComment_whenItemNotBooked_thenValidationExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndLessThan(anyLong(), anyLong(), any()))
                .thenReturn(null);
        assertThrows(ValidationException.class, () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(comment);
    }
}