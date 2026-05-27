Sản phẩm X có tồn kho ban đầu là 8. 
Người dùng A thêm 5 sản phẩm X vào giỏ hàng. 
Sau đó, người dùng B mua 3 sản phẩm X làm tồn kho giảm xuống còn 5.

Khi người dùng A cập nhật số lượng sản phẩm X lên 7, 
yêu cầu này không còn hợp lệ vì tồn kho thực tế không đủ. 
Nếu ShoppingCartService không kiểm tra tồn kho mới nhất hoặc kiểm tra không đúng cách, 
hệ thống có thể cho phép cập nhật sai hoặc trả về lỗi không rõ ràng, gây nhầm lẫn cho người dùng.