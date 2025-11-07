package dev.glabay.email.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailTemplateRenderer {

    private final TemplateEngine templateEngine;

    public EmailTemplateRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String render(String templateName, Map<String, Object> model) {
        Context context = new Context();
        if (model != null) {
            model.forEach(context::setVariable);
        }
        // templates are under templates/email/<templateName>.html
        String view = "email/" + templateName;
        return templateEngine.process(view, context);
    }
}
