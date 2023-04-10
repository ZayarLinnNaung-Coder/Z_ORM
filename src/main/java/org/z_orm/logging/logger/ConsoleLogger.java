package org.z_orm.logging.logger;

import org.z_orm.logging.ConsoleColor;

public class ConsoleLogger implements Logger{

    @Override
    public void info(String msg) {
        System.out.println(ConsoleColor.GREEN_BRIGHT + msg + ConsoleColor.RESET);
    }

    @Override
    public void warn(String msg) {
        System.out.println(ConsoleColor.YELLOW + msg + ConsoleColor.RESET);
    }

    @Override
    public void err(String msg) {
        System.out.println(ConsoleColor.RED + msg + ConsoleColor.RESET);
    }

}
