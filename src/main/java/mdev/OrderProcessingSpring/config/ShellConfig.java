package mdev.OrderProcessingSpring.config;

import mdev.OrderProcessingSpring.functions.CommandFunctions;
import mdev.OrderProcessingSpring.shell.ShellUsrEX;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author markodevelopment (Mihálovics Márkó)
 */
@Configuration
public class ShellConfig {

    /**
     * @return A new instance of the ShellUsrEx class
     */
    @Bean
    public ShellUsrEX shellUX() {
        return new ShellUsrEX();
    }

    /**
     * @return A new instance of the CommandFunctions class
     */
    @Bean
    public CommandFunctions commandFunctions() {
        return new CommandFunctions();
    }

}
