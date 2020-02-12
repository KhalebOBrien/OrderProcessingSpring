package mdev.OrderProcessingSpring.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class PrmtProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("op:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)
        );
    }

}