Demo có 2 luồng chính
1. Thanh toán thành công

1.1 Order Service:
+ Nhận request tạo đơn hàng từ Client
+ Lưu đơn hàng vào Database với trạng thái PENDING
+ GỬi event OrderCreatedEvent lên Kafka với topic order_created

1.2 Payment Service:
+ Consume event từ topic order_created
+ Lưu thông tin thanh toán với trạng thái PENDING
+ Xử lý thanh toán -> Giả lập thanh toán bằng Random(), nếu Success thì update trạng thái Payment thành SUCCESS
+ Gửi sự kiện PaymentStatusEvent lên Kafka topic payment_completed

1.3 Notification Service:
+ Consume sự kiện từ topic payment_completed
+ Gửi Email có đơn hàng mới về email của admin

2. Xử lý đơn treo

2.1 Order Service:
+ Tạo đơn và gửi OrderCreatedEvent như bình thường

2.2 Payment Service 
+ Nhận event và lưu trạng thái PENDING.
+ Xử lý thanh toán -> Giả lập thanh toán bằng Random(), nếu Failed thì giữ nguyên trạng thái PENDING
Process bị dừng lại, không update trạng thái và không gửi event
+ Kết quả: Record ở trạng thái PENDING trong Database

2.3 Cron job (db-scheduler):
+ Tạo 1 task tên cleanup-pending-payments tự động chạy mỗi phút
+ Quét DB: Tìm các Payment PENDING đã tạo quá giới hạn thời gian (2 phút)
+ Xử lý:
Chuyển trạng thái sang CANCELLED
Gửi event lỗi PaymentStatusEvent vào Kafka với topic payment.failed

2.4 Notification Service:
+ Nhận event từ payment.failed
+ Gửi email thông báo hủy đơn hàng