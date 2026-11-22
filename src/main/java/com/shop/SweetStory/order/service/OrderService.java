package com.shop.SweetStory.order.service;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.catalog.dto.ActionMessageResponse;
import com.shop.SweetStory.catalog.entity.Product;
import com.shop.SweetStory.catalog.repository.ProductRepository;
import com.shop.SweetStory.order.dto.AdminOrderItemResponse;
import com.shop.SweetStory.order.dto.AdminOrderResponse;
import com.shop.SweetStory.order.dto.CreateOrderRequest;
import com.shop.SweetStory.order.dto.CreateOrderResponse;
import com.shop.SweetStory.order.dto.GroupedOrderItemResponse;
import com.shop.SweetStory.order.dto.GroupedOrdersResponse;
import com.shop.SweetStory.order.dto.MyOrderItemResponse;
import com.shop.SweetStory.order.dto.MyOrderResponse;
import com.shop.SweetStory.order.dto.OrderCreatedItemResponse;
import com.shop.SweetStory.order.dto.OrderCustomerInfo;
import com.shop.SweetStory.order.dto.OrderProductSummary;
import com.shop.SweetStory.order.dto.UpdateOrderStatusRequest;
import com.shop.SweetStory.order.dto.UpdateOrderStatusResponse;
import com.shop.SweetStory.order.entity.Order;
import com.shop.SweetStory.order.entity.OrderStatus;
import com.shop.SweetStory.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final long maxOrdersPerDate;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            @Value("${app.orders.max-per-date:3}") long maxOrdersPerDate
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.maxOrdersPerDate = maxOrdersPerDate;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Long activeOrdersCount = orderRepository.countByDeliveryDateAndStatusNot(
                request.delivery_date(),
                OrderStatus.CANCELLED
        );
        if (activeOrdersCount != null && activeOrdersCount >= maxOrdersPerDate) {
            throw new IllegalStateException("DATE_LIMIT_EXCEEDED: Достигнут лимит заказов на дату " + request.delivery_date());
        }

        User currentUser = resolveCurrentUser();
        Product product = productRepository.findById(request.product_id())
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + request.product_id()));

        Order order = new Order();
        order.setDeliveryDate(request.delivery_date());
        order.setProduct(product);
        order.setUser(currentUser);
        order.setFilling(request.filling());
        order.setDecor(request.decor());
        order.setWeight(request.weight());
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);
        return new CreateOrderResponse(toCreatedItemResponse(saved));
    }

    public MyOrderResponse getMyOrders() {
        User currentUser = resolveCurrentUser();
        List<MyOrderItemResponse> items = orderRepository.getAllByUserIdOrderByDeliveryDateDesc(currentUser.getId())
                .stream()
                .map(this::toMyOrderItemResponse)
                .toList();

        return new MyOrderResponse(items);
    }

    public AdminOrderResponse getAllOrders() {
        List<AdminOrderItemResponse> items = orderRepository.findAllByOrderByDeliveryDateAscIdAsc()
                .stream()
                .map(this::toAdminOrderItemResponse)
                .toList();

        return new AdminOrderResponse(items);
    }

    public GroupedOrdersResponse getOrdersGroupedByDate() {
        Map<LocalDate, List<GroupedOrderItemResponse>> grouped = orderRepository.findAllByOrderByDeliveryDateAscIdAsc()
                .stream()
                .collect(Collectors.groupingBy(
                        Order::getDeliveryDate,
                        TreeMap::new,
                        Collectors.mapping(this::toGroupedOrderItemResponse, Collectors.toList())
                ));

        return new GroupedOrdersResponse(grouped);
    }

    @Transactional
    public UpdateOrderStatusResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = findOrderOrThrow(orderId);
        OrderStatus newStatus = OrderStatus.fromApiValue(request.order_status());
        order.setStatus(newStatus);
        orderRepository.save(order);

        return new UpdateOrderStatusResponse(
                "success",
                order.getId(),
                newStatus.toApiValue()
        );
    }

    @Transactional
    public ActionMessageResponse deleteOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        orderRepository.delete(order);
        return new ActionMessageResponse("success", "Заказ удален");
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new IllegalStateException("Authenticated user not found in security context");
        }
        String principalName = authentication.getName();
        return userRepository.findByEmail(principalName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + principalName));
    }

    private OrderCreatedItemResponse toCreatedItemResponse(Order order) {
        return new OrderCreatedItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                order.getProduct().getName(),
                order.getFilling(),
                order.getDecor(),
                order.getWeight(),
                order.getStatus().toApiValue()
        );
    }

    private MyOrderItemResponse toMyOrderItemResponse(Order order) {
        Product product = order.getProduct();
        OrderProductSummary summary = new OrderProductSummary(
                product.getId(),
                product.getName(),
                product.getPreviewImageUrl()
        );

        return new MyOrderItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                summary,
                order.getFilling(),
                order.getDecor(),
                order.getWeight(),
                order.getStatus().toApiValue()
        );
    }

    private AdminOrderItemResponse toAdminOrderItemResponse(Order order) {
        User user = order.getUser();
        OrderCustomerInfo customerInfo = new OrderCustomerInfo(
                user.getName(),
                user.getPhone(),
                user.getEmail()
        );

        return new AdminOrderItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                order.getStatus().toApiValue(),
                customerInfo,
                order.getProduct().getName(),
                order.getFilling(),
                order.getDecor(),
                order.getWeight()
        );
    }

    private GroupedOrderItemResponse toGroupedOrderItemResponse(Order order) {
        User user = order.getUser();
        return new GroupedOrderItemResponse(
                order.getId(),
                order.getStatus().toApiValue(),
                user.getName(),
                user.getPhone(),
                order.getProduct().getName(),
                order.getFilling(),
                order.getDecor(),
                order.getWeight()
        );
    }
}