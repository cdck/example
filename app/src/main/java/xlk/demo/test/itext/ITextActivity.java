package xlk.demo.test.itext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

//import com.itextpdf.text.Anchor;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.pdf.draw.DottedLineSeparator;
//import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import xlk.demo.test.R;

public class ITextActivity extends AppCompatActivity {

//    private Font titlefont, headfont, keyfont, textfont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_text);
        initPdfTextFont();
    }

    private void initPdfTextFont() {
//        try {
//            // 不同字体（这里定义为同一种字体：包含不同字号、不同style）
//            // STSong-Light : Adobe的字体
//            // UniGB-UCS2-H : pdf 字体
//            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//            titlefont = new Font(bfChinese, 16, Font.BOLD);
//            headfont = new Font(bfChinese, 14, Font.BOLD);
//            keyfont = new Font(bfChinese, 10, Font.BOLD);
//            textfont = new Font(bfChinese, 10, Font.NORMAL);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void test() {
//        try {
//            // 创建一个Document对象（pdf文档） A4纸张大小
//            Document document = new Document(PageSize.A4);
//            // 建立一个书写器(Writer)与document对象关联
//            String name = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.pdf";
//            FileOutputStream fos = new FileOutputStream(name);
//            //获取PDF书写器
//            PdfWriter.getInstance(document, fos);
//            // 打开文档
//            document.open();
//            // 向文档中输入一个内容
//            Paragraph paragraph = new Paragraph("会议名称：第一次测试会议",titlefont);
//            //设置文字居中 0靠左 1，居中 2，靠右
//            paragraph.setAlignment(1);
//            //设置左缩进
//            paragraph.setIndentationLeft(12);
//            //设置右缩进
//            paragraph.setIndentationRight(12);
//            //设置首行缩进
//            paragraph.setFirstLineIndent(24);
//            //行间距
//            paragraph.setLeading(20f);
//            //设置段落上空白
//            paragraph.setSpacingBefore(5f);
//            //设置段落下空白
//            paragraph.setSpacingAfter(10f);
//
//            // 直线
//            Paragraph p1 = new Paragraph();
//            p1.add(new Chunk(new LineSeparator()));
//            // 点线
//            Paragraph p2 = new Paragraph();
//            p2.add(new Chunk(new DottedLineSeparator()));
//
//            // 超链接
//            Anchor anchor = new Anchor(new Paragraph("超链接", headfont));
//            anchor.setReference("www.baidu.com");
//
//            document.add(paragraph);
//            document.add(anchor);
//            document.add(p2);
//            document.add(p1);
//
//            // 关闭文档
//            document.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
    }

    public void createPdf(View view) {
        test();
    }
}
