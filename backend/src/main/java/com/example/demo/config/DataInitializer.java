package com.example.demo.config;

import com.example.demo.entity.VocabSet;
import com.example.demo.entity.VocabWord;
import com.example.demo.repository.VocabSetRepository;
import com.example.demo.util.TextParserUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final VocabSetRepository setRepository;

    public DataInitializer(VocabSetRepository setRepository) {
        this.setRepository = setRepository;
    }

    private static final String SEED_DATA = """
paint\t/peɪnt/\t(v) sơn, vẽ
park\t/pɑːrk/\t(v) đỗ xe; (n) công viên
fish\t/fɪʃ/\t(v) câu cá, đánh bắt cá; (n) con cá
leave - left - left\t/liːv/ - /left/ - /left/\t(v) rời đi; để lại, bỏ lại
lean\t/liːn/\t(v) nghiêng, dựa vào
polish\t/ˈpɒlɪʃ/\t(v) đánh bóng
step\t/step/\t(v) bước; (n) bước chân; bậc thang
throw away\t/ˌθrəʊ əˈweɪ/\t(v) vứt đi
tape\t/teɪp/\t(v) dán; (n) băng dính
staple\t/ˈsteɪpl/\t(v) dập ghim
look over\t/lʊk ˈoʊvər/\t(v) nhìn qua, xem qua
prop up\t/prɒp ʌp/\t(v) chống đỡ, dựng lên
put away\t/pʊt əˈweɪ/\t(v) cất đi
arrange\t/əˈreɪndʒ/\t(v) sắp xếp
share\t/ʃer/\t(v) chia sẻ
remove\t/rɪˈmuːv/\t(v) gỡ bỏ, loại bỏ
pilot\t/ˈpaɪlət/\t(v) lái (máy bay, phi cơ); (n) phi công
sweep - swept - swept\t/swiːp/ - /swɛpt/ - /swɛpt/\t(v) quét
wrap\t/ræp/\t(v) gói, bọc
install\t/ɪnˈstɔːl/\t(v) lắp đặt
pull\t/pʊl/\t(v) kéo
push\t/pʊʃ/\t(v) đẩy
lay - laid - laid\t/leɪ/ - /leɪd/ - /leɪd/\t(v) đặt, để
slice\t/slaɪs/\t(v) cắt lát
kneel\t/niːl/\t(v) quỳ
pack\t/pæk/\t(v) đóng gói
compare\t/kəmˈper/\t(v) so sánh
plug (in)\t/plʌɡ/\t(v) cắm (điện)
unplug\t/ˌʌnˈplʌɡ/\t(v) rút phích cắm
wait in line (stand in line / line up)\t/weɪt ɪn laɪn/\t(v) xếp hàng
be seated (= sit)\t/ˈbiː ˈsiːtɪd/ = /sɪt/\t(v) ngồi xuống
reach\t/riːtʃ/\t(v) với tới, chạm tới
surround\t/səˈraʊnd/\t(v) bao quanh, vây quanh
under construction\t/ˌʌndə kənˈstrʌkʃn/\t(phr) đang trong quá trình xây dựng
bicyclist (cyclist)\t/ˈbaɪsɪklɪst/\t(n) người đi xe đạp
dentist\t/ˈdentɪst/\t(n) nha sĩ
swimmer\t/ˈswɪmər/\t(n) người bơi
fisherman\t/ˈfɪʃəmən/\t(n) ngư dân
gate\t/ɡeɪt/\t(n) cổng
shore\t/ʃɔːr/\t(n) bờ biển, bờ hồ
net\t/net/\t(n) lưới
sunglasses\t/ˈsʌnˌɡlɑːsɪz/\t(n) kính râm
vase\t/vɑːz/ (Br) - /veɪs/ (Am)\t(n) bình hoa
pillow\t/ˈpɪləʊ/\t(n) cái gối
sheet\t/ʃiːt/\t(n) ga trải giường; tờ giấy; tấm
walkway\t/ˈwɑːkweɪ/\t(n) lối đi bộ
railing\t/ˈreɪlɪŋ/\t(n) lan can
handrail\t/ˈhændreɪl/	(n) tay vịn cầu thang
poster\t/ˈpoʊstər/\t(n) áp phích
shoulder\t/ˈʃoʊldər/\t(n) vai
ladder\t/ˈlædər/\t(n) thang trèo
potted plant\t/ˈpɒtɪd plɑːnt/\t(n) cây trồng trong chậu
border\t/ˈbɔːrdər/\t(n) rìa, biên giới, đường viền
patio\t/ˈpætioʊ/\t(n) hiên, sân lát gạch ngoài trời
bench\t/bentʃ/\t(n) ghế dài
tablecloth\t/ˈteɪblklɒθ/\t(n) khăn trải bàn
briefcase\t/ˈbriːfkeɪs/\t(n) cặp tài liệu
cart\t/kɑːrt/\t(n) xe đẩy
brick\t/brɪk/\t(n) viên gạch
dessert\t/dɪˈzɜːrt/\t(n) món tráng miệng
pie\t/paɪ/\t(n) bánh nướng
purchase\t/ˈpɜː.tʃəs/\t(v) mua; (n) món hàng, đơn mua
pocket\t/ˈpɒkɪt/\t(n) túi áo, túi quần
luggage\t/ˈlʌɡɪdʒ/\t(n) hành lý
crowded (with)\t/ˈkraʊdɪd/\t(adj) đông đúc (với ...)
visible\t/ˈvɪzəbl/\t(adj) có thể nhìn thấy
director\t/daɪˈrektər/ - /dəˈrektər/\t(n) giám đốc; đạo diễn
examine\t/ɪɡˈzæmɪn/\t(v) kiểm tra, xem xét
environmental\t/ɪnˌvaɪrənˈmentl/\t(adj) (thuộc về) môi trường
effect\t/ɪˈfekt/\t(n) hiệu quả, tác động
activity\t/ækˈtɪvəti/\t(n) hoạt động
president\t/ˈprezɪdənt/\t(n) chủ tịch; tổng thống
unintentionally\t/ˌʌnɪnˈtenʃənəli/\t(adv) vô tình, không cố ý
timetable\t/ˈtaɪmteɪbl/\t(n) thời khóa biểu, thời gian biểu
necessary\t/ˈnesəsəri/ - /ˈnesəseri/\t(adj) cần thiết
office supply\t/ˈɒfɪs səˌplaɪ/\t(n) văn phòng phẩm
catalog\t/ˈkætəlɒɡ/\t(n) cuốn danh mục (sản phẩm)
manager\t/ˈmænɪdʒər/\t(n) người quản lý
management\t/ˈmænɪdʒmənt/\t(n) sự quản lý; ban quản lý
announce\t/əˈnaʊns/\t(v) thông báo, công bố
presentation\t/ˌpreznˈteɪʃn/\t(n) bài thuyết trình, buổi trình bày
salary\t/ˈsæləri/\t(n) tiền lương (hàng tháng)
increase\t/ɪnˈkriːs/ (v) - /ˈɪnkriːs/ (n)\t(v) tăng lên; (n) sự tăng lên
representative\t/ˌreprɪˈzentətɪv/\t(n) người đại diện
whole\t/hoʊl/\t(adj) toàn bộ, toàn thể
arrival\t/əˈraɪvl/\t(n) sự đến, sự xuất hiện
guest\t/ɡest/\t(n) khách
reserve\t/rɪˈzɜːrv/\t(v) đặt trước, giữ chỗ
solution\t/səˈluːʃn/\t(n) giải pháp
""";

    @Override
    public void run(String... args) {
        if (setRepository.count() == 0) {
            VocabSet defaultSet = new VocabSet(
                    "Bộ từ vựng TOEIC khởi tạo (79 từ)",
                    "Bộ từ vựng cơ bản và nâng cao thường gặp trong TOEIC, tranh Part 1 và giao tiếp công sở."
            );
            List<VocabWord> words = TextParserUtil.parseRawText(SEED_DATA);
            for (VocabWord w : words) {
                defaultSet.addWord(w);
            }
            setRepository.save(defaultSet);
            System.out.println(">>> Đã khởi tạo thành công bộ từ vựng mẫu với " + words.size() + " từ!");
        }
    }
}
