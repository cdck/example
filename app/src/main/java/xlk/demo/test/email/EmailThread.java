package xlk.demo.test.email;

import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author Created by xlk on 2020/11/16.
 * @desc
 */
public class EmailThread extends Thread {
    private final String TAG = "EmailThread-->";
    private final String user;
    private final String password;
    private final List<String> receivers;
    private final List<File> annex;
    private final String theme;
    private final String content;
    private Session session;
    private String host;
    private MimeMessage message;
    private Transport transport;

    public EmailThread(String from, String password, List<String> receivers, List<File> annex, String theme, String content) {
        this.user = from;
        this.password = password;
        this.receivers = receivers;
        this.annex = annex;
        this.theme = theme;
        this.content = content;
        initHost();
    }

    @Override
    public void run() {
        super.run();
        initParams();
        send();
    }


    private void initHost() {
        if (user.endsWith("163.com")) {
            host = "smtp.163.com";
        } else {
            host = "smtp.qq.com";
        }
        Log.i(TAG, "initHost " + host);
    }

    private void initParams() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);// mail.envisioncitrix.com
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.checkserveridentity", "false");
        properties.put("mail.smtp.ssl.trust", host);
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        };
        session = Session.getInstance(properties, authenticator);
        session.setDebug(false);// 开启后有调试信息
        message = new MimeMessage(session);
    }

    private void send() {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(user);
            from.setPersonal("张三", "UTF-8");
            message.setFrom(from);
            // 收件人(多个)
            InternetAddress[] sendTo = new InternetAddress[receivers.size()];
            for (int i = 0; i < receivers.size(); i++) {
                sendTo[i] = new InternetAddress(receivers.get(i));
            }
            message.setRecipients(MimeMessage.RecipientType.TO, sendTo);
            // 邮件主题
            message.setSubject(theme);
            // 添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            // 遍历添加附件
            if (annex != null && annex.size() > 0) {
                for (File file : annex) {
                    BodyPart attachmentBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    attachmentBodyPart.setDataHandler(new DataHandler(source));
                    attachmentBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(attachmentBodyPart);
                }
            }
            // 将多媒体对象放到message中
            message.setContent(multipart);
            // 保存邮件
            message.saveChanges();
            // SMTP验证，就是你用来发邮件的邮箱用户名密码
            transport = session.getTransport("smtp");
            transport.connect(host, user, password);
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println(theme + " Email send success!");
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
