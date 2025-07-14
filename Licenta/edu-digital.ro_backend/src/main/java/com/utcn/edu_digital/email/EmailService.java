package com.utcn.edu_digital.email;

import com.utcn.edu_digital.MediaFiles.MediaFiles;
import com.utcn.edu_digital.posts.PostEmailLog;
import com.utcn.edu_digital.posts.PostEmailLogRepository;
import com.utcn.edu_digital.posts.Posts;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PostEmailLogRepository postEmailLogRepository;

    public void sendMultiplePostsToEmails(List<Posts> posts, List<String> emails) {
        for (String to : emails) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject("📩 Postări partajate de pe Edu-Digital");

                StringBuilder html = new StringBuilder();
                html.append("<h2>Ai primit următoarele postări:</h2>");

                int imageCounter = 0;

                for (Posts post : posts) {
                    html.append("<hr>");
                    html.append("<h3>").append(post.getTitle()).append("</h3>");
                    html.append("<p><b>Autor:</b> ").append(post.getUser().getName()).append("</p>");
                    html.append("<p><b>Descriere:</b><br>").append(post.getDescription()).append("</p>");

                    if (post.getVideoUrl() != null && !post.getVideoUrl().isBlank()) {
                        html.append("<p><b>Video:</b> <a href=\"")
                                .append(post.getVideoUrl())
                                .append("\">Vizualizează aici</a></p>");
                    }

                    if (post.getMediaFiles() != null && !post.getMediaFiles().isEmpty()) {
                        html.append("<p><b>Imagini:</b><br>");
                        for (MediaFiles mf : post.getMediaFiles()) {
                            if (mf.getType().startsWith("image/")) {
                                String cid = "image" + (imageCounter++);
                                helper.addInline(cid, new ByteArrayResource(mf.getData()), mf.getType());
                                html.append("<img src='cid:")
                                        .append(cid)
                                        .append("' style='max-width:300px; margin:5px; border-radius:8px;' /><br>");
                            }
                        }
                        html.append("</p>");
                    }
                }

                helper.setText(html.toString(), true);
                mailSender.send(message);

                // ✅ Salvăm logurile în baza de date
                for (Posts post : posts) {
                    postEmailLogRepository.save(new PostEmailLog(post.getId(), to));
                }

            } catch (Exception e) {
                System.err.println("❌ Eroare la trimiterea emailului către " + to + ": " + e.getMessage());
            }
        }
    }
}
