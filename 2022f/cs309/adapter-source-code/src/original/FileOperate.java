package original;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileOperate implements FileOperateInterfaceV1 {

    @Override
    public List<StaffModel> readStaffFile() {
        List<StaffModel> list = new ArrayList<>();
        try {
            URL url = new URL("http://cse.sustech.edu.cn/en/people/tenure/type/professor");
            Document doc = Jsoup.parse(url, 10000);
            Elements teachers = doc.getElementsByClass("text");
            teachers.forEach(element -> {
                StaffModel staffModel = new StaffModel();
                staffModel.setName(element.getElementsByClass("name").first().text());
                staffModel.setTitle(element.child(1).text());
                staffModel.setEmail(element.child(2).text().replace("_AT_", "@"));
                staffModel.setRoom(element.child(3).text());
                if (Objects.nonNull(element.getElementsByClass("teacher-box-href").first())) {
                    staffModel.setLink(element.getElementsByClass("teacher-box-href").first().attr("href"));
                }
                list.add(staffModel);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("finish read");
        return list;
    }

    @Override
    public void printStaffFile(List<StaffModel> list) {
        if (list.size() == 0) {
            System.out.println("no staff information");
        } else {
            for (StaffModel s : list) {
                System.out.println(s);
            }
        }

    }

    @Override
    public void writeStaffFile(List<StaffModel> list) {
        try {
            if (list.size() == 0) {
                System.out.println("No information to be written");
                return;
            }
            FileWriter f = new FileWriter(getClass().getResource("staff.txt").getFile());
            BufferedWriter bufw = new BufferedWriter(f);
            String str = "";
            for (StaffModel s : list) {
                str = s.getName() + ":" +
                        s.getTitle() + "," +
                        s.getRoom() + "," +
                        s.getEmail() + "," +
                        s.getLink();
                bufw.write(str);
                bufw.newLine();
            }
            bufw.flush();
            bufw.close();
            System.out.println("finish writing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}