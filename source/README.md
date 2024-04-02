# ỨNG DỤNG HỖ TRỢ NGƯỜI DÙNG HỌC TỪ VỰNG TIẾNG ANH (VOCAWANDER)

[Chi tiết](https://docs.google.com/document/d/1pv8gwEzxe0LyAQKAl8ADJjF1OKS3qHfs/edit)

## Mô tả
Ứng dụng hỗ trợ người dùng học từ vựng Tiếng Anh theo dạng flashcard cho phép người dùng tạo ra các topic chứa các từ vựng cùng 1 chủ đề, sau đó học và làm bài tập kiểm tra dưới nhiều hình thức khác nhau.

## Các tính năng

- **Tính năng về tài khoản**: thực hiện các thao tác cơ bản liên quan đến tài khoản người dùng như đăng kí, đăng nhập, thay đổi thông tin, khôi phục mật khẩu, đăng xuất,...
- **Tạo và quản lí các topic**: thực hiện các thao tác liên quan đến nghiệp vụ quản lí các topic (CRUD).
- **Chế độ học tập**: có 3 chế độ học cho người dùng lựa chọn bao gồm Flashcard, trắc nghiệm, gõ từ.
- **Tính năng cộng đồng**: phân quyền người dùng tương ứng với các loại topic (private/public), xếp hạng thành tích,...

## Các yêu cầu:
### Chức năng về tài khoản:
- Khi tạo một tài khoản mới, người dùng cần cung cấp các thông tin như email (hoặc số điện thoại) và mật khẩu. Sau đó người dùng cần đăng nhập với tài khoản vừa tạo để sử dụng ứng dụng. 
- Đổi mật khẩu ở trang cài đặt. 
- Nếu quên mật khẩu, ứng dụng phải cung cấp một cơ chế khôi phục mật khẩu phù hợp (qua email hoặc tin nhắn OTP).

### Chức năng quản lí từ vựng, topic, folder
- Topic chứa không hoặc nhiều từ vựng, folder chứa không hoặc nhiều topic (không bắt buộc dùng folder chứa topic). Nếu xóa folder thì chỉ có folder bị xóa, các topic vẫn tồn tại.
- Người dùng có thể xem danh sách các topic đã tạo (được sắp xếp theo thời gian tạo hoặc truy cập gần đây) cùng với thông tin tóm tắt như tên chủ đề và số lượng từ,... (nếu có chức năng hiển thị tiến độ học ở từng chủ đề thì tốt).
- Người dùng dễ dàng tạo 1 topic mới và thêm từ vựng vào chủ đề đó. Số lượng từ cho mỗi chủ đề không bị giới hạn. Khi thêm từ mới vào chủ đề, người dùng cần cung cấp tối thiểu thông tin bao gồm từ tiếng Anh và tiếng Việt tương ứng. 
- Có thể nhập nhiều từ vựng từ tập tin .csv. Trước khi lưu vào dữ liệu, ứng dụng hiển thị preview để người dùng xem và xác nhận thông tin từ tập tin, cho phép điều chỉnh nếu có.
- Mặc định topic là riêng tư. Tuy nhiên, khi tạo chủ đề, người dùng có thể chọn chế độ công khai (public), cho phép mọi người trong hệ thống xem (read only) và tham gia học.

### Các chức năng học từ vựng
- **Tính năng học bằng FlashCard**: cho phép người dùng học từng từ vựng trong chủ đề, hiển thị mỗi từ trên một flashcard với hai mặt (2 ngôn ngữ). Người dùng có thể lướt tuần tự hoặc bật chế độ tự động. Cung cấp thông tin tiến độ hiện tại và các chức năng như lật thẻ, phát âm, di chuyển giữa từng từ, và cài đặt như trộn thứ tự từ, phát âm tự động, đổi vị trí ngôn ngữ và học từ được đánh dấu.
- **Chức năng trắc nghiệm**: hiển thị từng từ vựng với 4 lựa chọn, một đúng và ba sai, được lấy từ các từ khác trong chủ đề. Người dùng có thể tùy chỉnh hiển thị ngôn ngữ và sau bài học, xem điểm số, feedback và danh sách từ vựng trả lời đúng hoặc sai.
- **Chức năng gõ từ**: cho phép người dùng xem từ tiếng Việt và nhập nghĩa tiếng Anh (hoặc ngược lại) vào một textbox. Không yêu cầu nhập đúng viết hoa hoặc viết thường. Sau khi hoàn thành, người dùng xem kết quả và chi tiết về những gì đã làm.

> Với mỗi topic cần ghi nhận được tiến độ học của người dùng, cần phân chia các từ vựng vào ít nhất 3 nhóm: chưa học, đang học và đã ghi nhớ, dựa trên việc học thực tế của người dùng trên topic đó. Cần lưu ý rằng sau khi một topic được tạo ra và được học một thời gian, author hoàn toàn có thể bổ sung thêm hoặc xóa bớt từ vựng trong danh sách.

### Các chức năng liên quan đến lưu trữ trực tuyến
- Cơ sở dữ liệu: firebase
- Ứng dụng cần có giao diện riêng để hiển thị danh sách các chủ đề từ vựng được tạo public bởi người dùng khác trên hệ thống, sắp xếp theo thời gian mới nhất. Mỗi mục cần chứa tiêu đề, thông tin người tạo, số lượng từ vựng, số lượng người đã tham gia học và các thông tin cần thiết khác về topic.
- Người dùng có thể xem danh sách các chủ đề public và chọn muốn học topic nào để lưu vào danh sách cá nhân.
- Sau khi lưu vào danh sách cá nhân, người dùng có thể tham gia học nhưng không thể điều chỉnh. Chỉ tác giả được quyền điều chỉnh.
- Khi xem chi tiết một public topic, người dùng có thể xem bảng xếp hạng với thành tích tốt của những người tham gia học trên topic này.

### Tính năng gợi ý:
- Tự động dịch nghĩa và điền vào phần tiếng việt, sao đó vẫn cho phép người dùng điều chỉnh theo ý muốn.
- Chụp ảnh và hiển thị các từ vựng tiếng Anh của các đồ vật có trong đó
