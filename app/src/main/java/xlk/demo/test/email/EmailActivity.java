package xlk.demo.test.email;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import xlk.demo.test.R;

public class EmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
    }

    public void send(View view) {
        List<String> receivers = new ArrayList<>();
        receivers.add("xlk214501580@163.com");
        receivers.add("nhw214501580@qq.com");
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PaperlessStandardEdition/client.ini"));
        files.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PaperlessStandardEdition/File/图片/main_logo.png"));
        EmailThread emailUtil = new EmailThread("nhw214501580@qq.com", "esayzafbzvofbjag", receivers, files, "测试发送多人邮件", "测试发送多人邮件内容");
        emailUtil.start();
    }

    private void mail() {
        Authenticator authenticator = new Authenticator() {//身份认证直接重写也没有问题
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("xlk214501580@163.com", "fuck5689");
            }
        };
        try {
            Properties properties = new Properties();// 邮件相关配置
            properties.put("mail.smtp.host", "smtp.163.com");//邮箱服务地址
            properties.put("mail.smtp.port", 25);//邮箱端口，QQ和163应该都是25端口
            properties.put("mail.smtp.auth", "true");//身份认证
            Session session = Session.getDefaultInstance(properties, authenticator);// 根根配置以及验证器构造一个发送邮件的session
            Message message = new MimeMessage(session);
            Address from = new InternetAddress("xlk214501580@163.com");//发送者地址
            message.setFrom(from);
            Address to = new InternetAddress("nhw214501580@qq.com");
            message.setRecipient(Message.RecipientType.TO, to);//接收者地址
            message.setSubject("这是一份测试邮件标题");//标题
            message.setSentDate(new Date());
            message.setText("这是一份测试邮件内容");
            Transport.send(message);//发送邮件
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
