# PGPMail
App send and receive mail, encrypt, decrypt, sign and verify Java
Link: http://lelinhweb.com/index.php/2016/07/14/application-pgp-mail-client-java/
Chạy file .jar để chạy Chương Trình:
- Xuất hiện giao diện của chương trình:
- Vào Setting trên thanh menu để đăng nhập vào tài khoản của gmail hoặc yahoo ( Cần phải đăng nhập trước )
	+ Hostname: smtp.mail.yahoo.com/ smtp.gmail.com
	+ Port: 587
	+ Username: tài khoản yahoo hoặc gmail
	+ Password: password của tài khoản đó.
- Trên giao diện sẽ gồm: 
	+ Nút "Generate Key" dùng để phát sinh cặp PublicKey và PrivateKey, dùng để mã hóa
	+ Các nút "Import pub key", "Export pubkey", "Import pub/pri key", "Export pub/pri key" để import export key vào trong chương trình.
	+ Nút "Send" thực hiện chức năng send mail, click vào sẽ hiện thêm một hôp thoại:
		* Tại hộp thoại này: sẽ điền địa chỉ mail của người bạn sẽ gửi thư, tiêu đề, nội dung của mail, hoặc gửi file đính kèm.

		* Nếu bạn muốn mã hóa mail trước khi gửi thì
			**Trước khi click vào nút "Send" bạn cũng có thể chọn các tùy chọn RadioButton(Encrypt asymetric nad symmetric
				,Encrypt, hoặc Sign), để thực hiện các chức năng như: Mã hóa mail đối xứng, mã hóa mail vừa bất 
				đối xứng hoặc đối xứng, Ký tên lên mail.
			** Có 2 tùy chọn "Sign After Encrypt', "Sign Before Encrypt", để thực hiện chắc năng ký.
	+ Nút "Inbox" dùng để nhận Mail, khi click nút "Inbox" chương trình sẽ download các message.
		* Trong hộp thoại Inbox sẽ xuất hiện hộp thư, khi click vào các mail trong ListMail, sẽ hiển thị nội dung mail, và ngày tháng gửi mail.
			** Có Button "Delete" sẽ thực hiện chức năng xóa thư gồm các tùy chọn như xóa thư trên Client, Server, hoặc cả 2.
			** Checkbox "Decrypt", khi click vào, chương trình sẽ tìm các thư có mã hóa rồi giải mã ra nội dung thư, không có thì thôi.
			** Checkbox "Verify", click vào sẽ xác nhận chữ ký, để xem thư của bạn có bị người ta có phá thư của mình hay không.
