package org.example.bai3.service;

import org.example.bai3.entity.CartItem;
import org.example.bai3.entity.Product;
import org.example.bai3.entity.ShoppingCart;
import org.example.bai3.repository.CartRepository;
import org.example.bai3.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;
    private Product iphone;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        iphone = new Product(1L, "iPhone 15", 1000.0, 10);
        cart = new ShoppingCart(100L);
    }

    @Test
    void addProductToCart_Success_NewProduct() {

        when(productRepository.findById(1L)).thenReturn(iphone);

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.empty());

        when(cartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingCart result = shoppingCartService.addProductToCart(100L, 1L, 2);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());

        verify(productRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByUserId(100L);
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    void addProductToCart_Success_ExistingProduct() {

        cart.addItem(new CartItem(1L, 2));

        when(productRepository.findById(1L)).thenReturn(iphone);

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.of(cart));

        when(cartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingCart result = shoppingCartService.addProductToCart(100L, 1L, 3);

        assertEquals(5, result.getItems().get(0).getQuantity());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addProductToCart_ProductNotFound() {

        when(productRepository.findById(1L)).thenReturn(null);

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.addProductToCart(100L, 1L, 2)
                );

        assertEquals("Product not found", ex.getMessage());

        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProductToCart_NotEnoughStock() {

        when(productRepository.findById(1L)).thenReturn(iphone);

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.empty());

        IllegalStateException ex =
                assertThrows(
                        IllegalStateException.class,
                        () -> shoppingCartService.addProductToCart(100L, 1L, 20)
                );

        assertTrue(ex.getMessage().contains("Not enough stock"));

        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProductToCart_InvalidQuantity() {

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.addProductToCart(100L, 1L, 0)
                );

        assertEquals("Quantity must be positive", ex.getMessage());

        verifyNoInteractions(productRepository);
        verifyNoInteractions(cartRepository);
    }


    @Test
    void updateProductQuantity_Success() {

        cart.addItem(new CartItem(1L, 2));

        when(cartRepository.findByUserId(100L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(1L))
                .thenReturn(iphone);

        when(cartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingCart result =
                shoppingCartService.updateProductQuantity(
                        100L,
                        1L,
                        5
                );

        assertEquals(
                5,
                result.getItems().get(0).getQuantity()
        );

        verify(cartRepository, times(1))
                .save(cart);
    }

    @Test
    void updateProductQuantity_CartNotFound() {

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.updateProductQuantity(100L, 1L, 5)
                );

        assertTrue(ex.getMessage().contains("Cart not found"));

        verify(productRepository, never()).findById(any());
    }

    @Test
    void updateProductQuantity_ProductNotFoundInCart() {

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.of(cart));

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.updateProductQuantity(100L, 1L, 5)
                );

        assertTrue(ex.getMessage().contains("Product not found in cart"));

        verify(productRepository, never()).findById(any());
    }

    @Test
    void updateProductQuantity_NotEnoughStock() {

        cart.addItem(new CartItem(1L, 2));

        iphone.setStock(3);

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.of(cart));

        when(productRepository.findById(1L)).thenReturn(iphone);

        IllegalStateException ex =
                assertThrows(
                        IllegalStateException.class,
                        () -> shoppingCartService.updateProductQuantity(100L, 1L, 5)
                );

        assertTrue(ex.getMessage().contains("Not enough stock"));

        verify(cartRepository, never()).save(any());
    }

    @Test
    void updateProductQuantity_InvalidQuantity() {

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.updateProductQuantity(100L, 1L, 0)
                );

        assertEquals("Quantity must be positive", ex.getMessage());

        verifyNoInteractions(cartRepository);
    }


    @Test
    void removeProductFromCart_Success() {

        cart.addItem(new CartItem(1L, 2));

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.of(cart));

        when(cartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingCart result = shoppingCartService.removeProductFromCart(100L, 1L);

        assertEquals(0, result.getItems().size());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void removeProductFromCart_CartNotFound() {

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.removeProductFromCart(100L, 1L)
                );

        assertTrue(ex.getMessage().contains("Cart not found"));

        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeProductFromCart_ProductNotFoundInCart() {

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.of(cart));

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> shoppingCartService.removeProductFromCart(100L, 1L)
                );

        assertTrue(ex.getMessage().contains("Product not found in cart"));

        verify(cartRepository, never()).save(any());
    }


    @Test
    void updateProductQuantity_ExceedNewReducedStock() {

        cart.addItem(new CartItem(1L, 5));

        iphone.setStock(7);

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.of(cart));

        when(productRepository.findById(1L)).thenReturn(iphone);

        IllegalStateException ex =
                assertThrows(
                        IllegalStateException.class,
                        () -> shoppingCartService.updateProductQuantity(100L, 1L, 8)
                );

        assertTrue(ex.getMessage().contains("Not enough stock"));

        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProductToCart_VerifySavedCart() {

        when(productRepository.findById(1L)).thenReturn(iphone);

        when(cartRepository.findByUserId(100L)).thenReturn(Optional.empty());

        when(cartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        shoppingCartService.addProductToCart(100L, 1L, 3);

        ArgumentCaptor<ShoppingCart> captor =
                ArgumentCaptor.forClass(
                        ShoppingCart.class
                );

        verify(cartRepository).save(captor.capture());

        ShoppingCart savedCart = captor.getValue();

        assertEquals(1, savedCart.getItems().size());

        assertEquals(
                3,
                savedCart.getItems().get(0).getQuantity()
        );
    }

}