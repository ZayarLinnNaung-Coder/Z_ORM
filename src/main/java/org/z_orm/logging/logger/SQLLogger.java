package org.z_orm.logging.logger;

import org.z_orm.configuration.ConfigurationContext;
import org.z_orm.logging.ConsoleColor;

public class SQLLogger implements Logger{

    private final Class c;
    private ConfigurationContext configurationContext;

    public SQLLogger(Class c){
        this.c = c;
        this.configurationContext = ConfigurationContext.getInstance();
    }

    @Override
    public void info(String msg) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(ConsoleColor.CYAN);
        stringBuilder.append(c.getName());
        stringBuilder.append(" ");
        stringBuilder.append(ConsoleColor.RESET);

        stringBuilder.append(ConsoleColor.GREEN_BRIGHT);
        stringBuilder.append(msg);
        stringBuilder.append(ConsoleColor.RESET);

        System.out.println(stringBuilder);
    }

    @Override
    public void warn(String msg) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ConsoleColor.YELLOW_BRIGHT);
        stringBuilder.append(c.getName());
        stringBuilder.append(msg);
        stringBuilder.append(ConsoleColor.RESET);

        System.out.println(stringBuilder);
    }

    @Override
    public void err(String msg) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ConsoleColor.RED);
        stringBuilder.append(c.getName());
        stringBuilder.append(msg);
        stringBuilder.append(ConsoleColor.RESET);

        System.out.println(stringBuilder);
    }
}
